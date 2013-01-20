/**
 * base config
 */
$package("loon.core");

var config = {
	queue: [],
	updateRate: 1000,
    autorun: true,  
	blocking: false,
	useCanvasUpdate: true,
	beforeLoadMainFunction : true,
    beforeMainImport : true
};

/**
 * loon入口函数
 */
/*abstract*/function OnMain(){}

/**
 * loon主进程
 */
/*abstract*/function OnProcess(/*loon.Core*/core){}

/**
 * loon渲染更新接口(API实时刷新，如果config.useCanvasUpdate被设定为false则不调用)
 */
/*abstract*/function OnUpdate(/*loon.Renderer*/render,/*float*/ elapsed){}

function Setting(){
	var preload = [];
    var width = 320;
    var height = 320;
    var fps = 30;
    var showFps = false;
}

var _game_core = null;

function register(/*Setting*/setting){
		_game_core = new loon.Core(setting.width, setting.height);
		_game_core.setFPS(setting.fps);
		_game_core.setShowFPS(setting.showFps);
		if(setting.preload!=null&&setting.preload.length>0){
		     _game_core.preload(setting.preload);
		}
}

/**
 * 返回当前游戏容器
 */
var context = function(){
   return _game_core;
}

function $package(packName) {
  var i;
  var pkg = window;
  var parts = packName.split('.');
  for (i = 0; i < parts.length; i++) {
    if (typeof pkg[parts[i]] === 'undefined') {
      pkg[parts[i]] = {};
    }
    pkg = pkg[parts[i]];
  }
}

function Point() {
    var x = 0;
	var y = 0;	
}

function RectBox() {
    var x = 0;
    var y = 0;
    var width = 1;
    var height = 1;
}

/**
 * begin js
 */
