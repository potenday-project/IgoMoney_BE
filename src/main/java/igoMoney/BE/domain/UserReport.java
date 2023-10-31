package igoMoney.BE.domain;

import igoMoney.BE.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_report_id")
    private Long id;

    @Column(nullable = false)
    private Long reporterId;

    @Column(nullable = false)
    private Long offenderId;

    @Column(nullable = false)
    private Long recordId;

    @Column(nullable = false)
    private Integer reason; // 0spam/1adult/2ad/3self-harm&suicide/4copyright&defame/5private-info/6hate
}
