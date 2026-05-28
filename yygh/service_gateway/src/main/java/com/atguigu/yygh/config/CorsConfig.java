package com.atguigu.yygh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 跨域配置
 * 生产环境应将 allowedOriginPatterns 替换为具体的前端域名白名单
 */
public class CorsConfig {

    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        // 限制允许跨域的来源，生产环境必须配置为具体域名白名单
        // 例如: config.addAllowedOriginPattern("https://*.your-domain.com");
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        // 预检请求缓存时间（秒），减少 OPTIONS 请求次数
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
