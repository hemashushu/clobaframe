/*
 * common.js
 */

/**
 *  Page scripts initial pattern:
 *  define:
 *    $.pages.pageName = {
 *			init:function(){...},
 *			otherMethod:function(){...}
 *			};
 *  invoke:
 *    $.pages.pageName.init();
 *
 *  Page options, i.e. some values from backend to script:
 *	  $.pages.options = {
 *			"hideLauncherTitle":false,
 *			"authenticated":true
 *		};
 */
jQuery.pages = {};

var i18nMessageDefault= {};
var i18nMessageLocal = {};

/**
 * Addition functions
 */
(function($){
	String.prototype.escapeHtml = function(){
			var content = this.replace(/&/g, '&amp;');
			content = content.replace(/>/g, '&gt;');
			content = content.replace(/</g, '&lt;');
			content = content.replace(/"/g, '&quot;');
			content = content.replace(/\n/g, '<br/>');
			return content;
		};

	String.prototype.unescapeHtml = function(){
			var content = this.replace(/<br\/>/ig, '\n');
			content = content.replace(/<br>/ig, '\n');
			content = content.replace(/&lt;/g, '<');
			content = content.replace(/&gt;/g, '>');
			content = content.replace(/&quot;/g, '"');
			content = content.replace(/&amp;/g, '&');
			return content;
		};

	/*
	 * ie-fix (6,7,8 add Date.toISOString() function to 'Date' prototype)
	 * Output as 'YYYY-MM-DDTHH:mm:ss.sssZ'
	 */
	var padZero = function(number){
		var r = String(number);
		if ( r.length === 1 ) {
			r = '0' + r;
		}
		return r;
	};

	if (typeof Date.prototype.toISOString == 'undefined'){
		Date.prototype.toISOString = function(){
			var date = this;
			return date.getUTCFullYear()+'-'
				+ padZero(date.getUTCMonth()+1)+'-'
				+ padZero(date.getUTCDate())+'T'
				+ padZero(date.getUTCHours())+':'
				+ padZero(date.getUTCMinutes())+':'
				+ padZero(date.getUTCSeconds())+'Z';
		};
	};

	// add 'isie6' and 'isie8' to jQuery.browser
	$.browser.isie6 = function(){
		return ($.browser.msie && $.browser.version < 7) ? true:false;
	};

	$.browser.isie8 = function(){
		return ($.browser.msie && $.browser.version == 8) ? true:false;
	};

	/**
	 * check the DOM object equals.
	 *
	 * var left = $('div.one');
	 * var right = $('div.two');
	 * var result = $.jqEquals(left, right);
	 *
	 * null & null = true.
	 * null & element(s) = false.
	 * same element(s) = true.
	 * more elements & less elements = false.
	 * no element & no element = false.
	 *
	 */
	var jqEquals = function(left, right) {
		if (left == null && right == null) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}

		if (left.length != right.length) {
			return false;
		}

		if (left.length == 0){
			return false;
		}

		for (var idx=0; idx < left.length; idx++) {
			if (left[idx] != right[idx]) {
				return false;
			}
		}

		return true;
	};

	$.jqEquals = jqEquals;

	/**
	 * jQuery object set.
	 *
	 * Add none-duplicated object:
	 *
	 * var s = new $.jqSet();
	 * var object = $('#someElements');
	 * s.add(object);
	 *
	 * Get all items:
	 * var objects = s.getAll();
	 *
	 * Get item by index:
	 * var object = s.getByIndex(0);
	 *
	 */
	var jqSet = function () {
		var objects = [];

		this.size = function() {
			return objects.length;
		};

		this.getAll = function() {
			return objects;
		};

		this.getByIndex = function(idx) {
			return objects[idx];
		};

		this.add = function(jqObject){
			if (jqObject.length == 0){
				return;
			}

			if (!this.contains(jqObject)){
				objects.push(jqObject);
			}
		};

		this.remove = function(jqObject){
			var idx = this.indexOf(jqObject);
			if (idx >=0){
				objects.splice(idx, 1);
			}
		};

		this.indexOf = function(jqObject){
			for(var idx in objects){
				if ($.jqEquals(objects[idx], jqObject)){
					return idx;
				}
			}
			return -1;
		};

		this.contains = function(jqObject){
			return (this.indexOf(jqObject) != -1);
		};

		this.clear = function(){
			objects.splice(0, objects.length);
		};
	};

	$.jqSet = jqSet;

	/*
	 * A map that using jQuery object as key.
	 *
	 * var s = new $.jqMap();
	 * var object = $('#someElements');
	 * s.put(object, 'value1');
	 *
	 * Replace the value of the same key.
	 * s.put(object, 'value2');
	 *
	 * Get all items:
	 * var objects = s.getAll();
	 *
	 * Get item value by jQuery object:
	 * var value = s.get(object);
	 *
	 */
	var jqMap = function () {
		// objects contains a set of items: {key:jQueryObject, value:'itemValue'}
		var objects = [];

		this.size = function() {
			return objects.length;
		};

		this.getAll = function() {
			return objects;
		};

		this.get = function(jqObject) {
			var idx = this.indexOf(jqObject);
			if (idx >=0){
				return objects[idx].value;
			}else{
				return null;
			}
		};

		/*
		 * If the key (jqObject) has already exists, the new value will replace
		 * the old one.
		 *
		 */
		this.put = function(jqObject, value){
			if (jqObject.length == 0){
				return;
			}

			var idx = this.indexOf(jqObject);
			if (idx >= 0){
				// replace the old value.
				var oldItem = objects[idx];
				oldItem.value = value;
			}else{
				var item = {key:jqObject, value:value};
				objects.push(item);
			}
		};

		this.remove = function(jqObject){
			var idx = this.indexOf(jqObject);
			if (idx >=0){
				objects.splice(idx, 1);
			}
		};

		this.indexOf = function(jqObject){
			for(var idx in objects){
				var item = objects[idx];
				if ($.jqEquals(item.key, jqObject)){
					return idx;
				}
			}
			return -1;
		};

		this.contains = function(jqObject){
			return (this.indexOf(jqObject) != -1);
		};

		this.clear = function(){
			objects.splice(0, objects.length);
		};
	};

	$.jqMap = jqMap;

	/**
	 * Simple object collection query.
	 *
	 * Considers this:
	 * var array = [{id:1, name:'hello', pass:true},
	 *				{id:2, name:'world'},
	 *				{id:3, name:'foobar', pass:false}];
	 *
	 * // list:
	 * var r1 = new $.simpleQuery(array).list();
	 *
	 * // get first one:
	 * var r2 = new $.simpleQuery(array).first();
	 *
	 * // select names [id,name]':
	 * var r3 = new $.simpleQuery(array).select(['id','name']);
	 *
	 * // select items where 'name' equals 'world':
	 * var r4 = new $.simpleQuery(array).whereEquals('name', 'world').list();
	 *
	 */
	var simpleQuery = function(array){

		if(!$.isArray(array)) {
			$.error('The params must be a function.');
		}

		var items = array;

		var where = function(func) {
			if (typeof func != 'function'){
				$.error('The func option must be a function.');
			}

			var selected = [];
			for (var idx=0; idx<items.length; idx++){
				if (func(items[idx], idx)){
					selected.push(items[idx]);
				}
			}

			return new simpleQuery(selected);
		};

		this.where = where;

		this.whereEquals = function(name, val){
			return where(function(e){
				return (e[name] == val);
			});
		};

		this.first = function(){
			return ((items.length > 0)?items[0]:null);
		};

		this.list = function() {
			return items;
		};

		this.select = function(names){
			if (!$.isArray(names)) {
				$.error('The params must be a function.');
			}

			var selected = [];
			for (var idx=0; idx<items.length; idx++) {
				var item = items[idx];
				var obj = {};
				for (var sub=0; sub<names.length; sub++){
					var name = names[sub];
					obj[name] = item[name];
				}
				selected.push(obj);
			}
			return selected;
		};
	};

	$.simpleQuery = simpleQuery;

})(jQuery);

