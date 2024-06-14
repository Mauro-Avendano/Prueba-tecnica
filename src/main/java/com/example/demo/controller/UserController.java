package com.example.demo.controller;

import com.example.demo.JwtUtil;
import com.example.demo.UserService;
import com.example.demo.database.PhoneNumber;
import com.example.demo.database.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public  ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        try {
            ResponseEntity<?> requestValidation = validateRequest(userRequest);
            if (requestValidation != null) {
                return requestValidation;
            }

            User user = new User();
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(user.getPassword());
            user.setToken(JwtUtil.generateToken(userRequest.getEmail(), userRequest.getName()));
            List<PhoneRequest> phoneRequests = userRequest.getPhones();

            List<PhoneNumber> phones = phoneRequests.stream()
                    .map(phoneRequest -> {
                        PhoneNumber phone = new PhoneNumber();
                        phone.setNumber(phoneRequest.getNumber());
                        phone.setCountryCode(phoneRequest.getCountrycode());
                        phone.setCityCode(phoneRequest.getCitycode());
                        return phone;
                    })
                    .collect(Collectors.toList());

            user.setPhoneNumbers(phones);

            User createdUser = userService.createUser(user);

            return new ResponseEntity<>(buildUserResponse(createdUser), HttpStatus.CREATED);
        } catch (Exception e) {
            if (e.getMessage().contains("email")) {
                return new ResponseEntity<>("{\"mensaje\": \"el correo ya existe\"}", HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(String.format("{\"mensaje\": \"%s\"}", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserResponse buildUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setCreated(user.getCreated());
        userResponse.setModified(user.getModified());
        userResponse.setLast_login(user.getLast_login());
        userResponse.setIsactive(user.getIsActive());
        userResponse.setJwt(user.getToken());

        return userResponse;
    }

    private ResponseEntity<?> validateRequest(UserRequest request) {
        String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$";
        Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

        if (request.getEmail() == null || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            return new ResponseEntity<>("{\"mensaje\": \"correo inválido\"}", HttpStatus.BAD_REQUEST);
        }

        List<PhoneRequest> phones = request.getPhones();
        if (phones == null || phones.isEmpty()) {
            return new ResponseEntity<>("{\"mensaje\": \"falta la propiedad phones o su contenido es inválido\"}", HttpStatus.BAD_REQUEST);
        }

        for (PhoneRequest phone : phones) {
            if (phone.getNumber() == null || phone.getCitycode() == null || phone.getCountrycode() == null) {
                return new ResponseEntity<>("{\"mensaje\": \"falta la propiedad phones o su contenido es inválido\"}", HttpStatus.BAD_REQUEST);
            }
        }

        if (request.getName() == null || request.getName().isEmpty()) {
            return new ResponseEntity<>("{\"mensaje\": \"falta la propiedad nombre\"}", HttpStatus.BAD_REQUEST);
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return new ResponseEntity<>("{\"mensaje\": \"falta la propiedad password\"}", HttpStatus.BAD_REQUEST);
        }

        if (!PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            return new ResponseEntity<>("{\"mensaje\": \"por favor introduzca una password con al menos 8 carácteres y un número\"}", HttpStatus.BAD_REQUEST);
        }

        return null;
    }
}
