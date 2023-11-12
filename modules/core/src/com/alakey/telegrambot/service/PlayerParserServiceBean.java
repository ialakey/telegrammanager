package com.alakey.telegrambot.service;

import com.alakey.telegrambot.entity.Player;
import com.haulmont.cuba.core.global.DataManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

@Service(PlayerParserService.NAME)
public class PlayerParserServiceBean implements PlayerParserService {

    @Inject
    private DataManager dataManager;

    @Override
    public void getMovieUrl(int start, int finish) {
        try {
            for (int i = start; i < finish; i++) {
                String url = "https://api.framprox.ws/embed/movie/" + i;
                Document document = Jsoup.connect(url).ignoreHttpErrors(true).get();
                String title = document.title();
                if (title != null && !title.isEmpty() && !title.equals("Title")) {
                    Player player = dataManager.load(Player.class)
                            .query("select p from telegrambot_Player p where p.movieId = :movieId")
                            .parameter("movieId", i)
                            .optional()
                            .orElse(null);
                    if (player == null) {
                        player = dataManager.create(Player.class);
                    }
                    player.setMovieId(i);
                    player.setName(title.split("\\(", 2)[0]);
                    dataManager.commit(player);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}