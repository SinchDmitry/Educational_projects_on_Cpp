drop table if exists users;
create table if not exists users (
    id bigserial primary key unique,
    email text
 );
