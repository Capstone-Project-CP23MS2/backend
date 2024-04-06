package sit.cp23ms2.sportconnect.dtos.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileDto {
    private Integer activityId;
    private Integer userId;
    private MultipartFile multipartFile;
//    private String fileName;
//    private String fileUrl;
//    private Integer fileSize;
}
