package org.example.database;

import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.core.json.JsonObject;

public class MongoClientProvider {
    public static MongoClient createMongoClient(Vertx vertx) {
        JsonObject config = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "event-ticket-system");

        return MongoClient.createShared(vertx, config);
    }
}
