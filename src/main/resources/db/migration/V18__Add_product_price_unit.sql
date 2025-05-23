alter table products
    add column if not exists price_unit varchar(255) not null default '';