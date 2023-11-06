package igoMoney.BE.service;

import igoMoney.BE.domain.Challenge;
import igoMoney.BE.repository.ChallengeRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ChallengeServiceTest {

    @Autowired ChallengeService challengeService;
    @Autowired ChallengeRepository challengeRepository;
    @Autowired EntityManager em;

    @Test
    public void 시작안한챌린지_조회() throws Exception {
        //given
        for (int i = 1; i <= 30; i++) {
            challengeRepository.save(Challenge.builder()
                    .title(String.valueOf(i))
                    .content("__")
                    .targetAmount(10000)
                    .startDate(LocalDate.now().plusDays(2))
                    .build());
        }

        //when
        List<Challenge> challenges = challengeRepository.findAllNotStarted(10, null, LocalDate.now()); // pageNo는 0부터 시작이라 1이면 두번째 페이지 조회

        //then
        assertEquals(challenges.size(),10);
        assertEquals(30,(long) challenges.get(0).getId());
        assertEquals(21, (long) challenges.get(9).getId());

        // [NoOffset 2번째 페이지]
//        //when
//        List<Challenge> challenges = challengeRepository.findAllNotStarted(10, 21L); // DESC. 20~11
//
//        //then
//        assertThat(challenges).hasSize(10);
//        assertThat(challenges.get(0).getId()).isEqualTo(20);
//        assertThat(challenges.get(9).getId()).isEqualTo(11);
    }
}
