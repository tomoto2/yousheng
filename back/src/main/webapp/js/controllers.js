 (function(){
window.alert = function(name){
var iframe = document.createElement("IFRAME");
iframe.style.display="none";
iframe.setAttribute("src", 'data:text/plain');
document.documentElement.appendChild(iframe);
window.frames[0].window.alert(name);
iframe.parentNode.removeChild(iframe);
}
})();

function gaifn(data,token){
    	var ss="";
    	$.ajax({
		        url:urltou+"/ws/userInfo/uptUserInfo",
		        type:"POST",
		        async:false,
		        contentType:"application/json",
		        beforeSend: function(request){
			        request.setRequestHeader("Access-Control-Allow-Headers",token);
			    },
		        data:data,
		        success:function(res){
		            ss=res
		            console.log(res);
		        },
		        error:function(res){
		            console.log(res)
		        }
		    });
		    return ss;
    }
 function getFormatTime(times){
	var format_time=times.slice(0,4)+"-"+times.slice(4,6)+"-"+times.slice(6,8)
	+" "+times.slice(8,10)+":"+times.slice(10,12)+":"+times.slice(12,14);
	return format_time;
}
  function qijin(data,token){
    	var ss="";
    	$.ajax({
		        url:urltou+"/ws/agent/setCanuse",
		        type:"POST",
		        async:false,
		        contentType:"application/json",
		        beforeSend: function(request){
			        request.setRequestHeader("Access-Control-Allow-Headers",token);
			    },
		        data:data,
		        success:function(res){
		            ss=res
		            console.log(res);
		        },
		        error:function(res){
		            console.log(res)
		        }
		    });
		    return ss;
    }
function ajax(url,data,token){
	var datas=""
	$.ajax({
		url:url,
		type:"POST",
		async:false,
		contentType:"application/json",
		beforeSend: function(request){
            request.setRequestHeader("Access-Control-Allow-Headers", token);
        },
        data:data,
		success:function(res){
		    datas=res;
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
	});
	return datas;
};
function ajaxGet(url,token){
	var datas="";
	$.ajax({
		url:url,
		type:"GET",
		async:false,
		beforeSend: function(request){
            request.setRequestHeader("Access-Control-Allow-Headers", token);
        },
        success:function(res){
         	datas=res;
        },
        error:function(res){
             console.log(res)
        }
	});
	return datas;
};
function GetUrlPara()
	　　{
	　　　　var url = document.location.toString();
	　　　　var arrUrl = url.split("?");	
	　　　　var para = arrUrl[1];
	　　　　return para;
	　　}
	
function RQcheck(RQ) {
            var date = RQ;
            var result = date.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);

            if (result == null)
                return false;
            var d = new Date(result[1], result[3] - 1, result[4]);
            return (d.getFullYear() == result[1] && (d.getMonth() + 1) == result[3] && d.getDate() == result[4]);

        }	
function is_weixin() {
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) == "micromessenger") {
		return true;
	}else{
		return false;
	}

}	
var wait=60;
function time() {  
         if (wait == 0) {  
            $("#fyzm").attr("disabled",false);            
            $("#fyzm").val("获取验证码");  
            wait = 60;  
        } else {  
            $("#fyzm").attr("disabled", true);  
            $("#fyzm").val("重新发送(" + wait + ")");  
            wait--;  
            setTimeout(function() {  
                time()  
            },  
            1000)  
        } 
    } 
