function TMamTimePicker(){
    'use strict';
    let i;
    this.delimiter=":";
    this.width='180px';//00:00の横幅　枠の横幅ではない
    this.size=20;//00:00の数字のデカさ
    this.family=
      'Verdana,Roboto,"Droid Sans","游ゴシック",YuGothic,"メイリオ",Meiryo,'+
      '"ヒラギノ角ゴ ProN W3","Hiragino Kaku Gothic ProN","ＭＳ Ｐゴシック",sans-serif';
    let per_minutes=1;//分の刻み（5分刻みで表示したいなら5、10分刻みなら10を設定
    this.obj=null;
    this.show=false;
    this.canH=null;
    this.canM=null;
    this.arrH=[];
    for(i=0;i<24;i++){this.arrH[i]=("00"+i).slice(-2);}
    this.arrM=[];
    for(i=0;i<(60/per_minutes);i++){this.arrM[i]=("00"+i*per_minutes).slice(-2);}
    this.h=0;
    this.m=0;
    this.event={};
    this.event.flag=false;
    this.event.hflag=0;// -1 0 1 hour direction
    this.event.mflag=0;// -1 0 1 minute direction
    this.event.h=0;
    this.event.m=0;
    this.inputs=[];
    this.target=null;
    this.hup=null;
    this.hdown=null;
    this.mup=null;
    this.mdown=null;
  
    this.event.hdown=false;
    this.event.mdown=false;
    this.event.hdowny=0;
    this.event.mdowny=0;
  
    this.init=function(){
      //スタイルシートの登録
      let style=document.createElement("style");
      document.head.appendChild(style);
      style.sheet.insertRule(
        '#mamTimePicker{position:absolute;display:none;width:'+this.width+';font-size:0;'+
        'max-width:'+this.width+';min-width:'+this.width+';border:1px solid #000;'+
        'px;background-color:#fff;letter-spacing:0px;left:0px;top:100px;padding:0;margin:0;}' ,0);
      style.sheet.insertRule('.mamTimePickerCanvas{width:80px;height:100px;margin:0;padding:0;}',0);//height 大枠の高さ　width 入力項目の横幅　枠ではない
  
      let obj=document.createElement("div");
      obj.setAttribute("id","mamTimePicker");
      let html =
        '<div style="display:flex;margin:0;padding:20px 0 0 0;align-items:center;">'+
        '  <div style="width:80px;text-align:center;">'+
        '    <svg style="width:40px;height:24px;margin:auto;padding:0;" viewBox="0 0 40 30"'+
        '      onMouseOver="this.style.background=\'#ccc\'" onMouseOut="this.style.background=\'\'" id="mamTimePickerHup">'+
        '      <polygon points="1,28 38,28 19,1" stroke="none" fill="#666"></polygon>'+
        '    </svg>'+
        '  </div>'+
        '  <div style="width:20px;text-align:center;"></div>'+
        '  <div style="width:80px;text-align:center;">'+
        '    <svg style="width:40px;height:24px;margin:auto;padding:0;" viewBox="0 0 40 30"'+
        '      onMouseOver="this.style.background=\'#ccc\'" onMouseOut="this.style.background=\'\'" id="mamTimePickerMup">'+
        '      <polygon points="1,28 38,28 19,1" stroke="none" fill="#666"></polygon>'+
        '    </svg>'+
        '  </div>'+
        '</div>'+
        '<div style="display:flex;margin:0;padding:0;align-items:center;">'+
        '  <div style="margin:0;padding:0;"><canvas id="mamTimePickerH" class="mamTimePickerCanvas" width="80px" height="100px"></canvas></div>'+
        '  <div style="width:20px;text-align:center;">:</div>'+
        '  <div style="margin:0;padding:0;"><canvas id="mamTimePickerM" class="mamTimePickerCanvas" width="80px" height="100px"></canvas></div>'+
        '</div>'+
        '<div style="display:flex;margin:0;padding:0;align-items:center;">'+
        '  <div style="width:80px;text-align:center;">'+
        '    <svg style="width:40px;height:24px;margin:auto;padding:0;" viewBox="0 0 40 30"'+
        '      onMouseOver="this.style.background=\'#ccc\'" onMouseOut="this.style.background=\'\'" id="mamTimePickerHdown">'+
        '      <polygon points="1,1 38,1 18,28" stroke="none" fill="#666"></polygon>'+
        '    </svg>'+
        '  </div>'+
        '  <div style="width:20px;text-align:center;"></div>'+
        '  <div style="width:80px;text-align:center;">'+
        '    <svg style="width:40px;height:24px;margin:auto;padding:0;" viewBox="0 0 40 30"'+
        '       onMouseOver="this.style.background=\'#ccc\'" onMouseOut="this.style.background=\'\'" id="mamTimePickerMdown">'+
        '      <polygon points="1,1 38,1 18,28" stroke="none" fill="#666"></polygon>'+
        '    </svg>'+
        '  </div>'+
        '</div>'+
        '<div style="width:100%;text-align:center;" onMouseOver="this.style.background=\'#ccc\'" onMouseOut="this.style.background=\'\'" id="mamTimePickerSet">'+
        '  <button style="width:40px;height:30px;margin:auto;padding:0;" viewBox="0 0 40 30">'+
        '   ok</button>'+
        
        '</div>';
      obj.innerHTML=html;
  
      document.body.appendChild(obj);
      this.obj=obj;
      let mw=null;
      if('onwheel' in document){
        mw='wheel';
      }else if('onmousewheel' in document){
        mw='mousewheel';
      }else{
        wh='DOMMouseScroll';
      }
      this.canH=document.getElementById("mamTimePickerH");
      this.canH.addEventListener(mw,this.hourWheel.bind(this),{passive: false});
      this.canH.addEventListener("touchstart",this.hourDown.bind(this),{passive: false});
      this.canH.addEventListener("touchend",this.hourUp.bind(this),false);
      this.canH.addEventListener("touchmove",this.hourMove.bind(this),{passive: false});
      this.canH.addEventListener("touchchancel",this.hourUp.bind(this),false);
      this.canM=document.getElementById("mamTimePickerM");
      this.canM.addEventListener(mw,this.minuteWheel.bind(this),{passive: false});
      this.canM.addEventListener("touchstart",this.minuteDown.bind(this),{passive: false});
      this.canM.addEventListener("touchend",this.minuteUp.bind(this),false);
      this.canM.addEventListener("touchmove",this.minuteMove.bind(this),{passive: false});
      this.canM.addEventListener("touchchancel",this.minuteUp.bind(this),false);
      this.set=document.getElementById("mamTimePickerSet");
      this.set.addEventListener("click",this.setVal.bind(this));
      this.hup=document.getElementById("mamTimePickerHup");
      this.hdown=document.getElementById("mamTimePickerHdown");
      this.mup=document.getElementById("mamTimePickerMup");
      this.mdown=document.getElementById("mamTimePickerMdown");
      this.hup.addEventListener("mousedown" ,function(){this.hourChange(-1);}.bind(this)  ,false);
      this.hup.addEventListener("touchstart",function(){this.hourChange(-1);}.bind(this) ,{passive: false});
      this.hup.addEventListener("mouseup" ,function(){this.hourChange( 0);}.bind(this)  ,false);
      this.hup.addEventListener("touchend" ,function(){this.hourChange( 0);}.bind(this)  ,false);
      this.hup.addEventListener("mouseleave",function(){this.hourChange( 0);}.bind(this)  ,false);
      this.mup.addEventListener("mousedown" ,function(){this.minuteChange(-1);}.bind(this),false);
      this.mup.addEventListener("touchstart",function(){this.minuteChange(-1);}.bind(this),{passive: false});
      this.mup.addEventListener("mouseup" ,function(){this.minuteChange( 0);}.bind(this),false);
      this.mup.addEventListener("touchend" ,function(){this.minuteChange( 0);}.bind(this),false);
      this.mup.addEventListener("mouseleave",function(){this.minuteChange( 0);}.bind(this),false);
  
      this.hdown.addEventListener("mousedown" ,function(){this.hourChange( 1);}.bind(this)  ,false);
      this.hdown.addEventListener("touchstart",function(){this.hourChange( 1);}.bind(this)  ,{passive: false});
      this.hdown.addEventListener("mouseup" ,function(){this.hourChange( 0);}.bind(this)  ,false);
      this.hdown.addEventListener("touchend" ,function(){this.hourChange( 0);}.bind(this)  ,false);
      this.hdown.addEventListener("mouseleave",function(){this.hourChange( 0);}.bind(this)  ,false);
      this.mdown.addEventListener("mousedown" ,function(){this.minuteChange( 1);}.bind(this),false);
      this.mdown.addEventListener("touchstart",function(){this.minuteChange( 1);}.bind(this),{passive: false});
      this.mdown.addEventListener("mouseup" ,function(){this.minuteChange( 0);}.bind(this),false);
      this.mdown.addEventListener("touchend" ,function(){this.minuteChange( 0);}.bind(this),false);
      this.mdown.addEventListener("mouseleave",function(){this.minuteChange( 0);}.bind(this),false);
  
      let inp=document.querySelectorAll('input.mamTimePicker');
      let i;
      for(i=0;i<inp.length;i++){
        if(inp[i].getAttribute('type')==null||inp[i].getAttribute('type')==""||inp[i].getAttribute('type').toLowerCase()=='text'){
          this.inputs.push(inp[i]);
        }
      }
      for(i=0;i<this.inputs.length;i++){
        this.inputs[i].setAttribute("maxlength",4+this.delimiter.length);
        //必要であればplaceholder属性設定
        //this.inputs[i].setAttribute("placeholder","hh"+this.delimiter+"mm");
        this.inputs[i].addEventListener("keydown",this.canOpen.bind(this));
        this.inputs[i].addEventListener("keyup",this.open.bind(this));
        this.inputs[i].addEventListener("click",this.open.bind(this));
        this.inputs[i].addEventListener("focus",this.open.bind(this));
      }
      //閉じる設定(カレンダー以外をクリック時には閉じる
      //blurで処理したいがそれではうまくいかなかった
      document.body.addEventListener("click",this.otherClick.bind(this));
      document.documentElement.addEventListener("click",this.otherClick.bind(this));
      setInterval(this.interval.bind(this),20);
    }
  
    this.hourDown=function(event){
      this.event.hdown=true;
      this.event.hdowny=event.touches[0].pageY;
    }
    this.hourUp=function(event){
      this.event.hdown=false;
    }
    this.hourMove=function(event){
      let val=0;
      if(this.event.hdown&&this.event.flag==false){
        if((this.event.hdowny-event.touches[0].pageY)>2){
          val=1;
        }else if((this.event.hdowny-event.touches[0].pageY)<-2){
          val=-1;
        }
        if(val!=0&&this.event.hflag==0){
          this.event.hdowny=event.touches[0].pageY;
          this.event.hflag=val;
          this.event.h=Math.floor(this.h)+val;
        }
      }
      event.stopPropagation();
      event.preventDefault();
    }
    this.minuteDown=function(event){
      this.event.mdown=true;
      this.event.mdowny=event.touches[0].pageY;
    }
    this.minuteUp=function(event){
      this.event.mdown=false;
    }
    this.minuteMove=function(event){
      let val=0;
      if(this.event.mdown&&this.event.flag==false){
        if((this.event.mdowny-event.touches[0].pageY)>2){
          val=1;
        }else if((this.event.mdowny-event.touches[0].pageY)<-2){
          val=-1;
        }
        if(val!=0&&this.event.mflag==0){
          this.event.mdowny=event.touches[0].pageY;
          this.event.mflag=val;
          this.event.m=Math.floor(this.m)+val;
        }
      }
      event.stopPropagation();
      event.preventDefault();
    }
  
  
    this.hourWheel=function(event){
      let val=0;
      if(this.event.flag){
      }else{
        let delta=((event.deltaY || -event.wheelDelta || event.detail) >> 10) || 1;
        if(delta>0){
          val=1;
        }else{
          val=-1;
        }
        if(this.event.hflag==0){
          this.event.hflag=val;
          this.event.h=Math.floor(this.h)+val;
        }
      }
      event.stopPropagation();
      event.preventDefault();
    }
    this.minuteWheel=function(event){
      let val=0;
      if(this.event.flag){
      }else{
        let delta=((event.deltaY || -event.wheelDelta || event.detail) >> 10) || 1;
        if(delta>0){
          val=1;
        }else{
          val=-1;
        }
        if(this.event.mflag==0){
          this.event.mflag=val;
          this.event.m=Math.floor(this.m)+val;
        }
      }
      event.stopPropagation();
      event.preventDefault();
      return false;
    }
    //keydown
    this.canOpen=function(event){
      let k=event.key;
      if(k=="Tab"){this.close();return true;}
      if(k=="Shift"){return true;}
      if(k=="Escape"){event.target.value="";return true;}
      if(k=="ArrowLeft"||k=="ArrowRight"||k=="Delete"||k=="Backspace"||k=="Left"||k=="Right"||k=="Del"||
      k=="0"||k=="1"||k=="2"||k=="3"||k=="4"||k=="5"||k=="6"||k=="7"||k=="8"||k=="9"){return true;}
      if(k==this.delimiter){return true;}
      if(event.key=="c" && event.ctrlKey){return true;}
      if(event.key=="v" && event.ctrlKey){return true;}
      if(event.key=="x" && event.ctrlKey){return true;}
      if(event.type.match(/click/i)){return true;}
      event.stopPropagation();
      event.preventDefault();
    }
    //click,keyup
    this.open=function(event){
      this.target=event.target;
      let h=-1,m=-1,s="";
      if(this.target.value){
        s=this.target.value;
      }
      if(s.match(/^[0-9]{1,4}$/)){
        h=parseInt(s.substr(0,2));
        m=parseInt(s.substr(2,2));
      }else{
        if(this.delimiter!=''){
          let sp=s.split(this.delimiter);
          if(sp.length==2){
            h=parseInt(sp[0]);
            m=parseInt(sp[1]);
          }else{
            h=parseInt(sp[0]);
          }
        }
      }
      if(h<0||h>23||isNaN(h)){h=0;}
      if(m<0||m>59||isNaN(m)){m=0;}
      this.h=h;
      this.m=m;
  
      let rect=event.target.getBoundingClientRect();
      this.obj.style.left=(rect.left+window.pageXOffset)+"px";
      this.obj.style.top=(rect.bottom+window.pageYOffset)+"px";
      this.showModal();
    }
    this.showModal=function(){
      this.obj.style.display='block';
      this.show=true;
      this.drawCanvas(this.canH,this.arrH,this.h);
      this.drawCanvas(this.canM,this.arrM,this.m);
    }
    this.close=function(){
      this.obj.style.display='none';
      this.show=false;
    }
    this.drawCanvas=function(can,arr,val){
      let ctx=can.getContext("2d");
      ctx.fillStyle="rgb(255,255,255)";
      ctx.fillRect(0,0,can.width,can.height);
      ctx.fillStyle="rgb(0,0,0)";
      ctx.fillRect(0,can.height*2/5,can.width,1);
      ctx.fillRect(0,can.height*3/5,can.width,1);
  
      let ival=Math.floor(val);
      let dval=val-ival;//端数(小数点以下)
  
      let def=Math.floor(val);
      for(let i=-3;i<4;i++){
        let num=this.getVal(arr,ival+i);
        let ypos=(i+2-dval)*this.size+can.height/10;
        let col=Math.floor(Math.abs(ypos-can.height/2)*4);
        let size=this.size-Math.floor(Math.abs(ypos-can.height/2)/5);
        ctx.fillStyle="rgb("+col+","+col+","+col+")";
        ctx.font=size+"px "+this.family;
        ctx.textAlign="center";
        //ctx.textBaseline="top";
        //ctx.textBaseline="hanging";
        ctx.textBaseline="middle";
        ctx.fillText(arr[num],can.width/2,ypos+2);
      }
    }
    this.getVal=function(arr,val){
      let n=arr.length;
      let v=val%n;
      if(v<0){v+=n;}
      return v;
    }
    this.setVal=function(){
      let h=this.getVal(this.arrH,this.h);
      let m=this.getVal(this.arrM,this.m);
      h=this.arrH[h];
      m=this.arrM[m];
      var inputvalue=h+this.delimiter+m
      var day=$(this.target).attr('day')
  
      this.target.value=inputvalue;
      var wt=':worktime';
      var st=':start';
      var ed=':end';
      var ht=':halftime';
      var start = document.getElementById(day+st).value;
      var end = document.getElementById(day+ed).value;
      var half = document.getElementById(day+ht).value;
      var sth=Number(start.substr(0,2));
      var stm=Number(start.substr(3,2));
  
      var edh=Number(end.substr(0,2));
      var edm=Number(end.substr(3,2));
  
      var hfh=Number(half.substr(0,2));
      var hfm=Number(half.substr(3,2));
      
      console.log('h'+hfh);
      console.log('m'+hfm);
      var wtm=(edh-sth-hfh)*60+(edm-stm-hfm);
      console.log('wtm='+wtm);
      var tenhour=Math.floor(wtm/600);
      console.log('th'+tenhour);
      var hour=Math.floor(wtm%600/60);
      console.log('hour='+hour);
      var tenminutes=wtm%600%60;
      console.log('tm1='+tenminutes);
      var tenminutes=Math.floor(tenminutes/10);
      console.log('tm2='+tenminutes);
      var minutes=Math.floor(wtm%600%60%10%10);
      console.log('minutes='+minutes);
      var time=String(tenhour)+String(hour)+":"+String(tenminutes)+String(minutes);
      
      var resultForm = document.getElementById(day+wt);
      resultForm.value = time;
  
      $.ajax({
        url: "/RbAjaxServlet",
        type: "POST",
        data: {
          inputvalue : inputvalue,
          day : day,
          name :$(this.target).attr('name'),
        }
      }).done(function (result) {
        
        }).fail(function () {
          // 通信失敗時のコールバック
          //alert("更新に失敗しました");
        }).always(function (result) {
        // 常に実行する処理
      });
      this.close();
    }
    this.hourChange=function(val){
      if(val==0){
        this.event.flag=false;
        return;
      }else{
        this.event.flag=true;
      }
      if(this.event.hflag==0){
        this.event.hflag=val;
        this.event.h=Math.floor(this.h)+val;
      }
      event.stopPropagation();
      event.preventDefault();
    }
    this.minuteChange=function(val){
      if(val==0){
        this.event.flag=false;
        return;
      }else{
        this.event.flag=true;
      }
      if(this.event.mflag==0){
        this.event.mflag=val;
        this.event.m=Math.floor(this.m)+val;
      }
      event.stopPropagation();
      event.preventDefault();
    }
  
    this.interval=function(event){
      if(!this.show){return;}
      if(this.event.hflag!=0){
        let v=this.h+this.event.hflag/8;
  
        if(this.event.hflag==1){
          if(this.event.h>v){
            this.h=v;
          }else{
            this.h=this.event.h;
            if(this.event.flag){
              this.event.h=this.h+this.event.hflag;
            }else{
              this.event.hflag=0;
            }
          }
        }else{
          if(this.event.h<v){
            this.h=v;
          }else{
            this.h=this.event.h;
            if(this.event.flag){
              this.event.h=this.h+this.event.hflag;
            }else{
              this.event.hflag=0;
            }
          }
        }
        this.drawCanvas(this.canH,this.arrH,this.h);
      }
      if(this.event.mflag!=0){
        let v=this.m+this.event.mflag/8;
  
        if(this.event.mflag==1){
          if(this.event.m>v){
            this.m=v;
          }else{
            this.m=this.event.m;
            if(this.event.flag){
              this.event.m=this.m+this.event.mflag;
            }else{
              this.event.mflag=0;
            }
          }
        }else{
          if(this.event.m<v){
            this.m=v;
          }else{
            this.m=this.event.m;
            if(this.event.flag){
              this.event.m=this.m+this.event.mflag;
            }else{
              this.event.mflag=0;
            }
          }
        }
        this.drawCanvas(this.canM,this.arrM,this.m);
      }
    }
  
  
  
    //blurで処理したいがそれではうまくいかなかった
    this.otherClick=function(event){
      if(this.event.hdown){this.event.hdown=false;return;}
      if(event.target==this.target){return;}
      if(event.target==this.obj){return;}
      if(event.target.parentNode!=null){
        if(event.target.parentNode==this.obj){return;}
        if(event.target.parentNode.parentNode!=null){
          if(event.target.parentNode.parentNode==this.obj){return;}
          if(event.target.parentNode.parentNode.parentNode!=null){
          if(event.target.parentNode.parentNode.parentNode==this.obj){return;}
          if(event.target.parentNode.parentNode.parentNode.parentNode==this.obj){return;}
          }
        }
      }
      this.close();
    }
  
    window.addEventListener("DOMContentLoaded",this.init.bind(this));
  }
  var MamTimePicker=new TMamTimePicker();
  