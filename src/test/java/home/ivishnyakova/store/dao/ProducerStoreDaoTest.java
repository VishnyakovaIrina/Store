package home.ivishnyakova.store.dao;

import home.ivishnyakova.store.config.TestContextConfig;
import home.ivishnyakova.store.dao.storeDao.DatabaseScriptExecutor;
import home.ivishnyakova.store.entity.Producer;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.exceptions.ValidationException;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

/*Класс ProducerStoreDaoTest содержит тесты для проверки взаимодействия
* слоя доступа к данным о производителях товаров и базы данных.
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
public class ProducerStoreDaoTest {

    //для получения количества строк в таблицах
    @Autowired
    private JdbcTemplate jdbcTemplate;

    //для доступа к данным БД
    @Autowired
    private ProducerDao producerDao;

    //для выполнения sql-скриптов
    @Autowired
    private DatabaseScriptExecutor databaseScriptExecutor;

    //************************* Добавление производителя *************************

    /*Сценарий: добавление нового производителя. Наименование производителя - корректно.
    *           Такого производителя нет в базе данных.
    * Дано:
    *   - производитель
    * Результат: производитель добавлен.
    * */
    @Test
    public void insertProducerTest_whenCorrectProducer_thenOk() throws ValidationException{

        Producer producer = new Producer(0,"Apple");

        int countBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        producerDao.insert(producer);
        int countAfter = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");

        assertThat(producer.getId(), not(equalTo(0)));
        assertThat(countAfter - 1, equalTo(countBefore));
    }

    /*Сценарий: добавление нового производителя. Наименование производителя - корректно.
    *           Такой производитель уже есть в базе данных.
    * Дано:
    *   - производитель
    * Результат: производитель не добавлен и генерируется исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void insertProducerTest_whenProducerAlreadyExists_thenException() throws ValidationException{
        producerDao.insert(new Producer(0,"Asus"));
    }

    /*Сценарий: добавление нового производителя. Наименование производителя - null.
    *           Такого производителя нет в базе данных.
    * Дано:
    *   - производитель
    * Результат: производитель не добавлен и генерируется исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void insertProducerTest_whenNullProducer_thenException() throws ValidationException{
        producerDao.insert(null);
    }

    /*Сценарий: добавление нового производителя. Наименование производителя - "".
    *           Такого производителя нет в базе данных.
    * Дано:
    *   - производитель
    * Результат: производитель не добавлен и сгенерировано исключение ValidationException.
    * */
    @Test(expected = ValidationException.class)
    public void insertProducerTest_whenEmptyNameProducer_thenException() throws ValidationException{
        producerDao.insert(new Producer(0,""));
    }

    //************************* Обновление производителя *************************

    /*Сценарий: обновление данных производителя. Наименование производителя - корректно.
    *           Такой производитель есть в базе данных.
    * Дано:
    *   - производитель
    * Результат: инфо производителя обновлена (true).
    * */
    @Test
    public void updateProducerTest_whenCorrectProducer_thenOk() throws ValidationException{
        assertThat(producerDao.update(new Producer(1,"Apple")), equalTo(true));
    }

    /*Сценарий: обновление данных производителя. Наименование производителя - "".
    *           Такой производитель есть в базе данных.
    * Дано:
    *   - производитель
    * Результат: инфо производителя не обновлена и сгенерировано исключение ValidationException.
    * */
    @Test(expected = ValidationException.class)
    public void updateProducerTest_whenEmptyNameProducer_thenException() throws ValidationException{
        assertThat(producerDao.update(new Producer(1,"")), equalTo(true));
    }

    /*Сценарий: обновление данных производителя. Наименование производителя - null.
    *           Такой производитель есть в базе данных.
    * Дано:
    *   - производитель
    * Результат: инфо производителя не обновлена и сгенерировано исключение ValidationException.
    * */
    @Test(expected = ValidationException.class)
    public void updateProducerTest_whenNullNameProducer_thenException() throws ValidationException{
        assertThat(producerDao.update(new Producer(1,null)), equalTo(false));
    }

    /*Сценарий: обновление данных производителя. Наименование производителя - корректно.
    *           Таблица - пустая.
    * Дано:
    *   - производитель
    * Результат: инфо производителя не обновлено (false).
    * */
    @Test
    public void updateProducerTest_whenEmptyTable_thenNotUpdated()  throws ValidationException{
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        assertThat(producerDao.update(new Producer(1,"Apple")), equalTo(false));
    }

    /*Сценарий: обновление данных производителя. Наименование производителя - корректно.
    *           Такого производителя нет в базе данных.
    * Дано:
    *   - производитель
    * Результат:  инфо производителя не обновлено (false).
    * */
    @Test
    public void updateProducerTest_whenNoId_thenNotUpdated() throws ValidationException{
        assertThat(producerDao.update(new Producer(100,"Apple")), equalTo(false));
    }

    /*Сценарий: обновление данных производителя.
    * Дано:
    *   - производитель = null
    * Результат: инфо производителя не обновлено и сгенерировано исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void updateProducerTest_whenNullProducer_thenException() throws ValidationException{
        producerDao.update(null);
    }

    //************************* Удаление производителя *************************

    /*Сценарий: удаление производителя по его id.
    *       В базе данных нет товаров данного производителя.
    * Дано:
    *   - id производителя.
    * Результат: производитель удален.
    * */
    @Test
    public void deleteProducerTest_thenOk(){
        int countBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(producerDao.delete(11), equalTo(true));
        int countAfter = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(countAfter + 1,  equalTo(countBefore));
    }

    /*Сценарий: удаление производителя по его id.
    *       В базе данных есть товары данного производителя.
    * Дано:
    *   - id производителя.
    * Результат: производитель не удален и сгенерировано исключение StoreException.
    * */
    @Test(expected = StoreException.class)
    public void deleteProducerTest_whenExistsGoodsOfProducer_thenException(){
        assertThat(producerDao.delete(1), equalTo(false));
    }

    /*Сценарий: удаление производителя по его id.
    *       В базе данных нет данного производителя.
    * Дано:
    *   - id производителя.
    * Результат: производитель не удален (false).
    * */
    @Test
    public void deleteProducerTest_whenNotExistsId_thenNotDeleted(){
        int countBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(producerDao.delete(100), equalTo(false));
        int countAfter = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(countAfter,  equalTo(countBefore));
    }

    /*Сценарий: удаление производителя по его id.
    *       В базе данных нет данного производителя.
    * Дано:
    *   - id производителя (=0).
    * Результат: производитель не удален (false).
    * */
    @Test
    public void deleteProducerTest_whenZeroId_thenNotDeleted(){
        int countBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(producerDao.delete(0), equalTo(false));
        int countAfter = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(countAfter,  equalTo(countBefore));
    }

    /*Сценарий: удаление производителя по его id.
    *       В базе данных нет данного производителя.
    * Дано:
    *   - id производителя (<0).
    * Результат: производитель не удален (false).
    * */
    @Test
    public void deleteProducerTest_whenNegativeId_thenNotDeleted(){
        int countBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(producerDao.delete(-1), equalTo(false));
        int countAfter = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(countAfter,  equalTo(countBefore));
    }

    /*Сценарий: удаление производителя по его id.
    *       База данных - пустая.
    * Дано:
    *   - id производителя.
    * Результат: производитель не удален (false).
    * */
    @Test
    public void deleteProducerTest_EmptyTable_Empty(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        assertThat(producerDao.delete(1), equalTo(false));
    }

    //************************* Получить производителя по id *************************

    /*Сценарий: получение производителя по его id. Заданный производитель есть в БД.
    * Дано:
    *   - id производителя.
    * Результат: производитель с заданным id.
    * */
    @Test
    public void getProducerByIdTest_whenProducerExists_thenOk(){
        Producer producer = new Producer(1,"Samsung");
        assertThat(producerDao.getProducerById(1), equalTo(producer));
    }

    /*Сценарий: получение производителя по его id. Заданного производителя нет в БД.
    * Дано:
    *   - id производителя (=0).
    * Результат: сгенерировано исключение StoreException
    * */
    @Test(expected = StoreException.class)
    public void getProducerByIdTest_whenZeroId_thenException(){
        producerDao.getProducerById(0);
    }

    /*Сценарий: получение производителя по его id. Заданного производителя нет в БД.
    * Дано:
    *   - id производителя (<0).
    * Результат: сгенерировано исключение StoreException
    * */
    @Test(expected = StoreException.class)
    public void getProducerByIdTest_whenNegativeId_thenException(){
        producerDao.getProducerById(-1);
    }

    /*Сценарий: получение производителя по его id. БД - пустая.
    * Дано:
    *   - id производителя.
    * Результат: сгенерировано исключение StoreException
    * */
    @Test(expected = StoreException.class)
    public void getProducerByIdTest_whenEmptyTable_thenException(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        producerDao.getProducerById(1);
    }

    //************************* Получить список всех производителей *************************

    /*Сценарий: получение списка всех производителей.
    * Дано:  БД - не пустая.
    * Результат: список производителей
    * */
    @Test
    public void getProducerListTest_whenProducersExist_thenOk(){
        List<Producer> producers = producerDao.getProducerList();
        int count = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(producers.size(), equalTo(count));
    }

    /*Сценарий: получение списка всех производителей.
    * Дано:  БД - пустая.
    * Результат: пустой список производителей
    * */
    @Test
    public void getProducerListTest_whenEmptyTable_thenEmptyList(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        List<Producer> producers = producerDao.getProducerList();
        assertThat(producers.size(), equalTo(0));
    }

    /*Сценарий: получение списка всех производителей с сортировкой
    * наименований производителей по возрастанию.
    * Дано:  БД - не пустая.
    * Результат: отсортированный по возрастанию наименований список производителей
    * */
    @Test
    public void getProducerSortListTest_whenProducersExist_thenSortByAscOk(){
        List<Producer> producers = producerDao.getProducerSortList(true);
        int count = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(producers.size(), equalTo(count));

        List<Producer> producersSortList = producers.stream().sorted((o1, o2) -> {
            Comparator<String> compareString = Comparator.naturalOrder();
            return compareString.compare(o1.getName().toLowerCase(),o2.getName().toLowerCase());
        }).collect(Collectors.toList());

        assertThat(producers, equalTo(producersSortList));
    }

    /*Сценарий: получение списка всех производителей с сортировкой
    * наименований производителей по убыванию.
    * Дано:  БД - не пустая.
    * Результат: отсортированный по убыванию наименований список производителей
    * */
    @Test
    public void getProducerSortListTest_whenProducersExist_thenSortByDeskOk(){
        List<Producer> producers = producerDao.getProducerSortList(false);
        int count = JdbcTestUtils.countRowsInTable(jdbcTemplate,"producers");
        assertThat(producers.size(), equalTo(count));

        List<Producer> producersSortList = producers.stream().sorted((o1, o2) -> {
            Comparator<String> compareString = Comparator.naturalOrder();
            return compareString.reversed().compare(o1.getName().toLowerCase(),o2.getName().toLowerCase());
        }).collect(Collectors.toList());

        assertThat(producers, equalTo(producersSortList));
    }

    /*Сценарий: получение списка всех производителей с сортировкой
    * наименований производителей по возрастанию.
    * Дано:  БД -  пустая.
    * Результат: пустой список производителей
    * */
    @Test
    public void getProducerAscSortListTest_whenEmptyTables_thenEmptyList(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        List<Producer> producers = producerDao.getProducerSortList(true);
        assertThat(producers.size(), equalTo(0));
    }

    /*Сценарий: получение списка всех производителей с сортировкой
    * наименований производителей по убыванию.
    * Дано:  БД - пустая.
    * Результат: пустой список производителей
    * */
    @Test
    public void getProducerDescSortListTest_whenEmptyTables_thenEmptyList(){
        databaseScriptExecutor.executeScripts(CLEAR_TABLES.getScripts());
        List<Producer> producers = producerDao.getProducerSortList(false);
        assertThat(producers.size(), equalTo(0));
    }
}
