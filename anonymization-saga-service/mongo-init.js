db.createUser(
    {
        user: "root",
        pwd: "example",
        roles: [
            {
                role: "readWrite",
                db: "anonymization_saga_db"
            }
        ]
    }
);