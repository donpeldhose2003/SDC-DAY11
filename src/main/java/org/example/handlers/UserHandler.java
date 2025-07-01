package org.example.handlers;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.RoutingContext;
import org.example.utils.EmailUtil;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Random;

import jakarta.mail.MessagingException;

public class UserHandler {
    private final MongoClient mongoClient;

    public UserHandler(MongoClient client) {
        this.mongoClient = client;
    }

    // Generate a random password of given length
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Hash the password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    // Register user
    public void registerUser(RoutingContext context) {
        JsonObject body = context.body().asJsonObject();
        String name = body.getString("name");
        String email = body.getString("email");

        if (name == null || email == null) {
            context.response().setStatusCode(400).end("Missing name or email.");
            return;
        }

        String password = generateRandomPassword(8);
        String hashedPassword = hashPassword(password);

        JsonObject user = new JsonObject()
                .put("name", name)
                .put("email", email)
                .put("password", hashedPassword);

        mongoClient.save("users", user, res -> {
            if (res.succeeded()) {
                try {
                    EmailUtil.sendEmail(
                            email,
                            "Welcome to Event Ticket System",
                            "Hi " + name + ",\n\nYour account has been created.\nYour login password is: " + password + "\n\nPlease keep it safe."
                    );
                } catch (MessagingException e) {
                    context.response().setStatusCode(500).end("User created but email failed to send.");
                    return;
                }
                context.response().setStatusCode(201).end("User registered successfully and password sent via email.");
            } else {
                context.response().setStatusCode(500).end("Registration failed: " + res.cause().getMessage());
            }
        });
    }

    // User login
    public void loginUser(RoutingContext context) {
        JsonObject body = context.body().asJsonObject();
        String email = body.getString("email");
        String password = body.getString("password");

        if (email == null || password == null) {
            context.response().setStatusCode(400).end("Missing email or password.");
            return;
        }

        String hashedPassword = hashPassword(password);

        JsonObject query = new JsonObject()
                .put("email", email)
                .put("password", hashedPassword);

        mongoClient.findOne("users", query, null, res -> {
            if (res.succeeded()) {
                JsonObject user = res.result();
                if (user != null) {
                    context.response().setStatusCode(200).end("Login successful.");
                } else {
                    context.response().setStatusCode(401).end("Invalid email or password.");
                }
            } else {
                context.response().setStatusCode(500).end("Login failed.");
            }
        });
    }
}
