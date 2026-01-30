package br.com.lasanhaspec.carservice.domain.setup;


import br.com.lasanhaspec.carservice.domain.catalog.VehicleCatalog;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "setups")
public class SetupModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //base do carro original de fábrica
    @ManyToOne(optional = false)
    private VehicleCatalog vehicleCatalog;// instanciando o dominio do veículo original

    //estado atual da carroça(pocotos são perdidos ao longo dos anos)
    private Integer currentHorsepower;

    private Boolean Turbo;
    private String turboModel;

    private boolean modifiedCamshaft;


    private LocalDate createdAt;


    @PrePersist
    void prePersist(){
        this.createdAt = LocalDate.now();
    }

    ///  ainda n terminei aqui, ta muito cru
}
