drop type if exists media_type;

do
$$
    begin
        create type media_type as enum ('IMAGE', 'VIDEO');
    exception
        when duplicate_object then null;
    end;
$$;