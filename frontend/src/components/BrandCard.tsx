import { brandLogos } from "../data/brandLogos";
import "./BrandCard.css";

interface BrandCardProps {
  brand: string;
}


export function BrandCard({ brand }: BrandCardProps) {
  const logo = 
     brandLogos[brand as keyof typeof brandLogos] ||
    "/brands/default.svg";

     function handleClick() {
    alert(`Você clicou na marca ${brand}`);
  }

  console.log("Brand:", brand, "Logo:", logo);

  return (
        <div className="brand-card" onClick={handleClick}>
            <img src={logo} alt={`Logo ${brand}`} />
        </div>
  );
}