package home.ivishnyakova.store.controller;



import home.ivishnyakova.store.utils.LoggerUtil;
import home.ivishnyakova.store.exceptions.StoreException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


/* Класс MainController является контроллером для главной веб-страницы магазина.
*
* Автор: Вишнякова И.
* */
@Controller
public class MainController {
    //для фильтрации товаров на странице
    @Autowired
    private FilterUtils filterUtils;
    //для представления описания ошибок на веб-странице
    @Autowired
    private ViewHandlerException viewHandlerException;

    //относит. название web-страниц (ответы контроллера)
    private static final String INDEX_PAGE = "index";

    //относит. название web-страниц, которые обрабатывает контроллер
    private static final String ROOT = "/";
    private static final String GOODS_BY_FILTER = "/getGoodsByFilter";


    /*Метод rootPage подготавливает данные о всех товарах и фильтр.
    * @param    model - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда проиозошла ошибка при выполнении запроса;
    * @returns  В случае успешного выборки товаров будет загружена главная страница.
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = ROOT, method =  RequestMethod.GET)
    public String rootPage(ModelMap model){
        filterUtils.setDefaultAttributes(model);
        return INDEX_PAGE;
    }

    /*Метод getGoodsByFilter подготавливает данные о товарах согласно фильтру.
    * @param    filter - фильтр для отбора товаров;
    * @param    model - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда проиозошла ошибка при выполнении запроса;
    * @returns  В случае успешного выборки товаров будет загружена главная страница.
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = GOODS_BY_FILTER, method = RequestMethod.GET)
    public String getGoodsByFilter (ModelMap model,
                                    @ModelAttribute("goodsFilter") GoodsFilter filter){
        filterUtils.getGoodsByFilter(model,filter);
        return INDEX_PAGE;
    }

    /*Метод handleStoreException подготавливает инфо об исключении StoreException
     * для отображения на веб-странице.*/
    @ExceptionHandler(StoreException.class)
    public ModelAndView handleStoreException(StoreException e) {
        return viewHandlerException.handleStoreException(e, ROOT);
    }

    /*Метод handleAllException подготавливает инфо об исключении
     * для отображения на веб-странице.*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception e) {
        return viewHandlerException.handleAllException(e, ROOT);
    }

}
