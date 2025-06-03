do
$$
    begin

        create table if not exists categories
        (
            id                     bigserial,
            name                   varchar(255) not null,
            creation_date          timestamptz  not null default now(),
            last_modification_date timestamptz  not null default now(),

            constraint pk_categories_id primary key (id),
            constraint uk_categories_name unique (name)
        );

        create table if not exists delivery_methods
        (
            id                     bigserial,
            name                   varchar(255) not null unique,
            creation_date          timestamptz  not null default now(),
            last_modification_date timestamptz  not null default now(),

            constraint pk_delivery_methods_is primary key (id),
            constraint uk_delivery_methods_name unique (name)
        );

        create table if not exists products
        (
            id                     bigserial,
            category_id            bigint       not null,
            article_code           varchar(255) not null default null,
            title                  varchar(255) not null,
            main_description       text         not null,
            further_description    text         not null,
            summary_description    text         not null,
            primary_description    text         not null,
            preview_image_url      text         not null,
            creation_date          timestamptz  not null default now(),
            last_modification_date timestamptz  not null default now(),

            constraint pk_products_id primary key (id),
            constraint uk_products_article_code unique (article_code),
            constraint fk_products_category_id foreign key (category_id) references categories (id),
            constraint check_products_main_description check ( length(main_description) < 50000 ),
            constraint check_products_further_description check ( length(further_description) < 20000 ),
            constraint check_products_summary_description check ( length(summary_description) < 20000 ),
            constraint check_products_primary_description check ( length(primary_description) < 20000 ),
            constraint check_products_preview_image_url check ( length(preview_image_url) <= 20000 )
        );

        create table if not exists products_delivery_methods
        (
            id                 bigserial,
            product_id         bigint not null,
            delivery_method_id bigint not null,

            constraint pk_products_delivery_methods_id primary key (id),
            constraint fk_products_delivery_methods_product_id foreign key (product_id) references products (id),
            constraint fk_products_delivery_methods_delivery_method_id foreign key (delivery_method_id) references delivery_methods (id)
        );

        create index if not exists idx_categories_name on categories (name);
        create index if not exists idx_delivery_methods_name on delivery_methods (name);
        create index if not exists idx_products_category_id on products (category_id);
        create index if not exists idx_products_article_code on products (article_code);

        revoke update (article_code) on products from public;
        revoke update (creation_date) on categories from public;
        revoke update (creation_date) on delivery_methods from public;
        revoke update (creation_date) on products from public;

        raise notice 'All initial setup tables have been successfully created';

    exception
        when others then
            raise notice 'Error when creating initial setup tables: %s', sqlerrm;
    end;
$$;

