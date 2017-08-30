package io.logz.apollo.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.Reader;

public class WebsocketWriter {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketWriter.class);

    public static void readCharsFromStreamToSession(InputStream inputStream, Session session) {
        try (Reader reader = new InputStreamReader(inputStream)){

            RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
            while (true) {

                if (Thread.interrupted()) {
                    logger.info("Interrupted while starting to write to websocket");
                    Thread.currentThread().interrupt();
                    break;
                }

                try {
                    char character = (char) reader.read();
                    basicRemote.sendObject(character);
                } catch (InterruptedIOException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Interrupted while writing to websocket", e);
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

    public static void readLinesFromStreamToSession(InputStream inputStream, Session session) {
        try (Reader reader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(reader)){

            RemoteEndpoint.Basic basicRemote = session.getBasicRemote();
            while (true) {

                if (Thread.interrupted()) {
                    logger.info("Interrupted while starting to write to websocket");
                    Thread.currentThread().interrupt();
                    break;
                }

                try {
                    String line = bufferedReader.readLine();
                    basicRemote.sendObject(line + "\n\r");
                } catch (InterruptedIOException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Interrupted while writing to websocket", e);
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
