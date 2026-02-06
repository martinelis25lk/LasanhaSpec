package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.dto.VehiclePageDTO;
import br.com.lasanhaspec.carservice.service.VehicleCatalogService;
import br.com.lasanhaspec.carservice.service.VehiclePageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleCatalogController {


    private final VehicleCatalogService vehicleCatalogService;
    private  final VehiclePageService vehiclePageService;





    public VehicleCatalogController(
            VehicleCatalogService vehicleCatalogService,
            VehiclePageService vehiclePageService){


        this.vehicleCatalogService = vehicleCatalogService;
        this.vehiclePageService = vehiclePageService;
    }





    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleCatalogModel create(@RequestBody VehicleCatalogModel vehicleCatalogModel){
        System.out.println("CARRRO RECEBIDO: " + vehicleCatalogModel);
        return vehicleCatalogService.save(vehicleCatalogModel);
    }

    @GetMapping
    public List<VehicleCatalogModel> List(){
        return vehicleCatalogService.findAll();
    }


    @GetMapping("/{id}")
    public VehicleCatalogModel getById(@PathVariable Long id){
        return vehicleCatalogService.findById(id);
    }




}




