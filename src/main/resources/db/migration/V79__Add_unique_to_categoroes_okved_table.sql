alter table categories_okved
add constraint uk_categories_okved_category_id_okved_id unique (category_id, okved_id);