package main;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketConnectorStatus;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.api.WebSocketServerListener;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.instance.JWebSocketInstance;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.server.TokenServer;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;





import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import util.Camera;
import util.Model;
import util.Util;
import static opengl.GL.GL_CULL_FACE;
import static opengl.GL.GL_FILL;
import static opengl.GL.GL_FRONT_AND_BACK;
import static opengl.GL.GL_LINE;
import static opengl.GL.glDisable;
import static opengl.GL.glEnable;
import static opengl.GL.glGetUniformLocation;
import static opengl.GL.glPolygonMode;
import static opengl.GL.glUniform3f;
import static opengl.GL.glUniformMatrix4;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import com.mongodb.ServerAddress;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

import init.*;

public class CarConfigurator {
	
	private static int vaoId = 0;
	private static int vboId = 0;
	private static int iboId = 0;
	private static int naoId = 0;
	private static int nboId = 0;
	
	public static ArrayList<Integer> vbo = new ArrayList<Integer>();
	public static ArrayList<Integer> ibo = new ArrayList<Integer>();
	public static ArrayList<Integer> nbo = new ArrayList<Integer>();
	public static ArrayList<Model> part = new ArrayList<Model>();
	
	public static ArrayList<Float> json_vertices = new ArrayList<Float>();
	public static ArrayList<Float> json_indices = new ArrayList<Float>();

	
	// Variablen für Shader-Programm	
	private static int programID;       	
	private static int ATTR_POS;
	private static int ATTR_NORMAL;

	// Variablen fÃ¼r die Kamera
	// control
    private static int viewProjLoc;

    private static int flEyePositionLoc;
    private static long now, millis;
 // current configurations
    private static boolean bContinue = true;
    private static boolean culling = true;
    private static boolean wireframe = true;

    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera();  // UP VEKTOR PRÜFEN!!!!!!!
	
	
	private static float zoom = -4.0f;
	private static Matrix4f mvMatrix = new Matrix4f();
    private static final Matrix4f viewProjMatrix = new Matrix4f();

	
	private static FloatBuffer mvMat_Buffer = BufferUtils.createFloatBuffer(16);
	private static FloatBuffer nMat_Buffer = BufferUtils.createFloatBuffer(16);

	
	//Variablen für die Maus-Steuerung
	static boolean mouse_down = false;
	static boolean mouse_up = false;
	static float rotationAngle_X = 18.0f;
	static float rotationAngle_Y = 0.0f;
	static float translation_X = 0.0f;
	static float translation_Y = 0.0f;
	static float translation_Z = 0.0f;
	
	static String rotationDir;
	
	// Variablen für Fahrzeugauswahl
	static String car_selection;
	static int anz_car_parts = 0;
	
	static boolean leftButtonDown = false;//Mouse.isButtonDown(0); // is left mouse button down.
	
	// Variablen für Streaming
	private static IMediaWriter writer;
	private static final String filePath = "C://Users//Timo//Documents//Aptana Studio 3 Workspace//VideoStreaming//";
	private static final String outputFilename = "output.webm";
	//private static final String outputFilename = "output.mp4";  // mp4 Ausgabeformat für H.264
	
	private static ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private static String base64imageString = new String();
	private static byte[] imageByteArray;
	
	private static byte[] imageInByte;
	private static BufferedImage image;
	
	private static long startTime = System.nanoTime();
	private static long time = 0;
	

