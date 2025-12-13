alter table vendor_details
    add column description text check ( length(description) <= 20000 ),
    add column site varchar(255);