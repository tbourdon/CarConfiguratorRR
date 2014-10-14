/**
 * 		mouse.js
 * 
 * 		Mouse-Events
 */

	// Variablen für die Maus-Steuerung
	var rotationAngle_X = 0.0;
	var rotationAngle_Y = 0.0;
	var lastpoint = new Point(0,0);
	var mouse_down=false;
	var drawTime = 0;
	
	
	
	
	

	/*
	 * 		Mouse Events
	 */
	function Point(x, y) {
    	this.x = x;
    	this.y = y;
  	}
	function cc_mouse(){
		"use strict";
		
		//this.ereignis = ereignis;
		//this.wheelEreignis = wheelEreignis;
	};
	
	cc_mouse.mouseDown = function(ereignis)   {
		this.ereignis = ereignis;
		mouse_down = true;
		console.info("mouse_left_down");
		sendMessage("mouse_left_down");
					
	 };
	 
	cc_mouse.mouseUp = function(ereignis)   {
		this.ereignis = ereignis;
		mouse_down = false;
		console.info("mouse_left_up");
		sendMessage("mouse_left_up");
		
	 };
	  
	cc_mouse.mouseWheel = function(wheelEreignis)  {
		 this.wheelEreignis = wheelEreignis;
		
	     if((this.wheelEreignis).detail>0){
	     	
	     	interactionData[1]=0;
	       	console.info("Mauswheel ++");
	       	sendMessage(interactionData);//"mousewheel_inc");
	       	
	       	console.info("InteractionData: "+interactionData);
	     }  
		 else{
		   	
		   	interactionData[1]=1;
		   	console.info("Mauswheel --");
		   	sendMessage(interactionData);
		   	
		    console.info("InteractionData: "+interactionData);

		   	
		 }
		
	
	};
	
	cc_mouse.keyDown = function(ereignis){
		this.ereignis = ereignis;
		console.info("tasten ereignis passiert");
		if(ereignis.keyCode == 105){
			//Numpad Pfeil oben = zoom ++
			sendMessage("mousewheel_inc");
			console.info("zoom in");
		}
		if(ereignis.keyCode == 99){
			//Numpad Pfeil unten = zoom --
			sendMessage("mousewheel_dec");
			console.info("zoom out");
		}
		if(ereignis.keyCode == 104){
			//Numpad Pfeil oben = x- Drehung nach oben
			sendMessage("rotX++");
		}
		if(ereignis.keyCode == 98){
			//Numpad Pfeil runter = x-Drehung nach unten
			sendMessage("rotX--");
		}
		if(ereignis.keyCode == 102){
			//Numpad Pfeil links = y- Drehung nach links
			console.info("pfeil links");
			sendMessage("rotY++");
		}
		if(ereignis.keyCode == 100){
			//Numpad Pfeil rechts = y-Drehung nach rechts
			sendMessage("rotY--");
		}
		
	};
	
	cc_mouse.mouseMove = function(ereignis){
		this.ereignis = ereignis;

	    if(mouse_down == true)    {
	    	
				rotationAngle_Y+=((this.ereignis).clientX-lastpoint.x);
		  		sendMessage("rotY"+ rotationAngle_Y);
		  		console.info("Drehung um Y-Achse: "+rotationAngle_Y);
		}  		
		if(mouse_down == true){		
			
				rotationAngle_X+=((this.ereignis).clientY-lastpoint.y);
	      		sendMessage("rotX"+ rotationAngle_X);
		  		console.info("Drehung um X-Achse: "+rotationAngle_X);
			
		  					
		  //Bedingung für Sichtwinkel-Beschränkung	  
		  //console.info("Koordianten x,y: "+((this.ereignis).clientX)+","+((this.ereignis).clientY));
		  if(rotationAngle_X>=90) rotationAngle_X=90;
		  if(rotationAngle_X<=0) rotationAngle_X=0;
	         
	    }  
	    lastpoint.x=(this.ereignis).clientX;
	    lastpoint.y=(this.ereignis).clientY;

	     
	  
	};
	
	
	