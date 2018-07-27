package home.ivishnyakova.store.dao;

import home.ivishnyakova.store.config.TestContextConfig;
import home.ivishnyakova.store.dao.storeDao.DatabaseScriptExecutor;
import home.ivishnyakova.store.entity.Category;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.exceptions.ValidationException;
import home.ivishnyakova.store.utils.CategoryLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static home.ivishnyakova.store.dao.SqlScripts.CLEAR_TABLES;
import static home.ivishnyakova.store.utils.CategoryLevel.ROOT;
import static home.ivishnyakova.store.utils.CategoryLevel.SUB_CATEGORY;
import static home.ivishnyakova.store.utils.CategoryLevel.CATEGORY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

/*Класс CategoryStoreDaoTest содержит тесты для проверки взаимодействия
* слоя доступа к данным о категориях товаров и базы данных.
*
* Автор: Вишнякова И.
* */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestContextConfig.class })
@SqlGroup({
        @Sql("/sql/drop-tables.sql"),
        @Sql("/sql/create-tables.sql"),
        @Sql("/sql/insert-data.sql")
})
public class CategoryStoreDaoTest {

    //для получения количества строк в таблицах
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //для доступа к данным БД
    @Autowired
    private CategoryDao categoryDao;

    //для выполнения sql-скриптов
    @Autowired
    private DatabaseScriptExecutor databaseScriptExecutor;

    //************************* Добавление категории *************************

    /*Сценарий: добавление новой категории. Наименование категории - корректно.
    *           Такой категории нет в базе данных.
    * Дано:
    *   - категория
    * Результат: категория добавлена.
    * */
    @Test
    public void insertCategoryTest_whenCorrectCategory_thenOk() throws ValidationException{

        Category category = new Category(0,"Apple", CATEGORY.getLevel(),1);

        int countBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate,"categories");
        assertThat(categoryDao.insert(category), equalTo(true));
        int countAfter = JdbcTestUtils.countRowsInTable(jdbcTemplate,"categories");

