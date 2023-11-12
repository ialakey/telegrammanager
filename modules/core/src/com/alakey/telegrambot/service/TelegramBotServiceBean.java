package com.alakey.telegrambot.service;

import com.alakey.telegrambot.config.TelegramConfig;
import com.alakey.telegrambot.entity.Note;
import com.alakey.telegrambot.entity.StatusEnum;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.sys.AppContext;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service(TelegramBotService.NAME)
public class TelegramBotServiceBean implements TelegramBotService, AppContext.Listener {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(TelegramBotServiceBean.class);
    @Inject
    private DataManager dataManager;
    @Inject
    private FileLoader fileLoader;
    @Inject
    private TelegramConfig telegramConfig;

    private TelegramHelperBot bot;

    @PostConstruct
    void init() {
        AppContext.addListener(this);
    }

    @Override
    public void applicationStarted() {
        start();
    }

    @Override
    public void applicationStopped() {

    }

    public void start() {
        bot = new TelegramHelperBot(telegramConfig.getToken());
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(UUID id) {
        Note note = dataManager.load(Note.class)
                .id(id)
                .view("full-note")
                .one();

        bot = new TelegramHelperBot(note.getChat().getBotToken());

        if (note.getMovieId() != null) {
            String messageForMovie = createMessageForMovie(note);

            if (note.getFiles() != null && !note.getFiles().isEmpty()) {
                createSendPhotoIsMovie(note, messageForMovie);
            } else {
                createSendMessageIsMovie(note, messageForMovie);
            }
        } else {
            String message = createMessage(note);

            if (note.getFiles() != null && !note.getFiles().isEmpty()) {
                createSendPhoto(note, message);
            } else {
                createSendMessage(note, message);
            }
        }

        note.setStatus(StatusEnum.PUBLISHED.getId());
        note.setDatePublication(new Date());

        dataManager.commit(note);
    }

    private String createMessageForMovie(Note note) {
        StringBuilder message = new StringBuilder();
        //name
        if (note.getName() != null && !note.getName().isEmpty()) {
            message.append("\uD83C\uDFA5<b> " + note.getName() + "</b>\n\n");
        }
        //description
        if (note.getDescription() != null && !note.getDescription().isEmpty()) {
            message.append("✏ " + note.getDescription() + "\n\n");
        }
        //genre
        if (note.getCategories() != null && !note.getCategories().isEmpty()) {
            message.append("\uD83C\uDFAC ");
            note.getCategories().forEach(c -> message.append("#" + c.getName() + " "));
            message.append("\n\n");
        }
        //rating
        if (note.getRating() != null && !note.getRating().isEmpty()) {
            message.append("⭐ <b>" + note.getRating() + "</b>\n\n");
        }
        //runtime
        if (note.getRuntime() != null && !note.getRuntime().isEmpty()) {
            message.append("⏰ <b>" + note.getRuntime() + " мин.</b>" + "\n\n");
        }
        if (note.getMovieId() != null) {
            message.append("\uD83D\uDD0E <b>" + note.getMovieId() + " ID</b>\n\n");
        }
        return message.toString();
    }
    private String createMessage(Note note) {
        String description = note.getDescription().replaceAll("<div>", "\n");
        description = description.replaceAll("<br>", "");
        description = description.replaceAll("</div>", "");
        return description;
    }

    private void createSendPhotoIsMovie(Note note, String message) {
        InputStream inputStream = null;
        try {
            inputStream = fileLoader.openStream(note.getFiles().get(0));
        } catch (FileStorageException e) {
            throw new RuntimeException(e);
        }
        InputFile inputFile = new InputFile(inputStream, note.getFiles().get(0).getName());

        SendPhoto sendPhoto = new SendPhoto(note.getChat().getChatId(), inputFile);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton watchBtn = new InlineKeyboardButton();
        watchBtn.setText("Смотреть");
        watchBtn.setCallbackData("checkAndWatch");
        row.add(watchBtn);

        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        sendPhoto.setCaption(message);
        sendPhoto.setParseMode(ParseMode.HTML);
        try {
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSendMessageIsMovie(Note note, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(note.getChat().getChatId());
        sendMessage.setText(message);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton watchBtn = new InlineKeyboardButton();
        watchBtn.setText("Смотреть");
        watchBtn.setCallbackData("checkAndWatch");
        row.add(watchBtn);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setParseMode(ParseMode.HTML);

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSendPhoto(Note note, String message) {
        InputStream inputStream = null;
        try {
            inputStream = fileLoader.openStream(note.getFiles().get(0));
        } catch (FileStorageException e) {
            throw new RuntimeException(e);
        }
        InputFile inputFile = new InputFile(inputStream, note.getFiles().get(0).getName());

        SendPhoto sendPhoto = new SendPhoto(note.getChat().getChatId(), inputFile);

        if (note.getUrl() != null && !note.getUrl().isEmpty()) {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton watchBtn = new InlineKeyboardButton();
            watchBtn.setText("Смотреть");
            watchBtn.setUrl(note.getUrl());
            row.add(watchBtn);
            rows.add(row);
            inlineKeyboardMarkup.setKeyboard(rows);
            sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        }

        sendPhoto.setCaption(message);
        sendPhoto.setParseMode(ParseMode.HTML);
        try {
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSendMessage(Note note, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(note.getChat().getChatId());
        sendMessage.setText(message);

        if (note.getUrl() != null && !note.getUrl().isEmpty()) {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton watchBtn = new InlineKeyboardButton();
            watchBtn.setText("Смотреть");
            watchBtn.setUrl(note.getUrl());
            row.add(watchBtn);
            rows.add(row);
            inlineKeyboardMarkup.setKeyboard(rows);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        sendMessage.setParseMode(ParseMode.HTML);

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMovie(Long chatId, String movieName, String movieId) {
        String responseText = "Вот твой фильм \"" + movieName + "\"\nПриятного просмотра\uD83D\uDE0E";
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(chatId);
        responseMessage.setText(responseText);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        InlineKeyboardButton watchBtn1 = new InlineKeyboardButton();
        watchBtn1.setText("Смотреть зеркало 1");
        watchBtn1.setUrl("https://api.framprox.ws/embed/movie/" + movieId);
        row1.add(watchBtn1);
        InlineKeyboardButton watchBtn2 = new InlineKeyboardButton();
        watchBtn2.setText("Смотреть зеркало 2");
        watchBtn2.setUrl("https://api.delivembed.cc/embed/movie/" + movieId);
        row2.add(watchBtn2);
        InlineKeyboardButton watchBtn3 = new InlineKeyboardButton();
        watchBtn3.setText("Смотреть зеркало 3");
        watchBtn3.setUrl("https://appi23456.delivembed.cc/embed/movie/" + movieId);
        row3.add(watchBtn3);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        inlineKeyboardMarkup.setKeyboard(rows);
        responseMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            bot.execute(responseMessage);
        } catch (TelegramApiException e) {
            log.error("Error ", e);
        }
    }

    @Override
    public void sendMessage(Long chatId, String message) {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(chatId);
        responseMessage.setText(message);

        try {
            bot.execute(responseMessage);
        } catch (TelegramApiException e) {
            log.error("Error ", e);
        }
    }
}