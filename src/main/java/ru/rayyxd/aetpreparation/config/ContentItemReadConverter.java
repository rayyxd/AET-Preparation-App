package ru.rayyxd.aetpreparation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import ru.rayyxd.aetpreparation.noSqlEntities.ModuleNoSQL.ContentItem;

import java.io.IOException;

/**
 * Конвертер, который берёт org.bson.Document (то, что Spring Data вычитала из Mongo)
 * и преобразует его в конкретный подкласс ContentItem (HeadingContent, QuizContent и т.п.)
 * с помощью Jackson ObjectMapper.
 */
public class ContentItemReadConverter implements Converter<Document, ContentItem> {

    // ObjectMapper будет учитывать все @JsonTypeInfo/@JsonSubTypes в вашем ContentItem
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ContentItem convert(Document source) {
        try {
            // Document.toJson() вернёт строку JSON вида {"type":"quiz", "question": "...", ...}
            String json = source.toJson();
            // Jackson прочитает поле "type" и создаст нужный подкласс
            return mapper.readValue(json, ContentItem.class);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось десериализовать ContentItem из BSON: " + source.toJson(), e);
        }
    }
}
