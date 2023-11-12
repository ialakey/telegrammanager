package com.alakey.telegrambot.service;

import com.alakey.telegrambot.dto.ChatDTO;
import com.alakey.telegrambot.entity.Chat;

public interface TelegramChannelService {
    String NAME = "telegrambot_TelegramChannelService";

    void setPhoto(Chat chat);

    ChatDTO getInfoTgChannel(String chatId, String token);
}