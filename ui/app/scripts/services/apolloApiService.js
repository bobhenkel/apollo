'use strict';
angular
    .module('apollo')
    .service('apolloApiService', [
        '$q', '$http',
        ApiService
    ]);


function ApiService($q, $http){

    var getAllUsers = function() {
        return $http.get(CONFIG.appUrl + 'user/');
    };

    var getAllEnvironments = function() {
        return $http.get(CONFIG.appUrl + 'environment/');
    };

    var getAllServices = function() {
        return $http.get(CONFIG.appUrl + 'service/');
    };

    var getAllDeployableVersions = function() {
        return $http.get(CONFIG.appUrl + 'deployable-version/');
    };

    var getAllRunningDeployments = function() {
        return $http.get(CONFIG.appUrl + 'running-deployments/');
    };

    var getRunningAndJustFinishedDeployments = function() {
        return $http.get(CONFIG.appUrl + 'running-and-just-finished-deployments/');
    };

    var getAllDeployments = function() {
        return $http.get(CONFIG.appUrl + 'deployment/');
    };

    var getService = function(serviceId) {
        return $http.get(CONFIG.appUrl + 'service/' + serviceId + "/");
    };

    var getEnvironment = function(environmentId) {
        return $http.get(CONFIG.appUrl + 'environment/' + environmentId + "/");
    };

    var getUser = function(userId) {
        return $http.get(CONFIG.appUrl + 'user/' + userId + "/");
    };

    var getDeployableVersion = function(deployableVerisonId) {
        return $http.get(CONFIG.appUrl + 'deployable-version/' + deployableVerisonId + "/");
    };

    var getLatestDeployments = function() {
        return $http.get(CONFIG.appUrl + 'latest-deployments/');
    };

    var createNewDeployment = function(deployableVersionId, deployedService, deployedEnvironment) {
        return $http.post(CONFIG.appUrl + "deployment/", {
            serviceId: deployedService,
            environmentId: deployedEnvironment,
            deployableVersionId: deployableVersionId
        })
    };

    var revertDeployment = function(deploymentId) {
        return $http.delete(CONFIG.appUrl + "deployment/" + deploymentId + "/");
    };

    var getDeploymentLogs = function(deploymentId) {
        return $http.get(CONFIG.appUrl + "deployment/" + deploymentId + "/logs/");
    };


    var matchLabelToDeploymentStatus = function(deploymentStatus) {
        var statusToLabel = {
            "PENDING": "label-default",
            "PENDING_CANCELLATION": "label-default",
            "STARTED": "label-primary",
            "CANCELING": "label-warning",
            "DONE": "label-success",
            "CANCELED": "label-danger"
        };

        return statusToLabel[deploymentStatus]
    };

    var isRevertDisabledBasedOnStatus = function(deploymentStatus) {
        return deploymentStatus == "DONE" || deploymentStatus == "CANCELED";
    };

    var signup = function(userEmail, firstName, lastName, password) {
        return $http.post(CONFIG.appUrl + "signup/", {
            userEmail: userEmail,
            firstName: firstName,
            lastName: lastName,
            password: password
        })
    };

    var login = function(email, password) {
        return $http.post(CONFIG.appUrl + "_login/", {
            username: email,
            password: password
        })
    };

    return {
        getAllUsers: getAllUsers,
        getAllEnvironments: getAllEnvironments,
        getAllServices: getAllServices,
        getAllDeployableVersions: getAllDeployableVersions,
        createNewDeployment: createNewDeployment,
        getAllRunningDeployments: getAllRunningDeployments,
        getRunningAndJustFinishedDeployments: getRunningAndJustFinishedDeployments,
        getAllDeployments: getAllDeployments,
        getService: getService,
        getEnvironment: getEnvironment,
        getUser: getUser,
        getDeployableVersion: getDeployableVersion,
        getLatestDeployments: getLatestDeployments,
        revertDeployment: revertDeployment,
        getDeploymentLogs: getDeploymentLogs,
        matchLabelToDeploymentStatus: matchLabelToDeploymentStatus,
        isRevertDisabledBasedOnStatus: isRevertDisabledBasedOnStatus,
        signup: signup,
        login: login
    };
}
