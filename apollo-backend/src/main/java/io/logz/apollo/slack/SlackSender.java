package io.logz.apollo.slack;

import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.mustache.TemplateInjector;
import io.logz.apollo.notifications.Notification;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;


public class SlackSender {

    private static final Logger logger = LoggerFactory.getLogger(SlackSender.class);
    private static final MediaType MediaType_JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient = new OkHttpClient();
    private final TemplateInjector templateInjector = new TemplateInjector();

    private static String slackBodyTemplate = "{" +
            "\"username\": \"Apollo\"," +
            "\"icon_emoji\": \":ghost:\"," +
            "\"icon_url\": \"https://slack.com/img/icons/app-57.png\"," +
            "\"channel\": \"{{ channel }}\"," +
            "\"text\": \"{{ time }}\\n{{ phase }} {{ service-name }}@{{ environment }} \\nUser: {{ username }}\\nDeployment ID: {{ deployment-id }}\"" +
            "}";
    private String slackWebHookUrl;
    private String channel;

    public SlackSender(String slackWebHookUrl, String slackChanel) {
        this.slackWebHookUrl = slackWebHookUrl;
        this.channel = slackChanel;
    }

    public boolean send(Notification notification) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("time", DateFormat.getTimeInstance(DateFormat.SHORT).format(notification.lastUpdate));
        params.put("phase", notification.status);
        params.put("service-name", notification.serviceName);
        params.put("environment", notification.environmentName);
        params.put("username", notification.userEmail);
        params.put("deployment-id", notification.deploymentId);
        params.put("channel", channel);

        String body = generateRequestBody(slackBodyTemplate, params);

        return sendToSlack(body);
    }

    private boolean sendToSlack(String body) {
        logger.info("Slack message body: {}", body);

        Request.Builder builder = new Request.Builder().url(slackWebHookUrl);
        Request request = builder.post(RequestBody.create(MediaType_JSON, body)).build();
        try {
            return sendToSlack(request);
        } catch (Throwable t) {
            logger.warn("Slack message failed. Channel url: {}, Message body: {}", slackWebHookUrl, body);
            return false;
        }
    }

    private boolean sendToSlack(Request request) throws Exception {
        Buffer buffer = new Buffer();
        request.body().writeTo(buffer);
        String requestBody = buffer.readUtf8();
        try(Response response = httpClient.newCall(request).execute()) {
            if (HttpStatus.isPositive(response.code())) {
                logger.info("Slack message sent successfully. Channel url: {}, Message body: {}", slackWebHookUrl, requestBody);
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
}
