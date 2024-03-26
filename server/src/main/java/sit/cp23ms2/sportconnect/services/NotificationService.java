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
import sit.cp23ms2.sportconnect.dtos.notification.CreateNotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.NotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.PageNotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.UpdateNotificationDto;
import sit.cp23ms2.sportconnect.entities.Notification;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.enums.NotificationType;
import sit.cp23ms2.sportconnect.repositories.NotificationRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;

@Service
public class NotificationService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private NotificationRepository repository;
    @Autowired
    private UserRepository userRepository;

    public PageNotificationDto getNotification(int pageNum, int pageSize, String sortBy, Integer targetId) {
        //Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        Page<Notification> listNotifications = repository.findAllNotifications(pageRequest, targetId);
        PageNotificationDto pageNotificationDto = modelMapper.map(listNotifications, PageNotificationDto.class);
        return pageNotificationDto;
    }

    public Notification getById(Integer id) {
        Notification notification = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification: " + id + " Not Found"));
        return notification;
    }

    public Notification Create(CreateNotificationDto newNotification) {
        User user = userRepository.findById(newNotification.getTargetId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification: " + newNotification.getTargetId() + " Not Found"));
        Notification notification = modelMapper.map(newNotification, Notification.class);
        notification.setTargetId(user);
        repository.saveAndFlush(notification);
        return notification;
    }

    public NotificationDto update(UpdateNotificationDto updateNotification, Integer id) {
        Notification notification = getById(id);
        Notification updatedNotification = mapNotification(notification, updateNotification);
        return modelMapper.map(repository.saveAndFlush(updatedNotification), NotificationDto.class);
    }

    private Notification mapNotification(Notification existNotification, UpdateNotificationDto updateNotification) {
        if(updateNotification.getUnRead() != null)
            existNotification.setUnRead(updateNotification.getUnRead());
        if(updateNotification.getType() != null)
            existNotification.setType(NotificationType.valueOf(updateNotification.getType()));
        if(updateNotification.getMessage() != null && !updateNotification.getMessage().trim().equals("")) {
            existNotification.setMessage(updateNotification.getMessage());
        }
        return  existNotification;
    }

    public void delete(Integer id) {
        Notification notification = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification: " + id + " Not Found"));
        repository.deleteById(id);
    }
}
