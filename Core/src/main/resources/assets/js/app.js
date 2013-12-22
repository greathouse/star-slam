'use strict';

/* App Module */

var starSlamApp = angular.module('starSlamApp', [
  'ngRoute',
  'starSlamControllers',
  'starSlamServices',
  'starSlamFilters'
]);

starSlamApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/projects', {
        templateUrl: 'partials/projects.html',
        controller: 'ProjectListController'
      }).
      when('/projects/:projectId', {
        templateUrl: 'partials/project.html',
        controller: 'ProjectDetailController'
      }).
      when('/projects/:projectId/scans/:scanId', {
        templateUrl: 'partials/scan.html',
        controller: 'ScanDetailController'
      }).
      otherwise({
        redirectTo: '/projects'
      });
  }]);
