package com.lohith.gymtracker.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseConfig {

    @Bean
    public static BeanFactoryPostProcessor dbUrlNormalizer() {
        return beanFactory -> {
            ConfigurableEnvironment env = beanFactory.getBean(ConfigurableEnvironment.class);
            String url = env.getProperty("spring.datasource.url");
            if (url != null) {
                String fixedUrl = url;
                if (fixedUrl.startsWith("postgres://")) {
                    fixedUrl = "jdbc:postgresql://" + fixedUrl.substring(11);
                } else if (fixedUrl.startsWith("postgresql://") && !fixedUrl.startsWith("jdbc:postgresql://")) {
                    fixedUrl = "jdbc:postgresql://" + fixedUrl.substring(13);
                }
                if (!fixedUrl.equals(url)) {
                    Map<String, Object> props = new HashMap<>();
                    props.put("spring.datasource.url", fixedUrl);
                    env.getPropertySources().addFirst(new MapPropertySource("dbUrlNormalizer", props));
                }
            }
        };
    }
}
