package home.ivishnyakova.store.entity;

import home.ivishnyakova.store.exceptions.ValidationException;

import java.util.Properties;

/*Интерфейс Validation прелназначен для выполнения проверки валидации данных объекта
* с возможностью описания ошибок с использованием messages.
* Автор: Вишнякова И.*/
public interface Validation {
    void validate(Properties messages) throws ValidationException;
}
