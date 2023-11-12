package com.alakey.telegrambot.service;

import com.alakey.telegrambot.dto.MovieDbDTO;

public interface TheMovieDbService {
    String NAME = "telegrambot_TheMovieDbService";

    MovieDbDTO searchMovie(String search);

    MovieDbDTO fetchMovie(Integer id);

    void fetchGenres();
}