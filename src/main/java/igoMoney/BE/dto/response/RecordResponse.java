package igoMoney.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RecordResponse {

    private Long recordId;
    private Long challengeId;
    private Long userId;
    private String title;
    private String content;
    private String image;
    private LocalDate date;
}
