package newtime.gfx;

import java.awt.Color;
import java.awt.Graphics;

public class Debug {

	public static void main(String[] args) {
		
		TestRenderable renderable1 = new TestRenderable(0, 0, Color.RED);
		TestRenderable renderable2 = new TestRenderable(32, 0, Color.RED);
		TestRenderable renderable3 = new TestRenderable(64, 0, Color.RED);
		TestRenderable renderable4 = new TestRenderable(0, 32, Color.RED);
		TestRenderable renderable5 = new TestRenderable(16, 16, Color.BLUE);
		
		Screen screen = new Screen(800,600);
		
		screen.addRenderable(renderable1, 1);
		screen.addRenderable(renderable2, 1);
		screen.addRenderable(renderable3, 1);
		screen.addRenderable(renderable4, 1);
		screen.addRenderable(renderable5, 0);
		
		while(true) {
			screen.render();
		}
	}
	
}

class TestRenderable implements Renderable {
	
	private int x, y;
	private Color color;
	
	public TestRenderable(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public void render(Graphics g) {
		g.setColor(color);
		g.fillRect(x, y, 32, 32);
	}	
}