do
$$
    begin
        begin
            create table if not exists product_reviews
            (
                id                     bigserial,
                product_id             bigint      not null,
                user_id                bigint      not null,
                content                text        not null,
                rating                 int         not null default 1,
                creation_date          timestamptz not null default now(),
                last_modification_date timestamptz not null default now(),

                constraint pk_product_reviews_id primary key (id),
                constraint fk_product_reviews_product_id foreign key (product_id) references products (id),
                constraint fk_product_reviews_user_id foreign key (user_id) references users (id),
                constraint check_product_reviews_content check ( length(content) <= 10000 ),
                constraint check_product_reviews_rating check ( rating >= 1 and rating <= 5 )
            );

            create index if not exists idx_product_reviews_product_id on product_reviews (product_id);
            create index if not exists idx_product_reviews_user_id on product_reviews (user_id);

            revoke update (creation_date) on product_reviews from public;

            raise notice 'Product reviews table and indexes have been successfully created';

        exception
            when others then
                raise notice 'Error when creating product reviews table and indexes: %s', sqlerrm;
        end;
    end
$$;