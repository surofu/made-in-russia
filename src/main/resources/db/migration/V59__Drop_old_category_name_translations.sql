alter table categories
    drop column if exists name_en,
    drop column if exists name_ru,
    drop column if exists name_zh;