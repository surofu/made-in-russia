alter table product_review_media
    drop constraint if exists fk_product_images_product_id;

alter table product_review_media
    drop column if exists product_id;

alter table product_review_media
    add column if not exists product_review_id bigint not null;

do
$$
    begin
        if not exists(select 1
                      from pg_constraint
                      where conname = 'fk_product_review_media_product_review_id') then
            execute '
                    alter table product_review_media
                    add constraint fk_product_review_media_product_review_id
                    foreign key (product_review_id) references product_reviews (id);
                    ';
        end if;
    end;
$$;