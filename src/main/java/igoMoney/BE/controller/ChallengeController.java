package igoMoney.BE.controller;

import igoMoney.BE.dto.response.ChallengeResponse;
import igoMoney.BE.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    // 시작 안 한 챌린지 목록 조회
    @GetMapping("notstarted")
    public ResponseEntity<List<ChallengeResponse>> getNotStartedChallengeList() {

        List<ChallengeResponse> response = challengeService.getNotStartedChallengeList();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
