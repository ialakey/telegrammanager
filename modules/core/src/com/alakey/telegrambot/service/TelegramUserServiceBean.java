package com.alakey.telegrambot.service;

import com.alakey.telegrambot.entity.TelegramUser;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.inject.Inject;

@Service(TelegramUserService.NAME)
public class TelegramUserServiceBean implements TelegramUserService {

    @Inject
    private DataManager dataManager;

    @Override
    public void registry(User user, Long chatId) {
        TelegramUser telegramUser = dataManager.load(TelegramUser.class)
                .query("select t from telegrambot_TelegramUser t where t.userId = :userId")
                .parameter("userId", user.getId())
                .optional()
                .orElse(null);

        if (telegramUser == null) {
            telegramUser = dataManager.create(TelegramUser.class);
            telegramUser.setUserId(user.getId());
        }

        telegramUser.setUserName(user.getUserName());
        telegramUser.setFirstName(user.getFirstName());
        telegramUser.setLastName(user.getLastName());
        telegramUser.setIsPremium(user.getIsPremium());
        telegramUser.setLanguageCode(user.getLanguageCode());

        telegramUser.setChatId(chatId);

        dataManager.commit(telegramUser);
    }
}