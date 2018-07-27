package home.ivishnyakova.store.dao;

import home.ivishnyakova.store.entity.Goods;
import home.ivishnyakova.store.exceptions.ValidationException;

import java.util.List;

/* Интерфейс GoodsDao описывает интерфейс доступа к данным товаров магазина.
*
* Автор: Вишнякова И.
* */
public interface GoodsDao {

    //добавить новый товар
    boolean insert(Goods goods) throws ValidationException;

    //обновить данные о товаре
    boolean update(Goods goods) throws ValidationException;

    //удалить товар по id
    boolean delete(int id);

    //получить товар по id
    Goods getGoodsById(int id);

    //получить список товаров
    List<Goods> getGoodsList();

    //получить список товаров с сортировкой по наименованию товаров
    List<Goods> getGoodsList(boolean isAsc);

    //получить список товаров согласно параметрам фильтра
    List<Goods> getGoodsListByFilter(float minPrice, float maxPrice, int id_category, int id_producer, boolean in_storage);
}
