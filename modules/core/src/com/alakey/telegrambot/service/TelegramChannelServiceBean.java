package com.alakey.telegrambot.service;

import com.alakey.telegrambot.config.TelegramConfig;
import com.alakey.telegrambot.dto.ChatDTO;
import com.alakey.telegrambot.entity.Chat;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileLoader;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.Metadata;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.util.Date;

@Service(TelegramChannelService.NAME)
public class TelegramChannelServiceBean implements TelegramChannelService {

    @Inject
    private DataManager dataManager;
    @Inject
    private FileStorageService fileStorageService;
    @Inject
    private Metadata metadata;
    @Inject
    private FileLoader fileLoader;
    @Inject
    private TelegramConfig telegramConfig;

    private final String chat = "/getChat";
    private final String file = "/getFile?file_id=";
    private final String members = "/getChatMembersCount";
    private final String photo = "/setChatPhoto";

    private final OkHttpClient client;

    public TelegramChannelServiceBean() {
        client = new OkHttpClient().newBuilder().build();
    }

    @Override
    public void setPhoto(Chat chat) {
        try {
            byte[] byteArray = fileStorageService.loadFile(chat.getFiles().get(0));
            String filePath = chat.getFiles().get(0).getName();
            File outputFile = new File(filePath);

            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(byteArray);
            fos.close();

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("chat_id", chat.getChatId())
                    .addFormDataPart("photo", filePath, RequestBody.create(MediaType.parse("image/" + chat.getFiles().get(0).getExtension()), outputFile))
                    .build();
            Request request = new Request.Builder()
                    .url(telegramConfig.getUrlBot() + chat.getBotToken() + photo)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileStorageException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChatDTO getInfoTgChannel(String chatId, String token) {
        ChatDTO chatDTO = new ChatDTO();
        getInfo(chatDTO, chatId, token);
        getMembersCount(chatDTO, chatId, token);
        return chatDTO;
    }

    private void getInfo(ChatDTO chatDTO, String chatId, String token) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "chat_id=" + chatId);
        Request request = new Request.Builder()
                .url(telegramConfig.getUrlBot() + token + chat)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            if (jsonObject.has("result")) {
                JSONObject result = jsonObject.getJSONObject("result");
                chatDTO.setName(result.getString("title"));

                if (result.has("description")) {
                    chatDTO.setDescription(result.getString("description"));
                }

                if (result.has("photo")) {
                    getFile(chatDTO, token, result.getJSONObject("photo").getString("big_file_id"));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getFile(ChatDTO chatDTO, String token, String fileId) {
        Request request = new Request.Builder()
                .url(telegramConfig.getUrlBot() + token + file + fileId)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            downloadFile(chatDTO, token, jsonObject.getJSONObject("result").getString("file_path"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadFile(ChatDTO chatDTO, String token, String file_path) {
        Request request = new Request.Builder()
                .url(telegramConfig.getFileUrl() + token + "/" + file_path)
                .get()
                .build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    try (InputStream in = new BufferedInputStream(response.body().byteStream())) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                        FileDescriptor fileDescriptor = createFile(file_path, byteArrayOutputStream.toByteArray());
                        chatDTO.setAvatar(fileDescriptor);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private void getMembersCount(ChatDTO chatDTO, String chatId, String token) {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("chat_id", chatId)
                .build();
        Request request = new Request.Builder()
                .url(telegramConfig.getUrlBot() + token + members)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());

            if (jsonObject.has("result")) {
                int result = jsonObject.getInt("result");
                chatDTO.setChatMembersCount(String.valueOf(result));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}