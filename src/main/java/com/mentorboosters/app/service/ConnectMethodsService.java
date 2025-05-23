package com.mentorboosters.app.service;

import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.ConnectMethods;
import com.mentorboosters.app.repository.ConnectMethodsRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class ConnectMethodsService {

    private final ConnectMethodsRepository connectMethodsRepository;

    public ConnectMethodsService(ConnectMethodsRepository connectMethodsRepository){this.connectMethodsRepository=connectMethodsRepository;}

    public CommonResponse<List<ConnectMethods>> getAllConnectMethods() throws UnexpectedServerException {

        try {

            var connectMethods = connectMethodsRepository.findAll();

            if(connectMethods.isEmpty()){

                return CommonResponse.<List<ConnectMethods>>builder()
                        .message(NO_CONNECT_METHODS_AVAILABLE)
                        .status(STATUS_TRUE)
                        .data(connectMethods)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<ConnectMethods>>builder()
                    .message(LOADED_ALL_CONNECT_METHODS)
                    .status(STATUS_TRUE)
                    .data(connectMethods)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e) {

            throw new UnexpectedServerException(ERROR_FETCHING_CONNECT_METHODS + e.getMessage());
        }

    }

    public CommonResponse<ConnectMethods> saveConnectMethods(ConnectMethods connectMethods) throws UnexpectedServerException {

        if(connectMethodsRepository.existsByName(connectMethods.getName())){
            return CommonResponse.<ConnectMethods>builder()
                    .message(CONNECT_METHOD_ALREADY_EXISTS)
                    .status(STATUS_FALSE)
                    .statusCode(FORBIDDEN_CODE)
                    .build();
        }

        try {
            ConnectMethods connectMethod = connectMethodsRepository.save(connectMethods);

            return CommonResponse.<ConnectMethods>builder()
                    .message(SUCCESSFULLY_ADDED)
                    .status(STATUS_TRUE)
                    .data(connectMethod)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_ADDING_CONNECT_METHOD + e.getMessage());
        }

    }
}
