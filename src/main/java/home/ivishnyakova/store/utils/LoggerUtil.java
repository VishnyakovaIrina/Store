package home.ivishnyakova.store.utils;

/*Класс LoggerUtil - утилитный для log4j */
public class LoggerUtil {

    /*Метод getClassName позволяет получить имя класса, который вызвал этот метод.
    * Предназначен для опеределения имени класса для наименования логгера*/
    public static String getClassName(){
        try{
            throw new RuntimeException();
        }
        catch (RuntimeException e){
            return e.getStackTrace()[1].getClassName();
        }
    }
}
