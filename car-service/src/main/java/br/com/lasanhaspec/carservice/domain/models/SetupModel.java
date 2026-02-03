package br.com.lasanhaspec.carservice.domain.models;
//SetupModel = Setup Base / Setup da Comunidade

import br.com.lasanhaspec.carservice.domain.enums.AspirationType;
import br.com.lasanhaspec.carservice.domain.enums.EngineStage;
import br.com.lasanhaspec.carservice.domain.enums.ReliabilityLevel;
import br.com.lasanhaspec.carservice.domain.enums.UsageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


/**
 * Setup genérico / perfil de preparação
 * Não representa um carro real de usuário
 * Serve como base para comparação e inspiração
 */



@Getter
@Setter
@Entity
@Table(name = "setups")
public class SetupModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //base do carro original de fábrica
    @ManyToOne(optional = false)
    private VehicleCatalogModel vehicleCatalogModel;// instanciando o dominio do veículo original


    // aspiração final do projeto
    @Enumerated(EnumType.STRING)
    private AspirationType targetAspirationType;
    private Boolean communitySetup = true;

    private String createdBy; // ex: "Comunidade", "Curadoria", "Admin"

    private EngineStage engineStage; // STOCK, STAGE_1, STAGE_2

    private Integer targetHorsePower;

    private Integer targetTorque;

    private ReliabilityLevel reliability;

    private UsageType usage; // DAILY, TRACK, DRAG

    Boolean forgedInternals;

    private LocalDate createdAt;


    public void setVehicleCatalogModel(VehicleCatalogModel vehicleCatalogModel) {
        this.vehicleCatalogModel = vehicleCatalogModel;
    }

    @PrePersist
    void prePersist(){
        this.createdAt = LocalDate.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleCatalogModel getVehicleCatalogModel() {
        return vehicleCatalogModel;
    }


    public AspirationType getTargetAspirationType() {
        return targetAspirationType;
    }

    public void setTargetAspirationType(AspirationType targetAspirationType) {
        this.targetAspirationType = targetAspirationType;
    }

    public Boolean getCommunitySetup() {
        return communitySetup;
    }

    public void setCommunitySetup(Boolean communitySetup) {
        this.communitySetup = communitySetup;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public EngineStage getEngineStage() {
        return engineStage;
    }

    public void setEngineStage(EngineStage engineStage) {
        this.engineStage = engineStage;
    }

    public Integer getTargetHorsePower() {
        return targetHorsePower;
    }

    public void setTargetHorsePower(Integer targetHorsePower) {
        this.targetHorsePower = targetHorsePower;
    }

    public Integer getTargetTorque() {
        return targetTorque;
    }

    public void setTargetTorque(Integer targetTorque) {
        this.targetTorque = targetTorque;
    }

    public ReliabilityLevel getReliability() {
        return reliability;
    }

    public void setReliability(ReliabilityLevel reliability) {
        this.reliability = reliability;
    }

    public UsageType getUsage() {
        return usage;
    }

    public void setUsage(UsageType usage) {
        this.usage = usage;
    }

    public Boolean getForgedInternals() {
        return forgedInternals;
    }

    public void setForgedInternals(Boolean forgedInternals) {
        this.forgedInternals = forgedInternals;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }


    ///  ainda n terminei aqui, ta muito cru
}
