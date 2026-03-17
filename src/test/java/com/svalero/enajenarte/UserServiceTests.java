package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.dto.UserInDto;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.repository.UserRepository;
import com.svalero.enajenarte.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() {
        List<User> mockUserList = List.of(
                new User(1L, "patricia", "pass1", "patricia@mail.com", "Patricia User"/*LocalDate.now()*/, 40, true, 0, "user", null),
                new User(2L, "mario", "pass2", "mario@mail.com", "Mario User"
                        /*LocalDate.now()*/, 35, true, 0, "user", null)
        );


        List<UserOutDto> modelMapperOut = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user"),
                new UserOutDto(2L, "mario", "mario@mail.com", "Mario User", "user")
        );

        when(userRepository.findAll()).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<UserOutDto> actualUserList = userService.findAll("", "", "");

        assertEquals(2, actualUserList.size());
        assertEquals("patricia", actualUserList.getFirst().getUsername());
        assertEquals("mario", actualUserList.getLast().getUsername());

        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(0)).findByUsernameContainingIgnoreCase("");
        verify(userRepository, times(0)).findByEmailContainingIgnoreCase("");
        verify(userRepository, times(0)).findByActive(true);
    }

    @Test
    public void testFindAllByUsername() {
        List<User> mockUserList = List.of(
                new User(1L, "patricia", "pass1", "patricia@mail.com", "Patricia User",
                        40, true, 0, "user", null),
                new User(2L, "patricia.dev", "pass2", "patricia.dev@mail.com", "Patricia Dev",
                         22, true, 0, "user", null)
        );

        List<UserOutDto> modelMapperOut = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user"),
                new UserOutDto(2L, "patricia.dev", "patricia.dev@mail.com", "Patricia Dev", "user")
        );

        when(userRepository.findByUsernameContainingIgnoreCase("patricia")).thenReturn(mockUserList);
        when(modelMapper.map(mockUserList, new TypeToken<List<UserOutDto>>() {}.getType())).thenReturn(modelMapperOut);

        List<UserOutDto> actualUserList = userService.findAll("patricia", "", "");

        assertEquals(2, actualUserList.size());
        assertEquals("patricia", actualUserList.getFirst().getUsername());
        assertEquals("patricia.dev", actualUserList.getLast().getUsername());

        verify(userRepository, times(0)).findAll();
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase("patricia");
    }

    @Test
    public void testFindAllByEmail() {
        List<User> userRepositoryUsers = List.of(
                new User(1L, "patricia", "pass1", "patricia@mail.com", "Patricia User",
                        40, true, 0, "user", null)
        );

        List<UserOutDto> modelMapperUsersOutDto = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user")
        );

        when(userRepository.findByEmailContainingIgnoreCase("mail")).thenReturn(userRepositoryUsers);
        when(modelMapper.map(userRepositoryUsers, new TypeToken<List<UserOutDto>>() {}.getType()))
                .thenReturn(modelMapperUsersOutDto);

        List<UserOutDto> actualUsersOutDto = userService.findAll("", "mail", "");

        assertEquals(1, actualUsersOutDto.size());
        assertEquals("patricia@mail.com", actualUsersOutDto.getFirst().getEmail());

        verify(userRepository, times(1)).findByEmailContainingIgnoreCase("mail");
        verify(userRepository, times(0)).findAll();
    }

    @Test
    public void testFindAllByActive() {
        List<User> userRepositoryUsers = List.of(
                new User(1L, "patricia", "pass1", "patricia@mail.com", "Patricia User",
                        40, true, 0, "user", null)
        );

        List<UserOutDto> modelMapperUsersOutDto = List.of(
                new UserOutDto(1L, "patricia", "patricia@mail.com", "Patricia User", "user")
        );

        when(userRepository.findByActive(true)).thenReturn(userRepositoryUsers);
        when(modelMapper.map(userRepositoryUsers, new TypeToken<List<UserOutDto>>() {}.getType()))
                .thenReturn(modelMapperUsersOutDto);

        List<UserOutDto> actualUsersOutDto = userService.findAll("", "", "true");

        assertEquals(1, actualUsersOutDto.size());
        assertEquals("patricia", actualUsersOutDto.getFirst().getUsername());

        verify(userRepository, times(1)).findByActive(true);
        verify(userRepository, times(0)).findAll();
    }

    @Test
    public void testFindById() throws UserNotFoundException {
        User user = new User();
        user.setId(12L);

        UserOutDto userOutDto = new UserOutDto();
        userOutDto.setId(12L);

        when(userRepository.findById(12L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserOutDto.class)).thenReturn(userOutDto);

        UserOutDto actualUserOutDto = userService.findById(12L);

        assertNotNull(actualUserOutDto);
        assertEquals(12L, actualUserOutDto.getId());
    }

    @Test
    public void testFindById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));

        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    public void testAdd() {
        UserInDto userInDto = new UserInDto("patricia", "password", "patricia@mail.com", "Patricia User", 25
        );

        User mappedUser = new User();
        User savedUser = new User();
        savedUser.setId(10L);

        UserOutDto expectedUserOutDto = new UserOutDto(10L, "patricia", "patricia@mail.com", "Patricia User", "user"
        );

        when(modelMapper.map(userInDto, User.class)).thenReturn(mappedUser);
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserOutDto.class)).thenReturn(expectedUserOutDto);

        UserOutDto actualUserOutDto = userService.add(userInDto);

        assertNotNull(actualUserOutDto);
        assertEquals(10L, actualUserOutDto.getId());
        assertEquals("patricia", actualUserOutDto.getUsername());
        assertEquals("user", actualUserOutDto.getRole());

        assertEquals("user", mappedUser.getRole());
        assertTrue(mappedUser.isActive());
        assertEquals(0, mappedUser.getBalance());