/**
 * I18n message resolver.
 *
 * The global variable 'i18nMessageDefault' and 'i18nMessageLocal' defines
 * all messages text.
 *
 * Set source first:
 * $.message.setSource(defaultSourceObject, localSourceObject);
 *
 * Get text by code and params:
 * var text = $.message('test.text1');
 *
 * For example , the content of code 'test.text2' is 'hello {0}, now is {1}.',
 * and then put params ['Dude', new Date()].
 *
 * var text = $.message('test.text2',['Dude', new Date()]);
 *
 */
(function($){
	var messages = null;
	var messagesLocal = null;

	var methods = {
		setSource:function(defaults, local){
			messages = defaults;
			messagesLocal = local;
		},

		get:function(code, params){
			var content = null;
			if (messagesLocal == null){
				content = messages[code];
			}else{
				content = messagesLocal[code];
				if (content == null){
					content = messages[code];
				}
			}

			if (content == null){
				return 'CODE_NOT_DEFINED [' + code + ']';
			}

			// replace placeholders
			for(var idx in params){
				var regexp = new RegExp('\\{' + idx + '(,\\w+)*\\}','g');
				content = content.replace(regexp, params[idx]);
			}

			return content;
		}
	};

	$(function(){
		if (typeof i18nMessageDefault == 'undefined'){
			$.error('No default i18n message defined.');
		}else if (typeof i18nMessageLocal == 'undefined'){
			methods.setSource(i18nMessageDefault, null);
		}else{
			methods.setSource(i18nMessageDefault, i18nMessageLocal);
		}
	});

	// expose the 'get()' method to jQuery.message().
	$.message = methods.get;

})(jQuery);



/**
 * Flash message panel.
 * Commonly show the submit result or the error message.
 *
 * $('#someButton').flashMessage({
 *		content:'message.code',
 *		type:'info',
 *		hide:'normal',
 *		actions:[
 *			{title:'title.code', id:'action-id', params:'some-value-pass-to-callback'}
 *			]
 *		});
 *
 * $.flashMessage.hide();
 * $.flashMessage.addActionListener('actionId',function(params){});
 *
 */
(function($){
	//var isShowing = false; // indicates the showing state.
	var hideTimer = null; // the timer to hide message.

	var delayNormal = 6000; // automatically hiding delay
	var delayFast = 3000;
	var delaySlow = 9000;

//	var minWidth = 300; // the min-width of notification panel.
//	var maxWidth = 640;

	var defaults = {
		content:'', // the content message code
		type:'info', // 'info', 'warn'
		hide:'normal', // 'fast', 'normal', 'slow', 'off'.

		/*
		 * The showing position.
		 * 'right', 'bottom' is relate to the target.
		 * 'topmost' is float on the top side of page.
		 */
		position:'right', // 'right','bottom','topmost'
		offset:0, // the offset between the message panel and the target.

		/*
		 *actions list, such as:
		 * [
		 *   {title:'action-title(message-code)',id:'action-id', params:'param-value'},
		 *   {title:'Yes',id:'id-yes'}
		 * ]
		 */
		actions:null
	};

	// action handlers
	var actions = {};

	var methods = {

		buildElements:function(){
			var body = $('body');
			var exists = body.find('.flashMessagePanel');
			if (exists.length > 0){
				return exists;
			}

			var panel = $('<div class="flashMessagePanel">' +
				' <div class="content">' +
				'  <a class="close" href="#" title="Close">x</a>' +
				'  <span class="text"></span>' +
				'  <span class="action"></span>' +
				' </div>' +
				'</div>');

			body.append(panel);

			var closeButton = panel.find('a.close');
			closeButton.click(function(event){
				event.preventDefault();
				methods.hide();
			});

			return panel;
		},

//		show:function(content, type, hide, actions){
//			var message = {content:content, type:type, hide:hide, actions:actions};
//			methods.showByObject(message);
//		},

		init:function(options){
			return this.each(function(){
				var target = $(this);
				var settings = $.extend({}, defaults, options);
				methods.show(target, settings);
			});
		},

		show:function(target, settings){
			var panel = methods.buildElements();
			var contentPanel = panel.find('.content');

			// reset style
			panel.removeClass('flashMessagePanelTopMost');
			panel.removeClass('flashMessagePanelBottom');
			if (settings.position == 'topmost'){
				// show on the top side of page
				panel.addClass('flashMessagePanelTopMost');
			}else if (settings.position == 'bottom') {
				panel.addClass('flashMessagePanelBottom');
			}

			// reset content
			if (settings.messageType == 'info'){
				contentPanel.removeClass('warn');
				contentPanel.addClass('info');
			}else{
				contentPanel.removeClass('info');
				contentPanel.addClass('warn');
			}

			var textSpan = contentPanel.find('span.text');
			textSpan.text($.message(settings.content));

			var actionSpan = contentPanel.find('span.action');

			// remove old actions
			actionSpan.find('a').remove();

			if (settings.actions != null){
				for(var idx in settings.actions){
					var actionObj = settings.actions[idx];
					var title = $.message(actionObj.title);
					var action = $('<a href="#"></a>');
					action.text(title);
					action.data('id', actionObj.id);
					action.data('params', actionObj.params);

					// add click event handler
					action.click(function(event){
						event.preventDefault();
						var target = $(this);
						methods.onActionsClick(target);
					});

					actionSpan.append(action);
				}
			}

//			// calculate width.
//			// set max width first, let all content load.
//			panel.width(maxWidth);
//
			// load the layout to calculate the width and height, otherwise the values will be zero.
			panel.css('visibility', 'hidden');
			panel.css('display', 'block');
//
//			var width = textSpan.width() + 12 + actionSpan.width() + 26 * 2 + 10;
//
//			// the min and the max width
//			if (width < minWidth){
//				width = minWidth;
//			}else if (width > maxWidth){
//				width = maxWidth;
//			}
//
//			panel.width(width);
//			panel.css('margin-left', -width/2);

			var width = panel.width();
			var height = panel.height();

			panel.css('display', 'none');
			panel.css('visibility', 'visible');

			// set position
			if (settings.position == 'topmost'){
				// show on the top side of page
				panel.css('margin-left', -width/2);
				panel.css('left','50%');
				panel.css('top',5);
			}else{
				panel.css('margin-left', 0);

				var offset = target.offset();
				if (settings.position == 'bottom') {
					// bottom to the target
					panel.css('left', offset.left + target.outerWidth() - width); // right align.
					panel.css('top', offset.top + target.outerHeight() + 16 + settings.offset);
				}else{
					// default is right to the target
					panel.css('left', offset.left + target.outerWidth() + 22 + settings.offset);
					panel.css('top', offset.top + (target.outerHeight() - height) /2);
				}
			}

			window.clearTimeout(hideTimer);

			panel.fadeIn('fast');
			//isShowing = true;

			// set auto hide
			if (settings.hide != 'off') {
				var delay = delayNormal;
				if (settings.hide == 'fast'){
					delay = delayFast;
				}else if(settings.hide == 'slow'){
					delay = delaySlow;
				}

				hideTimer = window.setTimeout(
					function(){methods.hide();},
					delay);
			}
		},

		hide:function(){
			window.clearTimeout(hideTimer);

			//if (isShowing){
			var panel = methods.buildElements();
			if (panel.is(':visible')){
				panel.fadeOut('fast');
			}
			//	isShowing = false;
			//}
		},

		/**
		 * Add notify-action click handler register.
		 * The func param should be: function(params){...}.
		 */
		addActionListener:function(id, func){
			if (typeof func != 'function'){
				$.error('The func option must be a function.');
			}

			actions[id] = func;
			return methods;
		},

		onActionsClick:function(target){
			methods.hide();

			var id = target.data('id');
			var params = target.data('params');
			var func = actions[id];
			if(func != null){
				func(params);
			}
		},

		/**
		 * A short-cut to show the common server error message.
		 *
		 * $('#submitButton').flashMessage('SERVER_ERROR');
		 */
		SERVER_ERROR:function(position){
			var options = {
				content:'common.serverError',
				position: position
			}

			return this.each(function(){
				var target = $(this);
				var settings = $.extend({}, defaults, options);
				methods.show(target, settings);
			});
		}
	};

	$.fn.flashMessage = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.flashMessage');
		}
	};

	// expose to jQuery
	$.flashMessage = {
		hide:methods.hide,
		addActionListener:methods.addActionListener,

		/**
		 * Show flash message on the top side of page.
		 */
		show:function(options){
			options.position = 'topmost';
			$(window).flashMessage(options);
		},

		/**
		 * Show the common server error message on the top side of page.
		 */
		showServerError:function(){
			$(window).flashMessage({
				content:'common.serverError',
				position:'topmost'
			});
		}
	}

})(jQuery);

