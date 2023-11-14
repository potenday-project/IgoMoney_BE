package igoMoney.BE.domain;

import igoMoney.BE.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "record_id")
    @Builder.Default
    private List<Image> image = new ArrayList<Image>();

    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    @Builder.Default
    private Boolean hide = false; // 신고당했을 때 가림

    public void updateRecord(String title, String content, Integer cost, List<Image> image){
        this.title = title;
        this.content = content;
        this.cost = cost;
        setImage(image);
    }

    public void setHidden() { this.hide = true; }

    public void setImage(List<Image> aList){
        this.image.clear();
        if (aList != null){
            this.image.addAll(aList);
        }
    }
}
