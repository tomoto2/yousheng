var app = angular.module("myapp",['ui.router','controllers']);
//app.directive('onFinishRenderFilters', function ($timeout) {
//      return {
//          restrict: 'A',
//          link: function(scope, element, attr) {
//              if (scope.$last === true) {
//                  $timeout(function() {
//                      scope.$emit('ngRepeatFinished');
//                  });
//              }
//          }
//      };
//  });
app.config(function ($stateProvider, $urlRouterProvider) {
     $urlRouterProvider.when("", "/tab/wanjia");
     $stateProvider
        .state("tab", {
            url: "/tab",
            templateUrl: "tab.html",
            controller:"tabCtrl"
        })
        .state("tab.wj", {
            url:"/wanjia",
           views:{
            'wj':{
                templateUrl:"wanjia.html",
                controller:"wanjiaCtrl"
            }
           }
        })
        .state("tab.yh", {
            url:"/yonghu",
            views:{
            'yh':{
               templateUrl: "yonghu.html",
               controller:"yonghuCtrl"
               }
           }
        })
        .state("tab.xx", {
            url:"/xinxi",
            views:{
            'xx':{
               templateUrl: "dailixx.html",
            controller:"dailixxCtrl"
               }
           }
            
        })
         .state("tab.jg", {
            url:"/jiagou",
            views:{
            'jg':{
               templateUrl: "dailijg.html",
            controller:"dailijgCtrl"
               }
           }
            
        })
          .state("tab.jlb", {
            url:"/julebu",
            views:{
            'jlb':{
               templateUrl: "julebu.html",
               controller:"julebuCtrl"
               }
           }
            
        })
        .state("tab.chengyuan", {
            url:"/chengyuan/:id",
            views:{
            'jlb':{
                templateUrl: "chengyuan.html",
                controller:"chengyuanCtrl"
               }
           }
            
        })
           .state("tab.bb", {
            url:"/baobiao",
            views:{
            'bb':{
               templateUrl: "baobiao.html",
            controller:"baobiaoCtrl"
               }
           }
            
        })
            .state("tab.fl", {
            url:"/fanenli",
            views:{
            'fl':{
               templateUrl: "fanli.html",
                 controller:"fanliCtrl"
               }
           }
            
        })
        ;
});
app.controller("mycontroller",function($scope,$http){
//var urls='http://192.168.0.113:80/ws/admin/getAdminInfo'	;
//$.ajax({
//		url:urls,
//		xhrFields: {
//         withCredentials: true
//     },
//     crossDomain: true,
//		type:"GET",
//		success:function(res){
//		    console.log(res);
//		},
//		error:function(res){
//		    console.log(res)
//		}
// });
 $scope.tuichuFn=function(){
 	window.location.href='denglu.html';
 }
})
