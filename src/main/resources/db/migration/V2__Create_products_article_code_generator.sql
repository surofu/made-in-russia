do
$$
    begin
        begin
            create or replace function generate_article_code()
                returns text as
            $text$
            declare
                letters text := 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
                numbers text := '0123456789';
                result  text := '';
                counter int  := 0;
            begin
                loop
                    result := result || substr(letters, (1 + floor(random() * length(letters)))::int, 1);
                    counter := counter + 1;
                    exit when counter >= 4;
                end loop;

                result := result || '-';

                counter := 0;
                loop
                    result := result || substr(numbers, (1 + floor(random() * length(numbers)))::int, 1);
                    counter := counter + 1;
                    exit when counter >= 4;
                end loop;

                return result;
            end;
            $text$ language plpgsql;

            create or replace function generate_unique_product_article_code()
                returns text as
            $text$
            declare
                new_code    text;
                code_exists boolean;
            begin
                loop
                    new_code = generate_article_code();

                    select exists(select 1 from products p where p.article_code = new_code) into code_exists;

                    exit when not code_exists;
                end loop;

                return new_code;
            end;
            $text$ language plpgsql;

            create or replace function set_article_code()
                returns trigger as
            $trigger$
            begin
                if new.article_code is null then
                    new.article_code := generate_unique_product_article_code();
                end if;

                return new;
            end;
            $trigger$ language plpgsql;

            create or replace trigger trigger_set_product_article_code
                before insert
                on products
                for each row
            execute function set_article_code();
        end;

        raise notice 'All products article generator functions and triggers have been successfully created';

    exception
        when others then
            raise notice 'Error when creating products article generator functions and triggers';
    end
$$;