package home.ivishnyakova.store.utils;

/*Перечисление CategoryLevel описывает уровни вложенности категорий товаров.
*
* Автор: Вишнякова И.
* */
public enum CategoryLevel {
    ROOT((short)0),         //корневая категории
    CATEGORY((short)1),     //категории
    SUB_CATEGORY((short)2), //подкатегории
    MAX_NO_LEVEL((short)5); //максимальное количество уровней вложенности категорий

    private short level;

    CategoryLevel(short level){
        this.level = level;
    }

    public short getLevel() {
        return level;
    }
}
