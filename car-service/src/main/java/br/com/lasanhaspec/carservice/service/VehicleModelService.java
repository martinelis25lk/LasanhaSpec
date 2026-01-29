package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.entity.VehicleModel;
import br.com.lasanhaspec.carservice.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {


    private final CarRepository carRepository;

    public CarService(CarRepository carRepository){
        this.carRepository = carRepository;
    }


    public VehicleModel save(VehicleModel vehicleModel){
        return carRepository.save(vehicleModel);
    }


    public List<VehicleModel> findAll(){
        return carRepository.findAll();
    }


    public VehicleModel findById(Long id){
        return carRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("vehicleModel not found"));
    }


}
