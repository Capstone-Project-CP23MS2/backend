package sit.cp23ms2.sportconnect.testClass;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sit.cp23ms2.sportconnect.dtos.user.PageUserDto;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.repositories.UserRepository;
import sit.cp23ms2.sportconnect.services.UserService;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetById_found() {
        // เตรียมข้อมูลสำหรับทดสอบ
        User user = new User();
        user.setUserId(1);

        // กำหนดพฤติกรรมของ mock object
        when(repository.findById(1)).thenReturn(Optional.of(user));

        // เรียกใช้ method ที่ต้องการทดสอบ
        User foundUser = userService.getById(1);

        // ตรวจสอบผลลัพธ์
        assertEquals(user, foundUser);
    }

    @Test
    public void testGetUser() {
        // เตรียมข้อมูลสำหรับทดสอบ
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUserId(1);
        users.add(user);
        PageImpl<User> pageUsers = new PageImpl<>(users, PageRequest.of(0, 10), 100);

        // กำหนดพฤติกรรมของ mock object
        when(repository.findAllUsers(any(Pageable.class))).thenReturn(pageUsers);

        // เรียกใช้ method ที่ต้องการทดสอบ
        PageUserDto pageUserDto = userService.getUser(0, 10, "username");

        // ตรวจสอบผลลัพธ์
        assertEquals(1, pageUserDto.getTotalElements());
        assertEquals(10, pageUserDto.getTotalPages());
    }
}
