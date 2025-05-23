package com.mentorboosters.app.controller;

import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Category;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentorboosters/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){this.categoryService=categoryService;}

    @GetMapping("/getAllCategories")
    public CommonResponse<List<Category>> getAllCategoryDetails() throws UnexpectedServerException {
        return categoryService.getAllCategory();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/saveCategory")
    public CommonResponse<Category> saveCategory(@RequestBody Category category) throws UnexpectedServerException {
        return categoryService.saveCategory(category);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/updateCategory/{id}")
    public CommonResponse<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) throws UnexpectedServerException, ResourceNotFoundException {
        return categoryService.updateCategory(id, category);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("deleteCategory/{id}")
    public CommonResponse<Category> deleteCategory(@PathVariable Long id) throws UnexpectedServerException, ResourceNotFoundException {
        return categoryService.deleteCategory(id);
    }
}
