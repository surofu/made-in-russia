alter table categories
    add column icon_url text check ( length(icon_url) <= 20000 );