	private static WebSocketPacket handlepacket = null;
	private static WebSocketPacket binaryImagePacket = null;
    private static boolean process_opened = false;

	
	


/**
     * 	Laden der Showroom-Szene
     *  @throws ParseException 
     */
	public static void loadScene() throws ParseException{
		
		for(int i = 1; i < 9; i++){
			String filename = "models/showroom/part"+i+".json";
			try {
		    	System.out.println("Part"+i+" wird geladen...");

				loadParts(filename);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    }
	

    /**
	 * Laden der Parts für die Szene
	 * @param filename = Pfad der zuladenden JSON-Datei
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 */
	public static void loadParts(String filename) throws FileNotFoundException, IOException, ParseException{
		JSONParser parser = new JSONParser();
		Object reader;
		JSONObject jsonData = null;
	
		
		Model m = new Model();
		
		
			reader = parser.parse(new FileReader(filename));
			jsonData = (JSONObject) reader;
			m.alias = (String)jsonData.get("alias");
			
			int vertSize = ((JSONArray)jsonData.get("vertices")).size();
			float[] vertices = new float[vertSize];
			for(int i = 0; i < ((JSONArray)jsonData.get("vertices")).size(); i++){
				double d = (Double) (((JSONArray)jsonData.get("vertices")).get(i));
				float f = (float) (d);
				vertices[i] = f;
			}
			m.vertices = vertices;
					
			int indiSize = ((JSONArray)jsonData.get("indices")).size();
			int[] indices = new int[indiSize];
			for(int i = 0; i < ((JSONArray)jsonData.get("indices")).size(); i++){
				long d = (Long) (((JSONArray)jsonData.get("indices")).get(i));
				int f = (int) d;
				indices[i] = f;
			}
			m.indices = indices;
			
			float[] normals = Util.calculateNormals(m.vertices, m.indices);
			m.normals = normals;

		
			m.Ni = (Double)jsonData.get("Ni");
			
			int KaSize = ((JSONArray)jsonData.get("Ka")).size();
			float[] Ka = new float[KaSize];
			for(int i = 0; i < ((JSONArray)jsonData.get("Ka")).size(); i++){
				double d = (Double) (((JSONArray)jsonData.get("Ka")).get(i));
				float f = (float) d;
				Ka[i] = f;
			}
			m.Ka = Ka;
			
			m.d = (Double)jsonData.get("d");
			
			int KdSize = ((JSONArray)jsonData.get("Kd")).size();
			float[] Kd = new float[KdSize];
			for(int i = 0; i < ((JSONArray)jsonData.get("Kd")).size(); i++){
				double d = (Double) (((JSONArray)jsonData.get("Kd")).get(i));
				float f = (float) d;
				Kd[i] = f;
			}
			m.Kd = Kd;
	
			m.illum = (Long)jsonData.get("illum");
			
			int KsSize = ((JSONArray)jsonData.get("Ks")).size();
			float[] Ks = new float[KsSize];
			for(int i = 0; i < ((JSONArray)jsonData.get("Ks")).size(); i++){
				double d = (Double) (((JSONArray)jsonData.get("Ks")).get(i));
				float f = (float) d;
				Ks[i] = f;
			}
			m.Ks = Ks;
			
			m.Ns = (Double)jsonData.get("Ns");
			
			initBuffers(m);
		
		
	}
    
	
	/**
	 * Laden der Parts für den Audi R8
	 * 
	 * Laden erfolgt mit Drücken der A-Taste
	 * @throws ParseException
	 */
	public static void loadCars(String fahrzeug, int anzahl) throws ParseException{
					
			for(int i = 1; i < anzahl+1; i++){
				String filename = "models/"+fahrzeug+"/part"+i+".json";
				try {
			    	System.out.println("Part"+i+" wird geladen...");

					loadParts(filename);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		
	}
	/**
	 * Löschen aller Fahrzeug-Teile
	 * 
	 * Löschen erfolgt mit Drücken der Erease-Taste
	 * @throws ParseException
	 */
	public static void deleteCar(){
		
		int partAnzahl = 0;
		
		do {
			vbo.remove(8);
			ibo.remove(8);
			nbo.remove(8);
			part.remove(8);
			partAnzahl++;
		} while (part.size() > 8);
			
		System.out.println("Fahrzeug mit "+partAnzahl+" Teilen wurde erfolgreich gelöscht!");
	}
	
	
	/**
	 * Initialisierung der Buffer
	 * @param m Polygonkomponente als JSON Objekt mit zugehörigen Attributen
	 */
	public static void initBuffers(Model m){
		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(m.vertices.length);
		verticesBuffer.put(m.vertices);
		verticesBuffer.flip();
	
		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);
		
		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		
		IntBuffer indicesBuffer = BufferUtils.createIntBuffer(m.indices.length);
		indicesBuffer.put(m.indices);
		indicesBuffer.flip();
		
		iboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		
		FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(m.normals.length);
		normalBuffer.put(m.normals);
		normalBuffer.flip();
		
		naoId = glGenVertexArrays();
		glBindVertexArray(naoId);
		nboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, nboId);
		glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);

			
		vbo.add(vboId);
		nbo.add(nboId);
		ibo.add(iboId);
		part.add(m);
	
		
    	System.out.println("Part erfolgreich geladen!");

		
}
	
	
	/**
	 * 	Verwaltung von Nutzereingaben
	 * @throws ParseException 
	 */
	public static void handleInput() throws ParseException {

		 float moveSpeed = 2e-3f*(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 2.0f : 1.0f)*(float)millis;
	        float camSpeed = 5e-3f;
	        
	        while(Keyboard.next()) {
	            if(Keyboard.getEventKeyState()) {
	                switch(Keyboard.getEventKey()) {

	                	case Keyboard.KEY_W: moveDir.z += 1.0f; break;
	                	case Keyboard.KEY_S: moveDir.z -= 1.0f; break;
                    	case Keyboard.KEY_A: moveDir.x += 1.0f; break;
                    	case Keyboard.KEY_D: moveDir.x -= 1.0f; break;
                    	case Keyboard.KEY_SPACE: moveDir.y += 1.0f; break;
                    	case Keyboard.KEY_C: moveDir.y -= 1.0f; break;
	                
	                    case Keyboard.KEY_1: loadCars("aston_martin", 163);
	                }
	            } else {
	                switch(Keyboard.getEventKey()) {
	                	case Keyboard.KEY_W: moveDir.z -= 1.0f; break;
	                    case Keyboard.KEY_S: moveDir.z += 1.0f; break;
	                    case Keyboard.KEY_A: moveDir.x -= 1.0f; break;
	                    case Keyboard.KEY_D: moveDir.x += 1.0f; break;
	                    case Keyboard.KEY_SPACE: moveDir.y -= 1.0f; break;
	                    case Keyboard.KEY_C: moveDir.y += 1.0f; break;
	                    case Keyboard.KEY_F1: cam.changeProjection(); break;
	                   
	                    case Keyboard.KEY_F2: glPolygonMode(GL_FRONT_AND_BACK, (wireframe ^= true) ? GL_FILL : GL_LINE); break;
	                    case Keyboard.KEY_F3: if(culling ^= true) glEnable(GL_CULL_FACE); else glDisable(GL_CULL_FACE); break;
	                }
	            }
	        }
	        cam.move(moveSpeed * moveDir.z, moveSpeed * moveDir.x, moveSpeed * moveDir.y);
	        
	        while(Mouse.next()) {
	            if(Mouse.getEventButton() == 0) {
	                Mouse.setGrabbed(Mouse.getEventButtonState());
	            }
	            if(Mouse.isGrabbed()) {
	                cam.rotate(-camSpeed*Mouse.getEventDX(), -camSpeed*Mouse.getEventDY());
	            	
	           	    }
	        }
	        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) bContinue = false;
	        
	        Matrix4f.mul(cam.getProjection(), cam.getView(), viewProjMatrix); 
		
        if(handlepacket != null){
        	System.out.println(handlepacket.getString());
   	
        	if(handlepacket.getString().contentEquals("mouse_left_down")){
        		mouse_down = true;
        		System.out.println("mouse left pressed!!!");
        	}
        	if(handlepacket.getString().contentEquals("mouse_left_up")){
        		mouse_down = false;
        		System.out.println("mouse left released!!!");
        	}
        	
        	
        	//Mousewheel Zoom +/-
        	if(handlepacket.getString().contentEquals("mousewheel_inc")){
        		translation_Z+=0.3f;
        	}
        	
        	if(handlepacket.getString().contentEquals("mousewheel_dec")){
        		translation_Z-=0.3f;
        		if(translation_Z <= -9.264){
        			translation_Z = -9.264f;
            	}
        		
        	}
        	
        	if(handlepacket.getString().contentEquals("rotX++")){
        		rotationAngle_X+= 2.0f;
        		
        	}
        	if(handlepacket.getString().contentEquals("rotX--")){
        		rotationAngle_X-= 2.0f;
        	}
        	if(handlepacket.getString().contentEquals("rotY++")){
        		rotationAngle_Y+= 2.0f;
        	}
        	if(handlepacket.getString().contentEquals("rotY--")){
        		rotationAngle_Y-= 2.0f;
        	}
        


        		
        	handlepacket = null;
        }
    }
	
