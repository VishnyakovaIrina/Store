package home.ivishnyakova.store.config;

import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("home.ivishnyakova.store.dao.*")
@ComponentScan("home.ivishnyakova.store.config.*")
@ComponentScan("home.ivishnyakova.store.entity.*")
@ComponentScan("home.ivishnyakova.store.message.*")
@ImportResource({
        "classpath:context/root-context.xml",
        "classpath:context/db-context.xml",
        "classpath:context/message-context.xml"})
public class TestContextConfig {

}
