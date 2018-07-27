package home.ivishnyakova.store.exceptions;

import home.ivishnyakova.store.utils.LoggerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Properties;

/*Класс StoreHandlerExceptionResolver предназначен для обработки перехваченных
* исключений следующих типов при работе REST контроллеров:
*   HttpMediaTypeNotSupportedException;
*   RestException;
*   StoreException;
*   Exception;
*   В результате обработки исключений формируется объект типа ResponseEntity<String>,
*   содержащий текстовое сообщение и код ответа сервера на возникшее исключение.
*
*   Автор: Вишнякова И.
*   */
@ControllerAdvice
public class StoreHandlerExceptionResolver extends ExceptionHandlerExceptionResolver
{
    //для логирования
    Logger logger = LogManager.getLogger(LoggerUtil.getClassName());

    //текст сообщений
    @Autowired
    Properties messages;

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleControllerException(HttpMediaTypeNotSupportedException e){
        return new ResponseEntity<>(messages.getProperty("NOT_SUPPORTED_CONTENT_MEDIATYPE_REQUEST"),
                                    HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    //обработка исключения RestException
    @ExceptionHandler(RestException.class)
    public ResponseEntity<String> handleException(RestException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    //обработка исключения StoreException
    @ExceptionHandler(StoreException.class)
    public ResponseEntity<String> handleException(StoreException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //обработка исключения Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
