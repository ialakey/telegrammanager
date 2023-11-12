package com.alakey.telegrambot.service;

import com.alakey.telegrambot.entity.Player;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.security.app.Authentication;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO придумать чтото с сервисами слишком плохо выглядит
public class TelegramHelperBot extends TelegramLongPollingBot {

    private final String botToken;
    private final Authentication authentication = AppBeans.get(Authentication.class);
    private final UtilsService utilsService = AppBeans.get(UtilsService.class);
    private final PlayerService playerService = AppBeans.get(PlayerService.class);
    private final TelegramBotService telegramBotService = AppBeans.get(TelegramBotService.class);
    private final TelegramUserService telegramUserService = AppBeans.get(TelegramUserService.class);

    private final Long channelId = -1001972155026L;
    private final String botUrl = "https://t.me/TestMovieAlakeyBot?start=";
    private final String helloMessage = "Привет, %s!\nЧто желаете посмотреть?";
    private final String messageNotFound = "Упс! Фильм с данным id не удалось найти ;(";
    private final String error = "Я не понял команду ;(\nMожет попробуешь еще раз?";
    private final String messageToSubscribe = "Для просмотра подпишитесь на @testromalamovie \uD83D\uDE09";


    public TelegramHelperBot(String botToken) {
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        startBot(update);

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();

            checkAndWatch(callbackQuery, data);
        }
    }

    private void registryUser(User user, Long chatId) {
        authentication.begin();
        try {
            telegramUserService.registry(user, chatId);
        } finally {
            authentication.end();
        }
    }

    private void startBot(Update update) {
        authentication.begin();
        if (update.hasMessage() && update.getMessage().hasText()) {

            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();

            String command = update.getMessage().getText().trim();

            String username = "";
            if (update.getMessage().getFrom().getUserName() != null) {
                username = update.getMessage().getFrom().getUserName();
            } else {
                String lastName = "";
                if (update.getMessage().getFrom().getLastName() != null) {
                    lastName = update.getMessage().getFrom().getLastName();
                }
                username = update.getMessage().getFrom().getFirstName() + " " + lastName;
            }

            registryUser(update.getMessage().getFrom(), chatId);

            botNavigation(chatId, username, command, update, userId);
        }
    }

    private void checkAndWatch(CallbackQuery callbackQuery, String data) {
        if (data.equals("checkAndWatch")) {
            long userId = callbackQuery.getFrom().getId();
            long chatId = callbackQuery.getMessage().getChatId();

            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());

            if (!isSubscribe(chatId, userId)) {
                answer.setText(messageToSubscribe);
                answer.setShowAlert(true);
            } else {
                registryUser(callbackQuery.getFrom(), chatId);

                Pattern pattern = Pattern.compile("🔎 (\\d+) ID");
                Matcher matcher = pattern.matcher(callbackQuery.getMessage().toString());
                if (matcher.find()) {
                    String movieId = matcher.group(1);
                    answer.setUrl(botUrl + movieId);
                } else {
                    telegramBotService.sendMessage(chatId, messageNotFound);
                }
            }
            try {
                execute(answer);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void botNavigation(Long chatId, String username, String command, Update update, Long userId) {
        if (isSubscribe(channelId, userId)) {
            try {
                Message message = update.getMessage();
                String text = update.getMessage().getText();
                if (text.startsWith("/start ")) {
                    String parameter = text.substring(7);
                    Optional<Player> optPlayer = playerService.getPlayerByMovieId(parameter);
                    if (optPlayer.isPresent()) {
                        telegramBotService.sendMovie(message.getChatId(), optPlayer.get().getName(), parameter);
                    } else {
                        telegramBotService.sendMessage(chatId, messageNotFound);
                    }

                } else if (command.startsWith("/start")) {
                    String title = String.format(helloMessage, username);
                    telegramBotService.sendMessage(chatId, title);
                } else if (command.startsWith("/movie")) {
                    String[] movieId = command.split(" ", 2);
                    if (movieId.length == 2) {
                        if (utilsService.isInteger(movieId[1].trim())) {
                            sendMovieById(chatId, movieId[1].trim());
                        } else {
                            sendMovieByName(chatId, movieId[1].trim());
                        }
                    } else {
                        telegramBotService.sendMessage(chatId, error);
                    }
                } else {
                    telegramBotService.sendMessage(chatId, error);
                }
            } finally {
                authentication.end();
            }
        } else {
            telegramBotService.sendMessage(chatId, messageToSubscribe);
        }
    }

    private void sendMovieById(Long chatId, String id) {
        Optional<Player> optPlayer = playerService.getPlayerByMovieId(id);
        if (optPlayer.isPresent()) {
            telegramBotService.sendMovie(chatId, optPlayer.get().getName(), String.valueOf(optPlayer.get().getMovieId()));
        } else {
            telegramBotService.sendMessage(chatId, messageNotFound);
        }
    }

    private void sendMovieByName(Long chatId, String name) {
        Optional<Player> optPlayer = playerService.getPlayerByMovieName(name);
        if (optPlayer.isPresent()) {
            telegramBotService.sendMovie(chatId, optPlayer.get().getName(), String.valueOf(optPlayer.get().getMovieId()));
        } else {
            telegramBotService.sendMessage(chatId, messageNotFound);
        }
    }

    private boolean isSubscribe(long chatId, long userId) {
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId(chatId);
        getChatMember.setUserId(userId);

        ChatMember chatMember = null;
        try {
            chatMember = execute(getChatMember);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
       return chatMember.getStatus().equals("member");
    }

    @Override
    public String getBotUsername() {
        return "TestBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
