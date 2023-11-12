package com.alakey.telegrambot.service;

import com.alakey.telegrambot.entity.Player;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Optional;

//TODO переименовать
@Repository(PlayerService.NAME)
public class PlayerServiceBean implements PlayerService {

    @Inject
    private DataManager dataManager;

    @Override
    public Optional<Player> getPlayerByMovieId(String movieId) {
        return dataManager.load(Player.class)
                .query("select p from telegrambot_Player p " +
                        "where p.movieId = :movieId")
                .parameter("movieId", Integer.parseInt(movieId))
                .optional();
    }

    @Override
    public Optional<Player> getPlayerByMovieName(String nameMovie) {
        return dataManager.load(Player.class)
                .query("select p from telegrambot_Player p " +
                        "where lower(p.name) like lower(:nameMovie)")
                .parameter("nameMovie", nameMovie)
                .optional();
    }
}