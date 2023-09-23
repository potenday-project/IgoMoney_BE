package igoMoney.BE.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    private Long leaderId;
    private Long winnerId;
    private Long recordId;

    private String title;
    private String content;
    private Integer targetAmount;
    private LocalDate startDate;
    private Integer term;
    private LocalDate endDate;
}
