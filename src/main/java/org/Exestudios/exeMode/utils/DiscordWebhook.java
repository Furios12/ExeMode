package org.Exestudios.exeMode.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscordWebhook {
    private final String webhookUrl;
    private final Logger logger;
    
    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.logger = Logger.getLogger("ExeMode");
    }

    public void sendWebhook(String title, String description, int color) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
            

            title = escapeJsonString(title);
            description = escapeJsonString(description);
            
            String jsonBody = String.format("""
                {
                    "embeds": [
                        {
                            "title": "%s",
                            "description": "%s",
                            "color": %d,
                            "timestamp": "%s"
                        }
                    ]
                }""", title, description, color, timestamp);

            URI uri = URI.create(webhookUrl);
            HttpsURLConnection connection = (HttpsURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                logger.warning("Discord webhook ha restituito il codice: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Errore nell'invio del webhook Discord", e);
        }
    }

    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}