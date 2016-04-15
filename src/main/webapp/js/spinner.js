(function () {
	'use strict'; 
	
	angular.module('spinner', ['ngAnimate', 'treasure-overlay-spinner']);
    angular.module('spinner').run(run);

    run.$inject = ['$rootScope'];
    function run ($rootScope) {
      $rootScope.spinner = {
        active: false,
        on: function () {
          this.active = true;
        },
        off: function () {
          this.active = false;
        }
      };
    }
})();