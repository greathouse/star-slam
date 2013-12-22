'use strict';

/* Controllers */

var controllers = angular.module('starSlamControllers', []);

controllers.controller('ProjectListController', ['$scope', 'Project',
  function($scope, Project) {
    $scope.projects = Project.list();
  }
]);

controllers.controller('ProjectDetailController', ['$scope', '$location', '$routeParams', 'Project', 'Scan',
    function($scope, $location, $routeParams, Project, Scan) {
        $scope.project = Project.get({projectId: $routeParams.projectId}, function(project) {
            $scope.scans = Scan.list({projectId: project.id})
        });

        $scope.processingTimeInSeconds = function(scan) {
            return scan.processingTime / 1000;
        };

        $scope.goToScanDetail = function(scan) {
            $location.path('/projects/'+$routeParams.projectId+'/scans/'+scan.id);
        };
    }
]);

controllers.controller('ScanDetailController', ['$scope', '$routeParams', 'Scan', 'ScannedFile',
    function($scope, $routeParams, Scan, ScannedFile) {
        $scope.scan = Scan.get({projectId: $routeParams.projectId, scanId: $routeParams.scanId}, function(scan) {
            $scope.files = ScannedFile.list({projectId: $routeParams.projectId, scanId:$routeParams.scanId})
        });
    }
])