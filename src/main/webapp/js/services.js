(function() {	
'use strict';

angular.module('iLab.services', ['ngResource'])

/*
 * Some basic values
 */
.value('version', '0.1')
.value('copyright', "NBS - Copyright © UCSD Department of Network Biology - All rights reserved")

/*
 * This service defines a simple REST interface to GET data
 */
.service('LoadFormDataFactory',function($resource) {
    return $resource('/iLab/rest/project/getDetails/');
})

/*
 * This service defines a simple REST interface to POST data
 */
.service('FormSubmitFactory', function($http) {
	this.post = function($scope,fd){
		 return $http.post('/iLab/rest/project/runProject/', fd, {
			headers: {'Content-Type': undefined }
		 });
	};
});})();