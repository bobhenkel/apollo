'use strict';
/**
 * @ngdoc overview
 * @name apollo
 * @description
 * # apollo
 *
 * Main module of the application.
 */
angular
  .module('apollo', [
    'oc.lazyLoad',
    'ui.router',
    'ui.bootstrap',
    'angular-loading-bar',
    'ngAnimate',
    'angular-growl',
    'angularSpinner',
    'ngSanitize',
    'angular.filter'
  ])
  .config(['$stateProvider','$urlRouterProvider','$ocLazyLoadProvider',function ($stateProvider,$urlRouterProvider,$ocLazyLoadProvider) {
    
    $ocLazyLoadProvider.config({
      debug:false,
      events:true,
    });

    $urlRouterProvider.otherwise('/deployments/home');

    $stateProvider
      .state('deployments', {
        url:'/deployments',
        templateUrl: 'views/deployments/main.html',
        resolve: {
            loadMyDirectives:function($ocLazyLoad){
                return $ocLazyLoad.load(
                {
                    name:'apollo',
                    files:[
                    'scripts/directives/header/header.js',
                    'scripts/directives/sidebar/sidebar.js',
                    'scripts/directives/sidebar/sidebar-search/sidebar-search.js'
                    ]
                }),
                $ocLazyLoad.load(
                {
                   name:'toggle-switch',
                   files:["/apollo/ui/bower_components/angular-toggle-switch/angular-toggle-switch.min.js",
                          "/apollo/ui/bower_components/angular-toggle-switch/angular-toggle-switch.css"
                      ]
                }),
                $ocLazyLoad.load(
                {
                  name:'ngAnimate',
                  files:['/apollo/ui/bower_components/angular-animate/angular-animate.js']
                })
                $ocLazyLoad.load(
                {
                  name:'ngCookies',
                  files:['/apollo/ui/bower_components/angular-cookies/angular-cookies.js']
                })
                $ocLazyLoad.load(
                {
                  name:'ngResource',
                  files:['/apollo/ui/bower_components/angular-resource/angular-resource.js']
                })
                $ocLazyLoad.load(
                {
                  name:'ngSanitize',
                  files:['/apollo/ui/bower_components/angular-sanitize/angular-sanitize.js']
                })
                $ocLazyLoad.load(
                {
                  name:'ngTouch',
                  files:['/apollo/ui/bower_components/angular-touch/angular-touch.js']
                })
            }
        }
    })
      .state('deployments.home',{
        url:'/home',
        controller: 'MainCtrl',
        templateUrl:'views/deployments/home.html',
        resolve: {
          loadMyFiles:function($ocLazyLoad) {
            return $ocLazyLoad.load({
              name:'apollo',
              files:[
              'scripts/controllers/main.js',
              'scripts/controllers/chartContoller.js',
              'scripts/directives/notifications/notifications.js',
              'scripts/directives/dashboard/stats/stats.js'
              ]
            }),
            $ocLazyLoad.load({
              name:'chart.js',
              files:[
                '/apollo/ui/bower_components/angular-chart.js/dist/angular-chart.min.js',
                '/apollo/ui/bower_components/angular-chart.js/dist/angular-chart.css'
              ]
            })
            $ocLazyLoad.load({
              name:'ngAnimate',
              files:['/apollo/ui/bower_components/angular-animate/angular-animate.js']
            })
          }
        }
      })
      .state('deployments.new',{
        templateUrl:'views/deployments/new.html',
        controller: 'newDeploymentCtrl',
        url:'/new',
        resolve: {
          loadMyFiles:function($ocLazyLoad) {
            return $ocLazyLoad.load({
              name:'apollo',
              files:[
              'scripts/services/apolloApiService.js',
              'scripts/services/githubApiService.js',
              'scripts/controllers/newDeploymentCtrl.js'
              ]
            })
          }
        }
    })
      .state('deployments.ongoing',{
        templateUrl:'views/deployments/ongoing.html',
        controller: 'ongoingDeploymentCtrl',
        url:'/ongoing?deploymentId',
        resolve: {
                  loadMyFiles:function($ocLazyLoad) {
                    return $ocLazyLoad.load({
                      name:'apollo',
                      files:[
                      'scripts/services/apolloApiService.js',
                      'scripts/services/githubApiService.js',
                      'scripts/controllers/ongoingDeploymentCtrl.js'
                      ]
                    })
                  }
                }
    })
      .state('deployments.history',{
            templateUrl:'views/deployments/history.html',
            controller: 'deploymentHistoryCtrl',
            url:'/history',
            resolve: {
                      loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                          name:'apollo',
                          files:[
                          'scripts/services/apolloApiService.js',
                          'scripts/services/githubApiService.js',
                          'scripts/controllers/deploymentHistoryCtrl.js'
                          ]
                        })
                      }
                    }
    })

    $stateProvider
          .state('service', {
            url:'/service',
            templateUrl: 'views/service/main.html',
            resolve: {
                loadMyDirectives:function($ocLazyLoad){
                    return $ocLazyLoad.load(
                    {
                        name:'apollo',
                        files:[
                        'scripts/directives/header/header.js',
                        'scripts/directives/header/header-notification/header-notification.js',
                        'scripts/directives/sidebar/sidebar.js',
                        'scripts/directives/sidebar/sidebar-search/sidebar-search.js'
                        ]
                    }),
                    $ocLazyLoad.load(
                    {
                       name:'toggle-switch',
                       files:["/apollo/ui/bower_components/angular-toggle-switch/angular-toggle-switch.min.js",
                              "/apollo/ui/bower_components/angular-toggle-switch/angular-toggle-switch.css"
                          ]
                    }),
                    $ocLazyLoad.load(
                    {
                      name:'ngAnimate',
                      files:['/apollo/ui/bower_components/angular-animate/angular-animate.js']
                    })
                    $ocLazyLoad.load(
                    {
                      name:'ngCookies',
                      files:['/apollo/ui/bower_components/angular-cookies/angular-cookies.js']
                    })
                    $ocLazyLoad.load(
                    {
                      name:'ngResource',
                      files:['/apollo/ui/bower_components/angular-resource/angular-resource.js']
                    })
                    $ocLazyLoad.load(
                    {
                      name:'ngSanitize',
                      files:['/apollo/ui/bower_components/angular-sanitize/angular-sanitize.js']
                    })
                    $ocLazyLoad.load(
                    {
                      name:'ngTouch',
                      files:['/apollo/ui/bower_components/angular-touch/angular-touch.js']
                    })
                }
            }
        })
      .state('service.status',{
            templateUrl:'views/service/status.html',
            controller: 'serviceVersionStatusCtrl',
            url:'/status',
            resolve: {
                      loadMyFiles:function($ocLazyLoad) {
                        return $ocLazyLoad.load({
                          name:'apollo',
                          files:[
                          'scripts/services/apolloApiService.js',
                          'scripts/services/githubApiService.js',
                          'scripts/controllers/serviceVersionStatusCtrl.js'
                          ]
                        })
                      }
                    }
    })
      .state('service.configure',{
            templateUrl:'views/service/configure.html',
            url:'/configure'
    })

     $stateProvider
           .state('blocker', {
             url:'/blocker',
             templateUrl: 'views/blocker/main.html',
             resolve: {
                 loadMyDirectives:function($ocLazyLoad){
                     return $ocLazyLoad.load(
                     {
                         name:'apollo',
                         files:[
                         'scripts/directives/header/header.js',
                         'scripts/directives/header/header-notification/header-notification.js',
                         'scripts/directives/sidebar/sidebar.js',
                         'scripts/directives/sidebar/sidebar-search/sidebar-search.js'
                         ]
                     }),
                     $ocLazyLoad.load(
                     {
                        name:'toggle-switch',
                        files:["/apollo/ui/bower_components/angular-toggle-switch/angular-toggle-switch.min.js",
                               "/apollo/ui/bower_components/angular-toggle-switch/angular-toggle-switch.css"
                           ]
                     }),
                     $ocLazyLoad.load(
                     {
                       name:'ngAnimate',
                       files:['/apollo/ui/bower_components/angular-animate/angular-animate.js']
                     })
                     $ocLazyLoad.load(
                     {
                       name:'ngCookies',
                       files:['/apollo/ui/bower_components/angular-cookies/angular-cookies.js']
                     })
                     $ocLazyLoad.load(
                     {
                       name:'ngResource',
                       files:['/apollo/ui/bower_components/angular-resource/angular-resource.js']
                     })
                     $ocLazyLoad.load(
                     {
                       name:'ngSanitize',
                       files:['/apollo/ui/bower_components/angular-sanitize/angular-sanitize.js']
                     })
                     $ocLazyLoad.load(
                     {
                       name:'ngTouch',
                       files:['/apollo/ui/bower_components/angular-touch/angular-touch.js']
                     })
                 }
             }
         })
      .state('blocker.configure',{
            templateUrl:'views/blocker/configure.html',
            url:'/configure'
    })
      .state('login',{
        templateUrl:'views/pages/login.html',
        url:'/login'
    })

  }]);


angular
  .module('apollo')
  .config(['growlProvider', function(growlProvider) {
    growlProvider.globalTimeToLive(3000);
    growlProvider.globalReversedOrder(true);
  }]);

