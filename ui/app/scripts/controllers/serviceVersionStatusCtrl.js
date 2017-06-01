'use strict';

angular.module('apollo')
  .controller('serviceVersionStatusCtrl', ['apolloApiService', '$scope',
                                    '$timeout', '$state', '$interval', 'growl', 'usSpinnerService',
            function (apolloApiService, $scope, $timeout, $state, $interval, growl, usSpinnerService) {

            $scope.filteredResults = [];
            $scope.selectedStatus = null;
            $scope.selectedPodStatus = null;
            $scope.currentScreen = "filters";
            $scope.showingBy = null;
            $scope.showingByValue = null;
            $scope.websocket = null;
            $scope.term = null;

            $scope.showByService = function(service) {
                usSpinnerService.spin('result-spinner');
                $scope.filteredResults = [];
                apolloApiService.serviceStatus(service).then(function (response) {
                    $scope.filteredResults = response.data;
                    populateDeployableVersions();
                    refreshPreSelectedStatus();
                    usSpinnerService.stop('result-spinner');
                });
                $scope.currentScreen = "results";
                $scope.showingBy = "service";
                $scope.showingByValue = service;
            };

            $scope.showByEnvironment = function(environment) {
                usSpinnerService.spin('result-spinner');
                $scope.filteredResults = [];
                apolloApiService.environmentStatus(environment).then(function (response) {
                    $scope.filteredResults = response.data;
                    populateDeployableVersions();
                    refreshPreSelectedStatus();
                    usSpinnerService.stop('result-spinner');
                });
                $scope.currentScreen = "results";
                $scope.showingBy = "environment";
                $scope.showingByValue = environment;
            };

            $scope.backToFilter = function() {
                $scope.currentScreen = "filters";
            };

            $scope.setSelectedStatus = function (status) {
                $scope.selectedStatus = status;
            };

            $scope.getLogs = function() {
                fetchLatestLogs();
                $scope.logsInterval = $interval(fetchLatestLogs, 10000);
            };

            $scope.stopLogs = function() {
                $interval.cancel($scope.logsInterval);
            };

            $scope.restartPod = function (podName) {

                usSpinnerService.spin('result-spinner');

                apolloApiService.restartPod($scope.selectedStatus.environmentId, podName).then(function (response) {
                    usSpinnerService.stop('result-spinner');
                    growl.success("Successfully restarted pod " + podName + "!");
                }, function (error) {
                    usSpinnerService.stop('result-spinner');
                    growl.error("Could not restart pod! got: " + error.statusText)
                });
            };

            $scope.refreshStatus = function () {
                if ($scope.showingBy === "service") {
                    $scope.showByService($scope.showingByValue);
                } else {
                    $scope.showByEnvironment($scope.showingByValue);
                }
            };

            $scope.selectPod = function (podStatus) {
                $scope.selectedPodStatus = podStatus;
            };

            $scope.startLiveSession = function (containerName) {
                setTimeout(function () {
                    $scope.term = new Terminal({
                        scrollback: 3000
                    });

                    var environmentId = $scope.selectedStatus.environmentId;
                    var serviceId = $scope.selectedStatus.serviceId;
                    var podName = $scope.selectedPodStatus.name;
                    var execUrl = apolloApiService.getWebsocketExecUrl(environmentId, serviceId, podName, containerName);

                    $scope.websocket = new WebSocket(execUrl);

                    $scope.websocket.onopen = function () {
                        $scope.websocket.send("export TERM=\"xterm\"\n");
                    };

                    $scope.websocket.onerror = function (event) {
                        if (event.code) {
                            growl.error("Unknown error occurred, error code: " + event.code, {ttl: 7000});
                        } else {
                            growl.error("You don't have permissions to deploy to that service on that environment, hence no live-session!", {ttl: 7000});
                        }
                    };

                    $scope.term.open(document.getElementById('terminal'));
                    $scope.term.fit();
                    $scope.term.focus();

                    $scope.term.attach($scope.websocket, true, false);
                    $scope.term.writeln("Wait for the prompt, initializing...");

                }, 300);
            };

            $scope.closeLiveSession = function () {
                if ($scope.term !== null) {
                    $scope.term.detach();
                    $scope.term.destroy();
                }

                if ($scope.websocket !== null) {
                    $scope.websocket.close();
                }
            };

            function refreshPreSelectedStatus() {
                if ($scope.selectedStatus) {

                    // Refresh the view for the user
                    $scope.selectedStatus = $scope.filteredResults.filter(function (filtered) {
                        if (filtered) {
                            return (filtered.serviceId === $scope.selectedStatus.serviceId && filtered.environmentId === $scope.selectedStatus.environmentId)
                        }
                    })[0]
                }
            }

            function fetchLatestLogs() {
                apolloApiService.logsFromStatus($scope.selectedStatus.environmentId, $scope.selectedStatus.serviceId).then(function(response) {
                    $scope.dockerLogs = response.data;
                });
            }

            function populateDeployableVersions(service) {
                $scope.deployableVersions = [];
                $scope.filteredResults.forEach(function (status) {
                    if (status !== null) {
                        apolloApiService.getDeployableVersionBasedOnSha(status.gitCommitSha, status.serviceId).then(function (response) {
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