package igoMoney.BE.controller;

import igoMoney.BE.dto.response.NewsResponse;
import igoMoney.BE.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping("")
    public ResponseEntity<List<NewsResponse>> getNews(
            @RequestParam(value="lastId", required = false, defaultValue = "-1") Long lastId ) {

        List<NewsResponse> response = newsService.getNews(lastId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
