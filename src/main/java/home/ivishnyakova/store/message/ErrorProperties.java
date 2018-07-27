package home.ivishnyakova.store.message;

import java.util.Map;
import java.util.Optional;

/*Класс ErrorProperties представляет собой хранилище возможных ошибок
* при работе веб-приложения.
*
* Автор: Вишнякова И.
* */
public class ErrorProperties {

    //описание ошибок
    private Map<String, ErrorMessage> errors;

    public Map<String, ErrorMessage> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, ErrorMessage> errors) {
        this.errors = errors;
    }

    //получение описания ошибки по ключу
    public ErrorMessage getErrorMessage(String key){
        return Optional.ofNullable(errors.get(key))
                       .orElse(new ErrorMessage());
    }
}
