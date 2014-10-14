package main;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.IMediaViewer.Mode;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IContainer;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class viewer {
	public static IMediaReader mediaReader;
    public static void main(String[] args) throws FileNotFoundException {

      String streamUrl = "C://Users//Timo//Documents//Aptana Studio 3 Workspace//VideoStreaming//output.webm";

      	IContainer iContainer = IContainer.make();
        if (iContainer.open(streamUrl, IContainer.Type.READ, iContainer.getFormat()) < 0) {
        	   throw new RuntimeException("failed to open");   
        }
        System.out.println(iContainer.getDuration());
        mediaReader = ToolFactory.makeReader(streamUrl);
        mediaReader.addListener(ToolFactory.makeViewer(Mode.VIDEO_ONLY));
        while (mediaReader.readPacket() == null);
        

  
    }
}