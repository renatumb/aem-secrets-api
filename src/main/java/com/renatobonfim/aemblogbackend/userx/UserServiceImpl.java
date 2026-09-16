package com.renatobonfim.aemblogbackend.userx;

import com.renatobonfim.aemblogbackend.config.Constants;
import com.renatobonfim.aemblogbackend.customExceptions.ErrorReadingPhotoException;
import com.renatobonfim.aemblogbackend.customExceptions.InvalidFieldException;
import com.renatobonfim.aemblogbackend.customExceptions.UserNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user, MultipartFile profilePhoto) {

        String userName = user.getName();
        String userEmail = user.getEmail();
        String userPassword = user.getPassword();
        String userAbout = user.getAbout();

        try {
            Objects.requireNonNull(userName, Constants.USER_NAME_IS_EMPTY);

            if (userName.isEmpty() || userName.isBlank()) {
                throw new InvalidFieldException(Constants.USER_NAME_IS_EMPTY);
            }

            Objects.requireNonNull(userEmail, Constants.USER_EMAIL_IS_EMPTY);

            if (userEmail.isEmpty() || userEmail.isBlank()) {
                throw new InvalidFieldException(Constants.USER_EMAIL_IS_EMPTY);
            }

            Objects.requireNonNull(userPassword, Constants.USER_PASSWORD_IS_EMPTY);

            if (userPassword.isEmpty() || userPassword.isBlank()) {
                throw new InvalidFieldException(Constants.USER_PASSWORD_IS_EMPTY);
            }

            Objects.requireNonNull(userAbout, Constants.USER_ABOUT_IS_EMPTY);

            if (userAbout.isEmpty() || userAbout.isBlank()) {
                throw new InvalidFieldException(Constants.USER_ABOUT_IS_EMPTY);
            }

        } catch (NullPointerException | InvalidFieldException ex) {
            throw new InvalidFieldException(ex.getMessage());
        }

        User userCreated = userRepository.save(
                User.builder()
                .name(user.getName())
                .email(user.getEmail().toLowerCase(Locale.ROOT))
                .password( passwordEncoder.encode( user.getPassword() ))
                .about(user.getAbout())
                .accessLevel( AccessLevel.CAN_READ )
                .accountLocked(false)
                .build()
        );

        return userCreated;

        //userCreated.setPhotoProfile(uploadPhoto(userCreated.getId(), profilePhoto));
    }


    @Override
    public byte[] readPhoto(Path pathProfilePhoto) {
        byte[] photo = new byte[0];

        try {
            photo = Files.readAllBytes(Paths.get( Constants.USER_PROFILE_PICTURE_PATH, pathProfilePhoto.toString()).normalize() );
        } catch (IOException ioe) {
            throw new ErrorReadingPhotoException(Constants.ERROR_READING_PROFILE_PHOTO + ioe);
        }
        return photo;
    }

    @Override
    public void deleteUser(String userId) throws UserNotFoundException {
//        try {
//            String fileUrl = userRepository.findPhoto(userId).orElse(null);
//
//            if (!Objects.isNull(fileUrl)) {
//
//                String fileName = Arrays.stream(fileUrl.split("/"))
//                        .reduce((first, second) -> second)
//                        .orElse(null);
//
//                Files.deleteIfExists(Paths.get(Constants.PROFILE_PICTURE_STORAGE + fileName).normalize());
//            }
//        } catch (IOException ioe) {
//            throw new ErrorReadingPhotoException(Constants.ERROR_READING_PROFILE_PHOTO + ioe);
//        }
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User user, String userId, MultipartFile photoProfile) throws UserNotFoundException {
        User oldUser = findUserByID(userId);

        if( Objects.nonNull( user.getName() ) ) {
            oldUser.setAbout( user.getName() );
        }

        if( Objects.nonNull( user.getPassword() ) ) {
            oldUser.setPassword( passwordEncoder.encode( user.getPassword() ) );
        }

        if( Objects.nonNull( user.getAbout() ) ) {
            oldUser.setAbout( user.getAbout() );
        }

        if( Objects.nonNull( user.getAccessLevel() ) ){
            oldUser.setAccessLevel(user.getAccessLevel());
        }

        if( Objects.nonNull( user.getAccountLocked() ) ) {
            oldUser.setAccountLocked(user.getAccountLocked());
        }

        if( Objects.nonNull(photoProfile)){
            oldUser.setPhotoProfile( saveProfilePhoto(userId, photoProfile ) );
        }

        return userRepository.save(oldUser);
    }

    @Override
    public User findUserByID(String userID) throws UserNotFoundException {
        return userRepository.findById(userID).orElseThrow(() -> new UserNotFoundException(userID));
    }

    @Override
    public Page<User> findAllUsers(int page, int size, String sort, String[] properties) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.Direction.fromString(sort), properties));
    }

    private String saveProfilePhoto(String userID, MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String fileName = userID +  originalFileName.substring(originalFileName.lastIndexOf('.') );

            Path storageDir = Paths.get(Constants.USER_PROFILE_PICTURE_PATH).normalize();

            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }

            Path target = storageDir.resolve(fileName);
            Files.copy(file.getInputStream(), target, REPLACE_EXISTING);

            return fileName;

        } catch (IOException ex) {
            throw new RuntimeException("unable to save image", ex);
        }
    }

}
