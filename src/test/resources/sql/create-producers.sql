/*Создание таблицы producers - производители товаров*/
CREATE TABLE IF NOT EXISTS producers(
	id 		SERIAL 			NOT NULL,			   		-- первичный ключ, автоинкремент
	name    VARCHAR(255) 	NOT NULL UNIQUE,  	   		-- название производителя

	CONSTRAINT producer_id PRIMARY KEY (id)
);