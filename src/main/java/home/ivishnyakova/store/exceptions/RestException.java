package home.ivishnyakova.store.exceptions;

import home.ivishnyakova.store.utils.LoggerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

/*Класс RestException - исключение, которое генерируется при ошибке в работе
* REST контроллеров.
* Содержит код ответа клиенту по протоколу HTTP и сообщение.
*
*   Автор: Вишнякова И.
* */
public class RestException extends RuntimeException {
    private HttpStatus status;

    //для логирования
    private static final Logger logger = LogManager.getLogger(LoggerUtil.getClassName());

    public RestException(HttpStatus status){
        this.status = status;
        logger.error("Status: " + status);
    }

    public RestException(HttpStatus status, String message){
        super(message);
        this.status = status;
        logger.error("Status: " + status + " Message: " + message);
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\nHttp status = " + status;
    }
}
