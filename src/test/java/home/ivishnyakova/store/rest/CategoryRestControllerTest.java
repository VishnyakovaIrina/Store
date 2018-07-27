package home.ivishnyakova.store.rest;

import home.ivishnyakova.store.config.TestContextConfig;
import home.ivishnyakova.store.dao.CategoryDao;
import home.ivishnyakova.store.dao.storeDao.DatabaseScriptExecutor;
import home.ivishnyakova.store.entity.Category;
import home.ivishnyakova.store.utils.CategoryLevel;
import io.restassured.RestAssured;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static home.ivishnyakova.store.dao.SqlScripts.CLEAR_TABLES;
import static home.ivishnyakova.store.utils.CategoryLevel.ROOT;
import static home.ivishnyakova.store.utils.CategoryLevel.CATEGORY;
import static home.ivishnyakova.store.utils.CategoryLevel.SUB_CATEGORY;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpStatus.*;

/*Класс CategoryRestControllerTest содержит тесты для проверки работы
* REST-контроллера CategoryRestController (с форматом JSON).
* Запуск тестов следует выполнять на запущенном локальном сервере (порт 8888).
*
*   Перед каждым тестом таблицы базы данных удаляются,
 *создаются и заполняются данными.
* После каждого теста таблицы удаляются.
*
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
public class CategoryRestControllerTest {

    //uri ресурсов
    private static final String BASE_URL ="http://localhost:8888/store";
    private static final String CATEGORIES ="/categories";
    private static final String CATEGORY ="/categories/category";
    private static final String CATEGORIES_ID ="/categories/{id}";
    private static final String SUBCATEGORIES_ID ="/categories/{id}/subcategories";


    //для доступа к данным базы данных
    @Autowired
    private CategoryDao categoryDao;

    //для выполнения sql-скриптов
    @Autowired
    private DatabaseScriptExecutor databaseScriptExecutor;

    @BeforeClass
    public static void setUp(){
        RestAssured.baseURI = BASE_URL;
    }

    @AfterClass
    public static void tearDown(){
        RestAssured.reset();
    }

    //******************  Получить категорию по id *****************************
    /*Сценарий: получение категории по его id.
    *           Категория с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category/{id}"
    *   Результат:
    *       -   категория c id=1;
    *       -   статус ответа = ОК
    * */
    @Test
    public void getCategoryByIdTest_givenJSONAcceptAndJSONContentTypeAndCorrectId_whenGet_thenOk(){
        int id = 1;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(OK.value())
        .assertThat()
            .log().body()
            .body("id", is(id));
    }

    /*Сценарий: получение категории по его id.
    *           Категории с таким id нет в таблице.
    *   Дано:
    *       -   id = 100
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category/{id}"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void getCategoryByIdTest_givenJSONAcceptAndJSONContentTypeAndNotId_whenGet_thenNotFound(){
        int id = 100;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    /*Сценарий: получение категории по его id.
    *           Категория с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Accept = не определено
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category/{id}"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void getCategoryByIdTest_givenNotJSONAcceptAndJSONContentTypeAndNotId_whenGet_thenNotAcceptable(){
        int id = 1;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: получение категории по его id.
    *           Категория с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = не определено
    *       -   url = "/categories/category/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getCategoryByIdTest_givenJSONAcceptAndNotJSONContentTypeAndNotId_whenGet_thenUnsupportedMediaType(){
        int id = 1;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: получение категории по его id.
    *           Категория с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Accept = не определено
    *       -   Content-Type = не определено
    *       -   url = "/categories/category/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getCategoryByIdTest_givenNotJSONAcceptAndNotJSONContentTypeAndNotId_whenGet_thenUnsupportedMediaType(){
        int id = 1;
        given()
            .log().all()
            .pathParam("id", id)
        .when()
            .get(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    //******************  Получить список всех категорий *****************************
    /*Сценарий: получение списка всех категорий.
    *           Таблица категориц - не пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories"
    *   Результат:
    *       -   список категорий
    *       -   статус ответа = ОК
    * */
    @Test
    public void getCategoriesTest_givenJSONAcceptAndJSONContentType_whenGet_thenOk(){
        //список категорий (полученный из БД)
        List<Category> expectedList = categoryDao.getCategoryList();

        //получение ответа
        Response response = given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(CATEGORIES);
        response.then()
            .log().ifError()
            .statusCode(OK.value());

        ResponseBody responseBody = response.getBody();
        JsonPath jsonPath = new JsonPath(responseBody.asString());

        //список категорий в ответе сервера
        List<Category> actualList = jsonPath.getList("", Category.class);
        assertThat(actualList.size(),equalTo(expectedList.size()));
        assertThat(actualList.toArray(), equalTo(expectedList.toArray()));
    }

    /*Сценарий: получение списка всех категорий.
    *           Таблица категорий - пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void getCategoriesTest_givenJSONAcceptAndJSONContentTypeEmptyTable_whenGet_thenNotFound(){
        //очистить таблицу категорий
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        //получение ответа
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(CATEGORIES)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    /*Сценарий: получение списка всех категорий.
    *           Таблица категорий - не пустая.
    *   Дано:
    *       -   Accept = не определено
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories"
    *   Результат:
    *       -   статус ответа = ОК
    * */
    @Test
    public void getCategoriesTest_givenNotJSONAcceptAndJSONContentType_whenGet_thenNotAcceptable(){
        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .when()
            .get(CATEGORIES)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: получение списка всех категорий.
    *           Таблица категорий - не пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = не определено
    *       -   url = "/categories"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getCategoriesTest_givenJSONAcceptAndNotJSONContentType_whenGet_thenUnsupportedMediaType(){

        //получение ответа
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(CATEGORIES)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());

    }
    /*Сценарий: получение списка всех категорий.
    *           Таблица категорий - не пустая.
    *   Дано:
    *       -   Accept = не определено
    *       -   Content-Type = не определено
    *       -   url = "/categories"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getCategoriesTest_givenNotJSONAcceptAndNotJSONContentType_whenGet_thenUnsupportedMediaType(){

        //получение ответа
        given()
            .log().all()
        .when()
            .get(CATEGORIES)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());

    }
    //******  Получить список всех подкатегорий указанной по id категории ***************

    /*Сценарий: получение списка подкатегорий по id категории.
    *           Категория с таким id есть в таблице и имеет подкатегории.
    *   Дано:
    *       -   id = 1
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category/{id}/subcategories"
    *   Результат:
    *       -   список подкатегорий;
    *       -   статус ответа = ОК
    * */
    @Test
    public void getSubCategoryByIdTest_givenJSONAcceptAndJSONContentTypeAndCorrectId_whenGet_thenOk(){
        //Некоторая категория уровня CATEGORY
        Category category = new Category(2,"Бытовая техника", CategoryLevel.CATEGORY.getLevel(),1);

        //список категорий (полученный из БД)
        List<Category> expectedList = categoryDao.getCategoryListById(category.getId(), CategoryLevel.SUB_CATEGORY.getLevel());

        //получение ответа
        Response response = given()
            .log().all()
            .pathParam("id", category.getId())
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(SUBCATEGORIES_ID);
        response.then()
            .log().ifError()
            .statusCode(OK.value());

        ResponseBody responseBody = response.getBody();
        JsonPath jsonPath = new JsonPath(responseBody.asString());

        //список категорий в ответе сервера
        List<Category> actualList = jsonPath.getList("", Category.class);
        assertThat(actualList.size(),equalTo(expectedList.size()));
        assertThat(actualList.toArray(), equalTo(expectedList.toArray()));
    }

    /*Сценарий: получение списка подкатегорий по id категории.
    *           Категория с таким id есть в таблице, но не имеет подкатегорий.
    *   Дано:
    *       -   id =  26
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category/{id}/subcategories"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void getSubCategoryByIdTest_givenJSONAcceptAndJSONContentTypeAndNoSubcategories_whenGet_thenNotFound() {
        //Некоторая категория уровня CATEGORY
        Category category = new Category(26, "Бытовая химия", CategoryLevel.CATEGORY.getLevel(), 1);
        given()
            .log().all()
            .pathParam("id", category.getId())
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(SUBCATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    /*Сценарий: получение списка подкатегорий по id категории.
    *           Таблица категорий пустая.
    *   Дано:
    *       -   id = 1
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category/{id}/subcategories"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void getSubCategoryByIdTest_givenJSONAcceptAndJSONContentTypeAndEmptyTable_whenGet_thenNotFound(){
        //очистить таблицы
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());

        //получение ответа
        given()
            .log().all()
            .pathParam("id", 1)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .when()
            .get(SUBCATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }
    
    //******************  Добавление новой категории *****************************

    /*Сценарий: добавление корневой категории.
    *           Корневой категории нет в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   в таблице будет создана корневая категория;
    *       -   статус ответа = ОК
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentTypeAndRootCategoryEmptyTable_whenPost_thenOk(){

        //очистить таблицы
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());

        //корневая категория
        Category category = new Category(1,"Корневая категория", ROOT.getLevel(),0);

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(category)
        .when()
            .post(CATEGORY)
        .then()
            .log().ifError()
            .statusCode(CREATED.value());
    }

    /*Сценарий: добавление корневой категории.
    *           Корневой категория уже есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentTypeAndExistsRootCategory_whenPost_thenBadRequest(){
        //корневая категория
        Category category = new Category(1,"Корневая категория", ROOT.getLevel(),0);

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(category)
        .when()
            .post(CATEGORY)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }

    /*Сценарий: добавление категории в пустую таблицу.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentTypeAndEmptyTable_whenPost_thenBadRequest(){

        //очистить таблицы
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());

        //корневая категория
        Category category = new Category(0,"Категория", CategoryLevel.CATEGORY.getLevel(),1);

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(category)
        .when()
            .post(CATEGORY)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }

    /*Сценарий: добавление категории в таблицу категорий, в которой есть
                корневая категория.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   в таблице будет создана категория;
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentType_whenPost_thenOk(){
        Category category = new Category(0,"Категория", CategoryLevel.CATEGORY.getLevel(),1);
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(category)
        .when()
            .post(CATEGORY)
        .then()
            .log().ifError()
            .statusCode(CREATED.value());
    }

    /*Выполнение запроса по добавлению категории category с некорректными данными.
    * От сервера ожидается ответ с кодом BAD_REQUEST.
    * */
    private void createInvalidCategory_thenBagRequest(Category category){
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(category)
        .when()
            .post(CATEGORY)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }

    /*Сценарий: добавление категории без названия (=null).
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentTypeAndEmptyName_whenPost_thenBadRequest(){
        Category category = new Category(0,"", CategoryLevel.CATEGORY.getLevel(),1);
        createInvalidCategory_thenBagRequest(category);
    }

    /*Сценарий: добавление категории без названия (=null).
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentTypeAndNullName_whenPost_thenBadRequest(){
        Category category = new Category(0,null, CategoryLevel.CATEGORY.getLevel(),1);
        createInvalidCategory_thenBagRequest(category);
    }

    /*Сценарий: добавление категории.
    *           Уровень категории вложенности < 0
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentTypeAndNegativeLevel_whenPost_thenBadRequest(){
        Category category = new Category(0,"Категория", (short)-5,1);
        createInvalidCategory_thenBagRequest(category);
    }

    /*Сценарий: добавление категории.
    *           Уровень категории вложенности = 0
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentTypeAndZeroLevel_whenPost_thenBadRequest(){
        Category category = new Category(0,"Категория", (short)0,1);
        createInvalidCategory_thenBagRequest(category);
    }

    /*Сценарий: добавление категории.
    *           id родительской категории - отрицательное.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/category"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createCategoryTest_givenJSONAcceptAndJSONContentTypeAndNegativeIdCategory_whenPost_thenBadRequest(){
        Category category = new Category(0,"Категория", (short)1,-1);
        createInvalidCategory_thenBagRequest(category);
    }

    //******************  Обновление  категории *****************************

    /*Сценарий: обновление категории (не корневой).
    *           Категория есть в таблице. Новые данные заданы корректно.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   в таблице будет обновлена категория;
    *       -   статус ответа = NOT_CONTENT
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentType_whenPut_thenNoContent(){

        Category category = new Category(2,"Обновленная категория", CategoryLevel.CATEGORY.getLevel(),1);

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", category.getId())
            .body(category)
        .when()
            .put(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(NO_CONTENT.value());
    }

    /*Сценарий: обновление категории (корневой).
    *           Категория есть в таблице. Обновить можно только имя категории.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   в таблице будет обновлена корневая категория;
    *       -   статус ответа = NOT_CONTENT
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndRootCategory_whenPut_thenNoContent(){

        Category category = new Category(1,"Обновленная корневая категория", ROOT.getLevel(),0);

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", category.getId())
            .body(category)
        .when()
            .put(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(NO_CONTENT.value());
    }

    /*Метод выполняет запрос на обновление категории category.
    * Категория содержит некорректные данные.
    * Ожидается, что сервер вернет ответ со статусом BAD_REQUEST
    * */
    private void updateInvalidCategory_thenBadRequest(Category category){
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", category.getId())
            .body(category)
        .when()
            .put(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }


    /*Сценарий: обновление корневой категории. Новое название категории - пустая строка.
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndEmptyNameRootCategory_whenPut_thenBadRequest(){
        Category category = new Category(1,"", ROOT.getLevel(),0);
        updateInvalidCategory_thenBadRequest(category);
    }

    /*Сценарий: обновление корневой категории. Новое название категории - null.
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndNullNameRootCategory_whenPut_thenBadRequest(){
        Category category = new Category(1,null, ROOT.getLevel(),0);
        updateInvalidCategory_thenBadRequest(category);
    }

    /*Сценарий: обновление не корневой категории. Новое название категории - пустая строка.
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndEmptyNameCategory_whenPut_thenBadRequest(){
        Category category = new Category(2,"", CategoryLevel.CATEGORY.getLevel(),1);
        updateInvalidCategory_thenBadRequest(category);
    }

    /*Сценарий: обновление не корневой категории. Новое название категории - null.
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndNullNameCategory_whenPut_thenBadRequest(){
        Category category = new Category(2,null, CategoryLevel.CATEGORY.getLevel(),1);
        updateInvalidCategory_thenBadRequest(category);
    }

    /*Сценарий: обновление категории.  Уровень категории - < 0.
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndNegativeLevelCategory_whenPut_thenBadRequest(){
        Category category = new Category(2,"Категория", (short)-5,1);
        updateInvalidCategory_thenBadRequest(category);
    }

    /*Сценарий: обновление категории.  Сделать категорию корневой,
    *           уровень категории = 0. Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndZeroLevelCategory_whenPut_thenBadRequest(){
        Category category = new Category(2,"Категория", ROOT.getLevel(),1);
        updateInvalidCategory_thenBadRequest(category);
    }

    /*Сценарий: обновление категории.  Уровень категории - > 5.
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndMore5LevelCategory_whenPut_thenBadRequest(){
        Category category = new Category(2,"Категория", (short)6,1);
        updateInvalidCategory_thenBadRequest(category);
    }

    /*Сценарий: обновление категории.  id родительской категории - <0.
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndNegativeIdCategory_whenPut_thenBadRequest(){
        Category category = new Category(2,"Категория", CategoryLevel.CATEGORY.getLevel(),-5);
        updateInvalidCategory_thenBadRequest(category);
    }

    /*Сценарий: обновление категории.  id родительской категории - >0 (нет такой категории).
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndNoIdCategory_whenPut_thenBadRequest(){
        Category category = new Category(2,"Категория", CategoryLevel.CATEGORY.getLevel(),100);
        updateInvalidCategory_thenBadRequest(category);
    }

   /*Сценарий: обновление категории.  id родительской категории = 0.
    *           Корневая категория есть в таблице.
    *           Категория есть в таблице.
    *   Дано:
    *       -   категория
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateCategoryTest_givenJSONAcceptAndJSONContentTypeAndZeroIdCategory_whenPut_thenBadRequest(){
        Category category = new Category(2,"Категория", CategoryLevel.CATEGORY.getLevel(),0);
        updateInvalidCategory_thenBadRequest(category);
    }

    //******************  Удаление  категории *****************************

    /*Метод выполняет запрос на удаление категории с id.
    * Ожидается ответ от сервера со статусом responseStatus.*/
    private void deleteCategory_thenResponseStatus(int id, HttpStatus responseStatus){
        given()
            .log().all()
            .pathParam("id", id)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .delete(CATEGORIES_ID)
        .then()
            .log().ifError()
            .statusCode(responseStatus.value());
    }

   /*Сценарий: удаление категории по  id.
    *          В БД нет товаров заданной категории.
    *   Дано:
    *       -   id категории
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/categories/{id}"
    *   Результат:
    *       -   статус ответа = NO_CONTENT
    * */
    @Test
    public void deleteCategoryByIdTest_givenJSONAcceptAndNotJSONContentType_whenDelete_thenNoContent(){
        deleteCategory_thenResponseStatus(26, NO_CONTENT);
    }

    /*Сценарий: удаление категории по id.
     *          В БД есть товары заданной категории.
     *   Дано:
     *       -   id категории
     *       -   Accept = "application/json;charset=UTF-8"
     *       -   Content-Type = "application/json;charset=UTF-8"
     *       -   url = "/categories/{id}"
     *   Результат:
     *       -   статус ответа = INTERNAL_SERVER_ERROR
     * */
    @Test
    public void deleteCategoryByIdTest_givenJSONAcceptAndNotJSONContentTypeIsGoodsWithCategory_whenDelete_thenInternalServerError(){
        deleteCategory_thenResponseStatus(1, INTERNAL_SERVER_ERROR);
    }

    /*Сценарий: удаление категории по id. Такой категории нет в таблице.
     *   Дано:
     *       -   id категории
     *       -   Accept = "application/json;charset=UTF-8"
     *       -   Content-Type = "application/json;charset=UTF-8"
     *       -   url = "/categories/{id}"
     *   Результат:
     *       -   статус ответа = INTERNAL_SERVER_ERROR
     * */
    @Test
    public void deleteCategoryByIdTest_givenJSONAcceptAndNotJSONContentTypeNoCategory_whenDelete_thenNotFound(){
        deleteCategory_thenResponseStatus(100, NOT_FOUND);
    }
}
