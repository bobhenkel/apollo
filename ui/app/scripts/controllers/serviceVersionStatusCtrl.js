'use strict';

angular.module('apollo')
  .controller('serviceVersionStatusCtrl', ['apolloApiService', '$scope',
                                    '$timeout', '$state', '$interval', 'growl', 'usSpinnerService',
            function (apolloApiService, $scope, $timeout, $state, $interval, growl, usSpinnerService) {

            $scope.filteredResults = [];
            $scope.selectedStatus = null;
            $scope.currentScreen = "filters";

            $scope.showByService = function(service) {
                usSpinnerService.spin('result-spinner');
                $scope.filteredResults = [];
                apolloApiService.serviceStatus(service).then(function (response) {
                    $scope.filteredResults = response.data;
                    populateDeployableVersions();
                    usSpinnerService.stop('result-spinner');
                });
                $scope.currentScreen = "results";
            };

            $scope.showByEnvironment = function(environment) {
                usSpinnerService.spin('result-spinner');
                $scope.filteredResults = [];
                apolloApiService.environmentStatus(environment).then(function (response) {
                    $scope.filteredResults = response.data;
                    populateDeployableVersions();
                    usSpinnerService.stop('result-spinner');
                });
                $scope.currentScreen = "results";
            };

            $scope.backToFilter = function() {
                $scope.currentScreen = "filters";
            };

            $scope.setSelectedStatus = function (status) {
                $scope.selectedStatus = status;
            };

            $scope.getLogs = function() {
                fetchLatestLogs();
                $scope.logsInterval = $interval(fetchLatestLogs, 3000);
            };

            $scope.stopLogs = function() {
                $interval.cancel($scope.logsInterval);
            };

            function fetchLatestLogs() {
                apolloApiService.logsFromStatus($scope.selectedStatus.environmentId, $scope.selectedStatus.serviceId).then(function(response) {
                    $scope.dockerLogs = response.data;
                });
            }

            function populateDeployableVersions() {
                $scope.deployableVersions = [];
                $scope.filteredResults.forEach(function (status) {
                    if (status != null) {
                        apolloApiService.getDeployableVersionBasedOnSha(status.gitCommitSha).then(function (response) {
                            $scope.deployableVersions[response.data.gitCommitSha] = response.data;
                        })
                    }
                })
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
}]);