package home.ivishnyakova.store.controller;

import home.ivishnyakova.store.dao.CategoryDao;
import home.ivishnyakova.store.dao.GoodsDao;
import home.ivishnyakova.store.dao.ProducerDao;
import home.ivishnyakova.store.utils.CategoryLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;

/*Класс FilterUtils предназначен для загрузки и сохранения настроек фильтра
* товаров с/в представление.
*
* Автор: Вишнякова И.
* */
@Component
public class FilterUtils {
    //для доступа к категориям в БД
    @Autowired
    private CategoryDao categoryDao;
    //для доступа к производителям в БД
    @Autowired
    private ProducerDao producerDao;
    //для доступа к товарам в БД
    @Autowired
    private GoodsDao goodsDao;
    //уровень подкатегорий
    private short SUB_CATEGORY_LEVEL = CategoryLevel.SUB_CATEGORY.getLevel();

    /*Установка значений по умолчанию для фильтра товаров в модели model*/
    protected void setDefaultAttributes(ModelMap model) {

        model.addAttribute("filterMinPrice", 0);
        model.addAttribute("filterMaxPrice", 0);
        model.addAttribute("filterInStorage", true);
        model.addAttribute("filterIdCategory", 0);
        model.addAttribute("filterIdProducer", 0);

        model.addAttribute("categoriesList", categoryDao.getCategoryFullNameListByLevel(SUB_CATEGORY_LEVEL));
        model.addAttribute("producersList", producerDao.getProducerSortList(true));
        model.addAttribute("goodsList", goodsDao.getGoodsList(true));

    }

    /*Получение значений параметров фильтра товаров filter из модели model*/
    protected void getGoodsByFilter (ModelMap model,  @ModelAttribute("goodsFilter") GoodsFilter filter){

        model.addAttribute("filterMinPrice", filter.getFilterMinPrice());
        model.addAttribute("filterMaxPrice", filter.getFilterMaxPrice());
        model.addAttribute("filterInStorage", filter.isFilterInStorage());
        model.addAttribute("filterIdCategory", filter.getFilterIdCategory());
        model.addAttribute("filterIdProducer", filter.getFilterIdProducer());

        model.addAttribute("categoriesList", categoryDao.getCategoryFullNameListByLevel(SUB_CATEGORY_LEVEL));
        model.addAttribute("producersList", producerDao.getProducerSortList(true));
        model.addAttribute("goodsList", goodsDao.getGoodsListByFilter(filter.getFilterMinPrice(), filter.getFilterMaxPrice(), filter.getFilterIdCategory(), filter.getFilterIdProducer(), filter.isFilterInStorage()));

    }

    public short getSUB_CATEGORY_LEVEL() {
        return SUB_CATEGORY_LEVEL;
    }
}
