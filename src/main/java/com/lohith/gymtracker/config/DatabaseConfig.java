package com.lohith.gymtracker.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl();
        if (url != null && url.startsWith("postgres://")) {
            url = "jdbc:postgresql://" + url.substring(11);
        } else if (url != null && url.startsWith("postgresql://") && !url.startsWith("jdbc:postgresql://")) {
            url = "jdbc:postgresql://" + url.substring(13);
        }
        return properties.initializeDataSourceBuilder().url(url).build();
    }
}
