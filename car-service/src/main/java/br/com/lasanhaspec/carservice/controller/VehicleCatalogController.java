package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.domain.entity.VehicleModel;
import br.com.lasanhaspec.carservice.service.VehicleModelService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle-models")
public class VehicleModelController {


    private final VehicleModelService vehicleModelService;


    public VehicleModelController(VehicleModelService vehicleModelService){
        this.vehicleModelService = vehicleModelService;
    }





    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleModel create(@RequestBody VehicleModel vehicleModel){
        System.out.println("CAR RECEBIDO: " + vehicleModel);
        return vehicleModelService.save(vehicleModel);
    }

    @GetMapping
    public List<VehicleModel> List(){
        return vehicleModelService.findAll();
    }


    @GetMapping("/{id}")
    public VehicleModel getById(@PathVariable Long id){
        return vehicleModelService.findById(id);
    }

}
