alter table products
    alter column further_description drop not null,
    alter column further_description_translations drop not null;