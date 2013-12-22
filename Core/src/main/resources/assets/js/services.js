'use strict';

/* Services */

var starSlamServices = angular.module('starSlamServices', ['ngResource']);

starSlamServices.factory('Project', ['$resource',
  function($resource){
    return $resource('/projects/:projectId', {}, {
      list: {method:'GET', isArray:true}
    });
  }
]);

starSlamServices.factory('Scan', ['$resource',
    function($resource) {
        return $resource('/projects/:projectId/scans/:scanId', {}, {
            list: {method:'GET', isArray:true}
        });
    }
]);

starSlamServices.factory('ScannedFile', ['$resource',
    function($resource) {
        return $resource('/projects/:projectId/scans/:scanId/files', {}, {
            list: {method:'GET', isArray:true}
        })
    }
])
