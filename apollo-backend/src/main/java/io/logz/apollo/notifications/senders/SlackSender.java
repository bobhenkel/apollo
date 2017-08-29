package io.logz.apollo.notifications.senders;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.notifications.mustache.TemplateInjector;
import io.logz.apollo.notifications.NotificationTemplateMetadata;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;


public class SlackSender implements NotificationSender{

    private static final Logger logger = LoggerFactory.getLogger(SlackSender.class);
    private static final MediaType MediaType_JSON = MediaType.parse("application/json; charset=utf-8");

    private final SlackSenderConfiguration slackSenderConfiguration;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final TemplateInjector templateInjector = new TemplateInjector();

    private static String slackBodyTemplate = "{\n" +
            "\t\"icon_url\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/b/bf/Putin-pictogram.svg/2000px-Putin-pictogram.svg.png\",\n" +
            "    \"username\": \"Apollo\",\n" +
            "    \"channel\": \"{{ channel }}\",\n" +
            "    \"attachments\": [\n" +
            "        {\n" +
            "            \"fallback\": \"Apollo deployment of {{ service-name }}@{{ environment }} is {{ phase }}\",\n" +
            "            \"color\": \"#36a64f\",\n" +
            "            \"title\": \"Apollo Deployments\",\n" +
            "            \"title_link\": \"https://apollo.internal.logz.io/#/deployments/ongoing\",\n" +
            "            \"text\": \"Deployment id {{deployment-id}} finished at {{time}}\",\n" +
            "            \"fields\": [\n" +
            "                {\n" +
            "                    \"title\": \"Environment\",\n" +
            "                    \"value\": \"{{environment}}\",\n" +
            "                    \"short\": true\n" +
            "                },\n" +
            "\t\t\t\t                {\n" +
            "                    \"title\": \"Service\",\n" +
            "                    \"value\": \"{{service}}\",\n" +
            "                    \"short\": true\n" +
            "                },                {\n" +
            "                    \"title\": \"User\",\n" +
            "                    \"value\": \"{{username}}\",\n" +
            "                    \"short\": true\n" +
            "                }\n" +
            "\t\t\t\t,                {\n" +
            "                    \"title\": \"Phase\",\n" +
            "                    \"value\": \"{{phase}}\",\n" +
            "                    \"short\": true\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    public SlackSender(String notificationJsonConfiguration) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        slackSenderConfiguration = mapper.readValue(notificationJsonConfiguration, SlackSenderConfiguration.class);
    }

    @Override
    public boolean send(NotificationTemplateMetadata notificationTemplateMetadata) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("time", DateFormat.getTimeInstance(DateFormat.SHORT).format(notificationTemplateMetadata.getLastUpdate()));
        params.put("phase", notificationTemplateMetadata.getStatus());
        params.put("service-name", notificationTemplateMetadata.getServiceName());
        params.put("environment", notificationTemplateMetadata.getEnvironmentName());
        params.put("username", notificationTemplateMetadata.getUserEmail());
        params.put("deployment-id", notificationTemplateMetadata.getDeploymentId());
        params.put("channel", slackSenderConfiguration.getChannel());

        String body = generateRequestBody(slackBodyTemplate, params);

        return sendToSlack(body);
    }

    private boolean sendToSlack(String body) {
        logger.info("Slack message body: {}", body);

        Request.Builder builder = new Request.Builder().url(slackSenderConfiguration.getWebhookUrl());
        Request request = builder.post(RequestBody.create(MediaType_JSON, body)).build();
        try {
            return sendToSlack(request);
        } catch (Exception t) {
            logger.warn("Slack message failed. Channel url: {}, Message body: {}", slackSenderConfiguration.getWebhookUrl(), body);
            return false;
        }
    }

    private boolean sendToSlack(Request request) throws Exception {
        Buffer buffer = new Buffer();
        request.body().writeTo(buffer);
        String requestBody = buffer.readUtf8();
        try(Response response = httpClient.newCall(request).execute()) {
            if (HttpStatus.isPositive(response.code())) {
                logger.info("Slack message sent successfully. Channel url: {}, Message body: {}", slackSenderConfiguration.getWebhookUrl(), requestBody);
                return true;
            } else {
                logger.warn("Failed to send a message to slack endpoint. " + request + ". request body=" + requestBody + ". response=" + response + ". response-body=" + response.body().string());
                return false;
            }
        }
    }

    private String generateRequestBody(String bodyTemplate, Map<String, Object> templateParams) {
        return templateInjector.injectToTemplate(bodyTemplate, templateParams);
    }

    public static class SlackSenderConfiguration {
        private String webhookUrl;
        private String channel;

        public SlackSenderConfiguration() {
        }

        public String getWebhookUrl() {
            return webhookUrl;
        }

        public void setWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }
}
