package br.com.lasanhaspec.carservice.service;

import br.com.lasanhaspec.carservice.repository.VehicleCatalogRepository;
import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.domain.models.SetupModel;
import br.com.lasanhaspec.carservice.dto.VehiclePageDTO;
import br.com.lasanhaspec.carservice.mappers.VehiclePageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiclePageService {


    private VehicleCatalogService  vehicleCatalogService;
    private SetupService setupService;



    public void vehiclePageService(VehicleCatalogService vehicleCatalogService, SetupService setupService){

        this.vehicleCatalogService = vehicleCatalogService;
        this.setupService = setupService;
    }


    public VehiclePageDTO buildVehiclePage(Long vehicleId){

        VehicleCatalogModel vehicle = vehicleCatalogService.findById(vehicleId);

        List<SetupModel> setups = (List<SetupModel>) setupService.findByVehicle(vehicle.getId());
        // aqui só mapeamento, sem regra
        return  VehiclePageMapper.toDTO(vehicle, setups);

    }

}
