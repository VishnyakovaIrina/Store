package home.ivishnyakova.store.controller;

import home.ivishnyakova.store.dao.ProducerDao;
import home.ivishnyakova.store.dao.storeDao.ProducerStoreDao;
import home.ivishnyakova.store.entity.Producer;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.exceptions.ValidationException;
import home.ivishnyakova.store.message.ErrorMessage;
import home.ivishnyakova.store.message.ErrorProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

/*Класс ProducerControllerTest содержит юнит-тесты для контроллера ProducerController,
* который выполняет взаимодействие с моделью данных с помощью объекта типа ProducerDao и
* представлением, которое содержит информацию в объекте типа ModelMap.
*
* Автор: Вишнякова И.
* */
public class ProducerControllerTest {

    //название web-страниц (ответы контроллера)
    private static final String PAGE_PRODUCERS = "producers";
    private static final String PAGE_REDIRECT_PRODUCERS = "redirect:/getProducers/";

    //названия сообщений про ошибки
    private static final String NOT_VALID_NEW_PRODUCER_MSG      = "NOT_VALID_NEW_PRODUCER";
    private static final String NOT_INSERTED_PRODUCER_MSG       = "NOT_INSERTED_PRODUCER";
    private static final String NOT_VALID_UPDATED_PRODUCER_MSG  = "NOT_VALID_UPDATED_PRODUCER";
    private static final String NOT_UPDATED_PRODUCER_MSG        = "NOT_UPDATED_PRODUCER";
    private static final String NOT_DELETED_PRODUCER_MSG        = "NOT_DELETED_PRODUCER";
    private static final String NO_PRODUCER_MSG                 = "NO_PRODUCER";

    //источник данных
    private List<Producer> producersList = new ArrayList<>();
    //размер источника данных до выполнения теста
    private int sizeProducerListBefore;

    //описание ошибок
    private static ErrorMessage errorInvalidProducer;
    private static ErrorMessage errorNotInserted;
    private static ErrorMessage errorNotUpdated;
    private static ErrorMessage errorNotDeleted;
    private static ErrorMessage errorNoProducer;
    private static ErrorMessage errorInvalidUpdateProducer;

    @BeforeClass
    public static void init(){
        errorInvalidProducer   = new ErrorMessage();
        errorNotInserted       = new ErrorMessage();
        errorNotUpdated        = new ErrorMessage();
        errorNotDeleted        = new ErrorMessage();
        errorNoProducer        = new ErrorMessage();
        errorInvalidUpdateProducer  = new ErrorMessage();

        errorInvalidProducer.setMessage(NOT_VALID_NEW_PRODUCER_MSG);
        errorNotInserted.setMessage(NOT_INSERTED_PRODUCER_MSG);
        errorNotDeleted.setMessage(NOT_DELETED_PRODUCER_MSG);
        errorNotUpdated.setMessage(NOT_UPDATED_PRODUCER_MSG);
        errorNoProducer.setMessage(NO_PRODUCER_MSG);
        errorInvalidUpdateProducer.setMessage(NOT_VALID_UPDATED_PRODUCER_MSG);
    }

    @Before
    public void setUp(){
        //настройка данных для моделирования работы с БД
        producersList.add(new Producer(1,"HP"));
        producersList.add(new Producer(2,"Asus"));
        producersList.add(new Producer(3,"Samsung"));
        producersList.add(new Producer(4,"Lenovo"));
        producersList.add(new Producer(5,"Siemens"));

        sizeProducerListBefore = producersList.size();
    }

    @After
    public void tearDown(){
        producersList.clear();
    }


    /*Метод getProducerListTest моделирует получение списка производителей товаров.
    * Возвращает список производителей.
    * Если производителей нет, то возвращается пустой список.
    * */
    private void getProducerListTest(){
        //мок объекта доступа к данным БД
        ProducerDao producerDao = mock(ProducerStoreDao.class);
        when(producerDao.getProducerSortList(true)).thenReturn(producersList);

        //создание контроллера и внедрение в него бина producerDao
        ProducerController controller = new ProducerController();
        ReflectionTestUtils.setField(controller, "producerDao", producerDao);

        //получение списка производителей
        ModelMap modelMap = new ModelMap();
        String page = controller.getProducers(modelMap);
        List<Producer> producersListFromController = (List<Producer>)modelMap.get("producersList");

        assertThat(producersList, equalTo(producersListFromController));
        assertThat(page, equalTo(PAGE_PRODUCERS));
    }

    /*Сценарий: получение списка производителей. В БД есть инфо о производителях.
    * Результат: список производителей.
    * */
    @Test
    public void getProducerListTest_thenOK(){
        getProducerListTest();
    }

    /*Сценарий: получение списка производителей. В БД нет инфо о производителях.
    * Результат: пустой список производителей.
    * */
    @Test
    public void getProducerListTest_whenEmptyList_thenEmptyList(){
        producersList.clear();
        getProducerListTest();
    }

