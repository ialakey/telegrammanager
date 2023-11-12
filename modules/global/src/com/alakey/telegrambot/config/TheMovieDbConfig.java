package com.alakey.telegrambot.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface TheMovieDbConfig extends Config {

    @Property("themoviedb.url")
    @DefaultString("https://api.themoviedb.org/3/")
    String getUrl();

    @Property("themoviedb.keyapi")
    @DefaultString("Your key")
    String getKeyApi();

    @Property("themoviedb.accesskeyapi")
    @DefaultString("Your access key")
    String getAccessKeyApi();
}
