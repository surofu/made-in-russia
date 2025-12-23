create table categories_okved
(
    id          bigserial primary key,
    category_id bigint      not null,
    okved_id    varchar(50) not null,

    foreign key (category_id) references categories (id),
    unique (category_id, okved_id)
);