package sit.cp23ms2.sportconnect.dtos.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    private Integer locationId;
    private String name;
    private Double latitude;
    private Double longitude;
}
