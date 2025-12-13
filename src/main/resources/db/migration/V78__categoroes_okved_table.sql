create table if not exists categories_okved
(
    id          bigserial primary key,
    category_id bigint       not null,
    okved_id    varchar(255) not null,

    constraint fk_categories_okved_category_id foreign key (category_id) references categories (id)
);