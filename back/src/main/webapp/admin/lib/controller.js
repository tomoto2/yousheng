
var touurl="http://localhost";

function ajax(data,urls,lei){
   var datas="";
   var pageCount=""
//	var defer = $.Deferred();
   
	$.ajax({
		url:urls,
		xhrFields: {
           withCredentials: true
        },
        crossDomain: true,
		type:"POST",
		async:false,
		contentType:"application/json",
		data:data,
		success:function(res){
			if(res.status==0){
//				defer.resolve(res.data)
				datas=res;
				pageCount=res.pageCount
				
			}else{
				alert(res.errorMessage)
			}
//		    console.log(datas);
		},
		error:function(res){
		    console.log(res)
		}
   });
//return defer.promise();
return datas;
}
 function qijin(data){
    	var ss="";
    	$.ajax({
		        url:touurl+"/ws/agent/setCanuse",
		        type:"POST",
		        xhrFields: {
		           withCredentials: true
		        },
		        crossDomain: true,
		        async:false,
		        contentType:"application/json",
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
function wanjqijin(data){
    	var ss="";
    	$.ajax({
		        url:touurl+'/ws/admin/setCanuse',
		        type:"POST",
		        xhrFields: {
		           withCredentials: true
		        },
		        crossDomain: true,
		        async:false,
		        contentType:"application/json",
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
angular.module('controllers',[])
.controller("tabCtrl",function(){

	$("#Huifold1 li").click(function(){
	    $("#Huifold1 li").eq($(this).index()).addClass("navdown").siblings().removeClass("navdown");
	})

})
//玩家总概况
.controller("wanjiaCtrl",function($scope){
	var urls=touurl+'/ws/admin/getUserSum';
	$scope.wanjiaData='';
	$.ajax({
		        url:urls,
		        type:"GET",
		        xhrFields: {
		           withCredentials: true
		        },
		        crossDomain: true,
		        async:false,
//		        contentType:"application/json",
//		        data:data,
		        success:function(res){
		            if(res.status==0){
		            	$scope.wanjiaData=res.data
		            }else{
		            	//alert(res.errorMessage);
		            }
		        },
		        error:function(res){
		            console.log(res)
		        }
		    });
})
//用户信息
.controller("yonghuCtrl",function($scope,$compile){	
	$("#Huifold1 li").eq(1).addClass("navdown").siblings().removeClass("navdown");
	var ye=1;
	
	var urls=touurl+'/ws/admin/getAllPlayer';
	var data ={"size":10,"pageNo":ye} ;
	data= JSON.stringify(data);
	var yh=ajax(data,urls,1)
	$scope.count1=yh.pageCount;
	$scope.yonghuData=yh.data;
	if(ye<=1){
			$("#yhs").addClass("disabled");
		}else{
			$("#yhs").removeClass("disabled");
	}
	if(ye>=$scope.count1){
			$("#yhx").addClass("disabled");
		}else{
			$("#yhx").removeClass("disabled");
	}
	 $scope.kaiqiFn=function(index,event){ 
        var uid=$scope.jiagouData[index].uid;
		if(uid==1){
			alert("此为根代理不可操作")
		}else{
	var data ={"uid":uid,"canuse":"true"} ;
        data= JSON.stringify(data);
        var res =wanjqijin(data);
        if(res.status==0){
    	  $(event.target).attr("disabled",true);
    	  $(event.target).next().attr("disabled",false);
    	  $(event.target).addClass("wuxiao");
    	   $(event.target).next().removeClass('wuxiao');
    	   $(event.target).next().addClass('kedian');
     	}else{
     		alert(res.errorMessage)
     	}
     }
     	
 };
 $scope.jinzhiFn=function(index,event){    	
       	 var uid= $scope.jiagouData[index].uid;
		if(uid==1){
			alert("此为根代理不可操作")
		}else{
    	var data ={"uid":uid,"canuse":"false"} ;
        data= JSON.stringify(data)
        var res1 =wanjqijin(data);
       console.log(res1)
       if(res1.status==0){
       	 $(event.target).attr("disabled",true);
    	 $(event.target).prev().attr("disabled",false);
    	 $(event.target).addClass("wuxiao");
    	 $(event.target).prev().removeClass('wuxiao');
    	 $(event.target).prev().addClass('kedian');
       }else{
       	 alert(res1.errorMessage)
       }
      }
 }
 
	$scope.yhsyyFn=function(){
		if(ye<=1){
			$("#yhs").addClass("disabled");
		}else{
			if(ye==1){
			   $("#yhs").addClass("disabled");
			}else{
			  $("#yhs").removeClass("disabled");
			}
			$("#yhx").removeClass("disabled");
			ye=ye-1
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,1);
			$scope.yonghuData=datas.data;			
		}		
	}
	$scope.yhxyyFn=function(){
		if(ye>=$scope.count1){
			$("#yhx").addClass("disabled");
		}else{
			ye=ye+1;
			$("#yhs").removeClass("disabled");
			if(ye==$scope.count1){
				$("#yhx").addClass("disabled");
			}else{
				$("#yhx").removeClass("disabled");
			}
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,1);
			$scope.yonghuData=datas.data;
			
		}
	}
	
//弹窗
$(".modal-content ").css("height","300px");
$(".modal-content ").css("width","400px");
$(".modal-dialog").css("margin-top","200px");
	
var indexs="";

$scope.fjfkFn=function(index){
	$("#modal-demo").modal("show");
	
 	var html='<p>玩  家  ID : '+$scope.yonghuData[index].uid+'</p><p>玩家昵称 : '+$scope.yonghuData[index].nikeName+'</p><p>剩余房卡 : '+$scope.yonghuData[index].card+'</p><input id="inputshu" type="text" /><button ng-click="qrtj1Fn('+index+')" class="btn radius">确认添加</button>'
 	var $html = $compile(html)($scope);
	$(".modal-body").html($html);
 }
 $scope.qrtj1Fn=function(index){
 	var kashu=$("#inputshu").val();
 	indexs=kashu;
 	var html='<h3>充值成功后将无法撤回</h3><h3>是否确定充值</h3><button class="btn radius butt" ng-click="qxtjFn()">取消充值</button><button id="qd" class="btn radius butt" ng-click="qdtjFn('+index+')">确认充值</button>'
 	var $html = $compile(html)($scope);  
	$(".modal-body").html($html);
 }
 $scope.qdtjFn=function(index){ 	
// 	var kashu=$("#inputshu").val();
 	var urls=touurl+'/ws/admin/chongzhi';
	var data ={"uid":$scope.yonghuData[index].uid,"cartSum":indexs} ;
	 data= JSON.stringify(data);
	$.ajax({
		url:urls,
		xhrFields: {
           withCredentials: true
        },
        crossDomain: true,
		type:"POST",
		contentType:"application/json",
		data:data,
		success:function(res){
			if(res.status==0){
				$(".modal-body").html("<h3>充值成功</h3>");
			}else{
				$(".modal-body").html('<h3>'+res.errorMessage+'</h3>');
			}
			
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
  });
	
 }
 $scope.qxtjFn=function(){
	$(".modal-body").html("<h3>添加失败</h3>");
 }
 $scope.yonhuFn=function(){
 	var yhval=$("#yhinput").val();
 	if(yhval==""){
 		alert("请输入搜索ID")
 	}else{
 	var urls=touurl+'/ws/admin/getPlayer';
 	$.ajax({
		url:urls,
		xhrFields: {
           withCredentials: true
        },
        crossDomain: true,
		async:false,
		type:"POST",
		contentType:"application/json",
		data:yhval,
		success:function(res){
			if(res.status==0){
				var datas=res.data;
				$scope.yonghuData=[{"uid":datas.uid,"nikeName":datas.nikeName,"card":datas.card,"inviteTime":datas.inviteTime,"clubName":datas.clubName}]
			}else{
				alert(res.errorMessage);
			}
			
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
   });
 	}
 }
 
 
 $scope.kaiqiFn=function(index,event){
    	
    	  $(event.target).attr("disabled",true);
    	  $(event.target).next().attr("disabled",false);
	$(event.target).addClass("wuxiao");
          $(event.target).next().removeClass('wuxiao');
    	   $(event.target).next().addClass('kedian');

    	  //$(event.target).css("background","whitesmoke");
    	  //$(event.target).css("color","#2D4964");
    	 //$(event.target).next().css("background",'#2D4964');
    	// $(event.target).next().css("color",'white');
     	
    };
    $scope.jinzhiFn=function(index,event){
    	
       	 $(event.target).attr("disabled",true);
    	$(event.target).prev().attr("disabled",false);
           $(event.target).addClass("wuxiao");
	  $(event.target).prev().removeClass('wuxiao');
    	   $(event.target).prev().addClass('kedian');
    	  //$(event.target).css("background","whitesmoke");
    	  //$(event.target).css("color","#2D4964");
    	 //$(event.target).prev().css("background",'#2D4964');
    	// $(event.target).prev().css("color",'white');
       
    }
 
})
//代理信息
.controller("dailixxCtrl",function($scope){
    $("#Huifold1 li").eq(2).addClass("navdown").siblings().removeClass("navdown");
	var ye=1;
	var urls=touurl+'/ws/admin/getAllProxy';
	var data ={"size":10,"pageNo":ye} ;
	data= JSON.stringify(data);
	var datas=ajax(data,urls,2);
	$scope.count=datas.pageCount;
	$scope.dailiData=datas.data;
	if(ye<=1){
			$("#dls").addClass("disabled");
		}else{
			$("#dls").removeClass("disabled");
	}
	if(ye>=$scope.count){
			$("#dlx").addClass("disabled");
		}else{
			$("#dlx").removeClass("disabled");
	}
		for(var i=0;i<$scope.dailiData.length;i++){
			if($scope.dailiData[i].phone==null){
						
			}else{
				var phone= $scope.dailiData[i].phone
				var myphone = phone.substr(3, 4);  
				$scope.dailiData[i].phone= phone.replace(myphone, "****");
			}
				     
		}
	//分页 shang
	$scope.xxsyyFn=function(){
		if(ye<=1){
			$("#dls").addClass("disabled");
		}else{  
			$("#dlx").removeClass("disabled");
			if(ye==1){
				$("#dls").addClass("disabled");
			}else{
				$("#dls").removeClass("disabled");
			}
			ye=ye-1;
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,2);
			$scope.dailiData=datas.data;
			for(var i=0;i<$scope.dailiData.length;i++){
				if($scope.dailiData[i].phone==null){
							
				}else{
					var phone= $scope.dailiData[i].phone
					var myphone = phone.substr(3, 4);  
					$scope.dailiData[i].phone= phone.replace(myphone, "****");
				}
					     
			}
		}
		
	}
	$scope.xxxyyFn=function(){
		if(ye>=$scope.count){
	        $("#dlx").addClass("disabled");
		}else{
			ye=ye+1;
			$("#dls").removeClass("disabled");

			if(ye==$scope.count){
				$("#dlx").addClass("disabled");
			}else{
				$("#dlx").removeClass("disabled");
			}
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,2);
			$scope.dailiData=datas.data;
			for(var i=0;i<$scope.dailiData.length;i++){
				if($scope.dailiData[i].phone==null){
							
				}else{
					var phone= $scope.dailiData[i].phone
					var myphone = phone.substr(3, 4);  
					$scope.dailiData[i].phone= phone.replace(myphone, "****");
				}
					     
			}
		}
	}
	
	
	//代理信息搜索
	$scope.xinxFn=function(){
	    var xxval=$("#xxinput").val();
	 	if(xxval==""){
	 		alert("请输入搜索ID")
	 	}else{
	 	var urls=touurl+'/ws/admin/getAgent';
	 	$.ajax({
			url:urls,
			xhrFields: {
	           withCredentials: true
	        },
	        crossDomain: true,
			type:"POST",
		    async:false,
			contentType:"application/json",
			data:xxval,
			success:function(res){
				if(res.status==0){
					var datas=res.data;
					var phone= datas.phone
					var myphone = phone.substr(3, 4);  
					datas.phone= phone.replace(myphone, "****");
				
					$scope.dailiData=[{"inviteCode":datas.inviteCode,"nikeName":datas.nikeName,"wx":datas.wx,"phone":datas.phone,"card":datas.card,"balance":datas.balance,"Allchongzhi":datas.Allchongzhi,"allgoumai":datas.allgoumai}]
				}else{
					alert(res.errorMessage);
				}
				
			    console.log(res);
			},
			error:function(res){
			    console.log(res)
			}
	      })
	 }
  }
})
//代理架构
.controller("dailijgCtrl",function($scope){
	$("#Huifold1 li").eq(3).addClass("navdown").siblings().removeClass("navdown");
	var ye=1
	var urls=touurl+"/ws/admin/ProxySet";
	var data ={"size":10,"pageNo":ye};
	data= JSON.stringify(data);
	var jg=ajax(data,urls,3);
	$scope.count3=jg.pageCount;
	$scope.jiagouData=jg.data;
	if(ye<=1){
			$("#jgs").addClass("disabled");
		}else{
			$("#jgs").removeClass("disabled");
	}
	if(ye>=$scope.count3){
			$("#jgx").addClass("disabled");
		}else{
			$("#jgx").removeClass("disabled");
	}
	for(var i=0;i<$scope.jiagouData.length;i++){
			if($scope.jiagouData[i].parentPhone==null){
						
			}else{
				
				var phone= $scope.jiagouData[i].parentPhone
				var myphone = phone.substr(3, 4);  
				$scope.jiagouData[i].parentPhone= phone.replace(myphone, "****");
			}
				     
		}
	//启用
	$scope.qiyFn=function(index,event){
		var pid=$scope.jiagouData[index].pid;
		if(pid==1){
			alert("此为根代理不可操作")
		}else{
		var data ={"agentId":pid,"canuse":"true"} ;
                 data= JSON.stringify(data);
             var res =qijin(data);
           if(res.status==0){
    	        $(event.target).attr("disabled",true);
    	      $(event.target).next().attr("disabled",false);
    	  $(event.target).addClass("wuxiao");
    	   $(event.target).next().removeClass('wuxiao');
    	   $(event.target).next().addClass('kedian');
     	  }else{
     		alert(res.errorMessage)
     	  }
	 }
	}
	//禁用
	$scope.jinyFn=function(index,event){
		var pid= $scope.jiagouData[index].pid;
		if(pid==1){
			alert("此为根代理不可操作")
		}else{
    	         var data ={"agentId":pid,"canuse":"false"} ;
                  data= JSON.stringify(data)
                  var res1 =qijin(data);
                  console.log(res1)
          if(res1.status==0){
       	    $(event.target).attr("disabled",true);
    	        $(event.target).prev().attr("disabled",false);
    	      $(event.target).addClass("wuxiao");
    	    $(event.target).prev().removeClass('wuxiao');
    	         $(event.target).prev().addClass('kedian');
              }else{
       	     alert(res1.errorMessage)
            }
          }
	}
	 
	$scope.jgsyyFn=function(){
		if(ye<=1){
			$("#jgs").addClass("disabled");
		}else{
			$("#jgx").removeClass("disabled");
			if(ye==1){
			   $("#jgs").addClass("disabled");
			}else{
			    $("#jgs").removeClass("disabled");
			}
			ye=ye-1;
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,1);
			$scope.jiagouData=datas.data;
			for(var i=0;i<$scope.jiagouData.length;i++){
				if($scope.jiagouData[i].parentPhone==null){
							
				}else{
					var phone= $scope.jiagouData[i].parentPhone
					var myphone = phone.substr(3, 4);  
					$scope.jiagouData[i].parentPhone= phone.replace(myphone, "****");
				}
					     
			}
		}		
	}
	$scope.jgxyyFn=function(){
		if(ye>=$scope.count3){
			$("#jgx").addClass("disabled");
		}else{
			ye=ye+1;
			$("#jgs").removeClass("disabled");
			if(ye==$scope.count3){
				$("#jgx").addClass("disabled");
			}else{
				$("#jgx").removeClass("disabled");
			}
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,1);
			$scope.jiagouData=datas.data;
			for(var i=0;i<$scope.jiagouData.length;i++){
				if($scope.jiagouData[i].parentPhone==null){
							
				}else{
					var phone= $scope.jiagouData[i].parentPhone
					var myphone = phone.substr(3, 4);  
					$scope.jiagouData[i].parentPhone= phone.replace(myphone, "****");
				}
					     
			}
		}
	}
	

  //代理架构搜索
  $scope.jgsousFn=function(){
  var souid = $("#jginput").val();
  var datas={"agentNum":souid} ;
  if(souid==""){
	 		alert("请输入搜索ID")
	 	}else{
	 	var urls=touurl+'/ws/admin/ProxySetByOne';
	 	$.ajax({
			url:urls,
			xhrFields: {
	           withCredentials: true
	        },
	        crossDomain: true,
			type:"GET",
		    async:false,
			contentType:"application/json",
			data:datas,
			success:function(res){
				if(res.status==0){
					var datas=res.data;
					var phone= datas.parentPhone
					var myphone = phone.substr(3, 4);  
					datas.parentPhone= phone.replace(myphone, "****");
				
					$scope.jiagouData=[{"inviteCode":datas.inviteCode,"nikeName":datas.nikeName,"allgoumai":datas.allgoumai,"childSum":datas.childSum,"childBuySum":datas.childBuySum,"childMoneySum":datas.childMoneySum,"Allchongzhi":datas.Allchongzhi,"parentPhone":datas.parentPhone,"level":datas.level}]
				}else{
					alert(res.errorMessage);
				}
				
			    console.log(res);
			},
			error:function(res){
			    console.log(res)
			}
	      })
	 }
  }
})
//俱乐部管理
.controller("julebuCtrl",function($scope,$state){
	$("#Huifold1 li").eq(4).addClass("navdown").siblings().removeClass("navdown");
	var ye=1;
	var urls=touurl+'/ws/admin/getAllClub';
	var data ={"size":10,"pageNo":ye} ;
	data= JSON.stringify(data);
	var jlb=ajax(data,urls,4);
	$scope.julebuData=jlb.data;
	$scope.count4=jlb.pageCount;
	if(ye<=1){
			$("#jlbs").addClass("disabled");
		}else{
			$("#jlbs").removeClass("disabled");
	}
	if(ye>=$scope.count4){
			$("#jlbx").addClass("disabled");
		}else{
			$("#jlbx").removeClass("disabled");
	}
	$scope.jlbsyyFn=function(){
		if(ye<=1){
			$("#jlbs").addClass("disabled");
		}else{
			$("#jlbx").removeClass("disabled");
			if(ye==1){
				$("#jlbs").addClass("disabled");
			}else{
			     $("#jlbs").removeClass("disabled");
			}
			ye=ye-1;
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,4);
			$scope.julebuData=datas.data;			
		}		
	}
	$scope.jlbxyyFn=function(){
		if(ye>=$scope.count4){
	       $("#jlbx").addClass("disabled");
		}else{
			ye=ye+1;
			$("#jlbs").removeClass("disabled");
			if(ye==$scope.count4){
				$("#jlbx").addClass("disabled");
			}else{
				$("#jlbx").removeClass("disabled");
			}
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,4);
			$scope.julebuData=datas.data;
			
		}
	}
		
	//俱乐部查看成员
  $scope.chakanFn=function(index){
   	
 	 $state.go("tab.chengyuan",{
        id: $scope.julebuData[index].numId
     })
 }
   //俱乐部解散
  $scope.jieshanFn=function(index){
  	var urls=touurl+'/ws/admin/removeClub';
  	var id=$scope.julebuData[index].cid;
  	$.ajax({
		url:urls,
		xhrFields: {
           withCredentials: true
        },
        crossDomain: true,
		type:"POST",
		contentType:"application/json",
		data:id,
		success:function(res){
			if(res.status==0){
				alert("解散成功")
			}else{
				alert(res.errorMessage);
			}
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
   });
  }
  //搜索俱樂部
   $scope.jlbFn=function(){
   	  var jlbval=$("#jlbinput").val();
	 	if(jlbval==""){
	 		alert("请输入搜索俱乐部ID")
	 	}else{
	 	var urls=touurl+'/ws/admin/getClubByPnum';
	 	$.ajax({
			url:urls,
			xhrFields: {
	           withCredentials: true
	        },
	        crossDomain: true,
			type:"POST",
		    async:false,
			contentType:"application/json",
			data:jlbval,
			success:function(res){
				if(res.status==0){
					var datas=res.data;
					$scope.julebuData=[{"numId":datas.numId,"owner":datas.owner,"memberSum":datas.memberSum}]
				}else{
					alert(res.errorMessage);
				}
				
			    console.log(res);
			},
			error:function(res){
			    console.log(res)
			}
	      })
	 	}
   }
})
//俱乐部成员
.controller("chengyuanCtrl",function($scope,$state,$compile,$stateParams){
 $("#Huifold1 li").eq(4).addClass("navdown").siblings().removeClass("navdown");
 var dataId = decodeURIComponent($stateParams.id);
 var urls=touurl+'/ws/admin/getClubUserBycid';
 var ye=1;
 var data={"cid":dataId,"size":10,"pageNo":ye} ;
	data= JSON.stringify(data);
	var cy=ajax(data,urls);
 $scope.chengyuanData=cy.data;
 $scope.count5=cy.pageCount;
 	if(ye<=1){
			$("#cys").addClass("disabled");
		}else{
			$("#cys").removeClass("disabled");
	}
	if(ye>=$scope.count5){
			$("#cyx").addClass("disabled");
		}else{
			$("#cyx").removeClass("disabled");
	}
	$scope.jlbsyyFn=function(){
		if(ye<=1){
			$("#cys").addClass("disabled");
		}else{
			$("#cyx").removeClass("disabled");
			if(ye==1){
				$("#cys").addClass("disabled");
			}else{
			     $("#cys").removeClass("disabled");
			}
			ye=ye-1
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,4);
			$scope.chengyuanData=datas.data;			
		}		
	}
	$scope.jlbxyyFn=function(){
		if(ye>=$scope.count5){
	        $("#cyx").addClass("disabled");
		}else{
			ye=ye+1;
  			 $("#cys").removeClass("disabled");
			if(ye==$scope.count5){
				$("#cyx").addClass("disabled");
			}else{
				$("#cyx").removeClass("disabled");
			}
			var data ={"size":10,"pageNo":ye} ;
			data= JSON.stringify(data);
			var datas=ajax(data,urls,4);
			$scope.chengyuanData=datas.data;
			
		}
	}
$scope.goBackFn = function () {
      window.history.go(-1);
   };
$(".modal-content ").css("height","300px");
$(".modal-content ").css("width","400px");
$(".modal-dialog").css("margin-top","200px");
var kashu=""
$scope.chakanFn=function(index){
	$("#modal-demo1").modal("show");
 	var html='<p>玩  家  ID : '+$scope.chengyuanData[index].uid+'</p><p>玩家昵称 : '+$scope.chengyuanData[index].nikeName+'</p><p>剩余房卡 : '+$scope.chengyuanData[index].card+'</p><input id="kashu" type="text" /><button ng-click="qrtj1Fn('+$scope.chengyuanData[index].uid+')" class="btn radius">确认添加</button>'
 	var $html = $compile(html)($scope);
	$(".modal-body1").html($html);
 }
 $scope.qrtj1Fn=function(uid){
 	kashu=$("#kashu").val();
 	var html='<h3>添加成功后将无法撤回</h3><h3>是否确定添加</h3><button class="btn radius butt" ng-click="qxtjFn()">取消添加</button><button class="btn radius butt" ng-click="qdtjFn('+uid+')">确认添加</button>'
 	var $html = $compile(html)($scope);  
	$(".modal-body1").html($html);
 }
 $scope.qdtjFn=function(uid){
 	var urls=touurl+'/ws/admin/chongzhi';
	var data ={"uid":uid,"cartSum":kashu} ;
	 data= JSON.stringify(data);
	$.ajax({
		url:urls,
		xhrFields: {
           withCredentials: true
        },
        crossDomain: true,
		type:"POST",
		contentType:"application/json",
		data:data,
		success:function(res){
			if(res.status==0){
				$(".modal-body1").html("<h3>添加成功</h3>");
			}else{
				$(".modal-body1").html('<h3>'+res.errorMessage+'</h3>');
			}
			
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
   });

 }
 $scope.qxtjFn=function(){
	$(".modal-body1").html("<h3>添加失败</h3>");

 }
//成员搜索
 $scope.cyFn=function(){
   	  var cyval=$("#cyinput").val();
	 	if(cyval==""){
	 		alert("请输入搜索成员ID")
	 	}else{
	 	var urls=touurl+'/ws/admin/getPlayer';
	 	$.ajax({
			url:urls,
			xhrFields: {
	           withCredentials: true
	        },
	        crossDomain: true,
			type:"POST",
		    async:false,
			contentType:"application/json",
			data:cyval,
			success:function(res){
				if(res.status==0){
					var datas=res.data;
					$scope.chengyuanData=[{"uid":datas.uid,"nikeName":datas.nikeName,"card":datas.card}]
				}else{
					alert(res.errorMessage);
				}
				
			    console.log(res);
			},
			error:function(res){
			    console.log(res)
			}
	      })
   }
 }

 //移除
 $scope.yicuFn=function(index){
 	var data=$scope.chengyuanData[index].uid;
 	data= JSON.stringify(data);
 	var urls=touurl+'/ws/user/removeClub';
 	$.ajax({
		url:urls,
		xhrFields: {
           withCredentials: true
        },
        crossDomain: true,
		type:"POST",
		contentType:"application/json",
		data:data,
		success:function(res){
			if(res.status==0){
				alert("移除成功")
			}else{
				alert(res.errorMessage)
			}
			
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
   });
 }
})
//报表
.controller("baobiaoCtrl",function($scope){
	$("#Huifold1 li").eq(5).addClass("navdown").siblings().removeClass("navdown");
	var demo = {
        elem:'#demo',
        format:'YYYY-MM'
    }
    laydate(demo);
    var demo1 = {
        elem:'#demo1',
        format:'YYYY-MM'
    }
    laydate(demo1); 
    var mydata = new Date();
	$scope.name= mydata.getFullYear();
	$scope.month = mydata.getMonth()+1;
	if($scope.month<10){
		$scope.month="0"+$scope.month
	}
	$scope.sous = $scope.name+"-"+$scope.month;	
//      $scope.sous= JSON.stringify($scope.sous);
	var urls=touurl+"/ws/admin/getBaobiao";
	var datas={"monthDay":$scope.sous};
//	$scope.baobiaoData="";
  	
$.ajax({
		url:urls,
//		xhrFields: {
//         withCredentials: true
//      },
//      crossDomain: true,
		type:"GET",
		async:false,
		data:datas,
		dataType:"json",
//		contentType:"application/json",
		success:function(res){
			if(res.status==0){
				$scope.baobiaoData=res.data;
			}else{
				alert(res.errorMessage);
			}
			
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
   });
// 
   //分页
   
  //报表搜索
   $scope.baobiaoFn=function(){
   	var qis=$("#demo1").val();
   	var jies=$("#demo").val();
   	var urls=touurl+'/ws/admin/getBaobiaoTwoTime';
     var data={"beginTime":qis,"endTime":jies} ;
//	  data= JSON.stringify(data);
//   $scope.baobiaoData=ajax(data,urls);
       	
  $.ajax({
		url:urls,
//		xhrFields: {
//         withCredentials: true
//      },
//      crossDomain: true,
		type:"GET",
		async:false,
		data:data,
		dataType:"json",
//		contentType:"application/json",
		success:function(res){
			if(res.status==0){
				$scope.baobiaoData=res.data;
			}else{
				alert(res.errorMessage);
			}
			
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
   });
   }
	
	
})
//返利
.controller("fanliCtrl",function($scope,$compile){
 $("#Huifold1 li").eq(6).addClass("navdown").siblings().removeClass("navdown");
	var time = {
        elem:'#time',
        format:'YYYY-MM'
    }
    laydate(time);
    var times = {
        elem:'#times',
        format:'YYYY-MM'
    }
    laydate(times);
    
    var mydata = new Date();
	$scope.name= mydata.getFullYear();
	$scope.month = mydata.getMonth()+1;
	if($scope.month<10){
		$scope.month="0"+$scope.month
	}
	$scope.sous = $scope.name+"-"+$scope.month;	
	var urls=touurl+'/ws/admin/fanLi';
	var datas={"searchTime":$scope.sous};
  	$scope.fanliData="";
   $(function(){
    		$(".pageBox").pageFun({  /*在本地服务器上才能访问哦*/
    			interFace:touurl+'/ws/admin/fanLi',  /*接口*/
    			displayCount:10,  /*每页显示总条数*/
    			maxPage:1,/*每次最多加载多少页*/
    			datasData:datas,
    			dataFun:function(data,i){
    				var istrue="";
				var status='';
    				var dealWith="";
				var dataHtml =''
				var bili =(data.onerate/100);
					if(data.nikeName==null){
						data.nikeName="";
					}
    				if(data.status==0){
						status="已申请"
					}else if(data.status==1){
						status="已提现"
					}else if(data.status==2){
						status="已返回"
					}else{
						status="未申请"
					}
					if(data.dealWith==0){
						dealWith="未处理";
						 istrue=true;

    				                 dataHtml += '<div id="" style="background: white;height: 44px;">'
						+'<div class="col1">'+data.pnumId+'</div>'
						+'<div class="col1">'+data.nikeName+'</div>'
						+'<div class="col1">'+data.oneMoney+'</div>'
						+'<div class="col1">'+data.sum+'</div>'
						+'<div class="col1">'+bili+'</div>'
						+'<div class="col120">'+data.allFanli+'</div>'
						+'<div class="col120">'+data.qixiaAndDirect+'</div>'
						+'<div class="col100">'+data.shijiFanli+'</div>'
						+'<div class="col1" title="申请人：'+data.name+',微信号：'+data.wx+',支付宝号:'+data.alipay+'">'+status+'</div>'
						+'<div class="col80">'+dealWith+'</div>'
						+'<div class="col1">'
							+'<button class="btn radius buts" id="'+data.cashOutId+'"  ng-click="chuliFn($event)"  style="background:#2D4964;color: white;">处理</button>'						
						+'</div>'
					+'</div>';
					}else if(data.dealWith==1){
						dealWith="已处理";
						istrue=false;

    				                 dataHtml += '<div id="" style="background: white;height: 44px;">'
						+'<div class="col1">'+data.pnumId+'</div>'
						+'<div class="col1">'+data.nikeName+'</div>'
						+'<div class="col1">'+data.oneMoney+'</div>'
						+'<div class="col1">'+data.sum+'</div>'
						+'<div class="col1">'+bili+'</div>'
						+'<div class="col120">'+data.allFanli+'</div>'
						+'<div class="col120">'+data.qixiaAndDirect+'</div>'
						+'<div class="col100">'+data.shijiFanli+'</div>'
						+'<div class="col1" title="申请人：'+data.name+',微信号：'+data.wx+',支付宝号:'+data.alipay+'">'+status+'</div>'
						+'<div class="col80">'+dealWith+'</div>'
						+'<div class="col1">'
						+'</div>'
					+'</div>';
					}else{
						dealWith="";
						istrue=false;

    				                 dataHtml += '<div id="" style="background: white;height: 44px;">'
						+'<div class="col1">'+data.pnumId+'</div>'
						+'<div class="col1">'+data.nikeName+'</div>'
						+'<div class="col1">'+data.oneMoney+'</div>'
						+'<div class="col1">'+data.sum+'</div>'
						+'<div class="col1">'+bili+'</div>'
						+'<div class="col120">'+data.allFanli+'</div>'
						+'<div class="col120">'+data.qixiaAndDirect+'</div>'
						+'<div class="col100">'+data.shijiFanli+'</div>'
						+'<div class="col1" title="申请人：'+data.name+',微信号：'+data.wx+',支付宝号:'+data.alipay+'">'+status+'</div>'
						+'<div class="col80">'+dealWith+'</div>'
						+'<div class="col1">'
						+'</div>'
					+'</div>';
					}
					
					
                                               //istrue=CBool(istrue)
    				
//  				}
    				console.log(data)
    				dataHtml = $compile(dataHtml)($scope); 
    				
    				return dataHtml;
    			},
    			pageFun:function(i){
    				var pageHtml = '<li class="pageNum">'+i+'</li>';
						return pageHtml;
    			}

    		})
    	})
   
   
   //返利区间月搜索
   $scope.fanlissFn=function(){
   	 var kai=$("#times").val();
   	 var jisu=$("#time").val();
   	 var urls=touurl+'/ws/admin/fanLiTwoTime';
     var data={"beginTime":kai,"endTime":jisu};
     $(".pageDiv").html('');
//	  data= JSON.stringify(data);
    $(function(){
    		$(".pageBox").pageFun({  /*在本地服务器上才能访问哦*/
    			interFace:urls,  /*接口*/
    			displayCount:10,  /*每页显示总条数*/
    			maxPage:1,/*每次最多加载多少页*/
    			datasData:data,
    			dataFun:function(data,i){
    				var istrue="";
				var status='';
    				var dealWith="";
    				var dataHtml =''
				 var bili =(data.onerate/100);
				if(data.nikeName==null){
						data.nikeName="";
					}
    				if(data.status==0){
						status="已申请"
					}else if(data.status==1){
						status="已提现"
					}else if(data.status==2){
						status="已返回"
					}else{
						status="未申请"
					}
					if(data.dealWith==0){
						 dealWith="未处理";
						 istrue=true;
                                                     dataHtml += '<div id="" style="background: white;height: 44px;">'
						+'<div class="col1">'+data.pnumId+'</div>'
						+'<div class="col1">'+data.nikeName+'</div>'
						+'<div class="col1">'+data.oneMoney+'</div>'
						+'<div class="col1">'+data.sum+'</div>'
						+'<div class="col1">'+bili+'</div>'
						+'<div class="col120">'+data.allFanli+'</div>'
						+'<div class="col120">'+data.qixiaAndDirect+'</div>'
						+'<div class="col100">'+data.shijiFanli+'</div>'
						+'<div class="col1" title="申请人：'+data.name+',微信号：'+data.wx+',支付宝号:'+data.alipay+'">'+status+'</div>'
						+'<div class="col80">'+dealWith+'</div>'
						+'<div class="col1">'
							+'<button class="btn radius buts" id="'+data.cashOutId+'"  ng-click="chuliFn($event)"  ng-show='+istrue+' style="background:#2D4964;color: white;">处理</button>'						
						+'</div>'
					+'</div>';
					}else if(data.dealWith==1){
						dealWith="已处理";
						istrue=false;
                                                     dataHtml += '<div id="" style="background: white;height: 44px;">'
						+'<div class="col1">'+data.pnumId+'</div>'
						+'<div class="col1">'+data.nikeName+'</div>'
						+'<div class="col1">'+data.oneMoney+'</div>'
						+'<div class="col1">'+data.sum+'</div>'
						+'<div class="col1">'+bili+'</div>'
						+'<div class="col120">'+data.allFanli+'</div>'
						+'<div class="col120">'+data.qixiaAndDirect+'</div>'
						+'<div class="col100">'+data.shijiFanli+'</div>'
						+'<div class="col1" title="申请人：'+data.name+',微信号：'+data.wx+',支付宝号:'+data.alipay+'">'+status+'</div>'
						+'<div class="col80">'+dealWith+'</div>'
						+'<div class="col1">'
						+'</div>'
					+'</div>';
					}else{
						dealWith="";
						istrue=false;
						 dataHtml += '<div id="" style="background: white;height: 44px;">'
						+'<div class="col1">'+data.pnumId+'</div>'
						+'<div class="col1">'+data.nikeName+'</div>'
						+'<div class="col1">'+data.oneMoney+'</div>'
						+'<div class="col1">'+data.sum+'</div>'
						+'<div class="col1">'+bili+'</div>'
						+'<div class="col120">'+data.allFanli+'</div>'
						+'<div class="col120">'+data.qixiaAndDirect+'</div>'
						+'<div class="col100">'+data.shijiFanli+'</div>'
						+'<div class="col1" title="申请人：'+data.name+',微信号：'+data.wx+',支付宝号:'+data.alipay+'">'+status+'</div>'
						+'<div class="col80">'+dealWith+'</div>'
						+'<div class="col1">'
						+'</div>'
					+'</div>';
					}
					
					
    				 
//  				}
    				console.log(data)
    				dataHtml = $compile(dataHtml)($scope); 
    				
    				return dataHtml;
    			},
    			pageFun:function(i){
    				var pageHtml = '<li class="pageNum">'+i+'</li>';
						return pageHtml;
    			}

    		})
    	})
   }
    //处理按钮
    $scope.chuliFn=function($event){
    var urls=touurl+'/ws/admin/uptCashOutStatus';
  	var clId = $($event.target).attr("id");
  	$.ajax({
		url:urls,
		xhrFields: {
           withCredentials: true
        },
        crossDomain: true,
		type:"POST",
		contentType:"application/json",
		data:clId,
		success:function(res){
			if(res.status==0){
				alert('处理成功');
			}else{
				alert(res.errorMessage);
			}		
		    console.log(res);
		},
		error:function(res){
		    console.log(res)
		}
   });
    }
})