SELECT information_schema.key_column_usage.constraint_name as constraint_name, information_schema.key_column_usage.table_name as source_table, information_schema.key_column_usage.column_name as column_name, information_schema.constraint_column_usage.table_name as referenced_table
FROM information_schema.key_column_usage, information_schema.constraint_column_usage
WHERE information_schema.constraint_column_usage.constraint_name = information_schema.key_column_usage.constraint_name
AND information_schema.key_column_usage.table_schema = 'fifthattempt'
AND information_schema.key_column_usage.table_name = 'place'
AND information_schema.key_column_usage.column_name = 'aero_id'
GROUP BY information_schema.key_column_usage.constraint_name, information_schema.key_column_usage.table_name, information_schema.key_column_usage.column_name, information_schema.constraint_column_usage.table_name;
 constraint_name | source_table | column_name | referenced_table 
-----------------+--------------+-------------+------------------
 place_aero_fk   | place        | aero_id     | aero
 place_prim      | place        | aero_id     | place
(2 rows)
