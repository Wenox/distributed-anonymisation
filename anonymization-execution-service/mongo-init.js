db.createUser(
    {
        user: "root",
        pwd: "example",
        roles: [
            {
                role: "readWrite",
                db: "anonymization_execution_db"
            }
        ]
    }
);