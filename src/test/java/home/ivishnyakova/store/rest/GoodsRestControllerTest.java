package home.ivishnyakova.store.rest;
import home.ivishnyakova.store.config.TestContextConfig;
import home.ivishnyakova.store.dao.GoodsDao;
import home.ivishnyakova.store.dao.SqlScripts;
import home.ivishnyakova.store.dao.storeDao.DatabaseScriptExecutor;
import home.ivishnyakova.store.entity.Goods;
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

import static home.ivishnyakova.store.dao.SqlScripts.CLEAR_GOODS_TABLE;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.http.HttpStatus.*;


/*Класс GoodsRestControllerTest содержит тесты для проверки работы
* REST-контроллера GoodsRestController (с форматом JSON).
* Запуск тестов следует выполнять на запущенном локальном сервере (порт 8888).
*
*   Перед каждым тестом таблицы базы данных удаляются,
 *создаются и заполняются данными.
* После каждого теста таблицы удаляются.
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
public class GoodsRestControllerTest {

    //uri ресурсов
    private static final String BASE_URL ="http://localhost:8888/store";
    private static final String PRODUCTS ="/goods";
    private static final String PRODUCT ="/goods/goods";
    private static final String PRODUCT_ID ="/goods/{id}";

    //для доступа к данным базы данных
    @Autowired
    private GoodsDao goodsDao;

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

    //*********************  Получение товара по id  **********************************

    //*********************************************************************************
    /*Сценарий: получение товара по его id.
    *           Товар с таким id есть в таблице.
    *   Дано:
    *       -   id = 10
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   товар c id=10;
    *       -   статус ответа = ОК
    * */
    @Test
    public void getGoodsByIdTest_givenJSONAcceptAndJSONContentTypeAndCorrectId_whenGet_thenOk(){
        int id = 10;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(OK.value())
        .assertThat()
            .body("id", is(id));
    }

    /*Сценарий: получение товара по его id.
    *           Товара с таким id нет в таблице.
    *   Дано:
    *       -   id = -1
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void getGoodsByIdTest_givenJSONAcceptAndJSONContentTypeAndNotCorrectId_whenGet_thenNotFound(){

        int id = -1;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    /*Сценарий: получение товара по его id.
    *           Товар с таким id есть в таблице.
    *   Дано:
    *       -   id = 10
    *       -   Accept = не определено
    *       -   Content-Type = не определено
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getGoodsByIdTest_givenNotJSONAndCorrectId_whenGet_thenUnsupportedMediaType(){
        int id = 10;
        given()
            .log().all()
            .pathParam("id", id)
        .when()
            .get(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: получение товара по его id.
    *           Товар с таким id есть в таблице.
    *   Дано:
    *       -   id = 10
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = не определено
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getGoodsByIdTest_givenNotJSONContentTypeAndJSONAcceptAndCorrectId_whenGet_thenUnsupportedMediaType(){
        int id = 1;
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", id)
        .when()
            .get(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: получение товара по его id.
    *           Товар с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = не определено
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void getGoodsByIdTest_givenJSONContentTypeAndNotJSONAcceptAndCorrectId_whenGet_thenNotAcceptable(){
        int id = 1;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    //*********************  Получение списка товаров  **********************************

    /*Сценарий: получение списка всех товаров.
    *           Таблица товаров - не пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods"
    *   Результат:
    *       -   список товаров
    *       -   статус ответа = ОК
    * */
    @Test
    public void getGoodsTest_givenJSONAcceptAndJSONContentType_whenGet_thenOk(){
        //список товаров (полученный из БД)
        List<Goods> expectedGoodsList = goodsDao.getGoodsList();

        //получение ответа
        Response response = given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCTS);
        response.then()
            .log().ifError()
            .statusCode(OK.value());

        ResponseBody responseBody = response.getBody();
        JsonPath jsonPath = new JsonPath(responseBody.asString());

        //список товаров в ответе сервера
        List<Goods> actualGoodsList = jsonPath.getList("", Goods.class);

        assertThat(actualGoodsList.size(),equalTo(expectedGoodsList.size()));
        assertThat(actualGoodsList.toArray(), equalTo(expectedGoodsList.toArray()));
    }

    /*Сценарий: получение списка всех товаров.
    *           Таблица товаров - не пустая.
    *   Дано:
    *       -   Accept = не определено
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void getGoodsTest_givenNotJSONAcceptAndJSONContentType_whenGet_thenNotAcceptable(){
        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCTS)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: получение списка всех товаров.
    *           Таблица товаров - не пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = не определено
    *       -   url = "/goods"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getGoodsTest_givenJSONAcceptAndNotJSONContentType_whenGet_thenUnsupportedMediaType(){
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCTS)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: получение списка всех товаров.
    *           Таблица товаров - пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void getGoodsTest_givenJSONAcceptAndJSONContentType_whenCorrectUrlAndEmptyDbTable_thenFail(){

        //удалить данные из таблицы goods
        databaseScriptExecutor.executeScripts(CLEAR_GOODS_TABLE.getScripts());

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCTS)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    //*********************  Создание нового товара  **********************************

    /*Метод createGoods_givenCorrectGoods_thenCreated выполняет запрос на создание нового товара.
    * goods содержит корректные данные.
    * Ожидается, что сервер отвечает на запрос со статусом  CREATED.
    * */
    private void createGoods_givenCorrectGoods_thenCreated(Goods goods){

        Response response = given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(goods)
        .when()
            .post(PRODUCT);

        response.then()
            .log().ifError()
            .statusCode(CREATED.value());

        ResponseBody responseBody = response.getBody();
        JsonPath jsonPath = new JsonPath(responseBody.asString());
        Goods actualGoods = jsonPath.getObject("", Goods.class);

        assertThat(actualGoods.getId(),not(equalTo(0)));
        assertThat(actualGoods.getName(),equalTo(goods.getName()));
        assertThat(actualGoods.getDescription(),equalTo(goods.getDescription()));
        assertThat(actualGoods.getId_category(),equalTo(goods.getId_category()));
        assertThat(actualGoods.getId_producer(),equalTo(goods.getId_producer()));
        assertThat(actualGoods.getPrice(),equalTo(goods.getPrice()));
        assertThat(actualGoods.isIn_storage(),equalTo(goods.isIn_storage()));
    }

    /*Метод createGoods_givenInvalidGoods_thenBagRequest выполняет запрос на создание нового товара.
    * Однако goods содержит не корректные данные.
    * Ожидается, что сервер отвечает на запрос со статусом  responseStatus.
    * */
    private void createGoods_givenInvalidGoods_thenBagRequest(Goods goods, HttpStatus responseStatus){
        given()
                .log().all()
                .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(goods)
                .when()
                .post(PRODUCT)
                .then()
                .log().ifError()
                .statusCode(responseStatus.value());
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndGoods_whenPost_thenCreated(){
        createGoods_givenCorrectGoods_thenCreated(new Goods(0,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1));
    }

    /*Сценарий: Создание нового товара. Поля нового товара заданы корректно, кроме
    *           наименования товара = null.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndNullNameGoods_whenPost_thenBadRequest(){
         createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                null,
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Поля нового товара заданы корректно, кроме
    *           наименования товара = пустая строка.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndEmptyNameGoods_whenPost_thenBadRequest(){
        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Поля нового товара заданы корректно, кроме
    *           цены товара =  <0.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndNegativePriceGoods_whenPost_thenBadRequest(){
        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "Холодильник",
                -10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно.
    *           В таблице нет такого товара.
    *           Цена товара = 0
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndZeroPriceGoods_whenPost_thenCreated(){
        createGoods_givenCorrectGoods_thenCreated(new Goods(0,
                "Холодильник",
                0,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1));
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно.
    *           В таблице нет такого товара.
    *           Описание товара = пустая строка
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndEmptyDescriptionGoods_whenPost_thenCreated(){
        createGoods_givenCorrectGoods_thenCreated(new Goods(0,
                "Холодильник",
                5600,
                "",
                true,
                1,
                1));
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно.
    *           В таблице нет такого товара.
    *           Описание товара = null
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndNullDescriptionGoods_whenPost_thenCreated(){
        createGoods_givenCorrectGoods_thenCreated(new Goods(0,
                "Холодильник",
                5600,
                null,
                true,
                1,
                1));
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно.
    *           В таблице нет такого товара.
    *           Товар - не на складе (in_storage = false)
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndFalseInStorageGoods_whenPost_thenCreated(){
        createGoods_givenCorrectGoods_thenCreated(new Goods(0,
                "Холодильник",
                5600,
                "Описание холодильника",
                false,
                1,
                1));
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно, кроме
    *           id_category = 0.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndZeroIdCategoryGoods_whenPost_thenBadRequest(){
        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                0,
                1),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно, кроме
    *           id_category = 1000 (категории с таким id нет в базе данных).
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndNoIdCategoryGoods_whenPost_thenBadRequest(){
        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1000,
                1),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно, кроме
    *           id_category <0 (категории с таким id нет в базе данных).
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndNegativeIdCategoryGoods_whenPost_thenBadRequest(){
        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                -1000,
                1),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно, кроме
    *           id_producer <0 (производителя с таким id нет в базе данных).
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndNegativeIdProducerGoods_whenPost_thenBadRequest(){
        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                -100),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно, кроме
    *           id_producer=0.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndZeroIdProducerGoods_whenPost_thenBadRequest(){
        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                0),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно, кроме
    *           id_producer=100 (производителя с таким id нет в базе данных).
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndNoIdProducerGoods_whenPost_thenBadRequest(){
        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                100),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно, однако .
    *           в таблице уже есть такой товар. Сопоставляются имя, цена, id производителя и id категории.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndJSONContentTypeAndExistsGoods_whenPost_thenBadRequest(){

        createGoods_givenInvalidGoods_thenBagRequest(new Goods(0,
                "Стиральная машина Siemens SM-DS29573",
                9570,
                "",
                false,
                8,
                4),BAD_REQUEST);
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Accept = не определено
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void createGoodsTest_givenNotJSONAcceptAndJSONContentTypeAndGoods_whenPost_thenNotAcceptable(){

        Goods newGoods = new Goods(0, "Холодильник", 10000, "Холодильник класса А+, 2-х камерный, цвет - серебристый", true, 1,1);

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(newGoods)
        .when()
            .post(PRODUCT)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Content-Type = не определено
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void createGoodsTest_givenJSONAcceptAndNotJSONContentTypeAndGoods_whenPost_thenUnsupportedMediaType(){

        Goods newGoods = new Goods(0, "Холодильник", 10000, "Холодильник класса А+, 2-х камерный, цвет - серебристый", true, 1,1);

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(newGoods)
        .when()
            .post(PRODUCT)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: Создание нового товара. Все поля нового товара заданы корректно.
    *           В таблице нет такого товара.
    *   Дано:
    *       -   Content-Type = не определено
    *       -   Accept = не определено
    *       -   url = "/goods/goods"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void createGoodsTest_givenNotJSONAcceptAndNotJSONContentTypeAndGoods_whenPost_thenUnsupportedMediaType(){

        Goods newGoods = new Goods(0, "Холодильник", 10000, "Холодильник класса А+, 2-х камерный, цвет - серебристый", true, 1,1);

        given()
            .log().all()
            .body(newGoods)
        .when()
            .post(PRODUCT)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    //********************  Обновление товара по id  **********************************

    /*Метод updateGoods_expectedResponseStatus выполняет запрос на обновление товара.
    * id товара и новые значения товара хранятся в goods.
    * Всегда обновляются все поля (кроме id).
    * Ожидается, что на запрос сервер посылает ответ responseStatus.
    * */
    private void updateGoods_expectedResponseStatus(Goods goods, HttpStatus responseStatus){
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", goods.getId())
            .body(goods)
        .when()
            .put(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(responseStatus.value());
    }

    /*Сценарий: Обновление товара. Все поля товара заданы корректно и буду обновлены.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NO_CONTENT
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndGoods_whenPut_thenNoContent(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1), NO_CONTENT);
    }

    /*Сценарий: Обновление товара. Поля нового товара заданы корректно.
    *           В таблице нет товара с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndNoIdGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus( new Goods(1000,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1), NOT_FOUND);
    }

    /*Сценарий: Обновление товара. Поля нового товара заданы корректно, кроме
    *           наименования товара = null.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndNullNameGoods_whenPut_thenBadRequest(){

        updateGoods_expectedResponseStatus( new Goods(1,
                null,
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара. Поля нового товара заданы корректно, кроме
    *           наименования товара = пустая строка.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndEmptyNameGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus( new Goods(1,
                "",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара. Поля нового товара заданы корректно, кроме
    *           цены товара =  <0.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndNegativePriceGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus( new Goods(1,
                "Холодильник",
                -10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно.
    *           В таблице есть товар с заданным id.
    *           Цена товара = 0
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NO_CONTENT
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndZeroPriceGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                0,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1), NO_CONTENT);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно.
    *           В таблице есть товар с заданным id.
    *           Описание товара = пустая строка
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndEmptyDescriptionGoods_whenPut_thenNoContent(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "",
                true,
                1,
                1), NO_CONTENT);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно.
    *           В таблице есть товар с заданным id.
    *           Описание товара = null
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NO_CONTENT
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndNullDescriptionGoods_whenPut_thenNoContent(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Холодильник класса А+, 2-х камерный, цвет - серебристый",
                true,
                1,
                1), NO_CONTENT);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно.
    *           В таблице есть товар с заданным id.
    *           Товар - не на складе (in_storage = false)
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NO_CONTENT
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndFalseInStorageGoods_whenPut_thenNoContent(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Описание холодильника",
                false,
                1,
                1), NO_CONTENT);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно, кроме
    *           id_category = 0.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndZeroIdCategoryGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Описание холодильника",
                false,
                0,
                1), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара.. Все поля нового товара заданы корректно, кроме
    *           id_category = 1000 (категории с таким id нет в базе данных).
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAnNoIdCategoryGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Описание холодильника",
                false,
                1000,
                1), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно, кроме
    *           id_category <0 (категории с таким id нет в базе данных).
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndNegativeIdCategoryGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Описание холодильника",
                false,
                -1000,
                1), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно, кроме
    *           id_producer <0 (производителя с таким id нет в базе данных).
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndNegativeIdProducerGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Описание холодильника",
                false,
                1,
                -1), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно, кроме
    *           id_producer=0.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndZeroIdProducerGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Описание холодильника",
                false,
                1,
                0), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно, кроме
    *           id_producer=1000 (производителя с таким id нет в базе данных).
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndJSONContentTypeAndNoIdProducereGoods_whenPut_thenBadRequest(){
        updateGoods_expectedResponseStatus(new Goods(1,
                "Холодильник",
                10000,
                "Описание холодильника",
                false,
                1,
                1000), BAD_REQUEST);
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = не определено
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void updateGoodsTest_givenNotJSONAcceptAndJSONContentTypeAndGoods_whenPut_thenNotAcceptable(){

        Goods goods = new Goods(1, "Холодильник", 10000, "Холодильник класса А+, 2-х камерный, цвет - серебристый", true, 1,1);

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(goods)
            .pathParam("id",goods.getId())
        .when()
            .put(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Content-Type = не определено
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void updateGoodsTest_givenJSONAcceptAndNotJSONContentTypeAndGoods_whenPut_thenUnsupportedMediaType(){
        Goods goods = new Goods(1, "Холодильник", 10000, "Холодильник класса А+, 2-х камерный, цвет - серебристый", true, 1,1);

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(goods)
            .pathParam("id",goods.getId())
        .when()
            .put(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: Обновление товара. Все поля нового товара заданы корректно.
    *           В таблице есть товар с заданным id.
    *   Дано:
    *       -   Content-Type = не определено
    *       -   Accept = не определено
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void updateGoodsTest_givenNotJSONAcceptAndNotJSONContentTypeAndGoods_whenPut_thenUnsupportedMediaType(){
        Goods goods = new Goods(1, "Холодильник", 10000, "Холодильник класса А+, 2-х камерный, цвет - серебристый", true, 1,1);

        given()
            .log().all()
            .body(goods)
            .pathParam("id",goods.getId())
        .when()
            .put(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    //********************  Удаление товара по id  **********************************

    /*Сценарий: Удаление товара. В таблице есть товар с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NO_CONTENT
    * */
    @Test
    public void deleteGoodsTest_givenJSONAcceptAndJSONIdGoods_whenDelete_thenNoContent(){
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", 2)
        .when()
            .delete(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(NO_CONTENT.value());
    }

    /*Сценарий: Удаление товара. В таблице нет товара с заданным id.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void deleteGoodsTest_givenJSONAcceptAndJSONNoIdGoods_whenDelete_thenNotFound(){
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", 200)
        .when()
            .delete(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    /*Сценарий: Удаление товара. В таблице есть товар с заданным id.
    *   Дано:
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = не определено
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void deleteGoodsTest_givenNotJSONAcceptAndJSONNoIdGoods_whenDelete_thenNotAcceptable(){
        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", 200)
        .when()
            .delete(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: Удаление товара. В таблице есть товар с заданным id.
    *   Дано:
    *       -   Content-Type = не определено
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void deleteGoodsTest_givenJSONAcceptAndNotJSONNoIdGoods_whenDelete_thenUnsupportedMediaType(){
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", 200)
        .when()
            .delete(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: Удаление товара. В таблице есть товар с заданным id.
    *   Дано:
    *       -   Content-Type = не определено
    *       -   Accept = не определено
    *       -   url = "/goods/goods/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void deleteGoodsTest_givenNotJSONAcceptAndNotJSONNoIdGoods_whenDelete_thenUnsupportedMediaType(){
        given()
            .log().all()
            .pathParam("id", 200)
        .when()
            .delete(PRODUCT_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

}
