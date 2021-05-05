package com.modzo.foodnutrition.templates;

import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.util.Map;

@Component
public class TemplateRenderer {

    private final Mustache.Compiler compiler;

    private final MustacheResourceTemplateLoader templateLoader;

    public TemplateRenderer(Mustache.Compiler compiler, MustacheResourceTemplateLoader templateLoader) {
        this.compiler = compiler;
        this.templateLoader = templateLoader;
    }

    public String render(String templateName, Map<String, Object> context) {
        try {
            Reader templateReader = templateLoader.getTemplate(templateName);
            Template template = compiler.compile(templateReader);
            return template.execute(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