/**
 * Add loading icon in the anchor(link) button.
 *
 * $('#button').showLoading();
 * $('#button').showLoading({
 *		withDelay:true
 *		delay:500,
 *		withText:true
 * });
 * $('#button').showLoading('hide');
 *
 */
(function($){
	var PLUGIN_NAME = 'showLoading';

	var delayNormal = 300;

	var defaults = {
		className:'loadingAnimation', // the class name of target (button/link) while showing loading icon.
		delay:delayNormal, // this value will be ignore if 'withDelay'=false.
		withDelay:false,
		withText:false
	};

	var methods = {
		buildElements:function(container){
			var exists = container.find('span.loadingIcon');
			if (exists.length > 0){
				return exists;
			}

			var icon = $('<span class="loadingIcon"></span>');
			container.append(icon);

			return icon;
		},

		init:function(options){
			return this.each(function(){
				var target = $(this);
				if (target.data(PLUGIN_NAME + '.settings') == null){
					var settings = $.extend({}, defaults, options);

					settings.delayTimer = null;

					if (settings.withDelay) {
						settings.delayTimer = window.setTimeout(
							function(){
								methods.show(target, settings);
								}, settings.delay);
					}else{
						methods.show(target, settings);
					}

					// store the settings to the target.
					target.data(PLUGIN_NAME + '.settings', settings);
				}
			});
		},

		show:function(target, settings) {
			target.addClass(settings.className);

			var icon = methods.buildElements(target);
			if (settings.withText) {
				icon.addClass('withText');
				icon.text($.message('common.loading'));
			}else{
				icon.removeClass('withText');
				icon.text('');
			}

			icon.show();
		},

		hideLoading:function(target, settings) {
			var icon = methods.buildElements(target);
			icon.hide();

			if (settings.delayTimer) {
				window.clearTimeout(settings.delayTimer);
				settings.delayTimer = null;
			}

			target.removeClass(settings.className);
		},

		/**
		 * This method shoud be invoked by this way:
		 * $('#button').showLoading('hide');
		 */
		hide:function(){
			var target = this;
			var settings = target.data(PLUGIN_NAME + '.settings');

			if (settings != null){
				methods.hideLoading(target, settings);
				target.removeData(PLUGIN_NAME + '.settings');
			}
		}
	};

	$.fn.showLoading = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.showLoading');
		}
	};
})(jQuery);

/**
 * Once request.
 * To prevent duplicate click/submit.
 *
 * $('#button').onceRequest({
 *   busyClassName:'busying',
 *   onCheck:function(target){ return 'false' to cancel submitting. },
 *   onSubmit:function(target, args){},
 *   onRelease:function(target){}
 * });
 *
 *   args.release() // release the locking.
 *
 */
(function($){
	var PLUGIN_NAME = 'onceRequest';

	var defaults = {
		busyClassName:null, // the css style name to be added to the trigger(link/button)
		onCheck:function(target){}, // return 'false' to cancel submitting.
		onSubmit:function(target, args){},
		onRelease:function(target){}
	};

	var methods = {
		init:function(options){
			return this.each(function(){
				var target = $(this);
				if (target.data(PLUGIN_NAME + '.settings') == null){

					var settings = $.extend({}, defaults, options);

					if (typeof settings.onCheck != 'function'){
						$.error('The onCheck option must be a function.');
					}

					if (typeof settings.onSubmit != 'function'){
						$.error('The onSubmit option must be a function.');
					}

					if (typeof settings.onRelease != 'function'){
						$.error('The onRelease option must be a function.');
					}

					// set default state
					settings.busying = false;

					target.bind('click.' + PLUGIN_NAME, settings, function(event){
						event.preventDefault();
						var targetObj = $(this);
						methods.onClick(targetObj, event.data);
					});

					// store settings and prevent bind twice
					target.data(PLUGIN_NAME + '.settings', settings);
				}
			});
		},

		onClick:function(target, settings){
			if (settings.busying){
				return;
			}

			if (settings.onCheck(target) == false){
				return;
			}

			var args = {
				release:function(){
					methods.release(target, settings);
				}
			};

			settings.busying = true;
			settings.onSubmit(target, args);

			if (settings.busyClassName != null){
				target.addClass(settings.busyClassName);
			}
		},

		release:function(target, settings){
			settings.busying = false;
			settings.onRelease(target);

			if (settings.busyClassName != null){
				target.removeClass(settings.busyClassName);
			}
		}
	};

	$.fn.onceRequest = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.onceRequest');
		}
	};
})(jQuery);

/**
 * Form input hints label.
 * Show a label on the above of the input box and hide when
 * there is content in the box or got focus.
 *
 * $('#txtSearch').inputHints();
 * $('#txtSearch').inputHints({
 *    label:$('#txtSearch').siblings('label:first')
 *    });
 *
 */
