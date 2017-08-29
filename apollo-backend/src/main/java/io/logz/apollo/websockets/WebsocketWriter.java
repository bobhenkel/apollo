package io.logz.apollo.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Reader;

public class WebsocketWriter {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketWriter.class);

    public static void readFromStreamToSession(InputStream inputStream, Session session) {
        try {
            Reader reader = new InputStreamReader(inputStream);
            while (!Thread.interrupted()) {
                try {
                    char character = (char) reader.read();
                    session.getBasicRemote().sendObject(character);
                } catch (InterruptedIOException e) {
                    break;
                } catch (IOException e) {
                    if (!Thread.interrupted()) {
                        logger.warn("Got IOException while writing to websocket, bailing..", e);
                        break;
                    } else {
                        Thread.currentThread().interrupt();
                    }
                } catch (Exception e) {
                    logger.warn("Got exception while reading and sending line to websocket, continuing.. ", e);
                }
            }
        } catch (Exception e) {
            logger.error("Got unhandled exception while reading from input stream, swallowing", e);
        }
    }
}
