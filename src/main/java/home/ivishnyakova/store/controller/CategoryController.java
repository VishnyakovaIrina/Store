package home.ivishnyakova.store.controller;

import home.ivishnyakova.store.dao.CategoryDao;
import home.ivishnyakova.store.entity.Category;
import home.ivishnyakova.store.exceptions.StoreException;
import home.ivishnyakova.store.exceptions.ValidationException;
import home.ivishnyakova.store.message.ErrorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Optional;

import static home.ivishnyakova.store.utils.CategoryLevel.CATEGORY;
import static home.ivishnyakova.store.utils.CategoryLevel.SUB_CATEGORY;

/* Класс CategoryController является контроллером для веб-страницы администрирования
* категорий товаров.
*
* Автор: Вишнякова И.
* */
@Controller
public class CategoryController {

    //для доступа к инфо про категории
    @Autowired
    private CategoryDao categoryDao;

    //для представления описания ошибок на веб-странице
    @Autowired
    private ViewHandlerException viewHandlerException;

    //описание ошибок
    @Resource(name="errors")
    private ErrorProperties errors;

    //относит. название web-страниц (ответы контроллера)
    private static final String PAGE_CATEGORIES = "categories";
    private static final String PAGE_REDIRECT_CATEGORIES = "redirect:/getCategories/";
    private static final String PAGE_REDIRECT_SUBCATEGORIES_ID = "redirect:/getSubCategories?id=";
    private static final String PAGE_REDIRECT_SUBCATEGORIES = "redirect:/getSubCategories";
    private static final String ERROR_PAGE_CATEGORIES = "/getCategories";

    //относит. название web-страниц, которые обрабатывает контроллер
    private static final String GET_CATEGORIES = "/getCategories";
    private static final String GET_SUBCATEGORIES = "/getSubCategories";
    private static final String NEW_CATEGORY = "/newCategory";
    private static final String NEW_SUBCATEGORY = "/newSubCategory";
    private static final String UPDATE_CATEGORY = "/updateCategory/{id}";
    private static final String UPDATE_SUBCATEGORY = "/updateSubCategory/{id}";
    private static final String DELETE_CATEGORY = "/deleteCategory/{id}";
    private static final String DELETE_SUBCATEGORY = "/deleteSubCategory/{id}";


    /*Метод getCategories подготавливает данные о всех категориях.
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @returns  В случае успешного получение списка категорий будет загружена страница для
    *           администрирования категорий (categories.jsp).
    *           В противном случае, страница с описание возникшей ошибки.
    * */
    @RequestMapping(value = GET_CATEGORIES, method = RequestMethod.GET)
    public String getCategories (ModelMap model){
        model.addAttribute("categoriesList", categoryDao.getCategoryListByLevel(CATEGORY.getLevel()));
        return PAGE_CATEGORIES;
    }

    /*Метод getCategoriesById подготавливает данные о всех категориях и
    * подкатегориях (для указанной категории category).
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @param    category - категория, для кот. следует найти подкатегории;
    * @returns  В случае успешного получение списка категорий будет загружена страница для
    *           администрирования категорий (categories.jsp).
    *           В противном случае, страница с описание возникшей ошибки.
    * */
    @RequestMapping(value = GET_SUBCATEGORIES, method = RequestMethod.GET)
    public String getCategoriesById (ModelMap model, @ModelAttribute("category") Category category){

        //проверка: указана ли категория, для кот. следует найти подкатегории
        Category categoryOpt = Optional.ofNullable(category)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NO_CATEGORY_FOR_SUBCATEGORIES")));

        model.addAttribute("categoriesList", categoryDao.getCategoryListByLevel(CATEGORY.getLevel()));
        model.addAttribute("subCategoriesList", categoryDao.getCategoryListById(categoryOpt.getId(), SUB_CATEGORY.getLevel()));

        return PAGE_CATEGORIES;
    }

