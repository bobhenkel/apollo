'use strict';
/**
 * @ngdoc function
 * @name apollo.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the apollo
 */
angular.module('apollo')
  .controller('newDeploymentCtrl', ['apolloApiService','$scope', '$timeout' ,'growl' ,function (apolloApiService, $scope, $timeout, growl) {

        // Define the flow steps
        var deploymentSteps = ["choose-environment", "choose-service"];

        // Define validation functions.. //TODO: something better?
        var deploymentValidators = {"choose-environment" : validateEnvironment, "choose-service" : validateService};

        // Scope variables
		$scope.environmentIdSelected = null;
		$scope.serviceIdSelected = null;

		$scope.currentStep = deploymentSteps[0];


		// Change the environment table visual selection
        $scope.setSelectedEnvironment = function (environmentIdSelected) {
           $scope.environmentIdSelected = environmentIdSelected;
        };

        // Change the services table visual selection
        $scope.setSelectedService = function (serviceIdSelected) {
           $scope.serviceIdSelected = serviceIdSelected;
        };


        $scope.nextStep = function() {

            // First validate the input
            if (deploymentValidators[$scope.currentStep]()) {

                // Get the current index
                var currIndex = deploymentSteps.indexOf($scope.currentStep);
    8
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

        // Data fetching
		apolloApiService.getAllEnvironments().then(function(response) {
			$scope.allEnvironments = response.data;
		});

		apolloApiService.getAllServices().then(function(response) {
        	$scope.allServices = response.data;
        });

}]);