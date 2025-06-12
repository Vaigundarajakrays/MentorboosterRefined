package com.mentorboosters.app.service;

import com.mentorboosters.app.exceptionHandling.ResourceAlreadyExistsException;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Category;
import com.mentorboosters.app.model.Mentor;
import com.mentorboosters.app.model.SeminarNotes;
import com.mentorboosters.app.model.Users;
import com.mentorboosters.app.repository.SeminarNotesRepository;
import com.mentorboosters.app.repository.UsersRepository;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class SeminarNotesService {

    private final SeminarNotesRepository seminarNotesRepository;
    private final UsersRepository usersRepository;

    public SeminarNotesService(SeminarNotesRepository seminarNotesRepository, UsersRepository usersRepository){
        this.seminarNotesRepository=seminarNotesRepository;
        this.usersRepository=usersRepository;
    }

    public CommonResponse<SeminarNotes> saveNotes(SeminarNotes seminarNotes) throws ResourceNotFoundException, UnexpectedServerException {

        boolean isPresent = usersRepository.existsById(seminarNotes.getUserId());

        if(!isPresent){
            throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + seminarNotes.getUserId());
        }

        boolean isSaved = seminarNotesRepository.existsByUserIdAndTitle(seminarNotes.getUserId(),seminarNotes.getTitle());

        if(isSaved){
            throw new ResourceAlreadyExistsException(TITLE_ALREADY_EXISTS);
        }

        try {

            var savedNotes = seminarNotesRepository.save(seminarNotes);

            return CommonResponse.<SeminarNotes>builder()
                    .message(SUCCESSFULLY_ADDED)
                    .status(STATUS_TRUE)
                    .data(savedNotes)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){

            throw new UnexpectedServerException(ERROR_SAVING_NOTES + e.getMessage());

        }
    }

    public CommonResponse<SeminarNotes> updateNotes(Long id, SeminarNotes seminarNotes) throws ResourceNotFoundException, UnexpectedServerException {

        boolean isPresent = usersRepository.existsById(seminarNotes.getUserId());

        if(!isPresent){
            throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID + seminarNotes.getUserId());
        }

        boolean isSaved = seminarNotesRepository.existsByUserIdAndTitle(seminarNotes.getUserId(),seminarNotes.getTitle());

        if(isSaved){
            throw new ResourceAlreadyExistsException(TITLE_ALREADY_EXISTS);
        }

        SeminarNotes existingNotes = seminarNotesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOTES_NOT_FOUND_WITH_ID + seminarNotes.getId()));

        try {

            existingNotes.setTitle(seminarNotes.getTitle());
            existingNotes.setContent(seminarNotes.getContent());

            SeminarNotes updated = seminarNotesRepository.save(existingNotes);

            return CommonResponse.<SeminarNotes>builder()
                    .message(NOTES_UPDATED)
                    .statusCode(SUCCESS_CODE)
                    .data(updated)
                    .status(STATUS_TRUE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_UPDATING_NOTES + e.getMessage());
        }
    }

    public CommonResponse<SeminarNotes> deleteNotesById(Long id) throws ResourceNotFoundException, UnexpectedServerException {

        boolean isPresent = seminarNotesRepository.existsById(id);

        if(!isPresent){
            throw new ResourceNotFoundException(NOTES_NOT_FOUND_WITH_ID + id);
        }

        try {

            seminarNotesRepository.deleteById(id);

            return CommonResponse.<SeminarNotes>builder()
                    .message(NOTES_DELETED_SUCCESSFULLY)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_DELETING_NOTES + e.getMessage());
        }

    }

    // If not give transactional
    // postgresql says, "Hey! You're trying to access a large object. I need to be in a transaction for that, or I won’t allow it."
    @Transactional
    public CommonResponse<SeminarNotes> deleteNotesByUserId(Long userId) throws ResourceNotFoundException, UnexpectedServerException {

        boolean isPresent = seminarNotesRepository.existsByUserId(userId);

        if(!isPresent){
            throw new ResourceNotFoundException(NOTES_NOT_FOUND_WITH_USER_ID + userId);
        }

        try {

            seminarNotesRepository.deleteByUserId(userId);

            return CommonResponse.<SeminarNotes>builder()
                    .message(NOTES_DELETED_SUCCESSFULLY)
                    .status(STATUS_TRUE)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_DELETING_NOTES + e.getMessage());
        }

    }

    public CommonResponse<SeminarNotes> getNotesById(Long id) throws ResourceNotFoundException {

        SeminarNotes seminarNotes = seminarNotesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOTES_NOT_FOUND_WITH_ID + id));

        return CommonResponse.<SeminarNotes>builder()
                .message(LOADED_SEMINAR_NOTES)
                .status(STATUS_TRUE)
                .data(seminarNotes)
                .statusCode(SUCCESS_CODE)
                .build();
    }

    @Transactional
    public CommonResponse<SeminarNotes> getNotesByUserId(Long userId) throws ResourceNotFoundException {

        SeminarNotes seminarNotes = seminarNotesRepository.findByUserId(userId);

        if(seminarNotes==null){
            throw new ResourceNotFoundException(NOTES_NOT_FOUND_WITH_USER_ID + userId);
        }

        return CommonResponse.<SeminarNotes>builder()
                .message(LOADED_SEMINAR_NOTES)
                .status(STATUS_TRUE)
                .data(seminarNotes)
                .statusCode(SUCCESS_CODE)
                .build();
    }

    public CommonResponse<List<SeminarNotes>> getAllNotes() throws UnexpectedServerException {

        try {

            List<SeminarNotes> seminarNotes = seminarNotesRepository.findAll();

            if (seminarNotes.isEmpty()) {
                return CommonResponse.<List<SeminarNotes>>builder()
                        .status(STATUS_FALSE)
                        .message(NO_NOTES_AVAILABLE)
                        .data(seminarNotes)
                        .statusCode(SUCCESS_CODE)
                        .build();
            }

            return CommonResponse.<List<SeminarNotes>>builder()
                    .status(STATUS_TRUE)
                    .message(LOADED_SEMINAR_NOTES)
                    .data(seminarNotes)
                    .statusCode(SUCCESS_CODE)
                    .build();

        } catch (Exception e){
            throw new UnexpectedServerException(ERROR_FETCHING_NOTES + e.getMessage());
        }
    }

}
