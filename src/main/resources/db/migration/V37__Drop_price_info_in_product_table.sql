alter table products
    drop column if exists original_price;

alter table products
    drop column if exists discount;

alter table products
    drop column if exists price_unit;