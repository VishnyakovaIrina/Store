package home.ivishnyakova.store.dao.storeDao;

import home.ivishnyakova.store.dao.CategoryDao;
import home.ivishnyakova.store.entity.Category;
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
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/*Класс CategoryStoreDao реализует запросы CRUD к таблице categories.
*
*
* Записи в таблице категорий образуют древовидную структуру.
* В таблице должна быть только одна запись (корень дерева)
* с no_level = 0 и id_category = null.
* Категории имеют уровень no_level = 1, подкатегории -  no_level = 2 и т.д. до 5.
* Другие категории не должны иметь id_category = null.
*
* Автор: Вишнякова И.Н.
* */
@Repository
public class CategoryStoreDao implements CategoryDao {

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

    public CategoryStoreDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        this.namedParameterJdbcTemplate = Optional.ofNullable(namedParameterJdbcTemplate)
                .orElseThrow(( ) -> new IllegalArgumentException(errors.getErrorMessage("ILLEGAL_JDBC").getMessage()));
    }

    public void setMessages(Properties messages) {
        this.messages = messages;
    }

    /*Метод insert выполняет добавление категории  в таблицу БД categories.
    * @param    category - категория для добавления.
    * @return   true - категория добавлена, иначе - нет.
    * @throws   StoreException - категория не добавлена - произошла ошибка при выполнении запроса.
    * */
    @Override
    public boolean insert(Category category) throws ValidationException {
        boolean result = false;
        if (Optional.ofNullable(category).isPresent()) {
            category.validate(messages);
            try {
                //добавление корневой категории
                if (category.getId_category() == 0) {
                    //попытка добавить корневую категорию, если в таблице уже есть корневая категория
                    if (isRootCategory()) {
                        throw new StoreException(errors.getErrorMessage("ROOT_CATEGORY"));
                    } else {
                        result = insertRootCategory(category);
                    }
                    //добавление не корневой категории
                } else
                    result = insertNotRootCategory(category);
            }catch (DataAccessException e){
                logger.error(messages.getProperty("NOT_INSERTED_CATEGORY") + ": " + category, e);
                throw new StoreException(errors.getErrorMessage("NOT_INSERTED_CATEGORY"), e);
            }
        }
        return result;
    }

    /*Метод insertRootCategory выполняет добавление корневой категории  в таблицу БД categories.
    * @param    category - категория для добавления.
    * @return   true - категория добавлена, иначе - нет.
    * @throws   StoreException - категория не добавлена - произошла ошибка при выполнении запроса.
    * */
    private boolean insertRootCategory(Category category){
        String sql = "INSERT INTO categories(name) VALUES(:name)";

        //для получения ключа новой записи
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //формирование отображения именованных параметров
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", category.getName());

        if (namedParameterJdbcTemplate.update(sql, parameters, keyHolder, new String[]{"id"}) == 1) {
            //получить значение ключа добавленного продукта
            Integer generatedId = keyHolder.getKey().intValue();
            category.setId(generatedId);
            return true;
        }
        return false;
    }

    /*Метод insertNotRootCategory выполняет добавление некорневой категории  в таблицу БД categories.
    * @param    category - категория для добавления.
    * @return   true - категория добавлена, иначе - нет.
    * @throws   StoreException - категория не добавлена - произошла ошибка при выполнении запроса.
    * */
    private boolean insertNotRootCategory(Category category){
        //с уровнем 0 может быть только корневая категория
        if (category.getNo_level() == 0)
            throw new StoreException(errors.getErrorMessage("ROOT_CATEGORY"));

        String sql = "INSERT INTO categories(name,no_level,id_category) VALUES(:name,:no_level,:id_category)";

        //для получения ключа новой записи
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //формирование отображения именованных параметров
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", category.getName());
        parameters.addValue("no_level", category.getNo_level());
        parameters.addValue("id_category", category.getId_category());

        if (namedParameterJdbcTemplate.update(sql, parameters, keyHolder, new String[]{"id"}) == 1) {
            //получить значение ключа добавленного продукта
            Integer generatedId = keyHolder.getKey().intValue();
            category.setId(generatedId);
            return true;
        }
        return false;
    }

    /*Метод updateRootCategory выполняет обновление корневой категории  в таблице БД categories.
    * Обновить можно только наименование категории.
    * @param    category - новые данные категории.
    * @return   true - категория обновлена, иначе - нет.
    * */
    private boolean updateRootCategory(Category category){
        String sql = "UPDATE categories SET name=:name WHERE id=:id";

        //формирование отображения именованных параметров
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", category.getName());
        parameters.addValue("id", category.getId());

        return namedParameterJdbcTemplate.update(sql, parameters) == 1;
    }

    /*Метод updateNotRootCategory выполняет обновление некорневой категории  в таблице БД categories.
    * @param    category - новые данные категории.
    * @return   true - категория обновлена, иначе - нет.
    * */
    private boolean updateNotRootCategory(Category category){

        System.out.println("updateNotRootCategory" + category);

        if (category.getNo_level() == 0) {
            throw new StoreException(errors.getErrorMessage("CHANGE_LEVEL_CATEGORY"));
        }
        String sql = "UPDATE categories SET name=:name, no_level=:no_level, id_category=:id_category WHERE id=:id";

        //формирование отображения именованных параметров
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", category.getName());
        parameters.addValue("no_level", category.getNo_level());
        parameters.addValue("id_category", category.getId_category());
        parameters.addValue("id", category.getId());

        return namedParameterJdbcTemplate.update(sql, parameters) == 1;
    }

    /*Метод update выполняет обновление категории в таблице БД categories.
    * @param    category - новые данные категории.
    * @return   true - категория обновлена, иначе - нет.
    * @throws   StoreException - категория не обновлена - произошла ошибка при выполнении запроса.
    * */
    @Override
    public boolean update(Category category) throws ValidationException  {
        boolean result = false;
        if (Optional.ofNullable(category).isPresent()) {
            category.validate(messages);
            try {
                //найти категорию с заданным id
                Category categoryInTable = getCategoryById(category.getId());

                //категория есть в таблице
                if (Optional.ofNullable(categoryInTable).isPresent()){

                    //обновление корневой категории
                    if (categoryInTable.getId_category() == 0) {
                        result = updateRootCategory(category);
                    }
                    //обновление не корневой категории
                    else {
                        result = updateNotRootCategory(category);
                    }
                }
                else {
                    result = false;
                }
            }catch (DataAccessException e){
                logger.error(messages.getProperty("NOT_UPDATED_CATEGORY") + ": " + category, e);
                throw new StoreException(errors.getErrorMessage("NOT_UPDATED_CATEGORY"), e);
            }
        }
        return result;
    }

    /*Метод delete выполняет удаление категории из таблицы БД categories по id.
    * Если в БД есть товары данной категории, то категорию нельзя удалять.
    * @param    id  категории.
    * @return   true - категория удалена, иначе - нет.
    * @throws   StoreException - категория не удалена - произошла ошибка при выполнении запроса.
    * */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id=:id";
        try {
            return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id)) == 1;
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NOT_DELETED_CATEGORY") + ": " + id, e);
            throw new StoreException(errors.getErrorMessage("NOT_DELETED_CATEGORY"), e);
        }
    }

    /*Метод getCategoryById выполняет поиск категории в таблице БД categories по id.
    * @param    id  категории.
    * @return   категория Category с id, если категория есть в БД, иначе - null.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    * */
    @Override
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id=:id";
        try{
            return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id), new CategoryMapper());
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NO_CATEGORY") + ": " + id, e);
            throw new StoreException(errors.getErrorMessage("NO_CATEGORY"), e);
        }
    }

    /*Метод getCategoryList выполняет запрос на получение списка всех категорий
    * из таблицы categories (не рекурсивно).
    * @return пустой список, если таблица пуста, иначе список категорий.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    * */
    @Override
    public List<Category> getCategoryList() {
        String sql = "SELECT * FROM categories ORDER BY no_level ASC";
        try {
            return namedParameterJdbcTemplate.query(sql, new CategoryMapper());
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NO_CATEGORIES"), e);
            throw new StoreException(errors.getErrorMessage("NO_CATEGORIES"), e);
        }
    }

    @Override
    /*Метод getCategoryListByLevel выполняет запрос на получение списка категорий
    * заданного уровня no_level из таблицы categories.
    * @param    no_level  уровень вложенности категорий (0 - корень, 1 - категории, 2 - подкатегории и т.д.).
    * @return   пустой список - категорий заданного уровня нет в таблице, или список категорий.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    */
    public List<Category> getCategoryListByLevel(short no_level) {
        String sql = "SELECT * FROM categories WHERE no_level=:no_level ORDER BY name ASC";
        try {
            return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource("no_level", no_level), new CategoryMapper());
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NO_CATEGORIES_BY_LEVEL") + " : " + no_level, e);
            throw new StoreException(errors.getErrorMessage("NO_CATEGORIES_BY_LEVEL"),e);
        }
    }

    /*Метод getCategoryListById выполняет получение  списка категорий заданного уровня no_level
    * и заданной родительской категории id_category из таблицы categories.
    * @param    id_category  ключ родительской категории.
    * @param    no_level  уровень вложенности категорий (0 - корень, 1 - категории, 2 - подкатегории и т.д.).
    * @return   пустой список - категорий заданного уровня и заданной родительской категории нет в таблице,
    *           или список категорий.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    */
    public List<Category> getCategoryListById(int id_category, short no_level) {
        String sql;
        //формирование отображения именованных параметров
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("no_level", no_level);

        if(id_category == 0){
            sql = "SELECT * FROM categories WHERE no_level=:no_level AND id_category IS NULL ORDER BY name ASC";
        }
        else{
            sql = "SELECT * FROM categories WHERE no_level=:no_level AND id_category=:id_category ORDER BY name ASC";
            parameters.addValue("id_category", id_category);
        }

        try {
            return namedParameterJdbcTemplate.query(sql, parameters, new CategoryMapper());
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NO_CATEGORIES_BY_LEVEL") + " : " + no_level + ", id = " + id_category, e);
            throw new StoreException(errors.getErrorMessage("NO_CATEGORIES_BY_LEVEL"),e);
        }
    }

    @Override
    /*Метод getCategoryFullNameListByLevel выполняет запрос на получение списка категорий
    * заданного уровня вложенности no_level из таблицы categories.
    * Категория содержит поле Path - полное название категории в дереве категорий.
    * @param    no_level  уровень вложенности категорий (0 - корень, 1 - категории, 2 - подкатегории и т.д.).
    * @return   пустой список - категорий заданного уровня нет в таблице,
    *           или список категорий.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    */
    public List<Category> getCategoryFullNameListByLevel(short no_level) {
        String sql = " WITH RECURSIVE res(id, id_category, name, no_level) AS ( " +
                "   SELECT t1.id, t1.id_category, t1.name, t1.no_level, CAST (t1.name AS VARCHAR (255)) as PATH" +
                "   FROM categories t1 " +
                "   WHERE t1.id_category IS NULL " +
                "       UNION " +
                "   SELECT t2.id, t2.id_category, t2.name, t2.no_level, CAST (res.PATH ||\'->\'|| t2.name AS VARCHAR(255)) " +
                "   FROM categories t2 " +
                "   INNER JOIN res ON (res.id = t2.id_category) )" +
                "SELECT * FROM res WHERE no_level=:no_level LIMIT 100";

        try {
            return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource("no_level", no_level), new CategoryFullNameMapper());
        }catch (DataAccessException e){
            logger.error(messages.getProperty("NO_CATEGORIES_BY_LEVEL") + " : " + no_level, e);
            throw new StoreException(errors.getErrorMessage("NO_CATEGORIES_BY_LEVEL"),e);
        }
    }

    /*Метод isRootCategory выполняет запрос для проверки есть ли в таблице categories корневая категория.
    * @return   true - в БД есть корневая категория, иначе - нет.
    * @throws   StoreException - произошла ошибка при выполнении запроса.
    */
    private boolean isRootCategory(){
        String sql = "SELECT * FROM categories WHERE id_category IS NULL";
        try {
            return namedParameterJdbcTemplate.query(sql, new CategoryMapper()).size() == 1;
        }catch (DataAccessException e){
            throw new StoreException(errors.getErrorMessage("NO_CATEGORY"),e);
        }
    }

    /*Класс CategoryMapper преобразовывает строку таблицы categories
    * в объект Category.
    * */
    private static class CategoryMapper implements RowMapper<Category>{
        @Override
        public Category mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Category(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getShort("no_level"),
                    resultSet.getInt("id_category")
            );
        }
    }

    /*Класс CategoryFullNameMapper преобразовывает строку таблицы categories
    * в объект Category, где имя категории - это полное название категории в дереве категорий.
    * */
    private static class CategoryFullNameMapper implements RowMapper<Category>{
        @Override
        public Category mapRow(ResultSet resultSet, int i) throws SQLException {
            return new Category(
                    resultSet.getInt("id"),
                    resultSet.getString("path"),
                    resultSet.getShort("no_level"),
                    resultSet.getInt("id_category")
            );
        }
    }
}
