package tn.esprit.pidevspringboot.dto;


import lombok.*;

import java.util.Map;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponse {
    private long totalUsers;
    private Map<String, Long> countBySexe;
    private Map<String, Long> countByRole;
}
