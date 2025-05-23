create index if not exists idx_users_email on users (email);
create index if not exists idx_users_login on users (login);
create index if not exists idx_users_phone_number on users (phone_number);

create index if not exists idx_products_category_id on products (category_id);
create index if not exists idx_products_delivery_methods_product_id on products_delivery_methods (product_id);
create index if not exists idx_products_delivery_methods_delivery_method_id on products_delivery_methods (delivery_method_id);

create index if not exists idx_categories_name on categories (name);
create index if not exists idx_delivery_methods_name on delivery_methods (name);