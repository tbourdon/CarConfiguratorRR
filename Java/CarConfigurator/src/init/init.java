package init;

import static opengl.GL.GL_CULL_FACE;
import static opengl.GL.glEnable;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;

import main.CarConfigurator.JWebSocketEventListener;

import org.json.simple.parser.ParseException;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.server.TokenServer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;

import util.Util;


public class init{
	
	//OpenGL
	private static int width = 720;
	private static int height = 500;
	
	
	//ShaderProgram
	private static int shaderProgramID;       	
	private static int ATTR_POS;
	private static int ATTR_NORMAL;
	private static String vs;
	private static String fs;
	
	//Projektion
	private static int viewport_width = 0;
	private static int viewport_height = 0;
	private static float zNah = 0.1f;
	private static float zFern = 450.0f;
	private static float factor = 0.0004f;
	private static Matrix4f pMatrix = new Matrix4f();
	private static FloatBuffer pMat_Buffer = BufferUtils.createFloatBuffer(16);

	
	
		
	
	/**
	 *  Initialisierung OpenGL
	 */
    public static void openGL() {
       	
   		try {
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(4, 0)
				.withProfileCore(true)
				.withForwardCompatible(true);
			
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create(pixelFormat, contextAtrributes);
			
			glViewport(0, 0, width, height);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		
		
		glViewport(0, 0, width, height);
		
		glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
	 	glClearDepth(1.0f);
	 	
	 	glEnable(GL_DEPTH_TEST);
	 	glEnable(GL_CULL_FACE);
	
	 	glEnable(GL_BLEND);
	 	
	 	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glCullFace(GL_BACK);

	}
	
	/**
     * 	Initialisierung - Shader SourceCode
     */
    public static void initShaderCode(){
    	vs =    "#version 100 \n" +
    			"in vec3 vs_in_pos;"+
    			"in vec3 vs_in_normal;"+
    			"in vec4 vertexColor;"+
    			
    			"uniform mat4 modelView;"+
    			"uniform mat4 viewProj;"+
    			"uniform mat4 normals;"+
    			"const int NR_LIGHTS = 4;"+
    			"uniform vec3 uLightPosition[NR_LIGHTS];"+
    			
    			// out-Variable der Farbe für den Fragment-Shader
    			
    			"out vec3 vNormal;"+
    			"out vec3 vLightRay[NR_LIGHTS];"+
    			"out vec3 vEye[NR_LIGHTS];"+
    			
    			"void main(void) {"+
    			
    			     "vec4 positionWC = modelView * vec4(vs_in_pos, 1.0);"+
    			     "vNormal = vec3(normals * vec4(vs_in_normal, 1.0));"+
    			     "vec4 lightPosition = vec4(0.0);"+
    			     
    			     "for(int i = 0; i < NR_LIGHTS; i++){"+
    			     	"lightPosition = modelView * vec4(uLightPosition[i], 1.0);"+
    			     	"vLightRay[i] = positionWC.xyz - lightPosition.xyz;"+
    			     	"vEye[i] = -vec3(positionWC.xyz);"+
    				 "}"+
    	
    			     "gl_Position = viewProj * positionWC;"+//vec4(vs_in_pos, 1.0);"+
    			     
    			"}"   
    			  ;
    			  
    			fs =
    					"#version 100 \n" +
    					"#define NR_LIGHTS 4 \n" +
    					"precision highp float;" +
    					//Light uniforms
    					"uniform vec3 uLightPosition[NR_LIGHTS];"+
    					"uniform vec3  uLa[NR_LIGHTS];"+   //ambient
    					"uniform vec3  uLd[NR_LIGHTS];"+   //diffuse
    					"uniform vec3  uLs[NR_LIGHTS];"+   //specular
    					
    					
    					//Material uniforms
    					"uniform vec3  uKa;"+   //ambient
    					"uniform vec3  uKd;"+   //diffuse
    					"uniform vec3  uKs;"+   //specular
    					"uniform float uNs;"+   //specular coefficient
    					"uniform float d;"+     //Opacity
    					
    					"in vec3 vNormal;"+
    					"in vec3 vLightRay[NR_LIGHTS];"+
    					"in vec3 vEye[NR_LIGHTS];"+
    					
    					"void main(void) {"+
    					  
    					   "vec3 COLOR = vec3(0.0,0.0,0.0);"+
    					   "vec3 N =  normalize(vNormal);"+
    					   "vec3 L =  vec3(0.0,0.0,0.0);"+
    					   "vec3 E =  vec3(0.0,0.0,0.0);"+
    					   "vec3 R =  vec3(0.0,0.0,0.0);"+
    					   "vec3 deltaRay = vec3(0.0);"+
    					   "const int  lsize = 2;"+
    					   "const float step = 0.25;"+
    					   "const float inv_total = 1.0/((float(lsize*lsize) + 1.0)*(float(lsize*lsize) + 1.0));"+  //how many deltaRays
    					  
    					   "for(int i = 0; i < NR_LIGHTS; i++){"+
    					        "E = normalize(vEye[i]);"+
    					        "L = normalize(vLightRay[i]);"+
    					        "R = reflect(L, N);"+
    					        "COLOR += (uLa[i] * uKa);"+
    					        "COLOR += (uLd[i] * uKd * clamp(dot(N,-L),0.0,1.0));"+
    					        "COLOR += (uLs[i] * uKs * pow( max(dot(R, E), 0.0), uNs) * 4.0);"+    
    					   "}"+
    					        
    					   "gl_FragColor =  vec4(COLOR, d);"+
    					   "return;"+
    					   
    					"}";
    }
    
    /**
     * Erzeugt ein ShaderProgram aus einem Vertex- und Fragmentshader.
     * @param vs Pfad zum Vertexshader
     * @param fs Pfad zum Fragmentshader
     * @return ShaderProgram ID
     */
    public static int ShaderProgram() {
    	System.out.println("Shaderprogramm wird generiert...");
        shaderProgramID = glCreateProgram();
        
        int vsID = glCreateShader(GL_VERTEX_SHADER);
        int fsID = glCreateShader(GL_FRAGMENT_SHADER);
        
        glAttachShader(shaderProgramID, vsID);
        glAttachShader(shaderProgramID, fsID);
       
        initShaderCode();
        
        glShaderSource(vsID, vs);
        glShaderSource(fsID, fs);
        
        glCompileShader(vsID);
        glCompileShader(fsID);
                     
        glLinkProgram(shaderProgramID);  
        
        ATTR_POS = glGetAttribLocation(shaderProgramID, "vs_in_pos");
        ATTR_NORMAL = glGetAttribLocation(shaderProgramID, "vs_in_normal");        
        
        glEnableVertexAttribArray(ATTR_POS); //vs in pos
		glEnableVertexAttribArray(ATTR_NORMAL); //vs in normal
        
    	System.out.println("Shaderprogramm erfolgreich generiert!");
    	

        return shaderProgramID;
    }
    
    public static int getATTR_POS(){
    	return ATTR_POS;
    }
    
    public static int getATTR_NORMAL(){
    	return ATTR_NORMAL;
    }

    /**
     * 	Initialisierung - Projektionsmatrix
     */
    public static void projection() {
    	System.out.println("Projektion wird generiert...");

		
		viewport_width = 720;
		viewport_height = 500;
			
		glViewport(0, 0, viewport_width, viewport_height);
			
	
		float rechts = factor * viewport_width/2;
		float links  = factor * (-viewport_width/2);
	    float oben   = factor * viewport_height/2;
	    float unten  = factor * (-viewport_height/2);
		  
	    // Frustum fï¿½r perspektivische Projektion
		Util.frustum(links, rechts, unten, oben, zNah, zFern, pMatrix);
		  	  
		int pUniform = glGetUniformLocation(shaderProgramID, "proj");
   	    pMatrix.store(pMat_Buffer);
   	    pMat_Buffer.position(0);
   	    glUniformMatrix4(pUniform, false, pMat_Buffer);
		
    	System.out.println("Projektion erfolgreich generiert!");

	}
    
    /**
     * 	Initialisierung der Lichter
     */
    public static void lights() {
    	System.out.println("Licht wird generiert...");

    	 // Licht 2 - hinten links
		 float[] light2_pos = {-7.0f, 3.0f, -7.0f};
		 float[] light2_ambient = {0.0f, 0.0f, 0.0f};
		 float[] light2_diffuse = {0.9f, 0.9f, 0.9f};
		 float[] light2_specular = {0.8f, 0.8f, 0.8f};
		
		 // Licht 3 - hinten rechts
		 float[] light3_pos = {7.0f, 3.0f, -7.0f};
		 float[] light3_ambient = {0.0f, 0.0f, 0.0f};
		 float[] light3_diffuse = {0.9f, 0.9f, 0.9f};
		 float[] light3_specular = {0.8f, 0.8f, 0.8f};
		 
		 // Licht 4 - vorne links
		 float[] light4_pos = {-7.0f, 3.0f, 7.0f};
		 float[] light4_ambient = {0.0f, 0.0f, 0.0f};
		 float[] light4_diffuse = {0.9f, 0.9f, 0.9f};
		 float[] light4_specular = {0.8f, 0.8f, 0.8f};
		 		 
		 // Licht 5 - vorne rechts
		 float[] light5_pos = {7.0f, 3.0f, 7.0f};
		 float[] light5_ambient = {0.0f, 0.0f, 0.0f};
		 float[] light5_diffuse = {0.9f, 0.9f, 0.9f};
		 float[] light5_specular = {0.8f, 0.8f, 0.8f};
				
	    glUniform3f(glGetUniformLocation(shaderProgramID, "uLightPosition[0]"), light2_pos[0], light2_pos[1], light2_pos[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLa[0]"), light2_ambient[0], light2_ambient[1], light2_ambient[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLd[0]"), light2_diffuse[0], light2_diffuse[1], light2_diffuse[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLs[0]"), light2_specular[0], light2_specular[1], light2_specular[2]);
		
		
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLightPosition[1]"), light3_pos[0], light3_pos[1], light3_pos[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLa[1]"), light3_ambient[0], light3_ambient[1], light3_ambient[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLd[1]"), light3_diffuse[0], light3_diffuse[1], light3_diffuse[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLs[1]"), light3_specular[0], light3_specular[1], light3_specular[2]);
	
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLightPosition[2]"), light4_pos[0], light4_pos[1], light4_pos[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLa[2]"), light4_ambient[0], light4_ambient[1], light4_ambient[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLd[2]"), light4_diffuse[0], light4_diffuse[1], light4_diffuse[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLs[2]"), light4_specular[0], light4_specular[1], light4_specular[2]);
		
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLightPosition[3]"), light5_pos[0], light5_pos[1], light5_pos[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLa[3]"), light5_ambient[0], light5_ambient[1], light5_ambient[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLd[3]"), light5_diffuse[0], light5_diffuse[1], light5_diffuse[2]);
		glUniform3f(glGetUniformLocation(shaderProgramID, "uLs[3]"), light5_specular[0], light5_specular[1], light5_specular[2]);
		
    	System.out.println("Licht erfolgreich generiert!");

		 
	}

   
}