package home.ivishnyakova.store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/*WebMvcConfig - класс-настройка веб-контекста приложения.
* Автор: Вишнякова И.
* */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    //сколько времени ресурсы находятся в кеше (в секундах)
    private static int CACHE_TIME = 86400; //24 * 60 * 60 = сутки

    // настройка статических ресурсов
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // url ресурсов  = контекст страницы + "/resources/css/**"
        registry.addResourceHandler("/resources/css/**")
        // расположение ресурсов относительно папки webApp проекта
                .addResourceLocations("/WEB-INF/resources/css/").setCachePeriod(CACHE_TIME);
        registry.addResourceHandler("/resources/img/**")
                .addResourceLocations("/WEB-INF/resources/img/").setCachePeriod(CACHE_TIME);

    }

    //включить пересылку запросов на сервлетов «по умолчанию»
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
