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

    private  VehiclePageService vehiclePageService;


    public void VehiclePageController(VehiclePageService vehiclePageService) {
        this.vehiclePageService = vehiclePageService;
    }


    public VehicleCatalogController(VehicleCatalogService vehicleCatalogService){
        this.vehicleCatalogService = vehicleCatalogService;
    }





    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleCatalogModel create(@RequestBody VehicleCatalogModel vehicleCatalogModel){
        System.out.println("CAR RECEBIDO: " + vehicleCatalogModel);
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







    @GetMapping("/{id}/page")
    public VehiclePageDTO getVehiclePage(@PathVariable Long id) {
        return vehiclePageService.buildVehiclePage(id);
    }

}
