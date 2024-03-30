package sit.cp23ms2.sportconnect.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import sit.cp23ms2.sportconnect.dtos.notification.CreateNotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.NotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.PageNotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.UpdateNotificationDto;
import sit.cp23ms2.sportconnect.entities.Notification;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.services.NotificationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
@Component
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping
    public PageNotificationDto getNotification(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               @RequestParam(defaultValue = "notificationId") String sortBy,
                                               @RequestParam(required = false) Integer targetId) {

        return notificationService.getNotification(page, pageSize, sortBy, targetId);

    }

    @GetMapping("/{id}")
    public NotificationDto getById(@PathVariable Integer id) {
        return modelMapper.map(notificationService.getById(id), NotificationDto.class) ;
    }

    @PostMapping
    public NotificationDto createNotification(@Valid @ModelAttribute CreateNotificationDto createNotificationDto) {
        return modelMapper.map(notificationService.Create(createNotificationDto), NotificationDto.class);
    }

    @PatchMapping("/{id}")
    public NotificationDto update(@Valid @ModelAttribute UpdateNotificationDto updateNotificationDto,
                                  @PathVariable Integer id) throws ForbiddenException {
        return notificationService.update(updateNotificationDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws ForbiddenException {
        notificationService.delete(id);
    }
}
