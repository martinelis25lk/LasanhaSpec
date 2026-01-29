package br.com.lasanhaspec.carservice.domain.entity;


import jakarta.persistence.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Data
@Getter
@Setter
@Entity
@ToString
@Table(name = "vehicle_models")
public class VehicleModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private Integer year;


    private String engineCode;

    private Integer factoryHorsepower;
    private Integer factroyTorque;

    @Enumerated(EnumType.STRING)
    private AspirationType aspirationType;




    // lombok will generate getters and setters
}
