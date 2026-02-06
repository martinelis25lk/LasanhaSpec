package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.dto.VehiclePageDTO;
import br.com.lasanhaspec.carservice.service.VehiclePageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller("/vehicles")
public class VehiclePageController {



    //precisa da instancia do dto
    //precisa da instancia do service
    //precisa de mapper? acho q n

    private final VehiclePageService vehiclePageService;



    public VehiclePageController(VehiclePageService vehiclePageService){
        this.vehiclePageService = vehiclePageService;
    }



    @GetMapping("/{id}/page")
    public VehiclePageDTO getVehiclePage(@PathVariable Long id){
        return vehiclePageService.buildVehiclePage(id);

    }

}
