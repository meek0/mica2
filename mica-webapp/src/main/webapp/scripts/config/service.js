'use strict';

micaApp.factory('MicaConfig', ['$resource',
  function ($resource) {
    return $resource('ws/config', {}, {
      // override $resource.save method because it uses POST by default
      'save': {method: 'PUT'},
      'get': {method: 'GET'}
    });
  }]);