	/**
     * 	Update der Modelling- und Projection Matrix 
     */
    public static void updateTransformation(){
       	
   	    Matrix4f.setIdentity(mvMatrix);
   	    //Modell um "zoom" Einheiten auf der z-Achse verschieben:
   	    mvMatrix.translate(new Vector3f(translation_X, translation_Y, translation_Z), mvMatrix);  
   	    //Model rotieren:
   	    mvMatrix.rotate((float)(rotationAngle_X* Math.PI / 180.0), new Vector3f(1.0f, 0.0f, 0.0f));
   	    mvMatrix.rotate((float)(rotationAngle_Y* Math.PI / 180.0), new Vector3f(0.0f, 1.0f, 0.0f));
   	    
   	   
   		/*
   		 * 	Model View Transformationen
   		 */
   	  flEyePositionLoc = glGetUniformLocation(programID, "eyePosition");
      
      viewProjLoc = glGetUniformLocation(programID, "viewProj");
      matrix2uniform(viewProjMatrix, viewProjLoc);
   		
   	    int mvUniform = glGetUniformLocation(programID, "modelView");
   	    mvMatrix.store(mvMat_Buffer);
   	    mvMat_Buffer.position(0);
   	    glUniformMatrix4(mvUniform, false, mvMat_Buffer);
   	   
    		    
   	    Matrix4f normalMatrix = (Matrix4f) mvMatrix.invert();
   	    normalMatrix.transpose(normalMatrix);
   	        	    
   	    int nUniform = glGetUniformLocation(programID, "normals");
   	    normalMatrix.store(nMat_Buffer);
   	    nMat_Buffer.position(0);
   	    glUniformMatrix4(nUniform, false, nMat_Buffer);
       
       }
    