(function($){
	var PLUGIN_NAME = 'inputHints';

	var defaults = {
		label:null // the hints label.
	};

	var methods = {
		init:function(options){
			return this.each(function(){
				var target = $(this);
				if (target.data(PLUGIN_NAME + '.settings') == null){

					var settings = $.extend({}, defaults, options);

					if (settings.label == null){
						settings.label = target.siblings('label:first');
					}

					// set default state, the input field maybe fill with content,
					// or some browser (like Firefox) will fill content automaticaly after refresh page.
					if (target.val() != ''){
						settings.label.hide();
					}

					target.bind('focus.' + PLUGIN_NAME, settings, function(event){
						var label = event.data.label;
						//label.css('opacity', '.4');

						event.data.timer = window.setInterval(function(){
							if (target.val() == '') {
								label.show();
							}else {
								label.hide();
							}
						}, 100);
					});

					target.bind('blur.' + PLUGIN_NAME, settings, function(event){
						var label = event.data.label;
						//label.css('opacity', '1.0');

						window.clearInterval(event.data.timer);
					});

					// prevent bind twice
					target.data(PLUGIN_NAME + '.settings', settings);
				}
			});
		}
	};

	$.fn.inputHints = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.inputHints');
		}
	};
})(jQuery);

/**
 * Highlight effect.
 * Add highlight background color and set focus to the specify object.
 *
 * $('#txtPassword').highlight();
 * $('#txtPassword').highlight({
 *     className:'highlight',
 *     delay:1000,
 *     focus:false,
 *     ensureVisible:true
 * });
 */
(function($){
	var PLUGIN_NAME = 'highlight';

	var delayNormal = 3000; // the default delay milliseconds.

	var defaults = {
		className:'highlight', // the highlight class name.
		focus:false,
		ensureVisible:false,
		delay:delayNormal
	};

	var methods = {
		init:function(options){
			return this.each(function(){
				var target = $(this);

				var lastSettings = target.data(PLUGIN_NAME + '.settings');
				if (lastSettings != null){
					window.clearTimeout(lastSettings.timer);
				}

				var settings = $.extend({}, defaults, options);

				if (settings.ensureVisible){
					target.ensureVisible();
				}

				target.addClass(settings.className);

				settings.timer = window.setTimeout(
					function(){
						methods.hideHighlight(target, settings);
					}, settings.delay);

				if (settings.focus){
					target.focus();
				}

				// store settings into target
				target.data(PLUGIN_NAME + '.settings', settings);
			});
		},

		hideHighlight:function(target, settings){
			target.removeClass(settings.className);
			target.removeData(PLUGIN_NAME + '.settings');
		}
	};

	$.fn.highlight = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.highlight');
		}
	};
})(jQuery);


/**
 * Scroll to the specify element for ensuring visible.
 *
 * If the target in the visible area
 *	just keep current position.
 * Else if the target in the window viewport region
 *	scroll page to the top.
 * Else if the target out of the window viewport region
 *	scroll the target (up or down) to visible.
 *
 * $('#txtPassword').ensureVisible();
 */
(function($){
	var methods = {
		init:function(options){
			return this.each(function(){
				var target = $(this);

				var scrollTop = $(document).scrollTop();
				var offsetTop = target.offset().top;
				var windowHeight = $(window).height();
				var top = offsetTop - scrollTop;

				if (top > 0 && top < windowHeight) {
					// do nothing.
				}else if (offsetTop < windowHeight) {
					$(document).scrollTop(0);
				}else {
					target.get(0).scrollIntoView();
				}
			});
		}
	};

	$.fn.ensureVisible = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.ensureVisible');
		}
	};
})(jQuery);

/**
 * Shake effect.
 *
 * $('#box').shake({
 *   times:3,
 *   delay:30,
 *   onComplete:function(){...}
 * });
 *
 */
(function($){
	var defaultSteps = [15,30,15,0,-15,-30,-15,0];
	var delayNormal = 20; // the default delay milliseconds.

	var defaults = {
		times: 3,
		delay:delayNormal,
		onComplete:function(){}
	};

	var methods = {
		init:function(options){
			return this.each(function(){
				var target = $(this);

				var settings = $.extend({}, defaults, options);

				if (typeof settings.onComplete != 'function'){
					$.error('The onComplete option must be a function.');
				}

				var steps = [];
				for (var idx=0; idx < settings.times; idx++){
					steps = steps.concat(defaultSteps);
				}

				target.css('position', 'relative');
				methods.move(target, steps, settings);
			});
		},

		move:function(target, steps, settings){
			var step = steps.shift();
			target.css('left', step);

			if (steps.length > 0){
				window.setTimeout(function(){
					methods.move(target, steps, settings);},
					settings.delay);
			}else{
				settings.onComplete();
			}
		}
	};

	$.fn.shake = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.shake');
		}
	};
})(jQuery);

/**
 * Drop-down menu.
 * Show a drop-down menu when the specify trigger(link) clicked.
 * Only one drop-down menu can be shown at the same time.
 *
 * $('#button').dropDownMenu({
 *     menu:$('#somemenu')
 * });
 *
 * $('#button').dropDownMenu({
 *     menu:$('#somemenu'),
 * });
 *
 * $('#button').dropDownMenu({
 *     menu:$('#somemenu')
 *     menuName:'justAnotherDropDownMenu',
 *     onPosition:function(){
 *       return {left:10, top:40, offset:32}
 *       }
 * });
 *
 */
