package sit.cp23ms2.sportconnect.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import sit.cp23ms2.sportconnect.dtos.location.CreateLocationDto;
import sit.cp23ms2.sportconnect.dtos.location.LocationDto;
import sit.cp23ms2.sportconnect.dtos.location.PageLocationDto;
import sit.cp23ms2.sportconnect.dtos.location.UpdateLocationDto;
import sit.cp23ms2.sportconnect.entities.Location;
import sit.cp23ms2.sportconnect.exceptions.type.BadRequestException;
import sit.cp23ms2.sportconnect.repositories.LocationRepository;
import sit.cp23ms2.sportconnect.utils.ListMapper;

import java.util.List;

@Service
public class LocationService {
    @Autowired
    LocationRepository repository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    final private FieldError latErrorObj = new FieldError("createLocationDto",
            "latitude", "Latitude cannot more than 90 and cannot lower than -90");
    final private FieldError lngErrorObj = new FieldError("createLocationDto",
            "longitude", "Longitude cannot more than 180 and cannot lower than 180");

    public PageLocationDto getLocation(int pageNum, int pageSize, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page<Location> listLocations = repository.findAll(pageRequest);
        return modelMapper.map(listLocations, PageLocationDto.class);
    }

    public List<LocationDto> getLocationNoPaging(Double lat, Double lng, Integer radius) {
        //Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        List<Location> locations = repository.findAllLocationList(lat, lng, radius);
        return listMapper.mapList(locations, LocationDto.class, modelMapper);
    }

    public Location getById(Integer id) {
        Location location = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Location ID: " + id + " Not Found"));
        return location;
    }

    public Location create(CreateLocationDto newLocation, BindingResult result) throws MethodArgumentNotValidException, BadRequestException {
        if(newLocation.getLatitude() > 90.0 || newLocation.getLatitude() < -90.0) {
            result.addError(latErrorObj);
        }
        if(newLocation.getLongitude() > 180 || newLocation.getLongitude() < -180.0) {
            result.addError(lngErrorObj);
        }
        if (result.hasErrors()) throw new MethodArgumentNotValidException(null, result);
        Location createdLocation = new Location();
        try{
            Integer locationId = repository.insertWithEnum(newLocation.getName(), newLocation.getLatitude(), newLocation.getLongitude());
            createdLocation = modelMapper.map(newLocation, Location.class);
            createdLocation.setLocationId(locationId);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Something error on value");
        } catch (Exception e) {
            e.getMessage();
            throw new BadRequestException("Something error");
        }

        return createdLocation;
    }

    public Location update(UpdateLocationDto updateLocationDto, Integer id) {
        Location oldLocation = this.getById(id);
        if(updateLocationDto.getName() != null && !updateLocationDto.getName().trim().equals(""))
            oldLocation.setName(updateLocationDto.getName());
        return repository.saveAndFlush(oldLocation);
    }
}