(function(window, undefined){

/**
 * 获得当前系统时间
 */
window.getTime = (function() {
    if (window.performance && window.performance.now) {
        return function() {
            return window.performance.now();
        };
    } else if (window.performance && window.performance.webkitNow) {
        return function() {
            return window.performance.webkitNow();
        };
    } else {
        return Date.now;
    }
}());

/**
 * 同步处理指定函数
 * sample:
 *  	$sync(function() {
 *			//...
 *		});  
 */
function $sync(/*function*/ callback) {
	config.queue.push(callback);
	if (config.autorun && !config.blocking) {
		_process();
	}
}

function _process() {
	var start = window.getTime();
	while ( config.queue.length && !config.blocking ) {
		if ( config.updateRate <= 0 || ((window.getTime() - start) < config.updateRate) ) {
			config.queue.shift()();
		} else {
			setTimeout(_process, 13 );
			break;
		}
	}
}

/**
 * 仿java中Runnable接口
 */
function Runnable(callback) {
      this.callback = callback;
};

//构建Runnbale原型(该类仅loon内部有效，不能直接移植为其它用途)
Runnable.prototype = {
	constructor: Runnable,
	run: function() {
         if(this.callback!=null){
		    this.callback();
		 }
	}
};

/**
 * 仿java中Thread类
 */
function Thread(runnable, name) {
	this.running = false;
	this.name = 'Thread';
	if (runnable instanceof Runnable) {
		this.runnable = runnable;
		this.name = name || 'Thread';
	}
	else if (typeof runnable === 'string') {
		this.name = runnable;
	}

	this.context = null;
	this.priority = 0;
};

//构建Thread原型(该类仅loon内部有效,不能直接移植为其它用途)
Thread.prototype = {
	constructor: Thread,

	getName: function() {
		return this.name;
	},
	
	getPriority: function() {
		return this.priority;
	},
	
	setName: function(name) {
		this.name = name || this.name;
	},
	
	setPriority: function(priority) {
		this.priority = priority;
	},
	
	start: function() {
        this.running = true ;
	},

	_isRunning: function(){
	   return this.running;  
	},
	
	run: function() {
		if(running && runnable!=null){
			this.runnable.run();
		}
	},
	
	stop: function() {
		 this.running = false;
	},
	
	toString: function() {
		return this.name;
	}
	
};

/**
 * 键值对构建用类
 */
function DictionaryEntry(k, v){

	this.key = k;

	this.value = v;

	this.valueOf = function(){ 
		return this.value; 	
	};

	this.toString = function(){ 
		return String(this.value);
	};
}

/**
 * 线性数组遍历用类
 */
function Iterator(arr){
	var a=arr;
	var position=0;
	this.element=a[position]||null;
	this.atEnd=function(){
		return (position>=a.length);
	};
	this.get=function(){
		if(this.atEnd()){
			return null;	
		}
		this.element=a[position++];
		return this.element;	
	};
	this.map=function(fn,scope){
		return dojo.map(a, fn, scope);
	};
	this.reset=function(){
		position=0;
		this.element=a[position];
	};
}

/**
 * 键值对遍历用类
 */
function DictionaryIterator(obj){
	var a=[];	
	var testObject={};
	for(var p in obj){
		if(!testObject[p]){
			a.push(obj[p]);	
		}
	}
	var position=0;
	this.element=a[position]||null;
	this.atEnd=function(){
		return (position>=a.length);	
	};
	this.get=function(){
		if(this.atEnd()){
			return null;		
		}
		this.element=a[position++];
		return this.element;	
	};
	this.map=function(fn, scope){
		return dojo.map(a, fn, scope);
	};
	this.reset=function() { 
		position=0; 
		this.element=a[position];
	};
};


/**
 * 自动填充set/get方法（仿C#）
 */
if (typeof Object.defineProperty !== 'function') {
    Object.defineProperty = function(obj, prop, desc) {
        if ('value' in desc) {
            obj[prop] = desc.value;
        }
        if ('get' in desc) {
            obj.__defineGetter__(prop, desc.get);
        }
        if ('set' in desc) {
            obj.__defineSetter__(prop, desc.set);
        }
        return obj;
    };
}
if (typeof Object.defineProperties !== 'function') {
    Object.defineProperties = function(obj, descs) {
        for (var prop in descs) {
            if (descs.hasOwnProperty(prop)) {
                Object.defineProperty(obj, prop, descs[prop]);
            }
        }
        return obj;
    };
}
if (typeof Object.extend !== 'function') {
    Object.extend = function(prototype, descs) {
        function F() {
        }

        F.prototype = prototype;
        var obj = new F();
        if (descs != null) {
            Object.defineProperties(obj, descs);
        }
        return obj;
    };
}
if (typeof Object.getPrototypeOf !== 'function') {
    Object.getPrototypeOf = function(obj) {
        return obj.__proto__;
    };
}

if (typeof Function.prototype.bind !== 'function') {
    Function.prototype.bind = function(thisObject) {
        var func = this;
        var args = Array.prototype.slice.call(arguments, 1);
        var Nop = function() {
        };
        var bound = function() {
            var a = args.concat(Array.prototype.slice.call(arguments));
            return func.apply(
                this instanceof Nop ? this : thisObject || window, a);
        };
        Nop.prototype = func.prototype;
        bound.prototype = new Nop();
        return bound;
    };
}

/**
 * 获得当前应指向帧
 */
window.requestFrame =
    window.requestFrame ||
    window.mozrequestFrame ||
    window.webkitrequestFrame ||
    window.msrequestFrame ||
    (function() {
        var lastTime = window.getTime();
        var frame = 1000 / 60;
        return function(func) {
            var currentTime = window.getTime();
            var _id = setTimeout(function() {
                func(window.getTime());
            }, Math.max(0, lastTime + frame - currentTime));
            lastTime = currentTime;
            return _id;
        };
    }());


var loon = function(modules) {
    if (modules != null) {
        if (!(modules instanceof Array)) {
            modules = Array.prototype.slice.call(arguments);
        }
        modules = modules.filter(function(module) {
            return [module].join();
        });
    }
    (function include(module, prefix) {
        var submodules = [],
            i, len;
        for (var prop in module) {
            if (module.hasOwnProperty(prop)) {
                if (typeof module[prop] === 'function') {
                    window[prop] = module[prop];
                } else if (typeof module[prop] === 'object' && module[prop] !== null && Object.getPrototypeOf(module[prop]) === Object.prototype) {
                    if (modules == null) {
                        submodules.push(prop);
                    } else {
                        i = modules.indexOf(prefix + prop);
                        if (i !== -1) {
                            submodules.push(prop);
                            modules.splice(i, 1);
                        }
                    }
                }
            }
        }
        for (i = 0, len = submodules.length; i < len; i++) {
            include(module[submodules[i]], prefix + submodules[i] + '.');
        }
    }(loon, ''));

    if (modules != null && modules.length) {
        throw new Error('Cannot load module: ' + modules.join(', '));
    }
};

window.loon = loon;

window.addEventListener("message", function(msg, origin) {
    try {
        var data = JSON.parse(msg.data);
        if (data.type === "event") {
            loon.Core.instance.dispatchEvent(new loon.Event(data.value));
        } else if (data.type === "debug") {
            switch (data.value) {
                case "start":
                    loon.Core.instance.start();
                    break;
                case "pause":
                    loon.Core.instance.pause();
                    break;
                case "resume":
                    loon.Core.instance.resume();
                    break;
                case "tick":
                    loon.Core.instance._tick();
                    break;
                default:
                    break;
            }
        }
    } catch (e) {
    }
}, false);


/**
 * 构建一个js类
 */
loon.Class = function(superclass, definition) {
    return loon.Class.extend(superclass, definition);
};

/**
 * 获取指定对象的类型 
 */
loon.Class.typeOf = function(value) {
  var s = typeof value;
  if (s == 'object') {
    if (value) {
      if (value instanceof Array) {
        return 'array';
      } else if (value instanceof Object) {
        return s;
      }
      var className = Object.prototype.toString.call(value);
      if (className == '[object Window]') {
        return 'object';
      }
      if ((className == '[object Array]' ||
           typeof value.length == 'number' &&
           typeof value.splice != 'undefined' &&
           typeof value.propertyIsEnumerable != 'undefined' &&
           !value.propertyIsEnumerable('splice')

          )) {
        return 'array';
      }
      if ((className == '[object Function]' ||
          typeof value.call != 'undefined' &&
          typeof value.propertyIsEnumerable != 'undefined' &&
          !value.propertyIsEnumerable('call'))) {
        return 'function';
      }

    } else {
      return 'null';
    }
  } else if (s == 'function' && typeof value.call == 'undefined') {
    return 'object';
  }
  return s;
};

//loon类型判定开始

loon.isDef = function(val) {
  return val !== undefined;
};

loon.isNull = function(val) {
  return val === null;
};

loon.isArray = function(val) {
  return loon.Class.typeOf(val) == 'array';
};

loon.isString = function(val) {
  return typeof val == 'string';
};

loon.isBoolean = function(val) {
  return typeof val == 'boolean';
};

loon.isNumber = function(val) {
  return typeof val == 'number';
};

loon.isFunction = function(val) {
  return loon.Class.typeOf(val) == 'function';
};

loon.isObject = function(val) {
  var type = typeof val;
  return type == 'object' && val != null || type == 'function';
};

//loon类型判定结束

/**
 * 仿java继承
 */
loon.Class.extend = function(superclass, definition) {
    if (superclass == null && definition){
        throw new Error("superclass is undefined (loon.Class.extend)");
    }else if(superclass == null){
        throw new Error("definition is undefined (loon.Class.extend)");
    }
    if (arguments.length === 0) {
        return loon.Class.extend(Object, definition);
    } else if (arguments.length === 1 && typeof arguments[0] !== 'function') {
        return loon.Class.extend(Object, arguments[0]);
    }

    for (var prop in definition) {
        if (definition.hasOwnProperty(prop)) {
            if (typeof definition[prop] === 'object' && definition[prop] !== null && Object.getPrototypeOf(definition[prop]) === Object.prototype) {
                if (!('enumerable' in definition[prop])) {
                    definition[prop].enumerable = true;
                }
            } else {
                definition[prop] = { value: definition[prop], enumerable: true, writable: true };
            }
        }
    }
    var Constructor = function() {
        if (this instanceof Constructor) {
            Constructor.prototype.initialize.apply(this, arguments);
        } else {
            return new Constructor();
        }
    };
    Constructor.prototype = Object.extend(superclass.prototype, definition);
    Constructor.prototype.constructor = Constructor;
    if (Constructor.prototype.initialize == null) {
        Constructor.prototype.initialize = function() {
            superclass.apply(this, arguments);
        };
    }

    var tree = this.getSupers(superclass);
    for (var i = tree.length - 1; i >= 0; i--) {
        if (typeof tree[i]._inherited === 'function') {
            tree[i]._inherited(Constructor);
            break;
        }
    }

    return Constructor;
};


//loon类辅助功能开始

/**
 *获得指定构造对象所有父类 
 */
loon.Class.getSupers = function(Constructor) {
    var ret = [];
    var C = Constructor;
    var proto = C.prototype;
    while (C !== Object) {
        ret.push(C);
        proto = Object.getPrototypeOf(proto);
        C = proto.constructor;
    }
    return ret;
};

/**
 *克隆指定对象 
 */
loon.Class.clone = function(obj) {
  var type = loon.typeOf(obj);
  if (type == 'object' || type == 'array') {
    if (obj.clone) {
      return obj.clone();
    }
    var clone = type == 'array' ? [] : {};
    for (var key in obj) {
      clone[key] = loon.clone(obj[key]);
    }
    return clone;
  }
  return obj;
};

/**
 * 获得main.js所在地址
 */
loon.basePath = null;
loon.main = function() {
  if (loon.basePath === null) {
    var scripts = document.getElementsByTagName('script');
    for (var i = 0; i < scripts.length; i++) {
      var match = scripts[i].src.match(/(^|.*[\\\/])main.js(?:\?.*)?$/i);
      if (match) {
        loon.basePath = match[1];
        break;
      }
    }
  }
  return loon.basePath;
};

/**
 * 实例化指定对象
 */
loon.newInstance = function(name) {
  var currents, parts, constructorName;
  parts = name.split('.');
  constructorName = parts[parts.length - 1];
  currents = window;
  for (var i = 0; i < parts.length - 1; i++) {
    currents = currents[parts[i]];
  }
  return new currents[constructorName]();
};

/**
 * 仿写List(Java集合类中只有接口List，此类实现参考C#而非Java（因为结构简单）)
 */
loon.List = function(arr){

	var items=[];
	if(arr) items=items.concat(arr);
	this.count=items.length;

	this.add=function(obj){
		items.push(obj);
		this.count=items.length;
	};

	this.addRange=function(a){
		if(a.getIterator){
			var e=a.getIterator();
			while(!e.atEnd()){
				this.add(e.get());
			}
			this.count=items.length;
		}else{
			for(var i=0; i<a.length; i++){
				items.push(a[i]);
			}
			this.count=items.length;
		}
	};

	this.clear=function(){
		items.splice(0, items.length);
		this.count=0;
	};

	this.clone=function(){
		return new loon.ArrayList(items);
	};

	this.contains=function(obj){
		for(var i=0; i < items.length; i++){
			if(items[i] == obj) {
				return true;	
			}
		}
		return false;
	};

	this.getIterator=function(){
		return new Iterator(items);	
	};

	this.indexOf=function(obj){
		for(var i=0; i < items.length; i++){
			if(items[i] == obj) {
				return i;
			}
		}
		return -1;
	};

	this.insert=function(i, obj){
		items.splice(i,0,obj);
		this.count=items.length;
	};

	this.item=function(i){
		return items[i];
	};

	this.remove=function(obj){
		var i=this.indexOf(obj);
		if(i >=0) {
			items.splice(i,1);
		}
		this.count=items.length;
	};

	this.removeAt=function(i){
		items.splice(i,1);
		this.count=items.length;
	};

	this.reverse=function(){
		items.reverse();
	};

	this.sort=function(fn){
		if(fn){
			items.sort(fn);
		}else{
			items.sort();
		}
	};

	this.setByIndex=function(i,obj){
		items[i]=obj;
		this.count=items.length;
	};

	this.toArray=function(){
		return [].concat(items);
	}

	this.toString=function(delim){
		return items.join((delim||","));
	};
};

/**
 * 仿写Dictionary(java中没有此键值对集合对象，仿写C#实现)
 * sample:
 * var d = new loon.Dictionary();
 *	   d.add("name","value");  
 */
loon.Dictionary = function(dictionary){

	var items={};
	this.count=0;

	var testObject={};

	this.add=function(k, v){
		var b=(k in items);
		items[k]=new DictionaryEntry(k,v);
		if(!b){
			this.count++;
		}
	};

	this.clear=function(){
		items={};
		this.count=0;
	};

	this.clone=function(){
		return new loon.Dictionary(this);
	};

	this.contains=this.containsKey=function(k){
		if(testObject[k]){
			return false;			
		}
		return (items[k]!=null);	
	};

	this.containsValue=function(v){
		var e=this.getIterator();
		while(e.get()){
			if(e.element.value==v){
				return true;
			}
		}
		return false;
	};

	this.entry=function(k){
		return items[k];
	};

	this.getKeys=function(){
		return (this.getIterator()).map(function(entry){ 
			return entry.key; 
		});	
	};

	this.getValues=function(){
		return (this.getIterator()).map(function(entry){ 
			return entry.value; 
		});
	};

	this.item=function(k){
		if(k in items){
			return items[k].valueOf();
		}
		return undefined;
	};

	this.getIterator=function(){
		return new DictionaryIterator(items);
	};

	this.remove=function(k){
		if(k in items && !testObject[k]){
			delete items[k];
			this.count--;
			return true;
		}
		return false;
	};

	if (dictionary){
		var e=dictionary.getIterator();
		while(e.get()) {
			 this.add(e.element.key, e.element.value);
		}
	}
};

/**
 * 堆栈用类
 */
loon.Stack=function(arr){

	var q=[];
	if (arr) q=q.concat(arr);
	this.count=q.length;

	this.clear=function(){
		q=[];
		this.count=q.length;
	};

	this.clone=function(){
		return new loon.Stack(q);
	};

	this.contains=function(o){
		for (var i=0; i<q.length; i++){
			if (q[i] == o){
				return true;
			}
		}
		return false;
	};

	this.copyTo=function(arr, i){
		arr.splice(i,0,q);
	};

	this.getIterator=function(){
		return new Iterator(q);
	};

	this.peek=function(){
		return q[(q.length-1)];	
	};

	this.pop=function(){
		var r=q.pop();
		this.count=q.length;
		return r;
	};

	this.push=function(o){
		this.count=q.push(o);
	};

	this.toArray=function(){
		return [].concat(q);
	};
}

var _objects = new loon.List();

function $import(){
        var file = null;
		var element = null;
		var doc = document.getElementsByTagName('head').item(0);
        for (i = 0; i < arguments.length; i++) {
			    file = arguments[i];
				if (!_objects.contains(file)) {
					   element = document.createElement("script");
					if (element != undefined) {
					   if (navigator.userAgent.indexOf("Trident/5") > -1) {
							element.src = file;
							element.serial = i + 1;
                       }
                       else {
							element.async = false;
							element.src = file;
                       }
					   if(doc != undefined){
							  doc.appendChild(element);
					   }else{
                              document.body.appendChild(element);
					   }
					   _objects.add(file);
					}
               }
        }
}

/**
 * 角度转弧度常量。
 */
loon.DEG_TO_RAD = Math.PI / 180;

/**
 * 弧度转角度常量。
 */
loon.RAD_TO_DEG = 180 / Math.PI;

loon.rToD = function(rad) {
	return rad * loon.DEG_TO_RAD;
};

loon.dToR = function(deg) {
	return deg * loon.RAD_TO_DEG;
};

/**
 *返回一个随机数 
 */
loon.random = function(num) {
    return Math.random() * num;
}

/**
 *返回一个随机数 
 */
loon.random = function(start,end) {
    return start + Math.random() * (end - start);
}

/**
 * 返回一个指定id的窗体元素 
 */
loon.find = function(id) {
  if (document.getElementById && document.getElementById(id)) {
    return document.getElementById(id);
  } else if (document.all && document.all(id)) {
    return document.all(id);
  } else if (document.layers && document.layers[id]) {
    return document.layers[id];
  } else {
    return null;
  }
};

/**
 * 尝试创建一个http请求
 */
loon.request = function(mimeType) {
  if (window.XMLHttpRequest) {
    var req = new XMLHttpRequest();
    if (mimeType !== null && req.overrideMimeType) {
      req.overrideMimeType(mimeType);
    }
    return req;
  }
  else {
    return new ActiveXObject('Msxml2.XMLHTTP');
  }
};

/**
 * 验证指定类是否存在
 */
loon.classExists = function(fqcn) {
  var parts = fqcn.split(".");
  var partial = "";
  var i;
  for (i = 0; i < parts.length; i++) {
    partial += (i > 0 ? "." : "") + parts[i];
    if (eval("typeof " + partial) === 'undefined') return false;
  }
  return eval("typeof " + fqcn) === 'function';
};

/**
 * 获取指定js资源
 */
loon.js = function(url, callback, failCallback) {
  var req = loon.request("text/plain");
  req.onreadystatechange = function() {
    if (req.readyState === 4) {
      if (req.status === 200 || req.status === 0) {
        var e = document.createElement("script");
        e.language = "javascript";
		if(req.responseText!=null&&req.responseText.length>0){
             e.text = req.responseText;
		}else{
			 e.src = url;
		}
        e.type = "text/javascript";
        var head = document.getElementsByTagName("head")[0];
        if (head === null) {
          head = document.createElement("head");
          var html = document.getElementsByTagName("html")[0];
          html.insertBefore(head, html.firstChild);
        }
        try {
          head.appendChild(e);
        } catch (err) {
          alert("There was an error loading the script from '" + url + "': " + err);
          return;
        }
        if (callback) {
          callback(url);
        }               
        } else if (req.status === 404) {
           alert("404 error: the requested resource '" + url + "' could not be found.");
        if (failCallback) {
          failCallback();
        }
		if(e.src != null){
		   return list;
		}
      }
    }
  };
  req.open("GET",url, false);
  req.send(null);
};

/**
 *创建一个指定格式的窗体元素 
 */
loon.make = function(type, props)
{
	var dom = document.createElement(type);
	for(var p in props) 
	{
		var val = props[p];
		if(p == "style")
		{
			for(var s in val) dom.style[s] = val[s];
		}else
		{
			dom[p] = val;
		}
	}
	return dom;
};

/**
 * html5存储器封装
 */
loon.store = {
	isSupported: function() {
	  try {
		return 'localStorage' in window && window['localStorage'] !== null;
	  } catch (e) {
		return false;
	  }
	},
	getItem: function(key) {
       return isSupported() ? getItem(key) : undefined;
    },
	setItem: function(key, value) {
	  if (isSupported()) {
		localStorage[key] = value;
	  }
	},
    remove: function(key) {
	  if (isSupported()) {
		localStorage.removeItem(key);
	  }
	},
    clear: function() {
	  if (isSupported()) {
		clear();
	  }
	},
    length: function() {
	  if (isSupported()) {
		return localStorage.length;
	  }
	},
	getKey: function(idx) {
	  if (isSupported()) {
		return localStorage.key(idx);
	  }
	}
};


//loon类辅助功能结束
loon.System = {

    _use_touch: false,
    VERSION: "0.3.3-min",

    //判断是否正运行于手机环境
	ON_MOBILE: (navigator.userAgent.indexOf('mobile') != -1 || navigator.userAgent.indexOf('android') != -1),

    //获得标准userAgent名称数组 
    UA: navigator.userAgent.match(/(opera|ie|firefox|chrome|version)[\s\/:]([\w\d\.]+)?.*?(safari|version[\s\/:]([\w\d\.]+)|$)/) || [null, 'unknown', 0],

	//浏览器名称（前缀缩写）
    BROWSER: (function() {
        var ua = navigator.userAgent;
        if (ua.indexOf('Opera') !== -1) {
            return 'O';
        } else if (ua.indexOf('MSIE') !== -1) {
            return 'ms';
        } else if (ua.indexOf('WebKit') !== -1) {
            return 'webkit';
        } else if (navigator.product === 'Gecko') {
            return 'Moz';
        } else {
            return '';
        }
    }()),

	//浏览器是否支持Touch
    SUPPORT_TOUCH: (function() {
		if(this._use_touch){
		   return _use_touch;
		}
        var div = document.createElement('div');
        div.setAttribute('ontouchstart', 'return');
        return (this._use_touch = (typeof div.ontouchstart === 'function'));
    }()),

    //IOS屏幕大小修正
    RETINA_IOS_DISPLAY: (function() {
        if (navigator.userAgent.indexOf('iPhone') !== -1 && window.devicePixelRatio === 2) {
            var viewport = document.querySelector('meta[name="viewport"]');
            if (viewport == null) {
                viewport = document.createElement('meta');
                document.head.appendChild(viewport);
            }
            viewport.setAttribute('content', 'width=640');
            return true;
        } else {
            return false;
        }
    }()),

    //判断是否支持flash音频
    SUPPORT_FLASH_SOUND: (function() {
        var ua = navigator.userAgent;
        var vendor = navigator.vendor || "";
        return (location.href.indexOf('http') === 0 && ua.indexOf('Mobile') === -1 && vendor.indexOf('Apple') !== -1);
    }()),

    USE_DEFAULT_EVENT_TAGS: ['input', 'textarea', 'select', 'area'],

    CANVAS_DRAWING_METHODS: [
        'putImageData', 'drawImage', 'drawFocusRing', 'fill', 'stroke',
        'clearRect', 'fillRect', 'strokeRect', 'fillText', 'strokeText'
    ],

    KEY_BIND_TABLE: {
        37: 'left',
        38: 'up',
        39: 'right',
        40: 'down'
    },
    PREVENT_DEFAULT_KEY_CODES: [37, 38, 39, 40, 32],
    SOUND_ENABLED_ON_MOBILE_SAFARI: false,
    USE_WEBAUDIO: (function(){
        return location.protocol !== 'file:';
    }()),
    USE_ANIMATION: true
};

/**
 * 构建loon事件用类
 */
loon.Event = loon.Class.extend({

    initialize: function(type) {
        this.type = type;
        this.target = null;
        this.x = 0;
        this.y = 0;
        this.localX = 0;
        this.localY = 0;
    },
    _initPosition: function(pageX, pageY) {
        var _core = loon.Core.instance;
        this.x = this.localX = (pageX - _core._pageX) / _core.scale;
        this.y = this.localY = (pageY - _core._pageY) / _core.scale;
    }
});

/**
 * 构建Color用类
 */
loon.Color = loon.Class.extend({

	r:0,
	g:0,
	b:0,
	a:0,

    initialize: function(r,g,b,a) {
     this.set(r,g,b,a);
    },

    set:function(r,g,b,a) {
     if(r) this.r = r;
     if(g) this.g = g;
     if(b) this.b = b;
     if(a) this.a = a;
    },

	getColorAsRGBAString: function(r,g,b,a) {
		r = Math.round(r);
		g = Math.round(g);
		b = Math.round(b);
		if(g==undefined) {
			return "rgba(" + r + "," + g + "," + b + "," + a + ")";
		} 
		if(a==undefined) {
			return "rgba(" + r + "," + g + "," + b + ",255)";
		} 
	  return "rgba(" + r + "," + g + "," + b + "," + a + ")";
    },
		
	toRGBAString: function() {
	      return loon.Color.getColorAsRGBAString(this.r,this.g, this.b, this.a);
    }

});

loon.TEXT_ALIGN_LEFT = 0;
loon.TEXT_BASELINE_TOP = 1;
loon.TEXT_BASELINE_BOTTOM = 2;

/**
 * 构建Font用类
 */
loon.Font = loon.Class.extend({
		text:"",
		font:"Arial",
		size:20,
		alignment: loon.TEXT_ALIGN_LEFT,
		baseline: loon.TEXT_BASELINE_TOP,

        initialize:function(font, size, alignment, baseline) {
           if(font) this.font = font;
           if(size) this.size = size;
           if(alignment) this.alignment = alignment;
		   if(baseline) this.baseline = baseline;
        },

        setSize: function(size) {
           this.size = size;
        },

        setFont: function(font) {
           this.font = font;
        },

        setAlignment: function(alignment) {
            this.alignment = alignment;
        },

        setBaseline: function(baseline) {
           this.baseline = baseline;
        },

        setText: function(text) {
           this.text = text;
        },

        activate: function(ctx) {
            ctx.setFont(this.font, this.size);
            ctx.setTextAlignment(this.alignment);
            ctx.setTextBaseline(this.baseline);
        },

        draw: function(ctx, x, y, text) {
           if(text != undefined) {
			   this.text = text;
		   }
               this.activate(ctx);
           ctx.drawText(this.text, x, y);
        }
});

/**
 * 构建Renderer用类(取代标准版的GLEx，内部为Canvas封装，WebGL暂不考虑(因为GWT默认使用webgl，而标准版使用GWT实现HTML5，存在功能冲突))
 */
loon.Renderer = loon.Class.extend({

        initialize:function(canvas) {
	           this.ctx	= canvas;
	           this.bFill = true;
	           this.bStroke	= false;
	           this.bgColor	= new loon.Color();
	           this.shapePos = {x:0,y:0};
			   this.ctx.font = "14px serif";
        },
        
        _context: function(ctx){
		     this.ctx = ctx;
		},

		setBackgroundColor: function(r,g,b,a) {
	           this.bgColor.set(r,g,b,a);
        },

		clear: function(x,y,width,height) {
	        var curFill	= this.ctx.fillStyle;
	        this.ctx.fillStyle =  this.bgColor.toRGBAString();
	        this.ctx.fillRect(x,y,width,height);
	        this.ctx.fillStyle = curFill;
        },

        setColor: function(r,g,b,a) {
	        this.ctx.fillStyle = Pixel.getColorAsRGBAString(r,g,b,a);
	        this.bFill = true;
        },

        setColor: function(/*loon.Color*/ col) {
	        this.ctx.fillStyle = Pixel.getColorAsRGBAString(col.r,col.g,col.b,col.a);
	        this.bFill = true;
        },

        setStrokeSize: function(size) {
         	this.ctx.lineWidth = size;
        },

        pushMatrix: function() {
	        this.ctx.save();
        },

        popMatrix: function() {
	        this.ctx.restore();
        },

		translate: function(x,y) {
	        this.ctx.translate(x,y);
        },

        scale: function(x,y) {
	        this.ctx.scale(x,y);
        },

        rotate: function(angle) {
	        this.ctx.rotate(loon.dToR(angle));
        },

		setFont: function(font, size) {
	        if(size == undefined) {
		       this.setFont(font.fontFamily, font.size);
	        }else{
		       this.ctx.font = size + "pt " + font;
	        }
	        this._setTextAlignment(font.alignment);
	        this._setTextBaseline(font.baseline);
        },

        _setTextAlignment: function(alignment) {
	        this.ctx.textAlign = alignment;
        },


        _setTextBaseline: function(baseline) {
			switch(baseline) {
				case loon.TEXT_BASELINE_TOP:
					this.ctx.textBaseline = "top";
					break;
				case loon.TEXT_BASELINE_MIDDLE:
					this.ctx.textBaseline = "middle";
					break;
				case loon.TEXT_BASELINE_BOTTOM:
					this.ctx.textBaseline = "bottom";
					break;
			}
        },

        drawText: function(string, x, y) {
			if(x != undefined) {
				this.ctx.fillText(string, x, y);
			} else {
				this.ctx.fillText(string, this.cursorX, this.cursorY);
			}
        },

        getTextWidth: function(string) {
	         return this.ctx.measureText(string).width;
        },

	    drawImageRect: function(image, x, y, w, h) {
			 var len = arguments.length;
			 switch (len) {
				case 1:
					this.ctx.drawImage(image,0, 0);
					break;
				case 3:
					this.ctx.drawImage(image,x,y);
					break;
				case 5:
					this.ctx.drawImage(image,x,y,w,h);
					break;
				default:
					throw new Error("Error Arguments !");
					break;
			}
        },

		drawImage:function (image,/*point*/ sourcePoint,/*rectbox*/ sourceSize,/*point*/ destPoint,/*rectbox*/ destSize) {
			var len = arguments.length;
			switch (len) {
				case 2:
					this.ctx.drawImage(image, sourcePoint.x, sourcePoint.y);
					break;
				case 3:
					this.ctx.drawImage(image, sourcePoint.x, sourcePoint.y, sourceSize.width, sourceSize.height);
					break;
				case 5:
					this.ctx.drawImage(image, sourcePoint.x, sourcePoint.y, sourceSize.width, sourceSize.height, destPoint.x, destPoint.y,
						destSize.width, destSize.height);
					break;
				default:
					throw new Error("Error Arguments !");
					break;
			}
		},

        drawPoint: function (x,y, size) {
			if (!size) {
				size = 1;
			}
			this.ctx.beginPath();
			this.ctx.arc(x, y, size, 0, Math.PI * 2, false);
			this.ctx.closePath();
			this.ctx.fill();
        },

        drawPoints: function (xs, ys, size) {
			if (xs == null || ys == null) {
				return;
			}
			if (!size) {
				size = 1;
			}
			this.ctx.beginPath();
			for (var i = 0; i < xs.length; i++) {
				this.ctx.arc(xs[i].x, ys[i],size, 0, Math.PI * 2, false);
			}
			this.ctx.closePath();
			this.ctx.fill();
        }, 

		drawPoly: function (vertices, numOfVertices, closePolygon, fill) {
			if (fill == 'undefined') {
				fill = false;
			}
			if (vertices == null) {
				return;
			}
			if (vertices.length < 3) {
				throw new Error("points < 3 !");
			}
			var firstPoint = vertices[0];
			this._renderContext.beginPath();
			this._renderContext.moveTo(firstPoint.x, firstPoint.y);
			for (i = 1; i < vertices.length; i++) {
				this._renderContext.lineTo(vertices[i].x, vertices[i].y);
			}
			if (closePolygon) {
				this._renderContext.closePath();
			}
			if (fill) {
				this._renderContext.fill();
			} else {
				this._renderContext.stroke();
			}
        },

        beginShape: function(x,y) {
	         this.ctx.beginPath();
	         this.ctx.moveTo(x,y);
	         this.shapePos = {"x":x,"y":y};
        },

        addVertex: function(x,y, bEnd) {
	         this.ctx.lineTo(x,y);
	         if(bEnd != undefined) {
		        this.endShape();
	         }
	       this.shapePos = {"x":x,"y":y};
        },

        curveVertex: function(x, y) {
	        var x0,y0,x1,x2, y1, y2;
	            x0 = this.shapePos.x;
	            y0 = this.shapePos.y;

	            x2 = x;  
	            y2 = y;
	
				if((x0 > x && y0 > y) || (x0 < x && y0 < y)) {
					x1 = x2;
					y1 = y0;
				} else {
					x1 = x0;
					y1 = y2;
				}
	
				radius = (Math.abs(x0 - x2) + Math.abs(y0 - y2))/2;
				this.ctx.arcTo(x1, y1, x2, y2, radius);
				
				this.shapePos = {"x":x,"y":y};
         },

		 endShape: function(x,y) {
			this.ctx.closePath();
			this.shapePos = {"x":x,"y":y};
			this.ctx.fill();
		},

        drawLine: function(x1,y1,x2,y2) {
			this.ctx.beginPath();
			this.ctx.moveTo(x1,y1);
			this.ctx.lineTo(x2,y2);
			this.ctx.stroke();
        },

        drawRect: function(x,y,width,height) {
			if(y != undefined) {
				if(this.bFill) this.ctx.fillRect(x,y,width,height);
				if(this.bStroke) this.ctx.strokeRect(x,y,width,height);
			} else {
				var r = x;
				if(this.bFill) this.ctx.fillRect(r.x,r.y, r.width, r.height);
				if(this.bStroke) this.ctx.strokeRect(r.x, r.y,r.width, r.height);
			}
        },

		drawRoundedRect: function(x,y,width,height, radius) {
			if(typeof(radius) === 'number') {
				radius = {
					"tl":radius,
					"tr":radius,
					"br":radius,
					"bl":radius
				}
			}
	
			this.beginShape();
			this.addVertex(x + width-radius.tr, y);
			this.curveVertex(x + width, y + radius.tr);
			this.addVertex(x + width, y + height - radius.br);
			this.curveVertex(x + width - radius.br, y + height);
			this.addVertex(x + radius.bl, y + height);
			this.curveVertex(x, y + height - radius.bl);
			this.addVertex(x, y + radius.tl);
			this.curveVertex(x + radius.tl, y);
			this.endShape();
	
			if(this.bFill)	this.ctx.fill();
			if(this.bStroke) this.ctx.stroke();
        },

        drawEllipse: function(x,y,width,height) {
	          var kappa = 0.5522848;
			  ox = (width / 2) * kappa, 	
			  oy = (height / 2) * kappa, 	
			  xe = x + width,           	
			  ye = y + height,          	
			  xm = x + width / 2,       	
			  ym = y + height / 2;      	
			  this.ctx.beginPath();
			  this.ctx.moveTo(x, ym);
			  this.ctx.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
			  this.ctx.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
			  this.ctx.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
			  this.ctx.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
			  this.ctx.closePath();
			  if(this.bStroke)	this.ctx.stroke();
			  if(this.bFill)	this.ctx.fill();
        },

        drawCircle: function(x,y,size) {
			this.ctx.beginPath();
			this.ctx.arc(x + size/2, y + size/2, size/2, 0, Math.PI*2, false);
			if(this.bStroke) this.ctx.stroke();
			if(this.bFill) this.ctx.fill();
        }

});

/**
 * 构建Time用类
 */
loon.Time = loon.Class.extend({

	    active:true,
	    delay:0,
	    currentTick:0,

        initialize:function(delay) {
           if(delay){
		   this.delay = delay;
		   }
        },

		action:function(elapsedTime) {
			if (this.active) {
				this.currentTick += elapsedTime;
				if (this.currentTick >= this.delay) {
					this.currentTick -= this.delay;
					return true;
				}
			}
			return false;
	    },
			
	    refresh:function() {
		   this.currentTick = 0;
	    },

	    isActive:function() {
		    return this.active;
	    },
	
	    start:function() {
		   this.active = true;
	    },
	
	    stop:function() {
		   this.active = false;
	    },

	    getDelay:function() {
		   return this.delay;
	    },

	    setDelay:function(delay) {
		   this.delay = delay;
		   this.refresh();
	    },

	    getCurrentTick:function() {
		   return this.currentTick;
	    }

});

//loon事件状态对应字符
loon.Event.LOAD = 'load';
loon.Event.PROGRESS = 'progress';
loon.Event.ENTER_FRAME = 'enterframe';
loon.Event.EXIT_FRAME = 'exitframe';
loon.Event.ENTER = 'enter';
loon.Event.EXIT = 'exit';
loon.Event.CHILD_ADDED = 'childadded';
loon.Event.ADDED = 'added';
loon.Event.ADDED_TO_SCENE = 'addedtoScreen';
loon.Event.CHILD_REMOVED = 'childremoved';
loon.Event.REMOVED = 'removed';
loon.Event.REMOVED_FROM_SCENE = 'removedfromScreen';
loon.Event.TOUCH_START = 'touchstart';
loon.Event.TOUCH_MOVE = 'touchmove';
loon.Event.TOUCH_END = 'touchend';
loon.Event.RENDER = 'render';
loon.Event.INPUT_START = 'inputstart';
loon.Event.INPUT_CHANGE = 'inputchange';
loon.Event.INPUT_END = 'inputend';
loon.Event.LEFT_BUTTON_DOWN = 'leftbuttondown';
loon.Event.LEFT_BUTTON_UP = 'leftbuttonup';
loon.Event.RIGHT_BUTTON_DOWN = 'rightbuttondown';
loon.Event.RIGHT_fpsBUTTON_UP = 'rightbuttonup';
loon.Event.UP_BUTTON_DOWN = 'upbuttondown';
loon.Event.UP_BUTTON_UP = 'upbuttonup';
loon.Event.DOWN_BUTTON_DOWN = 'downbuttondown';
loon.Event.DOWN_BUTTON_UP = 'downbuttonup';
loon.Event.A_BUTTON_DOWN = 'abuttondown';
loon.Event.A_BUTTON_UP = 'abuttonup';
loon.Event.B_BUTTON_DOWN = 'bbuttondown';
loon.Event.B_BUTTON_UP = 'bbuttonup';
loon.Event.ADDED_TO_TIMELINE = "addedtotimeline";
loon.Event.REMOVED_FROM_TIMELINE = "removedfromtimeline";
loon.Event.ACTION_START = "actionstart";
loon.Event.ACTION_END = "actionend";
loon.Event.ACTION_TICK = "actiontick";
loon.Event.ACTION_ADDED = "actionadded";
loon.Event.ACTION_REMOVED = "actionremoved";
loon.EventTarget = loon.Class.extend({

    initialize: function() {
        this._listeners = {};
    },
 
    addEventListener: function(type, listener) {
        var listeners = this._listeners[type];
        if (listeners == null) {
            this._listeners[type] = [listener];
        } else if (listeners.indexOf(listener) === -1) {
            listeners.unshift(listener);

        }
    },

    on: function() {
        this.addEventListener.apply(this, arguments);
    },
 
    removeEventListener: function(type, listener) {
        var listeners = this._listeners[type];
        if (listeners != null) {
            var i = listeners.indexOf(listener);
            if (i !== -1) {
                listeners.splice(i, 1);
            }
        }
    },

    clearEventListener: function(type) {
        if (type != null) {
            delete this._listeners[type];
        } else {
            this._listeners = {};
        }
    },

    dispatchEvent: function(e) {
        e.target = this;
        e.localX = e.x - this._offsetX;
        e.localY = e.y - this._offsetY;
        if (this['on' + e.type] != null){
            this['on' + e.type](e);
        }
        var listeners = this._listeners[e.type];
        if (listeners != null) {
            listeners = listeners.slice();
            for (var i = 0, len = listeners.length; i < len; i++) {
                listeners[i].call(this, e);
            }
        }
    }
});

/**
 * loon核心处理
 */
(function() {

    var _core;
    var _fps;
	var _time;
	var _render;
	var/*Runnable*/ _runnables = new loon.List();
    var/*Runnable*/ _executedRunnables = new loon.List();

    loon.Core = loon.Class.extend(loon.EventTarget, {
     
	    _init_context: false,
        initialize: function(width, height) {
            if (window.document.body === null) {
                throw new Error("document.body is null !");
            }

            loon.EventTarget.call(this);
            var initial = true;
            if (_core) {
                initial = false;
                _core.exit();
            }
            _core = loon.Core.instance = this;

            this.width = width || 320;
            this.height = height || 320;
            this.scale = 1;

            var activity = document.getElementById('lactivity');
            if (!activity) {
                activity = document.createElement('div');
                activity.id = 'lactivity';
                activity.style.position = 'absolute';

                if (document.body.firstChild) {
                    document.body.insertBefore(activity, document.body.firstChild);
                } else {
                    document.body.appendChild(activity);
                }
                this.scale = Math.min(
                    window.innerWidth / this.width,
                    window.innerHeight / this.height
                );
                this._pageX = 0;
                this._pageY = 0;
            } else {
                var style = window.getComputedStyle(activity);
                width = parseInt(style.width, 10);
                height = parseInt(style.height, 10);
                if (width && height) {
                    this.scale = Math.min(
                        width / this.width,
                        height / this.height
                    );
                } else {
                    activity.style.width = this.width + 'px';
                    activity.style.height = this.height + 'px';
                }
                while (activity.firstChild) {
                    activity.removeChild(activity.firstChild);
                }
                activity.style.position = 'relative';

                var bounding = activity.getBoundingClientRect();
                this._pageX = Math.round(window.scrollX || window.pageXOffset + bounding.left);
                this._pageY = Math.round(window.scrollY || window.pageYOffset + bounding.top);
            }
            if (!this.scale) {
                this.scale = 1;
            }
            activity.style.fontSize = '12px';
            activity.style.webkitTextSizeAdjust = 'none';
            this._element = activity;

            this.fps = 30;
			this.show_fps = false;
            this.frame = 0;
            this.ready = false;
            this.running = false;
            this.file = {};
            var file = this._file = [];
            (function detectAssets(module) {
                if (module.file instanceof Array) {
                    [].push.apply(file, module.file);
                }
                for (var prop in module) {
                    if (module.hasOwnProperty(prop)) {
                        if (typeof module[prop] === 'object' && module[prop] !== null && Object.getPrototypeOf(module[prop]) === Object.prototype) {
                            detectAssets(module[prop]);
                        }
                    }
                }
            }(loon));

            this._screens = [];

            this.currentScreen = null;
            
			this.root = new loon.Screen();
            this.pushScreen(this.root);
           
			this.loadingScreen = new loon.Screen();
            this.loadingScreen.backgroundColor = '#000';
            
			var barWidth = this.width * 0.4 | 0;
            var barHeight = this.width * 0.05 | 0;
            var border = barWidth * 0.03 | 0;
            var bar = new loon.Sprite(barWidth, barHeight);

            bar.x = (this.width - barWidth) / 2;
            bar.y = (this.height - barHeight) / 2;
            
			var image = new loon.Bitmap(barWidth, barHeight);
            image.context.fillStyle = '#fff';
            image.context.fillRect(0, 0, barWidth, barHeight);
            image.context.fillStyle = '#000';
            image.context.fillRect(border, border, barWidth - border * 2, barHeight - border * 2);
            bar.image = image;
            
			var progress = 0, _progress = 0;
            this.addEventListener('progress', function(e) {
                progress = e.loaded / e.total;
            });
            bar.addEventListener('enterframe', function() {
                _progress *= 0.9;
                _progress += progress * 0.1;
                image.context.fillStyle = '#fff';
                image.context.fillRect(border, 0, (barWidth - border * 2) * _progress, barHeight);
            });
            this.loadingScreen.addChild(bar);

            this._mousedownID = 0;
            this._surfaceID = 0;
            this._soundID = 0;
            this._activated = false;

            this._offsetX = 0;
            this._offsetY = 0;

   
            this.input = {};
            if (!loon.System.KEY_BIND_TABLE) {
                loon.System.KEY_BIND_TABLE = {};
            }
            this._keybind = loon.System.KEY_BIND_TABLE;
            this.pressedKeysNum = 0;
            this._internalButtondownListeners = {};
            this._internalButtonupListeners = {};

            for (var prop in this._keybind) {
                this.keybind(prop, this._keybind[prop]);
            }

            if (initial) {
                activity = loon.Core.instance._element;
                var evt;
				//键盘按下
                document.addEventListener('keydown', function(e) {
                    _core.dispatchEvent(new loon.Event('keydown'));
                    if (loon.System.PREVENT_DEFAULT_KEY_CODES.indexOf(e.keyCode) !== -1) {
                        e.preventDefault();
                        e.stopPropagation();
                    }

                    if (!_core.running) {
                        return;
                    }
                    var button = _core._keybind[e.keyCode];
                    if (button) {
                        evt = new loon.Event(button + 'buttondown');
                        _core.dispatchEvent(evt);
                    }
                }, true);
				//键盘放开事件监听
                document.addEventListener('keyup', function(e) {
                    if (!_core.running) {
                        return;
                    }
                    var button = _core._keybind[e.keyCode];
                    if (button) {
                        evt = new loon.Event(button + 'buttonup');
                        _core.dispatchEvent(evt);
                    }
                }, true);
                //触屏按下事件监听
                if (loon.System.SUPPORT_TOUCH) {
                    activity.addEventListener('touchstart', function(e) {
                        var tagName = (e.target.tagName).toLowerCase();
                        if (loon.System.USE_DEFAULT_EVENT_TAGS.indexOf(tagName) === -1) {
                            e.preventDefault();
                            if (!_core.running) {
                                e.stopPropagation();
                            }
                        }
                    }, true);
					//触屏移动事件监听
                    activity.addEventListener('touchmove', function(e) {
                        var tagName = (e.target.tagName).toLowerCase();
                        if (loon.System.USE_DEFAULT_EVENT_TAGS.indexOf(tagName) === -1) {
                            e.preventDefault();
                            if (!_core.running) {
                                e.stopPropagation();
                            }
                        }
                    }, true);
					//触屏放开事件监听
                    activity.addEventListener('touchend', function(e) {
                        var tagName = (e.target.tagName).toLowerCase();
                        if (loon.System.USE_DEFAULT_EVENT_TAGS.indexOf(tagName) === -1) {
                            e.preventDefault();
                            if (!_core.running) {
                                e.stopPropagation();
                            }
                        }
                    }, true);
                }
				//鼠标按下事件监听
                activity.addEventListener('mousedown', function(e) {
                    var tagName = (e.target.tagName).toLowerCase();
                    if (loon.System.USE_DEFAULT_EVENT_TAGS.indexOf(tagName) === -1) {
                        e.preventDefault();
                        _core._mousedownID++;
                        if (!_core.running) {
                            e.stopPropagation();
                        }
                    }
                }, true);
				//鼠标移动事件监听
                activity.addEventListener('mousemove', function(e) {
                    var tagName = (e.target.tagName).toLowerCase();
                    if (loon.System.USE_DEFAULT_EVENT_TAGS.indexOf(tagName) === -1) {
                        e.preventDefault();
                        if (!_core.running) {
                            e.stopPropagation();
                        }
                    }
                }, true);
				//鼠标放开事件监听
                activity.addEventListener('mouseup', function(e) {
                    var tagName = (e.target.tagName).toLowerCase();
                    if (loon.System.USE_DEFAULT_EVENT_TAGS.indexOf(tagName) === -1) {
                        e.preventDefault();
                        if (!_core.running) {
                            e.stopPropagation();
                        }
                    }
                }, true);
                _core._touchEventTarget = {};
				//如果系统支持web触屏
                if (loon.System.SUPPORT_TOUCH) {
					//触屏按下
                    activity.addEventListener('touchstart', function(e) {
                        var _core = loon.Core.instance;
                        var evt = new loon.Event(loon.Event.TOUCH_START);
                        var touches = e.changedTouches;
                        var touch, target;
                        for (var i = 0, l = touches.length; i < l; i++) {
                            touch = touches[i];
                            evt._initPosition(touch.pageX, touch.pageY);
                            target = _core.currentScreen._determineEventTarget(evt);
                            _core._touchEventTarget[touch.identifier] = target;
                            target.dispatchEvent(evt);
                        }
                    }, false);
					//触屏移动
                    activity.addEventListener('touchmove', function(e) {
                        var _core = loon.Core.instance;
                        var evt = new loon.Event(loon.Event.TOUCH_MOVE);
                        var touches = e.changedTouches;
                        var touch, target;
                        for (var i = 0, l = touches.length; i < l; i++) {
                            touch = touches[i];
                            target = _core._touchEventTarget[touch.identifier];
                            if (target) {
                                evt._initPosition(touch.pageX, touch.pageY);
                                target.dispatchEvent(evt);
                            }
                        }
                    }, false);
					//触屏放开
                    activity.addEventListener('touchend', function(e) {
                        var _core = loon.Core.instance;
                        var evt = new loon.Event(loon.Event.TOUCH_END);
                        var touches = e.changedTouches;
                        var touch, target;
                        for (var i = 0, l = touches.length; i < l; i++) {
                            touch = touches[i];
                            target = _core._touchEventTarget[touch.identifier];
                            if (target) {
                                evt._initPosition(touch.pageX, touch.pageY);
                                target.dispatchEvent(evt);
                                delete _core._touchEventTarget[touch.identifier];
                            }
                        }
                    }, false);
                }
                activity.addEventListener('mousedown', function(e) {
                    var _core = loon.Core.instance;
                    var evt = new loon.Event(loon.Event.TOUCH_START);
                    evt._initPosition(e.pageX, e.pageY);
                    var target = _core.currentScreen._determineEventTarget(evt);
                    _core._touchEventTarget[_core._mousedownID] = target;
                    target.dispatchEvent(evt);
                }, false);
                activity.addEventListener('mousemove', function(e) {
                    var _core = loon.Core.instance;
                    var evt = new loon.Event(loon.Event.TOUCH_MOVE);
                    evt._initPosition(e.pageX, e.pageY);
                    var target = _core._touchEventTarget[_core._mousedownID];
                    if (target) {
                        target.dispatchEvent(evt);
                    }
                }, false);
                activity.addEventListener('mouseup', function(e) {
                    var _core = loon.Core.instance;
                    var evt = new loon.Event(loon.Event.TOUCH_END);
                    evt._initPosition(e.pageX, e.pageY);
                    _core._touchEventTarget[_core._mousedownID].dispatchEvent(evt);
                    delete _core._touchEventTarget[_core._mousedownID];
                }, false);
            }
			
        },
  
        preload: function(file) {
            if (!(file instanceof Array)) {
                file = Array.prototype.slice.call(arguments);
            }
            [].push.apply(this._file, file);
        },

        load: function(src, callback) {
            if (callback == null) {
                callback = function() {
                };
            }

            var ext = loon.Core.findExt(src);

            if (loon.Core._loadFuncs[ext]) {
                loon.Core._loadFuncs[ext].call(this, src, callback, ext);
            }
            else {
                var req = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject('Msxml2.XMLHTTP');
                req.open('GET', src, true);
                req.onreadystatechange = function(e) {
                    if (req.readyState === 4) {
                        if (req.status !== 200 && req.status !== 0) {
                            throw new Error(req.status + ': ' + 'Cannot load an asset: ' + src);
                        }
                        var type = req.getResponseHeader('Content-Type') || '';
                        if (type.match(/^image/)) {
                            _core.file[src] = loon.Bitmap.load(src);
                            _core.file[src].addEventListener('load', callback);
                        } else if (type.match(/^audio/)) {
                            _core.file[src] = loon.Sound.load(src, type);
                            _core.file[src].addEventListener('load', callback);
                        } else {
                            _core.file[src] = req.responseText;
                            callback();
                        }
                    }
                };
                req.send(null);
            }
        },
 
        show: function() {

            //初始化游戏刷新
            var onloadTimeSetter = function() {
                this.currentTime = 0;
                this._nextTime = 0;
                this.removeEventListener('load', onloadTimeSetter);
                this.running = true;
                this.ready = true;
                this._requestNextFrame();
            };
 
            //判定是否构建fps用div
            if(this.show_fps){
                _fps = document.getElementById('lfps');
	            if (!_fps) {
	                _fps = document.createElement('div');
	                _fps.id = 'lfps';
	                _fps.style.position = 'absolute';
					_fps.style.left = '5px';
					_fps.style.top = '5px';
                    _fps.style.fontSize = '20px';
					_fps.style.fontFamily = 'Arial';
					_fps.style.display = 'block';
	                document.body.appendChild(_fps);
	              }
			}

			if(!_time){
				_time = new loon.Time(1000);
		    }

            //加入初始化监听 
            this.addEventListener('load', onloadTimeSetter);

            //判定是否应引入外部文件
            if (!this._activated && this._file.length) {
                this._activated = true;
                if (loon.Sound.enabledInMobileSafari && !_core._touched &&
                    loon.System.BROWSER === 'webkit' && loon.System.SUPPORT_TOUCH) {
                    var screen = new loon.Screen();
                    screen.backgroundColor = '#000';
                    var size = Math.round(_core.width / 10);
                    var sprite = new loon.Sprite(_core.width, size);
                    sprite.y = (_core.height - size) / 2;
                    sprite.image = new loon.Bitmap(_core.width, size);
                    sprite.image.context.fillStyle = '#fff';
                    sprite.image.context.font = (size - 1) + 'px bold Helvetica,Arial,sans-serif';
                    var width = sprite.image.context.measureText('Touch to Start').width;
                    sprite.image.context.fillText('Touch to Start', (_core.width - width) / 2, size - 1);
                    screen.addChild(sprite);
                    document.addEventListener('touchstart', function() {
                        _core._touched = true;
                        _core.removeScreen(screen);
                        _core.show();
                    }, true);
                    _core.pushScreen(screen);
                    return;
                }

                var o = {};
                var file = this._file.filter(function(asset) {
                    return asset in o ? false : o[asset] = true;
                });
                var loaded = 0,
                    len = file.length,
                    loadFunc = function() {
                        var e = new loon.Event('progress');
                        e.loaded = ++loaded;
                        e.total = len;
                        _core.dispatchEvent(e);
                        if (loaded === len) {
                            _core.removeScreen(_core.loadingScreen);
                            _core.dispatchEvent(new loon.Event('load'));
                        }
                    };

                for (var i = 0; i < len; i++) {
                    this.load(file[i], loadFunc);
                }
                this.pushScreen(this.loadingScreen);
            } else {
                this.dispatchEvent(new loon.Event('load'));
            }

			
        },

  	    post : function(/*Runnable*/runnable) {
			  	$sync(function() {
                      _runnables.add(runnable);
             	});  
	    },

        _requestNextFrame: function() {
            if (!this.ready) {
                return;
            }
            var _core = this;
            window.requestFrame(_core._checkTick);
        },
      
        _checkTick: function(now) {
            var core = loon.Core.instance;
            if (core._nextTime < now) {
                core._tick(now);
            } else {
                window.requestFrame(core._checkTick);
            }
        },

        _tick: function(now) {
            var e = new loon.Event('enterframe');
            if (this.currentTime === 0) {
                e.elapsed = 0;
            } else {
                e.elapsed = now - this.currentTime;
            }

            this._nextTime = now + 1000 / this.fps;
            this.currentTime = now;
            this._actualFps = e.elapsed > 0 ? (1000 / e.elapsed) : 0;

            /**
			 * Runnable提交处理
			 */
            if(_runnables.count > 0){ 
				$sync(function() {
					_executedRunnables.clear();
					_executedRunnables.addRange(_runnables);
					_runnables.clear();
					for (var i = 0; i < _executedRunnables.count; i++) {
							_executedRunnables.item(i).run();
							
					}
				});  
			}
			
            //获取节点集合
            var nodes = this.currentScreen.childNodes.slice();
            var push = Array.prototype.push;
            while (nodes.length) {
                var node = nodes.pop();
                node.age++;
                node.dispatchEvent(e);
                if (node.childNodes) {
                    push.apply(nodes, node.childNodes);
                }
            }

            //累加screen代
            this.currentScreen.age++;
            this.currentScreen.dispatchEvent(e);
            this.dispatchEvent(e);

            this.dispatchEvent(new loon.Event('exitframe'));
            this.frame++;
            if (config.useCanvasUpdate && this.ready && this.root._context)
            {
				 if(!this._init_context){
					 if(_render == null){
						 _render = new loon.Renderer(this.root._context);
					 }else{
						 _render._context(this.root._context);
					 }
					 _init_context = true;
				 }
				 OnUpdate(_render,e.elapsed);
            }
	        //显示fps
			if(this.show_fps && _time.action(e.elapsed)){
						if(_fps && this._actualFps != this.fps){
							  _fps.innerHTML = '<strong>FPS:' + Math.round(this._actualFps || this.fps) + '</strong>';
						}
			}
            this._requestNextFrame();

        },
		
        getTime: function() {
            return window.getTime();
        },
    
        exit: function() {
            this.ready = false;
            this.running = false;
			this._init_context = false;
        },
 
        pause: function() {
            this.ready = false;
        },
  
        resume: function() {
            if (this.ready) {
                return;
            }
            this.currentTime = 0;
            this.ready = true;
            this.running = true;
            this._requestNextFrame();
        },

		setFPS: function(fps) {
		    this.fps = fps;
		},

        getFPS: function() {
            return this.fps;
        },

        setShowFPS: function(sf) {
            this.show_fps = sf;
        },

		getShowFPS: function() {
            return this.show_fps;
        },

        pushScreen: function(screen) {
            this._element.appendChild(screen._element);
            if (this.currentScreen) {
                this.currentScreen.dispatchEvent(new loon.Event('exit'));
            }
            this.currentScreen = screen;
            this.currentScreen.dispatchEvent(new loon.Event('enter'));
            return this._screens.push(screen);
        },
 
        popScreen: function() {
            if (this.currentScreen === this.root) {
                return this.currentScreen;
            }
            this._element.removeChild(this.currentScreen._element);
            this.currentScreen.dispatchEvent(new loon.Event('exit'));
            this.currentScreen = this._screens[this._screens.length - 2];
            this.currentScreen.dispatchEvent(new loon.Event('enter'));
            return this._screens.pop();
        },

        replaceScreen: function(screen) {
            this.popScreen();
            return this.pushScreen(screen);
        },

        removeScreen: function(screen) {
            if (this.currentScreen === screen) {
                return this.popScreen();
            } else {
                var i = this._screens.indexOf(screen);
                if (i !== -1) {
                    this._screens.splice(i, 1);
                    this._element.removeChild(screen._element);
                    return screen;
                } else {
                    return null;
                }
            }
        },

        keybind: function(key, button) {
            this._keybind[key] = button;
            var onxbuttondown = function(e) {
                var inputEvent;
                if (!this.input[button]) {
                    this.input[button] = true;
                    inputEvent = new loon.Event((this.pressedKeysNum++) ? 'inputchange' : 'inputstart');
                    this.dispatchEvent(inputEvent);
                    this.currentScreen.dispatchEvent(inputEvent);
                }
                this.currentScreen.dispatchEvent(e);
            };
            var onxbuttonup = function(e) {
                var inputEvent;
                if (this.input[button]) {
                    this.input[button] = false;
                    inputEvent = new loon.Event((--this.pressedKeysNum) ? 'inputchange' : 'inputend');
                    this.dispatchEvent(inputEvent);
                    this.currentScreen.dispatchEvent(inputEvent);
                }
                this.currentScreen.dispatchEvent(e);
            };

            this.addEventListener(button + 'buttondown', onxbuttondown);
            this.addEventListener(button + 'buttonup', onxbuttonup);

            this._internalButtondownListeners[key] = onxbuttondown;
            this._internalButtonupListeners[key] = onxbuttonup;
        },
 
        keyunbind: function(key) {
            if (!this._keybind[key]) {
                return;
            }
            var buttondowns = this._internalButtondownListeners;
            var buttonups = this._internalButtonupListeners;

            this.removeEventListener(key + 'buttondown', buttondowns);
            this.removeEventListener(key + 'buttonup', buttonups);

            delete buttondowns[key];
            delete buttonups[key];

            delete this._keybind[key];
        },
    
        getElapsedTime: function() {
            return this.frame / this.fps;
        }
    });

    loon.Core._loadFuncs = {};
    loon.Core._loadFuncs['jpg'] =
        loon.Core._loadFuncs['jpeg'] =
            loon.Core._loadFuncs['gif'] =
                loon.Core._loadFuncs['png'] =
                    loon.Core._loadFuncs['bmp'] = function(src, callback) {
                        this.file[src] = loon.Bitmap.load(src);
                        this.file[src].addEventListener('load', callback);
                    };
    loon.Core._loadFuncs['mp3'] =
        loon.Core._loadFuncs['aac'] =
            loon.Core._loadFuncs['m4a'] =
                loon.Core._loadFuncs['wav'] =
                    loon.Core._loadFuncs['ogg'] = function(src, callback, ext) {
                        this.file[src] = loon.Sound.load(src, 'audio/' + ext, callback);
                    };


    loon.Core.findExt = function(path) {
        var matched = path.match(/\.\w+$/);
        if (matched && matched.length > 0) {
            return matched[0].slice(1).toLowerCase();
        }
        if (path.indexOf('data:') === 0) {
            return path.split(/[\/;]/)[1].toLowerCase();
        }
        return null;
    };

    loon.Core.instance = null;
}());


loon.Node = loon.Class.extend(loon.EventTarget, {

    initialize: function() {
        loon.EventTarget.call(this);

        this._dirty = false;

        this._matrix = [ 1, 0, 0, 1, 0, 0 ];

        this._x = 0;
        this._y = 0;
        this._offsetX = 0;
        this._offsetY = 0;
        this.age = 0;

        this.parentNode = null;

        this.screen = null;

        this.addEventListener('touchstart', function(e) {
            if (this.parentNode) {
                this.parentNode.dispatchEvent(e);
            }
        });
        this.addEventListener('touchmove', function(e) {
            if (this.parentNode) {
                this.parentNode.dispatchEvent(e);
            }
        });
        this.addEventListener('touchend', function(e) {
            if (this.parentNode) {
                this.parentNode.dispatchEvent(e);
            }
        });

        if(loon.System.USE_ANIMATION){
            var tl = this.tl = new loon.Timeline(this);
        }
    },
  
    moveTo: function(x, y) {
        this._x = x;
        this._y = y;
        this._dirty = true;
    },
   
    moveBy: function(x, y) {
        this._x += x;
        this._y += y;
        this._dirty = true;
    },

    x: {
        get: function() {
            return this._x;
        },
        set: function(x) {
            this._x = x;
            this._dirty = true;
        }
    },

    y: {
        get: function() {
            return this._y;
        },
        set: function(y) {
            this._y = y;
            this._dirty = true;
        }
    },
    _updateCoordinate: function() {
        var node = this;
        var tree = [ node ];
        var parent = node.parentNode;
        var screen = this.screen;
        while (parent && node._dirty) {
            tree.unshift(parent);
            node = node.parentNode;
            parent = node.parentNode;
        }
        var matrix = loon.Matrix.instance;
        var stack = matrix.stack;
        var mat = [];
        var newmat, ox, oy;
        stack.push(tree[0]._matrix);
        for (var i = 1, l = tree.length; i < l; i++) {
            node = tree[i];
            newmat = [];
            matrix.makeTransformMatrix(node, mat);
            matrix.multiply(stack[stack.length - 1], mat, newmat);
            node._matrix = newmat;
            stack.push(newmat);
            ox = (typeof node._originX === 'number') ? node._originX : node._width / 2 || 0;
            oy = (typeof node._originY === 'number') ? node._originY : node._height / 2 || 0;
            var vec = [ ox, oy ];
            matrix.multiplyVec(newmat, vec, vec);
            node._offsetX = vec[0] - ox;
            node._offsetY = vec[1] - oy;
            node._dirty = false;
        }
        matrix.reset();
    },
    remove: function() {
        if (this._listener) {
            this.clearEventListener();
        }
        if (this.parentNode) {
            this.parentNode.removeChild(this);
        }
    }
});

var _intersectBetweenClassAndInstance = function(Class, instance) {
    var ret = [];
    var c;
    for (var i = 0, l = Class.collection.length; i < l; i++) {
        c = Class.collection[i];
        if (instance._intersectone(c)) {
            ret.push(c);
        }
    }
    return ret;
};

var _intersectBetweenClassAndClass = function(Class1, Class2) {
    var ret = [];
    var c1, c2;
    for (var i = 0, l = Class1.collection.length; i < l; i++) {
        c1 = Class1.collection[i];
        for (var j = 0, ll = Class2.collection.length; j < ll; j++) {
            c2 = Class2.collection[j];
            if (c1._intersectone(c2)) {
                ret.push([ c1, c2 ]);
            }
        }
    }
    return ret;
};

var _staticintersect = function(other) {
    if (other instanceof loon.Entity) {
        return _intersectBetweenClassAndInstance(this, other);
    } else if (typeof other === 'function' && other.collection) {
        return _intersectBetweenClassAndClass(this, other);
    }
    return false;
};

loon.Entity = loon.Class.extend(loon.Node, {

    initialize: function() {
        var _core = loon.Core.instance;
        loon.Node.call(this);

        this._rotation = 0;
        this._scaleX = 1;
        this._scaleY = 1;

        this._touchEnabled = true;
        this._clipping = false;

        this._originX = null;
        this._originY = null;

        this._width = 0;
        this._height = 0;
        this._backgroundColor = null;
        this._opacity = 1;
        this._visible = true;
        this._buttonMode = null;

        this._style = {};
        this.__styleStatus = {};

        this.compositeOperation = null;

        this.buttonMode = null;

        this.buttonPressed = false;
        this.addEventListener('touchstart', function() {
            if (!this.buttonMode) {
                return;
            }
            this.buttonPressed = true;
            var e = new loon.Event(this.buttonMode + 'buttondown');
            this.dispatchEvent(e);
            _core.dispatchEvent(e);
        });
        this.addEventListener('touchend', function() {
            if (!this.buttonMode) {
                return;
            }
            this.buttonPressed = false;
            var e = new loon.Event(this.buttonMode + 'buttonup');
            this.dispatchEvent(e);
            _core.dispatchEvent(e);
        });

        this.enableCollection();
    },

    width: {
        get: function() {
            return this._width;
        },
        set: function(width) {
            this._width = width;
            this._dirty = true;
        }
    },

    height: {
        get: function() {
            return this._height;
        },
        set: function(height) {
            this._height = height;
            this._dirty = true;
        }
    },

    backgroundColor: {
        get: function() {
            return this._backgroundColor;
        },
        set: function(color) {
            this._backgroundColor = color;
        }
    },
   
    opacity: {
        get: function() {
            return this._opacity;
        },
        set: function(opacity) {
            this._opacity = parseFloat(opacity);
        }
    },

    visible: {
        get: function() {
            return this._visible;
        },
        set: function(visible) {
            this._visible = visible;
        }
    },

    touchEnabled: {
        get: function() {
            return this._touchEnabled;
        },
        set: function(enabled) {
            this._touchEnabled = enabled;
            if (enabled) {
                this._style.pointerEvents = 'all';
            } else {
                this._style.pointerEvents = 'none';
            }
        }
    },

    intersect: function(other) {
        if (other instanceof loon.Entity) {
            return this._intersectone(other);
        } else if (typeof other === 'function' && other.collection) {
            return _intersectBetweenClassAndInstance(other, this);
        }
        return false;
    },
    _intersectone: function(other) {
        if (this._dirty) {
            this._updateCoordinate();
        } if (other._dirty) {
            other._updateCoordinate();
        }
        return this._offsetX < other._offsetX + other.width && other._offsetX < this._offsetX + this.width &&
            this._offsetY < other._offsetY + other.height && other._offsetY < this._offsetY + this.height;
    },

    within: function(other, distance) {
        if (this._dirty) {
            this._updateCoordinate();
        } if (other._dirty) {
            other._updateCoordinate();
        }
        if (distance == null) {
            distance = (this.width + this.height + other.width + other.height) / 4;
        }
        var _;
        return (_ = this._offsetX - other._offsetX + (this.width - other.width) / 2) * _ +
            (_ = this._offsetY - other._offsetY + (this.height - other.height) / 2) * _ < distance * distance;
    }, 

    scale: function(x, y) {
        this._scaleX *= x;
        this._scaleY *= (y != null) ? y : x;
        this._dirty = true;
    },

    rotate: function(deg) {
        this._rotation += deg;
        this._dirty = true;
    },

    scaleX: {
        get: function() {
            return this._scaleX;
        },
        set: function(scaleX) {
            this._scaleX = scaleX;
            this._dirty = true;
        }
    },

    scaleY: {
        get: function() {
            return this._scaleY;
        },
        set: function(scaleY) {
            this._scaleY = scaleY;
            this._dirty = true;
        }
    },

    rotation: {
        get: function() {
            return this._rotation;
        },
        set: function(rotation) {
            this._rotation = rotation;
            this._dirty = true;
        }
    },
    
    originX: {
        get: function() {
            return this._originX;
        },
        set: function(originX) {
            this._originX = originX;
            this._dirty = true;
        }
    },
   
    originY: {
        get: function() {
            return this._originY;
        },
        set: function(originY) {
            this._originY = originY;
            this._dirty = true;
        }
    },

    enableCollection: function() {
        this.addEventListener('addedtoscreen', this._addSelfToCollection);
        this.addEventListener('removedfromscreen', this._removeSelfFromCollection);
        if (this.screen) {
            this._addSelfToCollection();
        }
    },

    disableCollection: function() {
        this.removeEventListener('addedtoscreen', this._addSelfToCollection);
        this.removeEventListener('removedfromscreen', this._removeSelfFromCollection);
        if (this.screen) {
            this._removeSelfFromCollection();
        }
    },

    _addSelfToCollection: function() {
        var Constructor = this.getConstructor();
        Constructor._collectionTarget.forEach(function(C) {
            C.collection.push(this);
        }, this);
    },

    _removeSelfFromCollection: function() {
        var Constructor = this.getConstructor();
        Constructor._collectionTarget.forEach(function(C) {
            var i = C.collection.indexOf(this);
            if (i !== -1) {
                C.collection.splice(i, 1);
            }
        }, this);
    },

    getConstructor: function() {
        return Object.getPrototypeOf(this).constructor;
    }
});

var _collectizeConstructor = function(Constructor) {
    if (Constructor._collective) {
        return;
    }
    var rel = loon.Class.getSupers(Constructor);
    var i = rel.indexOf(loon.Entity);
    if (i !== -1) {
        Constructor._collectionTarget = rel.splice(0, i + 1);
    } else {
        Constructor._collectionTarget = [];
    }
    Constructor.intersect = _staticintersect;
    Constructor.collection = [];
    Constructor._collective = true;
};

_collectizeConstructor(loon.Entity);

loon.Entity._inherited = function(subclass) {
    _collectizeConstructor(subclass);
};

loon.Sprite = loon.Class.extend(loon.Entity, {

    initialize: function(width, height) {
        loon.Entity.call(this);

        this.width = width;
        this.height = height;
        this._image = null;
        this._frameLeft = 0;
        this._frameTop = 0;
        this._frame = 0;
        this._frameSequence = [];
     
        this.addEventListener('enterframe', function() {
            if (this._frameSequence.length !== 0) {
                var nextFrame = this._frameSequence.shift();
                if (nextFrame === null) {
                    this._frameSequence = [];
                } else {
                    this._setFrame(nextFrame);
                    this._frameSequence.push(nextFrame);
                }
            }
        });
    },

    image: {
        get: function() {
            return this._image;
        },
        set: function(image) {
            if (image === this._image) {
                return;
            }
            this._image = image;
            this._setFrame(this._frame);
        }
    },

    frame: {
        get: function() {
            return this._frame;
        },
        set: function(frame) {
            if(this._frame === frame) {
                return;
            }
            if (frame instanceof Array) {
                var frameSequence = frame;
                var nextFrame = frameSequence.shift();
                this._setFrame(nextFrame);
                frameSequence.push(nextFrame);
                this._frameSequence = frameSequence;
            } else {
                this._setFrame(frame);
                this._frameSequence = [];
                this._frame = frame;
            }
        }
    },
  
    _setFrame: function(frame) {
        var image = this._image;
        var row, col;
        if (image != null) {
            this._frame = frame;
            row = image.width / this._width | 0;
            this._frameLeft = (frame % row | 0) * this._width;
            this._frameTop = (frame / row | 0) * this._height % image.height;
        }
    },
  
    width: {
        get: function() {
            return this._width;
        },
        set: function(width) {
            this._width = width;
            this._setFrame();
            this._dirty = true;
        }
    },

    height: {
        get: function() {
            return this._height;
        },
        set: function(height) {
            this._height = height;
            this._setFrame();
            this._dirty = true;
        }
    },
    cvsRender: function(ctx) {
        if (this._image == null || this._width === 0 || this._height === 0) {
            return;
        }
        var image = this._image;
        var element = image._element;
        var sx = this._frameLeft;
        var sy = this._frameTop;
        var sw = Math.min(this.width, image.width - sx);
        var sh = Math.min(this.height, image.height - sy);
        var dw = Math.min(image.width, this.width);
        var dh = Math.min(image.height, this.height);
        var x, y, w, h;
        for (y = 0; y < this.height; y += dh) {
            h = (this.height < y + dh) ? this.height - y : dh;
            for (x = 0; x < this.width; x += dw) {
                w = (this.width < x + dw) ? this.width - x : dw;
                ctx.drawImage(element, sx, sy,
                    sw * w / dw, sh * h / dh, x, y, w, h);
            }
        }
    },
    domRender: function(element) {
        if (this._image) {
            if (this._image._css) {
                this._style['background-image'] = this._image._css;
                this._style['background-position'] =
                    -this._frameLeft + 'px ' +
                    -this._frameTop + 'px';
            }
        }
    }
});

/**
 * 文字标签
 */
loon.Label = loon.Class.extend(loon.Entity, {
  
    initialize: function(text) {
        loon.Entity.call(this);

        this.text = text || '';
        this.width = 300;
        this.font = '14px serif';
        this.textAlign = 'left';
    },

    text: {
        get: function() {
            return this._text;
        },
        set: function(text) {
            if(this._text === text) {
                return;
            }
            this._text = text;
            text = text.replace(/<(br|BR) ?\/?>/g, '<br/>');
            this._splitText = text.split('<br/>');
            this.updateBoundArea();
            for (var i = 0, l = this._splitText.length; i < l; i++) {
                text = this._splitText[i];
                var metrics = this.getMetrics(text);
                this._splitText[i] = {};
                this._splitText[i].text = text;
                this._splitText[i].height = metrics.height;
            }
        }
    },

    textAlign: {
        get: function() {
            return this._style['text-align'];
        },
        set: function(textAlign) {
            this._style['text-align'] = textAlign;
            this.updateBoundArea();
        }
    },
 
    font: {
        get: function() {
            return this._style.font;
        },
        set: function(font) {
            this._style.font = font;
            this.updateBoundArea();
        }
    },
   
    color: {
        get: function() {
            return this._style.color;
        },
        set: function(color) {
            this._style.color = color;
        }
    },
    cvsRender: function(ctx) {
        var x, y = 0;
        var labelWidth = this.width;
        var charWidth, amount, line, text, c, buf, increase, length;
        var bufWidth;
        if (this._splitText) {
            ctx.textBaseline = 'top';
            ctx.font = this.font;
            ctx.fillStyle = this.color || '#000000';
            charWidth = ctx.measureText(' ').width;
            amount = labelWidth / charWidth;
            for (var i = 0, l = this._splitText.length; i < l; i++) {
                line = this._splitText[i];
                text = line.text;
                c = 0;
                while (text.length > c + amount || ctx.measureText(text.slice(c, c + amount)).width > labelWidth) {
                    buf = '';
                    increase = amount;
                    length = 0;
                    while (increase > 0) {
                        if (ctx.measureText(buf).width < labelWidth) {
                            length += increase;
                            buf = text.slice(c, c + length);
                        } else {
                            length -= increase;
                            buf = text.slice(c, c + length);
                        }
                        increase = increase / 2 | 0;
                    }
                    ctx.fillText(buf, 0, y);
                    y += line.height - 1;
                    c += length;
                }
                buf = text.slice(c, c + text.length);
                if (this.textAlign === 'right') {
                    x = labelWidth - ctx.measureText(buf).width;
                } else if (this.textAlign === 'center') {
                    x = (labelWidth - ctx.measureText(buf).width) / 2;
                } else {
                    x = 0;
                }
                ctx.fillText(buf, x, y);
                y += line.height - 1;
            }
        }
    },
    domRender: function(element) {
        if (element.innerHTML !== this._text) {
            element.innerHTML = this._text;
        }
    },
    detectRender: function(ctx) {
        ctx.fillRect(this._boundOffset, 0, this._boundWidth, this._boundHeight);
    },
    updateBoundArea: function() {
        var metrics = this.getMetrics();
        this._boundWidth = metrics.width;
        this._boundHeight = metrics.height;
        if (this.textAlign === 'right') {
            this._boundOffset = this.width - this._boundWidth;
        } else if (this.textAlign === 'center') {
            this._boundOffset = (this.width - this._boundWidth) / 2;
        } else {
            this._boundOffset = 0;
        }
    }
});

loon.Label.prototype.getMetrics = function(text) {
    var ret = {};
    var div, width, height;
    if (document.body) {
        div = document.createElement('div');
        for (var prop in this._style) {
            if(prop !== 'width' && prop !== 'height') {
                div.style[prop] = this._style[prop];
            }
        }
        text = text || this._text;
        div.innerHTML = text.replace(/ /g, '&nbsp;');
        div.style.whiteSpace = 'noWrap';
        document.body.appendChild(div);
        ret.height = parseInt(getComputedStyle(div).height, 10) + 1;
        div.style.position = 'absolute';
        ret.width = parseInt(getComputedStyle(div).width, 10) + 1;
        document.body.removeChild(div);
    } else {
        ret.width = this.width;
        ret.height = this.height;
    }
    return ret;
};

loon.Map = loon.Class.extend(loon.Entity, {

    initialize: function(tileWidth, tileHeight) {
        var _core = loon.Core.instance;

        loon.Entity.call(this);

        var surface = new loon.Bitmap(_core.width, _core.height);
        this._surface = surface;
        var canvas = surface._element;
        canvas.style.position = 'absolute';
        if (loon.System.RETINA_IOS_DISPLAY && _core.scale === 2) {
            canvas.width = _core.width * 2;
            canvas.height = _core.height * 2;
            this._style.webkitTransformOrigin = '0 0';
            this._style.webkitTransform = 'scale(0.5)';
        } else {
            canvas.width = _core.width;
            canvas.height = _core.height;
        }
        this._context = canvas.getContext('2d');

        this._tileWidth = tileWidth || 0;
        this._tileHeight = tileHeight || 0;
        this._image = null;
        this._data = [
            [
                []
            ]
        ];
        this._dirty = false;
        this._tight = false;

        this.touchEnabled = false;
        this.collisionData = null;

        this._listeners['render'] = null;
        this.addEventListener('render', function() {
            if (this._dirty || this._previousOffsetX == null) {
                this.redraw(0, 0, _core.width, _core.height);
            } else if (this._offsetX !== this._previousOffsetX ||
                this._offsetY !== this._previousOffsetY) {
                if (this._tight) {
                    var x = -this._offsetX;
                    var y = -this._offsetY;
                    var px = -this._previousOffsetX;
                    var py = -this._previousOffsetY;
                    var w1 = x - px + _core.width;
                    var w2 = px - x + _core.width;
                    var h1 = y - py + _core.height;
                    var h2 = py - y + _core.height;
                    if (w1 > this._tileWidth && w2 > this._tileWidth &&
                        h1 > this._tileHeight && h2 > this._tileHeight) {
                        var sx, sy, dx, dy, sw, sh;
                        if (w1 < w2) {
                            sx = 0;
                            dx = px - x;
                            sw = w1;
                        } else {
                            sx = x - px;
                            dx = 0;
                            sw = w2;
                        }
                        if (h1 < h2) {
                            sy = 0;
                            dy = py - y;
                            sh = h1;
                        } else {
                            sy = y - py;
                            dy = 0;
                            sh = h2;
                        }

                        if (_core._buffer == null) {
                            _core._buffer = document.createElement('canvas');
                            _core._buffer.width = this._context.canvas.width;
                            _core._buffer.height = this._context.canvas.height;
                        }
                        var context = _core._buffer.getContext('2d');
                        if (this._doubledImage) {
                            context.clearRect(0, 0, sw * 2, sh * 2);
                            context.drawImage(this._context.canvas,
                                sx * 2, sy * 2, sw * 2, sh * 2, 0, 0, sw * 2, sh * 2);
                            context = this._context;
                            context.clearRect(dx * 2, dy * 2, sw * 2, sh * 2);
                            context.drawImage(_core._buffer,
                                0, 0, sw * 2, sh * 2, dx * 2, dy * 2, sw * 2, sh * 2);
                        } else {
                            context.clearRect(0, 0, sw, sh);
                            context.drawImage(this._context.canvas,
                                sx, sy, sw, sh, 0, 0, sw, sh);
                            context = this._context;
                            context.clearRect(dx, dy, sw, sh);
                            context.drawImage(_core._buffer,
                                0, 0, sw, sh, dx, dy, sw, sh);
                        }

                        if (dx === 0) {
                            this.redraw(sw, 0, _core.width - sw, _core.height);
                        } else {
                            this.redraw(0, 0, _core.width - sw, _core.height);
                        }
                        if (dy === 0) {
                            this.redraw(0, sh, _core.width, _core.height - sh);
                        } else {
                            this.redraw(0, 0, _core.width, _core.height - sh);
                        }
                    } else {
                        this.redraw(0, 0, _core.width, _core.height);
                    }
                } else {
                    this.redraw(0, 0, _core.width, _core.height);
                }
            }
            this._previousOffsetX = this._offsetX;
            this._previousOffsetY = this._offsetY;
        });
    },

    loadData: function(data) {
        this._data = Array.prototype.slice.apply(arguments);
        this._dirty = true;

        this._tight = false;
        for (var i = 0, len = this._data.length; i < len; i++) {
            var c = 0;
            data = this._data[i];
            for (var y = 0, l = data.length; y < l; y++) {
                for (var x = 0, ll = data[y].length; x < ll; x++) {
                    if (data[y][x] >= 0) {
                        c++;
                    }
                }
            }
            if (c / (data.length * data[0].length) > 0.2) {
                this._tight = true;
                break;
            }
        }
    },

    checkTile: function(x, y) {
        if (x < 0 || this.width <= x || y < 0 || this.height <= y) {
            return false;
        }
        var width = this._image.width;
        var height = this._image.height;
        var tileWidth = this._tileWidth || width;
        var tileHeight = this._tileHeight || height;
        x = x / tileWidth | 0;
        y = y / tileHeight | 0;
        var data = this._data[0];
        return data[y][x];
    },

    hit: function(x, y) {
        if (x < 0 || this.width <= x || y < 0 || this.height <= y) {
            return false;
        }
        var width = this._image.width;
        var height = this._image.height;
        var tileWidth = this._tileWidth || width;
        var tileHeight = this._tileHeight || height;
        x = x / tileWidth | 0;
        y = y / tileHeight | 0;
        if (this.collisionData != null) {
            return this.collisionData[y] && !!this.collisionData[y][x];
        } else {
            for (var i = 0, len = this._data.length; i < len; i++) {
                var data = this._data[i];
                var n;
                if (data[y] != null && (n = data[y][x]) != null &&
                    0 <= n && n < (width / tileWidth | 0) * (height / tileHeight | 0)) {
                    return true;
                }
            }
            return false;
        }
    },
  
    image: {
        get: function() {
            return this._image;
        },
        set: function(image) {
            var _core = loon.Core.instance;
            this._image = image;
            if (loon.System.RETINA_IOS_DISPLAY && _core.scale === 2) {
                var img = new loon.Bitmap(image.width * 2, image.height * 2);
                var tileWidth = this._tileWidth || image.width;
                var tileHeight = this._tileHeight || image.height;
                var row = image.width / tileWidth | 0;
                var col = image.height / tileHeight | 0;
                for (var y = 0; y < col; y++) {
                    for (var x = 0; x < row; x++) {
                        img.draw(image, x * tileWidth, y * tileHeight, tileWidth, tileHeight,
                            x * tileWidth * 2, y * tileHeight * 2, tileWidth * 2, tileHeight * 2);
                    }
                }
                this._doubledImage = img;
            }
            this._dirty = true;
        }
    },
 
    tileWidth: {
        get: function() {
            return this._tileWidth;
        },
        set: function(tileWidth) {
            this._tileWidth = tileWidth;
            this._dirty = true;
        }
    },

    tileHeight: {
        get: function() {
            return this._tileHeight;
        },
        set: function(tileHeight) {
            this._tileHeight = tileHeight;
            this._dirty = true;
        }
    },

    width: {
        get: function() {
            return this._tileWidth * this._data[0][0].length;
        }
    },
    
    height: {
        get: function() {
            return this._tileHeight * this._data[0].length;
        }
    },

    redraw: function(x, y, width, height) {
        if (this._image == null) {
            return;
        }

        var image, tileWidth, tileHeight, dx, dy;
        if (this._doubledImage) {
            image = this._doubledImage;
            tileWidth = this._tileWidth * 2;
            tileHeight = this._tileHeight * 2;
            dx = -this._offsetX * 2;
            dy = -this._offsetY * 2;
            x *= 2;
            y *= 2;
            width *= 2;
            height *= 2;
        } else {
            image = this._image;
            tileWidth = this._tileWidth;
            tileHeight = this._tileHeight;
            dx = -this._offsetX;
            dy = -this._offsetY;
        }
        var row = image.width / tileWidth | 0;
        var col = image.height / tileHeight | 0;
        var left = Math.max((x + dx) / tileWidth | 0, 0);
        var top = Math.max((y + dy) / tileHeight | 0, 0);
        var right = Math.ceil((x + dx + width) / tileWidth);
        var bottom = Math.ceil((y + dy + height) / tileHeight);

        var source = image._element;
        var context = this._context;
        var canvas = context.canvas;
        context.clearRect(x, y, width, height);
        for (var i = 0, len = this._data.length; i < len; i++) {
            var data = this._data[i];
            var r = Math.min(right, data[0].length);
            var b = Math.min(bottom, data.length);
            for (y = top; y < b; y++) {
                for (x = left; x < r; x++) {
                    var n = data[y][x];
                    if (0 <= n && n < row * col) {
                        var sx = (n % row) * tileWidth;
                        var sy = (n / row | 0) * tileHeight;
                        context.drawImage(source, sx, sy, tileWidth, tileHeight,
                            x * tileWidth - dx, y * tileHeight - dy, tileWidth, tileHeight);
                    }
                }
            }
        }
    },
    cvsRender: function(ctx) {
        var _core = loon.Core.instance;
        if (this.width !== 0 && this.height !== 0) {
            ctx.save();
            ctx.setTransform(1, 0, 0, 1, 0, 0);
            var cvs = this._context.canvas;
                ctx.drawImage(cvs, 0, 0, _core.width, _core.height);
            ctx.restore();
        }
    },
    domRender: function(element) {
        if (this._image) {
            this._style['background-image'] = this._surface._css;
            this._style[loon.System.BROWSER + 'Transform'] = 'matrix(1, 0, 0, 1, 0, 0)';
        }
    }
});

loon.Group = loon.Class.extend(loon.Node, {

    initialize: function() {
        this.childNodes = [];
        loon.Node.call(this);
        this._rotation = 0;
        this._scaleX = 1;
        this._scaleY = 1;
        this._originX = null;
        this._originY = null;
        this.__dirty = false;

        [loon.Event.ADDED_TO_SCENE, loon.Event.REMOVED_FROM_SCENE]
            .forEach(function(event) {
                this.addEventListener(event, function(e) {
                    this.childNodes.forEach(function(child) {
                        child.screen = this.screen;
                        child.dispatchEvent(e);
                    }, this);
                });
            }, this);
    },

    addChild: function(node) {
        this.childNodes.push(node);
        node.parentNode = this;
        var childAdded = new loon.Event('childadded');
        childAdded.node = node;
        childAdded.next = null;
        this.dispatchEvent(childAdded);
        node.dispatchEvent(new loon.Event('added'));
        if (this.screen) {
            node.screen = this.screen;
            var addedToScreen = new loon.Event('addedtoscreen');
            node.dispatchEvent(addedToScreen);
        }
    },

    insertBefore: function(node, reference) {
        var i = this.childNodes.indexOf(reference);
        if (i !== -1) {
            this.childNodes.splice(i, 0, node);
            node.parentNode = this;
            var childAdded = new loon.Event('childadded');
            childAdded.node = node;
            childAdded.next = reference;
            this.dispatchEvent(childAdded);
            node.dispatchEvent(new loon.Event('added'));
            if (this.screen) {
                node.screen = this.screen;
                var addedToScreen = new loon.Event('addedtoscreen');
                node.dispatchEvent(addedToScreen);
            }
        } else {
            this.addChild(node);
        }
    },

    removeChild: function(node) {
        var i;
        if ((i = this.childNodes.indexOf(node)) !== -1) {
            this.childNodes.splice(i, 1);
            node.parentNode = null;
            var childRemoved = new loon.Event('childremoved');
            childRemoved.node = node;
            this.dispatchEvent(childRemoved);
            node.dispatchEvent(new loon.Event('removed'));
            if (this.screen) {
                node.screen = null;
                var removedFromScreen = new loon.Event('removedfromscreen');
                node.dispatchEvent(removedFromScreen);
            }
        }
    },

    firstChild: {
        get: function() {
            return this.childNodes[0];
        }
    },
   
    lastChild: {
        get: function() {
            return this.childNodes[this.childNodes.length - 1];
        }
    },

    rotation: {
        get: function() {
            return this._rotation;
        },
        set: function(rotation) {
            this._rotation = rotation;
            this._dirty = true;
        }
    },
    scaleX: {
        get: function() {
            return this._scaleX;
        },
        set: function(scale) {
            this._scaleX = scale;
            this._dirty = true;
        }
    },
    scaleY: {
        get: function() {
            return this._scaleY;
        },
        set: function(scale) {
            this._scaleY = scale;
            this._dirty = true;
        }
    },
    originX: {
        get: function() {
            return this._originX;
        },
        set: function(originX) {
            this._originX = originX;
            this._dirty = true;
        }
    },
    originY: {
        get: function() {
            return this._originY;
        },
        set: function(originY) {
            this._originY = originY;
            this._dirty = true;
        }
    },
    _dirty: {
        get: function() {
            return this.__dirty;
        },
        set: function(dirty) {
            dirty = !!dirty;
            this.__dirty = dirty;
            if (dirty) {
                for (var i = 0, l = this.childNodes.length; i < l; i++) {
                    this.childNodes[i]._dirty = true;
                }
            }
        }
    }
});

loon.Matrix = loon.Class.extend({
    initialize: function() {
        if (loon.Matrix.instance) {
            return loon.Matrix.instance;
        }
        this.reset();
    },
    reset: function() {
        this.stack = [];
        this.stack.push([ 1, 0, 0, 1, 0, 0 ]);
    },
    makeTransformMatrix: function(node, dest) {
        var x = node._x;
        var y = node._y;
        var width = node.width || 0;
        var height = node.height || 0;
        var rotation = node._rotation || 0;
        var scaleX = (typeof node._scaleX === 'number') ? node._scaleX : 1;
        var scaleY = (typeof node._scaleY === 'number') ? node._scaleY : 1;
        var theta = rotation * Math.PI / 180;
        var tmpcos = Math.cos(theta);
        var tmpsin = Math.sin(theta);
        var w = (typeof node._originX === 'number') ? node._originX : width / 2;
        var h = (typeof node._originY === 'number') ? node._originY : height / 2;
        var a = scaleX * tmpcos;
        var b = scaleX * tmpsin;
        var c = scaleY * tmpsin;
        var d = scaleY * tmpcos;
        dest[0] = a;
        dest[1] = b;
        dest[2] = -c;
        dest[3] = d;
        dest[4] = (-a * w + c * h + x + w);
        dest[5] = (-b * w - d * h + y + h);
    },
    multiply: function(m1, m2, dest) {
        var a11 = m1[0], a21 = m1[2], adx = m1[4],
            a12 = m1[1], a22 = m1[3], ady = m1[5];
        var b11 = m2[0], b21 = m2[2], bdx = m2[4],
            b12 = m2[1], b22 = m2[3], bdy = m2[5];

        dest[0] = a11 * b11 + a21 * b12;
        dest[1] = a12 * b11 + a22 * b12;
        dest[2] = a11 * b21 + a21 * b22;
        dest[3] = a12 * b21 + a22 * b22;
        dest[4] = a11 * bdx + a21 * bdy + adx;
        dest[5] = a12 * bdx + a22 * bdy + ady;
    },
    multiplyVec: function(mat, vec, dest) {
        var x = vec[0], y = vec[1];
        var m11 = mat[0], m21 = mat[2], mdx = mat[4],
            m12 = mat[1], m22 = mat[3], mdy = mat[5];
        dest[0] = m11 * x + m21 * y + mdx;
        dest[1] = m12 * x + m22 * y + mdy;
    }
});
loon.Matrix.instance = new loon.Matrix();

loon.ColorManager = loon.Class.extend({
    initialize: function(reso, max) {
        this.reference = [];
        this.colorResolution = reso || 16;
        this.max = max || 1;
        this.capacity = Math.pow(this.colorResolution, 3);
        for (var i = 1, l = this.capacity; i < l; i++) {
            this.reference[i] = null;
        }
    },
    attachDetectColor: function(sprite) {
        var i = this.reference.indexOf(null);
        if (i === -1) {
            i = 1;
        }
        this.reference[i] = sprite;
        return this._getColor(i);
    },
    detachDetectColor: function(sprite) {
        var i = this.reference.indexOf(sprite);
        if (i !== -1) {
            this.reference[i] = null;
        }
    },
    _getColor: function(n) {
        var C = this.colorResolution;
        var d = C / this.max;
        return [
            parseInt((n / C / C) % C, 10) / d,
            parseInt((n / C) % C, 10) / d,
            parseInt(n % C, 10) / d,
            1.0
        ];
    },
    _decodeDetectColor: function(color) {
        var C = this.colorResolution;
        return ~~(color[0] * C * C * C / 256) +
            ~~(color[1] * C * C / 256) +
            ~~(color[2] * C / 256);
    },
    getSpriteByColor: function(color) {
        return this.reference[this._decodeDetectColor(color)];
    }
});

loon.CanvasLayer = loon.Class.extend(loon.Group, {

    initialize: function() {

        var _core = loon.Core.instance;

        loon.Group.call(this);

        this._cvsCache = {
            matrix: [1, 0, 0, 1, 0, 0],
            detectColor: '#000000'
        };
        this._cvsCache.layer = this;

        this.width = _core.width;
        this.height = _core.height;

        this._element = document.createElement('canvas');
        this._element.width = _core.width;
        this._element.height = _core.height;
        this._element.style.position = 'absolute';

        this._detect = document.createElement('canvas');
        this._detect.width = _core.width;
        this._detect.height = _core.height;
        this._detect.style.position = 'absolute';
        this._lastDetected = 0;

        this.context = this._element.getContext('2d');
        this._dctx = this._detect.getContext('2d');

        this._colorManager = new loon.ColorManager(16, 256);

        var touch = [
            loon.Event.TOUCH_START,
            loon.Event.TOUCH_MOVE,
            loon.Event.TOUCH_END
        ];

        touch.forEach(function(type) {
            this.addEventListener(type, function(e) {
                if (this._screen) {
                    this._screen.dispatchEvent(e);
                }
            });
        }, this);

        var __onchildadded = function(e) {
            var child = e.node;
            var self = e.target;
            var layer;
            if (self instanceof loon.CanvasLayer) {
                layer = self._screen._layers.Canvas;
            } else {
                layer = self.screen._layers.Canvas;
            }
            loon.CanvasLayer._attachCache(child, layer, __onchildadded, __onchildremoved);
            var render = new loon.Event(loon.Event.RENDER);
            if (self._dirty) {
                self._updateCoordinate();
            }
            child._dirty = true;
            loon.Matrix.instance.stack.push(self._matrix);
            layer._rendering(child, render);
            loon.Matrix.instance.stack.pop(self._matrix);
        };

        var __onchildremoved = function(e) {
            var child = e.node;
            var self = e.target;
            var layer;
            if (self instanceof loon.CanvasLayer) {
                layer = self._screen._layers.Canvas;
            } else {
                layer = self.screen._layers.Canvas;
            }
            loon.CanvasLayer._detachCache(child, layer, __onchildadded, __onchildremoved);
        };

        this.addEventListener('childremoved', __onchildremoved);
        this.addEventListener('childadded', __onchildadded);

    },

    getContext: function(){
	  return this.context;
	},

    _startRendering: function() {
        this.addEventListener('exitframe', this._onexitframe);
        this._onexitframe(new loon.Event(loon.Event.RENDER));
    },

    _stopRendering: function() {
        this.removeEventListener('render', this._onexitframe);
        this._onexitframe(new loon.Event(loon.Event.RENDER));
    },
    _onexitframe: function() {
        var _core = loon.Core.instance;
        var ctx = this.context;
        ctx.clearRect(0, 0, _core.width, _core.height);
        var render = new loon.Event(loon.Event.RENDER);
        this._rendering(this, render);
    },
    _rendering:  function(node, e) {
        var _core = loon.Core.instance;
        var matrix = loon.Matrix.instance;
        var stack = matrix.stack;
        var width = node.width;
        var height = node.height;
        var ctx = this.context;
        var child;
        ctx.save();
        node.dispatchEvent(e);
        if (node.compositeOperation) {
            ctx.globalCompositeOperation = node.compositeOperation;
        } else {
            ctx.globalCompositeOperation = 'source-over';
        }
        ctx.globalAlpha = (typeof node._opacity === 'number') ? node._opacity : 1.0;
        this._transform(node, ctx);
        if (typeof node._visible === 'undefined' || node._visible) {
            if (node._backgroundColor) {
                ctx.fillStyle = node._backgroundColor;
                ctx.fillRect(0, 0, width, height);
            }

            if (node.cvsRender) {
                node.cvsRender(ctx);
            }

            if (node._clipping) {
                ctx.rect(0, 0, width, height);
                ctx.clip();
            }
        }
        if (node.childNodes) {
            for (var i = 0, l = node.childNodes.length; i < l; i++) {
                child = node.childNodes[i];
                this._rendering(child, e);
            }
        }
        ctx.restore();
        loon.Matrix.instance.stack.pop();
    },
    _detectrendering: function(node) {
        var width = node.width;
        var height = node.height;
        var ctx = this._dctx;
        var child;
        ctx.save();
        this._transform(node, ctx);
        ctx.fillStyle = node._cvsCache.detectColor;
        if (node._touchEnabled) {
            if (node.detectRender) {
                node.detectRender(ctx);
            } else {
                ctx.fillRect(0, 0, width, height);
            }
        }
        if (node._clipping) {
            ctx.rect(0, 0, width, height);
            ctx.clip();
        }
        if (node.childNodes) {
            for (var i = 0, l = node.childNodes.length; i < l; i++) {
                child = node.childNodes[i];
                this._detectrendering(child);
            }
        }
        ctx.restore();
        loon.Matrix.instance.stack.pop();
    },
    _transform: function(node, ctx) {
        var matrix = loon.Matrix.instance;
        var stack = matrix.stack;
        var newmat;
        if (node._dirty) {
            matrix.makeTransformMatrix(node, node._cvsCache.matrix);
            newmat = [];
            matrix.multiply(stack[stack.length - 1], node._cvsCache.matrix, newmat);
            node._matrix = newmat;
        } else {
            newmat = node._matrix;
        }
        stack.push(newmat);
        ctx.setTransform.apply(ctx, newmat);
        var ox = (typeof node._originX === 'number') ? node._originX : node._width / 2 || 0;
        var oy = (typeof node._originY === 'number') ? node._originY : node._height / 2 || 0;
        var vec = [ ox, oy ];
        matrix.multiplyVec(newmat, vec, vec);
        node._offsetX = vec[0] - ox;
        node._offsetY = vec[1] - oy;
        node._dirty = false;

    },
    _determineEventTarget: function(e) {
        return this._getEntityByPosition(e.x, e.y);
    },
    _getEntityByPosition: function(x, y) {
        var _core = loon.Core.instance;
        var ctx = this._dctx;
        if (this._lastDetected < _core.frame) {
            ctx.clearRect(0, 0, this.width, this.height);
            this._detectrendering(this);
            this._lastDetected = _core.frame;
        }
        var color = ctx.getImageData(x, y, 1, 1).data;
        return this._colorManager.getSpriteByColor(color);
    }
});

loon.CanvasLayer._attachCache = function(node, layer, onchildadded, onchildremoved) {
    var child;
    if (!node._cvsCache) {
        node._cvsCache = {};
        node._cvsCache.matrix = [ 1, 0, 0, 1, 0, 0 ];
        node._cvsCache.detectColor = 'rgba(' + layer._colorManager.attachDetectColor(node) + ')';
        node.addEventListener('childadded', onchildadded);
        node.addEventListener('childremoved', onchildremoved);
    }
    if (node.childNodes) {
        for (var i = 0, l = node.childNodes.length; i < l; i++) {
            child = node.childNodes[i];
            loon.CanvasLayer._attachCache(child, layer, onchildadded, onchildremoved);
        }
    }
};

loon.CanvasLayer._detachCache = function(node, layer, onchildadded, onchildremoved) {
    var child;
    if (node._cvsCache) {
        layer._colorManager.detachDetectColor(node);
        node.removeEventListener('childadded', onchildadded);
        node.removeEventListener('childremoved', onchildremoved);
        delete node._cvsCache;
    }
    if (node.childNodes) {
        for (var i = 0, l = node.childNodes.length; i < l; i++) {
            child = node.childNodes[i];
            loon.CanvasLayer._detachCache(child, layer, onchildadded, onchildremoved);
        }
    }
};

loon.Screen = loon.Class.extend(loon.Group, {
 
    _context:null, 
    initialize: function() {
        var _core = loon.Core.instance;

        loon.Group.call(this);

        this.width = _core.width;
        this.height = _core.height;

        this.screen = this;

        this._backgroundColor = null;

        this._element = document.createElement('div');
        this._element.style.width = this.width + 'px';
        this._element.style.height = this.height + 'px';
        this._element.style.position = 'absolute';
        this._element.style.overflow = 'hidden';
        this._element.style[loon.System.BROWSER + 'TransformOrigin'] = '0 0';
        this._element.style[loon.System.BROWSER + 'Transform'] = 'scale(' + loon.Core.instance.scale + ')';

        this._layers = {};
        this._layerPriority = [];

        this.addEventListener(loon.Event.CHILD_ADDED, this._onchildadded);
        this.addEventListener(loon.Event.CHILD_REMOVED, this._onchildremoved);
        this.addEventListener(loon.Event.ENTER, this._onenter);
        this.addEventListener(loon.Event.EXIT, this._onexit);

        var that = this;
        this._dispatchExitframe = function() {
            var layer;
            for (var prop in that._layers) {
                layer = that._layers[prop];
                layer.dispatchEvent(new loon.Event(loon.Event.EXIT_FRAME));
            }
        };
    },

    x: {
        get: function() {
            return this._x;
        },
        set: function(x) {
            this._x = x;
            for (var type in this._layers) {
                this._layers[type].x = x;
            }
        }
    },
    y: {
        get: function() {
            return this._y;
        },
        set: function(y) {
            this._y = y;
            for (var type in this._layers) {
                this._layers[type].y = y;
            }
        }
    },
    rotation: {
        get: function() {
            return this._rotation;
        },
        set: function(rotation) {
            this._rotation = rotation;
            for (var type in this._layers) {
                this._layers[type].rotation = rotation;
            }
        }
    },
    scaleX: {
        get: function() {
            return this._scaleX;
        },
        set: function(scaleX) {
            this._scaleX = scaleX;
            for (var type in this._layers) {
                this._layers[type].scaleX = scaleX;
            }
        }
    },
    scaleY: {
        get: function() {
            return this._scaleY;
        },
        set: function(scaleY) {
            this._scaleY = scaleY;
            for (var type in this._layers) {
                this._layers[type].scaleY = scaleY;
            }
        }
    },
    backgroundColor: {
        get: function() {
            return this._backgroundColor;
        },
        set: function(color) {
            this._backgroundColor = this._element.style.backgroundColor = color;
        }
    },

    addLayer: function(type, i) {
        var _core = loon.Core.instance;
        if (this._layers[type]) {
            return;
        }
        var layer = new loon[type + 'Layer']();
        if (_core.currentScreen === this) {
            layer._startRendering();
        }
        this._layers[type] = layer;
        var element = layer._element;
        if (typeof i === 'number') {
            var nextSibling = this._element.childNodes[i];
            this._element.insertBefore(element, nextSibling);
            this._layerPriority.splice(i, 0, type);
        } else {
            this._element.appendChild(element);
            this._layerPriority.push(type);
        }
        layer._screen = this;
        this._context = layer.getContext();
    },
    _determineEventTarget: function(e) {
        var layer, target;
        for (var i = this._layerPriority.length - 1; i >= 0; i--) {
            layer = this._layers[this._layerPriority[i]];
            target = layer._determineEventTarget(e);
            if (target) {
                break;
            }
        }
        if (!target) {
            target = this;
        }
        return target;
    },
    _onchildadded: function(e) {
        var child = e.node;
        var next = e.next;
        if (child._element) {
            if (!this._layers.Dom) {
                this.addLayer('Dom', 1);
            }
            this._layers.Dom.insertBefore(child, next);
            child._layer = this._layers.Dom;
        } else {
            if (!this._layers.Canvas) {
                this.addLayer('Canvas', 0);
            }
            this._layers.Canvas.insertBefore(child, next);
            child._layer = this._layers.Canvas;
        }
        child.parentNode = this;
    },
    _onchildremoved: function(e) {
        var child = e.node;
        child._layer.removeChild(child);
        child._layer = null;
    },
    _onenter: function() {
        for (var type in this._layers) {
            this._layers[type]._startRendering();
        }
        loon.Core.instance.addEventListener('exitframe', this._dispatchExitframe);
    },
    _onexit: function() {
        for (var type in this._layers) {
            this._layers[type]._stopRendering();
        }
        loon.Core.instance.removeEventListener('exitframe', this._dispatchExitframe);
    }
});

loon.Bitmap = loon.Class.extend(loon.EventTarget, {

    initialize: function(width, height) {
        loon.EventTarget.call(this);

        var _core = loon.Core.instance;

        this.width = width;
      
        this.height = height;
    
        this.context = null;

        var id = 'lgraphics' + _core._surfaceID++;
        if (document.getCSSCanvasContext) {
            this.context = document.getCSSCanvasContext('2d', id, width, height);
            this._element = this.context.canvas;
            this._css = '-webkit-canvas(' + id + ')';
            var context = this.context;
        } else if (document.mozSetImageElement) {
            this._element = document.createElement('canvas');
            this._element.width = width;
            this._element.height = height;
            this._css = '-moz-element(#' + id + ')';
            this.context = this._element.getContext('2d');
            document.mozSetImageElement(id, this._element);
        } else {
            this._element = document.createElement('canvas');
            this._element.width = width;
            this._element.height = height;
            this._element.style.position = 'absolute';
            this.context = this._element.getContext('2d');

            loon.System.CANVAS_DRAWING_METHODS.forEach(function(name) {
                var method = this.context[name];
                this.context[name] = function() {
                    method.apply(this, arguments);
                    this._dirty = true;
                };
            }, this);
        }
    },

    getImage: function(){
	   return this.context;
	},

    getWidth: function(){
	    return this.width;
	},

    getHeight: function(){
		return this.height;
	},

    getPixel: function(x, y) {
        return this.context.getImageData(x, y, 1, 1).data;
    },
   
    setPixel: function(x, y, r, g, b, a) {
        var pixel = this.context.createImageData(1, 1);
        pixel.data[0] = r;
        pixel.data[1] = g;
        pixel.data[2] = b;
        pixel.data[3] = a;
        this.context.putImageData(pixel, x, y);
    },

    clear: function() {
        this.context.clearRect(0, 0, this.width, this.height);
    },

    draw: function(image) {
        image = image._element;
        if (arguments.length === 1) {
            this.context.drawImage(image, 0, 0);
        } else {
            var args = arguments;
            args[0] = image;
            this.context.drawImage.apply(this.context, args);
        }
    },
  
    clone: function() {
        var clone = new loon.Bitmap(this.width, this.height);
        clone.draw(this);
        return clone;
    },
 
    toDataURL: function() {
        var src = this._element.src;
        if (src) {
            if (src.slice(0, 5) === 'data:') {
                return src;
            } else {
                return this.clone().toDataURL();
            }
        } else {
            return this._element.toDataURL();
        }
    }
});

loon.Bitmap.load = function(src, callback) {
    var image = new Image();
    var surface = Object.extend(loon.Bitmap.prototype, {
        context: { value: null },
        _css: { value: 'url(' + src + ')' },
        _element: { value: image }
    });
    loon.EventTarget.call(surface);
    image.src = src;
    image.onerror = function() {
        throw new Error('Cannot load an asset: ' + image.src);
    };
    image.onload = function() {
        surface.width = image.width;
        surface.height = image.height;
        surface.dispatchEvent(new loon.Event('load'));
    };
    return surface;
};

/**
 * 游戏手柄(四方向)
 */
loon.Pad = loon.Class.extend(loon.Sprite, {
    initialize: function(src) {
        var core = loon.Core.instance;
        var image = core.file[src];
        loon.Sprite.call(this, image.width, image.height);
        this.image = image;
        this.input = { left: false, right: false, up: false, down: false };
        this.addEventListener('touchstart', function(e) {
            this._updateInput(this._detectInput(e.localX, e.localY));
        });
        this.addEventListener('touchmove', function(e) {
            this._updateInput(this._detectInput(e.localX, e.localY));
        });
        this.addEventListener('touchend', function(e) {
            this._updateInput({ left: false, right: false, up: false, down: false });
        });
    },

    _detectInput: function(x, y) {
        x -= this.width / 2;
        y -= this.height / 2;
        var input = { left: false, right: false, up: false, down: false };
        if (x * x + y * y > 200) {
            if (x < 0 && y < x * x * 0.1 && y > x * x * -0.1) {
                input.left = true;
            }
            if (x > 0 && y < x * x * 0.1 && y > x * x * -0.1) {
                input.right = true;
            }
            if (y < 0 && x < y * y * 0.1 && x > y * y * -0.1) {
                input.up = true;
            }
            if (y > 0 && x < y * y * 0.1 && x > y * y * -0.1) {
                input.down = true;
            }
        }
        return input;
    },

    _updateInput: function(input) {
        var core = loon.Core.instance;
        ['left', 'right', 'up', 'down'].forEach(function(type) {
            if (this.input[type] && !input[type]) {
                core.dispatchEvent(new loon.Event(type + 'buttonup'));
            }
            if (!this.input[type] && input[type]) {
                core.dispatchEvent(new loon.Event(type + 'buttondown'));
            }
        }, this);
        this.input = input;
    }
});

/**
 * 游戏控制器（八方向）
 */
loon.Control = loon.Class.extend(loon.Group, {

    initialize: function(src,mode) {

        var core = loon.Core.instance;
        var image = core.file[src];
        var w = this.width = image.width;
        var h = this.height = image.height;
        loon.Group.call(this);
	

        this.outside = new loon.Sprite(w, h);
        var outsideImage = new loon.Bitmap(w, h);
        outsideImage.draw(image, 0, 0, w, h / 4, 0, 0, w, h / 4);
        outsideImage.draw(image, 0, h / 4 * 3, w, h / 4, 0, h / 4 * 3, w, h / 4);
        outsideImage.draw(image, 0, h / 4, w / 4, h / 2, 0, h / 4, w / 4, h / 2);
        outsideImage.draw(image, w / 4 * 3, h / 4, w / 4, h / 2, w / 4 * 3, h / 4, w / 4, h / 2);
        this.outside.image = outsideImage;
        this.inside = new loon.Sprite(w / 2, h / 2);
        var insideImage = new loon.Bitmap(w / 2, h / 2);
        insideImage.draw(image, w / 4, h / 4, w / 2, h / 2, 0, 0, w / 2, h / 2);
        this.inside.image = insideImage;
        this.r = w / 2;

        this.isTouched = false;

        this.vx = 0;
        this.vy = 0;

        this.rad = 0;
        this.dist = 0;

        if (mode === 'direct') {
            this.mode = 'direct';
        } else {
            this.mode = 'normal';
        }
        this._updateImage();
        this.addChild(this.inside);
        this.addChild(this.outside);
        this.addEventListener('touchstart', function(e) {
            this._detectInput(e.localX, e.localY);
            this._calcPolar(e.localX, e.localY);
            this._updateImage(e.localX, e.localY);
            this._dispatchPadEvent('apadstart');
            this.isTouched = true;
        });
        this.addEventListener('touchmove', function(e) {
            this._detectInput(e.localX, e.localY);
            this._calcPolar(e.localX, e.localY);
            this._updateImage(e.localX, e.localY);
            this._dispatchPadEvent('apadmove');
        });
        this.addEventListener('touchend', function(e) {
            this._detectInput(this.width / 2, this.height / 2);
            this._calcPolar(this.width / 2, this.height / 2);
            this._updateImage(this.width / 2, this.height / 2);
            this._dispatchPadEvent('apadend');
            this.isTouched = false;
        });
    },
    _dispatchPadEvent: function(type) {
        var e = new loon.Event(type);
        e.vx = this.vx;
        e.vy = this.vy;
        e.rad = this.rad;
        e.dist = this.dist;
        this.dispatchEvent(e);
    },
    _updateImage: function(x, y) {
        x -= this.width / 2;
        y -= this.height / 2;
        this.inside.x = this.vx * (this.r - 10) + 25;
        this.inside.y = this.vy * (this.r - 10) + 25;
    },
    _detectInput: function(x, y) {
        x -= this.width / 2;
        y -= this.height / 2;
        var distance = Math.sqrt(x * x + y * y);
        var tan = y / x;
        var rad = Math.atan(tan);
        var dir = x / Math.abs(x);
        if (distance === 0) {
            this.vx = 0;
            this.vy = 0;
        } else if (x === 0) {
            this.vx = 0;
            if (this.mode === 'direct') {
                this.vy = (y / this.r);
            } else {
                dir = y / Math.abs(y);
                this.vy = Math.pow((y / this.r), 2) * dir;
            }
        } else if (distance < this.r) {
            if (this.mode === 'direct') {
                this.vx = (x / this.r);
                this.vy = (y / this.r);
            } else {
                this.vx = Math.pow((distance / this.r), 2) * Math.cos(rad) * dir;
                this.vy = Math.pow((distance / this.r), 2) * Math.sin(rad) * dir;
            }
        } else {
            this.vx = Math.cos(rad) * dir;
            this.vy = Math.sin(rad) * dir;
        }
    },
    _calcPolar: function(x, y) {
        x -= this.width / 2;
        y -= this.height / 2;
        var add = 0;
        var rad = 0;
        var dist = Math.sqrt(x * x + y * y);
        if (dist > this.r) {
            dist = this.r;
        }
        dist /= this.r;
        if (this.mode === 'normal') {
            dist *= dist;
        }
        if (x >= 0 && y < 0) {
            add = Math.PI / 2 * 3;
            rad = x / y;
        } else if (x < 0 && y <= 0) {
            add = Math.PI;
            rad = y / x;
        } else if (x <= 0 && y > 0) {
            add = Math.PI / 2;
            rad = x / y;
        } else if (x > 0 && y >= 0) {
            add = 0;
            rad = y / x;
        }
        if (x === 0 || y === 0) {
            rad = 0;
        }
        this.rad = Math.abs(Math.atan(rad)) + add;
        this.dist = dist;

    }
});

function callScript(){
	$sync(function() {
    loon();
	//加载当前目录下的main.js
	if(config.beforeMainImport){
		    $import('main.js');
			//如果存在OnMain函数，此处自动加载
			window.onload = function(){
				 OnMain();
				 if(_game_core != null){ 
				    _game_core.onload = function(){   
					   OnProcess(_game_core);
					   if(config.useCanvasUpdate && _game_core.root._context == null){
					      _game_core.root.addLayer('Canvas',0);
					   }
				   };
				   _game_core.show();
				 };
			}
	}
	});
}


loon.DOMSound = loon.Class.extend(loon.EventTarget, {
  
    initialize: function() {
        loon.EventTarget.call(this);
        this.duration = 0;
        throw new Error("Illegal Constructor");
    },

    play: function() {
        if (this._element) {
            this._element.play();
        }
    },
  
    pause: function() {
        if (this._element) {
            this._element.pause();
        }
    },

    exit: function() {
        this.pause();
        this.currentTime = 0;
    },
  
    clone: function() {
        var clone;
        if (this._element instanceof Audio) {
            clone = Object.extend(loon.DOMSound.prototype, {
                _element: { value: this._element.cloneNode(false) },
                duration: { value: this.duration }
            });
        } else if (loon.System.SUPPORT_FLASH_SOUND) {
            return this;
        } else {
            clone = Object.extend(loon.DOMSound.prototype);
        }
        loon.EventTarget.call(clone);
        return clone;
    },

    currentTime: {
        get: function() {
            return this._element ? this._element.currentTime : 0;
        },
        set: function(time) {
            if (this._element) {
                this._element.currentTime = time;
            }
        }
    },
  
    volume: {
        get: function() {
            return this._element ? this._element.volume : 1;
        },
        set: function(volume) {
            if (this._element) {
                this._element.volume = volume;
            }
        }
    }
});

loon.DOMSound.load = function(src, type, callback) {
    if (type == null) {
        var ext = loon.Core.findExt(src);
        if (ext) {
            type = 'audio/' + ext;
        } else {
            type = '';
        }
    }
    type = type.replace('mp3', 'mpeg').replace('m4a', 'mp4');

    var sound = Object.extend(loon.DOMSound.prototype);
    loon.EventTarget.call(sound);
    var audio = new Audio();
    if (!loon.System.SOUND_ENABLED_ON_MOBILE_SAFARI &&
        loon.System.BROWSER === 'webkit' && loon.System.SUPPORT_TOUCH) {
        window.setTimeout(function() {
            sound.dispatchEvent(new loon.Event('load'));
        }, 0);
    } else {
        if (!loon.System.SUPPORT_FLASH_SOUND && audio.canPlayType(type)) {
            audio.src = src;
            audio.load();
            audio.autoplay = false;
            audio.onerror = function() {
                throw new Error('Cannot load an asset: ' + audio.src);
            };
            audio.addEventListener('canplaythrough', function() {
                sound.duration = audio.duration;
                sound.dispatchEvent(new loon.Event('load'));
            }, false);
            sound._element = audio;
        } else if (type === 'audio/mpeg') {
            var embed = document.createElement('embed');
            var id = 'loon-audio' + loon.Core.instance._soundID++;
            embed.width = embed.height = 1;
            embed.name = id;
            embed.src = 'sound.swf?id=' + id + '&src=' + src;
            embed.allowscriptaccess = 'always';
            embed.style.position = 'absolute';
            embed.style.left = '-1px';
            sound.addEventListener('load', function() {
                Object.defineProperties(embed, {
                    currentTime: {
                        get: function() {
                            return embed.getCurrentTime();
                        },
                        set: function(time) {
                            embed.setCurrentTime(time);
                        }
                    },
                    volume: {
                        get: function() {
                            return embed.getVolume();
                        },
                        set: function(volume) {
                            embed.setVolume(volume);
                        }
                    }
                });
                sound._element = embed;
                sound.duration = embed.getDuration();
            });
            loon.Core.instance._element.appendChild(embed);
            loon.DOMSound[id] = sound;
        } else {
            window.setTimeout(function() {
                sound.dispatchEvent(new loon.Event('load'));
            }, 0);
        }
        sound.addEventListener('load', function() {
            callback.call(loon.Core.instance);
        });
    }
    return sound;
};


window.AudioContext = window.AudioContext || window.webkitAudioContext || window.mozAudioContext || window.msAudioContext || window.oAudioContext;

loon.WebAudioSound = loon.Class.extend(loon.EventTarget, {

    initialize: function() {
        if(!window.webkitAudioContext){
            throw new Error("This browser does not support WebAudio API.");
        }
        var actx = loon.WebAudioSound.audioContext;
        loon.EventTarget.call(this);
        this.src = actx.createBufferSource();
        this.buffer = null;
        this._volume = 1;
        this._currentTime = 0;
        this._state = 0;
        this.connectTarget = loon.WebAudioSound.destination;
    },
    play: function(dup) {
        var actx = loon.WebAudioSound.audioContext;
        if (this._state === 2) {
            this.src.connect(this.connectTarget);
        } else {
            if (this._state === 1 && !dup) {
                this.src.disconnect(this.connectTarget);
            }
            this.src = actx.createBufferSource();
            this.src.buffer = this.buffer;
            this.src.gain.value = this._volume;
            this.src.connect(this.connectTarget);
            this.src.noteOn(0);
        }
        this._state = 1;
    },
    pause: function() {
        var actx = loon.WebAudioSound.audioContext;
        this.src.disconnect(this.connectTarget);
        this._state = 2;
    },
    exit: function() {
        this.src.noteOff(0);
        this._state = 0;
    },
    clone: function() {
        var sound = new loon.WebAudioSound();
        sound.buffer = this.buffer;
        return sound;
    },
    dulation: {
        get: function() {
            if (this.buffer) {
                return this.buffer.dulation;
            } else {
                return 0;
            }
        }
    },
    volume: {
        get: function() {
            return this._volume;
        },
        set: function(volume) {
            volume = Math.max(0, Math.min(1, volume));
            this._volume = volume;
            if (this.src) {
                this.src.gain.value = volume;
            }
        }
    },
    currentTime: {
        get: function() {
            return this._currentTime;
        },
        set: function(time) {
            this._currentTime = time;
        }
    }
});

loon.WebAudioSound.load = function(src, type, callback) {
    var actx = loon.WebAudioSound.audioContext;
    var xhr = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject('Msxml2.XMLHTTP');
    var sound = new loon.WebAudioSound();
    var mimeType = 'audio/' + loon.Core.findExt(src);
    xhr.responseType = 'arraybuffer';
    xhr.open('GET', src, true);
    xhr.onload = function() {
        actx.decodeAudioData(
            xhr.response,
            function(buffer) {
                sound.buffer = buffer;
                callback.call(loon.Core.instance);
            },
            function(error) {
                window.console.log(error);
            }
        );
    };
    xhr.send(null);
    return sound;
};

if(window.AudioContext){
    loon.WebAudioSound.audioContext = new window.AudioContext();
    loon.WebAudioSound.destination = loon.WebAudioSound.audioContext.destination;
}

loon.Sound = window.AudioContext && loon.System.USE_WEBAUDIO ? loon.WebAudioSound : loon.DOMSound;

loon.Easing = {

    LINEAR: function(t, b, c, d) {
        return c * t / d + b;
    },
    SWING: function(t, b, c, d) {
        return c * (0.5 - Math.cos(((t / d) * Math.PI)) / 2) + b;
    },

    QUAD_EASEIN: function(t, b, c, d) {
        return c * (t /= d) * t + b;
    },

    QUAD_EASEOUT: function(t, b, c, d) {
        return -c * (t /= d) * (t - 2) + b;
    },

    QUAD_EASEINOUT: function(t, b, c, d) {
        if ((t /= d / 2) < 1) {
            return c / 2 * t * t + b;
        }
        return -c / 2 * ((--t) * (t - 2) - 1) + b;
    },

    CUBIC_EASEIN: function(t, b, c, d) {
        return c * (t /= d) * t * t + b;
    },

    CUBIC_EASEOUT: function(t, b, c, d) {
        return c * ((t = t / d - 1) * t * t + 1) + b;
    },

    CUBIC_EASEINOUT: function(t, b, c, d) {
        if ((t /= d / 2) < 1) {
            return c / 2 * t * t * t + b;
        }
        return c / 2 * ((t -= 2) * t * t + 2) + b;
    },

    QUART_EASEIN: function(t, b, c, d) {
        return c * (t /= d) * t * t * t + b;
    },

    QUART_EASEOUT: function(t, b, c, d) {
        return -c * ((t = t / d - 1) * t * t * t - 1) + b;
    },

    QUART_EASEINOUT: function(t, b, c, d) {
        if ((t /= d / 2) < 1) {
            return c / 2 * t * t * t * t + b;
        }
        return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
    },
    
    QUINT_EASEIN: function(t, b, c, d) {
        return c * (t /= d) * t * t * t * t + b;
    },

    QUINT_EASEOUT: function(t, b, c, d) {
        return c * ((t = t / d - 1) * t * t * t * t + 1) + b;
    },

    QUINT_EASEINOUT: function(t, b, c, d) {
        if ((t /= d / 2) < 1) {
            return c / 2 * t * t * t * t * t + b;
        }
        return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
    },

    SIN_EASEIN: function(t, b, c, d) {
        return -c * Math.cos(t / d * (Math.PI / 2)) + c + b;
    },

    SIN_EASEOUT: function(t, b, c, d) {
        return c * Math.sin(t / d * (Math.PI / 2)) + b;
    },

    SIN_EASEINOUT: function(t, b, c, d) {
        return -c / 2 * (Math.cos(Math.PI * t / d) - 1) + b;
    },
    
    CIRC_EASEIN: function(t, b, c, d) {
        return -c * (Math.sqrt(1 - (t /= d) * t) - 1) + b;
    },

    CIRC_EASEOUT: function(t, b, c, d) {
        return c * Math.sqrt(1 - (t = t / d - 1) * t) + b;
    },
   
    CIRC_EASEINOUT: function(t, b, c, d) {
        if ((t /= d / 2) < 1) {
            return -c / 2 * (Math.sqrt(1 - t * t) - 1) + b;
        }
        return c / 2 * (Math.sqrt(1 - (t -= 2) * t) + 1) + b;
    },

    ELASTIC_EASEIN: function(t, b, c, d, a, p) {
        if (t === 0) {
            return b;
        }
        if ((t /= d) === 1) {
            return b + c;
        }

        if (!p) {
            p = d * 0.3;
        }

        var s;
        if (!a || a < Math.abs(c)) {
            a = c;
            s = p / 4;
        } else {
            s = p / (2 * Math.PI) * Math.asin(c / a);
        }
        return -(a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
    },

    ELASTIC_EASEOUT: function(t, b, c, d, a, p) {
        if (t === 0) {
            return b;
        }
        if ((t /= d) === 1) {
            return b + c;
        }
        if (!p) {
            p = d * 0.3;
        }
        var s;
        if (!a || a < Math.abs(c)) {
            a = c;
            s = p / 4;
        } else {
            s = p / (2 * Math.PI) * Math.asin(c / a);
        }
        return (a * Math.pow(2, -10 * t) * Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b);
    },

    ELASTIC_EASEINOUT: function(t, b, c, d, a, p) {
        if (t === 0) {
            return b;
        }
        if ((t /= d / 2) === 2) {
            return b + c;
        }
        if (!p) {
            p = d * (0.3 * 1.5);
        }
        var s;
        if (!a || a < Math.abs(c)) {
            a = c;
            s = p / 4;
        } else {
            s = p / (2 * Math.PI) * Math.asin(c / a);
        }
        if (t < 1) {
            return -0.5 * (a * Math.pow(2, 10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
        }
        return a * Math.pow(2, -10 * (t -= 1)) * Math.sin((t * d - s) * (2 * Math.PI) / p) * 0.5 + c + b;
    },

    BOUNCE_EASEOUT: function(t, b, c, d) {
        if ((t /= d) < (1 / 2.75)) {
            return c * (7.5625 * t * t) + b;
        } else if (t < (2 / 2.75)) {
            return c * (7.5625 * (t -= (1.5 / 2.75)) * t + 0.75) + b;
        } else if (t < (2.5 / 2.75)) {
            return c * (7.5625 * (t -= (2.25 / 2.75)) * t + 0.9375) + b;
        } else {
            return c * (7.5625 * (t -= (2.625 / 2.75)) * t + 0.984375) + b;
        }
    },
   
    BOUNCE_EASEIN: function(t, b, c, d) {
        return c - loon.Easing.BOUNCE_EASEOUT(d - t, 0, c, d) + b;
    },
 
    BOUNCE_EASEINOUT: function(t, b, c, d) {
        if (t < d / 2) {
            return loon.Easing.BOUNCE_EASEIN(t * 2, 0, c, d) * 0.5 + b;
        } else {
            return loon.Easing.BOUNCE_EASEOUT(t * 2 - d, 0, c, d) * 0.5 + c * 0.5 + b;
        }

    },

    BACK_EASEIN: function(t, b, c, d, s) {
        if (s === undefined) {
            s = 1.70158;
        }
        return c * (t /= d) * t * ((s + 1) * t - s) + b;
    },
 
    BACK_EASEOUT: function(t, b, c, d, s) {
        if (s === undefined) {
            s = 1.70158;
        }
        return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
    },

    BACK_EASEINOUT: function(t, b, c, d, s) {
        if (s === undefined) {
            s = 1.70158;
        }
        if ((t /= d / 2) < 1) {
            return c / 2 * (t * t * (((s *= (1.525)) + 1) * t - s)) + b;
        }
        return c / 2 * ((t -= 2) * t * (((s *= (1.525)) + 1) * t + s) + 2) + b;
    },

    EXPO_EASEIN: function(t, b, c, d) {
        return (t === 0) ? b : c * Math.pow(2, 10 * (t / d - 1)) + b;
    },

    EXPO_EASEOUT: function(t, b, c, d) {
        return (t === d) ? b + c : c * (-Math.pow(2, -10 * t / d) + 1) + b;
    },

    EXPO_EASEINOUT: function(t, b, c, d) {
        if (t === 0) {
            return b;
        }
        if (t === d) {
            return b + c;
        }
        if ((t /= d / 2) < 1) {
            return c / 2 * Math.pow(2, 10 * (t - 1)) + b;
        }
        return c / 2 * (-Math.pow(2, -10 * --t) + 2) + b;
    }
};

loon.ActionEventTarget = loon.Class.extend(loon.EventTarget, {

    initialize: function() {
        loon.EventTarget.apply(this, arguments);
    },

    dispatchEvent: function(e) {
        var target;
        if (this.node) {
            target = this.node;
            e.target = target;
            e.localX = e.x - target._offsetX;
            e.localY = e.y - target._offsetY;
        } else {
            this.node = null;
        }

        if (this['on' + e.type] != null) {
            this['on' + e.type].call(target, e);
        }
        var listeners = this._listeners[e.type];
        if (listeners != null) {
            listeners = listeners.slice();
            for (var i = 0, len = listeners.length; i < len; i++) {
                listeners[i].call(target, e);
            }
        }
    }
});

/**
 * 角色时间线处理
 */
loon.Timeline = loon.Class.extend(loon.EventTarget, {

    initialize: function(node) {
        loon.EventTarget.call(this);
        this.node = node;
        this.queue = [];
        this.paused = false;
        this.looped = false;
        this.isFrameBased = true;
        this._parallel = null;
        this._activated = false;
        this.addEventListener(loon.Event.ENTER_FRAME, this.tick);
    },

    _deactivateTimeline: function() {
        if (this._activated) {
            this._activated = false;
            this.node.removeEventListener('enterframe', this._nodeEventListener);
        }
    },

    _activateTimeline: function() {
        if (!this._activated && !this.paused) {
            this.node.addEventListener("enterframe", this._nodeEventListener);
            this._activated = true;
        }
    },

    setFrameBased: function() {
        this.isFrameBased = true;
    },

    setTimeBased: function() {
        this.isFrameBased = false;
    },

    next: function(remainingTime) {
        var e, action = this.queue.shift();
        e = new loon.Event("actionend");
        e.timeline = this;
        action.dispatchEvent(e);

        if (this.queue.length === 0) {
            this._activated = false;
            this.node.removeEventListener('enterframe', this._nodeEventListener);
            return;
        }

        if (this.looped) {
            e = new loon.Event("removedfromtimeline");
            e.timeline = this;
            action.dispatchEvent(e);
            action.frame = 0;
            this.add(action);
        } else {
            e = new loon.Event("removedfromtimeline");
            e.timeline = this;
            action.dispatchEvent(e);
        }
        if (remainingTime > 0 || (this.queue[0] && this.queue[0].time === 0)) {
            var event = new loon.Event("enterframe");
            event.elapsed = remainingTime;
            this.dispatchEvent(event);
        }
    },

    tick: function(enterFrameEvent) {
        if (this.paused) {
            return;
        }
        if (this.queue.length > 0) {
            var action = this.queue[0];
            if (action.frame === 0) {
                var f;
                f = new loon.Event("actionstart");
                f.timeline = this;
                action.dispatchEvent(f);
            }

            var e = new loon.Event("actiontick");
            e.timeline = this;
            if (this.isFrameBased) {
                e.elapsed = 1;
            } else {
                e.elapsed = enterFrameEvent.elapsed;
            }
            action.dispatchEvent(e);
        }
    },
    add: function(action) {
        if (!this._activated) {
            var tl = this;
            this._nodeEventListener = function(e) {
                tl.dispatchEvent(e);
            };
            this.node.addEventListener("enterframe", this._nodeEventListener);

            this._activated = true;
        }
        if (this._parallel) {
            this._parallel.actions.push(action);
            this._parallel = null;
        } else {
            this.queue.push(action);
        }
        action.frame = 0;

        var e = new loon.Event("addedtotimeline");
        e.timeline = this;
        action.dispatchEvent(e);

        e = new loon.Event("actionadded");
        e.action = action;
        this.dispatchEvent(e);

        return this;
    },

    action: function(params) {
        return this.add(new loon.Action(params));
    },

    tween: function(params) {
        return this.add(new loon.Tween(params));
    },

    clear: function() {
        var e = new loon.Event("removedfromtimeline");
        e.timeline = this;

        for (var i = 0, len = this.queue.length; i < len; i++) {
            this.queue[i].dispatchEvent(e);
        }
        this.queue = [];
        this._deactivateTimeline();
        return this;
    },

    skip: function(frames) {
        var event = new loon.Event("enterframe");
        if (this.isFrameBased) {
            event.elapsed = 1;
        } else {
            event.elapsed = frames;
            frames = 1;
        }
        while (frames--) {
            this.dispatchEvent(event);
        }
        return this;
    },
  
    pause: function() {
        if (!this.paused) {
            this.paused = true;
            this._deactivateTimeline();
        }
        return this;
    },
 
    resume: function() {
        if (this.paused) {
            this.paused = false;
            this._activateTimeline();
        }
        return this;
    },

    loop: function() {
        this.looped = true;
        return this;
    },
  
    unloop: function() {
        this.looped = false;
        return this;
    },
   
    delay: function(time) {
        this.add(new loon.Action({
            time: time
        }));
        return this;
    },

    wait: function(time) {
        return this;
    },

    then: function(func) {
        var timeline = this;
        this.add(new loon.Action({
            onactiontick: function(evt) {
                func.call(timeline.node);
            },
            time: 0
        }));
        return this;
    },
   
    exec: function(func) {
        this.then(func);
    },

    cue: function(cue) {
        var ptr = 0;
        for (var frame in cue) {
            if (cue.hasOwnProperty(frame)) {
                this.delay(frame - ptr);
                this.then(cue[frame]);
                ptr = frame;
            }
        }
    },

    repeat: function(func, time) {
        this.add(new loon.Action({
            onactiontick: function(evt) {
                func.call(this);
            },
            time: time
        }));
        return this;
    },
 
    and: function() {
        var last = this.queue.pop();
        if (last instanceof loon.ParallelAction) {
            this._parallel = last;
            this.queue.push(last);
        } else {
            var parallel = new loon.ParallelAction();
            parallel.actions.push(last);
            this.queue.push(parallel);
            this._parallel = parallel;
        }
        return this;
    },
 
    or: function() {
        return this;
    },
   
    doAll: function(children) {
        return this;
    },

    waitAll: function() {
        return this;
    },

    waitUntil: function(func) {
        var timeline = this;
        this.add(new loon.Action({
            onactionstart: func,
            onactiontick: function(evt) {
                if (func.call(this)) {
                    timeline.next();
                }
            }
        }));
        return this;
    },
 
    fadeTo: function(opacity, time, easing) {
        this.tween({
            opacity: opacity,
            time: time,
            easing: easing
        });
        return this;
    },
 
    fadeIn: function(time, easing) {
        return this.fadeTo(1, time, easing);
    },
  
    fadeOut: function(time, easing) {
        return this.fadeTo(0, time, easing);
    },
  
    moveTo: function(x, y, time, easing) {
        return this.tween({
            x: x,
            y: y,
            time: time,
            easing: easing
        });
    },
  
    moveX: function(x, time, easing) {
        return this.tween({
            x: x,
            time: time,
            easing: easing
        });
    },

    moveY: function(y, time, easing) {
        return this.tween({
            y: y,
            time: time,
            easing: easing
        });
    },
  
    moveBy: function(x, y, time, easing) {
        return this.tween({
            x: function() {
                return this.x + x;
            },
            y: function() {
                return this.y + y;
            },
            time: time,
            easing: easing
        });
    },
   
    hide: function() {
        return this.then(function() {
            this.opacity = 0;
        });
    },
  
    show: function() {
        return this.then(function() {
            this.opacity = 1;
        });
    },
 
    removeFromScreen: function() {
        return this.then(function() {
            this.screen.removeChild(this);
        });
    },

    scaleTo: function(scale, time, easing) {
        if (typeof easing === "number") {
            return this.tween({
                scaleX: arguments[0],
                scaleY: arguments[1],
                time: arguments[2],
                easing: arguments[3]
            });
        }
        return this.tween({
            scaleX: scale,
            scaleY: scale,
            time: time,
            easing: easing
        });
    },

    scaleBy: function(scale, time, easing) {
        if (typeof easing === "number") {
            return this.tween({
                scaleX: function() {
                    return this.scaleX * arguments[0];
                },
                scaleY: function() {
                    return this.scaleY * arguments[1];
                },
                time: arguments[2],
                easing: arguments[3]
            });
        }
        return this.tween({
            scaleX: function() {
                return this.scaleX * scale;
            },
            scaleY: function() {
                return this.scaleY * scale;
            },
            time: time,
            easing: easing
        });
    },

    rotateTo: function(deg, time, easing) {
        return this.tween({
            rotation: deg,
            time: time,
            easing: easing
        });
    },

    rotateBy: function(deg, time, easing) {
        return this.tween({
            rotation: function() {
                return this.rotation + deg;
            },
            time: time,
            easing: easing
        });
    }
});

/**
 * 角色动作处理
 */
loon.Action = loon.Class.extend(loon.ActionEventTarget, {

    initialize: function(param) {
        loon.ActionEventTarget.call(this);
        this.time = null;
        this.frame = 0;
        for (var key in param) {
            if (param.hasOwnProperty(key)) {
                if (param[key] != null) {
                    this[key] = param[key];
                }
            }
        }
        var action = this;
        this.timeline = null;
        this.node = null;

        this.addEventListener(loon.Event.ADDED_TO_TIMELINE, function(evt) {
            action.timeline = evt.timeline;
            action.node = evt.timeline.node;
            action.frame = 0;
        });

        this.addEventListener(loon.Event.REMOVED_FROM_TIMELINE, function() {
            action.timeline = null;
            action.node = null;
            action.frame = 0;
        });

        this.addEventListener(loon.Event.ACTION_TICK, function(evt) {
            var remaining = action.time - (action.frame + evt.elapsed);
            if (action.time != null && remaining <= 0) {
                action.frame = action.time;
                evt.timeline.next(-remaining);
            } else {
                action.frame += evt.elapsed;
            }
        });

    }
});

/**
 * 同步加载loon启动函数
 */
if(config.beforeLoadMainFunction){
      callScript();
}

/**
 * end js
 */
}(window));
