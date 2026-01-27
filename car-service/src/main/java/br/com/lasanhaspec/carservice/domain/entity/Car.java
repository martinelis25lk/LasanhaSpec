package br.com.lasanhaspec.carservice.domain.entity;


import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.time.LocalDate;



@Data
@Getter
@Setter
@Entity
@ToString
@Table(name = "cars")
public class Car {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    private Long id;

    private String brand;

    private String model;
    private Integer year;


    private String engine;
    private Integer horsepower;
    private Integer torque;


    private Double fuelConsumptionCity;
    private Double fuelConsumptionHighway;

    private LocalDate createdAt;


    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDate.now();
    }



    // lombok will generate getters and setters
}
