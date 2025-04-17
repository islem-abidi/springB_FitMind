package tn.esprit.pidevspringboot.dto;


import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponse {
    private long totalUsers;
    private Map<String, Long> countBySexe;
    private Map<String, Long> countByRole;
    private Map<LocalDate, List<String>> loginsPerDayNames;
    private Map<LocalDate, Long> loginsPerDay;

    private long activeUsers;                  // ✅ new
    private long inactiveUsers;                // ✅ new
    private double avgLastSeenDays;            // ✅ new

}
