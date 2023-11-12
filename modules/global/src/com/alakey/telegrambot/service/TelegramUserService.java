package com.alakey.telegrambot.service;

import org.telegram.telegrambots.meta.api.objects.User;

public interface TelegramUserService {
    String NAME = "telegrambot_TelegramUserService";

    void registry(User user, Long chatId);
}