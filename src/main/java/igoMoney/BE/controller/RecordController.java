package igoMoney.BE.controller;

import igoMoney.BE.dto.request.RecordReportRequest;
import igoMoney.BE.dto.request.RecordSaveRequest;
import igoMoney.BE.dto.request.RecordUpdateRequest;
import igoMoney.BE.dto.response.IdResponse;
import igoMoney.BE.dto.response.RecordResponse;
import igoMoney.BE.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("records")
public class RecordController {

    private final RecordService recordService;

    // record 등록하기
    @PostMapping("new")
    public ResponseEntity<IdResponse> saveRecord (@Valid RecordSaveRequest request) throws IOException {

        Long recordId = recordService.saveRecord(request);
        IdResponse response = IdResponse.builder()
                .id(recordId)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // record 1건 조회
    @GetMapping("{recordId}")
    public ResponseEntity<RecordResponse> getRecord(@PathVariable("recordId") Long recordId) {

        RecordResponse response = recordService.getRecord(recordId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 사용자의 그날의 모든 record 조회
    @GetMapping("daily-records/{userId}/{date}")
    public ResponseEntity<List<RecordResponse>> getUserDailyRecordList(@PathVariable("userId") Long userId, @PathVariable("date") LocalDate date) {

        List<RecordResponse> response = recordService.getUserDailyRecordList(userId, date);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // record 삭제
    @DeleteMapping("delete/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable("recordId") Long recordId ) {

        recordService.deleteRecord(recordId);
        return new ResponseEntity(HttpStatus.OK);
    }

    // record 수정
    @PatchMapping("edit")
    public ResponseEntity<Void> updateRecord(@Valid RecordUpdateRequest request) throws IOException {

        recordService.updateRecord(request);
        return new ResponseEntity(HttpStatus.OK);
    }

    // 불량 record 신고하기
    @PostMapping("report")
    public ResponseEntity<IdResponse> reportRecord (@Valid RecordReportRequest request) throws IOException {

        Long userReportId = recordService.reportRecord(request);
        IdResponse response = IdResponse.builder()
                .id(userReportId)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
