package com.mentorboosters.app.service;

import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.CategoryNew;
import com.mentorboosters.app.repository.CategoryNewRepository;
import com.mentorboosters.app.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
@RequiredArgsConstructor
public class CategoryNewService {

    private final CategoryNewRepository categoryNewRepository;

    public CommonResponse<List<CategoryNew>> getAllCategories() throws UnexpectedServerException {

        try {

            List<CategoryNew> categories = categoryNewRepository.findAll();

            if (categories.isEmpty()) {
                return CommonResponse.<List<CategoryNew>>builder()
                        .status(STATUS_FALSE)
                        .statusCode(SUCCESS_CODE)
                        .data(categories)
                        .message("No categories found")
                        .build();
            }

            return CommonResponse.<List<CategoryNew>>builder()
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .message("Loaded all categories")
                    .data(categories)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException("Error while loading categories: " + e.getMessage());
        }
    }
}
