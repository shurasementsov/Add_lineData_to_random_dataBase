/*postgres=# */ alter table fifthattempt.sector drop constraint sector_place_fk;
ALTER TABLE
/*postgres=# */ commit;
WARNING:  there is no transaction in progress
COMMIT
/*postgres=# */ alter table fifthattempt.place drop constraint place_prim;
ALTER TABLE
/*postgres=# */ commit;
WARNING:  there is no transaction in progress
COMMIT
/*postgres=# */ alter table fifthattempt.place add constraint place_prim primary key (aero_id);
ALTER TABLE
/*postgres=# */ commit;
WARNING:  there is no transaction in progress
COMMIT
/*postgres=# */ alter table fifthattempt.sector add constraint sector_place_fk foreign key (place_id) references fifthattempt.place;
ALTER TABLE
/*postgres=# */ commit;
WARNING:  there is no transaction in progress
COMMIT
