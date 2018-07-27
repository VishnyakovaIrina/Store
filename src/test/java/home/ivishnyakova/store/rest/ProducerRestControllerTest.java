package home.ivishnyakova.store.rest;

import home.ivishnyakova.store.config.TestContextConfig;
import home.ivishnyakova.store.dao.ProducerDao;
import home.ivishnyakova.store.dao.storeDao.DatabaseScriptExecutor;
import io.restassured.RestAssured;

import home.ivishnyakova.store.entity.Producer;
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
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpStatus.*;


/*Класс ProducerRestControllerTest содержит тесты для проверки работы
* REST-контроллера ProducerRestController (с форматом JSON).
* Запуск тестов следует выполнять на запущенном локальном сервере (порт 8888).
*
*   Перед каждым тестом таблицы базы данных удаляются и,
 *создается и заполняется данными таблица producers.
* После каждого теста таблица producers удаляется.
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
public class ProducerRestControllerTest {

    //uri ресурсов
    private static final String BASE_URL ="http://localhost:8888/store";
    private static final String PRODUCERS ="/producers";
    private static final String PRODUCER ="/producers/producer";
    private static final String PRODUCERS_ID ="/producers/{id}";

    //для доступа к данным базы данных
    @Autowired
    private ProducerDao producerDao;

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

    //******************  Получить производителя по id*****************************
    /*Сценарий: получение производителя по его id.
    *           Производитель с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   производитель c id=1;
    *       -   статус ответа = ОК
    * */
    @Test
    public void getProducerByIdTest_givenJSONAcceptAndJSONContentTypeAndCorrectId_whenGet_thenOk(){
        int id = 1;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(OK.value())
        .assertThat()
            .body("id", is(id));
    }

    /*Сценарий: получение производителя по его id.
    *           Производителя с таким id нет в таблице.
    *   Дано:
    *       -   id = -1
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void getProducerByIdTest_givenJSONAcceptAndJSONContentTypeAndNotCorrectId_whenGet_thenNotFound(){

        int id = -1;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    /*Сценарий: получение производителя по его id.
    *           Производитель с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Accept = не определено
    *       -   Content-Type = не определено
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getProducerByIdTest_givenNotJSONAndCorrectId_whenGet_thenUnsupportedMediaType(){
        int id = 1;
        given()
            .log().all()
            .pathParam("id", id)
        .when()
            .get(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: получение производителя по его id.
    *           Производитель с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = не определено
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getProducerByIdTest_givenNotJSONContentTypeAndJSONAcceptAndCorrectId_whenGet_thenUnsupportedMediaType(){
        int id = 1;
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", id)
        .when()
            .get(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: получение производителя по его id.
    *           Производитель с таким id есть в таблице.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = не определено
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void getProducerByIdTest_givenJSONContentTypeAndNotJSONAcceptAndCorrectId_whenGet_thenNotAcceptable(){
        int id = 1;
        given()
            .log().all()
            .pathParam("id", id)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    //******************  Получить список всех производителей *********************
    /*Сценарий: получение списка всех производителей.
    *           Таблица производителей - не пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers"
    *   Результат:
    *       -   список производителей
    *       -   статус ответа = ОК
    * */
    @Test
    public void getProducersTest_givenJSONAcceptAndJSONContentType_whenGet_thenOk(){
        //список производителей (полученный из БД)
        List<Producer> expectedProducersList = producerDao.getProducerList();

        //получение ответа
        Response response = given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCERS);
        response.then()
            .log().ifError()
            .statusCode(OK.value());

        ResponseBody responseBody = response.getBody();
        JsonPath jsonPath = new JsonPath(responseBody.asString());

        //список производителей в ответе сервера
        List<Producer> actualProducersList = jsonPath.getList("", Producer.class);

        assertThat(actualProducersList.size(),equalTo(expectedProducersList.size()));
        assertThat(actualProducersList.toArray(), equalTo(expectedProducersList.toArray()));
    }

    /*Сценарий: получение списка всех производителей.
    *           Таблица производителей - не пустая.
    *   Дано:
    *       -   Accept = не определено
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void getProducersTest_givenNotJSONAcceptAndJSONContentType_whenGet_thenNotAcceptable(){
        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCERS)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: получение списка всех производителей.
    *           Таблица производителей - не пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = не определено
    *       -   url = "/producers"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void getProducersTest_givenJSONAcceptAndNotJSONContentType_whenGet_thenUnsupportedMediaType(){
        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCERS)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: получение списка всех производителей.
    *           Таблица производителей - пустая.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void getProducersTest_givenJSONAcceptAndJSONContentType_whenCorrectUrlAndEmptyDbTable_thenFail(){

        //удалить данные из таблицы producer
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        .when()
            .get(PRODUCERS)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    //*****************  Создание нового производителя  *************************
    /*Сценарий: Создание нового производителя.
    *           В таблице нет такого производителя.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createProducerTest_givenJSONAcceptAndJSONContentTypeAndProducer_whenPost_thenCreated(){

        Producer newProducer = new Producer(0,"Apple");

        Response response = given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(newProducer)
        .when()
            .post(PRODUCER);

        response.then()
                .log().ifError()
                .statusCode(CREATED.value());

        ResponseBody responseBody = response.getBody();
        JsonPath jsonPath = new JsonPath(responseBody.asString());
        Producer actualProducer = jsonPath.getObject("", Producer.class);

        assertThat(actualProducer.getName(),equalTo(newProducer.getName()));
    }

    /*Сценарий: Создание нового производителя.
    *           В таблице есть производитель с таким именем.
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createProducerTest_givenJSONAcceptAndJSONContentTypeAndProducerExists_whenPost_thenBadRequest(){

        Producer newProducer = new Producer(0,"Samsung");

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(newProducer)
        .when()
            .post(PRODUCER)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }

    /*Сценарий: Создание нового производителя.
    *           В таблице нет такого производителя.
    *           Название производителя = null
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createProducerTest_givenJSONAcceptAndJSONContentTypeAndNullNameProducer_whenPost_thenBadRequest(){

        Producer newProducer = new Producer(0,null);

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(newProducer)
        .when()
            .post(PRODUCER)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }

    /*Сценарий: Создание нового производителя.
    *           В таблице нет такого производителя.
    *           Название производителя = пустая строка
    *   Дано:
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void createProducerTest_givenJSONAcceptAndJSONContentTypeAndEmptyProducerName_whenPost_thenBadRequest(){

        Producer newProducer =  new Producer(0,"");

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(newProducer)
        .when()
            .post(PRODUCER)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }

    /*Сценарий: Создание нового производителя.
    *           В таблице нет такого производителя.
    *   Дано:
    *       -   Accept = не определено
    *       -   Content-Type = не определено
    *       -   url = "/producers/producer"
    *   Результат:
    *       -   статус ответа = CREATED
    * */
    @Test
    public void createProducerTest_givenNotJSONAcceptAndNotJSONContentTypeAndProducer_whenPost_thenUnsupportedMediaType(){

        Producer newProducer = new Producer(0,"Apple");

        given()
            .log().all()
            .body(newProducer)
        .when()
            .post(PRODUCER)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: Создание нового производителя.
    *           В таблице нет такого производителя.
    *   Дано:
    *       -   Accept = не определено
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void createProducerTest_givenJSONAcceptAndNotJSONContentTypeAndProducer_whenPost_thenNotAcceptable(){

        Producer newProducer = new Producer(0,"Apple");

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(newProducer)
        .when()
            .post(PRODUCER)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: Создание нового производителя.
    *           В таблице нет такого производителя.
    *   Дано:
    *       -   Content-Type = не определено
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void createProducerTest_givenNotJSONAcceptAndJSONContentTypeAndProducer_whenPost_thenUnsupportedMediaType(){

        Producer newProducer = new Producer(0,"Apple");

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(newProducer)
        .when()
            .post(PRODUCER)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    //******************  Обновление производителя  *******************************
    /*Сценарий: Обновление названия производителя.
    *           В таблице есть производитель с заданным id.
    *           Новое название производителя - не пустое.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = NO_CONTENT (обновлено)
    * */
    @Test
    public void updateProducerTest_givenJSONAcceptAndJSONContentTypeAndProducer_whenPut_thenNoContent(){

        Producer producer = new Producer(1,"Apple");

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(producer)
            .pathParam("id", producer.getId())
        .when()
            .put(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(NO_CONTENT.value());
    }

    /*Сценарий: Обновление названия производителя.
    *           В таблице нет производителя с заданным id.
    *           Новое название производителя - не пустое.
    *   Дано:
    *       -   id = -1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void updateProducerTest_givenJSONAcceptAndJSONContentTypeAndNotIdProducer_whenPut_thenNotFound(){

        Producer producer = new Producer(-1,"Apple");

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(producer)
            .pathParam("id", producer.getId())
        .when()
            .put(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    /*Сценарий: Обновление названия производителя.
    *           В таблице есть производитель с заданным id.
    *           Новое название производителя - null.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateProducerTest_givenJSONAcceptAndJSONContentTypeAndNullNameProducer_whenPut_thenBadRequest(){

        Producer producer = new Producer(1,null);

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(producer)
            .pathParam("id", producer.getId())
        .when()
            .put(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }

    /*Сценарий: Обновление названия производителя.
    *           В таблице есть производитель с заданным id.
    *           Новое название производителя - пустая строка.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = BAD_REQUEST
    * */
    @Test
    public void updateProducerTest_givenJSONAcceptAndJSONContentTypeAndEmptyNameProducer_whenPut_thenBadRequest(){

        Producer producer = new Producer(1,"");

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(producer)
            .pathParam("id", producer.getId())
        .when()
            .put(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(BAD_REQUEST.value());
    }

    /*Сценарий: Обновление названия производителя.
    *           В таблице есть производитель с заданным id.
    *           Новое название производителя - не пустая строка.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = не определено
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void updateProducerTest_givenNotJSONAcceptAndJSONContentTypeAndProducer_whenPut_thenNotAcceptable(){

        Producer producer = new Producer(1,"Apple");

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(producer)
            .pathParam("id", producer.getId())
        .when()
            .put(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }

    /*Сценарий: Обновление названия производителя.
    *           В таблице есть производитель с заданным id.
    *           Новое название производителя - не пустая строка.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = не определено
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void updateProducerTest_givenJSONAcceptAndNotJSONContentTypeAndProducer_whenPut_thenUnsupportedMediaType(){

        Producer producer = new Producer(1,"Apple");

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .body(producer)
            .pathParam("id", producer.getId())
        .when()
            .put(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    /*Сценарий: Обновление названия производителя.
    *           В таблице есть производитель с заданным id.
    *           Новое название производителя - не пустая строка.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = не определено
    *       -   Accept = не определено
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void updateProducerTest_givenNotJSONAcceptAndNotJSONContentTypeAndProducer_whenPut_thenUnsupportedMediaType(){

        Producer producer = new Producer(1,"Apple");

        given()
            .log().all()
            .body(producer)
            .pathParam("id", producer.getId())
        .when()
            .put(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }

    //******************  Удаление  производителя  *******************************
    /*Сценарий: Удаление производителя по его id.
    *           В таблице есть производитель с заданным id.
    *           В таблице товаров нет товаров этого производителя.
    *   Дано:
    *       -   id = 11
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = NO_CONTENT (удален)
    * */
    @Test
    public void deleteProducerTest_givenJSONAcceptAndJSONContentTypeAndIdFreeProducer_whenDelete_thenNoContent(){

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", 11)
        .when()
            .delete(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(NO_CONTENT.value());
    }
    /*Сценарий: Удаление производителя по его id.
    *           В таблице есть производитель с заданным id.
    *           В таблице товаров есть товары этого производителя.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = INTERNAL_SERVER_ERROR
    * */
    @Test
    public void deleteProducerTest_givenJSONAcceptAndJSONContentTypeAndIdProducer_whenDelete_thenInternalServerError(){

        given()
                .log().all()
                .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
                .pathParam("id", 1)
                .when()
                .delete(PRODUCERS_ID)
                .then()
                .log().ifError()
                .statusCode(INTERNAL_SERVER_ERROR.value());
    }

    /*Сценарий: Удаление производителя по его id.
    *           В таблице нет производителя с заданным id.
    *   Дано:
    *       -   id = -1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = NOT_FOUND
    * */
    @Test
    public void deleteProducerTest_givenJSONAcceptAndJSONContentTypeAndNotIdProducer_whenDelete_thenNoContent(){

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", -1)
        .when()
            .delete(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_FOUND.value());
    }

    /*Сценарий: Удаление производителя по его id.
    *           В таблице есть производитель с заданным id.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = "application/json;charset=UTF-8"
    *       -   Accept = не определено
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = NOT_ACCEPTABLE
    * */
    @Test
    public void deleteProducerTest_givenNotJSONAcceptAndJSONContentTypeAndIdProducer_whenDelete_thenNotAcceptable(){

        given()
            .log().all()
            .header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", 1)
        .when()
            .delete(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(NOT_ACCEPTABLE.value());
    }
    /*Сценарий: Удаление производителя по его id.
    *           В таблице есть производитель с заданным id.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = не определено
    *       -   Accept = "application/json;charset=UTF-8"
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void deleteProducerTest_givenJSONAcceptAndNotJSONContentTypeAndIdProducer_whenDelete_thenUnsupportedMediaType(){

        given()
            .log().all()
            .header("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE)
            .pathParam("id", 1)
        .when()
            .delete(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }
    /*Сценарий: Удаление производителя по его id.
    *           В таблице есть производитель с заданным id.
    *   Дано:
    *       -   id = 1
    *       -   Content-Type = не определено
    *       -   Accept = не определено
    *       -   url = "/producers/producer/{id}"
    *   Результат:
    *       -   статус ответа = UNSUPPORTED_MEDIA_TYPE
    * */
    @Test
    public void deleteProducerTest_givenNotJSONAcceptAndJSONContentTypeAndIdProducer_whenDelete_thenUnsupportedMediaType(){
        given()
            .log().all()
            .pathParam("id", 1)
        .when()
            .delete(PRODUCERS_ID)
        .then()
            .log().ifError()
            .statusCode(UNSUPPORTED_MEDIA_TYPE.value());
    }
}
