package com.renatobonfim.aemblogbackend.category;

import com.renatobonfim.aemblogbackend.customExceptions.CategoryNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<Category>> findAllCategories(@RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "5") int size,
                                                            @RequestParam(value = "sort", defaultValue = "asc") String sort,
                                                            @RequestParam(value = "fields", defaultValue = "id") String properties) {

        return ResponseEntity.ok().body(categoryService.findAllCategories(page, size, sort, properties.split(",")));
    }

    @GetMapping("/{categoryID}")
    public ResponseEntity<Category> findCategoryByID(@PathVariable Long categoryID) throws CategoryNotFoundException {
        return ResponseEntity.ok(categoryService.findCategoryById(categoryID));
    }

    @DeleteMapping("/{categoryID}")
    public ResponseEntity deleteCategory(@PathVariable Long categoryID) {
        categoryService.deleteCategory(categoryID);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{categoryID}")
    public ResponseEntity<Category> updateCategory(@RequestBody Category category, @PathVariable Long categoryID) throws CategoryNotFoundException {
        return ResponseEntity.ok(categoryService.updateCategory(category, categoryID));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) throws URISyntaxException {
        Category categoryCreated = categoryService.createCategory(category);
        return ResponseEntity.created(new URI(categoryCreated.getId().toString())).body(categoryCreated);
    }
}
