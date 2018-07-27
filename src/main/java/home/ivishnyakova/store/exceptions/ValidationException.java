package home.ivishnyakova.store.exceptions;

import home.ivishnyakova.store.utils.LoggerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*Класс ValidationException представляет собой исключение,
* возникающее при проверке валидности данных сущностей
* Producer, Category, Goods.
*
* Автор: Вишнякова И.
* */
public class ValidationException extends Exception {
    //для логирования
    private static final Logger logger = LogManager.getLogger(LoggerUtil.getClassName());

    public ValidationException() {
        logger.error("Validation exception");
    }

    public ValidationException(String message) {
        super(message);
        logger.error("Validation exception: " + message);
    }

    @Override
    public String getMessage() {
        return "Validation error: " + super.getMessage();
    }
}
