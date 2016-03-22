'use strict';

angular.module('apollo')
  .controller('deploymentHistoryCtrl', ['apolloApiService', '$scope', '$stateParams',
                                    '$timeout' , '$state', 'growl', 'usSpinnerService',
            function (apolloApiService, $scope, $stateParams, $timeout, $state, growl, usSpinnerService) {

                $scope.deploymentId = $stateParams.deploymentId;










                // Data fetching
                apolloApiService.getAllEnvironments().then(function(response) {
                    $scope.allEnvironments = response.data;
                });

                apolloApiService.getAllServices().then(function(response) {
                    $scope.allServices = response.data;
                });

                apolloApiService.getAllUsers().then(function(response) {
                    $scope.allUsers = response.data;
                });

            }]);