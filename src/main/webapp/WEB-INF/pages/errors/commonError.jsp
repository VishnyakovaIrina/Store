<%@ page language="java" contentType="text/html; charset=utf8; application/json" pageEncoding="utf8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
  <head>
    <title>Ошибка</title>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="Content-Type" content="application/json; text/html; charset=UTF-8" />

        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
        <link rel="icon" href="${pageContext.request.contextPath}/resources/img/ua.jpg">

  </head>

<body>

    <div class="errorInfo">

        <c:if test="${not empty errorMessage.code}">
            <h3 id="errorCode">Error #${errorMessage.code}</h3>
        </c:if>
        <c:if test="${not empty errorMessage.message}">
            <h3>${errorMessage.message}</h3>
        </c:if>

        <c:if test="${not empty errorMessage.cause}">
            <div id="errorCause">
                <h3>Possible causes of error:</h3><br>

                <c:forEach var="cause" items="${errorMessage.cause}">
                    <h4>${cause}</h4><br>
                </c:forEach>
            </div>
        </c:if>

        <c:if test="${empty errorMessage.message}">
            <h3>Undefined software error</h3>
        </c:if>

        <br>
        <c:if test="${not empty url}">
            <a href="${pageContext.request.contextPath}/${url}"> Вернуться назад </a>
        </c:if>

    </div>

</body>


</html>