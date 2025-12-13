do
$$
    begin
        begin
            alter table products
                alter column article_code type article_code_domain,
                alter column article_code set default generate_unique_product_article_code(),
                alter column article_code set not null,
                add constraint unique_article_code unique (article_code);

            raise notice 'All products article code attributes successfully altered';

        exception
            when others then
                raise notice 'Error when alter products article code: %s', sqlerrm;
        end;
    end
$$;