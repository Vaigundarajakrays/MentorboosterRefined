package com.mentorboosters.app.service;

import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Category;
import com.mentorboosters.app.repository.CategoryRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class CategoryService {

    private  final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){this.categoryRepository=categoryRepository;}

    public CommonResponse<List<Category>> getAllCategory() throws UnexpectedServerException {

        try {

            var categories = categoryRepository.findAll();

            if(categories.isEmpty()){

                return CommonResponse.<List<Category>>builder()
                        .message(NO_CATEGORIES_AVAILABLE)
                        .status(STATUS_TRUE)
                        .data(categories)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<Category>>builder()
                    .message(LOADED_ALL_CATEGORIES)
                    .status(STATUS_TRUE)
                    .data(categories)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_FETCHING_CATEGORIES + e.getMessage());
        }
    }

    public CommonResponse<Category> saveCategory(Category category) throws UnexpectedServerException {

        if(categoryRepository.existsByNameIgnoreCase(category.getName())){
            throw new ResourceAlreadyExistsException(CATEGORY_ALREADY_EXISTS);
        }

        try {

            Category savedCategory = categoryRepository.save(category);

            return CommonResponse.<Category>builder()
                    .message(SUCCESSFULLY_ADDED)
                    .status(STATUS_TRUE)
                    .data(savedCategory)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_ADDING_CATEGORY + e.getMessage());
        }
    }

    public CommonResponse<Category> updateCategory(Long id, Category category) throws ResourceNotFoundException, UnexpectedServerException {

        // Check if a category with the same name (case-insensitive) exists excluding the current category by ID
        if(categoryRepository.existsByNameIgnoreCaseAndIdNot(category.getName(), id)){
            throw new ResourceAlreadyExistsException(CATEGORY_ALREADY_EXISTS);
        }

        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_WITH_ID + id));

        try {

            existingCategory.setName(category.getName());
            existingCategory.setIcon(category.getIcon());

            Category updatedCategory = categoryRepository.save(existingCategory);

            return CommonResponse.<Category>builder()
                    .message(CATEGORY_UPDATED_SUCCESS)
                    .status(STATUS_TRUE)
                    .data(updatedCategory)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_UPDATING_CATEGORY + e.getMessage());
        }
    }

    public CommonResponse<Category> deleteCategory(Long id) throws ResourceNotFoundException, UnexpectedServerException {

        try {

            if (categoryRepository.existsById(id)) {

                categoryRepository.deleteById(id);

                return CommonResponse.<Category>builder()
                        .message(CATEGORY_DELETED_SUCCESSFULLY)
                        .status(STATUS_TRUE)
                        .statusCode(SUCCESS_CODE)
                        .build();
            } else {

                throw new ResourceNotFoundException(CATEGORY_NOT_FOUND_WITH_ID + id);
            }

        }

        catch (ResourceNotFoundException e){
            throw e;
        }

        catch (Exception e){
            throw new UnexpectedServerException(ERROR_DELETING_CATEGORY);
        }
    }
}
