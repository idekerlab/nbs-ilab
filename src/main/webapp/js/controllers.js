'use strict';

/* Controllers */

var uploadFiles = {};
angular.module('iLab.controllers', [])

/*
 * This controller is used to GET data from remote server and populate the form.
 */
.controller('LoadFormDataController',function($scope,LoadFormDataFactory) {
	
	$scope.form = {};
	uploadFiles = {};
	
	LoadFormDataFactory.get({fileName:"/nbs.properties"},function(formData) {
		$scope.form = formData;
	});
})

/*
 * This controller is used to control the FORM and POST data to the remote server.
 */
.controller('FormSubmitController',function($scope,$rootScope,FormSubmitFactory){
	
	$scope.setNetwork = function(value){
		$scope.form.networkInputType = value;
	};
	
	$scope.runProgram = function(form){
		$rootScope.spinner.on();
		setTimeout(function(){
			
			var fd = new FormData();
			fd.append('config', JSON.stringify(form));
			if(uploadFiles.patientFile != undefined) fd.append('patientFile',uploadFiles.patientFile);
			if(uploadFiles.networkFile != undefined) fd.append('networkFile',uploadFiles.networkFile);
			
			FormSubmitFactory.post($scope, fd)
				.success(function(data){
		 			var html;
					if(data.status == "nbs_in_use"){
						html = "<div>" + "<p>NBS is currently running at maximum capacity.</p>" +
						"<p>Please try your request again at a later time.</p>" + "</div>";
					}
					else {
						html = "<div>" + "<p>Success!<p>Your request to run <b>NBS</b> has been submitted to Ideker Lab web services.</p>" +
						"<p>The service will notify you via email once your job has completed.</p>" + "</div>";
					}
					$rootScope.spinner.off();
					$("#diaTxt").html(html);
					$("#dialog").dialog("open");
				})
				.error(function(data){
					$rootScope.spinner.off();
					console.log("failure =", data);
				});
		},10000); 
	};
})

/*
 * This Directive is used to control assignment of upload files to global variables.
 */
.directive("fileread", [function() {
	return {
	    scope: {
	    	"fileread": "="
	    },
	    link: function (scope, element, attributes) {
	        element.bind("change", function (changeEvent) {
	            scope.$apply(function () {
	            	if(attributes.value === "network")
	            			{ uploadFiles.networkFile = changeEvent.target.files[0];}
	            	else 	{ uploadFiles.patientFile = changeEvent.target.files[0];}
	            });
	        });
	    }
    };
}]);

//Clear browser cache (in development mode)
//http://stackoverflow.com/questions/14718826/angularjs-disable-partial-caching-on-dev-machine

//.run(function($rootScope, $templateCache) {
//	$rootScope.$on('$viewContentLoaded', function() {
//		$templateCache.removeAll();
//	});
//});

