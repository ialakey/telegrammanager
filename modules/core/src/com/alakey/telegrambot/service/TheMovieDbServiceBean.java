package com.alakey.telegrambot.service;

import com.alakey.telegrambot.config.TheMovieDbConfig;
import com.alakey.telegrambot.dto.MovieDbDTO;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

@Service(TheMovieDbService.NAME)
public class TheMovieDbServiceBean implements TheMovieDbService {

    @Inject
    private TheMovieDbConfig theMovieDbConfig;

    @Inject
    private JsonParserService jsonParserService;

    private final String language = "language=ru-RU";
    private final String page = "&page=1";
    private final String query = "search/movie?query=";
    private final String movie = "movie/";
    private final String genre = "/genre/movie/list?language=ru";
    private final String authorization = "Authorization";

    private final OkHttpClient client;

    public TheMovieDbServiceBean() {
        client = new OkHttpClient().newBuilder().build();
    }

    @Override
    public MovieDbDTO searchMovie(String search) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Request request = new Request.Builder()
                .url(theMovieDbConfig.getUrl() + query + search + "&" + language + page)
                .get()
                .addHeader(authorization, "Bearer " + theMovieDbConfig.getAccessKeyApi())
                .addHeader("accept", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return fetchMovie(jsonParserService.parseJsonFetchMovieId(response.body().string()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MovieDbDTO fetchMovie(Integer id) {
        if (id == 0) {
            return new MovieDbDTO();
        }
        Request request = new Request.Builder()
                .url(theMovieDbConfig.getUrl() + movie + id + "?" + language)
                .get()
                .addHeader(authorization, "Bearer " + theMovieDbConfig.getAccessKeyApi())
                .addHeader("accept", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return jsonParserService.parseJsonSearchMovie(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void fetchGenres() {
        Request request = new Request.Builder()
                .url(theMovieDbConfig.getUrl() + genre)
                .get()
                .addHeader(authorization, "Bearer " + theMovieDbConfig.getAccessKeyApi())
                .addHeader("accept", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            jsonParserService.parseJsonCategory(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}