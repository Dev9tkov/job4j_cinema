DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS hall;
create table hall (
    id serial primary key,
    row integer not null,
    place integer not null,
    price integer not null,
    free boolean);
create table account (
    id serial primary key,
    name text,
    telnumber text,
    hall_id integer references hall (id));
insert into hall (row, place, price, free)
values (1, 1, 300, false), (1, 2, 300, false), (1, 3, 300, false),
       (2, 1, 400, false), (2, 2, 500, false), (2, 3, 400, false),
       (3, 1, 200, false), (3, 2, 200, false), (3, 3, 200, false);


