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

    var getDeployableVersionBasedOnSha = function (sha, serviceId) {
        return $http.get(CONFIG.appUrl + "deployable-version/sha/" + sha + "/service/" + serviceId)
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

    var serviceStatus = function (serviceId) {
        return $http.get(CONFIG.appUrl + "status/service/" + serviceId + "/");
    };

    var environmentStatus = function (environmentId) {
        return $http.get(CONFIG.appUrl + "status/environment/" + environmentId + "/");
    };

    var logsFromStatus = function (environmentId, serviceId) {
        return $http.get(CONFIG.appUrl + "status/logs/environment/" + environmentId + "/service/" + serviceId);
    };

    var restartPod = function (environmentId, podName) {
        return $http.post(CONFIG.appUrl + "k8s/pod/restart", {
            environmentId: environmentId,
            podName: podName
        });
    };

    var createService = function(name, deploymentYaml, serviceYaml) {
        return $http.post(CONFIG.appUrl + "service/", {
            name: name,
            deploymentYaml: deploymentYaml,
            serviceYaml: serviceYaml
        });
    };

    var updateService = function(id, name, deploymentYaml, serviceYaml) {
        return $http.put(CONFIG.appUrl + "service/" + id, {
            name: name,
            deploymentYaml: deploymentYaml,
            serviceYaml: serviceYaml
        });
    };

    var getLatestDeployableVersionsByServiceId = function (serviceId) {
        return $http.get(CONFIG.appUrl + "deployable-version/latest/service/" + serviceId);
    };

    var getDeployableVersionFromLatestCommitOnBranch = function (branchName, sourceDeployableVersion) {
        // Double encoding, as nginx is opening the first one
        return $http.get(CONFIG.appUrl + "deployable-version/latest/branch/" + encodeURIComponent(encodeURIComponent(branchName)) + "/repofrom/" + sourceDeployableVersion);
    };
    
    var getWebsocketExecUrl = function (environment, service, podName, containerName) {
      return CONFIG.wsUrl + "exec/pod/" + podName + "/container/" + containerName + "?environment=" + environment + "&service=" + service;
    };
    
    var getAllBlockers = function () {
        return $http.get(CONFIG.appUrl + "blocker-definition");
    };
    
    var addBlocker = function (name, environmentId, serviceId, isActive, blockerTypeName, blockerJsonConfiguration) {
        return $http.post(CONFIG.appUrl + "blocker-definition", {
            name: name,
            environmentId: environmentId,
            serviceId: serviceId,
            isActive: isActive,
            blockerTypeName: blockerTypeName,
            blockerJsonConfiguration: blockerJsonConfiguration
        });
    };

    var updateBlocker = function (id, name, environmentId, serviceId, isActive, blockerTypeName, blockerJsonConfiguration) {
        return $http.put(CONFIG.appUrl + "blocker-definition/" + id, {
            name: name,
            environmentId: environmentId,
            serviceId: serviceId,
            isActive: isActive,
            blockerTypeName: blockerTypeName,
            blockerJsonConfiguration: blockerJsonConfiguration
        });
    };

    var deleteBlocker = function (id) {
        return $http.delete(CONFIG.appUrl + "blocker-definition/" + id);
    };

    var getHawtioLink = function (environmentId, podName) {
        var port = document.location.port;
        var host = document.location.hostname;
        var scheme = document.location.protocol.split(":")[0];
        var prefix = "";
        if (port === "") {
            // If running on nginx
            prefix = "api/";
            if (document.location.protocol === "https:") {
                port = "443";
            } else {
                port = "80";
            }
        }

        return CONFIG.hawtioUrl + "jvm/connect?name=" + podName +"&host=" + host + "&port=" + port + "&scheme="+ scheme + "&path=" + prefix + "jolokia/environment/" + environmentId + "/pod/" + podName;
    };

    return {
        getAllUsers: getAllUsers,
        getAllEnvironments: getAllEnvironments,
        getAllServices: getAllServices,
        getAllDeployableVersions: getAllDeployableVersions,
        getDeployableVersionBasedOnSha: getDeployableVersionBasedOnSha,
        getLatestDeployableVersionsByServiceId: getLatestDeployableVersionsByServiceId,
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
        login: login,
        serviceStatus: serviceStatus,
        environmentStatus: environmentStatus,
        logsFromStatus: logsFromStatus,
        restartPod: restartPod,
        createService: createService,
        updateService: updateService,
        getDeployableVersionFromLatestCommitOnBranch: getDeployableVersionFromLatestCommitOnBranch,
        getWebsocketExecUrl: getWebsocketExecUrl,
        getAllBlockers: getAllBlockers,
        addBlocker: addBlocker,
        updateBlocker: updateBlocker,
        deleteBlocker: deleteBlocker,
        getHawtioLink: getHawtioLink
    };
}
