package newtime.gfx.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GraphicsManager {

	private ITexture[] textures = new ITexture[1024];
	
	public GraphicsManager() {}
	
	public GraphicsManager(File file) {
		if(!file.exists()) {
			System.err.println("File does not exist: " + file.getAbsolutePath());
			return;
		}
		if(file.isDirectory()) {
			loadDirectory(file);
		}else if(file.isFile()) {
			loadFile(file);
		}
	}
	
	public int loadDirectory(File file) {
		File[] files = file.listFiles();
		int successful = 0;
		for(File f : files) {
			if(loadFile(f)) {
				successful++;
			}
		}
		return successful;
	}
	
	public boolean loadFile(File file) {
		try {
			String name = file.getName().split("\\.")[0];
			BufferedImage image = ImageIO.read(file);
			if(addTexture(new Texture(name, image)) >= 0) {
				return true;
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public int addTexture(ITexture texture) {
		for(int i = 0; i < textures.length; i++) {
			if(textures[i] == null) {
				textures[i] = texture;
				return i;
			}
		}
		return -1;
	}
	
	
}
