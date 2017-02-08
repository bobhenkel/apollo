'use strict';

angular.module('apollo')
  .controller('deploymentHistoryCtrl', ['apolloApiService', 'githubApiService', '$scope',
                                    '$timeout' , '$state', 'growl', 'usSpinnerService',
            function (apolloApiService, githubApiService, $scope, $timeout, $state, growl, usSpinnerService) {

                $scope.selectedDeployment = null;

                $scope.setSelectedDeployment = function(selectedDeployment) {
                    $scope.selectedDeployment = selectedDeployment;

                   };

                $scope.getLabel = function(deploymentStatus) {

                    return apolloApiService.matchLabelToDeploymentStatus(deploymentStatus);
                };

                $scope.showDetails = function() {

                    usSpinnerService.spin('details-spinner');

                    apolloApiService.getDeployableVersion(
                    $scope.allDeployments.filter(function(a){return a.id == $scope.selectedDeployment.id})[0].deployable_version)
                    .then(function(response) {

                        githubApiService.getCommitDetails(response.data.github_repository_url,
                                                          response.data.git_commit_sha)
                        .then(function(response){

                            usSpinnerService.stop('details-spinner');
                            $scope.githubResponse = response.data;
                        },
                        function(error){
                            usSpinnerService.stop('details-spinner');
                            growl.error("Could not fetch data from github! error: " + error.data);
                        });
                    },
                    function(error){
                            usSpinnerService.stop('details-spinner');
                            growl.error("Could not fetch data from apollo! error: " + error.data);
                    });
                };

                $scope.revert = function() {

                    // TODO: revert to deployemnt
                    growl.error("Not implemented yet!")
                };

                $scope.clearFilters = function() {

                    $scope.serviceSearch = "";
                    $scope.environmentSearch = "";
                    $scope.userSearch = "";
                    $scope.statusSearch = "";
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
``
                    var tempUsers = {};

                    response.data.forEach(function(user) {
                        tempUsers[user.id] = user;
                    });

                    $scope.allUsers = tempUsers;
                });

                apolloApiService.getAllDeployments().then(function(response) {

                   //var tempDeployments = {};

                   //response.data.forEach(function(deployment) {
                   //    tempDeployments[deployment.id] = deployment;
                   //});

                   //$scope.allDeployments = tempDeployments;
                   $scope.allDeployments = response.data;
                });


            }]);
