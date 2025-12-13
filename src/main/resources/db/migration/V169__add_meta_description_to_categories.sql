alter table categories
    add column meta_description              text check ( length(meta_description) <= 1000 ),
    add column meta_description_translations jsonb;