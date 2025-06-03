do
$$
    begin
        begin

            create table if not exists product_media
            (
                id                     bigserial,
                product_id             bigint       not null,
                media_type             media_type   not null,
                mime_type              varchar(255) not null,
                url                    text         not null,
                alt_text               varchar(255) not null,
                position               int          not null default 0,
                creation_date          timestamptz  not null default now(),
                last_modification_date timestamptz  not null default now(),

                constraint pk_product_media_id primary key (id),
                constraint fk_product_media_product_id foreign key (product_id) references products (id),
                constraint check_product_media_url check ( length(url) <= 20000 ),
                constraint check_product_media_position check ( position >= 0 )
            );

            create index if not exists idx_product_media_product_id on product_media (product_id);

            revoke update (creation_date) on product_media from public;

            raise notice 'Product media table and indexes have been successfully created';

        exception
            when others then
                raise notice 'Error when creating product media table and indexes: %s', sqlerrm;
        end;
    end
$$;