package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.domain.entity.Car;
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
    public Car create(@RequestBody Car car){
        System.out.println("CAR RECEBIDO: " + car);
        return carService.save(car);
    }

    @GetMapping
    public List<Car> List(){
        return carService.findAll();
    }


    @GetMapping("/{id}")
    public Car getById(@PathVariable Long id){
        return carService.findById(id);
    }

}
