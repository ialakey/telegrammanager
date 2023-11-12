package com.alakey.telegrambot.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "TELEGRAMBOT_CHAT")
@Entity(name = "telegrambot_Chat")
@NamePattern("%s|name")
public class Chat extends StandardEntity {
    private static final long serialVersionUID = -1530545653580378524L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "DESCRIPTION")
    protected String description;

    @NotNull
    @Column(name = "CHAT_ID")
    protected String chatId;

    @NotNull
    @Column(name = "BOT_TOKEN")
    protected String botToken;

    @Column(name = "CHAT_MEMBERS_COUNT")
    protected String chatMembersCount;

    @ManyToMany
    @JoinTable(name = "TELEGRAMBOT_FILE_DESCRIPTOR_CHAT_LINK",
            joinColumns = @JoinColumn(name = "CHAT_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    private List<FileDescriptor> files;

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

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getChatMembersCount() {
        return chatMembersCount;
    }

    public void setChatMembersCount(String chatMembersCount) {
        this.chatMembersCount = chatMembersCount;
    }

    public List<FileDescriptor> getFiles() {
        return files;
    }

    public void setFiles(List<FileDescriptor> files) {
        this.files = files;
    }
}