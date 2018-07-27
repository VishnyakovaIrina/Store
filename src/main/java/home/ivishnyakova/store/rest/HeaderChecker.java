package home.ivishnyakova.store.rest;

import home.ivishnyakova.store.exceptions.RestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Properties;

/*Класс HeaderChecker предназначен для проверки заголовков запросов к серверу.
* Автор: Вишнякова И.
* */
@Component
public class HeaderChecker {
    //для описания ошибок
    @Autowired
    private Properties messages;

    public Properties getMessages() {
        return messages;
    }

    public void setMessages(Properties messages) {
        this.messages = messages;
    }

    /*Метод checkRequestHeaders проверяет заголовок запроса headers.
    * Если заголовке запросов указаны Content-Type, Accept в форматах json/xml,
    * то запрос будет обрабатываться сервером, иначе генерируется исключение RestException.*/
    public void checkRequestHeaders(HttpHeaders headers){

        if (!(headers.getContentType().includes(MediaType.APPLICATION_JSON_UTF8) ||
                headers.getContentType().includes(MediaType.APPLICATION_XML)))
            throw new RestException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, messages.getProperty("NOT_SUPPORTED_CONTENT_MEDIATYPE_REQUEST"));

        if (!(headers.getAccept().contains(MediaType.APPLICATION_JSON_UTF8) ||
                headers.getAccept().contains(MediaType.APPLICATION_XML)))
            throw new RestException(HttpStatus.NOT_ACCEPTABLE, messages.getProperty("NOT_SUPPORTED_ACCEPT_MEDIATYPE_REQUEST"));
    }


}
