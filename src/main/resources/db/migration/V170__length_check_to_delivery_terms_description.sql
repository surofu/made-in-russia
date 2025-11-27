alter table delivery_terms
    add check ( length(description) <= 1000 );