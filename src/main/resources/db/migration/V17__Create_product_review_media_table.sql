do
$$
    begin
        begin
            create table if not exists product_review_media
            (
                id                     bigserial,
                product_review_id      bigint       not null,
                media_type             media_type   not null,
                mime_type              varchar(255) not null,
                url                    text         not null,
                alt_text               varchar(255) not null,
                position               int          not null default 0,
                creation_date          timestamptz  not null default now(),
                last_modification_date timestamptz  not null default now(),

                constraint pk_product_review_media_id primary key (id),
                constraint fk_product_review_media_product_review_id foreign key (product_review_id) references product_reviews (id),
                constraint check_product_review_media_url check ( length(url) <= 20000 ),
                constraint check_product_review_media_position check ( position >= 0 )
            );

            create index if not exists idx_product_review_media_product_review_id on product_review_media (product_review_id);

            revoke update (creation_date) on product_review_media from public;

            raise notice 'Product review media table and indexes have been successfully created';

        exception
            when others then
                raise notice 'Error when creating product review media table and indexes: %s', sqlerrm;
        end;
    end
$$;