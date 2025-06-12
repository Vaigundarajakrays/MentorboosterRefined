package com.mentorboosters.app.controller;

import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.UsersService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService){this.usersService=usersService;}

    @GetMapping("/getUserById/{id}")
    public CommonResponse<Users> getUserById(@PathVariable Long id) throws ResourceNotFoundException {
        return usersService.getUserById(id);
    }

//    @PutMapping("updateUser/{id}")
//    public CommonResponse<Users> updateUser(@PathVariable Long id ,@RequestBody Users updatedUser) throws ResourceNotFoundException, UnexpectedServerException {
//        return usersService.updateUser(id, updatedUser);
//    }

    @DeleteMapping("deleteUserById/{id}")
    public CommonResponse<Users> deleteUserById(@PathVariable Long id) throws ResourceNotFoundException, UnexpectedServerException {
        return usersService.deleteUserById(id);
    }
}
