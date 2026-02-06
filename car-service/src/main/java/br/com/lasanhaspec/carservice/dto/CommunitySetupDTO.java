package br.com.lasanhaspec.carservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunitySetupDTO {

    private Long id;



    private String usage;
    private String engineStage;


    private Integer targetHorsePower;
    private Integer targetTorque;


    private String reliability;
    private String targetAspirationType;
}
