alter table products
    add column if not exists article_code varchar(255) default null;