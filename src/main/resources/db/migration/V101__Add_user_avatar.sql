alter table users
    add column avatar_url text check ( length(avatar_url) <= 20000 );