    /*Сценарий: добавление производителя, которого нет  в БД. Данные производителя - корректны.
    * Результат: новый производитель добавлен.
    * */
    @Test
    public void addNewProducerTest_thenOK() throws ValidationException{

        Producer producer = new Producer(0,"HP");

        //мок объекта доступа к данным БД
        ProducerDao mockProducerDao = mock(ProducerStoreDao.class);
        doAnswer((Answer<Void>) invocationOnMock ->{
                producersList.add(producer);
                return null;
        }).when(mockProducerDao).insert(producer);

        //создание контроллера и внедрение в него бина producerDao
        ProducerController controller = new ProducerController();
        ReflectionTestUtils.setField(controller, "producerDao", mockProducerDao);

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("producer", producer);
        String namePage = controller.addNewProducer(producer, modelMap);

        assertThat(namePage, equalTo(PAGE_REDIRECT_PRODUCERS));
        assertThat(modelMap.get("name"), equalTo(producer.getName()));
        assertThat(sizeProducerListBefore, equalTo(producersList.size() - 1));
    }

    /*Метод моделирует добавление нового производителя с неудачным завершением.
    * @param    producer - производитель (либо null, либо содержит некорректные данные)
    * @param    errorMessage - описание ошибки.
    * @throws   ValidationException - производитель содержит некорректные данные.
    * Если производитель не добавлен, то метод генерирует исключение типа StoreException.
    * */
    private void addNewProducerHelperWithException(Producer producer, ErrorMessage errorMessage) throws ValidationException{

        //мок сообщений об ошибке
        ErrorProperties mockErrors = mock(ErrorProperties.class);

        when(mockErrors.getErrorMessage(NOT_VALID_NEW_PRODUCER_MSG)).thenReturn(errorInvalidProducer);
        when(mockErrors.getErrorMessage(NOT_INSERTED_PRODUCER_MSG)).thenReturn(errorNotInserted);

        //мок объекта доступа к данным БД
        ProducerDao mockProducerDao = mock(ProducerStoreDao.class);
        doThrow(new StoreException(errorMessage))
                .when(mockProducerDao).insert(producer);

        //создание контроллера и внедрение в него бина producerDao
        ProducerController controller = new ProducerController();
        ReflectionTestUtils.setField(controller, "producerDao", mockProducerDao);
        ReflectionTestUtils.setField(controller, "errors", mockErrors);

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("producer", producer);
        controller.addNewProducer(producer, modelMap);

    }

    /*Сценарий: добавление производителя, который уже есть в БД.
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void addNewProducerTest_whenProducerExists_thenException() throws ValidationException{
        Producer producer = new Producer(0,"Samsung");
        addNewProducerHelperWithException(producer, errorNotInserted);
    }

    /*Сценарий: добавление производителя = null.
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void addNewProducerTest_whenNullProducer_thenException() throws ValidationException{
        addNewProducerHelperWithException(null, errorInvalidProducer);
    }

    /*Сценарий: добавление производителя, которого нет в БД.
                Наименование производителя = null.
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void addNewProducerTest_whenNullNameProducer_thenException() throws ValidationException{
        Producer producer = new Producer(0, null);
        addNewProducerHelperWithException(producer, errorInvalidProducer);
    }

    /*Сценарий: добавление производителя, которого нет в БД.
                Наименование производителя = "".
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void addNewProducerTest_whenEmptyNameProducer_thenException() throws ValidationException{
        Producer producer = new Producer(0, "");
        addNewProducerHelperWithException(producer, errorInvalidProducer);
    }

    /*Сценарий: обновление производителя, который есть в БД. Новые данные производителя - корректны.
    * Результат: данные производителя обновлены.
    * */
    @Test
    public void updateProducerTest_thenOK() throws ValidationException{

        Producer producer = new Producer(1,"Apple");

        //мок объекта доступа к данным БД
        ProducerDao mockProducerDao = mock(ProducerStoreDao.class);
        when(mockProducerDao.update(producer))
                .thenAnswer((Answer<Boolean>) invocationOnMock -> {
                    producersList.removeIf(producerTmp ->  producerTmp.getId() == producer.getId());
                    producersList.add(producer);
                    return true;
                });

        //создание контроллера и внедрение в него бина producerDao
        ProducerController controller = new ProducerController();
        ReflectionTestUtils.setField(controller, "producerDao", mockProducerDao);

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("producer", producer);
        controller.updateProducer(producer, producer.getId(), modelMap);

        assertThat(sizeProducerListBefore, equalTo(producersList.size()));
        assertThat(producersList, hasItem(producer));
    }

