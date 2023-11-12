package com.alakey.telegrambot.service;

import com.alakey.telegrambot.entity.Player;

import java.util.Optional;

public interface PlayerService {
    String NAME = "telegrambot_PlayerService";

    Optional<Player> getPlayerByMovieId(String movieId);

    Optional<Player> getPlayerByMovieName(String movieName);
}