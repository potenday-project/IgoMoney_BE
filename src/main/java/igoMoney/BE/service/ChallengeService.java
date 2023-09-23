package igoMoney.BE.service;

import igoMoney.BE.domain.Challenge;
import igoMoney.BE.dto.response.ChallengeResponse;
import igoMoney.BE.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    // 시작 안 한 챌린지 목록 조회
    public List<ChallengeResponse> getNotStartedChallengeList() {

        List<ChallengeResponse> responseList = new ArrayList<>();
        List<Challenge> challengeList = challengeRepository.findAllByStartDateIsNull();
        for (Challenge challenge: challengeList){
            ChallengeResponse challengeResponse = ChallengeResponse.builder()
                    .id(challenge.getId())
                    .leaderId(challenge.getLeaderId())
                    .title(challenge.getTitle())
                    .content(challenge.getContent())
                    .targetAmount(challenge.getTargetAmount())
                    .build();
            responseList.add(challengeResponse);
        }

        return responseList;
    }
}
