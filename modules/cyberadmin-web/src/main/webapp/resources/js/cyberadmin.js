if ((typeof CyberAdmin) == 'undefined' && (typeof jQuery) != 'undefined' ) {
	
	var CyberAdmin = {};
	
	CyberAdmin.Flag = {
		value : false,
		enable : function() {
			this.value = true;
		},
		
		disable : function() {
			this.value = false;
		}
	};
	
	CyberAdmin.Common = {
		hide : function(id) {
			jQuery('table[id=' + id + ']').hide();
		},
		
		show : function(id) {
			jQuery('table[id=' + id + ']').show();
		}
	};
	
	CyberAdmin.Number = {
		//Suport the negative, positive number.
		pressNumber : function(e) {
			var charCode = (e.which) ? e.which : e.keyCode;
			var ctrlDown = e.ctrlKey;
			// Check for ctrl+c, v ,x and a
		    if (ctrlDown && (charCode==67 || charCode==99)){ return true; }// c
		    if (ctrlDown && (charCode==86 || charCode==118)){ return true; }// v
		    if (ctrlDown && (charCode==88 || charCode==120)){ return true; }// x
		    if (ctrlDown && (charCode==65 || charCode==97)){ return true; }//a
		    
			if(charCode == 46 || charCode == 39  || charCode == 37 || charCode == 45) {
				return true;
			}
			if (charCode > 31 && (charCode < 48 || charCode > 57)) {
				return false;
			}
			
			return true;
		},
		
		pressPositiveNumber : function(e) {
			var charCode = (e.which) ? e.which : e.keyCode;
			var ctrlDown = e.ctrlKey;
			// Check for ctrl+c, v ,x and a
		    if (ctrlDown && (charCode==67 || charCode==99)){ return true; }// c
		    if (ctrlDown && (charCode==86 || charCode==118)){ return true; }// v
		    if (ctrlDown && (charCode==88 || charCode==120)){ return true; }// x
		    if (ctrlDown && (charCode==65 || charCode==97)){ return true; }//a
		    
			if(charCode == 46 || charCode == 39  || charCode == 37) {
				return true;
			}
			
			if (charCode > 31 && (charCode < 48 || charCode > 57)) {
				return false;
			}
			
			return true;
		},
		
		pressTelephone : function(e) {
			var charCode = (e.which) ? e.which : e.keyCode;
			var ctrlDown = e.ctrlKey;
			// Check for ctrl+c, v ,x and a
		    if (ctrlDown && (charCode==67 || charCode==99)){ return true; }// c
		    if (ctrlDown && (charCode==86 || charCode==118)){ return true; }// v
		    if (ctrlDown && (charCode==88 || charCode==120)){ return true; }// x
		    if (ctrlDown && (charCode==65 || charCode==97)){ return true; }//a
		    
			if(charCode == 46 || charCode == 39  || charCode == 37 || charCode == 43) {
				return true;
			}
			if (charCode > 31 && (charCode < 48 || charCode > 57)) {
				return false;
			}
			
			return true;
		}
	};
	
	CyberAdmin.Modal = {
		remove : function(id) {
			jQuery('#' + id.replace(':', '\\:')).remove();
		}
	};
	
	CyberAdmin.Export = {		
		intervalId : null,		
		exportCA : function(e) {
			statusDialog.show();
			intervalId = window.setInterval(CyberAdmin.MashShow.checkCookieToHideMashShow, 500);
		}
	};
	
	CyberAdmin.MashShow = {
		checkCookieToHideMashShow : function() {
			var id = "form:screenUUID".replace(':', '\\:');
			var uuid = jQuery('#' + id).val();
			if (CyberAdmin.Cookie.get(uuid)) {
				   statusDialog.hide();
			       CyberAdmin.Cookie.remove(uuid);
			       window.clearInterval(this.intervalId);
			}
		}
	};
	
	CyberAdmin.Cookie = {
		get : function(name) {
			var results = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
			if (results) {
			  return unescape(results[2]);
			}
			return null;
		},
		
		remove : function(cookieName) {
			var cookieDate = new Date(); // current date & time
			cookieDate.setTime(cookieDate.getTime() - 1);
			document.cookie = cookieName += "=; expires=" + cookieDate.toGMTString();
		}
	};
}

PrimeFaces.widget.DefaultCommand = PrimeFaces.widget.BaseWidget.extend({
    init: function (b) {
        this.cfg = b;
        this.id = this.cfg.id;
        this.jqId = PrimeFaces.escapeClientId(this.id);
        this.jqTarget = jQuery(PrimeFaces.escapeClientId(this.cfg.target));
        var a = this;
        this.jqTarget.parents("form:first").keypress(function (d) {
            var c = jQuery.ui.keyCode;
            if (d.which == c.ENTER) {
                a.jqTarget.click();
                d.preventDefault()
            }
        });
        jQuery(this.jqId + "_s").remove()
    }
});