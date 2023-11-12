package com.alakey.telegrambot.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

@Service(FileManagerService.NAME)
public class FileManagerServiceBean implements FileManagerService {

    @Inject
    private DataManager dataManager;
    @Inject
    private Metadata metadata;
    @Inject
    private FileLoader fileLoader;

    @Override
    public FileDescriptor saveImage(String imageId) {
        if ("null".equals(imageId)) {
            return null;
        }

        String imageUrl = "https://image.tmdb.org/t/p/w500/" + imageId;

        try {
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                return createFile(imageId, byteArrayOutputStream.toByteArray());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FileDescriptor createFile(String imageId, byte[] fileData) {
        FileDescriptor fileDescriptor = metadata.create(FileDescriptor.class);
        fileDescriptor.setName(imageId);
        fileDescriptor.setCreateDate(new Date());
        try {
            fileLoader.saveStream(fileDescriptor, () -> new ByteArrayInputStream(fileData));
        } catch (FileStorageException e) {
            throw new IllegalStateException(e);
        }
        dataManager.commit(fileDescriptor);
        return fileDescriptor;
    }
}