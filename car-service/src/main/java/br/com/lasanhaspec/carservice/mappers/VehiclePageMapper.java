package br.com.lasanhaspec.carservice.mappers;

import br.com.lasanhaspec.carservice.domain.models.SetupModel;
import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.dto.CommunitySetupDTO;
import br.com.lasanhaspec.carservice.dto.TechnicalSpecsDTO;
import br.com.lasanhaspec.carservice.dto.VehiclePageDTO;
import br.com.lasanhaspec.carservice.dto.VehicleSummaryDTO;

import java.util.List;

public class VehiclePageMapper {


    public static VehiclePageDTO toDTO(VehicleCatalogModel vehicle, List<SetupModel> setups){

        VehiclePageDTO dto = new VehiclePageDTO();


        dto.setVehicle(VehiclePageMapper.buildSummary(vehicle));
        dto.setTechnicalSpecs(buildSpecs(vehicle));
        dto.setCommunitySetups(buildCommunitySetups(setups));

        return dto;
    }




    private static VehicleSummaryDTO buildSummary(VehicleCatalogModel vehicle){
        VehicleSummaryDTO  summary = new VehicleSummaryDTO();

        summary.setBrand(vehicle.getBrand());
        summary.setModel(vehicle.getModel());
        summary.setYear(vehicle.getYear());

        return summary;

    }



    private static TechnicalSpecsDTO buildSpecs(VehicleCatalogModel vehicle){

        TechnicalSpecsDTO specs = new TechnicalSpecsDTO();


        //cavalos
        specs.setFactoryHorsepower(vehicle.getFactoryHorsepower());
        //torque
        specs.setFactoryTorque(vehicle.getFactoryTorque());
        //aspiração

        specs.setAspirationType(vehicle.getAspirationType());


        return specs;
    }


    private static List<CommunitySetupDTO> buildCommunitySetups(List<SetupModel> setups){
        return setups.stream()
                .map(setup ->{

                    CommunitySetupDTO dto = new CommunitySetupDTO();

                    dto.setTargetHorsePower(setup.getTargetHorsePower());
                    dto.setUsage(setup.getUsage());
                    dto.setEngineStage(String.valueOf(setup.getEngineStage()));

                    return dto;


                })

                .toList();



    }





}
