do
$$
    begin
        begin
            create table if not exists product_faq
            (
                id                     bigserial,
                product_id             bigint      not null,
                question               text        not null,
                answer                 text        not null,
                creation_date          timestamptz not null default now(),
                last_modification_date timestamptz not null default now(),

                constraint pk_product_faq_id primary key (id),
                constraint fk_product_faq_product_id foreign key (product_id) references products (id),
                constraint check_product_faq_question check ( length(question) < 20000 ),
                constraint check_product_faq_answer check ( length(answer) < 20000 )
            );

            create index if not exists idx_product_faq_product_id on product_faq (product_id);

            revoke update (creation_date) on product_faq from public;

            raise notice 'Product faq table and indexes have been successfully created';

        exception
            when others then
                raise notice 'Error when creating product faq table and indexes: %s', sqlerrm;
        end;
    end
$$;