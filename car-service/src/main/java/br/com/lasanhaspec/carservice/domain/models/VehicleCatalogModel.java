package br.com.lasanhaspec.carservice.domain.models;


import br.com.lasanhaspec.carservice.domain.enums.*;
import jakarta.persistence.*;

import lombok.Data;
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

@Data
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
    private Integer factoryWeight;



    @Enumerated(EnumType.STRING)
    private AspirationType aspirationType;


    private Double displacement;
    private Integer cylinderCount;
    private Double topSpeed;
    private Double acceleration0to100;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    private DriveType driveType;

    @Enumerated(EnumType.STRING)
    private TransmissionType transmissionType;

    @Enumerated(EnumType.STRING)
    private TransmissionModel transmissionModel;
    private Integer gearCount;


    //campos pra correspondencia fipe
    private String fipeBrandCode;
    private String fipeModelCode;
    private String fipeYearCode;



    public String getFipeBrandCode() {
        return fipeBrandCode;
    }

    public void setFipeBrandCode(String fipeBrandCode) {
        this.fipeBrandCode = fipeBrandCode;
    }

    public String getFipeModelCode() {
        return fipeModelCode;
    }

    public void setFipeModelCode(String fipeModelCode) {
        this.fipeModelCode = fipeModelCode;
    }

    public String getFipeYearCode() {
        return fipeYearCode;
    }

    public void setFipeYearCode(String fipeYearCode) {
        this.fipeYearCode = fipeYearCode;
    }



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

    public AspirationType getAspirationType() {
        return aspirationType;
    }

    public Integer getFactoryWeight() {
        return factoryWeight;
    }


    public Double getDisplacement() {
        return displacement;
    }

    public void setDisplacement(Double displacement) {
        this.displacement = displacement;
    }

    public Integer getCylinderCount() {
        return cylinderCount;
    }

    public void setCylinderCount(Integer cylinderCount) {
        this.cylinderCount = cylinderCount;
    }

    public Double getTopSpeed() {
        return topSpeed;
    }

    public void setTopSpeed(Double topSpeed) {
        this.topSpeed = topSpeed;
    }

    public Double getAcceleration0to100() {
        return acceleration0to100;
    }

    public void setAcceleration0to100(Double acceleration0to100) {
        this.acceleration0to100 = acceleration0to100;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public DriveType getDriveType() {
        return driveType;
    }

    public void setDriveType(DriveType driveType) {
        this.driveType = driveType;
    }

    public TransmissionType getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }

    public TransmissionModel getTransmissionModel() {
        return transmissionModel;
    }

    public void setTransmissionModel(TransmissionModel transmissionModel) {
        this.transmissionModel = transmissionModel;
    }

    public Integer getGearCount() {
        return gearCount;
    }

    public void setGearCount(Integer geraCount) {
        this.gearCount = geraCount;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setEngineCode(String engineCode) {
        this.engineCode = engineCode;
    }

    public void setFactoryHorsepower(Integer factoryHorsepower) {
        this.factoryHorsepower = factoryHorsepower;
    }

    public void setFactoryTorque(Integer factoryTorque) {
        this.factoryTorque = factoryTorque;
    }

    public void setAspirationType(AspirationType aspirationType) {
        this.aspirationType = aspirationType;
    }

    public void setFactoryWeight(Integer factoryWeight) {
        this.factoryWeight = factoryWeight;
    }


}
