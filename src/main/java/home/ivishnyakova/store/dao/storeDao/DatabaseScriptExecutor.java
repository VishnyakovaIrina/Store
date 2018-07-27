package home.ivishnyakova.store.dao.storeDao;

import home.ivishnyakova.store.dao.SqlScripts;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.message.ErrorMessage;
import home.ivishnyakova.store.message.ErrorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/*Класс DatabaseScriptExecutor предназначен выполнения sql-скриптов,
расположенных в файлах.
Класс используется для подготовки базы данных перед запуском приложения,
* которая включает в себя следующие действия:
* - удаление таблиц, если они существуют в БД;
* - создание таблиц;
* - заполнение таблиц данными.
*
* Автор: Вишнякова И.
* */
@Component
public class DatabaseScriptExecutor {

    //для выполнения sql-скриптов
    @Autowired
    private DataSource dataSource;

    //описания ошибок
    @Resource(name="errors")
    private ErrorProperties errors;

    public DatabaseScriptExecutor(){}

    /*Метод выполняет sql-скрипты, определенные в файлах из списка scripts.
    * В результате в БД будут созданы таблицы и заполнены данными из скриптов.
    * @throws файл с sql-скриптами не существует или в sql-скрипте - ошибка.
    * Метод вызывается автоматически после создания бина, поскольку помечен как init.*/
    public void init(){
        executeScripts(SqlScripts.INIT_TABLES.getScripts());
    }

    /*Метод выполняет sql-скрипты, определенные в файлах из списка scripts.
    * @throws файл с sql-скриптами не существует или в sql-скрипте - ошибка.
    */
    public void executeScripts(List<String> scripts){
        try {
            ResourceDatabasePopulator action = new ResourceDatabasePopulator();
            action.setSqlScriptEncoding("UTF-8");
            scripts.forEach(script -> action.addScript(new ClassPathResource(script)));
            DatabasePopulatorUtils.execute(action, dataSource);

        }catch (DataAccessException e){
            ErrorMessage errorMessage = errors.getErrorMessage("NO_SCRIPT");
            String oldMessage = errorMessage.getMessage();
            errorMessage.setMessage(oldMessage + scripts);
            throw new StoreException(errorMessage);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ErrorProperties getErrors() {
        return errors;
    }

    public void setErrors(ErrorProperties errors) {
        this.errors = errors;
    }
}
