package igoMoney.BE.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordReportRequest {

    private Long recordId;
    private Long reporter_userId; // 신고자
    private Long offender_userId; // 신고 당한 사람
    private Integer reason;
}
