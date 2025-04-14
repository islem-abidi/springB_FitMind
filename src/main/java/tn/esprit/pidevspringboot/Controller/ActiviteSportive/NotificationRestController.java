package tn.esprit.pidevspringboot.Controller.ActiviteSportive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Notification;
import tn.esprit.pidevspringboot.Service.ActiviteSportive.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@AllArgsConstructor
@Tag(name = "Gestion des Notifications")
public class NotificationRestController {


}
