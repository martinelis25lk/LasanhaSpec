package br.com.lasanhaspec.market_service.util;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * A FIPE devolve o preço como texto no formato "R$ 45.678,00".
 * Aqui a gente normaliza isso pra BigDecimal (pra poder comparar/somar/ordenar de verdade)
 * e formata de volta pro mesmo padrão quando precisa devolver como texto.
 */

public class FipeValueParser {


    private FipeValueParser(){}

        public static BigDecimal parse(String rawValue){
            if(rawValue == null || rawValue.isBlank()){
                return null;
            }


        String cleaned = rawValue
                .replace("R$", "")
                .trim()
                .replace(".", "")
                .replace(",", ".");

            try{
                return new BigDecimal(cleaned);
            } catch (NumberFormatException e) {
                return null;
            }

    }

    public static String format(BigDecimal value){
        if(value == null){
            return null;
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return currencyFormat.format(value);
    }

}
