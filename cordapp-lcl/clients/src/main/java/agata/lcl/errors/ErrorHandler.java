package agata.lcl.errors;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ErrorResponse> handleStatusException(ResponseStatusException ex) {
        ErrorResponse res = new ErrorResponse(ex.getStatus(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(res);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse res = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: \n" + ex.getMessage());
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}

