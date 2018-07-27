package home.ivishnyakova.store.dao;

import home.ivishnyakova.store.entity.Producer;
import home.ivishnyakova.store.exceptions.ValidationException;

import java.util.List;

/* Интерфейс ProducerDao описывает интерфейс доступа к данным производителя товаров.
*
* Автор: Вишнякова И.
* */
public interface ProducerDao {

    //добавить нового производителя
    void insert(Producer producer) throws ValidationException;

    //обновить данные о производителе
    boolean update(Producer producer) throws ValidationException;

    //удалить производителя товаров по ключу
    boolean delete(int id);

    //получить инфо о производителе товаров по его ключу
    Producer getProducerById(int id);

    //получить список всех производителей (без сортировки)
    List<Producer> getProducerList();

    //получить список всех производителей (с сортировков по названию производителя)
    List<Producer> getProducerSortList(boolean isAsc);
}
