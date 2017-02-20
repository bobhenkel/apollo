'use strict';

angular.module('apollo')
  .controller('serviceVersionStatusCtrl', ['apolloApiService', '$scope',
                                    '$timeout' , '$state', 'growl', 'usSpinnerService',
            function (apolloApiService, $scope, $timeout, $state, growl, usSpinnerService) {

            $scope.filteredResults = [];
            $scope.currentScreen = "filters";

            $scope.showByService = function(service) {
                $scope.filteredResults = $scope.latestDeployments.filter(function(a){return a.serviceId == service})
                $scope.currentScreen = "results";
            };

            $scope.showByEnvironment = function(environment) {
                $scope.filteredResults = $scope.latestDeployments.filter(function(a){return a.environmentId == environment})
                $scope.currentScreen = "results";
            };

            $scope.backToFilter = function() {
                $scope.currentScreen = "filters";
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

            apolloApiService.getLatestDeployments().then(function(response) {
                $scope.latestDeployments = response.data;
                $scope.githubData = {};

                // For each deployment, i want to fetch the deployable version, and then the commit from github
                for(var i=0; i < response.data.length; i++) {

                    // apolloApiService.getDeployableVersion(response.data[i].deployableVersionId).then(function(deployableResponse){
                    //     githubApiService.getCommitDetails(deployableResponse.data.githubRepositoryUrl,
                    //                                       deployableResponse.data.gitCommitSha).then(function(gitResponse){
                    //         $scope.githubData[deployableResponse.data.id] = gitResponse.data;
                    //     })
                    // })
                }
            });

}]);