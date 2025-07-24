alter table advertisements
    add column if not exists third_text varchar(255);

update advertisements
set third_text = ''
where third_text is null;

alter table advertisements
    alter column third_text set not null;