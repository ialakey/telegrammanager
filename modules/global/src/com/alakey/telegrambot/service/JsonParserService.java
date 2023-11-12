package com.alakey.telegrambot.service;

import com.alakey.telegrambot.dto.MovieDbDTO;
import com.alakey.telegrambot.entity.Chat;
import com.alakey.telegrambot.entity.Player;

import java.util.List;

public interface JsonParserService {
    String NAME = "telegrambot_JsonParserService";

    MovieDbDTO parseJsonSearchMovie(String json);
    Integer parseJsonFetchMovieId(String json);

    void createNote(List<Player> playerList, Chat chat);

    void parseJsonCategory(String json);
}