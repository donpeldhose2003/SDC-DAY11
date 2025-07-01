package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.database.MongoClientProvider;
import org.example.handlers.UserHandler;
import org.example.handlers.EventHandler; // ✅ Important: Make sure this import is added

public class Main extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Main());
    }

    @Override
    public void start() {
        MongoClient mongoClient = MongoClientProvider.createMongoClient(vertx);
        UserHandler userHandler = new UserHandler(mongoClient);
        EventHandler eventHandler = new EventHandler(mongoClient); // ✅ Make sure this class exists

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        // Routes
        router.post("/api/register").handler(userHandler::registerUser);
        router.post("/api/login").handler(userHandler::loginUser);
        router.get("/api/events").handler(eventHandler::listEvents);
        router.post("/api/events/:eventId/book").handler(eventHandler::bookToken);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080)
                .onSuccess(server -> System.out.println("Server started on port " + server.actualPort()));
    }
}
