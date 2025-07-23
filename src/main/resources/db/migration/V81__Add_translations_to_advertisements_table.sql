alter table advertisements
    add column title_translations    hstore not null default '',
    add column subtitle_translations hstore not null default '';
