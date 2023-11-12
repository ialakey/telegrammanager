package com.alakey.telegrambot.web.screens.player;

import com.haulmont.cuba.gui.screen.*;
import com.alakey.telegrambot.entity.Player;

@UiController("telegrambot_Player.edit")
@UiDescriptor("player-edit.xml")
@EditedEntityContainer("playerDc")
@LoadDataBeforeShow
public class PlayerEdit extends StandardEditor<Player> {
}