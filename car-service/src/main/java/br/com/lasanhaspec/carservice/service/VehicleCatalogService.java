package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.entity.VehicleModel;
import br.com.lasanhaspec.carservice.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleModelService {


    private final VehicleRepository vehicleRepository;

    public VehicleModelService(VehicleRepository vehicleRepository){
        this.vehicleRepository = vehicleRepository;
    }


    public VehicleModel save(VehicleModel vehicleModel){
        return vehicleRepository.save(vehicleModel);
    }


    public List<VehicleModel> findAll(){
        return vehicleRepository.findAll();
    }


    public VehicleModel findById(Long id){
        return vehicleRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("vehicleModel not found"));
    }


}