    /**
     * 	Rendering
     * @throws ParseException 
     */
	public static void drawScene() throws ParseException {
    	    	
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    	
        //glUniform3f(flEyePositionLoc, cam.getCamPos().x, cam.getCamPos().y, cam.getCamPos().z);           

    	
    	handleInput();
    	//rotationAngle_Y += 0.45f;
    	updateTransformation();
        
    	/*
    	 *	Beleuchtung - Phong Shading
    	 *
    	 *  Uniform-Variablen
    	 * 
    	 *	uLightPosition  = Position der Lichtquelle
    	 *	uLa 			= Ambienter Anteil des Lichtes
    	 *	uLd	  			= Diffuser Anteil des Lichtes
    	 *  uLs   			= Spekularer Anteil des Lichtes
    	 *  uKa				= ambienter Farbanteil
    	 *  uKd				= diffuser Farbanteil
    	 *  uKs				= spekularer Farbanteil
    	 *
    	 */
    		for(int i = 0; i < part.size(); i++){
    			
    			
    			glUniform3f(glGetUniformLocation(programID, "uKa"), part.get(i).Ka[0], part.get(i).Ka[1], part.get(i).Ka[2]);
    			glUniform3f(glGetUniformLocation(programID, "uKd"), part.get(i).Kd[0], part.get(i).Kd[1], part.get(i).Kd[2]);
    			glUniform3f(glGetUniformLocation(programID, "uKs"), part.get(i).Ks[0], part.get(i).Ks[1], part.get(i).Ks[2]);
    			glUniform1f(glGetUniformLocation(programID, "uNs"), (float)part.get(i).Ns); 			
    			glUniform1f(glGetUniformLocation(programID, "d"), (float)part.get(i).d);
    			
    			if(car_selection == "vw_up"){
    				if(part.get(i).alias.contains("GLAS")){
    					glUniform1f(glGetUniformLocation(programID, "d"), (float)(part.get(i).d = 0.5));
    				}else{
    					glUniform1f(glGetUniformLocation(programID, "d"), (float)(part.get(i).d = 1.0));
    				}
    			}
    			
    			glBindBuffer(GL_ARRAY_BUFFER, vbo.get(i));
    			glVertexAttribPointer(ATTR_POS, 3, GL_FLOAT, false, 0,0);
    	    	glEnableVertexAttribArray(ATTR_POS);
    	    	
    	    	glBindBuffer(GL_ARRAY_BUFFER, nbo.get(i));
    	    	glVertexAttribPointer(ATTR_NORMAL, 3, GL_FLOAT, false, 0,0);
    			glEnableVertexAttribArray(ATTR_NORMAL);
    	
    	    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo.get(i)); 
    		    glDrawElements(GL_TRIANGLES, part.get(i).indices.length, GL_UNSIGNED_INT, 0);
    		
    	    }
	}
