package com.alakey.telegrambot.web.screens.note;

import com.alakey.telegrambot.entity.Chat;
import com.alakey.telegrambot.entity.Player;
import com.alakey.telegrambot.entity.StatusEnum;
import com.alakey.telegrambot.service.JsonParserService;
import com.alakey.telegrambot.service.PlayerParserService;
import com.alakey.telegrambot.service.TelegramBotService;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.core.inputdialog.DialogActions;
import com.haulmont.cuba.gui.app.core.inputdialog.DialogOutcome;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import com.alakey.telegrambot.entity.Note;
import com.haulmont.cuba.gui.screen.LookupComponent;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@UiController("telegrambot_Note.browse")
@UiDescriptor("note-browse.xml")
@LookupComponent("notesTable")
@LoadDataBeforeShow
public class NoteBrowse extends StandardLookup<Note> {
    @Inject
    private DataManager dataManager;
    @Inject
    private Dialogs dialogs;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private Notifications notifications;
    @Inject
    private JsonParserService jsonParserService;
    @Inject
    private TelegramBotService telegramBotService;
    @Inject
    private PlayerParserService playerParserService;
    @Inject
    private CollectionLoader<Note> notesDl;
    @Inject
    private GroupTable<Note> notesTable;

    @Subscribe("sendBtn")
    public void onSendBtnClick(Button.ClickEvent event) {
        try {
            telegramBotService.send(Objects.requireNonNull(notesTable.getSingleSelected()).getId());
            notesDl.load();
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withCaption("Отправлено")
                    .show();
        } catch (NullPointerException e) {

        }
    }

    @Subscribe("clearAllBtn")
    public void onClearAllBtnClick(Button.ClickEvent event) {
        List<Note> noteList = dataManager.load(Note.class)
                .list();
        for (Note note: noteList) {
            dataManager.remove(note);
        }
        notesDl.load();
        notifications.create(Notifications.NotificationType.HUMANIZED)
                .withCaption("Очищенно")
                .show();
    }

