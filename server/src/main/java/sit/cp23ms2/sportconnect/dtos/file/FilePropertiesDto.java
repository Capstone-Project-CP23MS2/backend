package sit.cp23ms2.sportconnect.dtos.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilePropertiesDto {
    private String fileName;
    private String fileUrl;
    private Integer fileSize;
}
