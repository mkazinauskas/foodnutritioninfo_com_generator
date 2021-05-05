package com.modzo.foodnutrition.render;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.modzo.foodnutrition.Seo;
import com.modzo.foodnutrition.data.DataPersistence;
import com.modzo.foodnutrition.parser.PageData;
import com.modzo.foodnutrition.templates.TemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.print.attribute.standard.PageRanges;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class Pages {

    private final TemplateRenderer templateRenderer;

    private final String outputPath;

    public Pages(@Value("${output.path}") String outputPath, TemplateRenderer templateRenderer) {
        this.outputPath = outputPath;
        this.templateRenderer = templateRenderer;
    }

    public void render(List<Integer> parsedPageData) {
        index();
        foodDatabase(parsedPageData);
        parsedPageData.parallelStream()
                .map(PageData::load)
                .forEach(item-> foodItem(item, random50Items(parsedPageData)));
        searchResults();
        sitemapIndex(parsedPageData);
        privacyPolicy();
    }

    private List<PageData> random50Items(List<Integer> parsedPageData) {
        List<Integer> items = Lists.newCopyOnWriteArrayList(parsedPageData);
        Collections.shuffle(items);
        return items.parallelStream()
                .limit(50)
                .map(PageData::load)
                .collect(Collectors.toList());
    }

    private void index() {
        ImmutableMap<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("pageTitle", "Nutritional Values For Common Foods And Products")
                .put("pageDescription", "Food nutritional facts with detailed information and search. Easy to find calories, proteins and fats")
                .put("pageKeywords", "nutrition, nutritional value, food, product, calories, proteins")
                .build();

        String page = templateRenderer.render("index", params);
        DataPersistence.save(outputPath + "/rendered/index.html", page);
    }

    private void searchResults() {
        ImmutableMap<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("pageTitle", "Search Results of Nutritional Values For Common Foods And Products")
                .put("pageDescription", "Search results of food nutritional facts with detailed information.")
                .put("pageKeywords", "search, results, nutrition, nutritional value, food, product, calories, proteins")
                .build();

        String page = templateRenderer.render("food_nutrition_search_results", params);
        DataPersistence.save(outputPath + "/rendered/food-nutrition-search-results.html", page);
    }

    private void foodDatabase(List<Integer> parsedPageData) {
        int totalElementSize = parsedPageData.size();
        int partitionSplitBy = 30;

        List<List<Integer>> partitions = Lists.partition(parsedPageData, partitionSplitBy);
        int partitionSize = partitions.size();
        for (int i = 0; i < partitionSize; i++) {
            String nextPage = "";
            if (i < partitionSize - 1) {
                int nextPageIndex = i * partitionSplitBy + partitionSplitBy + partitionSplitBy;
                nextPage = (i * partitionSplitBy + partitionSplitBy + 1) + "-" + (nextPageIndex > totalElementSize ? totalElementSize : nextPageIndex);
            }
            String currentPage = "";
            if (i > 0) {
                int lastPageSize = i * partitionSplitBy + partitionSplitBy;
                currentPage = (i * partitionSplitBy + 1) + "-" + (lastPageSize > totalElementSize ? totalElementSize : lastPageSize);
            }

            String previousPage = "";
            String seoPreviousPage = "";
            if (i > 0) {
                previousPage = (i * partitionSplitBy - partitionSplitBy + 1) + "-" + (i * partitionSplitBy);
            }

            if (i == 1) {
                seoPreviousPage = "";
            } else {
                seoPreviousPage = "-" + Seo.text(previousPage);
            }
            foodDatabase(partitions.get(i), nextPage, currentPage, previousPage, seoPreviousPage);
        }
    }

    private void foodDatabase(List<Integer> parsedPageData, String nextPage, String currentPage, String previousPage, String seoPreviousPage) {
        List<PageData> pages = parsedPageData.stream().map(PageData::load).collect(Collectors.toList());

        ImmutableMap<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("pageTitle", "Nutritional Values For Common Foods And Products Database")
                .put("pageDescription", "Food nutritional facts with detailed information from database.")
                .put("pageKeywords", "nutrition nutritional value food product database")
                .put("pages", pages)
                .put("nextPage", nextPage)
                .put("hasNextPage", !StringUtils.isEmpty(nextPage))
                .put("seoNextPage", Seo.text(nextPage))
                .put("previousPage", previousPage)
                .put("seoPreviousPage", seoPreviousPage)
                .put("hasPreviousPage", !StringUtils.isEmpty(previousPage))
                .build();

        String page = templateRenderer.render("food_database", params);
        if (StringUtils.isEmpty(currentPage)) {
            DataPersistence.save(outputPath + "/rendered/database/foods.html", page);
        } else {
            DataPersistence.save(outputPath + "/rendered/database/foods-" + currentPage + ".html", page);
        }
    }

    private void foodItem(PageData parsedPageData, List<PageData> lastSearches) {
        ImmutableMap<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("pageTitle", parsedPageData.getTitle() + " Nutritional Food Info")
                .put("pageDescription", parsedPageData.getTitle() + " nutritional food facts, calories, vitamins, proteins, fats info")
                .put("pageKeywords", parsedPageData.getTitle() + " nutrition nutritional value calories vitamins proteins fats info")
                .put("data", parsedPageData)
                .put("lastSearches", lastSearches)
                .build();

        String page = templateRenderer.render("food_item", params);
        DataPersistence.save(outputPath + "/rendered/foods/" + parsedPageData.getSeoTitle() + ".html", page);
    }

    private void sitemapIndex(List<Integer> parsedPageData) {
        List<PageData> pages = parsedPageData.parallelStream().map(PageData::load).collect(Collectors.toList());

        ImmutableMap<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("data", pages)
                .put("lastModified", LocalDate.now().toString())
                .build();

        String page = templateRenderer.render("sitemap", params);
        DataPersistence.save(outputPath + "/rendered/sitemap/sitemap.xml", page);
    }

    private void privacyPolicy() {
        ImmutableMap<String, Object> params = ImmutableMap.<String, Object>builder()
                .put("pageTitle", "Privacy policy")
                .put("pageDescription", "Food nutritional facts with detailed information and search.")
                .put("pageKeywords", "nutrition, nutritional value, food, product, calories, proteins")
                .build();

        String page = templateRenderer.render("privacy_policy", params);
        DataPersistence.save(outputPath + "/rendered/privacy-policy.html", page);
    }
}
