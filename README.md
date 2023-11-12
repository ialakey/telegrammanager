# Project TelegramBot

Hello! Welcome to the [TelegramManager](https://github.com/ialakey/telegrammanager) repository, where we are building an application for automating the publication of posts on Telegram.

## Table of Contents

- [Installation](#installation)
- [Description](#description)
- [Entities](#entities)
- [Demonstration](#demonstration)
- [Authorship](#authorship)
- [Check it](#checkit)

## Installation
<a name="installation"></a>
- Installed and running PostgreSQL
- OpenJDK 11
- [CUBA Platform](https://forum.cuba-platform.com) version 7.2

## Description
<a name="description"></a>
- The project is designed for the automatic filling of posts in a Telegram channel and subsequent publication, leading to earnings of $1,000,000.

## Entities
<a name="entities"></a>

### Note
- A post sent to a Telegram channel.

| Field              | Data Type             | Description                                           |
|--------------------|-----------------------|-------------------------------------------------------|
| ID                 | Unique number         | Unique identifier of the post                          |
| NAME               | String                | Name of the note                                       |
| DESCRIPTION        | String                | Description of the note                                |
| URL                | String                | URL to the video player                                |
| DATE_SCHEDULED     | Date and Time         | Date and time of scheduled post                        |
| DATE_PUBLICATION   | Date and Time         | Date and time of post publication                      |
| STATUS             | String                | Status of the post (default is "DRAFT")                |
| CHAT               | Chat                  | Chat associated with the post                          |
| FILES              | List<FileDescriptor>  | List of files associated with the post                 |
| CATEGORIES         | List<Category>        | List of categories associated with the post            |
| RATING             | String                | Rating of the movie/show                               |
| RUNTIME            | String                | Duration (runtime) of the movie/show                   |
| MOVIE_ID           | String                | Movie identifier (movieId) associated with the post    |


### Category
- Category used for specifying movie categories.

| Field        | Data Type       | Description                      |
|-------------|------------------|-----------------------------------|
| ID          | Unique number   | Unique identifier of the category |
| NAME        | String           | Category name                     |
| MOVIEDB_ID  | String           | Category identifier in MovieDB    |

### Chat
- Telegram channel to be serviced.

| Field                | Data Type           | Description                                      |
|---------------------|----------------------|--------------------------------------------------|
| ID                  | Unique number       | Unique identifier of the chat                    |
| NAME                | String               | Chat name                                        |
| DESCRIPTION         | String               | Description of the chat                          |
| CHAT_ID             | String               | Chat identifier (telegram)                       |
| BOT_TOKEN           | String               | Bot token associated with the chat (telegram)    |
| CHAT_MEMBERS_COUNT  | String               | Number of chat members                           |
| FILES               | List<FileDescriptor> | Chat avatar                                      |

### Player
- Player for storing links to movies taken from an external site.

| Field        | Data Type       | Description                                        |
|-------------|------------------|----------------------------------------------------|
| ID          | Unique number   | Unique identifier of the player                    |
| NAME        | String           | Name of the movie                                  |
| MOVIE_ID    | String           | Movie identifier in MovieDB                        |

### StatusEnum
- Status of the post

| Value     | Description                                                                 |
|-----------|-----------------------------------------------------------------------------|
| PUBLISHED | PUBLISHED - The post has been sent to the channel                           |
| DELAYED   | DELAYED - The post is scheduled for a future date                           |
| DRAFT     | DRAFT - The post has not been sent, and there is no scheduled date          |


### Video Player Mirrors:
- `https://api.delivembed.cc/embed/movie/`
- `https://appi23456.delivembed.cc/embed/movie/`
- `https://api.framprox.ws/embed/movie/`

## Demonstration
<a name="demonstration"></a>
<img width="955" alt="image" src="https://github.com/ialakey/telegrammanager/assets/56916175/f01bb5ad-f113-476b-b8c1-c2eb2500efa6">
<img width="958" alt="image" src="https://github.com/ialakey/telegrammanager/assets/56916175/c3661a1d-ec84-4485-be3f-d5108c31cbbd">
<img width="956" alt="image" src="https://github.com/ialakey/telegrammanager/assets/56916175/5dcd5875-8ab8-44f4-94b2-86e09972c1c6">
<img width="955" alt="image" src="https://github.com/ialakey/telegrammanager/assets/56916175/d37a4d8d-becf-4879-aec7-874b8c8878b0">



## Authorship
<a name="authorship"></a>
The project was developed by [I_Alakey](https://t.me/i_alakey).

## Check it
<a name="checkit"></a>
[Telegram](https://t.me/kino_narezo4ka).
