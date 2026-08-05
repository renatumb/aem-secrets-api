package com.renatobonfim.aemblogbackend.userx;

import com.renatobonfim.aemblogbackend.config.Constants;
import com.renatobonfim.aemblogbackend.customExceptions.ErrorReadingPhotoException;
import com.renatobonfim.aemblogbackend.customExceptions.InvalidFieldException;
import com.renatobonfim.aemblogbackend.customExceptions.UserNotFoundException;
import com.renatobonfim.aemblogbackend.utils.CustomUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
                .email(user.getEmail())
                .password( passwordEncoder.encode( user.getPassword() ))
                .about(user.getAbout())
                .build()
        );

        return userCreated;

        //userCreated.setPhotoProfile(uploadPhoto(userCreated.getId(), profilePhoto));
    }

    @Override
    public String uploadPhoto(String userId, MultipartFile multipartFile) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId) );

        String pathImage = CustomUtils.saveImage(userId, multipartFile, Constants.PROFILE_PICTURE_STORAGE );
        user.setPhotoProfile(pathImage);
        userRepository.save(user );
        return pathImage;
    }

    @Override
    public byte[] readPhoto(Path pathProfilePhoto) {
        byte[] photo = new byte[0];

        try {
            photo = Files.readAllBytes(pathProfilePhoto.normalize());
        } catch (IOException ioe) {
            throw new ErrorReadingPhotoException(Constants.ERROR_READING_PROFILE_PHOTO + ioe);
        }
        return photo;
    }

    @Override
    public void deleteUser(String userId) throws UserNotFoundException {
        try {
            String fileUrl = userRepository.findPhoto(userId).orElse(null);

            if (!Objects.isNull(fileUrl)) {

                String fileName = Arrays.stream(fileUrl.split("/"))
                        .reduce((first, second) -> second)
                        .orElse(null);

                Files.deleteIfExists(Paths.get(Constants.PROFILE_PICTURE_STORAGE + fileName).normalize());
            }
        } catch (IOException ioe) {
            throw new ErrorReadingPhotoException(Constants.ERROR_READING_PROFILE_PHOTO + ioe);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(User user, String userId) throws UserNotFoundException {
        User oldUser = findUserByID(userId);

        String userName = user.getName();
        String userEmail = user.getPassword();
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

        oldUser.setAbout(user.getAbout());
        oldUser.setEmail(user.getEmail());
        oldUser.setName(user.getName());
        oldUser.setPassword( passwordEncoder.encode( user.getPassword() ) );

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
}
