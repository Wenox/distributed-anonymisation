db.createUser(
    {
        user: "root",
        pwd: "example",
        roles: [
            {
                role: "readWrite",
                db: "templates_db"
            }
        ]
    }
);
db.createCollection('templates1')
db.createCollection('templates2')
db.createCollection('templates3')
