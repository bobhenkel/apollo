'use strict';
angular
    .module('apollo')
    .service('apolloApiService', [
        '$q', '$http',
        ApiService
    ]);

function ApiService($q, $http){

    var getAllUsers = function() {
        return $http.get(CONFIG.appUrl + 'user', {

        });
    };

    var getAllEnvironments = function() {
        return $http.get(CONFIG.appUrl + 'environment', {

        });
    };

    var getAllServices = function() {
        return $http.get(CONFIG.appUrl + 'service', {

        });
    };

    var getAllDeployableVersions = function() {
            return $http.get(CONFIG.appUrl + 'deployable-version/', {

            });
    };

    var getAllRunningDeployments = function() {

        return $http.get(CONFIG.appUrl + 'running-deployments', {

        });
    };

    var getService = function(serviceId) {
        return $http.get(CONFIG.appUrl + 'service/' + serviceId, {

        });
    };

    var getEnvironment = function(environmentId) {
        return $http.get(CONFIG.appUrl + 'environment/' + environmentId, {

        });
    };

    var getUser = function(userId) {
        return $http.get(CONFIG.appUrl + 'user/' + userId, {

        });
    };

    var createNewDeployment = function(targetVersion, deployedService, deployedEnvironment) {

        return $http.post(CONFIG.appUrl + "deployment/", {

            target_version: targetVersion,
            deployed_service: deployedService,
            deployed_environment: deployedEnvironment,
            initiated_by: 2  // TODO: give the correct user id from signin
        })
    };

    var revertDeployment = function(deploymentId) {

        return $http.delete(CONFIG.appUrl + "deployment/" + deploymentId,{

        });
    };

    var getDeploymentLogs = function(deploymentId) {

        return $http.get(CONFIG.appUrl + "deployment/" + deploymentId + "/logs" ,{

        });
    };


    var matchLabelToDeploymentStatus = function(deploymentStatus) {

        var statusToLable = {

            "pending": "label-default",
            "restart": "",
            "scale": "",
            "reverting", "",
            "done-success", "",
            "done-failed", ""
        }
    };


    return {
      getAllUsers: getAllUsers,
      getAllEnvironments: getAllEnvironments,
      getAllServices: getAllServices,
      getAllDeployableVersions: getAllDeployableVersions,
      createNewDeployment: createNewDeployment,
      getAllRunningDeployments: getAllRunningDeployments,
      getService: getService,
      getEnvironment: getEnvironment,
      getUser: getUser,
      revertDeployment: revertDeployment,
      getDeploymentLogs: getDeploymentLogs
    };
}
