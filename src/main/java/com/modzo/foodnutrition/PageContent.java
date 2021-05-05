package com.modzo.foodnutrition;

import org.springframework.http.HttpEntity;

public class PageContent {
    private final String body;

    public PageContent(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public static PageContent of(HttpEntity<String> response){
        return new PageContent(response.getBody());
    }
}
