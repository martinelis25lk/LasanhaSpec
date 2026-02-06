package br.com.lasanhaspec.carservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;




@Getter
@Setter
public class VehiclePageDTO {



    private VehicleSummaryDTO vehicle;

    private TechnicalSpecsDTO technicalSpecs;

    private List<CommunitySetupDTO> communitySetups;
}

