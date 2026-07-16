package br.com.lasanhaspec.market_service.controller;

import br.com.lasanhaspec.market_service.dto.FipePriceHistoryPointDTO;
import br.com.lasanhaspec.market_service.dto.FipePriceResponseDTO;
import br.com.lasanhaspec.market_service.service.FipeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/market/fipe")
public class FipeController {



    private final FipeService fipeService;

    public FipeController(FipeService fipeService){
        this.fipeService = fipeService;
    }



    @GetMapping("/price")
    public FipePriceResponseDTO getPrice(
            @RequestParam String brandCode,
            @RequestParam String modelCode,
            @RequestParam String yearCode
    )
    {

        return fipeService.getPrice(brandCode, modelCode, yearCode);
    }

    @GetMapping("/history")
    public List<FipericeHistoryPointDTO> getHistory(
            @RequestParam String brandcocde,
            @RequestParam String modelCode,
            @RequestParam String yearCode
    )
    {

        return fipeService.getHistory(brandcocde, modelCode, yearCode);

    }


}
