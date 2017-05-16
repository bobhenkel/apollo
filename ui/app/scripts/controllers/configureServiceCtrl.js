'use strict';

angular.module('apollo')
    .controller('configureServiceCtrl', ['apolloApiService', '$scope', '$timeout' , '$state', 'growl',
        function (apolloApiService, $scope, $timeout, $state, growl) {
            $scope.save = function() {
                apolloApiService.createService($scope.name, $scope.deploymentYaml, $scope.serviceYaml).then(
                    function () {
                        growl.success("Successfully configured service!", {ttl:7000});
                        $scope.buttonDisabled = true;
                    },
                    function(error){
                        growl.error("Got from apollo API: " + error.status + " (" + error.statusText + ")", {ttl: 7000});
                    });
            };
        }]);