alter table delivery_terms
    add column name_translations        jsonb,
    add column description_translations jsonb;