(function($){
	var PLUGIN_NAME = 'dropDownMenu';

	var lastShowingTarget = null; // the last showing menu's triger object.
	var isRegisterDocumentClick = false; // the flag to indicates binded document click event.

	var defaults = {
		menu:null, // the drop-down menu.

		dropDownClassName:null, // the style of target while menu showing.

		/*
		 * before onPosition() and onShow().
		 */
		onLoad:function(target, menu){},

		/* return position that relate to
		 * trigger, and the arrow direction and offset.
		 * such as {left:0, top:20, offset:32, arrow:'top'}
		 */
		onPosition:function(target, menu){},

		onShow:function(target, menu){},

		onHide:function(target, menu){}
	};

	var defaultPosition = {

		// the direction of the arrow, it can be 'left'/'right'/'top'/'bottom'
		arrow:'top',

		/*
		 *  the arrow position offset, it will be the top or left value, depends on
		 *  the arrow type (left/right or top/bottom).
		 */
		offset:32,

		//default menu position
		left:0,
		top:40
	};

	var methods = {
		registerDocumentClick:function(){
			// only bind document click event once.
			if (isRegisterDocumentClick){
				return;
			}

			isRegisterDocumentClick = true;
			$(document).bind('click.' + PLUGIN_NAME, methods.onDocumentClick);
		},

		init:function(options){

			methods.registerDocumentClick();

			return this.each(function(){
				var target = $(this);
				if (target.data(PLUGIN_NAME + '.settings') == null){

					var settings = $.extend({}, defaults, options);

					if (typeof settings.onLoad != 'function'){
						$.error('The onLoad option must be a function.');
					}

					if (typeof settings.onPosition != 'function'){
						$.error('The onPosition option must be a function.');
					}

					if (typeof settings.onShow != 'function'){
						$.error('The onShow option must be a function.');
					}

					if (typeof settings.onHide != 'function'){
						$.error('The onHide option must be a function.');
					}

					if (settings.menu == null){
						$.error('The menu option must be spcified.');
					}

					// set the default state
					settings.showing = false;

					target.bind('click.' + PLUGIN_NAME, settings, function(event){
						event.preventDefault();

						var targetObj = $(this);
						if (event.data.showing){
							methods.hide(targetObj);
						}else{
							methods.show(targetObj, event.data);
						}
					});

					// store settings on target and prevent bind twice.
					target.data(PLUGIN_NAME + '.settings', settings);
				}
			});
		},

		onDocumentClick:function(event){
			var target = $(event.target);

			if (lastShowingTarget == null){
				return;
			}

			// exclude the target and the menu button
			var settings = target.data(PLUGIN_NAME + '.settings')
			if (settings == null){
				target = target.parent();
				settings = target.data(PLUGIN_NAME + '.settings')
			}

			// exclude the menu
			var lastSettings = lastShowingTarget.data(PLUGIN_NAME + '.settings');
			var lastMenu = lastSettings.menu;

			if (settings == null &&
				!$.jqEquals(lastMenu, target) &&
				!$.jqEquals(lastMenu, target.parent())){
				methods.hide(lastShowingTarget);
			}
		},

		show:function(target, settings){
			// hide the previous menus
			if (lastShowingTarget != null){
				methods.hide(lastShowingTarget);
			}

			var menu = settings.menu;

			// raise onLoad event.
			settings.onLoad(target, menu);

			// set the menu position
			var position = settings.onPosition(target, menu);
			if (position != null){
				var pos = $.extend({}, defaultPosition, position);

				menu.css('left', pos.left);
				menu.css('top', pos.top);

				// set the arrow offset
				var arrow = menu.find('span.arrow');

				if (pos.arrow == 'left'){
					arrow.addClass('arrowLeft');
					arrow.removeClass('arrowRight');
					arrow.removeClass('arrowBottom');
				}else if(pos.arrow == 'right'){
					arrow.addClass('arrowRight');
					arrow.removeClass('arrowLeft');
					arrow.removeClass('arrowBottom');
				}else if(pos.arrow == 'buttom'){
					arrow.addClass('arrowButtom');
					arrow.removeClass('arrowLeft');
					arrow.removeClass('arrowRight');
				}

				if (arrow.hasClass('arrowLeft') || arrow.hasClass('arrowRight')){
					arrow.css('top', pos.offset);
					arrow.css('left', '');
				}else{
					arrow.css('top', '');
					arrow.css('left', pos.offset);
				}
			}

			if (settings.dropDownClassName != null){
				target.addClass(settings.dropDownClassName);
			}

			menu.show();
			settings.showing = true;

			lastShowingTarget = target;

			settings.onShow(target, menu);
		},

		hide:function(target){
			var settings = target.data(PLUGIN_NAME + '.settings');

			if (settings.showing){
				if (settings.dropDownClassName != null){
					target.removeClass(settings.dropDownClassName);
				}

				var menu = settings.menu;
				menu.hide();

				settings.showing = false;
				lastShowingTarget = null;

				settings.onHide(target, menu);
			}
		}
	};

	$.fn.dropDownMenu = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.dropDownMenu');
		}
	};
})(jQuery);


/**
 * Popup box.
 * Many popup boxies showing on a page is allow.
 *
 * $('#boxContainer').popupBox({
 *     left:200,
 *     content: 'hello world'
 * });
 *
 * Hide only the box right on the target
 * $('#boxContainer').popupBox('hide');
 *
 * Hide all popup boxies.
 * $.popupBox.hideAll();
 *
 * $.popupBox.addActionListener('actionId',function(params){});
 *
 */
(function($){
	var defaults = {

		// box width
		width: 220,

		// the direction of the arrow, it can be 'left'/'right'/'top'/'bottom'
		arrow:'left',

		// the text content, HTML will be display as text.
		content:'',

		// the i18n text content's paramters.
		// for example, the content text is 'hello {0}, this is {1}.', and put params:
		// ['John', 'cat'].
		contentParams:null,

		/* actions list, such [{title:'action-title',id:'action-id', params:''},
		 * {title:'Yes',id:'id-yes', params:''}]
		 */
		actions:null,

		/* the arrow position offset, it will be the top or left value, depends on
		 * the arrow type (left/right or top/bottom).
		 */
		offset:6,

		//default box position
		left:0,
		top:0
	};

	// action handlers.
	var actions = {};

	// the targets of all the showing popup box.
	var showingTargets = new $.jqSet();

	var methods = {

		buildElements:function(container){
			var exists = container.find('.popupBox');
			if (exists.length >0){
				return exists;
			}

			var box = $(
				'<div class="arrowBox popupBox hidden">' +
				' <div class="content">' +
				'  <div class="text"></div>' +
				'  <div class="action"></div>' +
				' </div>' +
				' <span class="arrow"></span>' +
				'</div>');

			container.append(box);

			return box;
		},

		init:function(options){
			return this.each(function(){
				var container = $(this);

				var settings = $.extend({}, defaults, options);

				var box = methods.buildElements(container);
				methods.show(container, settings, box);
			});
		},

		show:function(target, settings, box){
			// set width
			box.width(settings.width);

			box.css('left', settings.left);
			box.css('top', settings.top);

			// set the arrow offset
			var arrow = box.find('span.arrow');

			if (settings.arrow == 'left'){
				arrow.addClass('arrowLeft');
				arrow.removeClass('arrowRight');
				arrow.removeClass('arrowBottom');
			}else if(settings.arrow == 'right'){
				arrow.addClass('arrowRight');
				arrow.removeClass('arrowLeft');
				arrow.removeClass('arrowBottom');
			}else if(settings.arrow == 'buttom'){
				arrow.addClass('arrowButtom');
				arrow.removeClass('arrowLeft');
				arrow.removeClass('arrowRight');
			}

			if (settings.arrow == 'left' || settings.arrow == 'right'){
				arrow.css('top', settings.offset);
				arrow.css('left', '');
			}else{
				arrow.css('top', '');
				arrow.css('left', settings.offset);
			}

			var textPanel = box.find('.content .text');
			textPanel.text($.message(settings.content, settings.contentParams));

			var actionPanel = box.find('.content .action');

			// remove old actions.
			actionPanel.find('a').remove();

			if (settings.actions != null){
				for(var idx in settings.actions){
					var actionObject = settings.actions[idx];

					var title = $.message(actionObject.title);
					var action = $('<a href="#"></a>');
					action.text(title);
					action.data('id', actionObject.id);
					action.data('params', actionObject.params);

					// add click event handler
					action.click(function(event){
						event.preventDefault();
						var target = $(this);
						methods.onActionsClick(target);
					});

					actionPanel.append(action);
				}
			}

			box.show();
			showingTargets.add(target);
		},

		/**
		 * This method should be invoked by this way:
		 * $('#boxContainer').popupBox('hide');
		 */
		hide:function(){
			var target = this;
			if (showingTargets.contains(target)){
				var box = methods.buildElements(target);
				box.hide();

				showingTargets.remove(target);
			}
		},

		hideAll:function(){
			for(var idx in showingTargets.getAll()){
				var target = showingTargets.get(idx);
				var box = methods.buildElements(target);
				box.hide();
			}

			showingTargets.clear();
		},

		/**
		 * Add notify-action click handler register.
		 * The func param should be: function(params){...}
		 */
		addActionListener:function(id, func){
			if (typeof func != 'function'){
				$.error('The func option must be a function.');
			}

			actions[id] = func;
			return methods;
		},

		onActionsClick:function(target){
			var id = target.data('id');
			var params = target.data('params');
			var func = actions[id];
			if(func != null){
				func(params);
			}
		}
	};

	// Expose to jQuery
	$.popupBox = {
		hideAll:methods.hideAll,
		addActionListener:methods.addActionListener
	}

	$.fn.popupBox = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.popupBox');
		}
	};
})(jQuery);


