function createDbAndUser(db, user, pwd) {

    var createdDb = db.getSiblingDB(db);

    createdDb.createCollection('initCollection');

    createdDb.createUser({
        user: user,
        pwd: pwd,
        roles: [{ role: 'readWrite', db: db }]
    });
}


console.log("Initialising Mongodb databases and users...")

createDbAndUser('BLUEPRINTS_DB', 'BLUEPRINTS_USER', 'BLUEPRINTS_PASSWORD');
createDbAndUser('RESTORATIONS_DB', 'RESTORATIONS_USER', 'RESTORATIONS_PASSWORD');
createDbAndUser('METADATA_DB', 'METADATA_USER', 'METADATA_PASSWORD');
createDbAndUser('WORKSHEETS_DB', 'WORKSHEETS_USER', 'WORKSHEETS_PASSWORD');
createDbAndUser('ANONYMISATION_EXECUTION_DB', 'ANONYMISATION_EXECUTION_USER', 'ANONYMISATION_EXECUTION_PASSWORD');
createDbAndUser('ANONYMISATION_SAGA_DB', 'ANONYMISATION_SAGA_USER', 'ANONYMISATION_SAGA_PASSWORD');

console.log("Successfully initialised all Mongodb databases and users.")