package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.domain.entity.VehicleModel;
import br.com.lasanhaspec.carservice.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {


    private final CarService carService;


    public CarController(CarService carService){
        this.carService = carService;
    }





    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleModel create(@RequestBody VehicleModel vehicleModel){
        System.out.println("CAR RECEBIDO: " + vehicleModel);
        return carService.save(vehicleModel);
    }

    @GetMapping
    public List<VehicleModel> List(){
        return carService.findAll();
    }


    @GetMapping("/{id}")
    public VehicleModel getById(@PathVariable Long id){
        return carService.findById(id);
    }

}
