package com.modzo.foodnutrition.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.modzo.foodnutrition.Seo;
import com.modzo.foodnutrition.data.DataPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.modzo.foodnutrition.parser.PageData.NutritionInformation.Subject.*;
import static org.apache.commons.lang3.StringUtils.*;

public class PageData {
    private static final Logger LOG = LoggerFactory.getLogger(DataPersistence.class);

    private final int page;

    private final String title;

    private final String good;

    private final String bad;

    private final String glycemicLoad;

    private final String carbs;

    private final String fats;

    private final String protein;

    private final String servingSize;

    private final String nutrientBalance;

    private final String proteinQuality;

    private final NutritionInformation nutritionInformation;

    @JsonCreator
    public PageData(@JsonProperty("page") int page,
                    @JsonProperty("title") String title,
                    @JsonProperty("good") String good,
                    @JsonProperty("bad") String bad,
                    @JsonProperty("glycemicLoad") String glycemicload,
                    @JsonProperty("carbs") String carbs,
                    @JsonProperty("fats") String fats,
                    @JsonProperty("protein") String protein,
                    @JsonProperty("servingSize") String servingSize,
                    @JsonProperty("nutrientBalance") String nutrientBalance,
                    @JsonProperty("proteinQuality") String proteinQuality,
                    @JsonProperty("nutritionInformation") NutritionInformation nutritionInformation) {
        this.page = page;
        this.title = title;
        this.good = good;
        this.bad = bad;
        this.glycemicLoad = glycemicload;
        this.carbs = carbs;
        this.fats = fats;
        this.protein = protein;
        this.servingSize = servingSize;
        this.nutrientBalance = nutrientBalance;
        this.proteinQuality = proteinQuality;
        this.nutritionInformation = nutritionInformation;
    }

    public int getPage() {
        return page;
    }

    public String getTitle() {
        return title;
    }

    @JsonIgnore
    public String getSeoTitle() {
        return Seo.text(title);

    }

    public String getGood() {
        return good;
    }

    @JsonIgnore
    public boolean getIsGood(){
        return isNotBlank(good);
    }

    public String getBad() {
        return bad;
    }

    @JsonIgnore
    public boolean getIsBad(){
        return isNotBlank(bad);
    }

    public String getGlycemicLoad() {
        return glycemicLoad;
    }

    public String getCarbs() {
        return carbs;
    }

    public String getFats() {
        return fats;
    }

    public String getProtein() {
        return protein;
    }

    public String getServingSize() {
        return servingSize;
    }

    public String getNutrientBalance() {
        return nutrientBalance;
    }

    public String getProteinQuality() {
        return proteinQuality;
    }

    public NutritionInformation getNutritionInformation() {
        return nutritionInformation;
    }

    public static class NutritionInformation {

        private final Map<Subject, List<Data>> info;

        @JsonCreator
        public NutritionInformation(@JsonProperty("info") Map<Subject, List<Data>> info) {
            this.info = ImmutableMap.copyOf(info);
        }

        public enum Subject {
            CALORIE_INFORMATION,
            PROTEIN_AND_AMINO_ACIDS,
            CARBOHYDRATES,
            VITAMINS,
            FATS_AND_FATTY_ACIDS,
            MINERALS,
            STEROLS,
            OTHER
        }

        public static class Data {
            private final String name;
            private final String value;
            private final String dailyValue;

            @JsonCreator
            public Data(@JsonProperty("name") String name,
                        @JsonProperty("value") String value,
                        @JsonProperty("dailyValue") String dailyValue) {
                this.name = name;
                this.value = value;
                this.dailyValue = dailyValue;
            }

            public String getName() {
                return name;
            }

            public String getValue() {
                return value;
            }

            public String getDailyValue() {
                return dailyValue;
            }
        }

        public Map<Subject, List<Data>> getInfo() {
            return info;
        }

        public List<Data> getCaloriesInformation() {
            return info.get(CALORIE_INFORMATION);
        }

        public List<Data> getProteinAndAminoAcids() {
            return info.get(PROTEIN_AND_AMINO_ACIDS);
        }

        public List<Data> getCarbohydrates() {
            return info.get(CARBOHYDRATES);
        }

        public List<Data> getVitamins() {
            return info.get(VITAMINS);
        }

        public List<Data> getFatsAndFattyAcids() {
            return info.get(FATS_AND_FATTY_ACIDS);
        }

        public List<Data> getMinerals() {
            return info.get(MINERALS);
        }

        public List<Data> getSterols() {
            return info.get(STEROLS);
        }

        public List<Data> getOther() {
            return info.get(OTHER);
        }
    }

    public static class Builder {
        private int page;
        private String title;
        private String good;
        private String bad;
        private String glycemicLoad;
        private String carbs;
        private String fats;
        private String protein;
        private String servingSize;
        private String nutrientBalance;
        private String proteinQuality;
        private Map<NutritionInformation.Subject, List<NutritionInformation.Data>> nutritionInformation = new LinkedHashMap<>();

        public Builder setPage(int page) {
            this.page = page;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setGood (String good) {
            this.good = good;
            return this;
        }

        public Builder setBad (String bad) {
            this.bad = bad;
            return this;
        }

        public Builder setGlycemicLoad (String glycemicLoad) {
            this.glycemicLoad = glycemicLoad;
            return this;
        }

        public Builder setCarbs(String carbs) {
            this.carbs = carbs;
            return this;
        }

        public Builder setFats(String fats) {
            this.fats = fats;
            return this;
        }

        public Builder setProtein(String protein) {
            this.protein = protein;
            return this;
        }

        public Builder setServingSize(String servingSize) {
            this.servingSize = servingSize;
            return this;
        }

        public Builder setNutrientBalance(String nutrientBalance) {
            this.nutrientBalance = nutrientBalance;
            return this;
        }

        public Builder setProteinQuality(String proteinQuality) {
            this.proteinQuality = proteinQuality;
            return this;
        }

        public Builder setNutritionInformation(Map<NutritionInformation.Subject, List<NutritionInformation.Data>> nutritionInformation) {
            this.nutritionInformation = nutritionInformation;
            return this;
        }

        public PageData build() {
            return new PageData(
                    page,
                    title,
                    good,
                    bad,
                    glycemicLoad,
                    carbs,
                    fats,
                    protein,
                    servingSize,
                    nutrientBalance,
                    proteinQuality,
                    new NutritionInformation(nutritionInformation)
            );
        }
    }

    void save(int id) {
        try {
            String text = new ObjectMapper().writeValueAsString(this);
            LOG.info("Saving to file parsed json with id: " + id);
            DataPersistence.save(DataPersistence.Folder.NUTRITION_DATA, id, "parsed", "json", text);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    static boolean exists(int id) {
        return DataPersistence.exists(DataPersistence.Folder.NUTRITION_DATA, id, "parsed", "json");
    }

    public static PageData load(int id) {
        LOG.info("Loading parsed json with id: " + id);
        String loaded = DataPersistence.load(DataPersistence.Folder.NUTRITION_DATA, id, "parsed", "json");
        try {
            return new ObjectMapper().readValue(loaded, PageData.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}