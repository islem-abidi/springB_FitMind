package tn.esprit.pidevspringboot.Controller.ActiviteSportive;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Service.ActiviteSportive.BadgeService;

@RestController
@RequestMapping("/utilisateurs")
@AllArgsConstructor

public class BadgeController {

    @Autowired
    BadgeService badgeService;

    @GetMapping("/{id}/badges")
    public String getBadge(@PathVariable("id") Long userId) {
        return badgeService.getBadgeForUser(userId);
    }
}

