//日历类
function JoeCalendar(){
  var _JoeCalendar;
  var _dateTable;
  var _topTable;
  var _yearTable;
  var _fundusDiv;
  var _layer;
  var _leftButtonTd;
  var _rightButtonTd;
  var _dateTd;
  var _selectDate;
  var _nowDate;
  var _txtElement;
  var txtElement;
  var _span;
  var _boo;
  var iskai;
}
//画顶部区域  导航栏
JoeCalendar.prototype.drawTopTable = function(){
  var tr = _topTable.insertRow();
  _leftButtonTd = tr.insertCell();
  _dateTd = tr.insertCell();
  _rightButtonTd = tr.insertCell();
  _span = document.createElement("span");
  _dateTd.appendChild(_span);
}
//画日历第一层
JoeCalendar.prototype.drawDate = function(){
  var arr = new Array("日" , "一" , "二" , "三" , "四" , "五" , "六")
  for(var i = 0 ; i < 7 ; i++){
    var tr = _dateTable.insertRow();
    for(var j = 0 ; j < 7 ; j++){
      var td = tr.insertCell();
      if(i == 0){
        td.innerHTML = arr[j];
        td.className = "default-date";
      }
    }
  }
}
//画日历第二三四层
JoeCalendar.prototype.drawYear = function(){
  for(var i = 0 ; i < 3 ; i++){
    var tr = _yearTable.insertRow();
    for(var j = 0 ; j < 4 ; j++){
      tr.insertCell();
    }
  }
}
//填充第一层日历     接收一个日期对象，根据日期对象填充
JoeCalendar.prototype.fillDate = function(fillDate){
  _nowDate = new Date();
  _JoeCalendar.fillTopTable(fillDate);
  var topDate = _JoeCalendar.getTdDate(_topTable.rows[0].cells[1]);
  fillDate.setDate(1);
  var k;
  if(fillDate.getDay() == 0){
    var k = 6;
  }
  else{
    var k = fillDate.getDay() - 1;
  }
  fillDate.setDate(-k);
  for(var i = 1 ; i < 7 ; i++){
    for(var j = 0 ; j < 7 ; j++){
      var td = _dateTable.rows[i].cells[j];
      _JoeCalendar.setTd(fillDate , td);
      _JoeCalendar.setClass(td);
      _JoeCalendar.setEvent(td);
      fillDate.setDate(fillDate.getDate() + 1);
    }
  }
  _JoeCalendar.showDate();
}
//填充第二三四层日历   接收一个日期对象，根据日期对象填充
JoeCalendar.prototype.fillYear = function(fillDate){
  _nowDate = new Date();
  _JoeCalendar.fillTopTable(fillDate);
  var fillDate3 = new Date();
  var fillDate4 = new Date()
  fillDate3.setFullYear(fillDate.getFullYear() - 1);
  fillDate4.setFullYear(fillDate.getFullYear() - 10);
  for(var i = 0 ; i < 3 ; i++){
    for(var j = 0 ; j < 4 ; j++){
      var td = _yearTable.rows[i].cells[j]
      if(_layer == 2){
        _JoeCalendar.setTd(fillDate , td);
        fillDate.setMonth(fillDate.getMonth() + 1);
      }
      else if(_layer == 3){
        _JoeCalendar.setTd(fillDate3 , td);
        fillDate.setFullYear(fillDate3.getFullYear() + 1);
      }
      else if(_layer == 4){
        _JoeCalendar.setTd(fillDate4 , td);
        fillDate.setFullYear(fillDate4.getFullYear() + 10);
      }
      _JoeCalendar.setClass(td);
      _JoeCalendar.setEvent(td);
    }
  }
  _JoeCalendar.showYear();
}
//填充导航栏的方法
JoeCalendar.prototype.fillTopTable = function(fillDate){
  
  _dateTd.className = "topDate";
  
  _leftButtonTd.innerHTML = "<input type='button' class='btn' onclick='_JoeCalendar.pageDown()' value='<' />";
  _rightButtonTd.innerHTML = "<input type='button' class='btn' onclick='_JoeCalendar.pageUp()' value='>' />";
  
  var year = fillDate.getFullYear();
  var month = fillDate.getMonth();
  var day = fillDate.getDate();
  _dateTd.year = year;
   _dateTd.month = month;
  _dateTd.day = day;
  
  month += 1;
  if(month<10){
  	month="0"+month;
  }
  if(day<10){
  	day="0"+day;
  }
  if(_layer == 1){
    _span.innerHTML = year + "年" + month + "月";
  }
  if(_layer == 2){
    _span.innerHTML = year;
  }
  if(_layer == 3){
    _span.innerHTML = year + "-" + (year + 9);
  }
  if(_layer == 4){
    _span.innerHTML = year + "-" + (year + 99);
  }
  _span.onclick = function(){
    _JoeCalendar.enter();
  }
  
  
  
}
//填充td方法，填充的同时储存一个日期，  接受一个日期对象和一个单元格对象
JoeCalendar.prototype.setTd = function(fillDate , td){
  var year = fillDate.getFullYear();
  var month = fillDate.getMonth();
  var day = fillDate.getDate();
  if(month<10){
  	month="0"+month;
  }
  if(day<10){
  	day="0"+day;
  }
  td.year = year;
  td.month = month;
  td.day = day;
  
  if(_layer == 1){
    td.innerHTML = day;
  }
  if(_layer == 2){
    td.innerHTML = (month + 1) + "月";
  }
  if(_layer == 3){
    td.month = 0;
    td.innerHTML = year;
    fillDate.setFullYear(fillDate.getFullYear() + 1);
  }
  if(_layer == 4){
    td.month = 0;
    td.innerHTML = year +"<br/>" + "-" + "<br/>" + (year+9);
    fillDate.setFullYear(fillDate.getFullYear() + 10);
  }
  
  if(year < 1900 || year > 2099){
    td.innerHTML = "";
  }
}
//接收一个td对象，返回一个日期对象（td里存储的日期对象）
JoeCalendar.prototype.getTdDate = function(td){
  var year = td.year;
  var month = td.month;
  var day = td.day;
  if(month<10){
  	month="0"+month;
  }
  if(day<10){
  	day="0"+day;
  }
  var date = new Date();
  date.setFullYear(year);
  date.setMonth(month);
  date.setDate(day);
  return date;
}
//接收两个日期对象，判断两个日期对象的年月日是否相同，用于第一层判断当前单元格日期是否为当前日期
JoeCalendar.prototype.equalsDate = function(date1 , date2){
  
  if(date1 == null || date2 == null){
    return false;
  }
  else{
    
    if(date1.getFullYear() == date2.getFullYear() && date1.getMonth() == date2.getMonth() && date1.getDate() == date2.getDate()){
      
      return true;
    }
    else{
      return false;
    }
  }
}
//接受两个日期对象，判断两个日期年月是否相同，用于第二层判断当前单元格是否为当前日期
JoeCalendar.prototype.equalsYearMonth = function(date1 , date2){
  if(date1 == null || date2 == null){
    return false;
  }
  else{
    if(date1.getFullYear() == date2.getFullYear() && date1.getMonth() == date2.getMonth()){
      return true;
    }
    else{
      return false;
    }
  }
}
//接受两个日期对象，判断两个日期年份是否相同，用于第三层判断当前单元格是否为当前日期
JoeCalendar.prototype.equalsYear = function(date1 , date2){
  if(date1 == null || date2 == null){
    return false;
  }
  else{
    if(date1.getFullYear() == date2.getFullYear()){
      return true;
    }
    else{
      return false;
    }
  }
}
//用于第四层比较判断单元格日期是否为当前日期区间
JoeCalendar.prototype.equalsYearRange = function(date1 , date2){
  if(date1 == null || date2 == null){
    return false;
  }
  else{
    var nowYear = date1.getFullYear();
    var tdYear = date2.getFullYear();
    if(nowYear >= tdYear && nowYear <= tdYear + 9){
      return true;
    }
    else{
      return false;
    }
  }
}
//单元格鼠标划入事件    接收一个td对象
JoeCalendar.prototype.tdOnMouseOver = function(td){
  if(td.className != "nowTd-date" && td.className != "select-date" && td.className != "nowTd-year" && td.className != "select-year"){
    td.className = "mouseOver";
  }
}
//单元格鼠标滑出事件    接收一个td对象
JoeCalendar.prototype.tdOnMouseOut = function(td){
  _JoeCalendar.setClass(td);
}
//单元格单击事件      接收一个td对象
JoeCalendar.prototype.tdOnClick = function(td){
  if(_layer == 1){
    
    _selectDate = _JoeCalendar.getTdDate(td);
    var year = _selectDate.getFullYear();
    var month = _selectDate.getMonth() + 1;
    var day = _selectDate.getDate();
    if(month<10){
	  	month="0"+month;
	  }
	  if(day<10){
	  	day="0"+day;
	  }
    var data= new Date();
    var years=data.getFullYear();
    var months=data.getMonth() + 1;
    var dates = data.getDate();
    if(months<10){
	  	months="0"+months;
	  }
	  if(dates<10){
	  	dates="0"+dates;
	  }
    if(day<=dates && year<=years && month<=months){
    	if(iskai){
    		_txtElement.value = year + "-" + month + "-" + day;
    	}else{
    		 txtElement.value = year + "-" + month + "-" + day;
    	} 	
     _JoeCalendar.hideCalendar();
    }
  }
  else if(_layer == 2){
    _layer -= 1;
    var fillDate = _JoeCalendar.getTdDate(td);
    _JoeCalendar.fillDate(fillDate);
  }
  else{
    _layer -= 1;
    var fillDate = _JoeCalendar.getTdDate(td);
    _JoeCalendar.fillYear(fillDate);
  }
}
//隐藏日历
JoeCalendar.prototype.hideCalendar = function(){
  _fundusDiv.style.display = "none";
}
JoeCalendar.prototype.right = function(){
  _fundusDiv.style.marginLeft = "38%";
}
//显示日历第一层
JoeCalendar.prototype.showDate = function(){
  _fundusDiv.style.display = "block";
  _dateTable.style.display = "";
  _yearTable.style.display = "none";
}
//显示日历第二三四层
JoeCalendar.prototype.showYear = function(){
  _fundusDiv.style.display = "block";
  _dateTable.style.display = "none";
  _yearTable.style.display = "";
}
//前翻页
JoeCalendar.prototype.pageDown = function(){
  
  var date = _JoeCalendar.getTdDate(_dateTd);
  if(_layer == 1){
    date.setMonth(date.getMonth() - 1);
    if(date.getFullYear() >= 1900){
      _JoeCalendar.fillDate(date);
    }
    else{
      date.setMonth(date.getMonth() + 1);
    }
  }
  else{
    if(_layer == 2){
      if(date.getFullYear() >= 1901){
        date.setFullYear(date.getFullYear() - 1);
      }
    }
    else if(_layer == 3){
      if(date.getFullYear() >= 1910){
        date.setFullYear(date.getFullYear() - 10);
      }
    }
    else if(_layer == 4){
      if(date.getFullYear() >= 2000){
        date.setFullYear(date.getFullYear() - 100);
      }
    }
    _JoeCalendar.fillYear(date);
  }
}
//后翻页
JoeCalendar.prototype.pageUp = function(){
  var date = _JoeCalendar.getTdDate(_dateTd);
  if(_layer == 1){
    date.setMonth(date.getMonth() + 1);
    if(date.getFullYear() <= 2099){
      _JoeCalendar.fillDate(date);
    }
    else{
      date.setMonth(date.getMonth() - 1);
    }
  }
  else{
    if(_layer == 2){
      if(date.getFullYear() <= 2098){
        date.setFullYear(date.getFullYear() + 1);
      }
    }
    else if(_layer == 3){
      if(date.getFullYear() <= 2080){
        date.setFullYear(date.getFullYear() + 10);
      }
    }
    else if(_layer == 4){
      if(date.getFullYear() <= 1900){
        date.setFullYear(date.getFullYear() + 100);
      }
    }
    _JoeCalendar.fillYear(date);
  }
}
//点击导航栏日期进入下一层
JoeCalendar.prototype.enter = function(){
  if(_layer < 4){
    _layer += 1;
  }
  var fillDate = _JoeCalendar.getTdDate(_dateTd);
  var year = fillDate.getFullYear();
  if(_layer == 2){
    fillDate.setMonth(0);
  }
  else if(_layer == 3){
    year = parseInt(year/10)*10;
    fillDate.setFullYear(year);
  }
  else if(_layer == 4){
    year = parseInt(year/100)*100;
    fillDate.setFullYear(year);
  }
  _JoeCalendar.fillYear(fillDate);
}
//初始化方法   接收一个文本框id
JoeCalendar.prototype.init = function(_txtId,id){
  
//var oCss = document.createElement("link");
//oCss.setAttribute("rel", "stylesheet"); 
//oCss.setAttribute("type", "text/css");  
//oCss.setAttribute("href", "日历.css");
//document.getElementsByTagName("head")[0].appendChild(oCss);
	
  _JoeCalendar = this;
  _txtElement = document.getElementById(_txtId);
   txtElement = document.getElementById(id);
  _fundusDiv = document.createElement("div");
  _topTable = document.createElement("table");
  _dateTable = document.createElement("table");
  _yearTable = document.createElement("table");
  var baobiao = document.getElementById("rili");
  baobiao.appendChild(_fundusDiv);
  _fundusDiv.appendChild(_topTable);
  _fundusDiv.appendChild(_dateTable);
  _fundusDiv.appendChild(_yearTable);
  

  _JoeCalendar.drawDate();
  _JoeCalendar.drawYear();
  _JoeCalendar.drawTopTable();
  
  _dateTable.cellSpacing="0";
  _yearTable.cellSpacing="0";
  _topTable.cellSpacing="0";
  
  _fundusDiv.className = "fundusDiv";
  _dateTable.className = "dateDiv";
  _yearTable.className = "yearDiv";
  _topTable.className = "topDiv";
  _fundusDiv.style.display = "none";
   _fundusDiv.style.float="left";
_txtElement.readOnly = "readonly";
  txtElement.readOnly = "readonly";
  
  _txtElement.onclick = function(){
  	iskai=true;
    if(_txtElement.value == ""){
      _selectDate = null;
      _layer = 1 ;
      var fillDate = new Date();
      _JoeCalendar.fillDate(fillDate);
    }
    else{
      var fillDate = new Date();
      fillDate.setDate(_selectDate.getDate());
      fillDate.setMonth(_selectDate.getMonth());
      fillDate.setFullYear(_selectDate.getFullYear());
      _JoeCalendar.fillDate(fillDate);
    }
  }
   txtElement.onclick = function(){
   	iskai=false; 	
//   _JoeCalendar.right();
    if(txtElement.value == ""){
      _selectDate = null;
      _layer = 1 ;
      var fillDate = new Date();
      _JoeCalendar.fillDate(fillDate);
    }
    else{
      var fillDate = new Date();
      fillDate.setDate(_selectDate.getDate());
      fillDate.setMonth(_selectDate.getMonth());
      fillDate.setFullYear(_selectDate.getFullYear());
      _JoeCalendar.fillDate(fillDate);
    }
  }
}
//设置样式方法   接收一个td对象
JoeCalendar.prototype.setClass = function(td){
  var tdDate = _JoeCalendar.getTdDate(td);
  var topDate = _JoeCalendar.getTdDate(_dateTd);
  if(_layer == 1){
    td.className = "default-date";
    if(_JoeCalendar.equalsDate(tdDate , _nowDate)){
      td.className = "nowTd-date";
    }
    else if(_JoeCalendar.equalsDate(tdDate , _selectDate)){
      td.className = "select-date";
    }
    else if(!_JoeCalendar.equalsYearMonth(tdDate , topDate)){
      td.className = "noTheNow-date";
    }
  }
  else{
    td.className = "default-year";
    if(_selectDate != null){
      if(_layer == 2){
        if(_JoeCalendar.equalsYearMonth(tdDate , _selectDate)){
          td.className = "select-year";
        }
        else if(!_yearTable.rows[0].cells[0] == td || _yearTable.rows[2].cells[3] == td){
          td.className = "noTheNow-year";
        }
      }
      else if(_layer == 3){
        if(_JoeCalendar.equalsYear(tdDate , _selectDate)){
          td.className = "select-year";
        }
        else if(!_yearTable.rows[0].cells[0] == td || _yearTable.rows[2].cells[3] == td){
          td.className = "noTheNow-year";
        }
      }
      else if(_layer == 4){
        if(_JoeCalendar.equalsYearRange(_selectDate , tdDate)){
          td.className = "select-year";
        }
        else if(_yearTable.rows[0].cells[0] == td || _yearTable.rows[2].cells[3] == td){
          td.className = "noTheNow-year";
        }
      }
    }
    else{
      if(_layer == 2){
        if(_JoeCalendar.equalsYearMonth(tdDate , _nowDate)){
          td.className = "select-year";
        }
        else if(_yearTable.rows[0].cells[0] == td || _yearTable.rows[2].cells[3] == td){
          td.className = "noTheNow-year";
        }
      }
      else if(_layer == 3){
        if(_JoeCalendar.equalsYear(tdDate , _nowDate)){
          td.className = "select-year";
        }
        else if(_yearTable.rows[0].cells[0] == td || _yearTable.rows[2].cells[3] == td){
          td.className = "noTheNow-year";
        }
      }
      else if(_layer == 4){
        if(_JoeCalendar.equalsYearRange(_nowDate , tdDate)){
          td.className = "select-year";
        }
        else if(_yearTable.rows[0].cells[0] == td || _yearTable.rows[2].cells[3] == td){
          td.className = "noTheNow-year";
        }
      }
    }
  }
}
//设置事件方法   接收一个td对象
JoeCalendar.prototype.setEvent = function(td){
  if(td.year >= 1900 && td.year <= 2099){
    td.onclick = function(){
      _JoeCalendar.tdOnClick(this);
    }
    td.onmouseover = function(){
      _JoeCalendar.tdOnMouseOver(this);
    }
    td.onmouseout = function(){
      _JoeCalendar.tdOnMouseOut(this);
    }
  }
  else{
    td.onclick = function(){};
    td.onmouseover = function(){};
    td.onmouseout = function(){};
  }
}







