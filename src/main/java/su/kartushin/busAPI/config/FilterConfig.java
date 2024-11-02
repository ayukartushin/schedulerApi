package su.kartushin.busAPI.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import su.kartushin.busAPI.utils.RequestIdFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestIdFilter> loggingFilter() {
        FilterRegistrationBean<RequestIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestIdFilter());
        registrationBean.addUrlPatterns("/*"); // Настроить маршруты, которые нужно логировать
        return registrationBean;
    }
}