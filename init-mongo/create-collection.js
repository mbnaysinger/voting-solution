db = db.getSiblingDB('voting-solution');

if (!db.getCollectionNames().includes('agenda-cycle')) {
  db.createCollection('agenda-cycle');
  print('Collection "agenda-cycle" created successfully in database "voting-solution"');
} else {
  print('Collection "agenda-cycle" already exists in database "voting-solution"');
}
