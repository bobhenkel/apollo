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
    }

    var createNewDeployment = function(targetVersion, deployableVersionId, deployedService, deployedEnvironment) {

        return $http.post(CONFIG.appUrl + "deployment/", {

            target_version: targetVersion,
            deployed_service: deployedService,
            deployed_environment: deployedEnvironment,
            deployable_version: deployableVersionId
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
            "pending": "label-default",
            "restart": "label-primary",
            "scale": "label-primary",
            "reverting": "label-warning",
            "done-success": "label-success",
            "done-failed": "label-danger"
        };

        return statusToLabel[deploymentStatus]
    };

    var isRevertDisabledBasedOnStatus = function(deploymentStatus) {
        if (deploymentStatus == "done-success" || deploymentStatus == "done-failed") {
            return true;
        }
        return false;
    };

    var signup = function(email, first_name, last_name, password) {
        return $http.post(CONFIG.appUrl + "_signup/", {
            userEmail: emailAddress,
            firstName: first_name,
            lastName: last_name,
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
