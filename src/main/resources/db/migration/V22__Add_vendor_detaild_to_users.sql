do
$$
    begin
        begin
            alter table users
                add column if not exists vendor_details_id bigint,
                add constraint fk_users_vendor_details_id foreign key (vendor_details_id) references vendor_details (id);

            create index if not exists idx_users_vendor_details_id on users (vendor_details_id);

            raise notice 'vendor_details_id successfully added to the users table';
        exception
            when duplicate_object then
                raise notice 'duplicate_object: %s', sqlerrm;
            when others then
                raise notice 'Error when adding vendor_details_id to users: %s', sqlerrm;
        end;
    end
$$;