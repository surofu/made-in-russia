alter table product_package_options
    add column if not exists price_unit varchar(255);

update product_package_options
set price_unit = 'RUB'
where price_unit is null;

alter table product_package_options
    alter column price_unit set not null;