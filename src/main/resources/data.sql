merge into MPA (MPA_ID, MPA_NAME)
    values (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');
merge into GENRES (GENRE_ID, GENRE_NAME)
    values (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

-- INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY)
-- VALUES ('test1@mail.ru', 'JohnnyBoy1', 'John Filters1', '1996-03-31');
-- INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY)
-- VALUES ('test2@mail.ru', 'JohnnyBoy2', 'John Filters2', '1996-03-30');
-- INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY)
-- VALUES ('test3@mail.ru', 'JohnnyBoy3', 'John Filters3', '1996-03-30');
-- INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY)
-- VALUES ('test4@mail.ru', 'JohnnyBoy4', 'John Filters4', '1996-03-30');
-- INSERT INTO FRIENDS(user1_id, user2_id) VALUES (1,3);
-- INSERT INTO FRIENDS(user1_id, user2_id) VALUES (2,3);
-- INSERT INTO FRIENDS(user1_id, user2_id) VALUES (1,2);
-- INSERT INTO FRIENDS(user1_id, user2_id) VALUES (1,4);
-- INSERT INTO FRIENDS(user1_id, user2_id) VALUES (2,4);
-- INSERT INTO FRIENDS(user1_id, user2_id) VALUES (3,4);
--
-- INSERT INTO GENRES(GENRE_NAME)
-- VALUES ( 'Comedy' );
-- INSERT INTO GENRES(GENRE_NAME)
-- VALUES ( 'Drama' );
-- INSERT INTO GENRES(GENRE_NAME)
-- VALUES ( 'SciFi' );
--
-- INSERT INTO MPA(MPA_NAME)
-- values('G');
--
-- INSERT INTO FILMS(FILM_NAME, RELEASE_DATE, DESCRIPTION, DURATION, MPA_ID)
-- VALUES ( 'Film1','1980-03-12','blabla1',120,1 );
--
-- INSERT INTO FILMS(FILM_NAME, RELEASE_DATE, DESCRIPTION, DURATION, MPA_ID)
-- VALUES ( 'Film2','1980-03-12','blabla2',120,1 );
--
-- INSERT INTO FILMS(FILM_NAME, RELEASE_DATE, DESCRIPTION, DURATION, MPA_ID)
-- VALUES ( 'Film3','1980-03-12','blabla3',120,1 );
--
-- insert into FILMS_GENRES(FILM_ID, GENRE_ID) VALUES ( 1,1 );
-- insert into FILMS_GENRES(FILM_ID, GENRE_ID) VALUES ( 1,2 );
-- --insert into FILMS_GENRES(FILM_ID, GENRE_ID) VALUES ( 2,1 );
-- insert into FILMS_GENRES(FILM_ID, GENRE_ID) VALUES ( 3,1 );
-- insert into FILMS_GENRES(FILM_ID, GENRE_ID) VALUES ( 3,3 );

