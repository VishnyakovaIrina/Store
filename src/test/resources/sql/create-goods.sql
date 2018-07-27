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