    /*Метод моделирует обновление производителя, который есть в БД.
    * @param    producer - производитель (либо null, либо содержит некорректные новые данные)
    * @param    errorMessage - описание ошибки.
    * @throws   ValidationException - производитель содержит некорректные данные.
    * Если данные не обновлены, то метод генерирует исключение типа StoreException.
    * */
    private void updateProducerHelperWithException(int id, Producer producer, ErrorMessage errorMessage) throws ValidationException{

        //мок сообщений об ошибке
        ErrorProperties mockErrors = mock(ErrorProperties.class);
        when(mockErrors.getErrorMessage(NOT_VALID_UPDATED_PRODUCER_MSG)).thenReturn(errorInvalidProducer);
        when(mockErrors.getErrorMessage(NOT_UPDATED_PRODUCER_MSG)).thenReturn(errorNotUpdated);
        when(mockErrors.getErrorMessage(NO_PRODUCER_MSG)).thenReturn(errorNoProducer);


        //мок объекта доступа к данным БД
        ProducerDao mockProducerDao = mock(ProducerStoreDao.class);
        doThrow(new StoreException(errorMessage))
                .when(mockProducerDao).update(producer);

        //создание контроллера и внедрение в него бина producerDao
        ProducerController controller = new ProducerController();
        ReflectionTestUtils.setField(controller, "producerDao", mockProducerDao);
        ReflectionTestUtils.setField(controller, "errors", mockErrors);

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("producer", producer);
        controller.updateProducer(producer, id, modelMap);
    }

    /*Сценарий: обновление производителя, который нет в БД.
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void updateProducerTest_whenProducerNotExists_thenException() throws ValidationException{
        Producer producer = new Producer(100,"Toshiba");
        updateProducerHelperWithException(producer.getId(), producer, errorNoProducer);
    }

    /*Сценарий: обновление производителя = null.
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void updateProducerTest_whenNullProducer_thenException() throws ValidationException{
        updateProducerHelperWithException(1, null, errorInvalidUpdateProducer);
    }

    /*Сценарий: обновление производителя, который есть в БД.
                Наименование производителя = null.
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void updateProducerTest_whenNullNameProducer_thenException() throws ValidationException{
        Producer producer = new Producer(1, null);
        updateProducerHelperWithException(producer.getId(), producer, errorInvalidUpdateProducer);
    }

    /*Сценарий: обновление производителя, который есть в БД.
                Наименование производителя = "".
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void updateProducerTest_whenEmptyNameProducer_thenException() throws ValidationException{
        Producer producer = new Producer(0, "");
        updateProducerHelperWithException(producer.getId(), producer, errorInvalidUpdateProducer);
    }

    /*Сценарий: обновление производителя, который есть в БД. Новые данные производителя - корректны.
    * Результат: данные производителя обновлены.
    * */
    @Test
    public void deleteProducerTest_thenOK() throws ValidationException{

        int id = 1;

        //мок объекта доступа к данным БД
        ProducerDao mockProducerDao = mock(ProducerStoreDao.class);
        when(mockProducerDao.delete(id))
                .thenAnswer((Answer<Boolean>) invocationOnMock -> {
                    producersList.removeIf(producerTmp ->  producerTmp.getId() == id);
                    return true;
                });

        //создание контроллера и внедрение в него бина producerDao
        ProducerController controller = new ProducerController();
        ReflectionTestUtils.setField(controller, "producerDao", mockProducerDao);

        String namePage = controller.deleteProducer(id);
        assertThat(namePage, equalTo(PAGE_REDIRECT_PRODUCERS));
        assertThat(sizeProducerListBefore - 1, equalTo(producersList.size()));
    }

    /*Метод моделирует удаление производителя.
    * @param    id - код производителя, которого следует удалить.
    * @param    errorMessage - описание ошибки.
    * @throws   ValidationException - производитель содержит некорректные данные.
    * Если данные не обновлены, то метод генерирует исключение типа StoreException.
    * */
    private void deleteProducerHelperWithException(int id, ErrorMessage errorMessage){

        //мок сообщений об ошибке
        ErrorProperties mockErrors = mock(ErrorProperties.class);
        when(mockErrors.getErrorMessage(NO_PRODUCER_MSG)).thenReturn(errorNoProducer);

        //мок объекта доступа к данным БД
        ProducerDao mockProducerDao = mock(ProducerStoreDao.class);
        doThrow(new StoreException(errorMessage))
                .when(mockProducerDao).delete(id);

        //создание контроллера и внедрение в него бина producerDao
        ProducerController controller = new ProducerController();
        ReflectionTestUtils.setField(controller, "producerDao", mockProducerDao);
        ReflectionTestUtils.setField(controller, "errors", mockErrors);

        controller.deleteProducer(id);
    }

    /*Сценарий: удаление производителя, которого нет в БД.
    * Результат: ожидается исключение типа StoreException.
    * */
    @Test(expected = StoreException.class)
    public void deleteProducerTest_whenNoProducer_thenException(){
        deleteProducerHelperWithException(100, errorNoProducer);
    }

    @Test(expected = StoreException.class)
    public void deleteProducerTest_whenEmptyProducersList_thenException() {
        producersList.clear();
        deleteProducerHelperWithException(1, errorNoProducer);
    }

}