/**
 * A mini inline dialog.
 *
 * It's extend from the dropDownMenu plugin.
 *
 * $('.deleteButton').popupDialog({
 * 		onLoad:function(target, dialog){
 * 			return {
 * 				content:'Are you sure delete this message?',
 * 				contentParams:[],
 * 				actions:[
 * 					{title:'Yes, delete it.', listener:function(target){
 * 						//
 * 					}},
 * 					{title:'Cancel', listener:function(target){
 * 						//
 * 					}}
 * 				]
 * 			}
 * 		}
 * 	});
 *
 */
(function($){
	var PLUGIN_NAME = 'popupDialog';

	var defaults = {
		/*
		 * Return the dialog content and the action list:
		 *
		 */
		onLoad:function(target, dialog){}
	};

	var defaultAction = {
		title:null,
		listener:function(target){}
	};

	var methods = {
		buildElements:function(target){
			var exists = target.siblings('.popupDialog');
			if (exists.length >0){
				return exists;
			}

			var dialog = $(
				'<div class="arrowBox popupDialog hidden">' +
				' <div class="content">' +
				'  <div class="text"></div>' +
				'  <div class="action"></div>' +
				' </div>' +
				' <span class="arrow"></span>' +
				'</div>');

			dialog.insertAfter(target);
			return dialog;
		},

		init:function(options){
			return this.each(function(){
				var target = $(this);
				if (target.data(PLUGIN_NAME + '.settings') == null){
					var settings = $.extend({}, defaults, options);

					if (typeof settings.onLoad != 'function'){
						$.error('The onLoad option must be a function.');
					}

					var dialog = methods.buildElements(target);

					target.dropDownMenu({
						menu:dialog,
						onLoad:function(target, menu){
							methods.onLoad(target, menu, settings)
						}
					});

					// store settings on target and prevent bind twice.
					target.data(PLUGIN_NAME + '.settings', settings);
				}
			});
		},

		onLoad:function(target, dialog, settings){
			var returnObject = settings.onLoad(target, dialog);

			var textPanel = dialog.find('.content .text');
			textPanel.text($.message(returnObject.content, returnObject.contentParams));

			var actionPanel = dialog.find('.content .action');
			actionPanel.find('a').remove();

			if (returnObject.actions != null) {
				for(var idx in returnObject.actions) {
					var actionObject = $.extend({}, defaultAction, returnObject.actions[idx]);
					var action = $('<a href="#"></a>');

					var title = $.message(actionObject.title);
					action.text(title);
					action.bind('click.' + PLUGIN_NAME, actionObject, function(event){
						event.preventDefault();
						methods.onActionsClick(target, event.data.listener);
					});

					actionPanel.append(action);
				}
			}
		},

		onActionsClick:function(target, func){
			func(target);
		}
	};


	$.fn.popupDialog = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.popupDialog');
		}
	};
})(jQuery);

/**
 * TODO:: to be improved. #####
 *
 * Checkable form field checker
 *
 * Add checking field:
 * $('#textField').fieldCheck({
 *				name:'fieldName',
 *				statusBox:$('#statuxBoxObject'),
 * 				onChange:function(target, value){
 * 					return "message.code";
 * 				}
 * 			});
 *
 * $('#checkboxField').fieldCheck({
 * 				onChange:function(target, value, initValue, groupValue, isChecked){
 * 					return "message.code";
 * 				}
 * 			});
 *
 * Check all fields:
 * var result = $('#form').fieldCheck('checkFields');
 * if (result) {
 *     //submit
 * }
 *
 * Pre-check after document loaded:
 * $('#form').fieldCheck('preCheck');
 *
 * Force update field message:
 * $('#form').fieldCheck('forceUpdateField', fieldName, status, message, actions);
 *
 */
