package home.ivishnyakova.store.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*Перечисление SqlScripts содержит имена файлов-sql скриптов.
*
* Автор: Вишнякова И.
* */
public enum SqlScripts {
    DROP_PRODUCERS_TABLE(Collections.singletonList("/sql/drop-producers.sql")),
    DROP_GOODS_TABLE(Collections.singletonList("/sql/drop-goods.sql")),
    DROP_CATEGORIES_TABLE(Collections.singletonList("/sql/drop-categories.sql")),
    DROP_TABLES(Collections.singletonList("/sql/drop-tables.sql")),

    CREATE_PRODUCERS_TABLE(Collections.singletonList("/sql/create-producers.sql")),
    CREATE_GOODS_TABLE(Collections.singletonList("/sql/create-goods.sql")),
    CREATE_CATEGORIES_TABLE(Collections.singletonList("/sql/create-categories.sql")),
    CREATE_TABLES(Collections.singletonList("/sql/create-tables.sql")),

    CLEAR_PRODUCERS_TABLE(Collections.singletonList("/sql/clear-producers.sql")),
    CLEAR_GOODS_TABLE(Collections.singletonList("/sql/clear-goods.sql")),
    CLEAR_CATEGORIES_TABLE(Collections.singletonList("/sql/clear-categories.sql")),

    CLEAR_TABLES(Arrays.asList("/sql/clear-goods.sql",
                               "/sql/clear-categories.sql",
                               "/sql/clear-producers.sql")),

    INSERT_DATA_PRODUCERS_TABLE(Collections.singletonList("/sql/insert-producers.sql")),
    INSERT_DATA_GOODS_TABLE(Collections.singletonList("/sql/insert-goods.sql")),
    INSERT_DATA_CATEGORIES_TABLE(Collections.singletonList("/sql/insert-categories.sql")),
    INSERT_DATA_TABLES(Collections.singletonList("/sql/insert-data.sql")),

    INIT_TABLES(Arrays.asList("/sql/drop-tables.sql",
                              "/sql/create-tables.sql",
                              "/sql/insert-data.sql"));

    private List<String> scripts;

    SqlScripts(List<String> scripts){
        this.scripts = scripts;
    }

    public List<String> getScripts() {
        return scripts;
    }
}
