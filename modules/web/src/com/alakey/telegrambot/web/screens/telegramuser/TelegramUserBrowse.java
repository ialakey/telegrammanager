package com.alakey.telegrambot.web.screens.telegramuser;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.alakey.telegrambot.entity.TelegramUser;

import javax.inject.Inject;
import java.util.List;

@UiController("telegrambot_TelegramUser.browse")
@UiDescriptor("telegram-user-browse.xml")
@LookupComponent("telegramUsersTable")
@LoadDataBeforeShow
public class TelegramUserBrowse extends StandardLookup<TelegramUser> {

    @Inject
    private DataManager dataManager;
    @Inject
    private CollectionLoader<TelegramUser> telegramUsersDl;

    @Subscribe("clearBtn")
    public void onClearBtnClick(Button.ClickEvent event) {
        List<TelegramUser> telegramUserList = dataManager.load(TelegramUser.class)
                .list();
        for (TelegramUser telegramUser: telegramUserList) {
            dataManager.remove(telegramUser);
        }
        telegramUsersDl.load();
    }
}