package home.ivishnyakova.store.dao;

import home.ivishnyakova.store.entity.Category;
import home.ivishnyakova.store.exceptions.ValidationException;

import java.util.List;

/* Интерфейс CategoryDao описывает возможности доступа к категориям товаров.
*
* Автор: Вишнякова И.
* */
public interface CategoryDao {

    //добавление новой категории
    boolean insert(Category category) throws ValidationException;

    //обновление категории
    boolean update(Category category) throws ValidationException;

    //удалить категорию по id
    boolean delete(int id);

    //получение категории по id
    Category getCategoryById(int id);

    //получение списка категорий
    List<Category> getCategoryList();

    //получение списка категорий заданного уровня no_level
    List<Category> getCategoryListByLevel(short no_level);

    //получение списка категорий заданного уровня no_level с детальным именем категории
    List<Category> getCategoryFullNameListByLevel(short no_level);

    //получение списка категорий заданного уровня no_level, у которых родит. категория c id_category
    List<Category> getCategoryListById(int id_category, short no_level);

}
