DROP table if exists GENRES,MPA,FILMS,FILMS_GENRES,USERS,FILMS_LIKES,FRIENDS;

create table IF NOT EXISTS GENRES
(
    GENRE_ID   INTEGER auto_increment,
    GENRE_NAME CHARACTER VARYING(30) not null,
    constraint GENRES_PK
        primary key (GENRE_ID)
);

create table IF NOT EXISTS MPA
(
    MPA_ID   INTEGER auto_increment,
    MPA_NAME CHARACTER VARYING(10) not null,
    constraint MPA_PK
        primary key (MPA_ID)
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment,
    FILM_NAME    CHARACTER VARYING(50) not null,
    RELEASE_DATE DATE                  not null,
    DESCRIPTION  CHARACTER VARYING(255),
    DURATION     INTEGER               not null,
    MPA_ID       INTEGER               not null,
    LIKES_COUNT  INTEGER default 0     not null,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS__FK
        foreign key (MPA_ID) references MPA
);

create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment,
    EMAIL    CHARACTER VARYING     not null,
    LOGIN    CHARACTER VARYING(50) not null,
    NAME     CHARACTER VARYING(50) not null,
    BIRTHDAY DATE                  not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create table IF NOT EXISTS FILMS_LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint FILMS_LIKES_PK
        primary key (USER_ID, FILM_ID),
    constraint FILMS_LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS on delete cascade,
    constraint FILMS_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS on delete cascade
);

create table IF NOT EXISTS FRIENDS
(
    USER1_ID INTEGER not null,
    USER2_ID INTEGER not null,
    constraint FRIENDS_PK
        primary key (USER1_ID, USER2_ID),
    constraint FRIENDS_USERS_USER_ID_FK
        foreign key (USER1_ID) references USERS on delete cascade,
    constraint FRIENDS_USERS_USER_ID_FK_2
        foreign key (USER2_ID) references USERS on delete cascade
);

create table IF NOT EXISTS FILMS_GENRES
(
    FILM_ID  INTEGER NOT NULL,
    GENRE_ID INTEGER NOT NULL,
    constraint FILMS_GENRES_PK
        primary key (FILM_ID, GENRE_ID),
    constraint FILMS_GENRES_FILMS_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILMS_GENRES_GENRES_FK
        foreign key (GENRE_ID) references GENRES
);
