package home.ivishnyakova.store.controller;

import home.ivishnyakova.store.dao.CategoryDao;
import home.ivishnyakova.store.dao.GoodsDao;
import home.ivishnyakova.store.dao.ProducerDao;
import home.ivishnyakova.store.entity.Goods;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.exceptions.ValidationException;
import home.ivishnyakova.store.message.ErrorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Optional;

/* Класс GoodsController является контроллером для веб-страницы администрирования товаров.
*
* Автор: Вишнякова И.
* */
@Controller
public class GoodsController {

    //для доступа к товарам в БД
    @Autowired
    private GoodsDao goodsDao;

    //для доступа к категориям в БД
    @Autowired
    private CategoryDao categoryDao;

    //для доступа к производителям в БД
    @Autowired
    private ProducerDao producerDao;

    //для работы с фильтром товаров
    @Autowired
    private FilterUtils filterUtils;

    //для обработки исключений и представления ошибок на веб-странице
    @Autowired
    private ViewHandlerException viewHandlerException;

    //описание ошибок
    @Resource(name="errors")
    private ErrorProperties errors;

    //относит. название web-страниц (ответы контроллера)
    private static final String PAGE_GOODS = "goods";
    private static final String PAGE_REDIRECT_GOODS = "redirect:/getGoods/";
    private static final String ERROR_PAGE_GOODS = "/getGoods/";

    //относит. название web-страниц, которые обрабатывает контроллер
    private static final String GET_GOODS = "/getGoods";
    private static final String GET_FILTER_GOODS = "/getFilterGoods";
    private static final String NEW_GOODS = "/newGoods";
    private static final String UPDATE_GOODS = "/updateGoods/{id}";
    private static final String DELETE_GOODS = "/deleteGoods/{id}";


    /*Метод getGoods загружает в модель инфо о товарах, категориях и производителях,
    * а также настраивает фильт товаров (по умолчанию - отображать все товары).
    * @param    model - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае ошибки выполнения запроса к БД;
    * @returns  В случае успешной загрузки данныхбудет загружена страница для
    *           администрирования товаров ("goods").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = GET_GOODS, method = RequestMethod.GET)
    public String getGoods (ModelMap model){
        model.addAttribute("goodsList", goodsDao.getGoodsList());
        model.addAttribute("producersList", producerDao.getProducerSortList(true));
        model.addAttribute("categoriesList", categoryDao.getCategoryFullNameListByLevel(filterUtils.getSUB_CATEGORY_LEVEL()));

        filterUtils.setDefaultAttributes(model);
        return PAGE_GOODS;
    }

    /*Метод getGoodsByFilter выполняет фильтрацию товаров согласно фильтру filter.
    * @param    model - набор атрибутов для взаимодействия контроллера и представления;
    * @param    filter - фильт для отбора товаров (по цене, категории, производителю, товар на складе или нет);
    * @throws   StoreException - в случае ошибки выполнения запроса к БД;
    * @returns  В случае успешной загрузки данныхбудет загружена страница для
    *           администрирования товаров ("goods").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = GET_FILTER_GOODS, method = RequestMethod.GET)
    public String getGoodsByFilter (ModelMap model, @ModelAttribute("goodsFilter") GoodsFilter filter){
        filterUtils.getGoodsByFilter(model,filter);
        return PAGE_GOODS;
    }

    /*Метод addNewGoods выполняет добавление нового товара в БД.
    * @param    goods - товар, кот. будет добавляться в БД;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда goods = null или содержит некорректные данные;
    * @returns  В случае успешного добавления нового товара будет загружена страница для
    *           администрирования товаров ("redirect:/getGoods/").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = NEW_GOODS, method = RequestMethod.POST)
    public String addNewGoods(@ModelAttribute("goods") Goods goods, ModelMap modelMap){

        Optional.ofNullable(goods)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_VALID_NEW_PRODUCT")));

        try {
            if (!goodsDao.insert(goods))
                throw new StoreException(errors.getErrorMessage("NOT_INSERTED_PRODUCT"));
        } catch (ValidationException e) {
            throw new StoreException(errors.getErrorMessage("NOT_VALID_NEW_PRODUCT"));
        }
        return PAGE_REDIRECT_GOODS;
    }

    /*Метод updateGoods выполняет обновление товара по id в БД.
    * @param    id товара, кот. будет обновлен;
    * @param    goods - новые данные товара;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда goods = null или содержит некорректные данные;
    * @returns  В случае успешного обновления товара будет загружена страница для
    *           администрирования товаров ("redirect:/getGoods/").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = UPDATE_GOODS, method = RequestMethod.POST)
    public String updateGoods(@ModelAttribute("goods") Goods goods, @PathVariable("id") int id, ModelMap modelMap){

        Optional.ofNullable(goods)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_VALID_UPDATED_PRODUCT")));

        goods.setId(id);
        try {
            if (!goodsDao.update(goods))
                throw new StoreException(errors.getErrorMessage("NOT_UPDATED_PRODUCT"));
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        return PAGE_REDIRECT_GOODS;
    }

    /*Метод deleteGoods выполняет удаление товара по id из БД.
    * @param    id товара, кот. будет удален;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда товар не найден;
    * @returns  В случае успешного удаления товара будет загружена страница для
    *           администрирования товаров ("redirect:/getGoods/").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = DELETE_GOODS, method = RequestMethod.POST)
    public String deleteGoods(@PathVariable("id") int id){
        if (!goodsDao.delete(id))
            throw new StoreException(errors.getErrorMessage("NOT_DELETED_PRODUCT"));
        return PAGE_REDIRECT_GOODS;
    }

    /*Метод handleStoreException подготавливает инфо об исключении StoreException
     * для отображения на веб-странице.*/
    @ExceptionHandler(StoreException.class)
    public ModelAndView handleStoreException(StoreException e) {
        return viewHandlerException.handleStoreException(e, ERROR_PAGE_GOODS);
    }

    /*Метод handleAllException подготавливает инфо об исключении
     * для отображения на веб-странице.*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception e) {
        return viewHandlerException.handleAllException(e, ERROR_PAGE_GOODS);
    }


}
