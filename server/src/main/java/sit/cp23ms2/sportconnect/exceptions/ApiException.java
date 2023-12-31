package sit.cp23ms2.sportconnect.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class ApiException {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;
    private List<ErrorDetail> details;
    public ApiException(String message, HttpStatus httpStatus,
                        ZonedDateTime timestamp) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }
}