    /*Метод addNewCategory выполняет добавление категории category в БД.
    * @param    category - категория, кот. будет добавляться в БД;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда category = null или содержит некорректные данные;
    * @returns  В случае успешного добавления новой категории будет загружена страница для
    *           администрирования категорий ("redirect:/getCategories/").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = NEW_CATEGORY, method = RequestMethod.POST)
    public String addNewCategory(@ModelAttribute("category") Category category, ModelMap modelMap){

        Optional.ofNullable(category)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_VALID_NEW_CATEGORY")));

        category.setId_category(1);
        category.setNo_level(CATEGORY.getLevel());

        try {
            if (!categoryDao.insert(category))
                throw new StoreException(errors.getErrorMessage("NOT_INSERTED_CATEGORY"));
        } catch (ValidationException e) {
            throw new StoreException(errors.getErrorMessage("NOT_VALID_NEW_CATEGORY"));
        }
        return PAGE_REDIRECT_CATEGORIES;
    }

    /*Метод addNewSubCategory выполняет добавление подкатегории subCategory в БД.
    * @param    subCategory - подкатегория, кот. будет добавляться в БД;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда subCategory = null или содержит некорректные данные;
    * @returns  В случае успешного добавления новой подкатегории будет загружена страница для
    *           администрирования категорий с отображением подкатегорий для ввбранной категории
    *           ("redirect:/getSubCategories?id=").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = NEW_SUBCATEGORY, method = RequestMethod.POST)
    public String addNewSubCategory(@ModelAttribute("subCategory") Category subCategory, ModelMap modelMap){

        Optional.ofNullable(subCategory)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_VALID_NEW_SUBCATEGORY")));

        subCategory.setNo_level(SUB_CATEGORY.getLevel());
        try {
            if (!categoryDao.insert(subCategory))
                throw new StoreException(errors.getErrorMessage("NOT_INSERTED_SUBCATEGORY"));
        } catch (ValidationException e) {
            throw new StoreException(errors.getErrorMessage("NOT_VALID_NEW_CATEGORY"));
        }

        return PAGE_REDIRECT_SUBCATEGORIES_ID + subCategory.getId_category();
    }

    /*Метод updateCategory выполняет обновление категории по id в БД.
    * @param    id категория, кот. будет обновляться в БД;
    * @param    category - новые данные категории;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда category = null или содержит некорректные данные;
    * @returns  В случае успешного обновления категории будет загружена страница для
    *           администрирования категорий ("redirect:/getSubCategories/") с отображением подкатегорий.
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = UPDATE_CATEGORY, method = RequestMethod.POST)
    public String updateCategory(@ModelAttribute("category") Category category, @PathVariable("id") int id, ModelMap modelMap){
        Optional.ofNullable(category)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_VALID_UPDATED_CATEGORY")));

        category.setId(id);
        category.setNo_level(CATEGORY.getLevel());
        category.setId_category(1);

        try {
            if(!categoryDao.update(category))
                throw new StoreException(errors.getErrorMessage("NOT_UPDATED_CATEGORY"));

        } catch (ValidationException e) {
            throw new StoreException(errors.getErrorMessage("NOT_VALID_UPDATED_CATEGORY"));
        }
        return PAGE_REDIRECT_SUBCATEGORIES_ID + id;
    }


    /*Метод updateSubCategory выполняет обновление подкатегории по id в БД.
    * @param    id подкатегория, кот. будет обновляться в БД;
    * @param    subCategory - новые данные подкатегории;
    * @param    modelMap - набор атрибутов для взаимодействия контроллера и представления;
    * @throws   StoreException - в случае, когда subCategory = null или содержит некорректные данные;
    * @returns  В случае успешного обновления подкатегории будет загружена страница для
    *           администрирования категорий ("redirect:/getSubCategories/") с отображением подкатегорий.
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = UPDATE_SUBCATEGORY, method = RequestMethod.POST)
    public String updateSubCategory(@ModelAttribute("subCategory") Category subCategory, @PathVariable("id") int id, ModelMap modelMap){
        Optional.ofNullable(subCategory)
                .orElseThrow(() -> new StoreException(errors.getErrorMessage("NOT_VALID_UPDATED_SUBCATEGORY")));

        subCategory.setId(id);
        subCategory.setNo_level(SUB_CATEGORY.getLevel());

        try {
            if (!categoryDao.update(subCategory))
                throw new StoreException(errors.getErrorMessage("NOT_UPDATED_SUBCATEGORY"));
        } catch (ValidationException e) {
            throw new StoreException(errors.getErrorMessage("NOT_VALID_UPDATED_SUBCATEGORY"));
        }
        return PAGE_REDIRECT_SUBCATEGORIES_ID + subCategory.getId_category();
    }

    /*Метод deleteCategory выполняет удаление категории с заданным id из БД.
    * @param    id категории, кот. будет удаляться;
    * @throws   StoreException - в случае, когда категория не найдена;
    * @returns  В случае успешного удаления категории будет загружена страница для
    *           администрирования категорий ("redirect:/getCategories/").
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = DELETE_CATEGORY, method = RequestMethod.POST)
    public String deleteCategory(@PathVariable("id") int id, ModelMap modelMap){
        if (!categoryDao.delete(id))
            throw new StoreException(errors.getErrorMessage("NOT_DELETED_CATEGORY"));
        return PAGE_REDIRECT_CATEGORIES ;
    }

    /*Метод deleteSubCategory выполняет удаление подкатегории с заданным id из БД.
    * @param    id подкатегории, кот. будет удаляться;
    * @throws   StoreException - в случае, когда категория не найдена;
    * @returns  В случае успешного удаления категории будет загружена страница для
    *           администрирования категорий с подкатегориями текущей подкатегории
    *           ("redirect:/getSubCategories?id").
    *           Если подкатегория не біла найдена, то будет загружена страница ""redirect:/getSubCategories""
    *           В противном случае, страница с описанием возникшей ошибки.
    * */
    @RequestMapping(value = DELETE_SUBCATEGORY, method = RequestMethod.POST)
    public String deleteSubCategory(@PathVariable("id") int id, ModelMap modelMap){
        Category category = categoryDao.getCategoryById(id);
        categoryDao.delete(id);
        return Optional.ofNullable(category).map(item -> PAGE_REDIRECT_SUBCATEGORIES_ID + item.getId_category())
                .orElse(PAGE_REDIRECT_SUBCATEGORIES);
    }

    /*Метод handleStoreException подготавливает инфо об исключении StoreException
     * для отображения на веб-странице.*/
    @ExceptionHandler(StoreException.class)
    public ModelAndView handleStoreException(StoreException e) {
        return viewHandlerException.handleStoreException(e, ERROR_PAGE_CATEGORIES);
    }

    /*Метод handleAllException подготавливает инфо об исключении
     * для отображения на веб-странице.*/
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception e) {
        return viewHandlerException.handleAllException(e, ERROR_PAGE_CATEGORIES);
    }

}
