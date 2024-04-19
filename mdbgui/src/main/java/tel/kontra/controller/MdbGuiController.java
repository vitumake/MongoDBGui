package tel.kontra.controller;

import java.util.UUID;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import tel.kontra.database.DbConn;
import tel.kontra.model.Person;
import tel.kontra.view.Mdbgui;

public class MdbGuiController {
    
    @FXML
    private TextField uriField;

    @FXML
    private ComboBox<String> idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField cityField;

    @FXML
    private Text connIndicator;

    @FXML
    private ComboBox<String> collectionField;

    private MongoClient client;

    private DbConn dbConn;

    @FXML
    private void onConnect() {

        // Check if client is already connected
        if (client != null) {
            Mdbgui.alert("Client already conected!", "Connection error");
            return;
        }

        // Check if uri or collection field is empty
        if (uriField.getText().isEmpty()) {
            Mdbgui.alert("URI or collection field is empty!", "Connection error");
            return;
        }

        try {
            String uri = uriField.getText();

            // Logging
            System.out.println("Connecting to " + uri);
            client = MongoClients.create(uri);

            // Get connection address
            String address = client.getClusterDescription().getServerDescriptions().get(0).getAddress().toString();
            
            connIndicator.setText("Connected to " + address);
            
            // Get datbase from client
            String database = client.listDatabaseNames().first();

            // Log connection details
            System.out.println("Connected to database: " + database);

            // Create new DbConn object
            dbConn = new DbConn(client, database);

            // Populate collection field in GUI
            collectionField.getItems().addAll(dbConn.getCollections());
            collectionField.setValue(collectionField.getItems().get(0));

            // Set collection
            dbConn.setCollection(collectionField.getValue());

        } catch (Exception e) {
            System.out.println("Connection failed");
            e.printStackTrace();
            connIndicator.setText("Connection failed");
        }
    }

    @FXML
    private void onRead() {

        String id;

        // Check if client is null
        if (client == null) {
            Mdbgui.alert("Client not connected!", "Crud error");
            return;
        }

        // Check if id field is empty
        if (idField.getValue() == null) {
            id = null;
        } else {
            id = idField.getValue();
        }

        // Read person from database
        System.out.println("Reading person with id: " + id);
        
        // Update fields
        updateFields(dbConn.read(id));
    }

    @FXML
    private void onUpdate() {
        Person person = Person.builder()
            .id(idField.getValue())
            .name(nameField.getText())
            .age(Integer.parseInt(ageField.getText()))
            .city(cityField.getText())
            .build();
        
        try {
            dbConn.update(person);
        } catch (Exception e) {
            e.printStackTrace();
            Mdbgui.alert(e.getMessage(), "Update error");
        }
    }

    @FXML
    private void onDelete() {
        try {
            dbConn.delete(idField.getValue());
            
            // Update id field
            idField.getItems().clear();
            dbConn.getIds().forEach(id -> idField.getItems().add(id));

        } catch (Exception e) {
            e.printStackTrace();
            Mdbgui.alert(e.getMessage(), "Delete error");
        }
    }

    @FXML
    private void onAdd() {
        Person person = Person.builder()
            .id(idField.getValue())
            .name(nameField.getText())
            .age(Integer.parseInt(ageField.getText()))
            .city(cityField.getText())
            .build();
        
        try {
            dbConn.write(person);
            // Update id field
            idField.getItems().add(idField.getValue());

        } catch (Exception e) {
            e.printStackTrace();
            Mdbgui.alert(e.getMessage(), "Write error");
        }
    }

    @FXML
    private void generateId() {
        
        // Generate random text id
        UUID uuid = UUID.randomUUID();
        idField.setValue(uuid.toString());
    }

    @FXML
    private void onDisconnect() {
        if (client != null) {
            client.close();
            client = null;
            connIndicator.setText("Disconnected");

            // Clear fields
            idField.getItems().clear();
            nameField.clear();
            ageField.clear();
            cityField.clear();

            // Clear collection field
            collectionField.getItems().clear();
        }
    }

    @FXML
    private void onCollectionChange() {
        dbConn.setCollection(collectionField.getValue());

        // Update id field
        idField.getItems().clear();
        dbConn.getIds().forEach(id -> idField.getItems().add(id));
    }

    private void updateFields(Person person) {
        idField.setValue(person.getId());
        nameField.setText(person.getName());
        ageField.setText(String.valueOf(person.getAge()));
        cityField.setText(person.getCity());
    }

    public static void main(String args[]) {
        Mdbgui.launch(Mdbgui.class, args);
    }
}