(function($){

	var PLUGIN_NAME = 'fieldCheck';

	var defaults = {

		/**
		 * 'groupValue': optional, only available on check box and radio box.
		 * 'isChecked': optional, only available on check box.
		 *
		 * This function should return:
		 *	True: indicates check pass.
		 *	String: a message code string indicates check failed and return a message.
		 *	Object: {
		 *				status: 'checking|checked|failed',
		 *				message: 'message code',
		 *				actions: ['action list, see popupBox actions'],
		 *				delayCheck: function(updateCallback){
		 *								// call callback when async checking complete.
		 *								// the result object can be TRUE/String/Object/Function.
		 *								updateCallback(result);
		 *							};
		 *			}.
		 *
		 */
		onChange:function(target, name, value, initValue, groupValue, isChecked){}

	};

	/*
	 * form fields, each item contains:
	 * {
	 *  name: the field name,
	 *  target: input box object,
	 *  type: the input box type,
	 *  initValue: the inital value of the field, this plugin use the
	 *				value of 'data-init-value' attribute of the input box.
	 *  statusBox: the checking status box object,
	 *  status: 'uncheck(default)|checking|pass|failed',
	 *  message: the message code,
	 *  actions: []
	 *  }
	 */
	var fields = [];

	var methods = {
		init:function(options){
			return this.each(function(){
				var target = $(this);
				if (target.data(PLUGIN_NAME + '.settings') == null){

					var settings = $.extend({}, defaults, options);

					if (typeof settings.onChange != 'function'){
						$.error('The onChange option must be a function.');
					}

					settings.target = target;
					settings.status = 'uncheck';
					settings.message = null;
					settings.actions = null;

					if (settings.name == null) {
						settings.name = target.attr('name');
					}

					if (settings.statusBox == null) {
						settings.statusBox = target.parent().find('span.statusBox');
					}

					if (settings.initValue == null) {
						settings.initValue = target.data('init-value');
					}

					if (target.is('input')){
						settings.type = target.attr('type');
					}else if (target.is('select')){
						settings.type = 'select';
					}else if (target.is('textarea')){
						settings.type = 'textarea';
					}

					target.focus(function(){
						methods.updateField(settings);
					});

					target.change(function(){
						methods.checkField(settings);
					});

					// add to collection.
					fields.push(settings);

					// store settings on target and prevent bind twice.
					target.data(PLUGIN_NAME + '.settings', settings);
				}
			});
		},

		checkField:function(settings){

			var target = settings.target;
			var name = settings.name;
			var value = target.val();
			var initValue = settings.initValue;
			var result = null;

			if (settings.type == 'checkbox'){
				result = settings.onChange(target, name, value, initValue,
					methods.getCheckboxGroupValue(name),
					target.is(':checked'));

			}else if (settings.type == 'radio'){
				result = settings.onChange(target, name, value, initValue,
					methods.getRadioGroupValue(name));

			}else{
				result = settings.onChange(target, name, value, initValue);
			}

			methods.updateFieldByResult(settings, result);
		},

		/**
		 * Return empty string if none checked
		 */
		getCheckboxGroupValue:function(name){
			var checkedValues = [];
			for(var idx in fields){
				if (fields[idx].name == name &&
					fields[idx].target.is(':checked')){
					checkedValues.push(fields[idx].target.val());
				}
			}
			return checkedValues.join(',');
		},

		/**
		 * Return empty string if none checked
		 */
		getRadioGroupValue:function(name){
			var groupValue = '';
			for(var idx in fields){
				if (fields[idx].name == name &&
					fields[idx].target.is(':checked')){
					groupValue = fields[idx].target.val();
					break;
				}
			}
			return groupValue;
		},

		updateFieldByResult:function(settings, result){
			var resultType = typeof result;

			if (resultType == 'boolean' && result == true) {
				settings.status = 'pass';
				settings.message = null;
				settings.actions = null;

			}else if (resultType == 'string') {
				settings.status = 'failed';
				settings.message = result;
				settings.actions = null;

			}else if (resultType == 'object') {
				settings.status = result.status;
				settings.message = result.message;
				settings.actions = result.actions;

				if (result.delayCheck != null && typeof result.delayCheck == 'function') {
					var delayUpdate = function(r){
						methods.updateFieldByResult(settings, r);
					}
					result.delayCheck(delayUpdate);
				}
			}else {
				$.error('Illegal result.');
			}

			methods.updateField(settings);
		},

		updateField:function(settings){
			var statusBox = settings.statusBox;
			var container = statusBox.parent();

			if (settings.status == 'checking'){
				statusBox.removeClass('pass');
				statusBox.removeClass('failed');
				statusBox.addClass('checking');

				container.popupBox('hide');

			}else if (settings.status == 'pass'){
				statusBox.removeClass('checking');
				statusBox.removeClass('failed');
				statusBox.addClass('pass');

				container.popupBox('hide');

			}else if (settings.status == 'failed'){
				statusBox.removeClass('checking');
				statusBox.removeClass('pass');
				statusBox.addClass('failed');

				methods.showMessage(settings);
			}else {
				// reset to uncheck
				statusBox.removeClass('checking');
				statusBox.removeClass('failed');
				statusBox.removeClass('pass');

				container.popupBox('hide');
			}
		},

		showMessage:function(settings){
			// hide other popup box
			$.popupBox.hideAll();

			var statusBox = settings.statusBox;
			var container = statusBox.parent();
			var position = statusBox.position();

			container.popupBox({
				left: position.left + 38,
				top: -4,
				content: settings.message,
				actions: settings.actions
			});
		},

		/**
		 * Return true if all field pass, invoked this method
		 * before submit form.
		 */
		checkFields:function(){
			for(var idx in fields){
				var settings = fields[idx];

				// check the uncheck item first.
				if (settings.status == 'uncheck') {
					methods.checkField(settings);
				}

				// scroll to the checking item.
				if (settings.status == 'checking'){
					settings.target.ensureVisible();
					methods.showMessage(settings);
					return false;
				}

				if(settings.status == 'failed'){
					settings.target.highlight({
						focus:true,
						ensureVisible:true});
					return false;
				}
			}

			return true;
		},

		/**
		 * Some fields maybe filled with values after document ready, such as
		 * refresh page after input some content in Firefox, so check
		 * these fields first with this method.
		 * This method should only apply to the form with all field preset blank.
		 */
		preCheck:function(){
			for(var idx=fields.length -1; idx >=0; idx--){
				var settings = fields[idx];
				var value = null;

				if (settings.type == 'checkbox' || settings.type == 'radio'){
					value = settings.target.is(':checked');
				}else{
					value = settings.target.val();
				}

				if (value && value != settings.initValue){
					methods.checkField(settings);
				}
			}
		},

		/**
		 * Specify message by caller.
		 * Commonly be invoked after server return a fail message, such as
		 * some front checking pass but server checking failed.
		 */
		forceUpdateField:function(name, status, message, actions, focus) {
			var settings = methods.findField(name);
			if (settings) {
				settings.status = status;
				settings.message = message;
				settings.actions = actions;

				methods.updateField(settings);

				// set focus to field
				if (focus) {
					settings.target.highlight({
						focus:true,
						ensureVisible:true});
				}
			}
		},

		resetField:function(name){
			var settings = methods.findField(name);
			if (settings) {
				settings.status = 'uncheck';
				settings.message = null;
				settings.actions = null;

				methods.updateField(settings);
			}
		},

		findField:function(name) {
			var field = null;
			for(var idx in fields){
				var settings = fields[idx];
				if (settings.name == name) {
					field = settings;
					break;
				}
			}

			return field;
		}
	};

	$.fn.fieldCheck = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.fieldCheck');
		}
	};
})(jQuery);

/**
 * Toolbar edit/done button.
 *
 * $('#editButton').editDone({
 *
 * });
 *
 */
(function($){
	var PLUGIN_NAME = 'editDone';

	var defaults = {
		doneButton:null,
		onEdit:function(){},
		onDone:function(){}
	};

	var methods = {
		init:function(options){
			return this.each(function(){
				var target = $(this);
				if (target.data(PLUGIN_NAME + '.settings') == null){

					var settings = $.extend({}, defaults, options);

					if (typeof settings.onEdit != 'function'){
						$.error('The onEdit option must be a function.');
					}

					if (typeof settings.onDone != 'function'){
						$.error('The onDone option must be a function.');
					}

					if (settings.doneButton == null){
						settings.doneButton = target.parent().next().find('.done');
					}

					if (settings.doneButton == null){
						$.error('The doneButton option must be spcified.');
					}

					settings.editButton = target;

					target.bind('click.' + PLUGIN_NAME, settings, function(event){
						event.preventDefault();
						$(this).addClass('hidden');

						var settingsObject = event.data;
						settingsObject.doneButton.removeClass('hidden');
						settingsObject.onEdit();
					});

					settings.doneButton.bind('click.' + PLUGIN_NAME, settings, function(event){
						event.preventDefault();
						$(this).addClass('hidden');

						var settingsObject = event.data;
						settingsObject.editButton.removeClass('hidden');
						settingsObject.onDone();
					});

					// prevent bind twice
					target.data(PLUGIN_NAME + '.settings', settings);
				}
			});
		}
	};

	$.fn.editDone = function(method){
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || ! method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.editDone');
		}
	};
})(jQuery);

/**
 * Launcher bar
 */