    @Subscribe("uploadFilmBtn")
    public void onUploadFilmBtnClick(Button.ClickEvent event) {
        List<Chat> chatList = dataManager.load(Chat.class)
                .list();
        Player player = dataManager.load(Player.class)
                .query("select p from telegrambot_Player p where p.movieId = (select max(p2.movieId) from telegrambot_Player p2)")
                .optional()
                .orElse(null);
        FrameOwner owner = this;
        dialogs.createInputDialog(owner)
                .withCaption("Выберите чат и сколько записей загрузить")
                .withParameter(new InputParameter("chatId")
                        .withField(() -> {
                            LookupField<Chat> fieldChat = uiComponents.create(LookupField.NAME);
                            fieldChat.setCaption("Выберите чат");
                            fieldChat.setOptionsList(new ArrayList<>(chatList));
                            fieldChat.setWidthFull();
                            fieldChat.setRequired(true);
                            return fieldChat;
                        })
                        .withRequired(true)
                )
                .withParameter(new InputParameter("start")
                        .withField(() -> {
                            String startValue = "0";
                            if (player != null) {
                                startValue = String.valueOf(player.getMovieId());
                            }
                            TextField<String> fieldStart = uiComponents.create(TextField.NAME);
                            fieldStart.setCaption("Старт ID");
                            fieldStart.setValue(startValue);
                            fieldStart.setWidthFull();
                            fieldStart.setRequired(true);
                            return fieldStart;
                        })
                        .withRequired(true)
                )
                .withParameter(new InputParameter("finish")
                        .withField(() -> {
                            String finishValue = "22";
                            if (player != null) {
                                finishValue = String.valueOf(player.getMovieId() + 22);
                            }
                            TextField<String> fieldFinish = uiComponents.create(TextField.NAME);
                            fieldFinish.setCaption("Финиш ID");
                            fieldFinish.setValue(finishValue);
                            fieldFinish.setWidthFull();
                            fieldFinish.setRequired(true);
                            return fieldFinish;
                        })
                        .withRequired(true)
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.closedWith(DialogOutcome.OK)) {
                        playerParserService.getMovieUrl(
                                Integer.parseInt(closeEvent.getValue("start")),
                                Integer.parseInt(closeEvent.getValue("finish"))
                        );
                        List<Player> playerList = dataManager.load(Player.class)
                                .list();
                        jsonParserService.createNote(playerList, closeEvent.getValue("chatId"));
                        notesDl.load();
                        notifications.create(Notifications.NotificationType.HUMANIZED)
                                .withCaption("Загружено")
                                .show();
                        notesDl.load();
                    }
                })
                .build()
                .show();
    }

    @Subscribe("changeDateBtn")
    public void onChangeDateBtnClick(Button.ClickEvent event) {
        FrameOwner owner = this;
        dialogs.createInputDialog(owner)
                .withCaption("Изменение времени публикации")
                .withParameter(new InputParameter("date")
                        .withField(() -> {
                            DateField dateField = uiComponents.create(DateField.NAME);
                            dateField.setCaption("Выберите дату начала");
                            dateField.setWidthFull();
                            dateField.setRequired(true);
                            return dateField;
                        })
                        .withRequired(true)
                )
                .withParameter(new InputParameter("hours")
                        .withField(() -> {
                            TextField<String> hoursField = uiComponents.create(TextField.NAME);
                            hoursField.setCaption("Периодичность (ч.)");
                            hoursField.setWidthFull();
                            hoursField.setRequired(true);
                            return hoursField;
                        })
                        .withRequired(true)
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.closedWith(DialogOutcome.OK)) {
                        List<Note> noteList = dataManager.load(Note.class)
                                .query("select n from telegrambot_Note n order by n.dateScheduled asc")
                                .list();
                        Date baseDate = closeEvent.getValue("date");
                        for (int i = 0; i < noteList.size(); i++) {
                            Note note = noteList.get(i);
                            Date currentDate = baseDate;
                            if (i > 0) {
                                Date previousDate = noteList.get(i - 1).getDateScheduled();
                                currentDate = new Date(previousDate.getTime() + Integer.parseInt(closeEvent.getValue("hours")) * 60 * 60 * 1000);
                            }
                            note.setDateScheduled(currentDate);
                            dataManager.commit(note);
                        }
                        notifications.create(Notifications.NotificationType.HUMANIZED)
                                .withCaption("Изменено")
                                .show();
                        notesDl.load();
                    }
                })
                .build()
                .show();
    }

    @Subscribe("changeStatusBtn")
    public void onChangeStatusBtnClick(Button.ClickEvent event) {
        FrameOwner owner = this;
        dialogs.createInputDialog(owner)
                .withCaption("Изменение статусов")
                .withParameter(new InputParameter("status")
                        .withField(() -> {
                            LookupField<StatusEnum> lookupField = uiComponents.create(LookupField.class);
                            lookupField.setOptionsEnum(StatusEnum.class);
                            lookupField.setOptionCaptionProvider(status -> status.getId());
                            lookupField.setCaption("Выберите статус");
                            lookupField.setWidthFull();
                            lookupField.setRequired(true);
                            return lookupField;
                        })
                        .withRequired(true)
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.closedWith(DialogOutcome.OK)) {
                        List<Note> noteList = dataManager.load(Note.class).list();
                        StatusEnum selectedStatusEnum = closeEvent.getValue("status");
                        String selectedStatus = selectedStatusEnum.getId();
                        for (Note note: noteList) {
                            note.setStatus(selectedStatus);
                            dataManager.commit(note);
                        }
                        notifications.create(Notifications.NotificationType.HUMANIZED)
                                .withCaption("Изменено")
                                .show();
                        notesDl.load();
                    }
                })
                .build()
                .show();
    }

    @Subscribe("changeChatBtn")
    public void onChangeChatBtnClick(Button.ClickEvent event) {
        FrameOwner owner = this;
        dialogs.createInputDialog(owner)
                .withCaption("Изменение чата")
                .withParameter(new InputParameter("chat")
                        .withField(() -> {
                            List<Chat> chatList = dataManager.load(Chat.class)
                                    .list();
                            LookupField<Chat> lookupField = uiComponents.create(LookupField.class);
                            lookupField.setOptionsList(chatList);
                            lookupField.setCaption("Выберите чат");
                            lookupField.setWidthFull();
                            lookupField.setRequired(true);
                            return lookupField;
                        })
                        .withRequired(true)
                )
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.closedWith(DialogOutcome.OK)) {
                        List<Note> noteList = dataManager.load(Note.class)
                                .view("full-note")
                                .list();
                        for (Note note: noteList) {
                            note.setChat(closeEvent.getValue("chat"));
                            dataManager.commit(note);
                        }
                        notifications.create(Notifications.NotificationType.HUMANIZED)
                                .withCaption("Изменено")
                                .show();
                        notesDl.load();
                    }
                })
                .build()
                .show();
    }
}