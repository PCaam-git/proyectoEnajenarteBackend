package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.RegistrationInDto;
import com.svalero.enajenarte.dto.RegistrationOutDto;
import com.svalero.enajenarte.exception.RegistrationNotFoundException;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.repository.RegistrationRepository;
import com.svalero.enajenarte.repository.UserRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import com.svalero.enajenarte.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTests {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    public void testFindAll() throws Exception {
        List<Registration> mockRegistrationList = List.of(
                new Registration(1L, LocalDate.of(2026, 1, 1), "CONF-1", false, 1, 0f, null, null, null),
                new Registration(2L, LocalDate.of(2026, 1, 2), "CONF-2", true, 2, 20f, 5, null, null)
        );

        List<RegistrationOutDto> modelMapperRegistrationOutDtoList = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 1), "CONF-1", false, 1, 0, 0, 0L, 0L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 2), "CONF-2", true, 2, 20, 5, 0L, 0L)
        );

        when(registrationRepository.findAll()).thenReturn(mockRegistrationList);
        when(modelMapper.map(mockRegistrationList, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(modelMapperRegistrationOutDtoList);

        List<RegistrationOutDto> actualRegistrationOutDtoList = registrationService.findAll("", "", "");

        assertEquals(2, actualRegistrationOutDtoList.size());
        verify(registrationRepository, times(1)).findAll();
        verify(registrationRepository, times(0)).findByIsPaid(true);
    }

    @Test
    public void testFindAllByIsPaid() throws Exception {
        List<Registration> registrationList = List.of(
                new Registration(1L, LocalDate.of(2026, 1, 2), "CONF-2", true, 2, 20, 5, null, null),
                new Registration(2L, LocalDate.of(2026, 1, 3), "CONF-3", true, 1, 10, 4, null, null)
        );

        List<RegistrationOutDto> modelMapperRegistrationOutDtoList = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 2), "CONF-2",true, 2, 20, 5, 0L, 0L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 3), "CONF-3", true, 1, 10, 4, 0L, 0L)
        );

        when(registrationRepository.findByIsPaid(true)).thenReturn(registrationList);
        when(modelMapper.map(registrationList, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(modelMapperRegistrationOutDtoList);

        List<RegistrationOutDto> actualRegistrationOutDtoList = registrationService.findAll("", "", "true");

        assertEquals(2, actualRegistrationOutDtoList.size());
        assertTrue(actualRegistrationOutDtoList.getFirst().isPaid());

        verify(registrationRepository, times(0)).findAll();
        verify(registrationRepository, times(1)).findByIsPaid(true);
    }

    @Test
    public void testFindAllByUserId() throws Exception {
        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        List<Registration> registrationList = List.of(
                new Registration(1L, LocalDate.of(2026, 1, 10), "CONF-10", false, 2, 0, null, userRepositoryUser, null),
                new Registration(2L, LocalDate.of(2026, 1, 11), "CONF-11", true, 1, 20, 5, userRepositoryUser, null)
        );

        List<RegistrationOutDto> modelMapperRegistrationOutDtoList = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), "CONF-10", false, 2, 0, 0, 1L, 10L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 11), "CONF-11", true, 1, 20, 5, 1L, 10L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(registrationRepository.findByUser(userRepositoryUser)).thenReturn(registrationList);
        when(modelMapper.map(registrationList, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(modelMapperRegistrationOutDtoList);

        List<RegistrationOutDto> actualRegistrationOutDtoList = registrationService.findAll("", "1", "");

        assertEquals(2, actualRegistrationOutDtoList.size());
        verify(userRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).findByUser(userRepositoryUser);
    }

    @Test
    public void testFindAllByUserId_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> registrationService.findAll("", "99", ""));

        verify(userRepository, times(1)).findById(99L);
        verify(registrationRepository, times(0)).findByUser(any(User.class));
    }

    @Test
    public void testFindAllByWorkshopId() throws Exception {
        Workshop workshopRepositoryWorkshop = new Workshop();
        workshopRepositoryWorkshop.setId(10L);

        List<Registration> registrationRepositoryRegistrationList = List.of(
                new Registration(1L, LocalDate.of(2026, 1, 10), "CONF-10", false, 2, 0, null, null, workshopRepositoryWorkshop),
                new Registration(2L, LocalDate.of(2026, 1, 11), "CONF-11", true, 1, 20, 5, null, workshopRepositoryWorkshop)
        );

        List<RegistrationOutDto> modelMapperRegistrationOutDtoList = List.of(
                new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), "CONF-10",  false, 2, 0, 0, 1L, 10L),
                new RegistrationOutDto(2L, LocalDate.of(2026, 1, 11), "CONF-11", true, 1, 20, 5, 2L, 10L)
        );

        when(workshopRepository.findById(10L)).thenReturn(Optional.of(workshopRepositoryWorkshop));
        when(registrationRepository.findByWorkshop(workshopRepositoryWorkshop)).thenReturn(registrationRepositoryRegistrationList);
        when(modelMapper.map(registrationRepositoryRegistrationList, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(modelMapperRegistrationOutDtoList);

        List<RegistrationOutDto> actualRegistrationOutDtoList = registrationService.findAll("10", "", "");

        assertEquals(2, actualRegistrationOutDtoList.size());
        verify(workshopRepository, times(1)).findById(10L);
        verify(registrationRepository, times(1)).findByWorkshop(workshopRepositoryWorkshop);
    }

    @Test
    public void testFindAllByWorkshopId_WorkshopNotFound() {
        when(workshopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class, () -> registrationService.findAll("99", "", ""));

        verify(workshopRepository, times(1)).findById(99L);
        verify(registrationRepository, times(0)).findByWorkshop(any(Workshop.class));
    }

    @Test
    public void testFindById() throws Exception {
        Registration registrationRepositoryRegistration = new Registration();
        registrationRepositoryRegistration.setId(7L);

        User user = new User();
        user.setId(1L);
        Workshop workshop = new Workshop();
        workshop.setId(10L);

        registrationRepositoryRegistration.setUser(user);
        registrationRepositoryRegistration.setWorkshop(workshop);

        RegistrationOutDto modelMapperRegistrationOutDto = new RegistrationOutDto();
        modelMapperRegistrationOutDto.setId(7L);

        when(registrationRepository.findById(7L)).thenReturn(Optional.of(registrationRepositoryRegistration));
        when(modelMapper.map(registrationRepositoryRegistration, RegistrationOutDto.class)).thenReturn(modelMapperRegistrationOutDto);

        RegistrationOutDto actualRegistrationOutDto = registrationService.findById(7L);

        assertNotNull(actualRegistrationOutDto);
        assertEquals(7L, actualRegistrationOutDto.getId());
        verify(registrationRepository, times(1)).findById(7L);
    }

    @Test
    public void testFindById_NotFound() {
        when(registrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RegistrationNotFoundException.class, () -> registrationService.findById(99L));
        verify(registrationRepository, times(1)).findById(99L);
    }

    @Test
    public void testAdd() throws Exception {
        RegistrationInDto registrationInDto = new RegistrationInDto(2, 1L, 10L);

        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        Workshop workshopRepositoryWorkshop = new Workshop();
        workshopRepositoryWorkshop.setId(10L);

        Registration modelMapperRegistration = new Registration();
        Registration registrationRepositorySavedRegistration = new Registration();
        registrationRepositorySavedRegistration.setId(100L);
        registrationRepositorySavedRegistration.setUser(userRepositoryUser);
        registrationRepositorySavedRegistration.setWorkshop(workshopRepositoryWorkshop);

        RegistrationOutDto modelMapperRegistrationOutDto = new RegistrationOutDto();
        modelMapperRegistrationOutDto.setId(100L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(workshopRepository.findById(10L)).thenReturn(Optional.of(workshopRepositoryWorkshop));
        when(modelMapper.map(registrationInDto, Registration.class)).thenReturn(modelMapperRegistration);
        when(registrationRepository.save(modelMapperRegistration)).thenReturn(registrationRepositorySavedRegistration);
        when(modelMapper.map(registrationRepositorySavedRegistration, RegistrationOutDto.class)).thenReturn(modelMapperRegistrationOutDto);

        RegistrationOutDto actualRegistrationOutDto = registrationService.add(registrationInDto);

        assertNotNull(actualRegistrationOutDto);
        assertEquals(100L, actualRegistrationOutDto.getId());

        verify(userRepository, times(1)).findById(1L);
        verify(workshopRepository, times(1)).findById(10L);
        verify(registrationRepository, times(1)).save(modelMapperRegistration);
    }

    @Test
    public void testAdd_UserNotFound() {
        RegistrationInDto registrationInDto = new RegistrationInDto(2, 99L, 10L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> registrationService.add(registrationInDto));

        verify(userRepository, times(1)).findById(99L);
        verify(registrationRepository, times(0)).save(any(Registration.class));
    }

    @Test
    public void testAdd_WorkshopNotFound() {
        RegistrationInDto registrationInDto = new RegistrationInDto(2, 1L, 99L);

        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(workshopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class, () -> registrationService.add(registrationInDto));

        verify(userRepository, times(1)).findById(1L);
        verify(workshopRepository, times(1)).findById(99L);
        verify(registrationRepository, times(0)).save(any(Registration.class));
    }

    @Test
    public void testDelete() throws Exception {
        Registration registrationRepositoryRegistration = new Registration();
        registrationRepositoryRegistration.setId(1L);

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registrationRepositoryRegistration));

        registrationService.delete(1L);

        verify(registrationRepository, times(1)).findById(1L);
        verify(registrationRepository, times(1)).delete(registrationRepositoryRegistration);
    }

    @Test
    public void testDelete_NotFound() {
        when(registrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RegistrationNotFoundException.class, () -> registrationService.delete(99L));

        verify(registrationRepository, times(1)).findById(99L);
        verify(registrationRepository, times(0)).delete(any(Registration.class));
    }

    @Test
    public void testModify() throws Exception {
        long registrationId = 5L;

        Registration existingRegistration = new Registration();
        existingRegistration.setId(registrationId);
        existingRegistration.setRegistrationDate(LocalDate.of(2026, 1, 1));
        existingRegistration.setConfirmationCode("CONF-OLD");
        existingRegistration.setPaid(true);
        existingRegistration.setAmountPaid(20);
        existingRegistration.setRating(4);

        RegistrationInDto registrationInDto = new RegistrationInDto(3, 1L, 10L);

        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        Workshop workshopRepositoryWorkshop = new Workshop();
        workshopRepositoryWorkshop.setId(10L);

        Registration savedRegistration = new Registration();
        savedRegistration.setId(registrationId);
        savedRegistration.setUser(userRepositoryUser); // asigna el usuario
        savedRegistration.setWorkshop(workshopRepositoryWorkshop); // asigna el taller

        RegistrationOutDto modelMapperRegistrationOutDto = new RegistrationOutDto();
        modelMapperRegistrationOutDto.setId(registrationId);

        when(registrationRepository.findById(registrationId)).thenReturn(Optional.of(existingRegistration));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(workshopRepository.findById(10L)).thenReturn(Optional.of(workshopRepositoryWorkshop));

        doNothing().when(modelMapper).map(registrationInDto, existingRegistration);

        when(registrationRepository.save(existingRegistration)).thenReturn(savedRegistration);
        when(modelMapper.map(savedRegistration, RegistrationOutDto.class)).thenReturn(modelMapperRegistrationOutDto);

        RegistrationOutDto actualRegistrationOutDto = registrationService.modify(registrationId, registrationInDto);

        assertNotNull(actualRegistrationOutDto);
        assertEquals(registrationId, actualRegistrationOutDto.getId());

        verify(registrationRepository, times(1)).findById(registrationId);
        verify(registrationRepository, times(1)).save(existingRegistration);
    }

    @Test
    public void testModify_RegistrationNotFound() {
        RegistrationInDto registrationInDto = new RegistrationInDto(3, 1L, 10L);

        when(registrationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RegistrationNotFoundException.class, () -> registrationService.modify(99L, registrationInDto));

        verify(registrationRepository, times(1)).findById(99L);
        verify(registrationRepository, times(0)).save(any(Registration.class));
    }

    @Test
    public void testModify_UserNotFound() {
        long registrationId = 5L;

        Registration existingRegistration = new Registration();
        existingRegistration.setId(registrationId);

        RegistrationInDto registrationInDto = new RegistrationInDto(3, 99L, 10L);

        when(registrationRepository.findById(registrationId)).thenReturn(Optional.of(existingRegistration));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> registrationService.modify(registrationId, registrationInDto));

        verify(userRepository, times(1)).findById(99L);
        verify(registrationRepository, times(0)).save(any(Registration.class));
    }

    @Test
    public void testModify_WorkshopNotFound() {
        long registrationId = 5L;

        Registration existingRegistration = new Registration();
        existingRegistration.setId(registrationId);

        RegistrationInDto registrationInDto = new RegistrationInDto(3, 1L, 99L);

        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        when(registrationRepository.findById(registrationId)).thenReturn(Optional.of(existingRegistration));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(workshopRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WorkshopNotFoundException.class, () -> registrationService.modify(registrationId, registrationInDto));

        verify(workshopRepository, times(1)).findById(99L);
        verify(registrationRepository, times(0)).save(any(Registration.class));
    }
}
