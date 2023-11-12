package com.alakey.telegrambot.web.screens.note;

import com.alakey.telegrambot.entity.Chat;
import com.alakey.telegrambot.entity.StatusEnum;
import com.alakey.telegrambot.web.util.FileIconHelper;
import com.haulmont.cuba.core.app.FileStorageService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.screen.*;
import com.alakey.telegrambot.entity.Note;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.cuba.web.gui.components.WebClasspathResource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@UiController("telegrambot_Note.edit")
@UiDescriptor("note-edit.xml")
@EditedEntityContainer("noteDc")
@LoadDataBeforeShow
public class NoteEdit extends StandardEditor<Note> {

    @Inject
    private DataManager dataManager;
    @Inject
    private LookupPickerField<Chat> chatField;
    @Inject
    private UiComponents uiComponents;
    @Inject
    private FileUploadingAPI fileUploadingAPI;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private Dialogs dialogs;
    @Inject
    private HBoxLayout htmlParentHb;
    @Inject
    private FileMultiUploadField fileUploader;
    @Inject
    private FileStorageService fileStorageService;

    @Subscribe
    public void onInit(InitEvent event) {
        List<Chat> chatList = dataManager.load(Chat.class)
                .list();
        chatField.setOptionsList(chatList);
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        getScreenData().loadAll();
        initFileUploader();
        initShowFiles();
    }

    private void initFileUploader() {
        fileUploader.addQueueUploadCompleteListener(queueUploadCompleteEvent -> {
            for (Map.Entry<UUID, String> entry : fileUploader.getUploadsMap().entrySet()) {
                UUID fileId = entry.getKey();
                String fileName = entry.getValue();
                FileDescriptor fd = fileUploadingAPI.getFileDescriptor(fileId, fileName);
                if (fd == null) {
                    continue;
                }
                try {
                    fileUploadingAPI.putFileIntoStorage(fileId, fd);
                } catch (FileStorageException e) {
                    throw new RuntimeException("Error saving file to FileStorage", e);
                }
                if (getEditedEntity().getFiles() == null) {
                    getEditedEntity().setFiles(new ArrayList<>());
                }
                getEditedEntity().getFiles().add(dataManager.commit(fd));
            }
            initShowFiles();
            fileUploader.clearUploads();
        });
    }

