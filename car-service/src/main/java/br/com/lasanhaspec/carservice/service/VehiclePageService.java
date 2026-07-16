package br.com.lasanhaspec.carservice.service;

import br.com.lasanhaspec.carservice.domain.models.CommunitySetup;
import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.dto.FipeResponseDTO;
import br.com.lasanhaspec.carservice.dto.VehiclePageDTO;
import br.com.lasanhaspec.carservice.infrastructure.client.MarketServiceClient;
import br.com.lasanhaspec.carservice.mappers.VehiclePageMapper;
import org.springframework.stereotype.Service;
import br.com.lasanhaspec.carservice.dto.FipeHistoryPointDTO;
import java.util.Collections;

import java.util.List;

@Service
public class VehiclePageService {


    private VehicleCatalogService  vehicleCatalogService;
    private SetupService setupService;
    private MarketServiceClient marketServiceClient;



    public  VehiclePageService(VehicleCatalogService vehicleCatalogService,
                               SetupService setupService,
                               MarketServiceClient marketServiceClient){

        this.vehicleCatalogService = vehicleCatalogService;
        this.setupService = setupService;
        this.marketServiceClient = marketServiceClient;
    }


    public VehiclePageDTO buildVehiclePage(Long vehicleId){

        VehicleCatalogModel vehicle = vehicleCatalogService.findById(vehicleId);

        List<CommunitySetup> setups = setupService.findByVehicle(vehicle.getId());

        FipeResponseDTO fipePrice = null;

        if (vehicle.getFipeBrandCode() != null &&
                vehicle.getFipeModelCode() != null &&
                vehicle.getFipeYearCode() != null) {

            fipePrice = marketServiceClient.getFipePrice(
                    vehicle.getFipeBrandCode(),
                    vehicle.getFipeModelCode(),
                    vehicle.getFipeYearCode()
            );
        }



        return  VehiclePageMapper.toDTO(vehicle, setups, fipePrice);

    }

    public List<FipeHistoryPointDTO> getFipeHistory(Long vehicleId) {

        VehicleCatalogModel vehicle = vehicleCatalogService.findById(vehicleId);

        if (vehicle.getFipeBrandCode() == null ||
                vehicle.getFipeModelCode() == null ||
                vehicle.getFipeYearCode() == null) {
            return Collections.emptyList();
        }

        return marketServiceClient.getFipeHistory(
                vehicle.getFipeBrandCode(),
                vehicle.getFipeModelCode(),
                vehicle.getFipeYearCode()
        );
    }

}
