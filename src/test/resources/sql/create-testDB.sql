/*Создание БД интернет-магазина teststore для тестирования*/
CREATE DATABASE teststore
	WITH
    OWNER = postgres
    ENCODING = 'UTF8'
	TABLESPACE = pg_default
	/* Проблема с локалями на разных компьютерах
	LC_COLLATE = 'Ukrainian_Ukraine.1251'
	LC_CTYPE = 'Ukrainian_Ukraine.1251'
	*/
	;