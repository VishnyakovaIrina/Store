package home.ivishnyakova.store.rest;

import home.ivishnyakova.store.dao.GoodsDao;
import home.ivishnyakova.store.entity.Goods;
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

/*Класс GoodsRestController предназначен для получения доступа к состоянию
* ресурсов - товары.
* Ресурс представляется в форматах xml, json.
*
*   Автор: Вишнякова И.
* */
@RestController
@RequestMapping(value = "/goods",
        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
        headers={"Accept=" + MediaType.APPLICATION_JSON_UTF8_VALUE, "Accept=" + MediaType.APPLICATION_XML_VALUE })
public class GoodsRestController{

    //для доступа к данным в БД
    @Autowired
    private GoodsDao goodsDao;

    //сообщения - ответы сервера
    @Resource(name="messages")
    private Properties messages;

    //для проверки заголовка запроса
    @Autowired
    private HeaderChecker headerChecker;

    //относ. uri ресурсов
    private static final String GOODS = "";
    private static final String GOODS_ID = "/{id}";
    private static final String NEW_GOODS = "/goods";

    //все товары
    @RequestMapping(value = GOODS, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Goods> getGoods(@RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        List<Goods> resList = goodsDao.getGoodsList();
        if (resList.isEmpty()){
            throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NO_PRODUCTS"));
        }
        return resList;
    }

    //товар по id
    @RequestMapping(value = GOODS_ID, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Goods getGoods(@PathVariable("id") int id, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        try {
            return goodsDao.getGoodsById(id);
        }
        catch (StoreException e){
            throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NO_PRODUCT"));
        }
    }

    //создание товара
    @RequestMapping(value = NEW_GOODS, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody Goods createGoods(@RequestBody Goods goods, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        Goods goodsOpt = Optional.ofNullable(goods)
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_VALID_PRODUCT")));
        try {
            goodsDao.insert(goodsOpt);
        } catch (ValidationException | StoreException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_INSERTED_PRODUCT"));
        }
        return goodsOpt;
    }

    //обновление товара
    @RequestMapping(value = GOODS_ID, method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGoods(@PathVariable("id") int id, @RequestHeader HttpHeaders headers, @RequestBody Goods goods){
        headerChecker.checkRequestHeaders(headers);
        Goods goodsOpt = Optional.ofNullable(goods)
                .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_VALID_PRODUCT")));
        goods.setId(id);

        try {
            if (!goodsDao.update(goodsOpt))
                throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NOT_UPDATED_PRODUCT"));
        } catch (ValidationException|StoreException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_UPDATED_PRODUCT"));
        }
    }

    //удаление товара
    @RequestMapping(value = GOODS_ID, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoods(@PathVariable("id") int id, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        try {
            if (!goodsDao.delete(id))
                throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NOT_DELETED_PRODUCT"));
        } catch (StoreException e) {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, messages.getProperty("NOT_DELETED_PRODUCT"));
        }
    }
}
