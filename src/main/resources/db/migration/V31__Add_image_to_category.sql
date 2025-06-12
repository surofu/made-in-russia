alter table categories
    add column if not exists image_url text check ( length(image_url) < 20000 );