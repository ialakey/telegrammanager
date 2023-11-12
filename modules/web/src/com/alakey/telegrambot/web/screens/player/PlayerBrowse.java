package com.alakey.telegrambot.web.screens.player;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.alakey.telegrambot.entity.Player;

import javax.inject.Inject;
import java.util.List;

@UiController("telegrambot_Player.browse")
@UiDescriptor("player-browse.xml")
@LookupComponent("playersTable")
@LoadDataBeforeShow
public class PlayerBrowse extends StandardLookup<Player> {

    @Inject
    private DataManager dataManager;
    @Inject
    private CollectionLoader<Player> playersDl;

    @Subscribe("clearBtn")
    public void onClearBtnClick(Button.ClickEvent event) {
        List<Player> playerList = dataManager.load(Player.class)
                .list();
        for (Player player: playerList) {
            dataManager.remove(player);
        }
        playersDl.load();
    }
}