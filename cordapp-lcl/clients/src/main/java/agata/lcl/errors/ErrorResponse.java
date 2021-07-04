package agata.lcl.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final int status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private final LocalDateTime timestamp;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Object details;

    public ErrorResponse(HttpStatus status, String message, Object details) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.message = message;
        this.details = details;
    }

    public ErrorResponse(HttpStatus status, String message) {
        this(status, message, null);
    }
}

