package ru.rayyxd.aetpreparation.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories(basePackages = "ru.rayyxd.aetpreparation.noSqlRepositories")
public class MongoConfig {

    // Берём URL из application.properties (например "mongodb://localhost:27017/aet")
    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(new ConnectionString(connectionString));
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory(MongoClient client) {
        // Если нужно, можно указать название БД здесь, либо оно подтягивается из URI
        return new SimpleMongoClientDatabaseFactory(client, 
                new ConnectionString(connectionString).getDatabase());
    }

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        // Регистрируем наши два кастомных конвертера
        converters.add(new ContentItemReadConverter());
        converters.add(new ContentItemWriteConverter());
        return new MongoCustomConversions(converters);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory factory,
            MongoCustomConversions conversions,
            MongoMappingContext context) {

        // DbRefResolver по умолчанию
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        // Основной MappingMongoConverter
        MappingMongoConverter converter =
                new MappingMongoConverter(dbRefResolver, context);

        // Регистрируем наши кастомные конвертеры
        converter.setCustomConversions(conversions);

        // ВАЖНО: по умолчанию Spring Data хранит _class-поле, но мы можем его отключить, 
        // если хотим, чтобы BSON-шаблон был «чистым». 
        // Если поле _class необходимо, можно не комментировать эту строку.
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return converter;
    }
}
