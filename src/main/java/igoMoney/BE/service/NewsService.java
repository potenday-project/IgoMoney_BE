package igoMoney.BE.service;

import igoMoney.BE.domain.News;
import igoMoney.BE.dto.response.NewsResponse;
import igoMoney.BE.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NewsService {

    private final NewsRepository newsRepository;

    public List<NewsResponse> getNews(Long lastId) {

        List<NewsResponse> responseList = new ArrayList<>();
        List<News> latestNews;
        latestNews = newsRepository.findAllByIdGreaterThan(lastId);
        for (News news: latestNews){
            responseList.add(newsToNewsResponse(news));
        }
        return responseList;
    }

    private NewsResponse newsToNewsResponse(News news) {
        return  NewsResponse.builder()
                .newsId(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .date(LocalDate.from(news.getCreatedDate()))
                .build();
    }
}
