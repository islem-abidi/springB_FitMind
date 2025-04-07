package tn.esprit.pidevspringboot.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.dto.UserFilterDTO;
import tn.esprit.pidevspringboot.dto.UserRequest;
import tn.esprit.pidevspringboot.dto.UserResponse;
import tn.esprit.pidevspringboot.dto.UserStatsResponse;

import java.util.List;

public interface IUserService {
    User updateUser(Long id, UserRequest updatedUser);
    void deleteUser(Long id);
     List<UserResponse> getAllUsers();
   User getUserById(Long id);
     void archiveUser(Long id);
    void restoreUser(Long id);
    List<UserResponse> filterByField(String field, String value);
    Page<UserResponse> getUsersSortedByPrenom(Pageable pageable);
    UserStatsResponse getUserStats();
    boolean isEmailTaken(String email);

}
