package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.catalog.VehicleCatalog;
import br.com.lasanhaspec.carservice.repository.VehicleCatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleCatalogService {


    private final VehicleCatalogRepository vehicleCatalogRepository;

    public VehicleCatalogService(VehicleCatalogRepository vehicleCatalogRepository){
        this.vehicleCatalogRepository = vehicleCatalogRepository;
    }


    public VehicleCatalog save(VehicleCatalog vehicleCatalog){
        return vehicleCatalogRepository.save(vehicleCatalog);
    }


    public List<VehicleCatalog> findAll(){
        return vehicleCatalogRepository.findAll();
    }


    public VehicleCatalog findById(Long id){
        return vehicleCatalogRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("vehicleModel not found"));
    }


}
