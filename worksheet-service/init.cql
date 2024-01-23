CREATE KEYSPACE IF NOT EXISTS suppression_keyspace
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': '1'
        };

USE suppression_keyspace;

CREATE TABLE IF NOT EXISTS operations (
                                          task_id text,
                                          worksheet_id text,
                                          table_name text,
                                          column_name text,
                                          operation_type text,
                                          task_status text,
                                          column_type text,
                                          primary_key text,
                                          primary_key_type text,
                                          settings text,
                                          PRIMARY KEY ((worksheet_id), task_id)
);

CREATE INDEX IF NOT EXISTS operations_by_task_id ON operations (task_id);

