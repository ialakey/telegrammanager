package com.alakey.telegrambot.config;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.config.defaults.DefaultString;

@Source(type = SourceType.DATABASE)
public interface TelegramConfig extends Config {

    @Property("telegram.bot.token")
    @DefaultString("Your token")
    String getToken();

    @Property("telegram.url.bot")
    @DefaultString("https://api.telegram.org/bot")
    String getUrlBot();

    @Property("telegram.url.file")
    @DefaultString("https://api.telegram.org/file/bot")
    String getFileUrl();

    void setToken(String token);

    void setUrlBot(String botUrl);

    void setFileUrl(String fileUrl);
}
