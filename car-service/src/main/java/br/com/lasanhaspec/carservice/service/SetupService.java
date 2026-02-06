package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.models.SetupModel;
import br.com.lasanhaspec.carservice.repository.SetupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetupService {


    private final SetupRepository setupRepository;

    public SetupService(SetupRepository setupRepository){
        this.setupRepository = setupRepository;
    }




    public List<SetupModel> findByVehicle(Long vehicleId){
        return setupRepository.findByVehicleCatalogModelId(vehicleId);

        //return null;
    }


}
