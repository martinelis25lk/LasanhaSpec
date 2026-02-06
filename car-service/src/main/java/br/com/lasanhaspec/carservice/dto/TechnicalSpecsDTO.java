package br.com.lasanhaspec.carservice.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TechnicalSpecsDTO {


    //aba ficha tecnica

    private Integer factoryHorsepower;
    private Integer factoryTorque;
    private String aspirationType;
}
