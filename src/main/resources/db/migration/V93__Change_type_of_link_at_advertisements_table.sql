alter table advertisements
    alter column link type text,
    add check ( length(link) < 20000 );