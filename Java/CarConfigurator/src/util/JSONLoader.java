package util;
import java.io.BufferedReader;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL15.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class JSONLoader
{
	public static JSONObject loadModelFiles(File f) throws FileNotFoundException, IOException {
		JSONParser parser = new JSONParser();
		Object reader;
		JSONObject m = null;
		try {
			reader = parser.parse(new FileReader(f));
			m = (JSONObject) reader;
			
			String alias = (String) m.get("alias");
			JSONArray vertices = (JSONArray) m.get("vertices");
			JSONArray indices = (JSONArray) m.get("indices");
			//Iterator<String> iterator = vertices.iterator();
			
			
			return m;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m;

		
	}
	
	


}