'use strict';

/* Filters */

angular.module('starSlamFilters', []).filter('humanizeMilliseconds', function() {
  return function(input) {
    return moment.duration((input / 1000), 'seconds').humanize();
  };
});
