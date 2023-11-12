package com.alakey.telegrambot.service;

import java.util.UUID;

public interface TelegramBotService {
    String NAME = "telegrambot_TelegramBotService";

    void send(UUID id);

    void sendMovie(Long chatId, String name, String id);

    void sendMessage(Long chatId, String message);
}