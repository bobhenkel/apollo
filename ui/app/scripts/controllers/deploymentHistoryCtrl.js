'use strict';

angular.module('apollo')
  .controller('deploymentHistoryCtrl', ['apolloApiService', '$scope',
                                    '$timeout' , '$state', 'growl', 'usSpinnerService',
            function (apolloApiService, $scope, $timeout, $state, growl, usSpinnerService) {

                $scope.selectedDeployment = null;

                $scope.setSelectedDeployment = function(selectedDeployment) {
                    $scope.selectedDeployment = selectedDeployment;
                   };

                $scope.getLabel = function(deploymentStatus) {
                    return apolloApiService.matchLabelToDeploymentStatus(deploymentStatus);
                };

                $scope.showDetails = function() {

                    $scope.deployableVersion = undefined;
                    usSpinnerService.spin('details-spinner');

                    apolloApiService.getDeployableVersion(
                    $scope.allDeployments.filter(function(a){return a.id == $scope.selectedDeployment.id})[0].deployableVersionId)
                    .then(function(response) {
                        $scope.deployableVersion = response.data;
                        usSpinnerService.stop('details-spinner');
                    },
                    function(error){
                            usSpinnerService.stop('details-spinner');
                            growl.error("Could not fetch data from apollo! error: " + error.data);
                    });
                };

                $scope.revert = function() {

                    // Set spinner
                    usSpinnerService.spin('revert-spinner');

                    // Now we can deploy
                    apolloApiService.createNewDeployment($scope.selectedDeployment.deployableVersionId,
                        $scope.selectedDeployment.serviceId, $scope.selectedDeployment.environmentId).then(function (response) {

                        // Wait a bit to let the deployment be in the DB
                        setTimeout(function () {
                            usSpinnerService.stop('revert-spinner');

                            // Due to bug with angular-bootstrap and angular 1.4, the modal is not closing when redirecting.
                            // So just forcing it to :)   TODO: after the bug is fixed, remove this shit
                            $('#confirm-modal').modal('hide');
                            $('body').removeClass('modal-open');
                            $('.modal-backdrop').remove();

                            // Redirect user to ongoing deployments
                            $state.go('deployments.ongoing', {deploymentId: response.data.id});
                        }, 500);

                    }, function(error) {
                        // End spinner
                        usSpinnerService.stop('revert-spinner');

                        // 403 are handled generically on the interceptor
                        if (error.status != 403) {
                            growl.error("Got from apollo API: " + error.status + " (" + error.statusText + ")", {ttl: 7000})
                        }
                    });
                };

                $scope.clearFilters = function() {
                    $scope.serviceSearch = "";
                    $scope.environmentSearch = "";
                    $scope.userSearch = "";
                    $scope.statusSearch = "";
                };

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
                        tempUsers[user.userEmail] = user;
                    });
                    $scope.allUsers = tempUsers;
                });

                apolloApiService.getAllDeployments().then(function(response) {
                   $scope.allDeployments = response.data;
                });
            }]);
