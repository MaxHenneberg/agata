package agata.lcl.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {

    public ResourceNotFoundException(Class c, String id) {
        super(HttpStatus.NOT_FOUND, c.getSimpleName() + " with id '" + id + "' was not found");
    }
}

