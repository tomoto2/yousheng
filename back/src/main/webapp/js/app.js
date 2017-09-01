var app=angular.module('starter', ['ionic', 'starter.controllers', 'starter.services']);
app.directive('hideTabs', function($rootScope) {
    return {
        restrict: 'A',
        link: function(scope, element, attributes) {
            scope.$on('$ionicView.beforeEnter', function() {
                scope.$watch(attributes.hideTabs, function(value){
                    $rootScope.hideTabs = value;
                });
            });

            scope.$on('$ionicView.beforeLeave', function() {
                $rootScope.hideTabs = false;
            });
        }
    };
})
app.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {

    if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);

    }
    if (window.StatusBar) {
      StatusBar.styleDefault();
    }
  });
})
app.constant('$ionicLoadingConfig', {
   template: '<ion-spinner icon="circles" class="spinner-calm"></ion-spinner>',
   animation: 'fade-in',
   noBackdrop:true
})
app.config(function($stateProvider, $urlRouterProvider,$ionicConfigProvider) {
       $ionicConfigProvider.platform.ios.tabs.style('standard'); 
        $ionicConfigProvider.platform.ios.tabs.position('bottom');
        $ionicConfigProvider.platform.android.tabs.style('standard');
        $ionicConfigProvider.platform.android.tabs.position('standard');

        $ionicConfigProvider.platform.ios.navBar.alignTitle('center'); 
        $ionicConfigProvider.platform.android.navBar.alignTitle('bottom');//默认为left

        $ionicConfigProvider.platform.ios.backButton.previousTitleText('').icon('ion-ios-arrow-thin-left');
        $ionicConfigProvider.platform.android.backButton.previousTitleText('').icon('ion-android-arrow-back');        

        $ionicConfigProvider.platform.ios.views.transition('ios'); 
        $ionicConfigProvider.platform.android.views.transition('android');
  $stateProvider
  .state('tab', {
    url: '/tab',
    abstract: true,
    templateUrl: 'templates/tabs.html'
  })

  .state('tab.dash', {
    url: '/dash',
    views: {
      'tab-dash': {
        templateUrl: 'templates/tab-dash.html',
        controller: 'DashCtrl'
      }
    }
  })
  .state('tab.tixian', {
      url: '/dash/tixian:qian/:index',
      views: {
        'tab-dash': {
          templateUrl: 'templates/tixian.html',
          controller: 'tixianCtrl'
        }
      }
    })
   .state('tab.gaiwxh', {
      url: '/dash/tixian/gaiwxh:index',
      views: {
        'tab-dash': {
          templateUrl: 'templates/gaiwxh.html',
          controller: 'gaiwxhCtrl'
        }
      }
    })
   .state('tab.lisi', {
      url: '/dash/tixian/lisi',
      views: {
        'tab-dash': {
          templateUrl: 'templates/lisi.html',
          controller: 'lisiCtrl'
        }
      }
    })
    .state('tab.yuefen', {
      url: '/dash/yuefen',
      views: {
        'tab-dash': {
          templateUrl: 'templates/yuefen.html',
          controller: 'yuefenCtrl'
        }
      }
    })
     .state('tab.daili', {
      url: '/dash/daili',
      views: {
        'tab-dash': {
          templateUrl: 'templates/daili.html',
          controller: 'dailiCtrl'
        }
      }
    })
  .state('tab.chats', {
      url: '/chats',
      views: {
        'tab-chats': {
          templateUrl: 'templates/tab-chats.html',
          controller: 'ChatsCtrl'
        }
      }
    })
  //俱乐部
    .state('tab.zonghe-julebu', {
      url: '/chats/julebu',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/julebu.html',
          controller: 'ZongheJulebuCtrl'
        }
      }
    })
    //充值记录
     .state('tab.zonghe-jilu', {
      url: '/chats/julebu/jilu',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/congzjilu.html',
          controller: 'ZonghejiluCtrl'
        }
      }
    })
    .state('tab.zonghe-gouka', {
      url: '/chats/gouka',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/goumaika.html',
          controller: 'ZongheGoukaCtrl'
        }
      }
    })
    .state('tab.zonghe-taocan', {
      url: '/chats/gouka/taocanxq:shu',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/taocanxq.html',
          controller: 'ZonghetaocanxqCtrl'
        }
      }
    })
    //代理信息
    .state('tab.zonghe-dailixxi', {
      url: '/chats/dailixxi',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/dailixxi.html',
          controller: 'ZonghedailixxiCtrl'
        }
      }
    })
    //新增代理
    .state('tab.zonghe-xinzen', {
      url: '/chats/dailixxi/xinzen',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/xinzengdl.html',
          controller: 'ZonghexinzenCtrl'
        }
      }
    })
    //新增代理搜索
    .state('tab.zonghe-sousuo', {
      url: '/chats/dailixxi/sousuo',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/xinzsous.html',
          controller: 'ZonghesousuoCtrl'
        }
      }
    })
    .state('tab.zonghe-jiagou', {
      url: '/chats/jiagou',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/dljiagou.html',
          controller: 'ZonghejiagouCtrl'
        }
      }
    })
    .state('tab.zonghe-baobiao', {
      url: '/chats/biaobiao',
      views: {
        'tab-chats': {
          templateUrl: 'templates/zonghe/baobiao.html',
          controller: 'ZonghebaobiaoCtrl'
        }
      }
    })

  .state('tab.account', {
    url: '/account',
    views: {
      'tab-account': {
        templateUrl: 'templates/tab-account.html',
        controller: 'AccountCtrl'
      }
    }
  })
  .state('tab.xinxi', {
      url: '/account/xinxi:shuju/:shenf/:wxhao',
      views: {
        'tab-account': {
          templateUrl: 'templates/grxinxi.html',
          controller: 'xinxiCtrl'
        }
      }
    })
  .state('tab.gainic', {
      url: '/account/gainic',
      views: {
        'tab-account': {
          templateUrl: 'templates/gainicen.html',
          controller: 'gainicCtrl'
        }
      }
    })
  .state('tab.shenfenz', {
      url: '/account/shenfenz',
      views: {
        'tab-account': {
          templateUrl: 'templates/shenfenzhen.html',
          controller: 'shenfenzCtrl'
        }
      }
    })
  .state('tab.weixinh', {
      url: '/account/weixinh',
      views: {
        'tab-account': {
          templateUrl: 'templates/wxzh.html',
          controller: 'weixinhCtrl'
        }
      }
    })
   .state('tab.gouka', {
      url: '/account/gouka',
      views: {
        'tab-account': {
          templateUrl: 'templates/goukajl.html',
          controller: 'goukaCtrl'
        }
      }
    })
   .state('tab.julebu', {
      url: '/account/julebu',
      views: {
        'tab-account': {
          templateUrl: 'templates/julebu.html',
          controller: 'julebuCtrl'
        }
      }
    })
   .state('tab.xieyi', {
      url: '/account/xieyi',
      views: {
        'tab-account': {
          templateUrl: 'templates/xieyi.html',
          controller: 'xieyiCtrl'
        }
      }
   })
  ;
  $urlRouterProvider.otherwise('/tab/dash');

});
app.value("tokens","");
