package br.com.lasanhaspec.carservice.dto;


//cabecalho da pagina


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleSummaryDTO {

    private Long id;
    private String brand;
    private String model;
    private Integer year;



    private String engineCode;
    private String aspirationType;
}
