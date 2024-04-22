package sit.cp23ms2.sportconnect.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import sit.cp23ms2.sportconnect.dtos.location.LocationDto;
import sit.cp23ms2.sportconnect.dtos.location.PageLocationDto;
import sit.cp23ms2.sportconnect.services.LocationService;
import sit.cp23ms2.sportconnect.utils.ListMapper;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@Component
public class LocationController {
    @Autowired
    LocationService locationService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    @GetMapping
    public PageLocationDto getLocation(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(defaultValue = "locationId") String sortBy) {
        return locationService.getLocation(page, pageSize, sortBy);
    }

    @GetMapping("/getList")
    public List<LocationDto> getLocationNoPaging(@RequestParam(required = false)Double lat,
                                                 @RequestParam(required = false)Double lng,
                                                 @RequestParam(required = false)Integer radius) {
        return locationService.getLocationNoPaging(lat, lng, radius);
    }

    @GetMapping("/{id}")
    public LocationDto getById(@PathVariable Integer id) {
        return modelMapper.map(locationService.getById(id), LocationDto.class);
    }
}