//        assertNotNull(mappedUser.getRegistrationDate());

        verify(userRepository, times(1)).save(mappedUser);
    }

    @Test
    public void testDelete() throws UserNotFoundException {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDelete_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.delete(99L));

        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, times(0)).delete(any(User.class));
    }

    @Test
    public void testModify() throws UserNotFoundException, WorkshopNotFoundException {
        long userIdentifier = 5L;

        UserInDto userInDto = new UserInDto(
                "updatedUsername", "updatedPassword", "updated@mail.com", "Updated Full Name", 30
        );

        User existingUser = new User();
        existingUser.setId(userIdentifier);
        existingUser.setRole("user");
        existingUser.setActive(true);
        existingUser.setBalance(0);
//        existingUser.setRegistrationDate(LocalDate.of(2026, 1, 1));

        User savedUser = new User();
        savedUser.setId(userIdentifier);

        UserOutDto expectedUserOutDto = new UserOutDto(
                userIdentifier,
                "updatedUsername", "updated@mail.com", "Updated Full Name", "user"
        );

        when(userRepository.findById(userIdentifier)).thenReturn(Optional.of(existingUser));
        doNothing().when(modelMapper).map(userInDto, existingUser);
        when(userRepository.save(existingUser)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserOutDto.class)).thenReturn(expectedUserOutDto);

        UserOutDto actualUserOutDto = userService.modify(userIdentifier, userInDto);

        assertNotNull(actualUserOutDto);
        assertEquals(userIdentifier, actualUserOutDto.getId());
        assertEquals("updatedUsername", actualUserOutDto.getUsername());

        // Preservación de campos del sistema (no vienen del cliente)
        assertEquals("user", existingUser.getRole());
        assertTrue(existingUser.isActive());
        assertEquals(0, existingUser.getBalance());
//        assertEquals(LocalDate.of(2026, 1, 1), existingUser.getRegistrationDate());

        verify(userRepository, times(1)).findById(userIdentifier);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testModify_NotFound() {
        long userIdentifier = 99L;

        UserInDto userInDto = new UserInDto("updatedUsername", "updatedPassword", "updated@mail.com", "Updated Full Name", 30
        );

        when(userRepository.findById(userIdentifier)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.modify(userIdentifier, userInDto));

        verify(userRepository, times(1)).findById(userIdentifier);
        verify(userRepository, times(0)).save(any(User.class));
    }

}
