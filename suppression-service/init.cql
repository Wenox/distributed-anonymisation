CREATE KEYSPACE IF NOT EXISTS suppression_keyspace
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': '1'
        };