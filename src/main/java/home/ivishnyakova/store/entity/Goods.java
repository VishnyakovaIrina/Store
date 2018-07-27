package home.ivishnyakova.store.entity;

import home.ivishnyakova.store.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.Properties;

/* Класс Goods описывает товар.
*
* Автор: Вишнякова И.Н.
* */
@Component
public class Goods implements Serializable, Validation{

    private int id;             //первичный ключ
    private String name;        //наименование товара
    private float price;        //цена
    private String description; //описание
    private boolean in_storage; //есть ли на складе
    private int id_category;    //код категории, к которой относится
    private int id_producer;    //код производителя товара

    public Goods() {
    }

    public Goods(int id, String name, float price, String description, boolean in_storage, int id_category, int id_producer) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.in_storage = in_storage;
        this.id_category = id_category;
        this.id_producer = id_producer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIn_storage() {
        return in_storage;
    }

    public void setIn_storage(boolean in_storage) {
        this.in_storage = in_storage;
    }

    public int getId_category() {
        return id_category;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }

    public int getId_producer() {
        return id_producer;
    }

    public void setId_producer(int id_producer) {
        this.id_producer = id_producer;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", in_storage=" + in_storage +
                ", id_category=" + id_category +
                ", id_producer=" + id_producer +
                "}\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!Optional.ofNullable(obj).isPresent() || getClass() != obj.getClass()) return false;

        Goods goods = (Goods) obj;

        if (id != goods.id) return false;
        if (Float.compare(goods.price, price) != 0) return false;
        if (in_storage != goods.in_storage) return false;
        if (id_category != goods.id_category) return false;
        if (id_producer != goods.id_producer) return false;
        if (!name.equals(goods.name)) return false;
        return Optional.ofNullable(description).isPresent() ? description.equals(goods.description) : !Optional.ofNullable(goods.description).isPresent();
    }

    @Override
    public void validate(Properties messages) throws ValidationException {
        if (!Optional.ofNullable(name).isPresent() || (Optional.ofNullable(name).isPresent() && name.isEmpty()))
            throw new ValidationException(messages.getProperty("NULL_EMPTY_NAME_PRODUCT"));

        if (price < 0)
            throw new ValidationException(messages.getProperty("NOT_CORRECT_PRICE_PRODUCT"));

        if (id_category <= 0)
            throw new ValidationException(messages.getProperty("NOT_CORRECT_ID_CATEGORY"));

        if (id_producer <= 0)
            throw new ValidationException(messages.getProperty("NOT_CORRECT_ID_PRODUCER"));

    }
}