(function($){
	var PLUGIN_NAME = 'launcher';

	var loader = null; // ajax object

	var methods = {
		init:function(){
			var moreList = $('.mainSidebar .launcher ul.more');

			$('.mainSidebar .launcher .toggle').click(function(event){
				event.preventDefault();
				var target = $(this);
				if (target.hasClass('loading')){
					// cancel loader
					if (loader){
						loader.abort();
						target.removeClass('loading');
					}
				}else{
					// toggle the more-launcher list
					if (moreList.is(':visible')){
						moreList.slideUp('fast');
						var title = target.find('.title');
						title.text($.message('launcher.more'));
					}else{
						methods.showMore(target, moreList);
					}
				}
			});

			$(window).bind('resize.' + PLUGIN_NAME, function(){
				methods.adjustWindowWidth();
			});

			methods.adjustWindowWidth();
		},

		adjustWindowWidth:function(){
			if(!$.pages.options.hideLauncherTitle){
				if ($(document).width() <= 800){
					$('.pageBody').addClass('hideLauncherTitle');
				}else{
					$('.pageBody').removeClass('hideLauncherTitle');
				}
			}
		},

		isMoreLauncherLoaded:function(moreList) {
			return (moreList.hasClass('loaded'));
		},

		showMore:function(target, moreList){
			if (methods.isMoreLauncherLoaded(moreList)){
				moreList.slideDown('fast');
				var title = target.find('.title');
				title.text($.message('launcher.less'));
				return;
			}

			target.addClass('loading');

			// load more app launcher list
			loader = $.ajax({
				type:'POST',
				url:'/home/launcher/more'
			}).done(function(data){
				var afterItem = moreList.find('li.placeholder');

				// add launchers
				for (var idx in data){
					var launcher = data[idx];
					afterItem = methods.add(afterItem, launcher);
				}

				target.find('.title').text('Less');
				moreList.addClass('loaded');
				moreList.slideDown('fast');

			}).fail(function(jqXHR, textStatus, errorThrown){
				if (textStatus != 'abort'){
					$.flashMessage.showServerError();
				}
			}).always(function(){
				target.removeClass('loading');
			});
		},

		/**
		 * Launcher object:
		 *	{href, title, photoLocation, appId}
		 */
		add:function(afterItem, launcher) {
			var itemString = '<li class="app">' +
				'<a class="item" href="' + launcher.href + '" title="' + launcher.title + '">' +
					'<img class="icon" alt="' + launcher.title + '" src="' + launcher.photoLocation + '"/>' +
					'<div class="title">' + launcher.title + '</div>' +
				'</a>' +
				'</li>';
			var item = $(itemString);
			item.data('id', launcher.appId);
			item.insertAfter(afterItem);
			return item;
		},

		/**
		 * Launcher object:
		 *	{href, title, photoLocation, appId}
		 *
		 * listName: 'default','more'
		 * moveAfterAppId: the target position application id or the 'top' string.
		 */
		move:function(launcher, listName, moveAfterAppId){
			if (listName=='more'){
				methods.moveToMoreList(launcher, moveAfterAppId);
			}else{
				methods.moveToDefaultList(launcher, moveAfterAppId);
			}
		},

		moveToMoreList:function(launcher, moveAfterAppId){
			var moreList = $('.mainSidebar .launcher ul.more');
			var item = methods.find(launcher.appId);
			var moveAfterItem = null;
			if (methods.isMoreLauncherLoaded(moreList)){
				if (moveAfterAppId == 'top'){
					moveAfterItem = moreList.find('li.placeholder');
				}else{
					moveAfterItem = methods.find(moveAfterAppId);
				}
				item.insertAfter(moveAfterItem);
			}else{
				if (item){
					item.remove();
				}
			}
		},

		moveToDefaultList:function(launcher, moveAfterAppId){
			var defaultList = $('.mainSidebar .launcher ul:first');
			var item = methods.find(launcher.appId);
			var moveAfterItem = null;
			if (moveAfterAppId == 'top') {
				moveAfterItem = defaultList.find('li.placeholder');
			}else{
				moveAfterItem = methods.find(moveAfterAppId);
			}

			if (item) {
				item.insertAfter(moveAfterItem);
			}else{
				methods.add(moveAfterItem, launcher);
			}
		},

		find:function(appId){
			var selected = null;

			$('.mainSidebar .launcher li.app').each(function(){
				var app = $(this);
				if (app.data('id') == appId){
					selected = app;
					return false; // break each function;
				}
			});

			return selected;
		},

		remove:function(appId) {
			var item = methods.find(appId);
			if (item) {
				item.fadeOut('slow', function(){
					item.remove();
				});
			}
		}
	};

	$.pages.launcher = methods;

})(jQuery);

/**
 * header, footer and frame
 */
(function($){
	var searchBoxTimer = null;

	var methods = {
		init:function(){
			// launcher bar
			if ($.pages.options.authenticated) {
				$.pages.launcher.init();
			}

			// page header - search
			var searchBox = $('.pageHeader .search .box input');
			var searchBoxIcon = searchBox.siblings('.loadingIcon');
			var searchResultBox = $('.pageHeader .search .list');

			var lastText = null;
			var lastSearchText = null;

			searchBox.inputHints();

			searchBox.focus(function(){
				if (searchResultBox.data('result')){
					searchResultBox.show(); // show the last result.
				}

				searchBoxTimer = window.setInterval(function(){
					var text = searchBox.val();
					if(text == lastText){
						if (text != lastSearchText){
							lastSearchText = text;
							methods.onSearchBoxTextChange(text, searchBox, searchBoxIcon, searchResultBox);
						}
					}
					lastText = text;
				}, 500);
			});

			searchBox.blur(function(){
				// indicates searching canceled.
				searchBoxIcon.hide();
				window.clearInterval(searchBoxTimer);

				// cancel searching request.
				// TODO::
			});

			searchBox.keypress(function(event){
				if (event.which == 13){
					$.flashMessage.show({
						content:'common.todo'
					});
				}
			});

			$(document).bind('click.headerSearch', function(event){
				var target = $(event.target);
				if ($.jqEquals(target, searchBox)){
					// ignore
				}else{
					searchBoxIcon.hide();
					window.clearInterval(searchBoxTimer);

					searchResultBox.hide();

					// cancel searching request
					// TODO::
				}
			});

			// page header - profile
			$('.pageHeader .profile .icon').dropDownMenu({
				menu:$('.pageHeader .profile .list')
			});

			// page header - notification
			$('.pageHeader .notify .number').dropDownMenu({
				menu:$('.pageHeader .notify .list')
			});

			// page footer - language select menu
			$('.pageFooter .locale .dropDownButton').dropDownMenu({
				menu:$('.pageFooter .locale .dropDownMenu')
			});

			// TODO:: FOR TESTING STATE
			methods.disableTodoLink();
		},

		onSearchBoxTextChange:function(text, searchBox, searchBoxIcon, resultBox){

			if (text == ''){
				// set the state
				resultBox.data('result', false);

				searchBoxIcon.hide();
				resultBox.hide();
				return false;
			}

			searchBoxIcon.show();

			// start searching
			window.setTimeout(function(){

				// indicates searching canceled.
				if (!searchBox.is(':focus')){
					return;
				}

				searchBoxIcon.hide();
				resultBox.show();

				// set the state
				resultBox.data('result', true);

			}, 1000);
		},

		disableTodoLink:function(){
			$('a.todo').click(function(event){
				event.preventDefault();
				$.flashMessage.show({
					content:'common.todo'
				});
			});
		}
	};

	$.pages.commons = methods;

})(jQuery);
