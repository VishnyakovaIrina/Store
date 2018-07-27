package home.ivishnyakova.store.message;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/*Класс ErrorMessage описывает ошибку, которая произошла при работе
* клиента с веб-приложением.
*
* Автор: Вишнякова И.
* */
public class ErrorMessage implements Serializable{

    private String code;        //код ошибки
    private String message;     //сообщение
    private List<String> cause; //причина ошибки

    public ErrorMessage() {
        code = "500";
        message = "The internal server error";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getCause() {
        return cause;
    }

    public void setCause(List<String> cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        if (Optional.ofNullable(code)
                .filter((code)->!code.isEmpty())
                .isPresent()) {
            res.append("Error # " + code);
            res.append("\n");
        }
        if (Optional.ofNullable(message)
                .filter((message) -> !message.isEmpty())
                .isPresent()) {
            res.append("Message: ");
            res.append(message);
        }
        if (Optional.ofNullable(cause)
                .filter((cause) -> !cause.isEmpty())
                .isPresent()) {
            res.append("Cause: ");
            res.append(cause);
        }

        if (res.length() != 0)
            return res.toString();

        return "Undefined error";
    }
}
