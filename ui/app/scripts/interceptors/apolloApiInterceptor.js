'use strict';
angular
  .module('apollo')
  .factory('apolloApiInterceptor', ['$log', '$location', '$q', 'localStorageService', function($log, $location, $q, localStorageService) {

    var apolloInterceptor = {

        request: function(config) {
            if(config.url.startsWith(CONFIG.appUrl)) {

                if(!config.url.endsWith("login/")) {

                    if(localStorageService.get('token') != undefined) {
                        config.headers['Authorization'] = "Token " + localStorageService.get("token");
                    }
                    else {
                        // Redirect to login page
                        $location.path("/login");
                    }
                }
            }
            return config;
        },

        response: function(response) {

            if(response.config.url.startsWith(CONFIG.appUrl)) {
                if (response.status == 401) {

                    $location.path("/login");
                }
            }

            return response;
        }
    };

    return apolloInterceptor;
}]);