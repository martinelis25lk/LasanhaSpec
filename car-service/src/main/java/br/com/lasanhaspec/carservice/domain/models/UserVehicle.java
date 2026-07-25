package br.com.lasanhaspec.carservice.domain.models;

//essa entidade n vai guardar potencia, ou modificação alguma, eh so pra representar o carro base do usuário.
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_vehicles")
public class UserVehicle {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId; // vai virar relacionamento

    private Integer currentHorsePower;//potencia atual
    private Integer currentTorque;
    private Integer currentWeight;

    @ManyToOne(optional = false)
    private VehicleCatalogModel vehicleCatalogModel;

    private String nickname; // exemplo: minha e36 turbo

    private Boolean active = true;

    @OneToMany(mappedBy = "userVehicle",
            cascade = CascadeType.ALL)
    private List<CurrentSetup> setups;

    public List<VehicleImage> getImages() {
        return images;
    }

    @OneToMany(mappedBy = "userVehicle",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<VehicleImage> images = new ArrayList<>();

    public void setImages(List<VehicleImage> images) {
        this.images = images;
    }



    public Integer getCurrentHorsePower(){
        return currentHorsePower;
    }

    public void setCurrentHorsePower(Integer currentHorsePowers){
        this.currentHorsePower = currentHorsePowers;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public VehicleCatalogModel getVehicleCatalogModel() {
        return vehicleCatalogModel;
    }

    public void setVehicleCatalogModel(VehicleCatalogModel vehicleCatalogModel) {
        this.vehicleCatalogModel = vehicleCatalogModel;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<CurrentSetup> getSetups() {
        return setups;
    }

    public void setSetups(List<CurrentSetup> setups) {
        this.setups = setups;
    }


    public Integer getCurrentTorque() {
        return currentTorque;
    }

    public Integer getCurrentWeight() {
        return currentWeight;
    }


    

    public void setCurrentTorque(Integer currentTorque) {
        this.currentTorque = currentTorque;
    }




    public void setCurrentWeight(Integer currentWeight) {
        this.currentWeight = currentWeight;
    }

    public void setUser(User owner) {
    }


    //

    
}