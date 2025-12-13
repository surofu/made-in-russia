do
$$
    begin
        create type media_type as enum ('IMAGE', 'VIDEO');
    exception
        when duplicate_object then
            raise notice 'Media type enum already exists';
            null;
    end;
$$;