alter domain phone_number_domain
    drop constraint phone_number_domain_check;

alter domain phone_number_domain
    add check ( length(VALUE) >= 7 );