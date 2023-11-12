package com.alakey.telegrambot.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "TELEGRAMBOT_PLAYER")
@Entity(name = "telegrambot_Player")
@NamePattern("%s|name")
public class Player extends StandardEntity {
    private static final long serialVersionUID = -4526290431392266863L;

    @NotNull
    @Column(name = "NAME")
    protected String name;

    @NotNull
    @Column(name = "MOVIE_ID")
    protected Integer movieId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }
}