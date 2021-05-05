package com.modzo.foodnutrition.parser;

public interface Parser {

    PageData parse(int page, String raw);
}
