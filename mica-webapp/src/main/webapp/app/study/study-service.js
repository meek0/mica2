'use strict';

mica.study
  .factory('StudyStatesResource', ['$resource',
    function ($resource) {
      return $resource('ws/draft/study-states');
    }])

  .factory('StudyStateResource', ['$resource',
    function ($resource) {
      return $resource('ws/draft/study-state/:id', {}, {
        'get': {method: 'GET'}
      });
    }])

  .factory('DraftStudiesResource', ['$resource',
    function ($resource) {
      return $resource('ws/draft/studies', {}, {
        'save': {method: 'POST', errorHandler: true}
      });
    }])

  .factory('DraftStudyResource', ['$resource',
    function ($resource) {
      return $resource('ws/draft/study/:id', {}, {
        // override $resource.save method because it uses POST by default
        'save': {method: 'PUT', params: {id: '@id'}, errorHandler: true},
        'get': {method: 'GET'}
      });
    }])

  .factory('DraftStudyPublicationResource', ['$resource',
    function ($resource) {
      return $resource('ws/draft/study/:id/_publish', {}, {
        'publish': {method: 'PUT', params: {id: '@id'}}
      });
    }]);
