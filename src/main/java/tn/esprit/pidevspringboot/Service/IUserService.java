package tn.esprit.pidevspringboot.Service;

import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.dto.UserRequest;
import tn.esprit.pidevspringboot.dto.UserResponse;

import java.util.List;

public interface IUserService {
    User updateUser(Long id, UserRequest updatedUser);
    void deleteUser(Long id);
     List<UserResponse> getAllUsers();
   User getUserById(Long id);
     void archiveUser(Long id);
    void restoreUser(Long id);

}
