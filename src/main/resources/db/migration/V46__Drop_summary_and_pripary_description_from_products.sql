alter table products
    drop column if exists summary_description,
    drop column if exists primary_description;