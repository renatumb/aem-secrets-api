package com.renatobonfim.aemblogbackend.category;


import com.renatobonfim.aemblogbackend.customExceptions.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return this.categoryRepository.save(Category.builder()
                .name(category.getName())
                .description(category.getDescription())
                .build());
    }

    @Override
    public void deleteCategory(Long categoryId) {
        this.categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category updateCategory(Category category, Long categoryID) throws CategoryNotFoundException {
        Category oldCategory = this.categoryRepository.findById(categoryID ).orElseThrow(() -> new CategoryNotFoundException(categoryID));

        oldCategory.setDescription(category.getDescription() != null ? category.getDescription() : oldCategory.getDescription() );
        oldCategory.setName(category.getName()!= null ? category.getName() : oldCategory.getName());

        return this.categoryRepository.save(oldCategory);
    }

    @Override
    public Category findCategoryById(Long categoryId) throws CategoryNotFoundException {
        return this.categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException( categoryId));
    }

    @Override
    public Page<Category> findAllCategories(int page, int size, String sort, String[] properties) {
        return categoryRepository.findAll(PageRequest.of(page, size, Sort.Direction.fromString(sort), properties));
    }
}
