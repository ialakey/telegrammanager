package com.alakey.telegrambot.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "TELEGRAMBOT_CATEGORY")
@Entity(name = "telegrambot_Category")
@NamePattern("%s|name")
public class Category extends StandardEntity {
    private static final long serialVersionUID = -4110591161842908815L;

    @NotNull
    @Column(name = "NAME", nullable = false, unique = true)
    protected String name;

    @Column(name = "MOVIEDB_ID")
    protected String movieDbId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMovieDbId() {
        return movieDbId;
    }

    public void setMovieDbId(String movieDbId) {
        this.movieDbId = movieDbId;
    }
}