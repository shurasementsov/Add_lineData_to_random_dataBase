/*postgres=# */ SELECT information_schema.key_column_usage.constraint_name as constraint_name, information_schema.key_column_usage.table_name as source_table, information_schema.key_column_usage.column_name as column_name, information_schema.constraint_column_usage.table_name as referenced_table 
/*postgres-# */ FROM information_schema.key_column_usage, information_schema.constraint_column_usage 
/*postgres-# */ WHERE information_schema.constraint_column_usage.constraint_name = information_schema.key_column_usage.constraint_name 
/*postgres-# */ AND information_schema.key_column_usage.table_schema = 'fifthattempt' 
/*postgres-# */ AND information_schema.key_column_usage.table_name = 'reys'                                                         
/*postgres-# */ GROUP BY information_schema.key_column_usage.constraint_name, information_schema.key_column_usage.table_name, information_schema.key_column_usage.column_name, information_schema.constraint_column_usage.table_name;
 constraint_name | source_table | column_name | referenced_table 
-----------------+--------------+-------------+------------------
 reys_aerin_fk   | reys         | aero_in_id  | aerop
 reys_aero_fk    | reys         | aero_id     | aero
 reys_aerout_fk  | reys         | aero_out_id | aerop
 reys_comp_fk    | reys         | a_comp_id   | a_comp
 reys_prim       | reys         | reys_id     | reys
(5 rows)
