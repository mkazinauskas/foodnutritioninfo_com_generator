package com.modzo.foodnutrition.parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.modzo.foodnutrition.NutritionData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NutritionDataSiteParser implements Parser {

    private static final Logger LOG = LoggerFactory.getLogger(NutritionData.class);

    @Override
    public PageData parse(int page, String raw) {
        Document document = Jsoup.parse(raw);
        return new PageData.Builder()
                .setPage(page)
                .setTitle(getTitle(document))
                .setServingSize(getServingSize(document))
                .setNutrientBalance(getNutrientBalance(document))
                .setProteinQuality(getProteinQuality(document))
                .setNutritionInformation(getNutritionInformation(document))
                .setGood(getGoodInformation(document))
                .setBad(getBadInformation(document))
                .setGlycemicLoad(getGlycemicLoad(document))
                .setCarbs(getCarbs(document))
                .setFats(getFats(document))
                .setProtein(getProtein(document))
                .build();
    }

    private String getCarbs(Document document) {
        return document.select("table td.carbs").text();
    }

    private String getFats(Document document) {
        return document.select("table td.fats").text();
    }

    private String getProtein(Document document) {
        return document.select("table td.protein").text();
    }

    private String getGlycemicLoad(Document document) {
        return document.select("#SCORE_ESTIMATED_GLYCEMIC_LOAD").text();
    }

    private String getGoodInformation(Document document) {
        String goodInfo = goodAndBadParts(document).stream()
                .filter(item -> item.startsWith("The good:"))
                .map(item -> item.replace("The good: ", ""))
                .collect(Collectors.joining(","));
        return StringUtils.isEmpty(goodInfo) ? "" : goodInfo;
    }

    private String getBadInformation(Document document) {
        String badInfo = goodAndBadParts(document).stream()
                .filter(item -> item.startsWith("The bad:") || !item.startsWith("The good:"))
                .map(item -> item.replace("The bad: ", ""))
                .collect(Collectors.joining(","));
        return StringUtils.isEmpty(badInfo) ? "" : badInfo;
    }

    private List<String> goodAndBadParts(Document document) {
        String allText = document.select(".opinion_description p").text();
        if (allText.isEmpty()) {
            return ImmutableList.of();
        } else {
            return Arrays.stream(allText.split(" The bad: "))
                    .collect(Collectors.toList());
        }
    }

    private String getTitle(Document document) {
        return document.title().replace(" Nutrition Facts & Calories", "");
    }

    private String getServingSize(Document document) {
        return "100 grams";
    }

    private String getNutrientBalance(Document document) {
        return document.select("#nutrient-balance-container .box_PQI").text();
    }

    private String getProteinQuality(Document document) {
        return document.select("#protein-quality-container .box_PQI").text();
    }

    private Map<PageData.NutritionInformation.Subject, List<PageData.NutritionInformation.Data>> getNutritionInformation(Document document) {
        ImmutableMap.Builder<PageData.NutritionInformation.Subject, List<PageData.NutritionInformation.Data>> builder =
                ImmutableMap.builder();

        builder.put(PageData.NutritionInformation.Subject.CALORIE_INFORMATION, calories(document));
        builder.put(PageData.NutritionInformation.Subject.PROTEIN_AND_AMINO_ACIDS, nutritions("PROTEINS", document));
        builder.put(PageData.NutritionInformation.Subject.VITAMINS, nutritions("VITAMINS", document));
        builder.put(PageData.NutritionInformation.Subject.MINERALS, nutritions("MINERALS", document));
        builder.put(PageData.NutritionInformation.Subject.STEROLS, nutritions("STEROLS", document));
        builder.put(PageData.NutritionInformation.Subject.OTHER, nutritions("OTHER", document));
        builder.put(PageData.NutritionInformation.Subject.FATS_AND_FATTY_ACIDS, nutritions("FATS", document));
        builder.put(PageData.NutritionInformation.Subject.CARBOHYDRATES, nutritions("CARBOHYDRATES", document));

        return builder.build();
    }

    private List<PageData.NutritionInformation.Data> nutritions(String groupName, Document document) {
        List<PageData.NutritionInformation.Data> data = new ArrayList<>();
        for (Element element : document.select("#GROUP_" + groupName + " div.clearer")) {
            extractData(data, element);
        }
        return data;
    }

    private List<PageData.NutritionInformation.Data> calories(Document document) {
        List<PageData.NutritionInformation.Data> data = new ArrayList<>();

        Element first = document.select("#NutritionInformationSlide .groupBorder").first();

        if (first == null) {
            LOG.info("No nutrition info found `{}`", document.text());
            throw new RuntimeException("Error");
        }
        for (Element element : first.select(".clearer")) {
            extractData(data, element);
        }
        return data;
    }

    private void extractData(List<PageData.NutritionInformation.Data> data, Element element) {
        Elements div = element.select("div.left");
        String name = div.get(0).text();
        String value = div.get(1).text().trim() + div.get(2).text().trim();
        String dailyValue = div.get(3).text().trim();
        data.add(new PageData.NutritionInformation.Data(name, value, dailyValue));
    }

}