//保留两位小数
function returnFloat(value){
 var value=Math.round(parseFloat(value)*100)/100;
 var xsd=value.toString().split(".");
 if(xsd.length==1){
 value=value.toString()+".00";
 return value;
 }
 if(xsd.length>1){
 if(xsd[1].length<2){
  value=value.toString()+"0";
 }
 return value;
 }
}
var nicen="";
var wxhao="";
var shenfz="";
var token="";
var urltou="http://47.92.115.31/back";
angular.module('starter.controllers', [])
.controller('DashCtrl', function($scope,$state,$ionicLoading) {
	  $ionicLoading.show();
	  var pp =GetUrlPara();
	  var aa=pp.split("=")[1];
	  token = aa.split("#")[0];
	  console.log(token)
	  $scope.fenLidata="";
	var urls=urltou+'/ws/userInfo/getUserInfo';
	  var yhData = ajaxGet(urls,token);
	  var isSpecial=yhData.data.isSpecial;
      if(isSpecial=="true"){
      	      $scope.zahuzheng=0;
			  $scope.zahuxiaoshu=0;
			  //本月返利
			  $scope.byzheng=0;
			  $scope.byxiaoshu=0;
			  //上月
			  $scope.syzheng=0;
			  $scope.syxiaoshu=0;
			  $ionicLoading.hide();
      }else{
	  var urls = urltou+'/ws/profit/myFenli'
	  var data= ajaxGet(urls,token);
	  $scope.fenLidata=data.data;
	  console.log($scope.fenLidata)
	  $ionicLoading.hide();
	  //账户余额
	  if($scope.fenLidata.balance=='undefined'){
	  	alert("请重新登陆!")
	  }else{
		  var zhanghuyu=$scope.fenLidata.balance;
		  zhanghuyu=returnFloat(zhanghuyu);
		  $scope.zahuzheng=zhanghuyu.split(".")[0];
		  $scope.zahuxiaoshu=zhanghuyu.split(".")[1];
		  //本月返利
		  var byshu=returnFloat($scope.fenLidata.benShijiFanli);
		  $scope.byzheng=byshu.split(".")[0];
		  $scope.byxiaoshu=byshu.split(".")[1];
		  //上月
		  var sangyueshu=returnFloat($scope.fenLidata.preShijiFanli);
		  $scope.syzheng=sangyueshu.split(".")[0];
		  $scope.syxiaoshu=sangyueshu.split(".")[1];
	}
 }
	$scope.doRefresh = function() {
      var datas= ajaxGet(urls,token);
	  $scope.fenLidata=datas.data;
	  // 结束加载
      $scope.$broadcast('scroll.refreshComplete');
  };  
	  
	//计算返利比
	$scope.flbjsFn=function(){
		$state.go("tab.flbjs",{
	     })
	}
	//提现
  $scope.tiXianFn = function () {
   $state.go("tab.tixian",{
        qian: zhanghuyu
     })
  };
  //月份业绩
  $scope.yueJiFn=function(){
  	$state.go("tab.yuefen",{
            
    }); 
  };
  //代理业绩
  $scope.daiLiFn=function(){
  	$state.go("tab.daili",{
            
     })
  };  
})
.controller('flbjsCtrl',function($scope){
     $scope.img="./templates/1.png";
	$scope.goBackFn = function () {
      window.history.go(-1);
   };
})
.controller('tixianCtrl',function($scope,$state,$stateParams,$ionicPopup,$ionicLoading){
	 
	$ionicLoading.show();
	$scope.img="./templates/wx.png";
	$scope.img1="./templates/zfb.png";
	var urls=urltou+'/ws/userInfo/getUserInfo';
	var xinxData = ajaxGet(urls,token);
	var isSpecial=xinxData.data.isSpecial;
     if(isSpecial=="true"){
     	$scope.yuee = 0.0;
     }else{
     	var urls = urltou+'/ws/profit/myFenli';
	    var data= ajaxGet(urls,token);
	   $scope.yuee = data.data.balance;
     }
	$scope.wxh=xinxData.data.wx;
	$scope.zfb=xinxData.data.alipay;
	$scope.name=xinxData.data.name;
	$ionicLoading.hide();
	var types="";
	var typeshao="";
	$scope.doRefresh = function() {
      xinxData= ajaxGet(urls,token);
	  $scope.wxh=xinxData.data.wx;
	  $scope.zfb=xinxData.data.alipay;
	  $scope.name=xinxData.data.name;
	  // 结束加载
      $scope.$broadcast('scroll.refreshComplete');
   };  
	$scope.goBackFn = function () {
      window.history.go(-1);
   };
   $scope.wxFn=function(){
   	   types="wechatpay";
   	   typeshao=$scope.wxh;
   	  $("#wx").css("display","block");
   	  $("#zfb").css("display","none");
   }
   $scope.zfbFn=function(){
   	 types="alipay";
   	 typeshao=$scope.zfb;
   	 $("#wx").css("display","none");
   	 $("#zfb").css("display","block");

   }
	
    $scope.zifufangsFn=function(){
	   if($("#fangs").css("display")=="none"){
			$("#fangs").show();
		}else{
			$("#fangs").hide();
		}
    }
    $scope.tixianFn=function(){
     var qianshu = $("#txjine").val();
     if(qianshu<299.9){
     	var myPopup = $ionicPopup.show({
		    template: '<button ng-click="quedinFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">确定</button>',
		    title: '提现金额低于300，无法提现',
		    scope: $scope,
		   
		  });
		  $scope.quedinFn=function(){
		  	console.log("确定");
		  	if (myPopup) {
		　　　　myPopup.close();
		　　}
		  };
	   }else if(typeshao==null || typeshao==""){
	   	 if(types=="wechatpay"){
	   	 	alert("请输入微信号")
	   	 }else if(types=="alipay"){
	   	 	alert("请输入支付宝号")
	   	 }else{
	   	 	alert("请选择提现方式")
	   	 }
	   }else {
	   	var data ={"money":qianshu,"type":types,"cashAliOrwechat":typeshao} ;
        data= JSON.stringify(data);
		  $.ajax({
			url:urltou+'/ws/agent/applyCash',
			type:"POST",
			async:false,
			contentType:"application/json",
			beforeSend: function(request){
	            request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
	        data:data,
			success:function(res){
				if(res.status==0){
					var myPopup = $ionicPopup.show({
					    template: '<button ng-click="quedinFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">确定</button>',
					    title: '申请已提交后台，客服将在24小时内与您联系',
					    scope: $scope,
					   
					  });
					   $scope.quedinFn=function(){
					  	if (myPopup) {
					　　　　　myPopup.close();
					　　}
					  };
				  }else{
				  	alert(res.errorMessage);
				  }
			    location.reload();
			},
			error:function(res){
			    console.log(res)
			}
		}); 
		 
      }
    }
	$scope.lisiFn = function () {
     $state.go("tab.lisi",{
            
     })
   };
   $scope.gaiwxhFn=function (index) {
     $state.go("tab.gaiwxh",{
            index:index
     })
   };
})
.controller('gaiwxhCtrl',function($scope,$state,$stateParams,$ionicLoading){
	var index= decodeURIComponent($stateParams.index);
	$scope.goBackFn = function () {
      window.history.go(-1);
    };
    $scope.baocunFn=function(){
    	console.log(index);
    	var hao = $("#wxh").val();
    	if(index==1){
	    	if(hao==""){
	    		alert("微信号不能为空")
	    	}else{
	    		var data ={"weChat":hao} ;
			    data= JSON.stringify(data);
				var statu=gaifn(data,token);
	    		$state.go("tab.tixian",{
	    			shu:hao
	           })
	    		location.reload()
	    	}
    	}else if(index==2){
    		if(hao==""){
	    		alert("支付宝号不能为空")
	    	}else{
	    		var data ={"alipay":hao} ;
			    data= JSON.stringify(data);
				var statu=gaifn(data,token);
	    		$state.go("tab.tixian",{
	    			shu:hao
	          })
	    		location.reload()
	    	}
    	}else{
    		if(hao==""){
	    		alert("名字不能为空")
	    	}else{
	    		var data ={"name":hao} ;
			    data= JSON.stringify(data);
				var statu=gaifn(data,token);
	    		$state.go("tab.tixian",{
	    			shu:hao
	          })
	    	 location.reload()
	    	}
    	}
    	
    }
})
.controller('lisiCtrl',function($scope,$timeout,$ionicLoading){
	$ionicLoading.show();
	$scope.moredata=false;
	$scope.count=1;
	$scope.goBackFn = function () {
      window.history.go(-1);
    };
    $scope.tixianjlData=[];
    var data ={"size":10,"pageNo":1} ;
     data= JSON.stringify(data);
	$.ajax({
            url:urltou+"/ws/agent/agentCashOut",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers",token);
	        },
            data:data,
            success:function(res){
                 $scope.tixianjlData=res.data;
                  for(var i=0;i<$scope.tixianjlData.length;i++){
                  	if($scope.tixianjlData[i].status==0){
                  		$scope.tixianjlData[i].status="已申请";
                  	}else if($scope.tixianjlData[i].status==1){
                  		$scope.tixianjlData[i].status="已到账";
                  	}else{
                  		$scope.tixianjlData[i].status="已返回";
                  	}
                  	if($scope.tixianjlData[i].type=="alipay"){
                  		$scope.tixianjlData[i].type="提现到支付宝"
                  	}else{
                  		$scope.tixianjlData[i].type="提现到微信"
                  	}
                  }
                  $ionicLoading.hide();
                  console.log(res);
            },
            error:function(res){
                console.log(res)
            }
       });
       
    //下拉刷新
   $scope.doRefresh = function() {
       $.ajax({
            url:urltou+"/ws/agent/agentCashOut",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers",token);
	        },
            data:data,
            success:function(res){
                 $scope.tixianjlData=res.data;
                  for(var i=0;i<$scope.tixianjlData.length;i++){
                  	if($scope.tixianjlData[i].status==0){
                  		$scope.tixianjlData[i].status="已申请";
                  	}else if($scope.tixianjlData[i].status==1){
                  		$scope.tixianjlData[i].status="已到账";
                  	}else{
                  		$scope.tixianjlData[i].status="已返回";
                  	}
                  	if($scope.tixianjlData[i].type=="alipay"){
                  		$scope.tixianjlData[i].type="提现到支付宝"
                  	}else{
                  		$scope.tixianjlData[i].type="提现到微信"
                  	}
                  }
                  // 结束加载
               $scope.$broadcast('scroll.refreshComplete');
            },
            error:function(res){
                console.log(res)
            }
       });
       
	  
   };    
       
   //上拉加载  
  $scope.loadMore = function() {
    $scope.count += 1;
    var datas ={"size":10,"pageNo": $scope.count} ;
        datas= JSON.stringify(datas);
    var time = "";
    $.ajax({
            url:urltou+"/ws/agent/agentCashOut",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:datas,
            success:function(res){
                 if(res.data.length<=0){
                 	$scope.moredata=true;
                 }else{
                 	$scope.moredata=false;
                 }
                for(var i=0;i<res.data.length;i++){
                  	if(res.data[i].status==0){
                  		res.data[i].status="已申请";
                  	}else if(res.data[i].status==1){
                  		res.data[i].status="已到账";
                  	}else{
                  		res.data[i].status="已返回";
                  	}
                  	if(res.data[i].type=="alipay"){
                  		res.data[i].type="提现到支付宝"
                  	}else{
                  		res.data[i].type="提现到微信"
                  	}
                  }
		        $scope.tixianjlData = $scope.tixianjlData.concat(res.data);
		       
		        // 结束加载
		        tiem = $timeout(function () {
		          $scope.$broadcast('scroll.infiniteScrollComplete');
		        }, 2000);
            },
            error:function(res){
                console.log(res)
            }
       });
  };
})
.controller('yuefenCtrl', function($scope,$http,$ionicLoading) {
	$ionicLoading.show();
	$scope.yueFn=function(){
			var currYear = (new Date()).getFullYear();	
			var opt={};
			opt.date = {preset : 'date'};
			//opt.datetime = { preset : 'datetime', minDate: new Date(2012,3,10,9,22), maxDate: new Date(2014,7,30,15,44), stepMinute: 5  };
			opt.datetime = {preset : 'datetime'};
			opt.time = {preset : 'time'};
			opt.default = {
				theme: 'android-ics light',
		        display: 'modal', 
		        mode: 'scroller', 
				lang:'zh',
				dateFormat: 'yyyy-mm',
		        startYear:currYear - 10,
		        endYear:currYear + 10 
			};
	    	$("#appDate").val('').scroller('destroy').scroller($.extend(opt['date'], opt['default']));

	}
//	$scope.yue=['1','2','3','4','5','6','7','8','9','10','11','12'];
	var mydata = new Date();
	$scope.name= mydata.getFullYear();
	$scope.month = mydata.getMonth()+1;
	if($scope.month<10){
		$scope.month="0"+$scope.month
	}
	$scope.sous = $scope.name+"-"+$scope.month;
	var datas =$scope.sous;

    $scope.souyueData="";
	$.ajax({
            url:urltou+"/ws/profit/getMonthBenifit",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:datas,
            success:function(res){
            	if(res.status==0){
            		$scope.souyueData=res.data;
            		$ionicLoading.hide();
                   console.log(res)
            	}else{
            		alert(res.errorMessage);
            		$ionicLoading.hide();
            	}   
            },
            error:function(res){
                console.log(res)
                $ionicLoading.hide();
            }
     });
    $scope.yuesousuoFn=function(){ 
    $ionicLoading.show();
    var data =$("#times").val();
//   data= JSON.stringify(data);
	$.ajax({
            url:urltou+"/ws/profit/getMonthBenifit",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
                if(res.status==0){
            		$scope.souyueData=res.data;
            		$ionicLoading.hide();
                   console.log(res)
            	}else{
            		alert(res.errorMessage);
            		$ionicLoading.hide();
            	} 
            },
            error:function(res){
            	$ionicLoading.hide();
                console.log(res)
            }
        })
		
    	console.log($("#times").val());
    	
    }
	$scope.goBackFn = function () {
      window.history.go(-1);
   };
   
})
.controller('dailiCtrl', function($scope,$http,$timeout,$ionicLoading) {
    $ionicLoading.show();
    var urls=urltou+"/ws/profit/getDirectChildFanliList";
	$scope.tixiandlData=[];
	$scope.moredata=false;
	$scope.count=1;
	var index="1";
    var data ={"flag":index,"size":10,"pageNo":1};
    data= JSON.stringify(data);
	var tixin=ajax(urls,data,token);
	$scope.tixiandlData=tixin.data.result;
	$ionicLoading.hide();
    $scope.goBackFn = function () {
       window.history.go(-1);
    };  
    $scope.data="";
    $scope.jinriFn=function(){
    	$ionicLoading.show();
    	index="1";
    	data ={"flag":"1","size":10,"pageNo":1};
    	data= JSON.stringify(data);
    	tixin=ajax(urls,data,token);
    	$scope.tixiandlData=tixin.data.result;
        $ionicLoading.hide();
    };
    $scope.benyueFn=function(){
    	$ionicLoading.show();
    	index="2";
    	data ={"flag":"2","size":10,"pageNo":1};
    	data= JSON.stringify(data);
    	tixin=ajax(urls,data,token);
    	$scope.tixiandlData=tixin.data.result;
        $ionicLoading.hide();
    };
    $scope.dailisFn=function(){
    	$ionicLoading.show();
    	index="3";
    	data ={"flag":"3","size":10,"pageNo":1};
        data= JSON.stringify(data);
    	tixin=ajax(urls,data,token);
    	$scope.tixiandlData=tixin.data.result;
      	$ionicLoading.hide();
    };
    $scope.fanlimxFn=function(){
    	$ionicLoading.show();
    	index="4";
    	data ={"flag":"4","size":10,"pageNo":1};
    	data= JSON.stringify(data);
    	tixin=ajax(urls,data,token);
    	$scope.tixiandlData=tixin.data.result;
    	$ionicLoading.hide();
    };
    //上拉加载  
  $scope.loadMore = function() {
    $scope.count += 1;
    var datas ={"flag":index,"size":10,"pageNo": $scope.count} ;
        datas= JSON.stringify(datas);
    var time = "";
    $.ajax({
            url:urls,
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:datas,
            success:function(res){
                 if(res.data.result==null){
                 	$scope.moredata=true;
                 }else{
                 	$scope.moredata=false;
                 }
		        $scope.tixiandlData = $scope.tixiandlData.concat(res.data.result);
		       
		        // 结束加载
		        tiem = $timeout(function () {
		          $scope.$broadcast('scroll.infiniteScrollComplete');
		        }, 2000);
            },
            error:function(res){
                console.log(res)
            }
       });
  };
	
	
})
//综合
.controller('ChatsCtrl', function($scope, $state,$ionicLoading) {
 
  $scope.julibuFn = function () {
      $state.go("tab.zonghe-julebu",{
            
      })
   };
   $scope.goumaikaFn = function () {
      $state.go("tab.zonghe-gouka",{
            
      })
   };
   $scope.dailixxiFn = function () {
      $state.go("tab.zonghe-dailixxi",{
            
      })
   };
   $scope.jiagouFn = function () {
      $state.go("tab.zonghe-jiagou",{
            
      })
   };
   $scope.baobiaoFn = function () {
      $state.go("tab.zonghe-baobiao",{
            
      })
   };
  
})
//综合-俱乐部
.controller('ZongheJulebuCtrl', function($scope,$ionicPopup, $stateParams,$state,$ionicLoading) {
  $ionicLoading.show();
  var urls =urltou+'/ws/agent/getClubMsg';
  var datas=ajaxGet(urls,token);
  $scope.julebuxx=datas.data;
  $scope.julebuzixx="";
  $scope.count=1;
  var data ={"size":10,"pageNo":1} ;
     data= JSON.stringify(data);
	$.ajax({
            url:urltou+"/ws/user/getClumMember",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers",token);
	        },
            data:data,
            success:function(res){
                  $scope.julebuzixx=res.data;                 
                  console.log(res);
if($scope.julebuzixx.length==0){
       	$("#wjxx").css("display","block")
       }else{
       	$("#wjxx").css("display","none")
       }
                  $ionicLoading.hide();
            },
            error:function(res){
                console.log(res);
                $ionicLoading.hide();
            }
       });
  
  //下拉刷新
   $scope.doRefresh = function() {
   	  datas=ajaxGet(urls,token);
      $scope.julebuxx=datas.data;
      $.ajax({
            url:urltou+"/ws/user/getClumMember",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers",token);
	        },
            data:data,
            success:function(res){
                  $scope.julebuzixx=res.data;                 
                  console.log(res);
            },
            error:function(res){
                console.log(res);
            }
       });
         // 结束加载
        $scope.$broadcast('scroll.refreshComplete');  
   }
  $scope.loadMore = function() {
    $scope.count += 1;
    var datas ={"size":10,"pageNo": $scope.count} ;
        datas= JSON.stringify(datas);
    var time = "";
    $.ajax({
            url:urltou+"/ws/user/getClumMember",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:datas,
            success:function(res){
                 if(res.data.length<=0){
                 	$scope.moredata=true;
                 }else{
                 	$scope.moredata=false;
                 }
		        $scope.julebuzixx = $scope.julebuzixx.concat(res.data);
		       
		        // 结束加载
		        tiem = $timeout(function () {
		          $scope.$broadcast('scroll.infiniteScrollComplete');
		        }, 2000);
            },
            error:function(res){
                console.log(res)
            }
       });
  };
 
 $scope.goBackFn = function () {
        window.history.go(-1);
  };
  $scope.congzjiluFn=function(){
    	$state.go("tab.zonghe-jilu",{
            
     })
  };
    
 $scope.jlbsousuoFn= function() {
 	$ionicLoading.show();
 	$scope.vals = $("#jlbinput").val();
	if($scope.vals==""){
 		alert("ID不能为空")
        $ionicLoading.hide();
 	}else if($scope.vals.length>6){
 		alert("请输入正确ID")
        $ionicLoading.hide();
 	}else{
 	$scope.jlbsousuoData="";
 	var data =$scope.vals;
	$.ajax({
            url:urltou+"/ws/user/getUserMsgByMunber",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers",token);
	        },
            data:data,
            success:function(res){
		if(res.status ==0 ){
                 
                  $scope.jlbsousuoData=res.data;
                  if(res.data.sign=="1"){
                  	var ss=[{"uid":$scope.jlbsousuoData.uid,"name":$scope.jlbsousuoData.nikeName,"card":$scope.jlbsousuoData.card}];
                  	console.log(ss)
                  	 $scope.julebuzixx=ss;
                  	 $ionicLoading.hide();
                  }else if(res.data.sign=="4"){
                  	$ionicLoading.hide();
                 		 var myPopup = $ionicPopup.show({
				    template: '<div style=" height: 10rem;text-align: center;font-size: 18px;padding-top:20px;background:white">玩家昵称：'+$scope.jlbsousuoData.nikeName+'<br/>ID:'+$scope.jlbsousuoData.uid+'<br/><input id="fangka" placeholder="请输入充值房卡数量" style="border: 1px solid #8e8e8e;height:2.5rem;border-radius:20px; padding-left:40px;margin-top:10px;">'
				   +' <p style="color:#b1b1b1;font-size: 16px;margin-top:10px">注：此玩家非俱乐部成员，是否确定为他充值</p></div>'
				   +'<button ng-click="quedinFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">确定</button>'
				   +'<button ng-click="quxiaoFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">取消</button>',
				    title: '充值房卡',
				    cssClass: '',
				    scope: $scope,
				   
				  });
				  $scope.quedinFn=function(){
				  	var shul=$("#fangka").val();
				  	var data ={"cartSum":shul,"uid":$scope.jlbsousuoData.uid} ;
                    		 data= JSON.stringify(data);
				   $.ajax({
			            url:urltou+"/ws/agent/chongzhi",
			            type:"POST",
			            contentType:"application/json",
			            beforeSend: function(request){
				           request.setRequestHeader("Access-Control-Allow-Headers",token);
				        },
			            data:data,
			            success:function(res){ 
			            	if(res.status==0){
			            		alert("充值成功")
			            	}else{
			            	alert(res.errorMessage)
			            	}
			                  console.log(res);
			            },
			            error:function(res){
			                console.log(res)
			            }
			       });
					console.log("确定"+shul);
				  	if (myPopup) {
				　　　　　myPopup.close();
				　　}
				  };
				   $scope.quxiaoFn=function(){
				  	console.log("取消");
				  	if (myPopup) {
				　　　　　myPopup.close();
				　　}
				  }
             		 }else if($scope.jlbsousuoData.sign=="2"){
              			$ionicLoading.hide();
                 			alert("此用户为其他俱乐部成员,不可充值");
             		 }else{
              			$ionicLoading.hide();
              			alert("无此用户");
             		 }
  		}else if(res.status==204){
				$ionicLoading.hide();
				alert("无此用户");
        	        }else{
			 $ionicLoading.hide();
                 alert(res.errorMessage)
		   }
            },
            error:function(res){
                console.log(res)
            }
      });
   }
 };
 $scope.qunzhuFn=function(index){
 var num = $scope.julebuzixx[index].uid;
 var remarks=$scope.julebuzixx[index].remarks;
 if(remarks==null){
 	remarks='';
 }
 console.log(remarks)
  $scope.data = {};
  // 自定义弹窗
  var myPopup = $ionicPopup.show({
    template: '<input id="beizi"  placeholder="请输入备注" value="'+remarks+'" style="border: 1px solid #8e8e8e;height:7rem;padding-left:70px;" >'
   +' <button ng-click="quedinFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">确定</button>'
   +'<button ng-click="quxiaoFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">取消</button>',
    title: '备注',
    cssClass: '',
    scope: $scope,
   
  });
  $scope.quedinFn=function(){
  	var beizu=$("#beizi").val();
  	var data ={"number":num,"remarks":beizu} ;
     data= JSON.stringify(data);
	$.ajax({
            url:urltou+"/ws/user/uptRemarks",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers",token);
	        },
            data:data,
            success:function(res){               
                  console.log(res);
                 if(res.status==0){
            		alert("备注成功")
            	}else{
            		alert(res.errorMessage)
            	}
            },
            error:function(res){
                console.log(res)
            }
       });
  	 console.log("确定");
  	 if (myPopup) {//myPopup即为popup
　　　　　 myPopup.close();
　　 }
  };
   $scope.quxiaoFn=function(){
  	console.log("取消");
  	if (myPopup) {
　　　　　myPopup.close();
　　}
  }
 };
 $scope.guanliFn=function(index){
 	  var uid = $scope.julebuzixx[index].uid;
      var nikeName=$scope.julebuzixx[index].name;
	  var myPopup = $ionicPopup.show({
	    template: '<div style=" height: 7rem;text-align: center;font-size: 18px;padding-top:2rem;background:white">玩家昵称：'+nikeName+'<br/>ID:'+uid+'</div>'
	   +'<button  ng-click="quedinFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">移出俱乐部</button>'
	   +'<button ng-click="quxiaoFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">取消</button>',
	    title: '成员管理',
	    cssClass: '',
	    scope: $scope,
	   
	  });
	  $scope.quedinFn=function(){
	  var data =uid ;
	   data= JSON.stringify(data);
	   //alert("aaa");
	  $.ajax({
            url:urltou+"/ws/user/removeClub",
            type:"POST",
	   async:false,
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers",token);
	        },
            data:data,
            success:function(res){ 
            	 //alert("bbbb");
            	if(res.status==0){
            		alert("移除成功")
            	}else{
            		alert(res.errorMessage)
            	}
                  console.log(res);
            },
            error:function(res){
            	// alert("ccc");
		alert("连接出错，请重试")
                console.log(res)
            }
       });
        //alert("ddd");
	  	if (myPopup) {
	　　　　　myPopup.close();
	　　}
	  	location.reload();
	  };
	   $scope.quxiaoFn=function(){
	  	console.log("取消");
	  	if (myPopup) {
	　　　　　myPopup.close();
	　　}
	  }
   };
 $scope.chongzhiFn=function(index){
  	  var uid = $scope.julebuzixx[index].uid;
      var nikeName=$scope.julebuzixx[index].name;
      var card=$scope.julebuzixx[index].card;
	 var myPopup = $ionicPopup.show({
	    template: '<div style=" height: 6rem;text-align: center;font-size: 18px;padding-top:1rem;background:white">玩家昵称:'+nikeName+'<br/>ID:'+uid+'<br/>玩家剩余房卡数:'+card+'</div><input id="cks"  placeholder="请输入充值房卡数量" style="border: 1px solid #8e8e8e;height:2.5rem;border-radius:20px; padding-left:40px;">'
	   +' <button ng-click="quedinFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">确定</button>'
	   +'<button ng-click="quxiaoFn()" class="button" style=" width: 100px;margin-top:10px; background-color:white;color:#333333;border: 1px solid #8e8e8e;width:231px">取消</button>',
	    title: '充值房卡',
	    cssClass: '',
	    scope: $scope,
	   
	  });
	  $scope.quedinFn=function(){
	  	var kashu=$("#cks").val();
	  	var data ={"cartSum":kashu,"uid":uid} ;
       data= JSON.stringify(data);
	   $.ajax({
            url:urltou+"/ws/agent/chongzhi",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers",token);
	        },
            data:data,
            success:function(res){ 
                   console.log(res);
                   if(res.status==0){
                   	alert("充值成功")
                   }else{
                   	alert(res.errorMessage)
                   }
                 location.reload()
            },
            error:function(res){
                console.log(res)
            }
       });
	  	if (myPopup) {
	　　　　　myPopup.close();
	　　}
	  };
	   $scope.quxiaoFn=function(){
	  	console.log("取消");
	  	if (myPopup) {
	　　　　　myPopup.close();
	　　}
	  }
  };
})
//充值记录
.controller('ZonghejiluCtrl', function($scope, $stateParams,$http,$timeout,$ionicLoading) {
 $ionicLoading.show();
 $scope.moredata = false;
 $scope.goBackFn = function () {
      window.history.go(-1); 
   };
    $scope.count = 1;
   	var data ={"size":10,"pageNo":1} ;
     data= JSON.stringify(data);
     $scope.congziData="";
	$.ajax({
            url:urltou+"/ws/agent/palyerCZList",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
                 $scope.congziData=res.data;
                              if(res.status==0){
                 
		for(var i=0;i<$scope.congziData.length;i++){
                  var time=$scope.congziData[i].datetime;
	          $scope.congziData[i].datetime=getFormatTime(time);
		       console.log(res.data)
                    }
                   $ionicLoading.hide();
		}else{
            		$ionicLoading.hide();
            		alert(res.errorMessage);	
            	}
                  
                  if($scope.congziData.length==0){
                  	$("#wcz").css("display","block");
                  }else{
                  	$("#wcz").css("display","none");
                  }
//                console.log($scope.congziData[1].uid);
            },
            error:function(res){
            	$ionicLoading.hide();
                console.log(res)
            }
       });
  
  //上拉加载
  $scope.doRefresh = function() {
  	 $.ajax({
            url:urltou+"/ws/agent/palyerCZList",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
                  $scope.congziData=res.data;
				  for(var i=0;i<$scope.congziData.length;i++){
                  		var time=$scope.congziData[i].datetime;
                  		$scope.congziData[i].datetime=getFormatTime(time);

		                console.log(time)
                    }
            },
            error:function(res){
                console.log(res)
            }
      });  
  	$scope.$broadcast('scroll.refreshComplete');
                  	 
   }
  $scope.loadMore = function() {
    $scope.count += 1;
    var datas ={"size":10,"pageNo": $scope.count} ;
        datas= JSON.stringify(datas);
    var time = "";
    $.ajax({
            url:urltou+"/ws/agent/palyerCZList",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:datas,
            success:function(res){
                 if(res.data.length<=0){
                 	$scope.moredata=true;
                 }else{
                 	$scope.moredata=false;
                 }
                 for(var i=0;i<res.data.length;i++){
                  		var time=res.data[i].datetime;
                  		 res.data[i].datetime=getFormatTime(time);


		                console.log(time)
                    }
		        $scope.congziData = $scope.congziData.concat(res.data);
		       
		        // 结束加载
		        tiem = $timeout(function () {
		          $scope.$broadcast('scroll.infiniteScrollComplete');
		        }, 2000);
            },
            error:function(res){
                console.log(res)
            }
       });
  };
      
})
//综合-购买房卡
.controller('ZongheGoukaCtrl', function($scope, $stateParams,$state,$ionicLoading) {
  var urls = urltou+'/ws/userInfo/getUserInfo'
  var data= ajaxGet(urls,token);
   $scope.fangkaData=data.data;
   console.log($scope.fangkaData)
 $scope.goBackFn = function () {
      window.history.go(-1);
    };
  $scope.dinyiTaocFn=function(){
  	if($("#isXian").css("display")=="none"){
		 $("#isXian").show();
	}else{
		$("#isXian").hide();
	}
  };
  $scope.taocanxqFn=function(index){
  	var shul=$("#zdyl").val();
  	if(index==7){
  		if(shul<200){
  			alert("购卡数量大于等于最小套餐量")
  		}else{
  			window.location.href ='taocanx.html?name='+token+'&index='+index+'d'+shul; 		
  		}
  	}else{
  	  	window.location.href ='taocanx.html?name='+token+'&index='+index+'d1';
     
  	}
  }
  
})
//综合-套餐详情
.controller('ZonghetaocanxqCtrl', function($scope, $stateParams,$ionicLoading) {
	
})
//综合-代理信息
.controller('ZonghedailixxiCtrl', function($scope,$ionicLoading, $stateParams,$state,$timeout) {
    $ionicLoading.show();
    $scope.dailixData="";
    $scope.count=1;
    var data ={"size":10,"pageNo":1} ;
     data= JSON.stringify(data);
	$.ajax({
            url:urltou+"/ws/agent/getDirectChild",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
            	if(res.status==0){
            		$scope.dailixData=res.data;
                    for(var i=0;i<$scope.dailixData.length;i++){
	                   	var phone= $scope.dailixData[i].phone
	                    var myphone = phone.substr(3, 4);  
	          			$scope.dailixData[i].phone= phone.replace(myphone, "****"); 
	                    var phones= $scope.dailixData[i].childPhone;
	                    var myphones = phones.substr(3, 4);  
	          			$scope.dailixData[i].childPhone= phones.replace(myphones, "****");
	                }
                   $ionicLoading.hide();
            	}else{
            		$ionicLoading.hide();
            		alert(res.errorMessage);	
            	}
                console.log(res);
            },
            error:function(res){
                console.log(res)
                $ionicLoading.hide();
            }
       });
   if($scope.dailixData.length==0){
   	$("#wdlx").css("display","block")
   }else{
   	$("#wdlx").css("display","none")
   }
  $scope.doRefresh = function() {
  	 $.ajax({
            url:urltou+"/ws/agent/getDirectChild",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
            	if(res.status==0){
            		$scope.dailixData=res.data;
                    for(var i=0;i<$scope.dailixData.length;i++){
	                   	var phone= $scope.dailixData[i].phone
	                    var myphone = phone.substr(3, 4);  
	          			$scope.dailixData[i].phone= phone.replace(myphone, "****"); 
	                    var phones= $scope.dailixData[i].childPhone;
	                    var myphones = phones.substr(3, 4);  
	          			$scope.dailixData[i].childPhone= phones.replace(myphones, "****");
	                }
            	}else{
            		alert(res.errorMessage);	
            	}
                console.log(res);
            },
            error:function(res){
                console.log(res)
            }
       });
  	$scope.$broadcast('scroll.refreshComplete');
  }
  
  $scope.loadMore = function() {
    $scope.count += 1;
    var datas ={"size":10,"pageNo": $scope.count} ;
        datas= JSON.stringify(datas);
    var time = "";
    $.ajax({
            url:urltou+"/ws/agent/getDirectChild",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:datas,
            success:function(res){
                 if(res.data.length<=0){
                 	$scope.moredata=true;
                 }else{
                 	$scope.moredata=false;
                 }
                  for(var i=0;i<res.data.length;i++){
                   	var phone= res.data[i].phone
                    var myphone = phone.substr(3, 4);  
          			res.data[i].phone= phone.replace(myphone, "****"); 
                    var phones= res.data[i].childPhone;
                    var myphones = phones.substr(3, 4);  
          			res.data[i].childPhone= phones.replace(myphones, "****");
                  }
		        $scope.dailixData = $scope.dailixData.concat(res.data);
		       
		        // 结束加载
		        tiem = $timeout(function () {
		          $scope.$broadcast('scroll.infiniteScrollComplete');
		        }, 2000);
            },
            error:function(res){
                console.log(res)
            }
       });
  };
   
  $scope.goBackFn = function () {
      window.history.go(-1);
    };
  $scope.dlsousFn=function(){
       $state.go("tab.zonghe-sousuo",{           
     })
  };
   $scope.xinzendailFn=function(){
   	   $state.go("tab.zonghe-xinzen",{
            
     })
   }
   
})
//综合-新增代理
.controller('ZonghexinzenCtrl', function($scope, $stateParams,$state,$ionicLoading) {
   $scope.goBackFn = function () {
      window.history.go(-1);
   };
    var phone = "";
    
    //发送验证码
    $scope.fayanzmaFn=function(){
      time();
     phone = $("#sjhao").val();
    	console.log("发送验证码");
    	 $.ajax({
            url:urltou+"/ws/account/verifyCode",
            type:"POST",
            contentType:"application/json",
            data:{"phone":phone},
            success:function(res){
                    console.log(res);
            },
            error:function(res){
                console.log(res)
            }
       })	 
    }
    //新增代理
    $scope.xinzenFn=function(){
    var name=$("#nicen").val();
    var yzm = $("#yzma").val();
    var passwords=$("#password").val();
    var data = {nikeName:name,phone:phone,password:passwords,verifyCode:yzm,type:2};
       data= JSON.stringify(data);
       $.ajax({
            url:urltou+"/ws/account/addAgent ",
            type:"POST",
            contentType:"application/json",
            data:data,
            beforeSend: function(request){
		       request.setRequestHeader("Access-Control-Allow-Headers", token);
		    },
            success:function(res){
            	if(res.status==0){
            		alert(res.errorMessage);
//          		window.location.href ='denglu.html';
            	    window.history.go(-1);
                    location.reload()
            	}else{
            		alert(res.errorMessage);
            	}
            },
            error:function(res){
                console.log(res)
            }
        })
    }
      
})
//综合-新增代理搜索
.controller('ZonghesousuoCtrl', function($scope, $stateParams,$ionicLoading) {
   $scope.goBackFn = function () {
      window.history.go(-1);
    };
    $scope.sousuoFn=function(){
    	$ionicLoading.show();
    	$("#tjr").css("display","none");
	    var shuju=$("#dlsousuo").val();
//	    var data ={"phone":shuju} ;
//	     data= JSON.stringify(data);
	     $scope.dlsousData="";
		$.ajax({
	            url:urltou+"/ws/agent/oneDirectChild",
	            type:"POST",
	            contentType:"application/json",
	            beforeSend: function(request){
		           request.setRequestHeader("Access-Control-Allow-Headers", token);
		        },
	            data:shuju,
	            success:function(res){
					if(res.status==0){
						   $scope.dlsousData=res.data;	
		                   	var phone= $scope.dlsousData.phone;
		                    var myphone = phone.substr(3, 4);  
		          			$scope.dlsousData.phone= phone.replace(myphone, "****"); 
		                    var phones= $scope.dlsousData.childPhone;
		                    var myphones = phones.substr(3, 4);  
		          			$scope.dlsousData.childPhone= phones.replace(myphones, "****");              
						   console.log($scope.dlsousData.phone)
		                  $("#tjr").css("display","block");
		                $ionicLoading.hide();
		               
					}else{
						$("#tjr").css("display","none");
						alert(res.errorMessage);
						$ionicLoading.hide();
					}
						
            },
            error:function(res){
                console.log(res);
                $ionicLoading.hide();
            }
       });
  };
   
  $scope.goBackFn = function () {
      window.history.go(-1);
    };
  $scope.dlsousFn=function(){
       $state.go("tab.zonghe-sousuo",{           
     })
  };
   $scope.xinzendailFn=function(){
   	   $state.go("tab.zonghe-xinzen",{
            
     })
   }
   
})
						
