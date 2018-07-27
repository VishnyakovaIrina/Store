package home.ivishnyakova.store.entity;

import home.ivishnyakova.store.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Optional;
import java.util.Properties;

/* Класс Producer описывает производителя товара.
*
* Автор: Вишнякова И.Н.
* */
@Component
@XmlRootElement(name = "producer")
public class Producer implements Serializable, Validation{

    private int id;         //первичный ключ
    private String name;    //название

    public Producer() {}

    public Producer(int id, String name) {
        this.id = id;
        setName(name);
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

    @Override
    public String toString() {
        return "Producer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                "}\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!Optional.ofNullable(obj).isPresent() || getClass() != obj.getClass()) return false;

        Producer producer = (Producer) obj;

        if (id != producer.id) return false;
        return name.equals(producer.name);
    }

    @Override
    public void validate(Properties messages) throws ValidationException {
        if (!Optional.ofNullable(name).isPresent() || (Optional.ofNullable(name).isPresent() && name.isEmpty()))
            throw new ValidationException(messages.getProperty("NULL_EMPTY_NAME_PRODUCER"));
    }
}
