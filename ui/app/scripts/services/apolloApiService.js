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

    var createNewDeployment = function(targetVersion, deployedService, deployedEnvironment) {

        return $http.post(CONFIG.appUrl + "deployment/", {

            target_version: targetVersion,
            deployed_service: deployedService,
            deployed_environment: deployedEnvironment,
            initiated_by: "0"
        })
    }

    return {
      getAllUsers: getAllUsers,
      getAllEnvironments: getAllEnvironments,
      getAllServices: getAllServices,
      getAllDeployableVersions: getAllDeployableVersions,
      createNewDeployment: createNewDeployment
    };
}
