alter table advertisements
    add column if not exists link varchar(255);

update advertisements
set link = ''
where link is null;

alter table advertisements
    alter column link set not null;