package igoMoney.BE.controller;

import igoMoney.BE.dto.request.RecordSaveRequest;
import igoMoney.BE.dto.response.IdResponse;
import igoMoney.BE.service.RecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    // 사용자의 그날의 모든 record 조회


}
