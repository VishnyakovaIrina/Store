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
                <h1>Администрирование категорий товаров</h1>
            </div>
            <div id="menuProducer">
                <a href="/store/getProducers">Производители</a>
                <a href="/store/getCategories">Категории</a>
                <a href="/store/getGoods">Товары</a>
                <a href="/store">В магазин</a>
            </div>
        </div>

        <div>
            <div class="headerCategory">
                <table id="tAddCategory">
                    <tbody>
                        <tr>
                            <td>
                                <div class="headerCategory">
                                    <h3>Добавление новой категории</h3>
                                </div>
                            </td>
                            <td>
                                <div class="headerCategory">
                                    <h3>Добавление новой подкатегории</h3>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div id="addCategoryRegion">
                                    <form id="formNewCategory"
                                    action="${pageContext.request.contextPath}/newCategory"
                                    modelAttribute="category"
                                    method="post" >

                                        <label id="labelNameCategory" class="LabelNameCategory">Название</label>
                                        <input id="name" class="inputCategory" name="name" type="text"
                                        value="" required>
                                        <div align="right">
                                            <input id="btnAddCategory" class="btnCategory" type="submit" value="Добавить">
                                        </div>

                                    </form>
                                </div>
                            </td>
                            <td>
                                <div id="addSubCategoryRegion">
                                    <form id="formNewSubCategory"
                                    action="${pageContext.request.contextPath}/newSubCategory"
                                    modelAttribute="subCategory"
                                    method="post" >

                                        <label id="labelNameSubCategory" class="LabelNameCategory">Название</label>
                                        <input id="name" class="inputCategory" name="name" type="text"
                                        value="" required>

                                        <br>
                                        <label class="LabelNameCategory">Категория</label>
                                        <select id="id_category" name="id_category" class="inputGoods" required>
                                            <c:forEach var="category" items="${categoriesList}">
                                                <option value=${category.id}>
                                                    ${category.name}
                                                </option>
                                            </c:forEach>
                                        </select>

                                        <div align="right">
                                            <input id="btnAddSubCategory" class="btnCategory" type="submit" value="Добавить">
                                        </div>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <table id="tDataCategories">
                <tr>
                    <td>
                        <div class="headerCategory">
                            <h3>Категории</h3>
                        </div>
                    </td>
                    <td>
                        <c:if test="${not empty subCategoriesList}">
                            <div align="center" class="headerCategory">
                                <h3>Подкатегории</h3>
                            </div>
                        </c:if>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div id="dataCategories" >
                            <c:if test="${empty categoriesList}">
                                    <div align="center">
                                        <h3>Нет категорий</h3>
                                    </div>
                            </c:if>
                            <c:if test="${not empty categoriesList}">
                                <table id="tCategories">
                                    <thead>
                                    <tr>
                                        <th id="idHeaderCategoryCol">Код</th>
                                        <th id="nameHeaderCategoryCol">Название</th>
                                        <th id="controlHeaderCategoryCol" colspan="3">Управление</th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                        <c:forEach var="category" items="${categoriesList}">
                                            <tr>
                                                <td class="idCategoryCol">${category.id}</td>

                                                <td id="nameCategoryCol">
                                                    <form id="formUpdateCategory"
                                                    action="${pageContext.request.contextPath}/updateCategory/${category.id}"
                                                    modelAttribute="category"
                                                    method="post">
                                                       <input id="name" class="inputCategory" name="name" type="text"
                                                       value="${category.name}" required>
                                                </td>

                                                <td id="saveCategoryCol">
                                                   <input id="btnUpdateCategory" class="btnCategory" type="submit" value="Сохранить">
                                                </td>

                                                    </form>
                                                <td id="deleteCategoryCol">
                                                   <form action="${pageContext.request.contextPath}/deleteCategory/${category.id}"
                                                   method="post">
                                                       <input id="btnDeleteCategory" class="btnCategory" type="submit" value="Удалить">
                                                   </form>
                                                </td>

                                                <td id="subCategoryCol">
                                                    <form id="formSubCategory"
                                                    action="${pageContext.request.contextPath}/getSubCategories"
                                                    modelAttribute="category"
                                                    method="get">
                                                       <input type="text" id="id" name="id" value="${category.id}" hidden>
                                                       <input id="btnSubCategory" class="btnCategory" type="submit" value="Подкатегории">
                                                   </form>
                                                </td>
                                                </form>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                        </div>
                    </td>
                    <td>
                        <div id="dataSubCategories">
                            <c:if test="${not empty subCategoriesList}">
                                <table id="tSubCategories">
                                    <thead>
                                        <tr>
                                            <th id="idHeaderSubCategoryCol">Код</th>
                                            <th id="nameHeaderSubCategoryCol">Подкатегории</th>
                                            <th id="controlHeaderSubCategoryCol" colspan="2">Управление</th>
                                        </tr>
                                    </thead>
                                    <tbody>

                                        <c:forEach var="subCategory" items="${subCategoriesList}">
                                            <tr>
                                                <td class="idCategoryCol">${subCategory.id}</td>

                                                <td id="nameSubCategoryCol">
                                                    <form id="formUpdateSubCategory"
                                                    action="${pageContext.request.contextPath}/updateSubCategory/${subCategory.id}"
                                                    modelAttribute="subCategory"
                                                    method="post">
                                                        <p class="titleCategories">Название подкатегории</p>
                                                        <input id="name" class="inputCategory" name="name" type="text"
                                                        value="${subCategory.name}" required>

                                                        <br>
                                                        <p class="titleCategories">К какой категории принадлежит</p>
                                                        <select id="id_category" name="id_category" class="inputCategory" required>
                                                            <c:forEach var="category" items="${categoriesList}">
                                                                <option value=${category.id}
                                                                    <c:if test="${subCategory.id_category == category.id}">
                                                                    <c:out value="selected" />
                                                                    </c:if>
                                                                >${category.name}
                                                                </option>
                                                            </c:forEach>
                                                        </select>
                                                </td>

                                                <td id="saveSubCategoryCol">
                                                   <input id="btnUpdateSubCategory" class="btnCategory" type="submit" value="Сохранить">
                                                </td>
                                                    </form>

                                                <td id="deleteSubCategoryCol">
                                                   <form action="${pageContext.request.contextPath}/deleteSubCategory/${subCategory.id}"
                                                   method="post">
                                                       <input id="btnDeleteSubCategory" class="btnCategory" type="submit" value="Удалить">
                                                   </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
     </body>
</html>