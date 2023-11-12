package com.alakey.telegrambot.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum StatusEnum implements EnumClass<String> {
    PUBLISHED("ОПУБЛИКОВАНО"),
    DELAYED("ОТЛОЖЕНО"),
    DRAFT("ЧЕРНОВИК")
    ;

    private String id;

    StatusEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static StatusEnum fromId(String id) {
        for (StatusEnum at : StatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}