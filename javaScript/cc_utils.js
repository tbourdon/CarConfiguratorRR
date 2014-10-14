/**
 * @author Timo
 * 
 * cc_utils.js
 * 
 * 		Allgemeine Funktionen für den Car Configurator
 */
function cc_utils(){
		"use strict";
	
};

/**
  *		Wechselt Bildelement aus DOM-Tree mit einem anderen Bild 
  */
 
	cc_utils.bildwechsel = function(Bildnr,Bildobjekt){
    	window.document.images[Bildnr].src = Bildobjekt.src;
  };
   
   

/**
  * 	Modelling-Transformations 
  */
   cc_utils.updateTransformation = function(selectedPerspective, ev){
    	switch(selectedPerspective){
    		case "perspective_front":
    		rotationAngle_X = 20;
    		rotationAngle_Y = 0;
    		break;
    		
    		case "perspective_back":
    		rotationAngle_X = 20;
    		rotationAngle_Y = 180;
    		
    		
    		break;
    		
    		case "perspective_side":
    		rotationAngle_X = 20;
    		rotationAngle_Y = -90;
    		break;
    		
    		case "cockpit":
    		// rotationAngle_X = 20;
    		// rotationAngle_Y = -45;
    		rotationAngle_X = 16;
    		rotationAngle_Y = 180;
    		translation_X = 1.1102230246251565e-16;
	        translation_Y = -1.2;
			translation_Z =	0;
			zoom = -0.5000000000000033;
    		break;
    	}
		
		if(start_rotation == true){
			rotationAngle_Y = rotationAngle_Y + 0.3;
		}
		
		
	    mat4.identity(mvMatrix);
	    //Modell um "zoom" Einheiten auf der z-Achse verschieben:
	    mat4.translate(mvMatrix,[translation_X,translation_Y,zoom]);  
	    //Model rotieren:
	    mat4.rotateX(mvMatrix, rotationAngle_X* Math.PI / 180.0);
	    mat4.rotateY(mvMatrix, rotationAngle_Y* Math.PI / 180.0);
	   
	    
	    
	
	
		/*
		 * 	Model View Transformationen
		 */
		
	    var pUniform = gl.getUniformLocation(shaderProgram, "proj");
	    gl.uniformMatrix4fv(pUniform, false, pMatrix);
 	
	    var mvUniform = gl.getUniformLocation(shaderProgram, "modelView");
	    gl.uniformMatrix4fv(mvUniform, false, mvMatrix);
 		    
	    var normalMatrix = mat4.inverse(mvMatrix);
	    normalMatrix = mat4.transpose(normalMatrix);
 	    
	    var nUniform = gl.getUniformLocation(shaderProgram, "normals");
	    gl.uniformMatrix4fv(nUniform, false, normalMatrix);
    
    };

/**
 * 		Vollbild Modus
 */
(function () {
    var viewFullScreen = document.getElementById("view-fullscreen");
    
    if (viewFullScreen) {

        viewFullScreen.addEventListener("click", function () {
            var docElm = document.documentElement;
            if (docElm.requestFullscreen) {
                docElm.requestFullscreen();
            }
            else if (docElm.mozRequestFullScreen) {
                docElm.mozRequestFullScreen();
            }
            else if (docElm.webkitRequestFullScreen) {
                docElm.webkitRequestFullScreen();
            }
        }, false);
    }

    var cancelFullScreen = document.getElementById("cancel-fullscreen");
   
    if (cancelFullScreen) {
        cancelFullScreen.addEventListener("click", function () {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            }
            else if (document.mozCancelFullScreen) {
                document.mozCancelFullScreen();
            }
            else if (document.webkitCancelFullScreen) {
                document.webkitCancelFullScreen();
            }
        }, false);
    }


    var fullscreenState = document.getElementById("fullscreen-state");
    if (fullscreenState) {
        document.addEventListener("fullscreenchange", function () {
            fullscreenState.innerHTML = (document.fullscreenElement)? "" : "not ";
        }, false);
        
        document.addEventListener("mozfullscreenchange", function () {
            fullscreenState.innerHTML = (document.mozFullScreen)? "" : "not ";
        }, false);
        
        document.addEventListener("webkitfullscreenchange", function () {
            fullscreenState.innerHTML = (document.webkitIsFullScreen)? "" : "not ";
        }, false);
    }
})();






