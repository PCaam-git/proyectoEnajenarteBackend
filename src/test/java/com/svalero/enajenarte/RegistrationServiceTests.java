package com.svalero.enajenarte;

import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.RegistrationInDto;
import com.svalero.enajenarte.dto.RegistrationOutDto;
import com.svalero.enajenarte.exception.*;
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
        Registration registration1 = new Registration();
        registration1.setId(1L);
        registration1.setRegistrationDate(LocalDate.of(2026, 1, 1));
        registration1.setConfirmationCode("CONF-1");
        registration1.setPaid(false);
        registration1.setNumberOfTickets(1);
        registration1.setAmountPaid(0f);
        registration1.setRating(null);
        registration1.setStatus("pending");
        registration1.setPaymentStatus("pending");

        Registration registration2 = new Registration();
        registration2.setId(2L);
        registration2.setRegistrationDate(LocalDate.of(2026, 1, 2));
        registration2.setConfirmationCode("CONF-2");
        registration2.setPaid(true);
        registration2.setNumberOfTickets(2);
        registration2.setAmountPaid(20f);
        registration2.setRating(5);
        registration2.setStatus("pending");
        registration2.setPaymentStatus("pending");

        List<Registration> mockRegistrationList = List.of(registration1, registration2);

        RegistrationOutDto dto1 = new RegistrationOutDto(1L, LocalDate.of(2026, 1, 1), "CONF-1", false, 1, 0, 0, "pending", "pending", 0L, 0L);
        RegistrationOutDto dto2 = new RegistrationOutDto(2L, LocalDate.of(2026, 1, 2), "CONF-2", true, 2, 20, 5, "pending", "pending", 0L, 0L);

        List<RegistrationOutDto> expectedDtos = List.of(dto1, dto2);

        when(registrationRepository.findAll()).thenReturn(mockRegistrationList);
        when(modelMapper.map(mockRegistrationList, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(expectedDtos);

        List<RegistrationOutDto> actualRegistrationOutDtoList = registrationService.findAll("", "", "");

        assertEquals(2, actualRegistrationOutDtoList.size());
        verify(registrationRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllByIsPaid() throws Exception {
        Registration registration1 = new Registration();
        registration1.setId(1L);
        registration1.setRegistrationDate(LocalDate.of(2026, 1, 2));
        registration1.setConfirmationCode("CONF-2");
        registration1.setPaid(true);
        registration1.setNumberOfTickets(2);
        registration1.setAmountPaid(20);
        registration1.setRating(5);
        registration1.setStatus("pending");
        registration1.setPaymentStatus("pending");

        Registration registration2 = new Registration();
        registration2.setId(2L);
        registration2.setRegistrationDate(LocalDate.of(2026, 1, 3));
        registration2.setConfirmationCode("CONF-3");
        registration2.setPaid(true);
        registration2.setNumberOfTickets(1);
        registration2.setAmountPaid(10);
        registration2.setRating(4);
        registration2.setStatus("pending");
        registration2.setPaymentStatus("pending");

        List<Registration> allRegistrations = List.of(registration1, registration2);

        RegistrationOutDto dto1 = new RegistrationOutDto(1L, LocalDate.of(2026, 1, 2), "CONF-2", true, 2, 20, 5, "pending", "pending", 0L, 0L);
        RegistrationOutDto dto2 = new RegistrationOutDto(2L, LocalDate.of(2026, 1, 3), "CONF-3", true, 1, 10, 4, "pending", "pending", 0L, 0L);

        List<RegistrationOutDto> expectedDtos = List.of(dto1, dto2);

        when(registrationRepository.findAll()).thenReturn(allRegistrations);
        when(modelMapper.map(allRegistrations, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(expectedDtos);

        List<RegistrationOutDto> actualRegistrationOutDtoList = registrationService.findAll("", "", "true");

        assertEquals(2, actualRegistrationOutDtoList.size());
        assertTrue(actualRegistrationOutDtoList.get(0).isPaid());
        assertTrue(actualRegistrationOutDtoList.get(1).isPaid());

        verify(registrationRepository, times(1)).findAll();
    }

    @Test
    public void testFindAllByUserId() throws Exception {
        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        Registration registration1 = new Registration();
        registration1.setId(1L);
        registration1.setRegistrationDate(LocalDate.of(2026, 1, 10));
        registration1.setConfirmationCode("CONF-10");
        registration1.setPaid(false);
        registration1.setNumberOfTickets(2);
        registration1.setAmountPaid(0);
        registration1.setRating(null);
        registration1.setStatus("pending");
        registration1.setPaymentStatus("pending");
        registration1.setUser(userRepositoryUser);

        Registration registration2 = new Registration();
        registration2.setId(2L);
        registration2.setRegistrationDate(LocalDate.of(2026, 1, 11));
        registration2.setConfirmationCode("CONF-11");
        registration2.setPaid(true);
        registration2.setNumberOfTickets(1);
        registration2.setAmountPaid(20);
        registration2.setRating(5);
        registration2.setStatus("pending");
        registration2.setPaymentStatus("pending");
        registration2.setUser(userRepositoryUser);

        List<Registration> allRegistrations = List.of(registration1, registration2);

        RegistrationOutDto dto1 = new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), "CONF-10", false, 2, 0, 0, "pending", "pending", 1L, 0L);
        RegistrationOutDto dto2 = new RegistrationOutDto(2L, LocalDate.of(2026, 1, 11), "CONF-11", true, 1, 20, 5, "pending", "pending", 1L, 0L);

        List<RegistrationOutDto> expectedDtos = List.of(dto1, dto2);

        when(registrationRepository.findAll()).thenReturn(allRegistrations);
        when(modelMapper.map(allRegistrations, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(expectedDtos);

        List<RegistrationOutDto> actualRegistrationOutDtoList = registrationService.findAll("", "1", "");

        assertEquals(2, actualRegistrationOutDtoList.size());
        assertEquals(1L, actualRegistrationOutDtoList.get(0).getUserId());
        assertEquals(1L, actualRegistrationOutDtoList.get(1).getUserId());

        verify(registrationRepository, times(1)).findAll();
    }

//    @Test
//    public void testFindAllByUserId_UserNotFound() {
//        when(userRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(UserNotFoundException.class, () -> registrationService.findAll("", "99", ""));
//
//        verify(userRepository, times(1)).findById(99L);
//        verify(registrationRepository, times(0)).findByUser(any(User.class));
//    }

    @Test
    public void testFindAllByWorkshopId() throws Exception {
        Workshop workshopRepositoryWorkshop = new Workshop();
        workshopRepositoryWorkshop.setId(10L);

        Registration registration1 = new Registration();
        registration1.setId(1L);
        registration1.setRegistrationDate(LocalDate.of(2026, 1, 10));
        registration1.setConfirmationCode("CONF-10");
        registration1.setPaid(false);
        registration1.setNumberOfTickets(2);
        registration1.setAmountPaid(0);
        registration1.setRating(null);
        registration1.setStatus("pending");
        registration1.setPaymentStatus("pending");
        registration1.setWorkshop(workshopRepositoryWorkshop);

        Registration registration2 = new Registration();
        registration2.setId(2L);
        registration2.setRegistrationDate(LocalDate.of(2026, 1, 11));
        registration2.setConfirmationCode("CONF-11");
        registration2.setPaid(true);
        registration2.setNumberOfTickets(1);
        registration2.setAmountPaid(20);
        registration2.setRating(5);
        registration2.setStatus("pending");
        registration2.setPaymentStatus("pending");
        registration2.setWorkshop(workshopRepositoryWorkshop);

        List<Registration> allRegistrations = List.of(registration1, registration2);

        RegistrationOutDto dto1 = new RegistrationOutDto(1L, LocalDate.of(2026, 1, 10), "CONF-10", false, 2, 0, 0, "pending", "pending", 0L, 10L);
        RegistrationOutDto dto2 = new RegistrationOutDto(2L, LocalDate.of(2026, 1, 11), "CONF-11", true, 1, 20, 5, "pending", "pending", 0L, 10L);

        List<RegistrationOutDto> expectedDtos = List.of(dto1, dto2);

        when(registrationRepository.findAll()).thenReturn(allRegistrations);
        when(modelMapper.map(allRegistrations, new TypeToken<List<RegistrationOutDto>>() {}.getType()))
                .thenReturn(expectedDtos);

        List<RegistrationOutDto> actualRegistrationOutDtoList = registrationService.findAll("10", "", "");

        assertEquals(2, actualRegistrationOutDtoList.size());
        assertEquals(10L, actualRegistrationOutDtoList.get(0).getWorkshopId());
        assertEquals(10L, actualRegistrationOutDtoList.get(1).getWorkshopId());

        verify(registrationRepository, times(1)).findAll();
    }

//    @Test
//    public void testFindAllByWorkshopId_WorkshopNotFound() {
//        when(workshopRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(WorkshopNotFoundException.class, () -> registrationService.findAll("99", "", ""));
//
//        verify(workshopRepository, times(1)).findById(99L);
//        verify(registrationRepository, times(0)).findByWorkshop(any(Workshop.class));
//    }

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

        workshopRepositoryWorkshop.setMaxCapacity(20);

        Registration modelMapperRegistration = new Registration();
        Registration registrationRepositorySavedRegistration = new Registration();
        registrationRepositorySavedRegistration.setId(100L);
        registrationRepositorySavedRegistration.setUser(userRepositoryUser);
        registrationRepositorySavedRegistration.setWorkshop(workshopRepositoryWorkshop);

        RegistrationOutDto modelMapperRegistrationOutDto = new RegistrationOutDto();
        modelMapperRegistrationOutDto.setId(100L);
        modelMapperRegistration.setNumberOfTickets(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(workshopRepository.findById(10L)).thenReturn(Optional.of(workshopRepositoryWorkshop));
        when(registrationRepository.existsByUserIdAndWorkshopId(1L, 10L)).thenReturn(false);
        when(registrationRepository.findByWorkshop(workshopRepositoryWorkshop)).thenReturn(List.of());
        when(modelMapper.map(registrationInDto, Registration.class)).thenReturn(modelMapperRegistration);
        when(registrationRepository.save(modelMapperRegistration)).thenReturn(registrationRepositorySavedRegistration);
        when(modelMapper.map(registrationRepositorySavedRegistration, RegistrationOutDto.class)).thenReturn(modelMapperRegistrationOutDto);

        RegistrationOutDto actualRegistrationOutDto = registrationService.add(registrationInDto);

        assertNotNull(actualRegistrationOutDto);
        assertEquals(100L, actualRegistrationOutDto.getId());

        verify(userRepository, times(1)).findById(1L);
        verify(workshopRepository, times(1)).findById(10L);
        verify(registrationRepository, times(1)).save(modelMapperRegistration);
        assertEquals("CONFIRMED", modelMapperRegistration.getStatus());
        assertEquals("PENDING", modelMapperRegistration.getPaymentStatus());
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

    @Test
    public void testAdd_DuplicateRegistration() {
        RegistrationInDto registrationInDto = new RegistrationInDto(2, 1L, 10L);

        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        Workshop workshopRepositoryWorkshop = new Workshop();
        workshopRepositoryWorkshop.setId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(workshopRepository.findById(10L)).thenReturn(Optional.of(workshopRepositoryWorkshop));
        when(registrationRepository.existsByUserIdAndWorkshopId(1L, 10L)).thenReturn(true);

        assertThrows(DuplicateRegistrationException.class, () -> registrationService.add(registrationInDto));

        verify(registrationRepository, times(1)).existsByUserIdAndWorkshopId(1L, 10L);
        verify(registrationRepository, times(0)).save(any(Registration.class));
    }

    @Test
    public void testAdd_WorkshopCapacityExceeded() {
        RegistrationInDto registrationInDto = new RegistrationInDto(2, 1L, 10L);

        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        Workshop workshopRepositoryWorkshop = new Workshop();
        workshopRepositoryWorkshop.setId(10L);
        workshopRepositoryWorkshop.setMaxCapacity(20);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(workshopRepository.findById(10L)).thenReturn(Optional.of(workshopRepositoryWorkshop));
        when(registrationRepository.existsByUserIdAndWorkshopId(1L, 10L)).thenReturn(false);
        Registration existingRegistration = new Registration();
        existingRegistration.setNumberOfTickets(20);

        when(registrationRepository.findByWorkshop(workshopRepositoryWorkshop))
                .thenReturn(List.of(existingRegistration));

        assertThrows(WorkshopCapacityExceededException.class, () -> registrationService.add(registrationInDto));

        verify(registrationRepository, times(1)).findByWorkshop(workshopRepositoryWorkshop);
        verify(registrationRepository, times(0)).save(any(Registration.class));
    }

    @Test
    public void testAdd_InvalidNumberOfTickets() {
        RegistrationInDto registrationInDto = new RegistrationInDto(0, 1L, 10L);

        User userRepositoryUser = new User();
        userRepositoryUser.setId(1L);

        Workshop workshopRepositoryWorkshop = new Workshop();
        workshopRepositoryWorkshop.setId(10L);
        workshopRepositoryWorkshop.setMaxCapacity(20);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userRepositoryUser));
        when(workshopRepository.findById(10L)).thenReturn(Optional.of(workshopRepositoryWorkshop));
        when(registrationRepository.existsByUserIdAndWorkshopId(1L, 10L)).thenReturn(false);
        when(registrationRepository.findByWorkshop(workshopRepositoryWorkshop)).thenReturn(List.of());

        assertThrows(InvalidRegistrationStateException.class, () -> registrationService.add(registrationInDto));

        verify(userRepository, times(1)).findById(1L);
        verify(workshopRepository, times(1)).findById(10L);
        verify(registrationRepository, times(1)).existsByUserIdAndWorkshopId(1L, 10L);
        verify(registrationRepository, times(1)).findByWorkshop(workshopRepositoryWorkshop);
        verify(registrationRepository, times(0)).save(any(Registration.class));
    }
}
