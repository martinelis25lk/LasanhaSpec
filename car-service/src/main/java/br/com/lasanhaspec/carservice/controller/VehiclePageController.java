package br.com.lasanhaspec.carservice.controller;


import br.com.lasanhaspec.carservice.dto.VehiclePageDTO;
import br.com.lasanhaspec.carservice.service.VehiclePageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.lasanhaspec.carservice.dto.FipeHistoryPointDTO;
import java.util.List;

@RestController
@RequestMapping("/catalog/vehicles")
public class VehiclePageController {


    private final VehiclePageService vehiclePageService;

    public VehiclePageController(VehiclePageService vehiclePageService){
        this.vehiclePageService = vehiclePageService;
    }



    @GetMapping("/{id}/page")
    public VehiclePageDTO getVehiclePage(@PathVariable Long id){
        return vehiclePageService.buildVehiclePage(id);

    }

    @GetMapping("/{id}/fipe-history")
    public List<FipeHistoryPointDTO> getFipeHistory(@PathVariable Long id){
        return vehiclePageService.getFipeHistory(id);
    }



}
