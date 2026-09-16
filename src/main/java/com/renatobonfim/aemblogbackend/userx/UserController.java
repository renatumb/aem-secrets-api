package com.renatobonfim.aemblogbackend.userx;

import com.renatobonfim.aemblogbackend.customExceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    public UserService userService;
    /**/
    @Operation(summary = "Find all users (default: 5 users per request) ")
    @GetMapping
    public ResponseEntity<Page<User>> findAllUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                                   @RequestParam(value = "sort", defaultValue = "asc") String sort,
                                                   @RequestParam(value = "fields", defaultValue = "id") String properties) {
        return ResponseEntity.ok().body(userService.findAllUsers(page, size, sort, properties.split(",")));
    }


    @Operation(summary = "Find an User by its #Id ")
    @GetMapping("/{userID}")
    public ResponseEntity<User> findUserByID(@PathVariable("userID") String userId) throws UserNotFoundException {
        return ResponseEntity.ok().body(userService.findUserByID(userId));
    }

    @Operation(summary = "Update an user ", description = "DESCRIPTION")
    @PatchMapping(value = "/{userId}" )
    public ResponseEntity<User> updateUser(@PathVariable("userId") String userID,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "about", required = false) String about,
                                           @RequestParam(value = "accessLevel", required = false) String accessLevel,
                                           @RequestParam(value = "accountLocked", required = false) Boolean accountLocked,
                                           @RequestParam(value = "password", required = false) String password,
                                           @RequestParam(value = "photo", required = false) MultipartFile photoProfile
    ) throws UserNotFoundException {

        User userPayload = new User();

        userPayload.setAbout(name);
        userPayload.setAbout(about);
        userPayload.setAccessLevel(Objects.nonNull(accessLevel) ? AccessLevel.valueOf(accessLevel) : null);
        userPayload.setAccountLocked(accountLocked);
        userPayload.setPassword(password);

        User updatedUser = userService.updateUser(userPayload, userID, photoProfile);

        return ResponseEntity.ok(updatedUser);
    }

//    @Operation(summary = "Delete an User by its #Id )")
//    @DeleteMapping("/{userID}")
//    public ResponseEntity deleteUser(@PathVariable("userID") String userId) throws UserNotFoundException {
//        userService.deleteUser(userId);
//        return ResponseEntity.noContent().build();
//    }

    @Operation(summary = "Retrieve a User photo")
    @GetMapping(value = "/profilephoto/{fileName}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getPhoto(@PathVariable("fileName") String fileName) throws IOException {

        return ResponseEntity.ok().body(userService.readPhoto( Paths.get(fileName).normalize() ));

    }


    @Operation(summary = "Create an User  ")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User userCreated = userService.createUser(user, null);

        return ResponseEntity.created(URI.create("/api/user/" + userCreated.getId())).body(userCreated);
    }
}
