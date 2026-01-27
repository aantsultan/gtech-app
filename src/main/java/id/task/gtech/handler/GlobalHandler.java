package id.task.gtech.handler;

import id.task.gtech.dto.ResponseDto;
import id.task.gtech.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDto<String>> notFound(NotFoundException e) {
        return new ResponseEntity<>(new ResponseDto<>(null, e.getMessage()), HttpStatus.NOT_FOUND);
    }

}
