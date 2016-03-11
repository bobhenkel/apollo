'use strict';
/**
 * @ngdoc function
 * @name apollo.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the apollo
 */
angular.module('apollo')
  .controller('newDeploymentCtrl', ['apolloApiService', '$scope', '$timeout', function (apolloApiService, $scope, $timeout) {

		$scope.environmentIdSelected = null;
        $scope.setSelectedEnvironment = function (environmentIdSelected) {
           $scope.environmentIdSelected = environmentIdSelected;
        };


		apolloApiService.getAllEnvironments().then(function(response) {
			$scope.allEnvironments = response.data;
		});
}]);