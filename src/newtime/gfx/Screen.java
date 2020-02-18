package newtime.gfx;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;

public class Screen extends Canvas {
	
	protected final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
	protected ExecutorService threadPool = Executors.newFixedThreadPool(AVAILABLE_PROCESSORS);
	
	protected JFrame frame;
	
	protected Color clearColor = Color.BLACK;
	
	protected Renderable[][] renderables = new Renderable[1024][64];
	
	public Screen(int frameWidth, int frameHeight) {
		this.frame = new JFrame();
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setSize(frameWidth, frameHeight);
		this.frame.setLocationRelativeTo(null);
		this.frame.add(this);
		this.frame.setVisible(true);
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(this.clearColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		for(int z = 0; z < this.renderables.length; z++) {
			if(this.renderables[z] == null) {continue;}
			Future[] futures = new Future[this.AVAILABLE_PROCESSORS];
			for(int x = 0; x < this.AVAILABLE_PROCESSORS; x++) {
				futures[x] = threadPool.submit(new RenderWorker(this.renderables[z], this.AVAILABLE_PROCESSORS, x, g));
			}
			
			boolean completed;
			do {
				completed = true;
				for(int i = 0; i < futures.length; i++) {
					if(!futures[i].isDone()) {completed = false;}
				}
			}while(!completed);
		}
		
		g.dispose();
		bs.show();
	}
		
	public Color getClearColor() {
		return this.clearColor;
	}
	
	public void setClearColor(Color clearColor) {
		this.clearColor = clearColor;
	}
	
	public void setClearColor(int r, int g, int b) {
		this.clearColor = new Color(r,g,b);
	}
	
	public void setClearColor(int r, int g, int b, int a) {
		this.clearColor = new Color(r,g,b,a);
	}
	
	public int getProcessorCount() {
		return this.AVAILABLE_PROCESSORS;
	}
	
	public int addRenderable(Renderable renderable, int zIndex) {
		Renderable[] layer = this.renderables[zIndex];
		for(int i = 0; i < layer.length; i++) {
			if(layer[i] == null) {
				layer[i] = renderable;
				return i;
			}
		}
		return -1;
	}
	
	public Renderable putRenderable(Renderable renderable, int zIndex, int index) {
		Renderable temp = this.renderables[zIndex][index];
		this.renderables[zIndex][index] = renderable;
		return temp;
	}
	
	public void removeRenderable(Renderable renderable, int zIndex, int index) {
		this.renderables[zIndex][index] = null;
	}
	
	public void clearLayer(int zIndex) {
		this.renderables[zIndex] = new Renderable[this.renderables[zIndex].length];
	}
	
	public void clearRenderables() {
		this.renderables = new Renderable[this.renderables[0].length][this.renderables.length];
	}
	
}

class RenderWorker implements Runnable {
	
	private final Renderable[] renderables;
	
	private final int threadCount;
	private final int index;
	
	private final Graphics graphicsContext;
	
	public RenderWorker(Renderable[] renderables, int threadCount, int index, Graphics graphicsContext) {
		this.renderables = renderables;
		
		this.threadCount = threadCount;
		this.index = index;
		
		this.graphicsContext = graphicsContext;
	}
	
	public void run() {
		for(int i = index; i < renderables.length; i+=threadCount) {
			if(i >= renderables.length) {break;}
			if(renderables[i] != null) {
				renderables[i].render(graphicsContext);
			}
		}
	}
	
}
