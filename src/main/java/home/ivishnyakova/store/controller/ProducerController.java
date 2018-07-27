package home.ivishnyakova.store.controller;

import home.ivishnyakova.store.dao.ProducerDao;
import home.ivishnyakova.store.entity.Producer;
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

/* Класс ProducerController является контроллером для веб-страницы администрирования
* производителей товаров.
*
* Автор: Вишнякова И.
* */
@Controller
public class ProducerController {

    //для доступа к инфо о производителях
    @Autowired
    private ProducerDao producerDao;

    //для представления описания ошибок на веб-странице
    @Autowired
    private ViewHandlerException viewHandlerException;

    //описание ошибок
    @Resource(name="errors")
    private ErrorProperties errors;

    //относит. название web-страниц (ответы контроллера)
    private static final String PAGE_PRODUCERS = "producers";
    private static final String PAGE_REDIRECT_PRODUCERS = "redirect:/getProducers/";
    private static final String ERROR_PAGE_PRODUCERS = "/getProducers/";

    //относит. название web-страниц, которые обрабатывает контроллер
    private static final String GET_PRODUCERS = "/getProducers";
    private static final String NEW_PRODUCER = "/newProducer";
    private static final String UPDATE_PRODUCER = "/updateProducer/{id}";
    private static final String DELETE_PRODUCER = "/deleteProducer/{id}";

    /*Метод getProducers подготавливает данные о всех производителях.
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @returns  В случае успешного получение списка производителей будет загружена страница для
    *           администрирования производителей (producers.jsp).
    *           В противном случае, страница с описание возникшей ошибки.
    * */
    @RequestMapping(value = GET_PRODUCERS, method = RequestMethod.GET)
    public String getProducers (ModelMap model){
        model.addAttribute("producersList", producerDao.getProducerSortList(true));
        return PAGE_PRODUCERS;
    }

    /*Метод addNewProducer выполняет добавление производителя producer в БД.
    * @param    producer - производитель, кот. будет добавляться в БД;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда producer = null или содержит некорректные данные;
    * @returns  В случае успешного добавления нового производителя будет загружена страница для
    *           администрирования производителей ("redirect:/getProducers/").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = NEW_PRODUCER, method = RequestMethod.POST)
    public String addNewProducer(@ModelAttribute("producer") Producer producer, ModelMap modelMap) {

        Optional.ofNullable(producer)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_VALID_NEW_PRODUCER")));

        modelMap.addAttribute("name", producer.getName());
        try {
            producerDao.insert(producer);
        }catch (ValidationException e){
            throw  new StoreException(errors.getErrorMessage("NOT_VALID_NEW_PRODUCER"));
        }
        return PAGE_REDIRECT_PRODUCERS;
    }

    /*Метод updateProducer выполняет обновление данных производителя с заданным id в БД.
    * @param    producer - новые данные о производителе;
    * @param    id - id производителя, кот. будет обновляться;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда producer = null или содержит некорректные данные;
    * @returns  В случае успешного обновления данных производителя будет загружена страница для
    *           администрирования производителей ("redirect:/getProducers/").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = UPDATE_PRODUCER, method = RequestMethod.POST)
    public String updateProducer(@ModelAttribute("producer") Producer producer, @PathVariable("id") int id, ModelMap modelMap){
        Optional.ofNullable(producer)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_VALID_UPDATED_PRODUCER")));

        producer.setId(id);
        try {
            if (!producerDao.update(producer))
                throw  new StoreException(errors.getErrorMessage("NO_PRODUCER"));
        } catch (ValidationException e) {
            throw  new StoreException(errors.getErrorMessage("NOT_VALID_UPDATED_PRODUCER"));
        }
        return PAGE_REDIRECT_PRODUCERS;
    }

    /*Метод deleteProducer выполняет удаление производителя с заданным id из БД.
    * @param    id - id производителя, кот. будет удаляться;
    * @throws   StoreException - в случае, когда производитель не найден;
    * @returns  В случае успешного удаления производителя будет загружена страница для
    *           администрирования производителей ("redirect:/getProducers/").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = DELETE_PRODUCER, method = RequestMethod.POST)
    public String deleteProducer(@PathVariable("id") int id){
        if (!producerDao.delete(id)) {
            throw new StoreException(errors.getErrorMessage("NO_PRODUCER"));
        }
        return PAGE_REDIRECT_PRODUCERS;
    }

    /*Метод handleStoreException подготавливает инфо об исключении StoreException
     * для отображения на веб-странице.*/
    @ExceptionHandler(StoreException.class)
    public ModelAndView handleStoreException(StoreException e) {
        return viewHandlerException.handleStoreException(e, ERROR_PAGE_PRODUCERS);
    }

    /*Метод handleAllException подготавливает инфо об исключении
     * для отображения на веб-странице.*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception e) {
        return viewHandlerException.handleAllException(e, ERROR_PAGE_PRODUCERS);
    }

}
