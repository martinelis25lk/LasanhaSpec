import { brandLogos } from "../data/brandLogos";
import { useNavigate } from "react-router-dom";
import "./BrandCard.css";

interface BrandCardProps {
  brand: string;
  isActive: boolean;
}


export function BrandCard({ brand, isActive }: BrandCardProps) {
  const navigate = useNavigate();
  const logo = brandLogos[brand as keyof typeof brandLogos] ||
    "/brands/default.svg";

     function handleClick() {
      if(isActive) navigate('/catalog/${brand}');
    //alert(`Você clicou na marca ${brand}`);
    // se não for o ativo, o Swiper centraliza sozinho — não precisa navegar aqui
  }

  console.log("Brand:", brand, "Logo:", logo);

  return (
         <div className={`brand-card ${isActive ? "is-active" : ""}`} onClick={handleClick}>
            <img src={logo} alt={`Logo ${brand}`} />
         </div>
  );
}