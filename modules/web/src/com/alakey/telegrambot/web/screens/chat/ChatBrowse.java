package com.alakey.telegrambot.web.screens.chat;

import com.alakey.telegrambot.dto.ChatDTO;
import com.alakey.telegrambot.service.TelegramChannelService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.screen.*;
import com.alakey.telegrambot.entity.Chat;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@UiController("telegrambot_Chat.browse")
@UiDescriptor("chat-browse.xml")
@LookupComponent("chatsTable")
@LoadDataBeforeShow
public class ChatBrowse extends StandardLookup<Chat> {

    @Inject
    private DataManager dataManager;
    @Inject
    private TelegramChannelService telegramChannelService;

    @Subscribe
    public void onAfterInit(AfterInitEvent event) {
        List<Chat> chatList = dataManager.load(Chat.class)
                .view("full-chat")
                .list();
        for (Chat chat: chatList) {
            ChatDTO chatDTO = telegramChannelService.getInfoTgChannel(chat.getChatId(), chat.getBotToken());
            chat.setName(chatDTO.getName());
            chat.setDescription(chatDTO.getDescription());
            chat.setChatMembersCount(chatDTO.getChatMembersCount());
            List<FileDescriptor> fileDescriptorList = new ArrayList<>();
            fileDescriptorList.add(chatDTO.getAvatar());
            chat.setFiles(fileDescriptorList);
            dataManager.commit(chat);
        }
    }
}