//package com.mentorboosters.app.controller;
//
//import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.Category;
//import com.mentorboosters.app.repository.CategoryRepository;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.service.CategoryService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static com.mentorboosters.app.util.Constant.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class CategoryControllerTest {
//
//    @Mock
//    CategoryRepository categoryRepository;
//
//    @InjectMocks
//    CategoryService categoryService;
//
//    @Test
//    void getAllCategory_shouldReturnListOfCategories_whenCategoriesExist() throws UnexpectedServerException {
//        Category category1 = Category.builder()
//                .id(1L)
//                .name("Design")
//                .icon("Awesome.code")
//                .build();
//        LocalDateTime now = LocalDateTime.now();
//        category1.setCreatedAt(now);
//        category1.setUpdatedAt(now);
//
//        List<Category> mockList = List.of(category1);
//
//        when(categoryRepository.findAll()).thenReturn(mockList);
//
//        // Act
//        CommonResponse<List<Category>> response = categoryService.getAllCategory();
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(LOADED_ALL_CATEGORIES, response.getMessage());
//
//        Category returnedCategory = response.getData().get(0);
//        assertEquals(1L, returnedCategory.getId());
//        assertEquals("Design", returnedCategory.getName());
//        assertEquals("Awesome.code", returnedCategory.getIcon());
//        assertEquals(now, returnedCategory.getCreatedAt());
//        assertEquals(now, returnedCategory.getUpdatedAt());
//
//        verify(categoryRepository).findAll();
//    }
//
//    @Test
//    void getAllCategory_shouldReturnEmptyList_whenNoCategoriesExist() throws UnexpectedServerException {
//        // Arrange
//        when(categoryRepository.findAll()).thenReturn(List.of());
//
//        // Act
//        CommonResponse<List<Category>> response = categoryService.getAllCategory();
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(NO_CATEGORIES_AVAILABLE, response.getMessage());
//
//        assertTrue(response.getData().isEmpty());
//
//        verify(categoryRepository).findAll();
//    }
//
//    @Test
//    void getAllCategory_shouldThrowUnexpectedServerException_whenRepositoryThrowsException() {
//        // Arrange
//        when(categoryRepository.findAll()).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            categoryService.getAllCategory();
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_FETCHING_CATEGORIES ));
//
//        verify(categoryRepository).findAll();
//    }
//
//    @Test
//    void saveCategory_shouldReturnSuccess_whenCategoryIsSaved() throws UnexpectedServerException {
//        // Arrange
//        Category category = Category.builder()
//                .name("Design")
//                .icon("Awesome.code")
//                .build();
//
//        Category category1 = Category.builder()
//                .id(1L)
//                .name("Design")
//                .icon("Awesome.code")
//                .build();
//
//        LocalDateTime now = LocalDateTime.now();
//        category1.setCreatedAt(now);
//        category1.setUpdatedAt(now);
//
//        when(categoryRepository.existsByNameIgnoreCase(category.getName())).thenReturn(false);
//        when(categoryRepository.save(category)).thenReturn(category1);
//
//        // Act
//        CommonResponse<Category> response = categoryService.saveCategory(category);
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(SUCCESSFULLY_ADDED, response.getMessage());
//
//        // Assert fields of the Category returned in data
//        assertEquals(1L, response.getData().getId());
//        assertEquals("Design", response.getData().getName());
//        assertEquals("Awesome.code", response.getData().getIcon());
//        assertEquals(now, response.getData().getCreatedAt());
//        assertEquals(now, response.getData().getUpdatedAt());
//
//        verify(categoryRepository).existsByNameIgnoreCase(category.getName());
//        verify(categoryRepository).save(category);
//    }
//
//    @Test
//    void saveCategory_shouldReturnError_whenCategoryAlreadyExists() throws UnexpectedServerException {
//        // Arrange
//        Category category = Category.builder()
//                .id(1L)
//                .name("Design")
//                .icon("Awesome.code")
//                .build();
//
//        when(categoryRepository.existsByNameIgnoreCase(category.getName())).thenReturn(true);
//
//        // Act
//        CommonResponse<Category> response = categoryService.saveCategory(category);
//
//        // Assert
//        assertNotNull(response);
//        assertFalse(response.getStatus());
//        assertEquals(FORBIDDEN_CODE, response.getStatusCode());
//        assertEquals(CATEGORY_ALREADY_EXISTS, response.getMessage());
//        assertNull(response.getData());
//
//        verify(categoryRepository).existsByNameIgnoreCase(category.getName());
//    }
//
//
//    @Test
//    void saveCategory_shouldThrowUnexpectedServerException_whenErrorOccurs() throws UnexpectedServerException {
//        // Arrange
//        Category category = Category.builder()
//                .name("Design")
//                .icon("Awesome.code")
//                .build();
//
//        when(categoryRepository.existsByNameIgnoreCase(category.getName())).thenReturn(false);
//        when(categoryRepository.save(category)).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            categoryService.saveCategory(category);
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_ADDING_CATEGORY));
//
//        verify(categoryRepository).existsByNameIgnoreCase(category.getName());
//        verify(categoryRepository).save(category);
//    }
//
//    @Test
//    void updateCategory_shouldReturnSuccess_whenCategoryIsUpdated() throws ResourceNotFoundException, UnexpectedServerException, ResourceNotFoundException {
//        // Arrange
//        Long categoryId = 1L;
//
//        Category existingCategory = Category.builder()
//                .id(categoryId)
//                .name("OldName")
//                .icon("Old.icon")
//                .build();
//        existingCategory.setCreatedAt(LocalDateTime.now());
//        existingCategory.setUpdatedAt(LocalDateTime.now());
//
//        Category updateRequest = Category.builder()
//                .name("NewDesign")
//                .icon("New.icon")
//                .build();
//
//        Category updatedCategory = Category.builder()
//                .id(categoryId)
//                .name("NewDesign")
//                .icon("New.icon")
//                .build();
//        updatedCategory.setCreatedAt(existingCategory.getCreatedAt());
//        updatedCategory.setUpdatedAt(LocalDateTime.now());
//
//        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(updateRequest.getName(), categoryId)).thenReturn(false);
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
//        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);
//
//        // Act
//        CommonResponse<Category> response = categoryService.updateCategory(categoryId, updateRequest);
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(CATEGORY_UPDATED_SUCCESS, response.getMessage());
//
//        Category returnedCategory = response.getData();
//        assertEquals(categoryId, returnedCategory.getId());
//        assertEquals("NewDesign", returnedCategory.getName());
//        assertEquals("New.icon", returnedCategory.getIcon());
//        assertEquals(existingCategory.getCreatedAt(), returnedCategory.getCreatedAt());
//        assertNotNull(returnedCategory.getUpdatedAt());
//
//        verify(categoryRepository).existsByNameIgnoreCaseAndIdNot(updateRequest.getName(), categoryId);
//        verify(categoryRepository).findById(categoryId);
//        verify(categoryRepository).save(existingCategory);
//    }
//
//    @Test
//    void updateCategory_shouldReturnError_whenCategoryNameAlreadyExists() throws ResourceNotFoundException, UnexpectedServerException {
//        // Arrange
//        Long categoryId = 1L;
//
//        Category updateRequest = Category.builder()
//                .name("ExistingName")
//                .icon("Some.icon")
//                .build();
//
//        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(updateRequest.getName(), categoryId)).thenReturn(true);
//
//        // Act
//        CommonResponse<Category> response = categoryService.updateCategory(categoryId, updateRequest);
//
//        // Assert
//        assertNotNull(response);
//        assertFalse(response.getStatus());
//        assertEquals(403, response.getStatusCode());
//        assertEquals(CATEGORY_ALREADY_EXISTS, response.getMessage());
//        assertNull(response.getData());
//
//        verify(categoryRepository).existsByNameIgnoreCaseAndIdNot(updateRequest.getName(), categoryId);
//        verify(categoryRepository, never()).findById(anyLong());
//        verify(categoryRepository, never()).save(any(Category.class));
//    }
//
//    @Test
//    void updateCategory_shouldThrowResourceNotFoundException_whenCategoryNotFound() {
//        // Arrange
//        Long categoryId = 1L;
//
//        Category updateRequest = Category.builder()
//                .name("NewName")
//                .icon("New.icon")
//                .build();
//
//        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(updateRequest.getName(), categoryId)).thenReturn(false);
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            categoryService.updateCategory(categoryId, updateRequest);
//        });
//
//        assertTrue(exception.getMessage().contains(CATEGORY_NOT_FOUND_WITH_ID + categoryId));
//
//        verify(categoryRepository).existsByNameIgnoreCaseAndIdNot(updateRequest.getName(), categoryId);
//        verify(categoryRepository).findById(categoryId);
//        verify(categoryRepository, never()).save(any(Category.class));
//    }
//
//    @Test
//    void updateCategory_shouldThrowUnexpectedServerException_whenSaveFails() {
//        // Arrange
//        Long categoryId = 1L;
//
//        Category updateRequest = Category.builder()
//                .name("NewName")
//                .icon("New.icon")
//                .build();
//
//        Category existingCategory = Category.builder()
//                .id(categoryId)
//                .name("OldName")
//                .icon("Old.icon")
//                .build();
//        existingCategory.setCreatedAt(LocalDateTime.now());
//        existingCategory.setUpdatedAt(LocalDateTime.now());
//
//        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(updateRequest.getName(), categoryId)).thenReturn(false);
//        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
//        when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException("DB Error"));
//
//        // Act & Assert
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            categoryService.updateCategory(categoryId, updateRequest);
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_UPDATING_CATEGORY));
//
//        verify(categoryRepository).existsByNameIgnoreCaseAndIdNot(updateRequest.getName(), categoryId);
//        verify(categoryRepository).findById(categoryId);
//        verify(categoryRepository).save(any(Category.class));
//    }
//
//    @Test
//    void deleteCategory_shouldReturnSuccess_whenCategoryExists() throws ResourceNotFoundException, UnexpectedServerException {
//        // Arrange
//        Long categoryId = 1L;
//
//        when(categoryRepository.existsById(categoryId)).thenReturn(true);
//
//        // Act
//        CommonResponse<Category> response = categoryService.deleteCategory(categoryId);
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(CATEGORY_DELETED_SUCCESSFULLY, response.getMessage());
//
//        verify(categoryRepository).existsById(categoryId);
//        verify(categoryRepository).deleteById(categoryId);
//    }
//
//    @Test
//    void deleteCategory_shouldThrowResourceNotFoundException_whenCategoryDoesNotExist() {
//        // Arrange
//        Long categoryId = 1L;
//
//        when(categoryRepository.existsById(categoryId)).thenReturn(false);
//
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
//            categoryService.deleteCategory(categoryId);
//        });
//
//        assertTrue(exception.getMessage().contains(CATEGORY_NOT_FOUND_WITH_ID + categoryId));
//
//        verify(categoryRepository).existsById(categoryId);
//        verify(categoryRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    void deleteCategory_shouldThrowUnexpectedServerException_whenExceptionOccurs() {
//        // Arrange
//        Long categoryId = 1L;
//
//        when(categoryRepository.existsById(categoryId)).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class, () -> {
//            categoryService.deleteCategory(categoryId);
//        });
//
//        assertTrue(exception.getMessage().contains(ERROR_DELETING_CATEGORY));
//
//        verify(categoryRepository).existsById(categoryId);
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//}
