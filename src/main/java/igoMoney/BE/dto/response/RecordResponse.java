package igoMoney.BE.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class RecordResponse {

    private Long recordId;
    private Long challengeId;
    private Long userId;
    private String title;
    private String content;
    private List<String> image;
    private Integer cost;
    private LocalDate date;
    private Boolean hide;
}
