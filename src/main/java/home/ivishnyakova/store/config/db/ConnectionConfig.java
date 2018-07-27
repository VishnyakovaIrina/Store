package home.ivishnyakova.store.config.db;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.sql.Connection;

/*Класс ConnectionConfig предназначен для создания пула соединений Connection.
*
* Автор: Вишнякова И.
* */
@Configuration
public class ConnectionConfig {

    //фабрика соединений с БД
    @Resource(name="connectionFactory")
    private ConnectionFactory connectionFactory;

    //пул соединений с БД
    @Bean(name="connectionPool")
    public GenericObjectPool<Connection> getConnectionPool(){
        return new GenericObjectPool<>(connectionFactory);
    }

}
