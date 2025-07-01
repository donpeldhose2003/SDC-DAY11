package org.example.handlers;

import jakarta.mail.internet.*;
import jakarta.mail.MessagingException;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.example.utils.EmailUtil;
import jakarta.mail.*;
import java.security.SecureRandom;
import java.util.Base64;

public class EventHandler {
    private final MongoClient mongoClient;

    public EventHandler(MongoClient client) {
        this.mongoClient = client;
    }

    // List all events
    public void listEvents(RoutingContext context) {
        mongoClient.find("events", new JsonObject(), res -> {
            if (res.succeeded()) {
                context.response()
                        .putHeader("Content-Type", "application/json")
                        .end(res.result().toString());  // List of events
            } else {
                context.response().setStatusCode(500).end("Failed to fetch events.");
            }
        });
    }


    // Book a token
    public void bookToken(RoutingContext context) {

        String eventId = context.pathParam("eventId");
        JsonObject body = context.body().asJsonObject();
        String userEmail = body.getString("email");

        if (eventId == null || userEmail == null) {
            context.response().setStatusCode(400).end("Missing eventId or email.");
            return;
        }

        JsonObject query = new JsonObject().put("_id", eventId);

        mongoClient.findOne("events", query, null, res -> {
            if (res.succeeded() && res.result() != null) {
                JsonObject event = res.result();
                int available = event.getInteger("availableTokens");

                if (available <= 0) {
                    context.response().setStatusCode(400).end("No tokens left.");
                    return;
                }

                // ✅ Generate token
                String tokenCode = generateToken();

                // ✅ Save to bookings collection
                JsonObject booking = new JsonObject()
                        .put("eventId", eventId)
                        .put("email", userEmail)
                        .put("token", tokenCode);

                mongoClient.save("bookings", booking, bookingRes -> {
                    if (bookingRes.succeeded()) {
                        // ✅ Decrement availableTokens
                        JsonObject update = new JsonObject()
                                .put("$inc", new JsonObject().put("availableTokens", -1));
                        mongoClient.updateCollection("events", query, update, updateRes -> {});

                        // ✅ Send token via email
                        try {
                            EmailUtil.sendEmail(userEmail, "Your Event Token",
                                    "Thanks for booking!\nYour token is: " + tokenCode);
                            context.response().setStatusCode(200).end("Booking confirmed. Token emailed.");
                        } catch (MessagingException e) {
                            context.response().setStatusCode(500).end("Booking saved but email failed.");
                        }
                    } else {
                        context.response().setStatusCode(500).end("Booking failed.");
                    }
                });
            } else {
                context.response().setStatusCode(404).end("Event not found.");
            }
        });
    }

    // Random alphanumeric token
    private String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[6];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}
