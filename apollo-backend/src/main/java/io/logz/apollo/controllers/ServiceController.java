package io.logz.apollo.controllers;

import io.logz.apollo.dao.ServiceDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.models.Service;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.security.annotation.LoggedIn;

import java.util.List;

/**
 * Created by roiravhon on 12/20/16.
 */
@Controller
public class ServiceController {

    private final ServiceDao serviceDao;

    public ServiceController() {
        serviceDao = ApolloMyBatis.getDao(ServiceDao.class);
    }

    @LoggedIn
    @GET("/service")
    public List<Service> getAllServices() {
        return serviceDao.getAllServices();
    }

    @LoggedIn
    @GET("/service/{id}")
    public Service getService(int id) {
        return serviceDao.getService(id);
    }

    @LoggedIn
    @POST("/service")
    public void addDeployableVersion(String name, Req req) {

        Service newService = new Service();

        //TODO: fill more here..
        newService.setName(name);

        serviceDao.addService(newService);

        req.response().code(201);
        req.response().contentType(MediaType.APPLICATION_JSON);
        req.response().json(newService);
    }
}
