package sit.cp23ms2.sportconnect.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TestFileStore {
    private MultipartFile multipartFile;
}
