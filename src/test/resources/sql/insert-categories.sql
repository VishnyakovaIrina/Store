/*Запросы для добавления категорий*/
--корень дерева
INSERT INTO categories(name) VALUES(' ');

INSERT INTO categories(name,no_level,id_category) VALUES('Бытовая техника',1,1);
INSERT INTO categories(name,no_level,id_category) VALUES('Смартфоны и телефоны',1,1);
INSERT INTO categories(name,no_level,id_category) VALUES('ТВ, аудио, видео',1,1);
INSERT INTO categories(name,no_level,id_category) VALUES('Ноутбуки, планшеты, компьютеры',1,1);
INSERT INTO categories(name,no_level,id_category) VALUES('Посуда, товары для дома и отдыха',1,1);

/*Добавление в категорию "Бытовая техника"*/
INSERT INTO categories(name,no_level,id_category) VALUES('Холодильники',2,2);
INSERT INTO categories(name,no_level,id_category) VALUES('Стиральные машины',2,2);
INSERT INTO categories(name,no_level,id_category) VALUES('Газовые плиты',2,2);

/*Добавление в категорию "Смартфоны и телефоны"*/
INSERT INTO categories(name,no_level,id_category) VALUES('Смартфоны',2,3);
INSERT INTO categories(name,no_level,id_category) VALUES('Телефоны',2,3);
INSERT INTO categories(name,no_level,id_category) VALUES('Зарядные устройства',2,3);
INSERT INTO categories(name,no_level,id_category) VALUES('Наушники',2,3);

/*Добавление в категорию "ТВ, аудио, видео"*/
INSERT INTO categories(name,no_level,id_category) VALUES('Телевизоры',2,4);
INSERT INTO categories(name,no_level,id_category) VALUES('Аккустика',2,4);
INSERT INTO categories(name,no_level,id_category) VALUES('Домашние кинотеатры',2,4);
INSERT INTO categories(name,no_level,id_category) VALUES('DVD/HD-медиаплееры',2,4);
INSERT INTO categories(name,no_level,id_category) VALUES('Аксессуары',2,4);

/*Добавление в категорию "Ноутбуки, планшеты, компьютеры"*/
INSERT INTO categories(name,no_level,id_category) VALUES('Ноутбуки',2,5);
INSERT INTO categories(name,no_level,id_category) VALUES('Системные блоки',2,5);
INSERT INTO categories(name,no_level,id_category) VALUES('Мониторы',2,5);
INSERT INTO categories(name,no_level,id_category) VALUES('Планшеты',2,5);

/*Добавление в категорию "Посуда, товары для дома и отдыха"*/
INSERT INTO categories(name,no_level,id_category) VALUES('Блендеры',2,6);
INSERT INTO categories(name,no_level,id_category) VALUES('Варочные панели',2,6);
INSERT INTO categories(name,no_level,id_category) VALUES('Весы кухонные',2,6);

INSERT INTO categories(name,no_level,id_category) VALUES('Бытовая химия',1,1);