//	/**
//	 * Creates image from Framebuffer
//	 * @param imagezahl
//	 * @return
//	 */
//    public static BufferedImage createImage(int imagezahl){
//    	GL11.glReadBuffer(GL11.GL_FRONT);
//    	int width = Display.getDisplayMode().getWidth();
//    	int height= Display.getDisplayMode().getHeight();
//    	int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
//    	ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
//    	System.out.println("BufferGröße: "+buffer.capacity());
//    	GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
//    	
//
//    	image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//    	 
//    	for(int x = 0; x < width; x++){
//	    	for(int y = 0; y < height; y++)
//	    	{
//		    	int i = (x + (width * y)) * bpp;
//		    	int r = buffer.get(i) & 0xFF;
//		    	int g = buffer.get(i + 1) & 0xFF;
//		    	int b = buffer.get(i + 2) & 0xFF;
//		    	image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
//
//	    	}
//    	}
//    	 
//    	return image;
//    }
	
	/**
	 * Creates imageBuffer from Framebuffer
	 * @param imagezahl
	 * @return
	 */
    public static byte[] createImageBufferArray(){
    	GL11.glReadBuffer(GL11.GL_FRONT);
    	int width = Display.getDisplayMode().getWidth();
    	int height= Display.getDisplayMode().getHeight();
    	int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
    	ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * bpp);
    	System.out.println("BufferGröße: "+buffer.capacity());
    	GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
    	byte[] b = new byte[buffer.capacity()];
    	buffer.get(b);
    	return b;
    	
    }
