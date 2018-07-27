/*Запросы для добавления товаров*/
/*Категория "Холодильники"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Холодильник Saturn ST-CF2949', 2300, 'Однокамерный холодильник объемом 46 л. Класс энергопотребления - А+', 7, 9);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Холодильник Samsung R-V660PUC3KTWH', 18500, 'Холодильная камера - 405 л, морозильная камера - 145 л. Класс энергопотребления - А++', false, 7, 1);

/*Категория "Стиральные машинки"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Стиральная машина Siemens SM-DS29573', 9570, 'Загрузка - фронтальная. Объем загрузки - 8 кг', 8, 4);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Стиральная машина LG LG-LE32573', 12300, 'Загрузка - фронтальная. Объем загрузки - 10 кг', false, 8, 7);

/*Категория "Газовые плиты"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Плита газовая Gorenje K 5341 WF-B', 7980, 'Плита комбинированная с электронным управлением', 9, 5);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Плита газовая Bosch HGD625255Q', 12200, 'Плита комбинированная с механическим управлением', false, 9, 3);

/*Категория "Смартфоны"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Смартфон Samsung J400 Galaxy J4 Black', 4670, 'Диагональ дисплея: 5,5; Разрешение экрана: 1280x720; Тип экрана: Super Amoled', 10, 1);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Смартфон Samsung J600 Galaxy J6 Gold', 6300, 'Диагональ дисплея: 5,6; Разрешение экрана: 1480х720; Тип экрана: Super Amoled', false, 10, 1);

/*Категория "Телефоны"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Телефон шнуровой Panasonic KX-TS 2350 UAJ', 450, 'Проводной телефон', 11, 10);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Телефон шнуровой Panasonic KX-TS 2350 UAS', 530, 'Проводной телефон', false, 11, 10);

/*Категория "Зарядные устройства"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Сетевое зарядное устройство SAMSUNG QX BK', 540, 'Питание - электросеть 220В', 12, 1);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Сетевое зарядное устройство SAMSUNG QX SW', 650, 'Автомобильное, Питание - 12В', false, 12, 1);

/*Категория "Наушники"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Наушники вкладыши Philips SHB5250BK/00', 1200, 'Вкладыши в ушную раковину', 13, 8);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Наушники Samsung Mode Black (4090939)', 1300, 'Вкладыши в ушную раковину', false, 13, 1);

/*Категория "Телевизоры"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Телевизор Samsung UE40MU6100UXUA', 19400, 'LED-телевизор, диагональ 40 дюймов, матрица LED', 14, 1);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Телевизор LG 32LK615BPLB', 10300, 'LED-телевизор, диагональ 32 дюйма, матрица LED', false, 14, 7);

/*Категория "Домашние кинотеатры"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('LG SH4D', 6700, 'Количество акустических каналов: 2.1. Тип: Саундбары. Разъемы и интерфейсы: USB,  Bluetooth,  Цифровой оптический вход. Общая мощность звука: 300 Вт', 16, 7);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Samsung HW-M4500/RU', 7700, 'Количество акустических каналов: 2.1. Тип: Саундбары. Разъемы и интерфейсы: HDMI,  USB,  Bluetooth,  Wi-Fi,  Цифровой оптический вход,  AUX. Общая мощность звука: 260 Вт', false, 16, 1);

/*Категория "Ноутбуки"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Ноутбук Asus ZenBook 3 UX390UA (UX390UA-GS042R) Royal Blue', 27000, 'Экран 12.5" IPS (1920x1080) Full HD LED, глянцевый / Intel Core i5-7200U (2.5 - 3.1 ГГц) / RAM 8 ГБ / SSD 256 ГБ / Intel HD Graphics 620 / без ОД / Wi-Fi / веб-камера / Windows 10 Pro / 910 г / синий', 19, 6);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Ноутбук Lenovo IdeaPad 320-15IKB (80XL03GXRA) Onyx Black', 13000, 'Экран 15.6" (1920x1080) Full HD, матовый / Intel Pentium 4415U (2.3 ГГц) / RAM 8 ГБ / SSD 256 ГБ / nVidia GeForce GT 940MX, 2 ГБ / без ОД / LAN / Wi-Fi / Bluetooth / веб-камера / DOS / 2.2 кг / черный', false, 19, 2);

/*Категория "Системные блоки"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Lenovo IdeaCentre 300 (90DN0043UL)', 6500, 'Intel Pentium J3710 (1.6 - 2.64 ГГц) / RAM 4 ГБ / HDD 500 ГБ / Intel HD Graphics 405 / DVD+/-RW / LAN / DOS', 20, 2);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Asus 285 G2 MT (V7R10EA)', 9900, 'AMD A6-6400B (3.9 ГГц) / RAM 4 ГБ / HDD 1 ТБ / AMD Radeon HD8470D / DVD Super Multi / LAN / DOS / мышь', false, 20, 6);

/*Категория "Мониторы"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Монитор 24" Samsung S24D300HS', 4500, 'Тип матрицы: TN. Интерфейсы: VGA,  HDMI. Время реакции матрицы: 2 мс. Яркость дисплея: 250 кд/м2', 21, 1);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Монитор 23" Philips 237E7QDSB', 4300, 'Тип матрицы: AH-IPS. Интерфейсы: DVI,  VGA,  HDMI. Время реакции матрицы: 5мс (G to G). Яркость дисплея: 250 кд/м2', false, 21, 8);

/*Категория "Планшеты"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Планшет Asus ZenPad 10 3/32GB LTE Dark Gray (Z301ML-1H033A)', 6570, 'Экран 10.1" IPS (1280x800) емкостный MultiTouch / MediaTek MT8735W (1.3 ГГц) / RAM 3 ГБ / 32 ГБ встроенной памяти + microSD / 3G / LTE / Wi-Fi 802.11 a/b/g/n / Bluetooth 4.2 / основная камера 5 Мп, фронтальная - 2 Мп / GPS / ГЛОНАСС / ОС Android 7.0 / 490 г / темно-серый', 22, 6);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Планшет Asus ZenPad 10 2/16GB LTE Dark Gray (Z301MFL-1H011A)', 6800, 'Экран 10.1" IPS (1920x1080) емкостный MultiTouch / MediaTek MT8735А (1.45 ГГц) / RAM 2 ГБ / 16 ГБ встроенной памяти + microSD / 3G / LTE / Wi-Fi 802.11 a/b/g/n / Bluetooth 4.1 / основная камера 5 Мп, фронтальная - 2 Мп / GPS / ГЛОНАСС / ОС Android 6.0 / 490 г / темно-серый', false, 22, 6);

/*Категория "Блендеры"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Блендер BOSCH MSM671X0', 3770, 'Емкость стакана: 700 мл. Мощность: 750 Вт. Тип: Погружной. Материал ножки блендера: Металл. Мини-измельчитель: Есть. Кол-во скоростей: 12 + турбо-режим', 23, 3);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Блендер SATURN ST-FP9091', 1200, 'Емкость стакана: 600 мл. Мощность: 1000 Вт. Тип: Погружной. Материал ножки блендера: Металл. Мини-измельчитель: Нет. Кол-во скоростей: 2', false, 23, 9);

/*Категория "Варочные панели"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Варочная поверхность электрическая BOSCH PKF 645 CA', 7200, 'Количество конфорок: 4. Тип нагревательных элементов (конфорки): Hi-Light. Материал поверхности: Стеклокерамика. Управление: Поворотные переключатели. Вид поверхности: Классическая', 24, 3);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Варочная поверхность электрическая GORENJE ISC 635 CSC', 10300, 'Количество конфорок: 4. Тип нагревательных элементов (конфорки): Индукционные. Материал поверхности: Стеклокерамика. Управление: Сенсор-слайдер. Вид поверхности: Классическая', false, 24, 5);

/*Категория "Весы кухонные"*/
INSERT INTO goods(name,price,description,id_category,id_producer)
VALUES('Весы кухонные SATURN ST-KS7803 Black', 278, 'Тип: электронные. Конструкция: съемная чаша. Максимальная нагрузка: 5 кг. Материал платформы: пластик. Материал чаши: пластик. Дополнительные функции: выбор единиц измерения, обнуление тары.', 25, 9);
INSERT INTO goods(name,price,description,in_storage,id_category,id_producer)
VALUES('Весы кухонные SATURN ST-KS7235 White', 200, 'Тип: электронные. Конструкция: плоская платформа. Максимальная нагрузка: 5 кг. Материал платформы: стекло. Дополнительные функции: обнуление тары.', false, 25, 9);
