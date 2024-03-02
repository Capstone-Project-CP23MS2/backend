package sit.cp23ms2.sportconnect.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.cp23ms2.sportconnect.dtos.location.PageLocationDto;
import sit.cp23ms2.sportconnect.entities.Location;
import sit.cp23ms2.sportconnect.repositories.LocationRepository;

@Service
public class LocationService {
    @Autowired
    LocationRepository repository;
    @Autowired
    ModelMapper modelMapper;

    public PageLocationDto getLocation(int pageNum, int pageSize, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page<Location> listLocations = repository.findAll(pageRequest);
        return modelMapper.map(listLocations, PageLocationDto.class);
    }

    public Location getById(Integer id) {
        Location location = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Location ID: " + id + " Not Found"));
        return location;
    }
}
