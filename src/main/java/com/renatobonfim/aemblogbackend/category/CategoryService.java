package com.renatobonfim.aemblogbackend.category;


import com.renatobonfim.aemblogbackend.customExceptions.CategoryNotFoundException;
import org.springframework.data.domain.Page;

public interface CategoryService {

    Category createCategory(Category category);

    void deleteCategory(Long categoryId);

    Category updateCategory(Category category, Long categoryID) throws  CategoryNotFoundException;

    Category findCategoryById(Long categoryId) throws CategoryNotFoundException;

    Page<Category> findAllCategories(int page, int size, String sort, String[] properties);
}
