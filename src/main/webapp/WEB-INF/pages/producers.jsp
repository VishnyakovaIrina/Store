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

  </head>

     <body>
        <div id="container">
            <div id="header">
                <h1>Администрирование производителей товаров</h1>
            </div>
            <div id="menuProducer">
                <a href="/store/getProducers">Производители</a>
                <a href="/store/getCategories">Категории</a>
                <a href="/store/getGoods">Товары</a>
                <a href="/store">В магазин</a>
            </div>
        </div>

        <div>
            <div id="addProducerRegion">

                <div id="headerAddProducer">
                    <h3>Добавление нового производителя</h3>
                </div>
                    <form id="formNewProducer"
                        action="${pageContext.request.contextPath}/newProducer"
                        modelAttribute="producer"
                        method="post" >

                       <label id="labelNameProducer" class="Label">Название</label>
                       <input id="name" class="inputProducerName" name="name" type="text"
                       value="" required>
                       <input id="btnAddProducer" type="submit" value="Добавить">

                    </form>
            </div>

            <div id="dataProducers" >
                <c:if test="${not empty producersList}">
                    <table id="tProducers">
                        <thead>
                        <tr>
                            <th id="idHeaderProducerCol">Код</th>
                            <th id="nameHeaderProducerCol">Название</th>
                            <th id="controlHeaderProducerCol" colspan="2">Управление</th>
                        </tr>
                        </thead>
                        <tbody>

                            <c:forEach var="producer" items="${producersList}">
                                <tr>
                                    <td id="idProducerCol">${producer.id}</td>

                                    <td id="nameProducerCol">
                                        <form id="formUpdateProducer"
                                        action="${pageContext.request.contextPath}/updateProducer/${producer.id}"
                                        modelAttribute="producer"
                                        method="post">
                                           <input id="name" class="inputProducerName" name="name" type="text"
                                           value="${producer.name}" required>
                                    </td>

                                    <td id="saveProducerCol">
                                       <input id="btnUpdateProducer" type="submit" value="Сохранить">
                                    </td>
                                        </form>

                                    <td id="deleteProducerCol">
                                       <form action="${pageContext.request.contextPath}/deleteProducer/${producer.id}"
                                       method="post">
                                           <input id="btnDeleteProducer" type="submit" value="Удалить">
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