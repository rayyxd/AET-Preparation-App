package ru.rayyxd.aetpreparation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import ru.rayyxd.aetpreparation.noSqlEntities.ModuleNoSQL.ContentItem;

import java.io.IOException;

/**
 * Конвертер, который берёт конкретный Java-объект ContentItem (HeadingContent, QuizContent и т.п.)
 * и сериализует его в org.bson.Document, чтобы Spring Data мог сохранить в MongoDB.
 */
public class ContentItemWriteConverter implements Converter<ContentItem, Document> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Document convert(ContentItem source) {
        try {
            // Переводим Java-объект в JSON-строку (учитываются аннотации Jackson)
            String json = mapper.writeValueAsString(source);
            // Затем парсим эту JSON-строку в org.bson.Document
            return Document.parse(json);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сериализовать ContentItem в BSON: " + source, e);
        }
    }
}
