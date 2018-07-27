package home.ivishnyakova.store.dao.storeDao;

import home.ivishnyakova.store.exceptions.ValidationException;
import home.ivishnyakova.store.message.ErrorProperties;
import home.ivishnyakova.store.utils.LoggerUtil;
import home.ivishnyakova.store.dao.ProducerDao;
import home.ivishnyakova.store.entity.Producer;
import home.ivishnyakova.store.exceptions.StoreException;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/*Класс ProducerStoreDao предоставляет реализацию доступа к данным о
* производителе товаров в интернет-магазине с применением "чистого" JDBC.
*
* Автор: Вишнякова И.
* */
@Repository
public class ProducerStoreDao implements ProducerDao {

    private Connection connection;  //соединение к БД
    private GenericObjectPool<Connection> connectionPool;   //пул соединений

    //для логирования
    private static final Logger logger = LogManager.getLogger(LoggerUtil.getClassName());

    //сообщения - для логгирования и описания ошибок
    @Resource(name="messages")
    private Properties messages;

    //описания ошибок
    @Resource(name="errors")
    private ErrorProperties errors;

    public ProducerStoreDao(){}

    @Autowired
    public ProducerStoreDao(GenericObjectPool<Connection> connectionPool) throws Exception {
        this.connectionPool = Optional.ofNullable(connectionPool)
                .orElseThrow(()-> new IllegalArgumentException(messages.getProperty("ILLEGAL_CONNECTION_POOL")));
        this.connection = connectionPool.borrowObject();
    }

    @PreDestroy
    public void destroy() throws Exception {
        Optional.ofNullable(connection).ifPresent(
            (connectionTmp) ->  connectionPool.returnObject(connectionTmp));
    }

    /*Метод insert выполняет добавление нового producer в БД.
    * @param    producer - новый производитель.
    * @returns  Если производитель добавлен в БД, то в producer будет обновлено поле id (содержит первичный ключ).
    * @throws   ValidationException - данные нового производителя не корректны.
    * @throws   StoreException - новый производитель не добавлен.
    *           Если в БД уже имеется такой производитель (название производителя должно быть уникальным)
    *           или по причине отсутствия соединения с БД,
    *           или ошибка при обработке запроса.
    * */
    @Override
    public void insert(Producer producer) throws ValidationException{
        Producer producerOpt = Optional.ofNullable(producer)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_SPECIFIED_PRODUCER")));
        producerOpt.validate(messages);

        String sql = "INSERT INTO producers(name) VALUES(?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            statement.setString(1, producerOpt.getName());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                producer.setId(resultSet.getInt("id"));
            }
        }
        catch (SQLException e){
            throw new StoreException(errors.getErrorMessage("NOT_INSERTED_PRODUCER"), e);
        }
    }

    /*Метод update выполняет обновление информации о производителе с id в БД.
    * @param    producer - новые данные производителя с id.
    * @returns  true - данные производителя были обновлены, иначе - нет (не был найден).
    * @throws   ValidationException - данные нового производителя не корректны.
    * @throws   StoreException - данные производителя не были обновлены.
    *           Если в результате обновления инфо о производителе нарушается уникальность
    *           наименования производителей в БД
    *           или отсутствует соединение с БД,
    *           или ошибка при обработке запроса
    * */
    @Override
    public boolean update(Producer producer) throws ValidationException{

        Producer producerOpt = Optional.ofNullable(producer)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_SPECIFIED_PRODUCER")));
        producerOpt.validate(messages);

        String sql = "UPDATE producers SET name=? WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, producerOpt.getName());
            statement.setInt(2, producerOpt.getId());
            return statement.executeUpdate() == 1;
        }
        catch (SQLException e){
            throw new StoreException(errors.getErrorMessage("NOT_UPDATED_PRODUCER"), e);
        }
    }

    /*Метод delete удаляет производителя с id из БД.
    * @param    id производителя, кот. следует удалить.
    * @returns  true - производитель удален, иначе - нет (не был найден).
    * @throws   StoreException - производитель не удален.
    *           Если отсутствует соединение с БД,
    *           или ошибка при обработке запроса.
    * */
    @Override
    public boolean delete(int id) throws StoreException{
        String sql = "DELETE FROM producers WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
        catch (SQLException e){
            throw new StoreException(errors.getErrorMessage("NOT_DELETED_PRODUCER"), e);
        }
    }

    /*Метод getProducerById выполняет выборку инфо о производителе по его коду id.
    * @param    id производителя, кот. следует найти.
    * @returns  производитель, если такой есть в БД, иначе - StoreException.
    * @throws   StoreException - производитель не был найден,
    *           или отсутствует соединение с БД,
    *           или ошибка при обработке запроса.
    * */
    @Override
    public Producer getProducerById(int id) throws StoreException{
        Producer producer = null;
        String sql = "SELECT * FROM producers WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                if (resultSet.next()) {
                    producer = new Producer();
                    producer.setId(resultSet.getInt("id"));
                    producer.setName(resultSet.getString("name"));
                }
            }
        }
        catch (SQLException e){
            throw new StoreException(errors.getErrorMessage("NO_PRODUCER"), e);
        }

        return Optional.ofNullable(producer)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NO_PRODUCER")));
    }


    /*Метод getProducerListBySql выполняет выборку инфо об производителях согласно запросу sql.
    * @param    sql запрос на выборку.
    * @returns  список производителей.
    * @throws   StoreException - отсутствует соединение с БД  или ошибка при обработке запроса.
    * */
    private List<Producer> getProducerListBySql(String sql) throws StoreException{
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();

            List<Producer> producers = new ArrayList<>();
            while (resultSet.next()){
                Producer producer = new Producer();
                producer.setId(resultSet.getInt("id"));
                producer.setName(resultSet.getString("name"));
                producers.add(producer);
            }
            return producers;
        }
        catch (SQLException e){
            throw new StoreException(errors.getErrorMessage("NO_PRODUCERS"), e);
        }
    }

    /*Метод getProducerList выполняет выборку инфо об всех производителях.
    * @returns  список производителей.
    * @throws   StoreException - отсутствует соединение с БД  или ошибка при обработке запроса.
    * */
    @Override
    public List<Producer> getProducerList() throws StoreException{
        String sql = "SELECT * FROM producers";
        return getProducerListBySql(sql);
    }

    /*Метод getProducerSortList выполняет выборку инфо об всех производителях
    * с сортировкой по названию производителей.
    * @param    isAsc вид сортировки (true - по возрастанию).
    * @returns  список производителей с указанным видом сортировки.
    * @throws   StoreException - отсутствует соединение с БД  или ошибка при обработке запроса.
    * */
    @Override
    public List<Producer> getProducerSortList(boolean isAsc) throws StoreException{
        String sql = isAsc ? "SELECT * FROM producers ORDER BY name ASC" :
                             "SELECT * FROM producers ORDER BY name DESC";
        return getProducerListBySql(sql);
    }
}
