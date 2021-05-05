package com.modzo.foodnutrition;

import com.modzo.foodnutrition.parser.PageData;
import com.modzo.foodnutrition.parser.PageDataParser;
import com.modzo.foodnutrition.render.Pages;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Executor implements CommandLineRunner {

    private final List<Crawlable> crawlableList;

    private final PageDataParser pageDataParser;

    private final Pages pages;

    public Executor(List<Crawlable> crawlableList,
                    PageDataParser pageDataParser, Pages pages) {
        this.crawlableList = crawlableList;
        this.pages = pages;
        this.pageDataParser = pageDataParser;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Integer> collectedPages = IntStream.rangeClosed(1, 10690)
                .mapToObj(number -> crawlableList.stream().map(crawlable -> crawlable.crawl(number)).collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(CrawledContent::getPage)
                .collect(Collectors.toList());

        List<Integer> parsedPages = collectedPages.parallelStream()
                .map(page -> crawlableList.stream().map(crawlable -> crawlable.crawl(page)).collect(Collectors.toList()))
                .flatMap(Collection::parallelStream)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(pageDataParser::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(PageData::getPage)
                .collect(Collectors.toList());


        pages.render(parsedPages);
    }
}