package com.mentorboosters.app.controller;

import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Comment;
import com.mentorboosters.app.service.CommentService;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mentorboosters.app.response.CommonResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService=commentService;
    }

    @PostMapping("/saveComment")
    public CommonResponse<Comment> addComment(@RequestBody Comment comment) throws UnexpectedServerException, ResourceNotFoundException {
        return commentService.addComment(comment);
    }

    @GetMapping("/getCommentsByPostId/{postId}")
    public CommonResponse<List<Comment>> getComments(@PathVariable Long postId) throws UnexpectedServerException, ResourceNotFoundException {
        return commentService.getCommentsByPostId(postId);
    }
}

