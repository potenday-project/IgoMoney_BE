package igoMoney.BE.controller;

import igoMoney.BE.dto.request.ChallengeCreateRequest;
import igoMoney.BE.dto.response.ChallengeResponse;
import igoMoney.BE.dto.response.ChallengeTotalCostResponse;
import igoMoney.BE.dto.response.IdResponse;
import igoMoney.BE.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 참여중인 챌린지 기본정보 조회
    @GetMapping("my-active-challenge/{userId}")
    public ResponseEntity<ChallengeResponse> getMyActiveChallenge(@PathVariable Long userId) {

        ChallengeResponse response = challengeService.getMyActiveChallenge(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // 챌린지 등록하기
    @PostMapping("new")
    public ResponseEntity<IdResponse> createChallenge(@Valid ChallengeCreateRequest request) {

        Long challengeId = challengeService.createChallenge(request);

        IdResponse response = IdResponse.builder()
                .id(challengeId)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 챌린지 참여 신청하기
    @PostMapping("apply/{challengeId}/{userId}")
    public ResponseEntity<Void> applyChallenge(@PathVariable Long userId, @PathVariable Long challengeId) {

        challengeService.applyChallenge(userId, challengeId);
        return new ResponseEntity(HttpStatus.OK);
    }

    // 챌린지 포기하기
    @PostMapping("giveup/{userId}")
    public ResponseEntity<Void> giveupChallenge(@PathVariable Long userId) {

        challengeService.giveupChallenge(userId);
        return new ResponseEntity(HttpStatus.OK);
    }

    // 챌린지의 각 사용자별 누적금액 조회
    @GetMapping("total-cost/{challengeId}")
    public ResponseEntity<List<ChallengeTotalCostResponse>> getTotalCostPerChallengeUser(@PathVariable Long challengeId) {

        List<ChallengeTotalCostResponse> response = challengeService.getTotalCostPerChallengeUser(challengeId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
