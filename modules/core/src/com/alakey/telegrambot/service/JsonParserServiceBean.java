package com.alakey.telegrambot.service;

import com.alakey.telegrambot.config.PlayerConfig;
import com.alakey.telegrambot.dto.MovieDbDTO;
import com.alakey.telegrambot.entity.*;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(JsonParserService.NAME)
public class JsonParserServiceBean implements JsonParserService {

    @Inject
    private DataManager dataManager;
    @Inject
    private TheMovieDbService theMovieDbService;
    @Inject
    private FileManagerService fileManagerService;
    @Inject
    private PlayerConfig playerConfig;

    @Override
    public MovieDbDTO parseJsonSearchMovie(String json) {
        JSONObject jsonObject = new JSONObject(json);
        String title = "";
        String overview = "";
        String backdrop_path = "";
        String vote_average = "";
        int runtime = 0;
        List<String> genre_ids = new ArrayList<>();
        MovieDbDTO movieDbDTO = new MovieDbDTO();

        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        movieDbDTO.setTitle(title);
        movieDbDTO.setOverview(overview);

        if (jsonObject.get("backdrop_path") != null) {
            backdrop_path = jsonObject.get("backdrop_path").toString();
            movieDbDTO.setBackdrop_path(backdrop_path);
        }

        if (jsonObject.get("runtime") != null) {
            runtime = jsonObject.getInt("runtime");
            movieDbDTO.setRuntime(Integer.toString(runtime));
        }

        if (jsonObject.get("genres") != null) {
            JSONArray genreIdsArray = jsonObject.getJSONArray("genres");

            for (int j = 0; j < genreIdsArray.length(); j++) {
                JSONObject firstGenre = genreIdsArray.getJSONObject(j);
                int id = firstGenre.getInt("id");
                genre_ids.add(String.valueOf(id));
            }
            movieDbDTO.setGenre_ids(genre_ids);
        }

        if (jsonObject.get("vote_average") != null) {
            vote_average = jsonObject.get("vote_average").toString();
            movieDbDTO.setVote_average(vote_average);
        }
        return movieDbDTO;
    }

    @Override
    public Integer parseJsonFetchMovieId(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray resultsArray = jsonObject.getJSONArray("results");
        for (int i = 0; i < resultsArray.length(); i++) {
            if (resultsArray.getJSONObject(i) != null) {
                JSONObject movieObject = resultsArray.getJSONObject(i);
                return movieObject.getInt("id");
            }
        }
        return 0;
    }

    @Override
    public void createNote(List<Player> playerList, Chat chat) {
        String url = playerConfig.getPlayerUrl();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date startDate = null;
        try {
            startDate = dateFormat.parse(playerConfig.getDateStart());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        for (Player player: playerList) {
            String name = player.getName();
            Integer movieId = player.getMovieId();

            Note note = dataManager.load(Note.class)
                    .query("select n from telegrambot_Note n where n.movieId = :movieId")
                    .parameter("movieId", movieId)
                    .optional()
                    .orElse(null);
            if (note == null) {
                note = dataManager.create(Note.class);
            } else {
                continue;
            }

            MovieDbDTO movieDbDTO = theMovieDbService.searchMovie(name);
            String overview = movieDbDTO.getOverview();

            if (overview == null || overview.isEmpty()) {
                continue;
            }

            note.setName(name);
            note.setDescription(overview);
            note.setUrl(url + movieId);
            note.setMovieId(movieId);
            note.setChat(chat);
            note.setRating(movieDbDTO.getVote_average());
            note.setRuntime(movieDbDTO.getRuntime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(playerConfig.getHours()));
            startDate = calendar.getTime();

            note.setDateScheduled(startDate);
            note.setStatus(StatusEnum.DELAYED.getId());

            List<Category> categoryList = dataManager.load(Category.class)
                    .query("select c from telegrambot_Category c where c.movieDbId in :movieDbIds")
                    .parameter("movieDbIds", movieDbDTO.getGenre_ids())
                    .list();
            note.setCategories(categoryList);

            if (movieDbDTO.getBackdrop_path() != null &&
                    !movieDbDTO.getBackdrop_path().isEmpty() &&
                    fileManagerService.saveImage(movieDbDTO.getBackdrop_path()) != null) {
                List<FileDescriptor> fileDescriptorList = new ArrayList<>();
                fileDescriptorList.add(fileManagerService.saveImage(movieDbDTO.getBackdrop_path()));
                note.setFiles(fileDescriptorList);
            }

            playerConfig.setDateStart(dateFormat.format(startDate));
            dataManager.commit(note);
        }
    }

    @Override
    public void parseJsonCategory(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray genresArray = jsonObject.getJSONArray("genres");

        for (int i = 0; i < genresArray.length(); i++) {
            JSONObject genreObject = genresArray.getJSONObject(i);
            String movieId = Integer.toString(genreObject.getInt("id"));
            String name = genreObject.getString("name");
            Category category = dataManager.load(Category.class)
                    .query("select c from telegrambot_Category c where c.movieDbId = :movieDbId")
                    .parameter("movieDbId", movieId)
                    .optional()
                    .orElse(null);
            if (category == null) {
                category = dataManager.create(Category.class);
            }
            category.setName(name);
            category.setMovieDbId(movieId);
            dataManager.commit(category);
        }
    }
}