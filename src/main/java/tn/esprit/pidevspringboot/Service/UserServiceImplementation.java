
package tn.esprit.pidevspringboot.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.User.LoginEvent;
import tn.esprit.pidevspringboot.Entities.User.Role;
import tn.esprit.pidevspringboot.Entities.User.Sexe;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Mapper.UserMapper;
import tn.esprit.pidevspringboot.Repository.LoginEventRepository;
import tn.esprit.pidevspringboot.Repository.RoleRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.dto.UserRequest;
import tn.esprit.pidevspringboot.dto.UserResponse;
import tn.esprit.pidevspringboot.dto.UserStatsResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements IUserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserMapper userMapper;
    @PersistenceContext private EntityManager entityManager;
    @Autowired private LoginEventRepository loginEventRepository;

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
            user.setMotDePasse(passwordEncoder.encode(userRequest.getPassword()));
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
                .filter(u -> !u.isArchived() && u.isEnabled())
                .orElseThrow(() -> new RuntimeException("User not found or not verified"));
    }


    @Override
    public void banUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.isBanned()) {
            return; // ✅ Ne rien faire si déjà banni
        }

        user.setBanned(true);
        userRepository.save(user);
    }


    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!user.isBanned()) {
            throw new RuntimeException("Utilisateur n'est pas banni");
        }

        user.setBanned(false);
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
        List<LoginEvent> events = loginEventRepository.findAll();

        long total = users.size();

        Map<String, Long> countBySexe = users.stream()
                .filter(u -> u.getSexe() != null)
                .collect(Collectors.groupingBy(
                        u -> u.getSexe().toString(),
                        Collectors.counting()
                ));

        Map<String, Long> countByRole = users.stream()
                .filter(u -> u.getRole() != null)
                .collect(Collectors.groupingBy(
                        u -> u.getRole().getRoleType().name(),
                        Collectors.counting()
                ));

        // ✅ Liste des noms par jour
        Map<LocalDate, List<String>> loginsPerDayNames = events.stream()
                .filter(e -> e.getLoginDate() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getLoginDate().toLocalDate(),
                        Collectors.mapping(
                                e -> e.getUser().getPrenom() + " " + e.getUser().getNom(),
                                Collectors.toList()
                        )
                ));

        // ✅ Compte total par jour
        Map<LocalDate, Long> loginsPerDay = events.stream()
                .filter(e -> e.getLoginDate() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getLoginDate().toLocalDate(),
                        Collectors.counting()
                ));

        // ✅ Dernière connexion par utilisateur
        Map<Long, Date> lastLoginMap = events.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getUser().getIdUser(),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(LoginEvent::getLoginDate)),
                                opt -> opt.map(event ->
                                        Date.from(event.getLoginDate().atZone(ZoneId.systemDefault()).toInstant())
                                ).orElse(null)
                        )
                ));

        LocalDate today = LocalDate.now();

        long activeUsers = lastLoginMap.values().stream()
                .filter(date -> date != null &&
                        ChronoUnit.DAYS.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), today) <= 7)
                .count();

        long inactiveUsers = total - activeUsers;

        double avgLastSeenDays = lastLoginMap.values().stream()
                .filter(Objects::nonNull)
                .mapToLong(date -> ChronoUnit.DAYS.between(
                        date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), today))
                .average().orElse(0);

        return new UserStatsResponse(
                total,
                countBySexe,
                countByRole,
                loginsPerDayNames,
                loginsPerDay,
                activeUsers,
                inactiveUsers,
                avgLastSeenDays
        );
    }




    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
