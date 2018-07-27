package home.ivishnyakova.store.controller;

import home.ivishnyakova.store.message.ErrorMessage;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.utils.LoggerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/*Класс ViewHandlerException предназначен для представления описания исключительных
* ситуаций, возникших в контроллерах, в виде веб-страниц.
*
* Автор: Вишнякова И.*/
@Component
public class ViewHandlerException {

    //для логирования
    private static Logger logger = LogManager.getLogger(LoggerUtil.getClassName());

    //относит.адрес страницы для описания ошибок
    private static final String ERROR_PAGE = "/errors/commonError";

    /*Метод handleStoreException подготавливает инфо об исключении StoreException
     * для отображения на веб-странице.*/
    @ExceptionHandler(StoreException.class)
    public ModelAndView handleStoreException(StoreException e, String backUrl) {

        ModelAndView model = new ModelAndView(ERROR_PAGE);

        ErrorMessage errorMessage = e.getError();
        logger.error("StoreException = " + errorMessage, e);

        model.addObject("errorMessage", errorMessage);
        model.addObject("url", backUrl);

        return model;
    }

    /*Метод handleAllException подготавливает инфо о любом исключении (кроме StoreException)
     * для отображения на веб-странице.*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception e, String backUrl) {

        ModelAndView model = new ModelAndView(ERROR_PAGE);

        logger.error("Exception", e);

        model.addObject("errorMessage", e.getMessage());
        model.addObject("url", backUrl);

        return model;
    }

}
