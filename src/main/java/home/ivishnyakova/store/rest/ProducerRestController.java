package home.ivishnyakova.store.rest;

import home.ivishnyakova.store.dao.ProducerDao;
import home.ivishnyakova.store.entity.Producer;
import home.ivishnyakova.store.exceptions.RestException;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/*Класс ProducerRestController предназначен для получения доступа к состоянию
* ресурсов - производители товаров.
* Ресурс представляется в форматах xml, json.
*
*   Автор: Вишнякова И.
* */
@RestController
@RequestMapping(value = "/producers",
                consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
                produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
                headers={"Accept=" + MediaType.APPLICATION_JSON_UTF8_VALUE, "Accept=" + MediaType.APPLICATION_XML_VALUE })
public class ProducerRestController{

    //для доступа к данным в БД
    @Autowired
    private ProducerDao producerDao;

    //для проверки заголовка запроса
    @Autowired
    private HeaderChecker headerChecker;

    //сообщения - ответы сервера
    @Resource(name="messages")
    private Properties messages;

    //относ. uri ресурсов
    private static final String PRODUCERS = "";
    private static final String PRODUCERS_ID = "/{id}";
    private static final String NEW_PRODUCER = "/producer";

    //все производители
    @RequestMapping(value = PRODUCERS, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Producer> getProducers(@RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        List<Producer> resList = producerDao.getProducerList();
        if (resList.isEmpty()){
            throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NO_PRODUCERS"));
        }
        return resList;
    }

    //производитель по id
    @RequestMapping(value = PRODUCERS_ID, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Producer getProducer(@PathVariable("id") int id, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        try {
            return producerDao.getProducerById(id);
        }catch (StoreException e) {
            throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NO_PRODUCER"));
        }
    }

    //создание производителя
    @RequestMapping(value = NEW_PRODUCER, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody Producer createProducer(@RequestBody Producer producer, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        Producer producerOpt = Optional.ofNullable(producer)
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_SPECIFIED_PRODUCER")));
        try {
            producerDao.insert(producerOpt);
        } catch (ValidationException | StoreException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_INSERTED_PRODUCER"));
        }
        return producerOpt;
    }

    //обновление производителя
    @RequestMapping(value = PRODUCERS_ID, method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProducer(@PathVariable("id") int id, @RequestHeader HttpHeaders headers, @RequestBody Producer producer){
        headerChecker.checkRequestHeaders(headers);
        Producer producerOpt = Optional.ofNullable(producer)
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_SPECIFIED_PRODUCER")));
        producer.setId(id);
        try {
            if(!producerDao.update(producerOpt))
                throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NOT_DELETED_PRODUCER"));
        } catch (ValidationException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_UPDATED_PRODUCER"));
        } catch (StoreException e) {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, messages.getProperty("NOT_UPDATED_PRODUCER"));
        }
    }

    //удаление производителя
    @RequestMapping(value = PRODUCERS_ID, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProducer(@PathVariable("id") int id, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        try {
            if (!producerDao.delete(id))
                throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NOT_DELETED_PRODUCER"));
        } catch (StoreException e) {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, messages.getProperty("NOT_DELETED_PRODUCER"));
        }
    }
}
