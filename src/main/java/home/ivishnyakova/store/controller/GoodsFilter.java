package home.ivishnyakova.store.controller;

import java.io.Serializable;

/*Класс GoodsFilter представляет собой фильтр для выборки товаров.
*
* Автор: Вишнякова И.Н.*/
public class GoodsFilter implements Serializable{

    private int filterIdCategory;       //код категории товара
    private int filterIdProducer;       //код производителя товара
    private float filterMinPrice;       //минимальная цена диапазона
    private float filterMaxPrice;       //максимальная цена диапазона
    private boolean filterInStorage;    //признак есть ли складе

    public GoodsFilter(){
    }

    public int getFilterIdCategory() {
        return filterIdCategory;
    }

    public void setFilterIdCategory(int filterIdCategory) {
        this.filterIdCategory = filterIdCategory;
    }

    public int getFilterIdProducer() {
        return filterIdProducer;
    }

    public void setFilterIdProducer(int filterIdProducer) {
        this.filterIdProducer = filterIdProducer;
    }

    public float getFilterMinPrice() {
        return filterMinPrice;
    }

    public void setFilterMinPrice(float filterMinPrice) {
        this.filterMinPrice = filterMinPrice;
    }

    public float getFilterMaxPrice() {
        return filterMaxPrice;
    }

    public void setFilterMaxPrice(float filterMaxPrice) {
        this.filterMaxPrice = filterMaxPrice;
    }

    public boolean isFilterInStorage() {
        return filterInStorage;
    }

    public void setFilterInStorage(boolean filterInStorage) {
        this.filterInStorage = filterInStorage;
    }
}
