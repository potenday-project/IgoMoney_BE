package igoMoney.BE.domain;

import igoMoney.BE.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    private Integer cost;
    private String image;
    @Column(nullable = false)
    private LocalDate date;

    public void updateRecord(String title, String content, Integer cost, String image){
        this.title = title;
        this.content = content;
        this.cost = cost;
        this.image = image;
    }
}
