<%@ page language="java" contentType="text/html; charset=utf8; application/json" pageEncoding="utf8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
    <head>
        <title>Администрирование</title>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta http-equiv="Content-Type" content="application/json; text/html; charset=UTF-8" />

        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
        <link rel="icon" href="${pageContext.request.contextPath}/resources/img/ua.jpg">

        <script>
            function viewdiv(id, link) {
                var el = document.getElementById(id);
                var link = document.getElementById(link);
                if (el.style.display == "block") {
                    el.style.display = "none";
                    link.innerText = link.getAttribute('data-text-hide');
                } else {
                    el.style.display = "block";
                    link.innerText = link.getAttribute('data-text-show');
                }
            }
        </script>

    </head>

    <body>
        <div id="container">
            <div id="header">
                <h1>Администрирование товаров</h1>
            </div>
            <div id="menuProducer">
                <a href="/store/getProducers">Производители</a>
                <a href="/store/getCategories">Категории</a>
                <a href="/store/getGoods">Товары</a>
                <a href="/store">В магазин</a>
            </div>

            <%-- Блок добавления нового товара --%>
            <div id="addGoodsRegion">

                <div id="headerAddGoods">
                    <h3>Добавление нового товара</h3>
                </div>

                <table id="tAddGoods">
                    <tbody>
                        <tr>
                            <td>
                                <form id="formNewGoods"
                                action="${pageContext.request.contextPath}/newGoods"
                                modelAttribute="goods"
                                method="post">

                                    <label id="labelNameGoods" class="Label">Наименование товара</label>
                                    <input id="name" class="inputGoods" name="name" type="text"
                                    value="" required>

                                    <br>
                                    <label class="Label">Категория</label>
                                    <select id="id_category" name="id_category" class="inputGoods" required>
                                        <c:forEach var="category" items="${categoriesList}">
                                            <option value=${category.id}>
                                            ${category.name}
                                            </option>
                                        </c:forEach>
                                    </select>

                                    <br>
                                    <label class="Label">Производитель</label>
                                    <select id="id_producer" name="id_producer" class="inputGoods" required>
                                        <c:forEach var="producer" items="${producersList}">
                                            <option value=${producer.id}>
                                            ${producer.name}
                                            </option>
                                        </c:forEach>
                                    </select>

                                    <label class="Label">Цена</label>
                                    <input id="price" name="price" type="number" value="0.0" min="0.0" class="inputPriceGoods" required>

                                    <label class="Label">На складе</label>
                                    <input id="in_storage" name="in_storage" type="checkbox" checked>

                            </td>
                            <td>
                                    <label id="labelInputDesc">Описание</label>
                                    <div id="divInputDescGoods">
                                        <textarea id="description" name="description" class="inputDescriptionGoods">
                                        </textarea>
                                    </div>

                                    <div id="divBtnAddGoods">
                                        <input id="btnAddGoods" type="submit" value="Добавить">
                                    </div>
                            </td>
                        </tr>
                                </form>
                    </tbody>
                </table>
            </div>

            <%-- Блок фильтра товаров товаров --%>
            <a id="filterLink" href="javascript:void(0);" onclick="viewdiv('goodsFilterRegion','filterLink');" data-text-show="Скрыть фильтр" data-text-hide="Показать фильтр">Показать фильтр</a>
            <div id="goodsFilterRegion">
                <div>
                    <form action="${pageContext.request.contextPath}/getGoods" method="get">
                        <label id="headerFilter" class="Label"><h3>Фильтр товаров</h3></label>
                        <input class="buttonForm" type="submit" value="Очистить">
                    </form>
                </div>

                <div id="filter">
                    <form id="formFilterGoods" title="Критерии отбора товаров"
                    action="${pageContext.request.contextPath}/getFilterGoods"
                    modelAttribute="goodsFilter"
                    method="get">

                    <div>
                        <label class="Label">Категория</label>
                        <select id="filterIdCategory" name="filterIdCategory">
                            <option value="0">Все</option>
                            <c:forEach var="category" items="${categoriesList}">
                                <option value=${category.id}

                                <c:if test="${category.id == filterIdCategory}">
                                    <c:out value="selected" />
                                </c:if>

                                >${category.name}</option>
                            </c:forEach>
                        </select>

                        <label class="Label">Производитель</label>
                        <select id="filterIdProducer" name="filterIdProducer">
                            <option value="0">Все</option>
                            <c:forEach var="producer" items="${producersList}">
                                <option value=${producer.id}

                                <c:if test="${producer.id == filterIdProducer}">
                                    <c:out value="selected" />
                                </c:if>

                                >${producer.name}</option>
                            </c:forEach>
                        </select>

                        <label class="Label">Минимальная цена</label>
                        <input id="filterMinPrice" name="filterMinPrice" type="number"
                        value="${filterMinPrice}" min="0">

                        <label class="Label">Максимальная цена</label>
                        <input id="filterMaxPrice" name="filterMaxPrice" type="number"
                        value="${filterMaxPrice}" min="0">

                        <label class="Label">На складе</label>
                        <input id="filterInStorage" name="filterInStorage" type="checkbox"
                        <c:if test="${filterInStorage}">
                            <c:out value="checked" />
                        </c:if> >

                        <br>

                    </div>

                    <div align="right">
                        <input class="buttonForm" type="submit" value="Применить">
                    </div>
                    </form>
                </div>
            </div>

            <%-- Таблица товаров --%>
            <div id="dataGoods">
                <c:if test="${empty goodsList}">
                    <div align="center">
                        <h3>Нет товаров</h3>
                    </div>
                </c:if>
                <c:if test="${not empty goodsList}">
                    <table id="tGoods">
                        <thead>
                            <tr>
                                <th id="idHeaderGoodsCol">Код</th>
                                <th id="nameHeaderGoodsCol">Товар</th>
                                <th id="priceHeaderGoodsCol">Цена, грн.</th>
                                <th id="inStorageHeaderGoodsCol">На складе</th>
                                <th id="producerHeaderGoodsCol">Описание</th>
                                <th id="controlHeaderGoodsCol" colspan="2">Управление</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="goods" items="${goodsList}">
                                <tr>
                                    <td id="idGoodsCol">${goods.id}</td>

                                    <td id="nameGoodsCol">
                                        <form id="formUpdateGoods"
                                        action="${pageContext.request.contextPath}/updateGoods/${goods.id}"
                                        modelAttribute="goods"
                                        method="post">
                                            <p class="titleGoods">Наименование</p>
                                            <input id="name" class="editNameGoods" name="name" type="text" value="${goods.name}" required>

                                            <p class="titleGoods">Категория</p>
                                            <select id="id_category" name="id_category" class="editCategoryGoods" required>
                                                <c:forEach var="category" items="${categoriesList}">
                                                    <option value=${category.id}
                                                        <c:if test="${category.id == goods.id_category}">
                                                        <c:out value="selected" />
                                                        </c:if>
                                                    >${category.name}
                                                    </option>
                                                </c:forEach>
                                            </select>

                                            <p class="titleGoods">Производитель</p>
                                            <select id="id_producer" name="id_producer" class="editProducerGoods" required>
                                                <c:forEach var="producer" items="${producersList}">
                                                    <option value=${producer.id}
                                                        <c:if test="${producer.id == goods.id_producer}">
                                                        <c:out value="selected" />
                                                        </c:if>
                                                    >${producer.name}
                                                    </option>
                                                </c:forEach>
                                            </select>

                                    </td>

                                    <td id="priceGoodsCol">
                                        <input id="price" name="price" type="number" class="editPriceGoods" value="${goods.price}" min="0.0" required>
                                    </td>

                                    <td id="inStorageGoodsCol">
                                        <input id="in_storage" name="in_storage" type="checkbox" class="editInStorageGoods"
                                            <c:if test="${goods.in_storage}">
                                                <c:out value="checked"/>
                                            </c:if>
                                        >
                                    </td>

                                    <td>
                                        <textarea id="description" class="editDescriptionGoods" name="description">${goods.description}</textarea>
                                    </td>

                                    <td id="saveGoodsCol">
                                       <input id="btnUpdateGoods" type="submit" value="Сохранить">
                                    </td>

                                        </form>

                                    <td id="deleteGoodsCol">
                                       <form action="${pageContext.request.contextPath}/deleteGoods/${goods.id}"
                                       method="post">
                                           <input id="btnDeleteGoods" type="submit" value="Удалить">
                                       </form>
                                    </td>
                                </tr>

                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>
     </body>
</html>