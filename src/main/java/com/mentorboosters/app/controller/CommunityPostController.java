package com.mentorboosters.app.controller;

import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.CommunityPost;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.CommunityPostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    public CommunityPostController(CommunityPostService communityPostService){
        this.communityPostService=communityPostService;
    }

    @PostMapping("/savePosts")
    public CommonResponse<CommunityPost> createPost(@RequestBody CommunityPost post) throws UnexpectedServerException, ResourceNotFoundException {
        return communityPostService.createPost(post);
    }

    @GetMapping("/getAllPosts")
    public CommonResponse<List<CommunityPost>> getAllPosts() throws UnexpectedServerException {
        return communityPostService.getAllPosts();
    }

    @GetMapping("/getPostById/{id}")
    public CommonResponse<CommunityPost> getPostById(@PathVariable Long id) throws ResourceNotFoundException {
        return communityPostService.getPostById(id);
    }

    @DeleteMapping("/deletePostById/{id}")
    public CommonResponse<Void> deletePost(@PathVariable Long id) throws ResourceNotFoundException, UnexpectedServerException {
        return communityPostService.deletePost(id);
    }
}

