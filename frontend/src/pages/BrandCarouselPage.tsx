import { BrandCard } from "../components/BrandCard";
import { useNavigate } from "react-router-dom";
import { brandInfo } from "../data/brandInfo";
import { brandCountry } from "../data/brandCountry";
import { useState } from "react";
import "./BrandCarouselPage.css";

import { Swiper, SwiperSlide } from "swiper/react";
import { EffectCoverflow, Keyboard } from "swiper/modules";

import "swiper/css";
import "swiper/css/effect-coverflow";



export function BrandCarouselPage() {



  const brands = [
    "BMW",
    "Subaru",
    "Chevrolet",
    "Byd",
    "Mercedes",
    "Renault",
    "Ford",
    "Nissan",
    "Volkswagen",
    "Audi",
    "Honda",
    "Toyota",
    "Peugeot",
    "Fiat",
    "Hyundai",
    "Volvo",
    "Alfaromeo",
    "Citroen",
    "Jeep",
    "Mitsubishi"
  ];

  const [selectedBrand, setSelectedBrand] = useState(brands[0]);
  const navigate = useNavigate();

  



  return (
    <div className="brand-carousel-page">


        <div className="carousel-container">
          <div className="brand-header">
            <h1>Escolha sua fabricante</h1>
             <p>Explore modelos, motores e especificações das principais marcas. </p>
        
           </div>
        </div>



    <div className="carousel-frame">

      <Swiper
        slideToClickedSlide={true}

        effect="coverflow"
        modules={[EffectCoverflow, Keyboard]}
        keyboard={{ enabled: true }}
        centeredSlides={true}
        slidesPerView={"auto"}
        spaceBetween={30}
        loop={true}
        grabCursor={true}

        onSlideChange={(swiper) => {
        const index = swiper.realIndex;
        setSelectedBrand(brands[index]);
        }}

        coverflowEffect={{
          rotate: 40,
          depth: 150,
          modifier: 1,
          slideShadows: false,
        }}
    >

        {brands.map((brand) => (
          <SwiperSlide key={brand}>
            <BrandCard brand={brand} isActive={brand === selectedBrand}/>
          </SwiperSlide>
        ))}
      </Swiper>


      <div key={selectedBrand} className="brand-info">

        
        <div className="brand-title"> 
           <img src={`https://flagcdn.com/w40/${brandCountry[selectedBrand as keyof typeof brandCountry]}.png`} 
           alt={`Bandeira ${brandCountry[selectedBrand as keyof typeof brandCountry]}`}  />
           <h2>{selectedBrand}</h2>
        </div>

        <p className="brand-description">
            {brandInfo[selectedBrand as keyof typeof brandInfo] || "Explore essa marca"}
        </p>

        <span className="brand-index">
           {brands.indexOf(selectedBrand) + 1} / {brands.length}
        </span>

        <div className="brand-actions">
          <button className="cta-primary" onClick={() => navigate(`/catalog/${selectedBrand}`)}>
            Ver catálogo {selectedBrand} 
          </button>
          <button className="cta-ghost" onClick={()=> navigate("/catalog")}>
            Ver catálogo completo
          </button>
        </div>

    </div>




  <div className="brand-story-section">
      <div className="story-tile tile-a">
        <img src="/parts/motor.jpeg" />
        <p>Não existem carros ruins, existem carros que foram negligenciados pelos donos.</p>
      </div>
    <div className="story-tile tile-b">
       <img src="/parts/fendas.jpeg" />
       <p>O conhecimento compartilhado é o que mantém as máquinas vivas.</p>
    </div>
    <div className="story-tile tile-c">
       <img src="/parts/cabecote.jpeg" />
       <p>Todo projeto começa com um problema, curiosidade e força de vontade.</p>
    </div>
    <div className="story-tile tile-d">
      <img src="/parts/molas.jpeg" />
       <p>Resiliência define o entusiasta, não a garagem cheia.</p>
    </div>
    <div className="story-tile tile-e">
       <img src="/parts/chaves.jpeg" />
       <p>Criada pra unir e compartilhar conhecimento.</p>
    </div>
    <div className="story-tile tile-f">
      <img src="/parts/porcas.jpeg" />
      <p>Achar a agulha no palheiro: a peça certa pra máquina certa.</p>
    </div>
  </div>

    </div>

    </div>

  );
}


