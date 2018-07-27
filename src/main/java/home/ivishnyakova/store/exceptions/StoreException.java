package home.ivishnyakova.store.exceptions;

import home.ivishnyakova.store.message.ErrorMessage;
import home.ivishnyakova.store.utils.LoggerUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/*Класс StoreException является исключением, которое описывает ошибку, возникшую
* при работе веб-приложения.
*
* Автор: Вишнякова И.
* */
public class StoreException extends RuntimeException {
    //для логирования
    private static final Logger logger = LogManager.getLogger(LoggerUtil.getClassName());

    @Autowired
    private ErrorMessage error;

    public StoreException(){
        super();
    }

    public StoreException(Throwable cause){
        super(cause);
        logger.error("Cause exception: " + cause.getMessage());
    }

    public StoreException(ErrorMessage error, Throwable cause){
        super(cause);
        this.error = error;
        logger.error("Error: " + error + " Cause exception: " + cause.getMessage());
    }

    public StoreException(ErrorMessage error){
        super();
        this.error = error;
        logger.error("Error: " + error);
    }

    public ErrorMessage getError() {
        return error;
    }

    public void setError(ErrorMessage error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        StringBuilder res = new StringBuilder();

        if (Optional.ofNullable(error).isPresent()){
            res.append(error);
        }
        if (Optional.ofNullable(getCause()).isPresent()){
            res.append(" Cause: ");
            res.append(getCause().getMessage());
        }
        if(res.length() == 0){
            res.append("Some undefined system error");
        }
        return res.toString();
    }
}