        assertThat(category.getId(), not(equalTo(0)));
        assertThat(countAfter - 1, equalTo(countBefore));
    }

    /*Сценарий: добавление новой категории. Наименование категории - корректно.
    *           Такая категория есть в базе данных.
    * Дано:
    *   - категория
    * Результат: категория не добавлена, исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void insertCategoryTest_whenCategoryExists_thenException() throws ValidationException{
        Category category = new Category(0,"Смартфоны и телефоны", CATEGORY.getLevel(),1);
        categoryDao.insert(category);
    }

    /*Сценарий: добавление категории.
    * Дано:
    *   - категория = null
    * Результат: категория не добавлена (false)
    * */
    @Test
    public void insertCategoryTest_whenNullCategory_thenFalse() throws ValidationException{
        assertThat(categoryDao.insert(null),equalTo(false));
    }

    /*Сценарий: добавление категории.
    * Дано:
    *   - категория, наименование которой = null
    * Результат: категория не добавлена, исключение StoreException.
    * */
    @Test(expected = ValidationException.class)
    public void insertCategoryTest_whenNullNameCategory_thenException() throws ValidationException{
        Category category = new Category(0,null, CATEGORY.getLevel(),1);
        categoryDao.insert(category);
    }

    /*Сценарий: добавление категории.
    * Дано:
    *   - категория, наименование которой = ""
    * Результат: категория не добавлена, исключение StoreException.
    * */
    @Test(expected = ValidationException.class)
    public void insertCategoryTest_whenEmptyNameCategory_thenFalse() throws ValidationException{
        Category category = new Category(0, "", CATEGORY.getLevel(),1);
        categoryDao.insert(category);
    }

    /*Сценарий: добавление корневой категории.
    *           Корневой категории нет в базе данных.
    * Дано:
    *   - категория
    * Результат: категория добавлена.
    * */
    @Test
    public void insertCategoryTest_whenCorrectRootCategoryEmptyTable_thenOk() throws ValidationException{
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        Category category = new Category(0,"Root", ROOT.getLevel(),0);

        int countBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate,"categories");
        assertThat(categoryDao.insert(category), equalTo(true));
        int countAfter = JdbcTestUtils.countRowsInTable(jdbcTemplate,"categories");

        assertThat(category.getId(), not(equalTo(0)));
        assertThat(countAfter - 1, equalTo(countBefore));
    }

    /*Сценарий: добавление корневой категории.
    *           В БД уже есть корневая категория.
    * Дано:
    *   - категория
    * Результат: категория не добавлена, исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void insertCategoryTest_whenCorrectRootCategory_thenException() throws ValidationException{
        Category category = new Category(0,"Root", ROOT.getLevel(),0);
        categoryDao.insert(category);
    }

    /*Сценарий: добавление категории без указания родительской категории.
    *           Такой категории нет в базе данных.
    * Дано:
    *   - категория
    * Результат: категория не добавлена, исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void insertCategoryTest_whenCorrectNoRootCategory_thenException() throws ValidationException{
        Category category = new Category(0,"Root", CATEGORY.getLevel(),0);
        categoryDao.insert(category);
    }

    //************************ Обновление категории *******************************
    /*Сценарий: обновление новой категории. Наименование категории - корректно.
    *           Такая категория есть в базе данных.
    * Дано:
    *   - категория
    * Результат: категория обновлена.
    * */
    @Test
    public void updateCategoryTest_whenCorrectCategory_thenOk() throws ValidationException{
        Category category = new Category(2,"Категория обновлена", CATEGORY.getLevel(),1);
        assertThat(categoryDao.update(category), equalTo(true));
    }

    /*Сценарий: обновление категории. Категории нет в БД
    * Дано:
    *   - категория
    * Результат: категория не обновлена (false)
    * */
    @Test(expected = StoreException.class)
    public void updateCategoryTest_whenCategoryExists_thenException() throws ValidationException{
        Category category = new Category(100,"Samsung", CATEGORY.getLevel(),1);
        categoryDao.update(category);
    }

    /*Сценарий: обновление категории.
    * Дано:
    *   - категория = null
    * Результат: категория не обновлена (false)
    * */
    @Test
    public void updateCategoryTest_whenNullCategory_thenFalse() throws ValidationException{
        assertThat(categoryDao.update(null),equalTo(false));
    }

    /*Сценарий: обновление категории.
    * Дано:
    *   - категория, наименование которой = null
    * Результат: категория не обновлена, исключение ValidationException.
    * */
    @Test(expected = ValidationException.class)
    public void updateCategoryTest_whenNullNameCategory_thenException() throws ValidationException{
        Category category = new Category(1,null, CATEGORY.getLevel(),1);
        categoryDao.update(category);
    }

    /*Сценарий: обновление категории.
    * Дано:
    *   - категория, наименование которой = ""
    * Результат: категория не добавлена, исключение ValidationException.
    * */
    @Test(expected = ValidationException.class)
    public void updateCategoryTest_whenEmptyNameCategory_thenFalse() throws ValidationException{
        Category category = new Category(1, "", CATEGORY.getLevel(),1);
        categoryDao.update(category);
    }

    /*Сценарий: обновление корневой категории.
    *           Корневой категории нет в базе данных.
    * Дано:
    *   - категория
    * Результат: категория не обновлена.
    * */
    @Test(expected = StoreException.class)
    public void updateCategoryTest_whenCorrectRootCategoryEmptyTable_thenOk() throws ValidationException{
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        Category category = new Category(0,"Root", ROOT.getLevel(),0);
        categoryDao.update(category);
    }

    /*Сценарий: обновление корневой категории.
    *           В БД уже есть корневая категория.
    * Дано:
    *   - категория
    * Результат: категория обновлена.
    * */
    @Test
    public void updateCategoryTest_whenCorrectRootCategory_thenException() throws ValidationException{
        Category category = new Category(1,"Root", ROOT.getLevel(),0);
        assertThat(categoryDao.update(category), equalTo(true));
    }

    /*Сценарий: обновление категории без указания родительской категории.
    *           Категория есть в базе данных.
    * Дано:
    *   - категория
    * Результат: категория не обновлена, исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void updateCategoryTest_whenCorrectNoRootCategory_thenException() throws ValidationException{
        Category category = new Category(2,"Root", CATEGORY.getLevel(),0);
        categoryDao.update(category);
    }

    //**************** Удаление категории ****************************************

    /*Сценарий: удаление категории по id.
    *           Категория есть в базе данных. Товаров данной категории нет в БД.
    * Дано:
    *   - id
    * Результат: категория удалена.
    * */
    @Test
    public void deleteCategoryTest_whenCategoryExists_thenOk(){
        categoryDao.delete(26);
    }

    /*Сценарий: удаление категории по id.
    *           Категория есть в базе данных. В БД есть товары с заданной категорией.
    * Дано:
    *   - id
    * Результат: категория не удалена, исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void deleteCategoryTest_whenGoodsWithCategoryExists_thenException(){
        categoryDao.delete(2);
    }

    /*Сценарий: удаление категории по id.
    *           Категории нет в базе данных.
    * Дано:
    *   - id
    * Результат: категория не найдена (false).
    * */
    @Test
    public void deleteCategoryTest_whenCategoryNotExists_thenException(){
        assertThat(categoryDao.delete(200),equalTo(false));
    }

    /*Сценарий: удаление категории по id.
    *           База данных пустая.
    * Дано:
    *   - id
    * Результат: категория не найдена (false).
    * */
    @Test
    public void deleteCategoryTest_whenEmptyTables_thenException(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        assertThat(categoryDao.delete(2), equalTo(false));
    }

    //************  Получение категории по id  **************************************

    /*Сценарий: получение категории по id.
    *           База данных - не пустая. Категория есть в БД.
    * Дано:
    *   - id
    * Результат: категория найдена.
    * */
    @Test
    public void getCategoryByIdTest_whenCategoryExists_thenOk(){

        Category actualCategory = new Category(2,"Бытовая техника", CategoryLevel.CATEGORY.getLevel(),1);
        Category expectedCategory = categoryDao.getCategoryById(actualCategory.getId());

        assertThat(actualCategory, equalTo(expectedCategory));
    }

    /*Сценарий: получение категории по id.
    *           База данных - не пустая. В БД нет такой категории.
    * Дано:
    *   - id
    * Результат: категория не найдена, исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void getCategoryByIdTest_whenCategoryNotExists_thenException(){
        Category actualCategory = new Category(200,"Бытовая техника", CategoryLevel.CATEGORY.getLevel(),1);
        categoryDao.getCategoryById(actualCategory.getId());
    }

    /*Сценарий: получение категории по id.
    *           База данных - пустая.
    * Дано:
    *   - id
    * Результат: категория не найдена, исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void getCategoryByIdTest_whenEmptyTables_thenException(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        Category actualCategory = new Category(2,"Бытовая техника", CategoryLevel.CATEGORY.getLevel(),1);
        categoryDao.getCategoryById(actualCategory.getId());
    }

    //************ Получение списка категорий, ***************************************
    // ******* отсортированных по возрастанию уровней вложенности ********************

    /*Сценарий: получение списка категорий, отсортированных по возрастанию
   *            уровней вложенности.
   *           База данных - не пустая.
   * Результат: список категорий.
   * */
    @Test
    public void getCategoryListTest_thenOk(){

        List<Category> categories = categoryDao.getCategoryList();
        int count = JdbcTestUtils.countRowsInTable(jdbcTemplate,"categories");
        assertThat(categories.size(), equalTo(count));

        List<Category> categoriesSortList = categories.stream().sorted((o1, o2) -> {
            Comparator<Short> compareString = Comparator.naturalOrder();
            return compareString.compare(o1.getNo_level(),o2.getNo_level());
        }).collect(Collectors.toList());

        assertThat(categories, equalTo(categoriesSortList));
    }

    /*Сценарий: получение списка категорий, отсортированных по возрастанию
   *            уровней вложенности.
   *           База данных - пустая.
   * Результат: пустой список категорий.
   * */
    @Test
    public void getCategoryListTest_whenEmptyTables_thenEmptyList(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        List<Category> categories = categoryDao.getCategoryList();
        int count = JdbcTestUtils.countRowsInTable(jdbcTemplate,"categories");
        assertThat(categories.size(), equalTo(count));
    }

    //************ Получение списка категорий заданного уровня no_level, ***************
    //************ отсортированного по возрастанию названия категорий  *****************
    @Test
    public void getCategoryListByLevelTest_whenZeroLevel_thenOk(){
        List<Category> categories = categoryDao.getCategoryListByLevel(ROOT.getLevel());
        assertThat(categories.size(), equalTo(1));
    }

    @Test
    public void getCategoryListByLevelTest_whenCategoryLevel_thenOk(){

        List<Category> categories = categoryDao.getCategoryListByLevel(CATEGORY.getLevel());
        assertThat(categories.size(), not(equalTo(0)));

        List<Category> categoriesSortList = categories.stream().sorted((o1, o2) -> {
            Comparator<String> compareString = Comparator.naturalOrder();
            return compareString.compare(o1.getName(),o2.getName());
        }).collect(Collectors.toList());

        assertThat(categories, equalTo(categoriesSortList));
    }

    @Test
    public void getCategoryListByLevelTest_whenNoLevel_thenEmptyList(){
        List<Category> categories = categoryDao.getCategoryListByLevel((short)100);
        assertThat(categories.size(), equalTo(0));
    }

    @Test
    public void getCategoryListByLevelTest_whenNegativeLevel_thenEmptyList(){
        List<Category> categories = categoryDao.getCategoryListByLevel((short)-1);
        assertThat(categories.size(), equalTo(0));
    }

    @Test
    public void getCategoryListByLevelTest_whenEmptyTables_thenEmptyList(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        List<Category> categories = categoryDao.getCategoryListByLevel(ROOT.getLevel());
        assertThat(categories.size(), equalTo(0));
    }

    //************ Получение списка категорий заданного уровня no_level, ***************
    //************ и заданной родительской категории  **********************************

    @Test
    public void getCategoryListByIdTest_whenZeroLevelRootCategory_thenOk(){
        List<Category> categories = categoryDao.getCategoryListById(0, ROOT.getLevel());
        assertThat(categories.size(), equalTo(1));
    }
    @Test
    public void getCategoryListByIdTest_whenCategoryLevel_thenOk(){
        List<Category> categories = categoryDao.getCategoryListById(1, CATEGORY.getLevel());
        assertThat(categories.size(), greaterThanOrEqualTo(0));
    }
    @Test
    public void getCategoryListByIdTest_whenPositiveLevelRootCategory_thenEmptyList(){
        List<Category> categories = categoryDao.getCategoryListById(5, ROOT.getLevel());
        assertThat(categories.size(), equalTo(0));
    }
    @Test
    public void getCategoryListByIdTest_whenZeroLevel_thenEmptyList(){
        List<Category> categories = categoryDao.getCategoryListById(1, CATEGORY.getLevel());
        assertThat(categories.size(), not(equalTo(0)));
    }
    @Test
    public void getCategoryListByIdTest_whenNoLevel_thenEmptyList(){
        List<Category> categories = categoryDao.getCategoryListById(100, CATEGORY.getLevel());
        assertThat(categories.size(), equalTo(0));
    }

    //************ Получение списка категорий заданного уровня вложенности no_level ***
    //************************** с полным именем категорий  ***************************

    @Test
    public void getCategoryFullNameListByLevelTest_whenRootCategory_thenOk(){
        List<Category> categories = categoryDao.getCategoryFullNameListByLevel(ROOT.getLevel());
        assertThat(categories.size(), equalTo(1));
    }

    @Test
    public void getCategoryFullNameListByLevelTest_whenCategory_thenOk(){
        List<Category> categories = categoryDao.getCategoryFullNameListByLevel(CATEGORY.getLevel());
        List<Category> categoriesByLevel = categoryDao.getCategoryListByLevel(CATEGORY.getLevel());
        assertThat(categories.size(), greaterThanOrEqualTo(0));
        assertThat(categories.size(), equalTo(categoriesByLevel.size()));
    }

    @Test
    public void getCategoryFullNameListByLevelTest_whenSubCategory_thenOk(){
        List<Category> categories = categoryDao.getCategoryFullNameListByLevel(SUB_CATEGORY.getLevel());
        List<Category> categoriesByLevel = categoryDao.getCategoryListByLevel(SUB_CATEGORY.getLevel());
        assertThat(categories.size(), greaterThanOrEqualTo(0));
        assertThat(categories.size(), equalTo(categoriesByLevel.size()));
    }

    @Test
    public void getCategoryFullNameListByLevelTest_whenNotCategory_thenEmptyList(){
        List<Category> categories = categoryDao.getCategoryFullNameListByLevel((short)5);
        assertThat(categories.size(), equalTo(0));
    }

    @Test
    public void getCategoryFullNameListByLevelTest_whenEmptyTable_thenEmptyList(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        List<Category> categories = categoryDao.getCategoryFullNameListByLevel(SUB_CATEGORY.getLevel());
        assertThat(categories.size(), equalTo(0));
    }
}
