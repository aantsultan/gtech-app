package id.task.gtech.handler;

import id.task.gtech.dto.ResponseDto;
import id.task.gtech.exception.NotFoundException;
import id.task.gtech.exception.TransferException;
import id.task.gtech.helper.ExceptionHelper;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandler {

    /**
     * Exception for non-transactional
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<@NonNull ResponseDto<String>> notFound(NotFoundException e) {
        return new ResponseEntity<>(new ResponseDto<>(null, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<@NonNull ResponseDto<String>> notFound(TransferException e) {
        return new ResponseEntity<>(new ResponseDto<>(null, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Exception for transactional
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ResponseDto<String>> dataIntegrityViolation(DataIntegrityViolationException e) {
        String message = e.getMessage();
        if (e.getMessage().contains("Unique index or primary key violation")) {
            message = ExceptionHelper.TRANSFER_DUPLICATE;
        }
        return new ResponseEntity<>(new ResponseDto<>(null, message), HttpStatus.BAD_REQUEST);
    }

}
