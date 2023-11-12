package com.alakey.telegrambot.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface PlayerConfig extends Config  {

    @Property("player.url")
    @DefaultString("https://api.framprox.ws/embed/movie/")
    String getPlayerUrl();

    //Date start dd.MM.yyyy HH:mm
    @Property("player.dateStart")
    @DefaultString("24.10.2023 00:59")
    String getDateStart();

    //Interval
    @Property("player.hours")
    @DefaultString("6")
    String getHours();

    void setDateStart(String DateStart);
}
