package com.renatobonfim.aemblogbackend.userx;

import com.renatobonfim.aemblogbackend.customExceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Page<User> findAllUsers(int page, int size, String sort, String[] properties);

    User findUserByID(String UserID) throws UserNotFoundException;

    void deleteUser(String userId);

    String uploadPhoto(String userId, MultipartFile multipartFile) throws UserNotFoundException;

    User updateUser(User user, String UserId) throws UserNotFoundException;

    User createUser(User user, MultipartFile profilePhoto );
}