//综合-代理架构
.controller('ZonghejiagouCtrl', function($scope, $stateParams,$ionicLoading) {
  $ionicLoading.show();
  var urls=urltou+'/ws/agent/agentPolicy'
   var dljg=ajaxGet(urls,token);
    $scope.dljgData=dljg.data;
    if($scope.dljgData.length==0){
    	$("#wdl").css("display","block")
    }else{
    	$("#wdl").css("display","none")
    }
    console.log(dljg);
    $ionicLoading.hide();
  $scope.goBackFn = function () {
      window.history.go(-1);
    };
    $scope.kaiqiFn=function(index,event){
    	var pid= $scope.dljgData[index].pid;
    	var data ={"agentId":pid,"canuse":"true"} ;
        data= JSON.stringify(data);
        var res =qijin(data,token);
        if(res.status==0){
    	  $(event.target).attr("disabled",true);
    	  $(event.target).next().attr("disabled",false);
     	}else{
     		alert(res.errorMessage)
     	}
    };
    $scope.jinzhiFn=function(index,event){
    	var pid= $scope.dljgData[index].pid;
    	var data ={"agentId":pid,"canuse":"false"} ;
        data= JSON.stringify(data)
        var res1 =qijin(data,token);
       console.log(res1)
       if(res1.status==0){
       	 $(event.target).attr("disabled",true);
    	 $(event.target).prev().attr("disabled",false);
       }else{
       	 alert(res1.errorMessage)
       }
    }
})
//综合-每日报表
.controller('ZonghebaobiaoCtrl', function($scope,$ionicPopup, $stateParams,$ionicLoading) {
   $scope.goBackFn = function () {
      window.history.go(-1);
   };
    //查询
    
    
    $scope.kaisiFn=function(){
    	var currYear = (new Date()).getFullYear();	
			var opt={};
			opt.date = {preset : 'date'};
			//opt.datetime = { preset : 'datetime', minDate: new Date(2012,3,10,9,22), maxDate: new Date(2014,7,30,15,44), stepMinute: 5  };
			opt.datetime = {preset : 'datetime'};
			opt.time = {preset : 'time'};
			opt.default = {
				theme: 'android-ics light', //皮肤样式
		        display: 'modal', //显示方式 
		        mode: 'scroller', //日期选择模式
				lang:'zh',
				dateFormat: 'yyyy-mm-dd',
		        startYear:currYear - 10, //开始年份
		        endYear:currYear + 10 //结束年份
			};

		$("#txtDate").val('').scroller('destroy').scroller($.extend(opt['date'], opt['default']));

    }
     $scope.jiesuFn=function(){
    	var currYear = (new Date()).getFullYear();	
			var opt={};
			opt.time = {preset : 'date'};
			opt.default = {
				theme: 'android-ics light', //皮肤样式
		        display: 'modal', //显示方式 
		        mode: 'scroller', //日期选择模式
				lang:'zh',
				dateFormat: 'yyyy-mm-dd',
		        startYear:currYear - 10, //开始年份
		        endYear:currYear + 10 //结束年份
			};

		$("#jiesu").val('').scroller('destroy').scroller($.extend(opt['time'], opt['default']));

    }
    
    
    
// $(function(){
// 	  var joeCalendar = new JoeCalendar();
//    joeCalendar.init("txtBeginDate","jiesu");
// 
// });
  $scope.baobiaochaFn=function(){
  	$ionicLoading.show();
  	if($("#txtDate").val()==""){
  		alert("请选择输入日期");
	   $ionicLoading.hide();
  	}else{
    var qishi =	$("#txtDate").val();
    qishi=qishi+" 00:00:00";
    var jieshu =$("#jiesu").val();
    jieshu=jieshu+" 00:00:00";
    console.log(qishi+"......"+jieshu);
    var data ={"beginTime":qishi,"endTime":jieshu} ;
     data= JSON.stringify(data);
     $scope.baobiaoData="";
	$.ajax({
            url:urltou+"/ws/profit/getTeamByTwoTime",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
                  if(res.status==0){
	                  $scope.baobiaoData=res.data;
	                  console.log(res);
	                  $ionicLoading.hide();
                  }else{
                  	alert(res.errorMessage);
                  	$ionicLoading.hide();
                  }
            },
            error:function(res){
                console.log(res);
                $ionicLoading.hide();
            }
        })
	}	
  }
})
//我的信息
.controller('AccountCtrl', function($scope,$state,$ionicLoading) {
//var xx =GetUrlPara();
//xx=xx.split("=")[1];
//var token = xx.split("#")[0];
  var urls=urltou+'/ws/userInfo/getUserInfo';
  var xinData = ajaxGet(urls,token);
  $scope.xinData=xinData.data;
  console.log(xinData)
$scope.xinxGaiFn=function(){
  	$state.go("tab.xinxi",{
            
     })
  };
  $scope.goukaFn=function(){
  	$state.go("tab.gouka",{
            
     })
  };
  $scope.julebuFn=function(){
  	$state.go("tab.julebu",{
            
     })
  };
  $scope.xieyiFn=function(){
  	$state.go("tab.xieyi",{
            
     })
  }
  $scope.gaimimaFn=function(){
  	window.location.href='http://mingmen.mingmenhuyu.com/back/czmima.html?name='+token 
  }
})
//个人信息修改
.controller('xinxiCtrl', function($scope,$state,$stateParams,$ionicLoading) {
	$ionicLoading.show();
	var urls=urltou+'/ws/userInfo/getUserInfo';
	var xinxData = ajaxGet(urls,token);
	$scope.gerenxxData=xinxData.data;
	$scope.nicen=xinxData.data.nikeName;
	$scope.sfz=xinxData.data.idCard;
	$scope.wxhao=xinxData.data.wx;
	console.log(xinxData);
	$ionicLoading.hide();
	$scope.doRefresh = function() {
	xinxData = ajaxGet(urls,token);
	$scope.gerenxxData=xinxData.data;
	$scope.nicen=xinxData.data.nikeName;
	$scope.sfz=xinxData.data.idCard;
	$scope.wxhao=xinxData.data.wx;
  	$scope.$broadcast('scroll.refreshComplete');
	}
  $scope.goBackFn = function () {
      window.history.go(-1);
    };
   $scope.gainicFn=function(){
    	$state.go("tab.gainic",{
            
       })
   }
   $scope.shenfzFn=function(){
   	  $state.go("tab.shenfenz",{
            
       })
   }
   $scope.gaiwxhFn=function(){
   	  $state.go("tab.weixinh",{
            
       })
   }
   
})
//改昵称
.controller('gainicCtrl', function($scope,$state,$ionicLoading) {
  $scope.goBackFn = function () {
      window.history.go(-1);
    };
   
  $scope.baocunFn=function(){	
       var nicen = $("#nicen").val();
    	if(nicen==""){
    		alert("昵称不能为空");
    	}else if(nicen.length>5){
    		alert("输入字符不能超过五个");
    	}else{
    	var data ={"nikeName":nicen} ;
		    data= JSON.stringify(data);
		var status=	gaifn(data,token);
		if(status.status==0){
          window.history.go(-1);
          location.reload()
		}else{
			alert(status.errorMessage);
		}
    		
    	}   
 }
})
//身份证
.controller('shenfenzCtrl', function($scope,$state,$ionicLoading) {
  $scope.goBackFn = function () {
      window.history.go(-1);
    };
  $scope.baocunFn=function(){	
       var sfz = $("#sfz").val();
    	if(sfz==""){
    		alert("身份证号不能为空");
    	}else{
    		var data ={"idCard":sfz} ;
		     data= JSON.stringify(data);
			var statu = gaifn(data,token);
			if(statu.status==0){
            window.history.go(-1);
            location.reload()
			}else{
				alert(status.errorMessage);
			}
    		
    	}   
 }
})
//微信号
.controller('weixinhCtrl', function($scope,$state,$ionicLoading) {
  $scope.goBackFn = function () {
      window.history.go(-1);
    };
  $scope.baocunFn=function(){	
       var wxhao = $("#wxhao").val();
    	if(wxhao==""){
    		alert("微信号不能为空");
    	}else{
    		var data ={"weChat":wxhao} ;
		    data= JSON.stringify(data);
			var statu=gaifn(data,token);
			if(statu.status==0){
			    window.history.go(-1);
			    location.reload()
			    
			}else{
				alert(status.errorMessage);
			}
    	}   
 }
})
//购卡记录
.controller('goukaCtrl', function($scope,$state,$timeout,$ionicLoading) {
	$ionicLoading.show();
	$scope.count=1;
	var data ={"size":10,"pageNo":1} ;
    data= JSON.stringify(data);
    var ss="";
    $scope.goukaData=ss;
	$.ajax({
            url:urltou+"/ws/agent/agentCZList",
            type:"POST",
		    async:false,
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
                  if(res.status==0){
                  	ss=res.data;
                  	$scope.goukaData=ss;
                  for(var i=0;i<$scope.goukaData.length;i++){
                  		var time=$scope.goukaData[i].createTime;
                  		 res.data[i].createTime= getFormatTime(time);
		                console.log(time);
		                if($scope.goukaData[i].payWay==1 || $scope.goukaData[i].payWay=='alipay'){
	          				$scope.goukaData[i].payWay="支付宝支付"
	          			}else if($scope.goukaData[i].payWay==2 || $scope.goukaData[i].payWay=='wechatpay'){
	          				$scope.goukaData[i].payWay="微信支付"
	          			}else{
					 $scope.goukaData[i].payWay="";
					}
                    }
                  	$ionicLoading.hide();
                  }else{
                  	 alert(res.errorMessage);
                  	 $ionicLoading.hide();
                  }
                  
                  console.log(res)
            },
            error:function(res){
                console.log(res);
                $ionicLoading.hide();
            }
       });
       if($scope.goukaData.length==0){
       	$("#wjl").css("display","block")
       }else{
       	$("#wjl").css("display","none")
       }
        $scope.doRefresh = function() {
      $.ajax({
            url:urltou+"/ws/agent/agentCZList",
            type:"POST",
		    async:false,
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
                  if(res.status==0){
                  	ss=res.data;
                  	$scope.goukaData=ss;
                  for(var i=0;i<$scope.goukaData.length;i++){
                  		var time=$scope.goukaData[i].createTime;
                  		 res.data[i].createTime= getFormatTime(time)
		                console.log(time);
		                if($scope.goukaData[i].payWay==1 || $scope.goukaData[i].payWay=='alipay'){
	          				$scope.goukaData[i].payWay="支付宝支付"
	          			}else if($scope.goukaData[i].payWay==2 || $scope.goukaData[i].payWay=='wechatpay'){
	          				$scope.goukaData[i].payWay="微信支付"
	          			}else{
					 $scope.goukaData[i].payWay="";
					}
		              }
                  	
                  }else{
                  	 alert(res.errorMessage);
                  }
                  
                  console.log(res)
            },
            error:function(res){
                console.log(res);
                $ionicLoading.hide();
            }
       });
         // 结束加载
     $scope.$broadcast('scroll.refreshComplete');  
                  	 
   }   

   $scope.loadMore = function() {
    $scope.count += 1;
    var datas ={"size":10,"pageNo": $scope.count} ;
        datas= JSON.stringify(datas);
    var time = "";
    $.ajax({
            url:urltou+"/ws/agent/agentCZList",
            type:"POST",
		    async:false,
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:datas,
            success:function(res){
                 if(res.data.length<=0){
                 	$scope.moredata=true;
                 }else{
                 	$scope.moredata=false;
                 }
                for(var i=0;i<res.data.length;i++){
                  		var time=res.data[i].createTime;
                                   res.data[i].createTime= getFormatTime(time)

		                if(res.data[i].payWay==1 || res.data[i].payWay=='alipay'){
	          				res.data[i].payWay="支付宝支付"
	          			}else if($scope.goukaData[i].payWay==2 || $scope.goukaData[i].payWay=='wechatpay'){
	          				$scope.goukaData[i].payWay="微信支付"
	          			}else{
					 $scope.goukaData[i].payWay="";
					}
                  	}
		        $scope.goukaData = $scope.goukaData.concat(res.data);
		        // 结束加载
		        tiem = $timeout(function () {
		          $scope.$broadcast('scroll.infiniteScrollComplete');
		        }, 2000);
            },
            error:function(res){
                console.log(res)
            }
       });
  };   
   $scope.goBackFn = function () {
       window.history.go(-1);
   };
   
    $scope.goukaFn=function(){
    	var currYear = (new Date()).getFullYear();	
			var opt={};
			opt.timed = {preset : 'date'};
			opt.default = {
				theme: 'android-ics light', //皮肤样式
		        display: 'modal', //显示方式 
		        mode: 'scroller', //日期选择模式
				lang:'zh',
				dateFormat: 'yyyy-mm-dd',
		        startYear:currYear - 10, //开始年份
		        endYear:currYear + 10 //结束年份
			};

		$("#gktime").val('').scroller('destroy').scroller($.extend(opt['timed'], opt['default']));
    }
   
   
  $scope.jilusousFn=function(){
  	$ionicLoading.show();
  	$scope.goukaData='';
  	$scope.counts = 1;
  	var goukatime=$("#gktime").val();
  	if (!RQcheck(goukatime)) {
  		   $ionicLoading.hide();
        alert("请输入正确的日期格式如:2017-01-01");
    }else{
     	
  	var data ={"size":10,"pageNo":1,"searchTime":goukatime};
     data= JSON.stringify(data);
	$.ajax({
            url:urltou+"/ws/agent/agentDateList",
            type:"POST",
		    async:false,
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
                  if(res.status==0){
                  	$scope.goukaData=res.data;
                  	 for(var i=0;i<$scope.goukaData.length;i++){
                  		var time=$scope.goukaData[i].createTime;
                  		$scope.goukaData[i].createTime= getFormatTime(time)

		                console.log(time)
                  	}
                  	 if($scope.goukaData.length==0){
       			$("#wjl").css("display","block")
      			 }else{
       			$("#wjl").css("display","none")
     			  }
                  	$ionicLoading.hide();
                  }else{
                  	 alert(res.errorMessage);
                  	 $ionicLoading.hide();
                  }
                  console.log(res)
            },
            error:function(res){
                console.log(res);
                $ionicLoading.hide();
            }
       });
     }   
   $scope.loadMore = function() {
    $scope.counts += 1;
    var datas ={"size":10,"pageNo": $scope.counts,"searchTime":goukatime} ;
        datas= JSON.stringify(datas);
    var time = "";
    $.ajax({
            url:urltou+"/ws/agent/agentDateList",
            type:"POST",
		    async:false,
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:datas,
            success:function(res){
                 if(res.data.length<=0){
                 	$scope.moredata=true;
                 }else{
                 	$scope.moredata=false;
                 }
               
		        $scope.goukaData = $scope.goukaData.concat(res.data);
		        // 结束加载
		        tiem = $timeout(function () {
		          $scope.$broadcast('scroll.infiniteScrollComplete');
		        }, 2000);
            },
            error:function(res){
                console.log(res)
            }
       });
  };    
  };
})
//俱乐部公告
.controller('julebuCtrl', function($scope,$state) {
  $scope.goBackFn = function () {
      window.history.go(-1);
    
  };
     $.ajax({
            url:urltou+"/ws/agent/getNotice",
            type:"GET",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            success:function(res){
            	 if(res.status==0){
                    $scope.id=res.data.cnid;
            	  $scope.text=res.data.text;
            	 }else{
            	 	alert(res.errorMessage)
            	 }
              
            },
            error:function(res){
                console.log(res)
            }
        })
 
  $scope.gonggaofabuFn=function(){
  var content= $("#content").val();
  var data ={"text":content,"cnid":$scope.id} ;
     data= JSON.stringify(data);
     console.log(token);
  	$.ajax({
            url:urltou+"/ws/agent/addNotice",
            type:"POST",
            contentType:"application/json",
            beforeSend: function(request){
	           request.setRequestHeader("Access-Control-Allow-Headers", token);
	        },
            data:data,
            success:function(res){
				if(res.status==0){
            	 	alert("发布成功")
            	 }else{
            	 	alert(res.errorMessage);
            	 }
                  
            },
            error:function(res){
                console.log(res)
            }
        })
  }
})
//名门协议
.controller('xieyiCtrl', function($scope,$state) {
  $scope.goBackFn = function () {
      window.history.go(-1);
    };
  
})
;
