alter table products
    add column if not exists primary_description text not null default '' check ( length(primary_description) < 5000 );