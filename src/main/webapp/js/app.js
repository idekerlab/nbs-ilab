(function(angular) {
  'use strict';

angular.module('iLab', ['ngRoute','ui.bootstrap','spinner','ui.bootstrap.modal','iLab.filters','iLab.services','iLab.directives','iLab.controllers'])

.config(function($routeProvider, $locationProvider) {
		
	$routeProvider
		.when('/nbs',{
			controller	: 'LoadFormDataController',
			templateUrl	: 'partials/details.html'});
	
	$routeProvider
		.otherwise({
			redirectTo	: '/nbs'});
		
})})(window.angular);
