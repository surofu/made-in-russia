do
$$
    begin
        create type user_role as enum ('ROLE_ADMIN', 'ROLE_USER', 'ROLE_VENDOR');

    exception
        when duplicate_object then
            raise notice 'User role enum already exists';
            null;
    end;
$$;