package home.ivishnyakova.store.rest;

import home.ivishnyakova.store.dao.CategoryDao;
import home.ivishnyakova.store.entity.Category;
import home.ivishnyakova.store.exceptions.RestException;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.exceptions.ValidationException;
import home.ivishnyakova.store.utils.CategoryLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/*Класс CategoryRestController предназначен для получения доступа к состоянию
* ресурсов - категории товаров.
* Ресурс представляется в форматах xml, json.
*
*   Автор: Вишнякова И.
* */
@RestController
@RequestMapping(value = "/categories",
        produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
        headers={"content-type=" + MediaType.APPLICATION_JSON_UTF8_VALUE, "content-type=" + MediaType.APPLICATION_XML_VALUE })
public class CategoryRestController{

    //для доступа к данным в БД
    @Autowired
    private CategoryDao categoryDao;

    //сообщения - описание ответов сервера на запрос
    @Autowired
    private Properties messages;

    //для проверки заголовка запроса
    @Autowired
    private HeaderChecker headerChecker;

    //относ. uri ресурсов
    private static final String CATEGORIES = "";
    private static final String CATEGORIES_ID = "/{id}";
    private static final String CATEGORIES_ID_SUBCATEGORIES = "/{id}/subcategories";
    private static final String NEW_CATEGORY = "/category";

    //все категории
    @RequestMapping(value = CATEGORIES, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getCategories(@RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);

        List<Category> resList = categoryDao.getCategoryList();

        if (resList.isEmpty()){
            throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NO_CATEGORIES"));
        }
        return resList;
    }

    //категория по id
    @RequestMapping(value = CATEGORIES_ID, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Category getCategory(@PathVariable("id") int id, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        try {
            return categoryDao.getCategoryById(id);
        }catch (StoreException e) {
            throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NO_CATEGORY"));
        }
    }

    //подкатегории категории заданной по id
    @RequestMapping(value = CATEGORIES_ID_SUBCATEGORIES, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getSubCategoriesById (@PathVariable("id") int id, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        List<Category> resList = categoryDao.getCategoryListById(id, CategoryLevel.SUB_CATEGORY.getLevel());
        if (resList.isEmpty()){
            throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NO_SUBCATEGORIES"));
        }
        return resList;
    }

    //создание категории
    @RequestMapping(value = NEW_CATEGORY, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody Category createCategory(@RequestBody Category category, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        Category categoryOpt = Optional.ofNullable(category)
                                       .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_VALID_CATEGORY")));

        try {
            categoryDao.insert(categoryOpt);
        } catch (ValidationException | StoreException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_INSERTED_CATEGORY"));
        }
        return categoryOpt;
    }

    //обновление категории
    @RequestMapping(value = CATEGORIES_ID, method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable("id") int id, @RequestHeader HttpHeaders headers, @RequestBody Category category){
        headerChecker.checkRequestHeaders(headers);
        Category categoryOpt = Optional.ofNullable(category)
                                       .orElseThrow(() -> new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_VALID_CATEGORY")));
        category.setId(id);
        try {
            categoryOpt.validate(messages);
            if (!categoryDao.update(categoryOpt))
                throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NOT_UPDATED_CATEGORY"));
        } catch (ValidationException|StoreException e) {
            throw new RestException(HttpStatus.BAD_REQUEST, messages.getProperty("NOT_UPDATED_CATEGORY"));
        }
    }

    //удаление категории
    @RequestMapping(value = CATEGORIES_ID, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") int id, @RequestHeader HttpHeaders headers){
        headerChecker.checkRequestHeaders(headers);
        try {
            if (!categoryDao.delete(id))
                throw new RestException(HttpStatus.NOT_FOUND, messages.getProperty("NOT_DELETED_CATEGORY"));
        } catch (StoreException e) {
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, messages.getProperty("NOT_DELETED_CATEGORY"));
        }
    }

}
