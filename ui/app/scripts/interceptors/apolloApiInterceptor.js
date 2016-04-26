'use strict';
angular
  .module('apollo')
  .factory('apolloApiInterceptor', ['$log', 'localStorageService', function($log, localStorageService) {

    var apolloInterceptor = {

        request: function(config) {
            if(config.url.startsWith(CONFIG.appUrl)) {

                if(localStorageService.get('token') != undefined) {
                    config.headers['Authorization'] = "Token " + localStorageService.get("token");
                }
                else {

                    // Redirect to login page
                    //$state.go("login");
                }
            }
            return config;
        }
    };

    return apolloInterceptor;
}]);