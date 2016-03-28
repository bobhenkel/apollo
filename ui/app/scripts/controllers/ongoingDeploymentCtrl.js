'use strict';

angular.module('apollo')
  .controller('ongoingDeploymentCtrl', ['apolloApiService', '$scope', '$stateParams', '$interval',
                                    '$timeout' , '$state', 'growl', 'usSpinnerService',
            function (apolloApiService, $scope, $stateParams, $interval, $timeout, $state, growl, usSpinnerService) {

                $scope.deploymentId = $stateParams.deploymentId;
                $scope.selectedDeployment = null;

                $scope.setSelectedDeployment = function(selectedDeployment) {
                    $scope.selectedDeployment = selectedDeployment;
                };

                $scope.revert = function(deploymentId) {

                    usSpinnerService.spin('ongoing-spinner');

                    // Revert
                    apolloApiService.revertDeployment($scope.selectedDeployment).then(function(response) {

                        // End spinner
                        usSpinnerService.stop('ongoing-spinner');
                        growl.success("Successfully reverted deployment!", {ttl: 7000})

                    }, function(error) {

                        // End spinner
                        usSpinnerService.stop('ongoing-spinner');
                        growl.error("Got from apollo API: " + error.status + " (" + error.statusText + ")", {ttl: 7000})
                    });

                }

                $scope.getLogs = function() {

                    fetchLatestLogs();
                    $scope.logsInterval = $interval(fetchLatestLogs, 3000);
                };

                $scope.stopLogs = function() {

                    $interval.cancel($scope.logsInterval);
                }

                function fetchLatestLogs() {

                    apolloApiService.getDeploymentLogs($scope.selectedDeployment.id).then(function(response) {

                        $scope.dockerLogs = response.data;
                    });
                }

                $scope.getLabel = function(deploymentStatus) {

                    return apolloApiService.matchLabelToDeploymentStatus(deploymentStatus);
                }

                $scope.getRevertClass = function(deploymentStatus) {

                    if (apolloApiService.isRevertDisabledBasedOnStatus(deploymentStatus)) {

                        return "disabled";
                    }

                    return "";
                }

                $scope.isRevertDisabledBasedOnStatus = function(deploymentStatus) {

                    return apolloApiService.isRevertDisabledBasedOnStatus(deploymentStatus);
                }


                // Data fetching
                apolloApiService.getAllEnvironments().then(function(response) {

                    var tempEnvironment = {};

                    response.data.forEach(function(environment) {
                        tempEnvironment[environment.id] = environment;
                    });

                    $scope.allEnvironments = tempEnvironment;
                });

                apolloApiService.getAllServices().then(function(response) {

                    var tempServices = {};

                    response.data.forEach(function(service) {
                        tempServices[service.id] = service;
                    });

                    $scope.allServices = tempServices;
                });

                apolloApiService.getAllUsers().then(function(response) {

                    var tempUsers = {};

                    response.data.forEach(function(user) {
                        tempUsers[user.id] = user;
                    });

                    $scope.allUsers = tempUsers;
                });

                apolloApiService.getAllRunningDeployments().then(function(response) {
                    $scope.allRunningDeployments = response.data;

                });
            }]);