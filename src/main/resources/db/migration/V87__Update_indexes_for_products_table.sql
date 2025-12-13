drop index if exists idx_products_article_code;
create unique index idx_products_article_code on products (article_code);
