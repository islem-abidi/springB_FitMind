// src/main/java/tn/esprit/pidevspringboot/Service/UserServiceImplementation.java

package tn.esprit.pidevspringboot.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.User.Role;
import tn.esprit.pidevspringboot.Entities.User.Sexe;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Mapper.UserMapper;
import tn.esprit.pidevspringboot.Repository.RoleRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.dto.UserRequest;
import tn.esprit.pidevspringboot.dto.UserResponse;
import tn.esprit.pidevspringboot.dto.UserStatsResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements IUserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserMapper userMapper;
    @PersistenceContext private EntityManager entityManager;

    @Override
    public User updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setNom(userRequest.getNom());
        user.setPrenom(userRequest.getPrenom());
        user.setEmail(userRequest.getEmail());
        user.setDateNaissance(userRequest.getDateNaissance());
        user.setSexe(userRequest.getSexe());
        user.setNumeroDeTelephone(userRequest.getNumeroDeTelephone());
        user.setPhotoProfil(userRequest.getPhotoProfil());

        if (userRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        if (userRequest.getId_role() != null) {
            Role role = roleRepository.findById(userRequest.getId_role())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByArchivedFalseAndIsVerifiedTrue()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getIdUser(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getDateNaissance(),
                user.getSexe(), // ✅ Sexe Enum directement
                user.getNumeroDeTelephone(),
                user.getPhotoProfil(),
                (user.getRole() != null) ? user.getRole().getRoleType().name() : null,
                user.isArchived()
        );
    }



    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .filter(u -> !u.isArchived() && u.isVerified())
                .orElseThrow(() -> new RuntimeException("User not found or not verified"));
    }

    @Override
    public void archiveUser(Long idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.isArchived()) {
            throw new RuntimeException("Utilisateur déjà archivé");
        }

        user.setArchived(true);
        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        System.out.println("🎯 ID = " + id + " | archived = " + user.isArchived());

        user.setArchived(false);
        userRepository.save(user);
    }




    @Override
    public List<UserResponse> filterByField(String field, String value) {
        List<User> users;
        switch (field.toLowerCase()) {
            case "nom":
                users = userRepository.findByNomContainingIgnoreCase(value);
                break;
            case "prenom":
                users = userRepository.findByPrenomContainingIgnoreCase(value);
                break;
            case "sexe":
                users = userRepository.findBySexe(Sexe.valueOf(value));
                break;
            case "archived":
                users = userRepository.findByArchived(Boolean.parseBoolean(value));
                break;
            default:
                throw new IllegalArgumentException("Champ de filtre invalide: " + field);
        }

        return users.stream().map(userMapper::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> getUsersSortedByPrenom(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
    }


    @Override
    public UserStatsResponse getUserStats() {
        List<User> users = userRepository.findAllByArchivedFalseAndIsVerifiedTrue();

        long total = users.size();

        Map<String, Long> countBySexe = users.stream()
                .filter(u -> u.getSexe() != null)
                .collect(Collectors.groupingBy(
                        user -> user.getSexe().toString(),
                        Collectors.counting()
                ));

        Map<String, Long> countByRole = users.stream()
                .filter(u -> u.getRole() != null)
                .collect(Collectors.groupingBy(
                        user -> user.getRole().getRoleType().name(),
                        Collectors.counting()
                ));

        return new UserStatsResponse(total, countBySexe, countByRole);
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
