create table if not exists vendor_faq
(
    id                     bigserial primary key,
    vendor_details_id      bigint      not null,
    question               text        not null,
    answer                 text        not null,
    creation_date          timestamptz not null default now(),
    last_modification_date timestamptz not null default now(),

    constraint fk_vendor_faq_vendor_details_id foreign key (vendor_details_id) references vendor_details (id),
    constraint check_vendor_faq_question check ( length(question) < 20000 ),
    constraint check_vendor_faq_answer check ( length(answer) < 20000 )
);

create index if not exists idx_vendor_faq_vendor_details_id on vendor_faq (vendor_details_id);

revoke update (creation_date) on vendor_faq from public;