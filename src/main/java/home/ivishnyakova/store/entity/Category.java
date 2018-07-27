package home.ivishnyakova.store.entity;

import home.ivishnyakova.store.exceptions.ValidationException;
import org.springframework.stereotype.Component;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Optional;
import java.util.Properties;

import static home.ivishnyakova.store.utils.CategoryLevel.MAX_NO_LEVEL;

/* Класс Category описывает категорию товара.
*  Категории могут вкладываться одна в другую,
*  т.е. образовывать древовидную структуру.
*
* Автор: Вишнякова И.Н.
* */
@Component
@XmlRootElement(name = "category")
public class Category implements Serializable, Validation{
    private int id;             //первичный ключ
    private String name;        //название категории
    private short no_level;     //номер уровня вложенности (0 - только корень дерева)
    private int id_category;    //номер родительской категории (только для корня - равна null)

    public Category() {
    }

    public Category(int id, String name, short no_level, int id_category) {
        this.id = id;
        this.name = name;
        this.no_level = no_level;
        this.id_category = id_category;
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public short getNo_level() {
        return no_level;
    }

    public void setNo_level(short no_level) {
        this.no_level = no_level;
    }

    @XmlElement
    public Integer getId_category() {
        return id_category;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", no_level=" + no_level +
                ", id_category=" + id_category +
                '}';
    }

    @Override
    public void validate(Properties messages) throws ValidationException {
        if (!Optional.ofNullable(name).isPresent() || (Optional.ofNullable(name).isPresent() && name.isEmpty()))
            throw new ValidationException(messages.getProperty("NULL_EMPTY_NAME_CATEGORY"));

        if (id_category == id && id_category != 0)
            throw new ValidationException(messages.getProperty("RECURSIVE_ID_CATEGORY"));

        if (id_category < 0)
            throw new ValidationException(messages.getProperty("NOT_CORRECT_ID_CATEGORY"));

        if (no_level < 0 || no_level > MAX_NO_LEVEL.getLevel())
            throw new ValidationException(messages.getProperty("NOT_CORRECT_NO_LEVEL_CATEGORY"));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!Optional.ofNullable(obj).isPresent() || getClass() != obj.getClass()) return false;

        Category category = (Category) obj;

        if (id != category.id) return false;
        if (no_level != category.no_level) return false;
        if (id_category != category.id_category) return false;
        return name.equals(category.name);
    }

}
