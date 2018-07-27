<%@ page language="java" contentType="text/html; charset=utf8; application/json" pageEncoding="utf8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
    <head>
        <title>Интернет-магазин</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta http-equiv="Content-Type" content="application/json; text/html; charset=UTF-8" />

        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
        <link rel="icon" href="${pageContext.request.contextPath}/resources/img/ua.jpg">
    </head>

    <body>
        <div id="container">
            <div id="header">
                <h1>Интернет-магазин</h1>
            </div>

            <div id="menuFilter">
                <a href="/store/getProducers/">Администрирование</a>
            </div>

            <div>
                <form action="${pageContext.request.contextPath}/" method="get">
                    <label id="headerFilter" class="Label"><h3>Фильтр товаров</h3></label>
                    <input class="buttonForm" type="submit" value="Очистить">
                </form>
            </div>

            <div id="filter">
                <form id="formFilterGoods" title="Критерии отбора товаров"
                action="${pageContext.request.contextPath}/getGoodsByFilter"
                modelAttribute="goodsFilter" method="get">

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
                    </div>

                    <div align="right">
                        <input class="buttonForm" type="submit" value="Применить">
                    </div>

                </form>
            </div>

            <div align="center">
                <h3>Номенклатура товаров</h3>
            </div>

            <div id="content">
                <c:if test="${empty goodsList}">
                    <div align="center">
                        <h3>Нет товаров</h3>
                    </div>
                </c:if>

                <c:if test="${not empty goodsList}">
                    <table id="tFilterGoods">
                        <thead>
                            <tr>
                                <th id="idFilterCol">Код</th>
                                <th id="nameFilterCol">Наименование</th>
                                <th id="priceFilterCol">Цена, грн.</th>
                                <th id="categoryFilterCol">Категория</th>
                                <th id="producerFilterCol">Производитель</th>
                                <th id="storageFilterCol">На складе</th>
                                <th id="descFilterCol">Описание</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="goods" items="${goodsList}">
                                <tr>
                                    <td>${goods.id}</td>
                                    <td>${goods.name}</td>
                                    <td>${goods.price}</td>
                                    <td>
                                        <c:forEach var="category" items="${categoriesList}">
                                            <c:if test="${category.id == goods.id_category}">
                                                <c:out value="${category.name}" />
                                            </c:if>
                                        </c:forEach>
                                    </td>
                                    <td>
                                        <c:forEach var="producer" items="${producersList}">
                                            <c:if test="${producer.id == goods.id_producer}">
                                                <c:out value="${producer.name}" />
                                            </c:if>
                                        </c:forEach>
                                    </td>
                                    <td>
                                        <c:if test="${goods.in_storage}">
                                            <c:out value="+" />
                                        </c:if>
                                    </td>
                                    <td class="descCell">${goods.description}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </body>
</html>
