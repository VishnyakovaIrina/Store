package home.ivishnyakova.store.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/*ApplicationContextConfig - класс-настройки контекста приложения.
* Автор: Вишнякова И.
* */
@Configuration
@ComponentScan("home.ivishnyakova.store.*")
@EnableWebMvc
@ImportResource({
        "classpath:context/root-context.xml",
        "classpath:context/db-context.xml",
        "classpath:context/message-context.xml"})
public class ApplicationContextConfig {

    /*Распознаватель представлений*/
    @Bean(name = "viewResolver")
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

}
