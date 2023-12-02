package igoMoney.BE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponse {
    private Long newsId;
    private String title;
    private String content;
    private LocalDate date;
}
