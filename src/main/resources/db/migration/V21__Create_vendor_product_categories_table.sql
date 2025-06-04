do
$$
    begin
        begin
            create table if not exists vendor_product_categories
            (
                id                     bigserial,
                vendor_details_id      bigint       not null,
                name                   varchar(255) not null,
                creation_date          timestamptz  not null default now(),
                last_modification_date timestamptz  not null default now(),

                constraint pk_vendor_product_categories_id primary key (id),
                constraint fk_vendor_product_categories_vendor_details_id foreign key (vendor_details_id) references vendor_details (id)
            );

            create index if not exists idx_vendor_product_categories_vendor_details_id on vendor_product_categories (vendor_details_id);

            revoke update (creation_date) on vendor_product_categories from public;

            raise notice 'Table vendor product categories successfully created';
        exception
            when others then
                raise notice 'Error when creating vendor product categories table: %s', sqlerrm;
        end;
    end
$$;