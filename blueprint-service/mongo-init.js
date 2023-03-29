db.createUser(
    {
        user: "root",
        pwd: "example",
        roles: [
            {
                role: "readWrite",
                db: "blueprints_db"
            }
        ]
    }
);
db.createCollection('blueprints1')
db.createCollection('blueprints2')
db.createCollection('blueprints3')