    private void initShowFiles() {
        List<FileDescriptor> files;
        if (getEditedEntity().getFiles() == null)
            files = new ArrayList<>();
        else
            files = getEditedEntity().getFiles();
        if (files.size() > 0)
            htmlParentHb.setVisible(true);
        StringBuilder resultHtml = new StringBuilder();
        htmlParentHb.removeAll();
        HtmlBoxLayout htmlBoxLayout = uiComponents.create(HtmlBoxLayout.NAME);
        htmlBoxLayout.setHtmlSanitizerEnabled(false);
        htmlBoxLayout.setId("filesBox");
        for (FileDescriptor file : files) {
            String item = "<div class=\"tracker-file-item\">" +
                    "<div class=\"tracker-file-description\">" +
                    "<div location=\"mimeImage" + file.getId() + "\"></div>" +
                    "<div class=\"tracker-file-previewDivBtn\" location=\"previewBtn" + file.getId() + "\"></div>" +
                    "<div location=\"fileName" + file.getId() + "\"></div>" +
                    "</div>" +
                    "<div class=\"incident-file-removeBtn\" location=\"removeFileBtn" + file.getId() + "\"></div>" +
                    "<div class=\"incident-file-removeBtn\" location=\"downloadBtn" + file.getId() + "\"></div>" +
                    "</div>";
            resultHtml.append(item);

            String fileName = file.getName();
            Image mimeImage = uiComponents.create(Image.NAME);
            mimeImage.setId("mimeImage" + file.getId());
            mimeImage.setWidth("32px");
            mimeImage.setHeight("32px");
            mimeImage.setScaleMode(Image.ScaleMode.SCALE_DOWN);
            mimeImage.setSource(new WebClasspathResource().setPath(FileIconHelper.getIcon(fileName)));
            Label<String> fileNameLabel = uiComponents.create(Label.NAME);
            fileNameLabel.setId("fileName" + file.getId());
            fileNameLabel.setValue(fileName.length() > 20 ? fileName.substring(0, 15) + "..." : fileName);
            fileNameLabel.setStyleName("tracker-fileName-label");
            fileNameLabel.setWidth("60px");
            fileNameLabel.setHeight("40px");
            PopupView popupView = uiComponents.create(PopupView.NAME);
            popupView.setId("popup" + file.getId());
            popupView.setPopupPosition(PopupView.PopupPosition.MIDDLE_CENTER);
            BrowserFrame previewFrame = uiComponents.create(BrowserFrame.NAME);
            previewFrame.setWidth("900px");
            previewFrame.setHeight("750px");
            popupView.setPopupContent(previewFrame);
            popupView.setHideOnMouseOut(false);

            Button previewBtn = uiComponents.create(Button.NAME);
            previewBtn.setId("previewBtn" + file.getId());
            previewBtn.setIcon(CubaIcon.EYE.source());
            previewBtn.setStyleName("tracker-previewBtn icon-only");
            previewBtn.setWidth("97px");
            previewBtn.setHeight("40px");
            previewBtn.addClickListener(clickEvent -> {
                exportDisplay.show(file);
            });

            Button downloadBtn = uiComponents.create(Button.class);
            downloadBtn.setId("downloadBtn" + file.getId());
            downloadBtn.setCaption("Скачать");
            downloadBtn.setStyleName("borderless");
            downloadBtn.setWidth("77px");
            downloadBtn.setHeight("20px");
            downloadBtn.addClickListener(clickEvent ->
            {
                try {
                    exportDisplay.show(new ByteArrayDataProvider(fileStorageService.loadFile(file)), fileName, ExportFormat.OCTET_STREAM);
                } catch (FileStorageException e) {
                    throw new RuntimeException(e);
                }
            });
            Button removeFileBtn = uiComponents.create(Button.class);
            removeFileBtn.setId("removeFileBtn" + file.getId());
            removeFileBtn.setStyleName("icon-only borderless");
            removeFileBtn.setIcon(CubaIcon.TRASH.source());
            removeFileBtn.setWidth("20px");
            removeFileBtn.setHeight("20px");
            removeFileBtn.addClickListener(clickEvent ->
                    dialogs.createOptionDialog()
                            .withCaption("Удаление файла")
                            .withMessage("Удалить файл " + file.getName())
                            .withActions(new DialogAction(DialogAction.Type.NO),
                                    new DialogAction(DialogAction.Type.OK)
                                            .withHandler(actionPerformedEvent -> {
                                                dataManager.remove(file);
                                                getEditedEntity().getFiles().remove(file);
                                                try {
                                                    fileStorageService.removeFile(file);
                                                } catch (FileStorageException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                initShowFiles();
                                            }))
                            .show());

            htmlBoxLayout.add(mimeImage);
            htmlBoxLayout.add(fileNameLabel);
            htmlBoxLayout.add(previewBtn);
            htmlBoxLayout.add(downloadBtn);
            htmlBoxLayout.add(popupView);
            htmlBoxLayout.setTemplateContents(item);
            htmlBoxLayout.add(removeFileBtn);
        }
        htmlBoxLayout.setTemplateContents(resultHtml.toString());
        htmlParentHb.add(htmlBoxLayout, 0);
    }

    @Subscribe
    public void onBeforeClose(BeforeCloseEvent event) {
        if (getEditedEntity().getDateScheduled() != null) {
            getEditedEntity().setStatus(StatusEnum.DELAYED.getId());
        }
        dataManager.commit(getEditedEntity());
    }
}