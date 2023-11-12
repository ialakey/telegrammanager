-- begin TELEGRAMBOT_NOTE
create table TELEGRAMBOT_NOTE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(1000),
    DESCRIPTION varchar(1000),
    URL varchar(255),
    DATE_SCHEDULED timestamp,
    DATE_PUBLICATION timestamp,
    STATUS varchar(255),
    CHAT_ID uuid,
    RATING varchar(255),
    RUNTIME varchar(255),
    MOVIE_ID integer,
    --
    primary key (ID)
)^
-- end TELEGRAMBOT_NOTE
-- begin TELEGRAMBOT_CHAT
create table TELEGRAMBOT_CHAT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    DESCRIPTION varchar(255),
    CHAT_ID varchar(255),
    BOT_TOKEN varchar(255),
    CHAT_MEMBERS_COUNT varchar(255),
    --
    primary key (ID)
)^
-- end TELEGRAMBOT_CHAT
-- begin TELEGRAMBOT_FILE_DESCRIPTOR_LINK
create table TELEGRAMBOT_FILE_DESCRIPTOR_LINK (
    NOTE_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (NOTE_ID, FILE_DESCRIPTOR_ID)
)^
-- end TELEGRAMBOT_FILE_DESCRIPTOR_LINK
-- begin TELEGRAMBOT_PLAYER
create table TELEGRAMBOT_PLAYER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    MOVIE_ID integer,
    --
    primary key (ID)
)^
-- end TELEGRAMBOT_PLAYER
-- begin TELEGRAMBOT_CATEGORY
create table TELEGRAMBOT_CATEGORY (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    MOVIEDB_ID varchar(255),
    --
    primary key (ID)
)^
-- end TELEGRAMBOT_CATEGORY
-- begin TELEGRAMBOT_NOTE_CATEGORIES
create table TELEGRAMBOT_NOTE_categories (
    note_ID uuid,
    categories_ID uuid,
    primary key (note_ID, categories_ID)
)^
-- end TELEGRAMBOT_NOTE_CATEGORIES
-- begin TELEGRAMBOT_FILE_DESCRIPTOR_CHAT_LINK
create table TELEGRAMBOT_FILE_DESCRIPTOR_CHAT_LINK (
    CHAT_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (CHAT_ID, FILE_DESCRIPTOR_ID)
)^
-- end TELEGRAMBOT_FILE_DESCRIPTOR_CHAT_LINK
-- begin TELEGRAMBOT_TELEGRAM_USER
create table TELEGRAMBOT_TELEGRAM_USER (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    USER_ID bigint not null,
    CHAT_ID bigint,
    FIRST_NAME varchar(255),
    LAST_NAME varchar(255),
    USER_NAME varchar(255),
    LANGUAGE_CODE varchar(7) not null,
    IS_PREMIUM boolean,
    --
    primary key (ID)
)^
-- end TELEGRAMBOT_TELEGRAM_USER
