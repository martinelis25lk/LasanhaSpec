package br.com.lasanhaspec.carservice.domain.models;


import jakarta.persistence.*;
import br.com.lasanhaspec.carservice.domain.enums.AspirationType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//****************************************
//
//
//VehicleCatalogModel = base de fábrica
//
 //
 //
 //
 //******************************************
@Setter
@Data
@Getter
@Entity
@ToString
@Table(name = "vehicle_models")
public class VehicleCatalogModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private Integer year;


    private String engineCode;

    private Integer factoryHorsepower;
    private Integer factoryTorque;

    @Enumerated(EnumType.STRING)
    private AspirationType aspirationType;



    public Long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Integer getYear() {
        return year;
    }

    public String getEngineCode() {
        return engineCode;
    }

    public Integer getFactoryHorsepower() {
        return factoryHorsepower;
    }

    public Integer getFactoryTorque() {
        return factoryTorque;
    }

    public String getAspirationType() {
        return aspirationType;
    }


    // lombok will generate getters and setters
}