/**
  *		Lackierung der Fahrzeuge 
  */
 
 
 
    cc_utils.updateColor = function(color_selection){
    	//Switch-case für die Farbauswahl
		switch(color_selection){
			
			/**
			 * 		Lackierung: SCHWARZ
			 */
			
			case "black":
			if(object_selection == "audi_r8"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("Lack_uffizi_probe.hdr_uffizi_probe.hdr") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.1, part[i].Kd[1]=0.1, part[i].Kd[2]=0.1);
					  }
				}
			}
			if(object_selection == "aston_martin_db9"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("body") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.1, part[i].Kd[1]=0.1, part[i].Kd[2]=0.1);
					  }
				}
			}
			if(object_selection == "vw_up"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("E_xxxxxx_TRIM_AUSSENFARBE2SG") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.1, part[i].Kd[1]=0.1, part[i].Kd[2]=0.1);
					  } 
				}				
			}	
			break;
			
			/**
			 * 		Lackierung: ROT
			 */
			
			case "red": 
			if(object_selection == "audi_r8"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("Lack_uffizi_probe.hdr_uffizi_probe.hdr") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=1.0, part[i].Kd[1]=0.0, part[i].Kd[2]=0.0);
					  }						
				}				
			}
			if(object_selection == "aston_martin_db9"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("body") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=1.0, part[i].Kd[1]=0.0, part[i].Kd[2]=0.0);
					  }
				}
			}		
			if(object_selection == "vw_up"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("E_xxxxxx_TRIM_AUSSENFARBE2SG") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=1.0, part[i].Kd[1]=0.0, part[i].Kd[2]=0.0);
					  }						
				}				
			}					
			break;
			
			/**
			 * 		Lackierung: ORANGE
			 */
			
			case "orange": 
			if(object_selection == "audi_r8"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("Lack_uffizi_probe.hdr_uffizi_probe.hdr") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=1.0, part[i].Kd[1]=0.44, part[i].Kd[2]=0.0);
					  }						
				}				
			}
		
			if(object_selection == "aston_martin_db9"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("body") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=1.0, part[i].Kd[1]=0.44, part[i].Kd[2]=0.0);
					  }
				}				
			}		
			if(object_selection == "vw_up"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("E_xxxxxx_TRIM_AUSSENFARBE2SG") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=1.0, part[i].Kd[1]=0.44, part[i].Kd[2]=0.0);
					  }						
				}			
			}		
			break;
			
			/**
			 * 		Lackierung: SILBER
			 */
	
			case "silver": 
			if(object_selection == "audi_r8"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("Lack_uffizi_probe.hdr_uffizi_probe.hdr") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.8, part[i].Kd[1]=0.8, part[i].Kd[2]=0.8);
					  }						
				}
			}
			if(object_selection == "aston_martin_db9"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("body") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.8, part[i].Kd[1]=0.8, part[i].Kd[2]=0.8);
					  }
				}	
			}		
			if(object_selection == "vw_up"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("E_xxxxxx_TRIM_AUSSENFARBE2SG") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.8, part[i].Kd[1]=0.8, part[i].Kd[2]=0.8);
					  }					
				}				
			}			
			break;
			
			/**
			 * 		Lackierung: WARMES GRAU
			 */
			
			case "warm_grey":
			if(object_selection == "audi_r8"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("Lack_uffizi_probe.hdr_uffizi_probe.hdr") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.9, part[i].Kd[1]=0.9, part[i].Kd[2]=0.8);
					  }						
				}				
			}
			if(object_selection == "aston_martin_db9"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("body") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.9, part[i].Kd[1]=0.9, part[i].Kd[2]=0.8);
					  }
				}	
			}	
			if(object_selection == "vw_up"){
				for(var i = 0; i<= part.length; i++){
					if((part[i].alias.indexOf("E_xxxxxx_TRIM_AUSSENFARBE2SG") != -1)){
						gl.uniform3f(gl.getUniformLocation(shaderProgram, "uKd"), part[i].Kd[0]=0.9, part[i].Kd[1]=0.9, part[i].Kd[2]=0.8);
					  }						
				}				
			}		
			break;	    	
	    }
	};