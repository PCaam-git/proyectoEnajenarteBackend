package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.dto.UserInDto;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;


    public List<UserOutDto> findAll(String username, String email, String active) {

        // Convertir parámetros a variables finales para el stream. Si el filtro no se usa, devuelve null. Si se usa, aplica el valor del filtro
        final String finalUsername = username.isEmpty() ? null : username.toLowerCase();
        final String finalEmail = email.isEmpty() ? null : email.toLowerCase();
        final Boolean finalActive = active.isEmpty() ? null : Boolean.parseBoolean(active);

        // filtrado con stream
        List<User> filteredusers = userRepository.findAll().stream()
                .filter(user -> finalUsername == null || user.getUsername().toLowerCase().contains(finalUsername))
                .filter(user -> finalEmail == null || user.getEmail().toLowerCase().contains(finalEmail))
                .filter(user -> finalActive == null || user.isActive() == finalActive)
                .toList();

        // Mapear DTOs
        List<UserOutDto> userOutDtoList =
                modelMapper.map(filteredusers, new TypeToken<List<UserOutDto>>() {
                }.getType());

        return userOutDtoList;
    }

    // GET by id
    public UserOutDto findById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserOutDto.class);
    }

    // POST
    public UserOutDto add(UserInDto userInDto) {
        User user = modelMapper.map(userInDto, User.class);

        // generadas por el sistema
        user.setRole("user");
        user.setActive(true);
        user.setBalance(0);

        User newUser = userRepository.save(user);
        return modelMapper.map(newUser, UserOutDto.class);
    }

    // PUT
    public UserOutDto modify(long id, UserInDto userInDto) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        //Generado por el sistema
        String role = existingUser.getRole();
        boolean active = existingUser.isActive();
        float balance = existingUser.getBalance();


        modelMapper.map(userInDto, existingUser);
        existingUser.setId(id);

        existingUser.setRole(role);
        existingUser.setActive(active);
        existingUser.setBalance(balance);


        User updateUser = userRepository.save(existingUser);
        return modelMapper.map(updateUser, UserOutDto.class);
    }

    // DELETE
    public void delete(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
    }
}
