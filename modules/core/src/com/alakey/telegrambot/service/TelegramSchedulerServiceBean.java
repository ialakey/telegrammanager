package com.alakey.telegrambot.service;

import com.alakey.telegrambot.entity.Note;
import com.alakey.telegrambot.entity.StatusEnum;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Service(TelegramSchedulerService.NAME)
public class TelegramSchedulerServiceBean implements TelegramSchedulerService {

    @Inject
    private DataManager dataManager;
    @Inject
    private TelegramBotService telegramBotService;

    @Override
    public void distribution() {
        List<Note> notes = dataManager.load(Note.class)
                .query("select n from telegrambot_Note n where n.status = :status")
                .parameter("status", StatusEnum.DELAYED.getId())
                .list();
        for (Note note : notes) {
            Date targetDate = note.getDateScheduled();
            Date currentDate = new Date();
            long delayInMillis = targetDate.getTime() - currentDate.getTime();
            if (delayInMillis < 0) {
                telegramBotService.send(note.getId());
                delayInMillis = 0;
            }
        }
    }
}