/*Создание таблицы categories - категории товаров*/
CREATE TABLE IF NOT EXISTS categories(
	id 			SERIAL 			NOT NULL,			   		-- первичный ключ, автоинкремент
	name    	VARCHAR(255) 	NOT NULL UNIQUE,  	   		-- название категории
	id_category INTEGER 		DEFAULT NULL,				-- категория товара	(рекурсивная связь)
	no_level	SMALLINT		NOT NULL DEFAULT 0,			-- уровень вложенности категории в дереве [0;5]

	CONSTRAINT category_id PRIMARY KEY (id),

	CONSTRAINT no_level_check CHECK (no_level >= 0 AND no_level <= 5),

	CONSTRAINT fkey_category_in_categories FOREIGN KEY (id_category) REFERENCES categories (id)
	ON DELETE CASCADE ON UPDATE CASCADE
);