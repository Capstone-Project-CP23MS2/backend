package sit.cp23ms2.sportconnect.dtos.file;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class FileDto {
    private Integer fileId;
    private Integer activityId;
    private Integer userId;
    private String fileName;
    private String fileUrl;
    private Integer fileSize;
    private Instant createdAt;
}
