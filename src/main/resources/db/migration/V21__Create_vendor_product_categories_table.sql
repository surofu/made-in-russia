-- Категории товаров

do
$$
    begin
        begin
            create table if not exists vendor_details
            (
                id           bigserial,
                user_id      bigint       not null,
                inn          varchar(255) not null,
                company_name varchar(255) not null,

                constraint pk_vendor_details_id primary key (id),
                constraint fk_vendor_details_user_id foreign key (user_id) references users (id),
                constraint unique_vendor_details_inn unique (inn),
                constraint unique_vendor_details_company_name unique (company_name),
                constraint check_vendor_details_inn check ( length(inn) > 7 )
            );

            create index if not exists idx_vendor_details_user_id on vendor_details (user_id);

            raise notice 'Table vendor details successfully created';
        exception
            when others then
                raise notice 'Error when creating vendor details table: %s', sqlerrm;
        end;
    end
$$;