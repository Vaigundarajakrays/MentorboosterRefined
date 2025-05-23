//package com.mentorboosters.app.controller;
//
//import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
//import com.mentorboosters.app.model.ConnectMethods;
//import com.mentorboosters.app.repository.ConnectMethodsRepository;
//import com.mentorboosters.app.response.CommonResponse;
//import com.mentorboosters.app.service.ConnectMethodsService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static com.mentorboosters.app.util.Constant.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ConnectMethodsControllerTest {
//
//    @Mock
//    ConnectMethodsRepository connectMethodsRepository;
//
//    @InjectMocks
//    ConnectMethodsService connectMethodsService;
//
//    @Test
//    void getAllConnectMethods_shouldReturnListOfConnectMethods_whenConnectMethodsExist() throws UnexpectedServerException {
//        // Arrange
//        ConnectMethods connectMethod = ConnectMethods.builder()
//                .id(1L)
//                .name("Google Meet")
//                .build();
//        connectMethod.setCreatedAt(LocalDateTime.now());
//        connectMethod.setUpdatedAt(LocalDateTime.now());
//
//        when(connectMethodsRepository.findAll()).thenReturn(List.of(connectMethod));
//
//        // Act
//        CommonResponse<List<ConnectMethods>> response = connectMethodsService.getAllConnectMethods();
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(LOADED_ALL_CONNECT_METHODS, response.getMessage());
//
//        ConnectMethods returnedMethod = response.getData().get(0);
//        assertEquals(1L, returnedMethod.getId());
//        assertEquals("Google Meet", returnedMethod.getName());
//        assertNotNull(returnedMethod.getCreatedAt());
//        assertNotNull(returnedMethod.getUpdatedAt());
//
//        verify(connectMethodsRepository).findAll();
//    }
//
//    @Test
//    void getAllConnectMethods_shouldReturnEmptyList_whenNoConnectMethodsExist() throws UnexpectedServerException {
//        // Arrange
//        when(connectMethodsRepository.findAll()).thenReturn(List.of());
//
//        // Act
//        CommonResponse<List<ConnectMethods>> response = connectMethodsService.getAllConnectMethods();
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(NO_CONNECT_METHODS_AVAILABLE, response.getMessage());
//        assertTrue(response.getData().isEmpty());
//
//        verify(connectMethodsRepository).findAll();
//    }
//
//    @Test
//    void getAllConnectMethods_shouldThrowUnexpectedServerException_whenExceptionOccurs() {
//        // Arrange
//        when(connectMethodsRepository.findAll()).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        UnexpectedServerException exception = assertThrows(UnexpectedServerException.class,
//                () -> connectMethodsService.getAllConnectMethods());
//
//        assertTrue(exception.getMessage().contains(ERROR_FETCHING_CONNECT_METHODS));
//
//        verify(connectMethodsRepository).findAll();
//    }
//
//    @Test
//    void saveConnectMethods_shouldReturnSuccess_whenConnectMethodIsSaved() throws UnexpectedServerException {
//        // Arrange
//        ConnectMethods connectMethod = ConnectMethods.builder()
//                .name("Zoom")
//                .build();
//
//        ConnectMethods savedConnectMethod = ConnectMethods.builder()
//                .id(1L)
//                .name("Zoom")
//                .build();
//        savedConnectMethod.setCreatedAt(LocalDateTime.now());
//        savedConnectMethod.setUpdatedAt(LocalDateTime.now());
//
//        when(connectMethodsRepository.existsByName(connectMethod.getName())).thenReturn(false);
//        when(connectMethodsRepository.save(connectMethod)).thenReturn(savedConnectMethod);
//
//        // Act
//        CommonResponse<ConnectMethods> response = connectMethodsService.saveConnectMethods(connectMethod);
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(response.getStatus());
//        assertEquals(200, response.getStatusCode());
//        assertEquals(SUCCESSFULLY_ADDED, response.getMessage());
//        assertEquals(savedConnectMethod, response.getData());
//        assertEquals("Zoom", response.getData().getName());
//        assertEquals(1L, response.getData().getId());
//        assertNotNull(response.getData().getCreatedAt());
//        assertNotNull(response.getData().getUpdatedAt());
//
//        verify(connectMethodsRepository).existsByName(connectMethod.getName());
//        verify(connectMethodsRepository).save(connectMethod);
//    }
//
//    @Test
//    void saveConnectMethods_shouldReturnError_whenConnectMethodAlreadyExists() throws UnexpectedServerException {
//        // Arrange
//        ConnectMethods connectMethod = ConnectMethods.builder()
//                .name("Zoom")
//                .build();
//
//        when(connectMethodsRepository.existsByName(connectMethod.getName())).thenReturn(true);
//
//        // Act
//        CommonResponse<ConnectMethods> response = connectMethodsService.saveConnectMethods(connectMethod);
//
//        // Assert
//        assertNotNull(response);
//        assertFalse(response.getStatus());
//        assertEquals(FORBIDDEN_CODE, response.getStatusCode());
//        assertEquals(CONNECT_METHOD_ALREADY_EXISTS, response.getMessage());
//        assertNull(response.getData());
//
//        verify(connectMethodsRepository).existsByName(connectMethod.getName());
//        verify(connectMethodsRepository, never()).save(connectMethod);
//    }
//
//    @Test
//    void saveConnectMethods_shouldReturnError_whenUnexpectedServerExceptionOccurs() throws UnexpectedServerException {
//        // Arrange
//        ConnectMethods connectMethod = ConnectMethods.builder()
//                .name("Zoom")
//                .build();
//
//        when(connectMethodsRepository.existsByName(connectMethod.getName())).thenReturn(false);
//        when(connectMethodsRepository.save(connectMethod)).thenThrow(new RuntimeException("Unexpected error"));
//
//        // Act & Assert
//        assertThrows(UnexpectedServerException.class, () -> {
//            connectMethodsService.saveConnectMethods(connectMethod);
//        });
//
//        verify(connectMethodsRepository).existsByName(connectMethod.getName());
//        verify(connectMethodsRepository).save(connectMethod);
//    }
//
//
//
//
//}
