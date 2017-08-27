package io.logz.apollo.controllers;

import io.logz.apollo.common.ControllerCommon;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.dao.EnvironmentDao;
import io.logz.apollo.kubernetes.KubernetesHandlerStore;
import io.logz.apollo.models.Environment;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.DELETE;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.HEAD;
import org.rapidoid.annotation.OPTIONS;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.PUT;
import org.rapidoid.http.Req;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Controller
public class JolokiaController {

    private final KubernetesHandlerStore kubernetesHandlerStore;
    private final EnvironmentDao environmentDao;

    @Inject
    public JolokiaController(KubernetesHandlerStore kubernetesHandlerStore, EnvironmentDao environmentDao) {
        this.kubernetesHandlerStore = requireNonNull(kubernetesHandlerStore);
        this.environmentDao = requireNonNull(environmentDao);
    }

    @GET("/jolokia/environment/{environmentId}/pod/{podName}/{path:(.*|^$)}")
    public void jolokiaProxyGet(int environmentId, String podName, String path, Req req) {
        genericJolokiaProxy(environmentId, podName, path, req);
    }

    @GET("/jolokia/environment/{environmentId}/pod/{podName}")
    public void jolokiaProxyGet(int environmentId, String podName,Req req) {
        genericJolokiaProxy(environmentId, podName, "", req);
    }

    @OPTIONS("/jolokia/environment/{environmentId}/pod/{podName}/{path:(.*|^$)}")
    public void jolokiaProxyOptions(int environmentId, String podName, String path, Req req) {
        genericJolokiaProxy(environmentId, podName, path, req);
    }

    @OPTIONS("/jolokia/environment/{environmentId}/pod/{podName}")
    public void jolokiaProxyOptions(int environmentId, String podName,Req req) {
        genericJolokiaProxy(environmentId, podName, "", req);
    }

    @HEAD("/jolokia/environment/{environmentId}/pod/{podName}/{path:(.*|^$)}")
    public void jolokiaProxyHead(int environmentId, String podName, String path, Req req) {
        genericJolokiaProxy(environmentId, podName, path, req);
    }

    @HEAD("/jolokia/environment/{environmentId}/pod/{podName}")
    public void jolokiaProxyHead(int environmentId, String podName,Req req) {
        genericJolokiaProxy(environmentId, podName, "", req);
    }

    @POST("/jolokia/environment/{environmentId}/pod/{podName}/{path:(.*|^$)}")
    public void jolokiaProxyPost(int environmentId, String podName, String path, Req req) {
        genericJolokiaProxy(environmentId, podName, path, req);
    }

    @POST("/jolokia/environment/{environmentId}/pod/{podName}")
    public void jolokiaProxyPost(int environmentId, String podName, Req req) {
        genericJolokiaProxy(environmentId, podName, "", req);
    }

    @PUT("/jolokia/environment/{environmentId}/pod/{podName}/{path:(.*|^$)}")
    public void jolokiaProxyPut(int environmentId, String podName, String path, Req req) {
        genericJolokiaProxy(environmentId, podName, path, req);
    }

    @DELETE("/jolokia/environment/{environmentId}/pod/{podName}/{path:(.*|^$)}")
    public void jolokiaProxyDelete(int environmentId, String podName, String path, Req req) {
        genericJolokiaProxy(environmentId, podName, path, req);
    }

    private void genericJolokiaProxy(int environmentId, String podName, String path, Req req) {
        Environment environment = environmentDao.getEnvironment(environmentId);

        if (environment == null) {
            ControllerCommon.assignJsonResponseToReq(req, HttpStatus.BAD_REQUEST, "Environment " + environmentId + " does not exists");
            return;
        }

        Optional<Response> response = kubernetesHandlerStore.getOrCreateKubernetesHandler(environment).proxyJolokia(podName, path, req);

        if (response.isPresent()) {
            try (ResponseBody body = response.get().body()) {
                ControllerCommon.assignJsonBytesToReq(req, response.get().code(), body.bytes());
            } catch (IOException e) {
                ControllerCommon.assignJsonResponseToReq(req, HttpStatus.INTERNAL_SERVER_ERROR, "Could not read response from jolokia");
            }
        } else {
            ControllerCommon.assignJsonResponseToReq(req, HttpStatus.BAD_REQUEST, "Could not find jolokia");
        }
    }
}
