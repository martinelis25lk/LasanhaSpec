package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.repository.VehicleCatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleCatalogService {


    private final VehicleCatalogRepository vehicleCatalogRepository;

    public VehicleCatalogService(VehicleCatalogRepository vehicleCatalogRepository){
        this.vehicleCatalogRepository = vehicleCatalogRepository;
    }


    public VehicleCatalogModel save(VehicleCatalogModel vehicleCatalogModel){
        return vehicleCatalogRepository.save(vehicleCatalogModel);
    }


    public List<VehicleCatalogModel> findAll(){
        return vehicleCatalogRepository.findAll();
    }


    public VehicleCatalogModel findById(Long id){
        return vehicleCatalogRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("vehicleModel not found"));
    }


}
