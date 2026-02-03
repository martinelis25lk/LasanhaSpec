package br.com.lasanhaspec.carservice.mappers;

import br.com.lasanhaspec.carservice.domain.models.SetupModel;
import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.dto.VehiclePageDTO;

import java.util.List;

public class VehiclePageMapper {


    public static VehiclePageDTO toDTO(VehicleCatalogModel vehicle, List<SetupModel> setups){

        VehiclePageDTO dto = new VehiclePageDTO();


        //dto.setVehicle(buildSummary(vehicle));
        //dto.setTechnicalSpecs(buildSpecs(vehicle));
        //dto.setCommunitySetups(buildCommunitySetups(setups));

        return dto;
    }
}
