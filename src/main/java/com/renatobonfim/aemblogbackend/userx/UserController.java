package com.renatobonfim.aemblogbackend.userx;

import com.renatobonfim.aemblogbackend.config.Constants;
import com.renatobonfim.aemblogbackend.customExceptions.UserNotFoundException;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    public UserService userService;

    @GetMapping
    public ResponseEntity<Page<User>> findAllUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                                   @RequestParam(value = "sort", defaultValue = "asc") String sort,
                                                   @RequestParam(value = "fields", defaultValue = "id") String properties) {
        return ResponseEntity.ok().body(userService.findAllUsers(page, size, sort, properties.split(",")));
    }

    @GetMapping("/{userID}")
    public ResponseEntity<User> findUserByID(@PathVariable("userID") String userId) throws UserNotFoundException {
        return ResponseEntity.ok().body(userService.findUserByID(userId));
    }

    @DeleteMapping("/{userID}")
    public ResponseEntity deleteUser(@PathVariable("userID") String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/profilephoto")
    public ResponseEntity<String> uploadPhoto(@PathVariable("userId") String userID, @RequestParam("photoProfile") MultipartFile  photoProfile) throws UserNotFoundException {

        return ResponseEntity.status(HttpStatus.CREATED).body( userService.uploadPhoto(userID, photoProfile ) );
    }

    @GetMapping(value = "/profilephoto/{fileName}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getPhoto(@PathVariable("fileName") String fileName) throws IOException {
        return ResponseEntity.ok().body( Files.readAllBytes(   Paths.get(Constants.PROFILE_PICTURE_STORAGE + fileName ).normalize() ) );
    }

    @PutMapping ("/{userID}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "userID") String userId,
                                           @RequestBody User user) throws UserNotFoundException {
        User updatedUser = userService.updateUser( user, userId);

        return ResponseEntity.ok(updatedUser );
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User userCreated = userService.createUser(user, null);

        return ResponseEntity.created(URI.create("/api/user/" + userCreated.getId())).body(userCreated);
    }
}
