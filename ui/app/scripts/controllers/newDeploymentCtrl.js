'use strict';
/**
 * @ngdoc function
 * @name apollo.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the apollo
 */
angular.module('apollo')
  .controller('newDeploymentCtrl', ['apolloApiService', 'githubApiService', '$scope', '$timeout' ,'growl' ,
            function (apolloApiService, githubApiService, $scope, $timeout, growl) {

        // Define the flow steps
        var deploymentSteps = ["choose-environment", "choose-service", "choose-version", "confirmation"];

        // Define validation functions.. //TODO: something better?
        var deploymentValidators = {"choose-environment" : validateEnvironment,
                                    "choose-service" : validateService,
                                    "choose-version" : validateVersion};

        // Scope variables
		$scope.environmentIdSelected = null;
		$scope.serviceIdSelected = null;
		$scope.versionIdSelected = null;

		$scope.currentStep = deploymentSteps[0];


        // Scope setters
        $scope.setSelectedEnvironment = function (environmentIdSelected) {
           $scope.environmentIdSelected = environmentIdSelected;
        };
        $scope.setSelectedService = function (serviceIdSelected) {
           $scope.serviceIdSelected = serviceIdSelected;
        };
        $scope.setSelectedVersion = function (versionIdSelected) {
                   $scope.versionIdSelected = versionIdSelected;
        };

        // Visual change the next step
        $scope.nextStep = function() {

            // First validate the input
            if (deploymentValidators[$scope.currentStep]()) {

                // Get the current index
                var currIndex = deploymentSteps.indexOf($scope.currentStep);

                // Increment the index
                currIndex++

                // Choose the next step
                $scope.currentStep = deploymentSteps[currIndex];

                // Clear the search
                $scope.globalSearch = "";
            }
            else {
                growl.error("You must select one!");
            }

        };

        // Validators
        function validateEnvironment() {

            if ($scope.environmentIdSelected == null) {
                return false;
            }
            return true;
        }

        function validateService() {

            if ($scope.serviceIdSelected == null) {
                return false;
            }
            return true;
        }

        function validateVersion() {

            if ($scope.versionIdSelected == null) {
                return false;
            }
            // TODO: add more checks here.. (service can get the version etc..)
            return true;
        }

        // Data fetching
		apolloApiService.getAllEnvironments().then(function(response) {
			$scope.allEnvironments = response.data;
		});

		apolloApiService.getAllServices().then(function(response) {
        	$scope.allServices = response.data;
        });

        apolloApiService.getAllDeployableVersions().then(function(response) {

            $scope.allVersions = [];

            for(var i=0; i < response.data.length; i++) {

                githubApiService.getCommitDetails(response.data[i].github_repository_url,
                                                  response.data[i].git_commit_sha).then(function(gitResponse){

                                                    $scope.allVersions.push(gitResponse.data);
                                                  });
            }
        });
}]);