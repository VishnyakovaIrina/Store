package home.ivishnyakova.store.config.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Autowired;

/*Класс ConnectionFactory представляет собой фабрику соединений к БД.
*
* Автор: Вишнякова И.
* */
public class ConnectionFactory extends BasePooledObjectFactory<Connection> {

    //инфо о соединении к БД
    @Autowired
    private ConnectionInfo connectionInfo;

    public ConnectionFactory(){}

    public ConnectionFactory(ConnectionInfo connectionInfo){
        this.connectionInfo = connectionInfo;
    }

    @Override
    public Connection create() throws Exception {
        Class.forName(connectionInfo.getDriver());
        return DriverManager.getConnection(
                connectionInfo.getUrl(),
                connectionInfo.getUserName(),
                connectionInfo.getPassword());
    }

    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(PooledObject<Connection> connection) throws Exception {
        connection.getObject().close();
    }
}