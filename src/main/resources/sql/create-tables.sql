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

/*Создание таблицы producers - производители товаров*/
CREATE TABLE IF NOT EXISTS producers(
	id 		SERIAL 			NOT NULL,			   		-- первичный ключ, автоинкремент
	name    VARCHAR(255) 	NOT NULL UNIQUE,  	   		-- название производителя

	CONSTRAINT producer_id PRIMARY KEY (id)
);

/*Создание таблицы goods - товары*/
CREATE TABLE IF NOT EXISTS goods(
	id 			SERIAL 			NOT NULL,			   		-- первичный ключ, автоинкремент
	name    	VARCHAR(255) 	NOT NULL UNIQUE,  	  		-- наименование товара
	price   	REAL			NOT NULL DEFAULT 0.0,		-- цена
	description TEXT,										-- описание
	in_storage  BOOLEAN			NOT NULL DEFAULT TRUE,		-- есть ли на складе
	id_category INTEGER			NOT NULL,					-- категория товара
	id_producer INTEGER			NOT NULL,					-- производитель товара

	CONSTRAINT goods_id PRIMARY KEY (id),

	CONSTRAINT price_check CHECK (price >= 0.0),

	CONSTRAINT unique_goods UNIQUE (name, price, id_category, id_producer),

	CONSTRAINT fkey_category_in_goods FOREIGN KEY (id_category) REFERENCES categories (id)
	ON DELETE NO ACTION ON UPDATE CASCADE,

	CONSTRAINT fkey_producer_in_goods FOREIGN KEY (id_producer) REFERENCES producers (id)
	ON DELETE NO ACTION ON UPDATE CASCADE
);