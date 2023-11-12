package com.alakey.telegrambot.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.*;

@Table(name = "TELEGRAMBOT_NOTE")
@Entity(name = "telegrambot_Note")
@NamePattern("%s|id")
public class Note extends StandardEntity {
    private static final long serialVersionUID = 2183626463377768370L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "URL")
    protected String url;

    @Column(name = "DATE_SCHEDULED")
    protected Date dateScheduled;

    @Column(name = "DATE_PUBLICATION")
    protected Date datePublication;

    @Column(name = "STATUS")
    protected String status = StatusEnum.DRAFT.getId();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ID")
    protected Chat chat;

    @ManyToMany
    @JoinTable(name = "TELEGRAMBOT_FILE_DESCRIPTOR_LINK",
            joinColumns = @JoinColumn(name = "NOTE_ID"),
            inverseJoinColumns = @JoinColumn(name = "FILE_DESCRIPTOR_ID"))
    private List<FileDescriptor> files;

    @ManyToMany
    @JoinTable(name = "TELEGRAMBOT_NOTE_categories",
            joinColumns = @JoinColumn(name = "note_ID"),
            inverseJoinColumns = @JoinColumn(name = "categories_ID"))
    private List<Category> categories = new ArrayList<>();

    @Column(name = "RATING")
    protected String rating;

    @Column(name = "RUNTIME")
    protected String runtime;

    @Column(name = "MOVIE_ID")
    protected Integer movieId;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDateScheduled() {
        return dateScheduled;
    }

    public void setDateScheduled(Date dateScheduled) {
        this.dateScheduled = dateScheduled;
    }

    public Date getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(Date datePublication) {
        this.datePublication = datePublication;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public List<FileDescriptor> getFiles() {
        return files;
    }

    public void setFiles(List<FileDescriptor> files) {
        this.files = files;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }
}