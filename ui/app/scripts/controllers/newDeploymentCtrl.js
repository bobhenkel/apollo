'use strict';

angular.module('apollo')
  .controller('newDeploymentCtrl', ['apolloApiService', 'githubApiService', '$scope',
                                    '$timeout' , '$state', 'growl', 'usSpinnerService',
            function (apolloApiService, githubApiService, $scope, $timeout, $state, growl, usSpinnerService) {

        // Define the flow steps
        var deploymentSteps = ["choose-environment", "choose-service", "choose-version", "confirmation"];

        // Define validation functions.. //TODO: something better?
        var deploymentValidators = {"choose-environment" : validateEnvironment,
                                    "choose-service" : validateService,
                                    "choose-version" : validateVersion};

        // Scope variables
		$scope.environmentSelected = null;
		$scope.serviceSelected = null;
		$scope.versionSelected = null;
		$scope.showNextStep = true;

		$scope.currentStep = deploymentSteps[0];

        // Scope setters
        $scope.setSelectedEnvironment = function (environmentSelected) {
           $scope.environmentSelected = environmentSelected;
        };
        $scope.setSelectedService = function (serviceSelected) {
           $scope.serviceSelected = serviceSelected;
        };
        $scope.setSelectedVersion = function (versionSelected) {
                   $scope.versionSelected = versionSelected;
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

                // Finish flow if in last step
                if (currIndex == deploymentSteps.length - 1) {

                    $scope.showNextStep = false;
                }
            }
            else {
                growl.error("You must select one!");
            }

        };

        $scope.deploy = function() {

            // Just running the validators first, to make sure nothing has changed
            angular.forEach(deploymentValidators, function(validateFunction, name) {

                  if (!validateFunction()) {
                    growl.error("Something unexpected has occurred! Try again.")
                    return;
                  }
             })

            // Set spinner
            usSpinnerService.spin('deployment-spinner')

            // Now we can deploy
            apolloApiService.createNewDeployment($scope.versionSelected.sha,
                    $scope.serviceSelected.id, $scope.environmentSelected.id).then(function (response) {

                        // End spinner
                        usSpinnerService.stop('deployment-spinner');

                        // Due to bug with angular-bootstrap and angular 1.4, the modal is not closing when redirecting.
                        // So just forcing it to :)   TODO: after the bug is fixed, remove this shit
                        $('#confirm-modal').modal('hide');
                        $('body').removeClass('modal-open');
                        $('.modal-backdrop').remove();

                        // Redirect user to ongoing deployments
                        $state.go('deployments.ongoing', {deploymentId: response.data.id});

                    }, function(error) {

                        // End spinner
                        usSpinnerService.stop('deployment-spinner');
                        growl.error("Got from apollo API: " + error.status + " (" + error.statusText + ")", {ttl: 7000})
                    });
        }

        // Validators
        function validateEnvironment() {

            if ($scope.environmentSelected == null) {
                return false;
            }
            return true;
        }

        function validateService() {

            if ($scope.serviceSelected == null) {
                return false;
            }
            return true;
        }

        function validateVersion() {

            if ($scope.versionSelected == null) {
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
                                                    $scope.allVersions.sort(function(a, b) { return new Date(b.commit.author.date) - new Date(a.commit.author.date) });
                                                  });
            }
        });
}]);