//    /**
//     * Erzeugt einen Base64 codierten Binär-String aus einem übergebenen
//     * BufferedImage.
//     * 
//     * @param buffImage	- Übergebenes BufferedImage
//     * @return 
//     * @throws IOException
//     */
//    private static String createBinaryImage(BufferedImage buffImage) throws IOException {
//		// Aktuelles Bild in ByteArray laden
//    	String imageString = null;
//		buffImage.toString()ImageIO.write(buffImage, "jpg",  baos); // Schreibe das aktuelle BufferedImage in den BinaryArrayOutputStream
//		//System.out.println("Baos-Size : "+baos.size()/1000+" kB");
//		baos.flush();
//		
//		imageString = Base64.encodeBase64String(baos.toByteArray()); // Base64 String der das aktuelle Bild als Binary hat
//		byte[] b = imageString.getBytes("UTF-8");
//		//System.out.println("StringSize: "+b.length/1024+" kB");
//		
//		baos.reset();
//		baos.close();
//		return imageString;
//		
//	}

    /**
     * Encode image to string
     * @param image The image to encode
     * @param type jpeg, bmp, ...
     * @return encoded string
     */
    public static String encodeImgFileToString(String path, String type) {
        String imageString = null;
        File imagefile = new File(path);
        try {
            byte[] imageBytes = FileUtils.readFileToByteArray(imagefile);
            
            Base64.encodeBase64(imageBytes);
            imageString = Base64.encodeBase64String(imageBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
    
    /**
     * Encodes video from the given picture into the stream with the specified index. 
     * @param imagefile
     */
    
    public static void createStream(BufferedImage imagefile){
    	
    	time = System.nanoTime() - startTime;
    	writer.encodeVideo(0, imagefile, time, TimeUnit.NANOSECONDS);
 
    	
    }
        
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
    	
    	TokenServer server = createTokenServer();
    	
    	//Wenn Server gestartet
	    if(server!=null) {
	      // Listen ob Client anfragt
	      server.addListener(new JWebSocketEventListener());
	    }
    	
        //Solange keine Anfrage vom Clienten, höre...
        while(process_opened == false){
        	System.out.println("Listen...");
    	    //System.out.println(1e+2f);
        	

        }
    	System.out.println("Mit Client "+server.getAllConnectors()+" verbunden!");
        
	    init.openGL();
	    System.out.println(1e-2f);
	    programID = init.ShaderProgram();
	    glUseProgram(programID);
	    ATTR_POS = init.getATTR_POS();
		ATTR_NORMAL = init.getATTR_NORMAL();
	    glEnableVertexAttribArray(ATTR_POS); //vs in pos
		glEnableVertexAttribArray(ATTR_NORMAL); //vs in normal
		
      	 
		cam.move(-3.2f, 2.1f, 5.9f);
		
	    init.projection();
		init.lights();
		
		loadScene();
	
		
		//writer = ToolFactory.makeWriter(filePath+outputFilename);
    	//writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_VP8, 720, 500); // webm Format (Theora-Codec)
    	//writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, 720, 500); // MP4 Format (H.264-Codec)
    	
    	// Enkodier ein JPG-File zu einem Binärstring und übergebe an String Variable
    	//base64imageString = encodeImgFileToString("showroom_loading.jpg", "jpg");
		//System.out.println("BinaryImageString: "+base64imageString);
	
		long last = System.currentTimeMillis();
	    long frameTimeDelta = 0;
	    int frames = 0;
	    int imagecount = 0;
	    
	    while (JWebSocketInstance.getStatus() != JWebSocketInstance.SHUTTING_DOWN){
	        try {
	          Thread.sleep(250);//?
	        
			    while (!Display.isCloseRequested()) {
			    	
			    	
					drawScene();
					
					// time handling
			        now = System.currentTimeMillis();
			        millis = now - last;
			        last = now;     
			        frameTimeDelta += millis;
			        ++frames;
			        if(frameTimeDelta > 1000) {
			            System.out.println(1e3f * (float)frames / (float)frameTimeDelta + " FPS");
			            frameTimeDelta -= 1000;
			            frames = 0;
			            
			         			        	
			        }
					
					
					Display.sync(30);
					Display.update();
					
					imageByteArray = createImageBufferArray();
					
					//createBinaryImage(image);
					//base64imageString = createBinaryImage(image);
					//System.out.println("BinaryImageString: "+base64imageString);
					
					
					
			        //writer.encodeVideo(0, image, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
					
					//TODO
					//neues Paket anlegen
					//in das Paket den base64 string paken
					//processPaket(base64StringPaket)
			
					//createStream(image);
					//imagecount++;
					
					
					
					
				}
	    
	        }
	        catch (InterruptedException e) {
	        }
	      }
	    
	    //writer.close();
    }


	
	/**
	 * Klasse die einen JWebSocketListener erzeugt der auf die Events des Webclients
	 * reagiert und durch umsetzen der process_opened Variable den OpenGL Anwendungs
	 * Prozess einleitet. In der Initialisierung des Servers wird eine Instanz dieser
	 * Klasse an den Server gehängt.
	 * 
	 * @author Timo
	 *
	 */
	public static class JWebSocketEventListener implements WebSocketServerListener {
	      public void processOpened(WebSocketServerEvent event) {
	        System.out.println("Client: "+event.getSessionId()+" connected to Server!");
	        process_opened = true;
	        
	      }
	      //Client löst Event aus und schickt packet zum Server, im Server wird dann packet ins handlepacket übergeben und ausgewertet
	      public void processPacket(WebSocketServerEvent event, WebSocketPacket packet) {
	        
	    	handlepacket = packet;
	    	System.out.println("Client sendet: "+packet.getString());
	    	binaryImagePacket = packet;
	    	//binaryImagePacket.setString(base64imageString);
	    	binaryImagePacket.setByteArray(imageByteArray);
	    	System.out.println("Connector am Event: "+event.getConnector());
	        event.getConnector().sendPacket(binaryImagePacket);
	        
	      }
	      public void processClosed(WebSocketServerEvent event) {
		        System.out.println("Client: "+event.getSessionId()+" disconnected!");

	      }
	 }
	/**
	 * 	Initialisierung Tokenserver
	 * @return 
	 */
	public static TokenServer createTokenServer(){
			JWebSocketFactory.printCopyrightToConsole();
		    JWebSocketConfig.initForConsoleApp(new String[]{});
		    JWebSocketFactory.start();
		    TokenServer server = (TokenServer)JWebSocketFactory.getServer("ts0");
		    System.out.println("TokenServer wird erstellt...");
		    System.out.println("TokenServer ID: "+server.getId());
		    System.out.println("TokenServer erstellt!");
		   
		    return server;
		    
		    
		
	}
	/**
     * Hilfsmethode, um eine Matrix in eine Uniform zu schreiben. Das
     * zugehoerige Programmobjekt muss aktiv sein.
     * @param matrix Quellmatrix
     * @param uniform Ziellocation
     */
    private static void matrix2uniform(Matrix4f matrix, int uniform) {
        matrix.store(Util.MAT_BUFFER);
        Util.MAT_BUFFER.position(0);
        glUniformMatrix4(uniform, false, Util.MAT_BUFFER);
    }
}
	    
	    
	
	

