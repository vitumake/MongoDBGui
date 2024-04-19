package tel.kontra.database;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

import tel.kontra.model.Person;

public class DbConn {
    
    private MongoClient client;
    private MongoCollection<Document> collection;
    private String database;

    public DbConn(MongoClient client,String database) {
        this.client = client;
        this.database = database;
    }
    
    public void setCollection(String collectionName) {
        collection = client.getDatabase(database).getCollection(collectionName);
    }

    public List<String> getCollections() {
        List<String> collections = new ArrayList<>();
        for (String collectionName : client.getDatabase(database).listCollectionNames()) {
            collections.add(collectionName);
        }
        return collections;
    }

    public void close() {
        client.close();
    }

    public Person read(String id) {

        // If id is null get first document
        if (id == null) {
            Document doc = collection.find().first();
            return Person.builder()
                .id(doc.getString("_id"))
                .name(doc.getString("name"))
                .age(doc.getInteger("age"))
                .city(doc.getString("city"))
                .build();
        } else {
            Document doc = collection.find(new Document("_id", id)).first();
            return Person.builder()
                .id(doc.getString("_id"))
                .name(doc.getString("name"))
                .age(doc.getInteger("age"))
                .city(doc.getString("city"))
                .build();
        }
    }

    public void write(Person person) {
        Document doc = new Document("_id", person.getId())
            .append("name", person.getName())
            .append("age", person.getAge())
            .append("city", person.getCity());
        collection.insertOne(doc);
    }

    public void delete(String id) {
        collection.deleteOne(new Document("_id", id));
    }

    public void update(Person person) {
        Document doc = new Document("_id", person.getId())
            .append("name", person.getName())
            .append("age", person.getAge())
            .append("city", person.getCity());
        collection.replaceOne(new Document("_id", person.getId()), doc);
    }

    public List<String> getIds() {
        List<String> ids = new ArrayList<>();
        for (Document doc : collection.find()) {
            ids.add(doc.getString("_id"));
        }
        return ids;
    }
}
