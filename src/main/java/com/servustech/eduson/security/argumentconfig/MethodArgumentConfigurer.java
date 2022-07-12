package com.servustech.eduson.security.argumentconfig;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.format.FormatterRegistry;
import org.springframework.core.convert.converter.Converter;
import java.util.List;
import java.time.ZonedDateTime;

@Configuration
@AllArgsConstructor
public class MethodArgumentConfigurer implements WebMvcConfigurer {

    private final CurrentUserArgumentResolver resolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToZoneDateTime());
    }
    static class StringToZoneDateTime implements Converter<String, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(String value) {
            return ZonedDateTime.parse(value);
        }
    }
}
