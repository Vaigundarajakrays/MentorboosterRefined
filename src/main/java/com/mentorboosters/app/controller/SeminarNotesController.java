package com.mentorboosters.app.controller;

import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.SeminarNotes;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.SeminarNotesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SeminarNotesController {

    private final SeminarNotesService seminarNotesService;

    public SeminarNotesController(SeminarNotesService seminarNotesService){
        this.seminarNotesService=seminarNotesService;
    }

    @PostMapping("/saveNotes")
    public CommonResponse<SeminarNotes> saveNotes(@RequestBody SeminarNotes seminarNotes) throws UnexpectedServerException, ResourceNotFoundException {
        return seminarNotesService.saveNotes(seminarNotes);
    }

    @PutMapping("/updateNotes/{id}")
    public CommonResponse<SeminarNotes> updateNotes(@PathVariable Long id, @RequestBody SeminarNotes seminarNotes) throws UnexpectedServerException, ResourceNotFoundException {
        return seminarNotesService.updateNotes(id, seminarNotes);
    }

    @DeleteMapping("/deleteNotesById/{id}")
    public CommonResponse<SeminarNotes> deleteNotesById(@PathVariable Long id) throws UnexpectedServerException, ResourceNotFoundException {
        return seminarNotesService.deleteNotesById(id);
    }

    @DeleteMapping("/deleteNotesByUserId/{userId}")
    public CommonResponse<SeminarNotes> deleteNotesByUserId(@PathVariable Long userId) throws UnexpectedServerException, ResourceNotFoundException {
        return seminarNotesService.deleteNotesByUserId(userId);
    }

    @GetMapping("/getNotesById/{id}")
    public CommonResponse<SeminarNotes> getNotesById(@PathVariable Long id) throws ResourceNotFoundException {
        return seminarNotesService.getNotesById(id);
    }

    @GetMapping("/getNotesByUserId/{userId}")
    public CommonResponse<SeminarNotes> getNotesByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        return seminarNotesService.getNotesByUserId(userId);
    }

    @GetMapping("/getAllNotes")
    public CommonResponse<List<SeminarNotes>> getAllNotes() throws UnexpectedServerException {
        return seminarNotesService.getAllNotes();
    }
}
