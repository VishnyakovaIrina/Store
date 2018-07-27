package home.ivishnyakova.store.dao.storeDao;

import home.ivishnyakova.store.dao.GoodsDao;
import home.ivishnyakova.store.entity.Goods;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.exceptions.ValidationException;
import home.ivishnyakova.store.message.ErrorProperties;
import home.ivishnyakova.store.utils.LoggerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/*Класс GoodsStoreDao реализовывает запросы CRUD к таблице goods.
*
*Автор: Вишнякова И.Н.
* */
@Repository
public class GoodsStoreDao implements GoodsDao {

    //для логирования
    private static final Logger logger = LogManager.getLogger(LoggerUtil.getClassName());

    //для выполнения запросов к БД
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    //сообщения - для логгирования
    @Resource(name="messages")
    private Properties messages;

    //описания ошибок
    @Resource(name="errors")
    private ErrorProperties errors;

    public GoodsStoreDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        this.namedParameterJdbcTemplate = Optional.ofNullable(namedParameterJdbcTemplate)
                .orElseThrow(( ) -> new IllegalArgumentException(errors.getErrorMessage("ILLEGAL_JDBC").getMessage()));
    }

    /*Метод insert выполняет добавление товара  в таблицу БД goods.
    * @param    goods - товар для добавления.
    * @return   true - товар добавлен и будет проинициализировано
    *           поле id, иначе - нет.
    * @throws   StoreException - товар не добавлен - произошла ошибка при выполнении запроса.
    * */
    @Override
    public boolean insert(Goods goods) throws ValidationException {
        boolean result = false;
        if (Optional.ofNullable(goods).isPresent()) {

            goods.validate(messages);

            String sql = "INSERT INTO goods(name,price,description,in_storage,id_category,id_producer) " +
                    "VALUES(:name,:price,:description,:in_storage,:id_category,:id_producer)";

            //для получения ключа новой записи
            KeyHolder keyHolder = new GeneratedKeyHolder();

            //формирование отображения именованных параметров
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("name", goods.getName());
            parameters.addValue("price", goods.getPrice());
            parameters.addValue("description", goods.getDescription());
            parameters.addValue("in_storage", goods.isIn_storage());
            parameters.addValue("id_category", goods.getId_category());
            parameters.addValue("id_producer", goods.getId_producer());

            try{
                if (result = (namedParameterJdbcTemplate.update(sql, parameters, keyHolder, new String[]{"id"}) == 1)) {
                    //получить значение ключа добавленного продукта
                    Integer generatedId = keyHolder.getKey().intValue();
                    goods.setId(generatedId);
                }
            }catch(DataAccessException e){
                logger.error(messages.getProperty("NOT_INSERTED_GOODS") + ": " + goods, e);
                throw new StoreException(errors.getErrorMessage("NOT_INSERTED_PRODUCT"), e);
            }
        }
        return result;
    }

    /*Метод update выполняет обновление товара в таблице БД goods.
    * @param    goods - новая инфо о товаре.
    * @return   true - товар обновлен, иначе - нет.
    * @throws   StoreException - товар не обновлен - произошла ошибка при выполнении запроса.
    * */
    @Override
    public boolean update(Goods goods) throws ValidationException  {

        if (Optional.ofNullable(goods).isPresent()) {

            goods.validate(messages);

            String sql = "UPDATE goods SET name=:name, price=:price, description=:description, " +
                         "in_storage=:in_storage, id_category=:id_category, id_producer=:id_producer " +
                         "WHERE id=:id";

            //формирование отображения именованных параметров
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", goods.getName());
            parameters.put("price", goods.getPrice());
            parameters.put("description", goods.getDescription());
            parameters.put("in_storage", goods.isIn_storage());
            parameters.put("id_category", goods.getId_category());
            parameters.put("id_producer", goods.getId_producer());
            parameters.put("id", goods.getId());

            try {
                return namedParameterJdbcTemplate.update(sql, parameters) == 1;
            }catch (DataAccessException e){
                logger.error(messages.getProperty("NOT_UPDATED_GOODS") + ": " + goods, e);
                throw new StoreException(errors.getErrorMessage("NOT_UPDATED_PRODUCT"), e);
            }
        }
        return false;
    }

    /*Метод delete выполняет удаление товара из таблицы БД goods по id.
    * @param    id товара.
    * @return   true - товар удален, иначе - нет (не найден).
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    * */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM goods WHERE id=:id";
        try {
            return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id)) == 1;
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NOT_DELETED_GOODS") + ": " + id, e);
            throw new StoreException(errors.getErrorMessage("NOT_DELETED_PRODUCT"), e);
        }
    }

    /*Метод getGoodsById выполняет получение товара из таблицы БД goods по id.
    * @param    id товара.
    * @return   товар - если такой есть в БД, иначе - null (не найден).
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    * */
    @Override
    public Goods getGoodsById(int id) {
        String sql = "SELECT * FROM goods WHERE id=:id";
        try{
            return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), new GoodsMapper());
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NO_PRODUCT") + ": " + id, e);
            throw new StoreException(errors.getErrorMessage("NO_PRODUCT"), e);
        }
    }

    /*Метод getGoodsListBySql выполняет получение товара из таблицы БД по заданному запросу sql.
    * @param    sql - запрос на выборку товара.
    * @return   список товаров, или пустой - если товаров (согдасно запросу sql) нет в БД.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    * */
    private List<Goods> getGoodsListBySql(String sql) {
        try{
            return namedParameterJdbcTemplate.query(sql, new GoodsMapper());
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NO_PRODUCTS"), e);
            throw new StoreException(errors.getErrorMessage("NO_PRODUCTS"), e);
        }
    }

    /*Метод getGoodsList возвращает список товаров (с сортировкой в обратном порядке по коду).
    * @return   список товаров, или пустой - если товаров нет в БД.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    * */
    @Override
    public List<Goods> getGoodsList() {
        String sql = "SELECT * FROM goods ORDER BY id DESC";
        return getGoodsListBySql(sql);
    }

    /*Метод getGoodsList возвращает список товаров
    * (с сортировкой согласно параметру isAsc по наименованию товара).
    * @return   список товаров, или пустой - если товаров нет в БД.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    * */
    @Override
    public List<Goods> getGoodsList(boolean isAsc) {
        String sql = "SELECT * FROM goods ORDER BY name " + (isAsc ? "ASC" : "DESC");
        return getGoodsListBySql(sql);
    }

    /*Метод getGoodsListByFilter выполняет запрос на выборку товаров согласно параметра фильтра
    * с сортировкой по возрастанию по наименованию товара.
    * @param    minPrice - минимальная цена.
    * @param    maxPrice - максимальная цена.
    * @param    id_category - код категории.
    * @param    id_producer - код производитель.
    * @param    in_storage - на складе или нет
    * В запрос включаются параметры при выполнении след.условий:
    *- id_producer <> 0;
    *- id_category <> 0;
    *- minPrice <= maxPrice и minPrice <> 0 и maxPrice <> 0;
    * @return   список товаров, или пустой - если нет товаров в БД согдасно фильтру.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    * */
    @Override
    public List<Goods> getGoodsListByFilter(float minPrice, float maxPrice, int id_category, int id_producer, boolean in_storage) {

        //формирование отображения именованных параметров
        Map<String, Object> parameters = new HashMap<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM goods WHERE in_storage = ");
        sql.append(in_storage);

        if(id_category != 0) {
            sql.append(" AND id_category = ");
            sql.append(id_category);
        }

        if(id_producer != 0){
            sql.append(" AND id_producer = ");
            sql.append(id_producer);
       }

        if((minPrice != 0.0 || maxPrice != 0.0) && minPrice <= maxPrice) {
            sql.append(" AND price BETWEEN :minPrice AND :maxPrice");
            parameters.put("minPrice", minPrice);
            parameters.put("maxPrice", maxPrice);
        }

        sql.append(" ORDER BY name ASC");
        logger.info("SQL = " + sql.toString());

        try{
            return namedParameterJdbcTemplate.query(sql.toString(), parameters, new GoodsMapper());
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NO_PRODUCTS"), e);
            throw new StoreException(errors.getErrorMessage("NO_PRODUCTS"), e);
        }
    }

    /*Класс GoodsMapper преобразовывает строку таблицы goods
    * в объект Goods. */
    private static class GoodsMapper implements RowMapper<Goods> {
        @Override
        public Goods mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Goods(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getFloat("price"),
                    resultSet.getString("description"),
                    resultSet.getBoolean("in_storage"),
                    resultSet.getInt("id_category"),
                    resultSet.getInt("id_producer")
            );
        }
    }
}
