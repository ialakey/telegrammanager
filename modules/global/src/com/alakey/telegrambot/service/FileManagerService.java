package com.alakey.telegrambot.service;

import com.haulmont.cuba.core.entity.FileDescriptor;

public interface FileManagerService {
    String NAME = "telegrambot_FileManagerService";

    FileDescriptor saveImage(String imageId);
}