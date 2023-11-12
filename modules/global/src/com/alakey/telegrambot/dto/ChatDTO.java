package com.alakey.telegrambot.dto;

import com.haulmont.cuba.core.entity.FileDescriptor;

import java.io.Serializable;

public class ChatDTO implements Serializable {

    private String name;

    private String description;

    private String chatMembersCount;

    private FileDescriptor avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChatMembersCount() {
        return chatMembersCount;
    }

    public void setChatMembersCount(String chatMembersCount) {
        this.chatMembersCount = chatMembersCount;
    }

    public FileDescriptor getAvatar() {
        return avatar;
    }

    public void setAvatar(FileDescriptor avatar) {
        this.avatar = avatar;
    }
}
