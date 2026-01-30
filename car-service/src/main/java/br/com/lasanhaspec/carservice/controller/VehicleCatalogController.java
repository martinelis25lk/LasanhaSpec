package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.domain.catalog.VehicleCatalog;
import br.com.lasanhaspec.carservice.service.VehicleCatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle-models")
public class VehicleCatalogController {


    private final VehicleCatalogService vehicleCatalogService;


    public VehicleCatalogController(VehicleCatalogService vehicleCatalogService){
        this.vehicleCatalogService = vehicleCatalogService;
    }





    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleCatalog create(@RequestBody VehicleCatalog vehicleCatalog){
        System.out.println("CAR RECEBIDO: " + vehicleCatalog);
        return vehicleCatalogService.save(vehicleCatalog);
    }

    @GetMapping
    public List<VehicleCatalog> List(){
        return vehicleCatalogService.findAll();
    }


    @GetMapping("/{id}")
    public VehicleCatalog getById(@PathVariable Long id){
        return vehicleCatalogService.findById(id);
    }

}
