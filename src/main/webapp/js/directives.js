'use strict';

/* Directives */

angular.module('iLab.directives', [])

.directive('appVersion', ['version', function(version) {
    return function(scope, elm, attrs) {
      elm.text(version);
    };
}])

.directive('appCopyright', ['copyright', function(copyright) {
    return function(scope, elm, attrs) {
      elm.text(copyright);
    };
}]);