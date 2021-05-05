package com.modzo.foodnutrition;

public class Seo {
    public static String text(String text){
        return  text.toLowerCase()
                .replaceAll(" ?- ?","-") // remove spaces around hyphens
                .replaceAll("[ ']","-") // turn spaces and quotes into hyphens
                .replaceAll("[^0-9a-zA-Z-]","");
    }
}
