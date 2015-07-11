/**
 * Latoocarfian chaotic system in OpenGL
 * 
 * @author John Burnett
 */


import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;


public class Latoocarfian implements GLEventListener, 
							   ActionListener, 
							   ChangeListener, 
							   KeyListener {
	
	public static void main(String[] args) { new Latoocarfian(); }
	
	private float initX = 0.1f;
	private float initY  = 0.1f;
	private float initZ = 0f;
	private float A = 2f;
	private float B = 1.526f;
	private float C = 1.455f;
	private float D = 1.139f;
	private int ITER = 1000000;
	private float RADIUS = 0.002f;
	private float viewX = 0f;
	private float viewY = 3f;
	private float viewZ = 0f;
	private float lookatX = 0f;
	private float lookatY = 0f;
	private float lookatZ = 0f;
	private float lastX = 0.1f;
	private float lastY = 0.1f;
	private float lastZ = 0.0f;
	private int TRAIL_LENGTH = 3000;
	private int TRAIL_SPEED = 450;
	private final int INTITIAL_WIDTH = 700;
	private final int INITIAL_HEIGHT = 700;
	private final int FPS = 30;
	private final float BG[] = {0.9f, 0.9f, 0.9f};
	
	private float BAKE[][] = new float[ITER][3];
	
	private FPSAnimator animator;
	private boolean animate = false;
	
	private GLCanvas canvas;
	private GL2 gl;
	private GLU glu;
	JButton startButton, stopButton, quitButton, resetButton;
	private JSlider trailLengthSlider, trailSpeedSlider, aSlider, bSlider, cSlider, dSlider;
	
	
	public  Latoocarfian() {
			GLProfile glp=GLProfile.getDefault();
			GLCapabilities caps = new GLCapabilities(glp);
			canvas = new GLCanvas(caps);
			canvas.addGLEventListener(this);
			canvas.addKeyListener(this);
			JFrame frame = new JFrame("Latoocarfian");

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
			frame.setSize(INTITIAL_WIDTH, INITIAL_HEIGHT);
			frame.setLayout(new BorderLayout());
			JPanel north = new JPanel( new BorderLayout());
			JPanel topRow = new JPanel();
//			JPanel bottomRow = new JPanel( new GridLayout(1, 2) );
//			JPanel bottomWest = new JPanel(new GridLayout(2, 2));
//			JPanel bottomEast = new JPanel(new GridLayout(3, 2));
			JPanel bottomRow = new JPanel( new GridLayout(2, 2) );
			JPanel bottomWest = new JPanel();
			JPanel bottomEast = new JPanel();
			
			startButton = new JButton("Start");
			startButton.addActionListener(this);
			topRow.add(startButton);
			topRow.add(new JLabel( "    "));
			stopButton = new JButton("Stop");
			stopButton.addActionListener(this);
			topRow.add(stopButton);
			topRow.add(new JLabel( "    "));
			resetButton = new JButton("Reset");
			resetButton.addActionListener(this);
			topRow.add(resetButton);
			topRow.add(new JLabel( "    "));
			quitButton = new JButton("Quit");
			quitButton.addActionListener(this);
			topRow.add(quitButton);
			
			trailLengthSlider = new JSlider(0, 5000);
			trailLengthSlider.setValue((int)(TRAIL_LENGTH));
			trailLengthSlider.setMajorTickSpacing(1000);
			trailLengthSlider.setPaintTicks(true);
			trailLengthSlider.setPaintLabels(true);
			trailLengthSlider.addChangeListener(this);
			JLabel lengthLabel = new JLabel("Length");
			bottomWest.add(lengthLabel);
			bottomWest.add(trailLengthSlider);
			
			trailSpeedSlider = new JSlider(0, 1000);
			trailSpeedSlider.setValue((int)(TRAIL_SPEED));
			trailSpeedSlider.setMajorTickSpacing(250);
			trailSpeedSlider.setPaintTicks(true);
			trailSpeedSlider.setPaintLabels(true);
			trailSpeedSlider.addChangeListener(this);
			JLabel speedLabel = new JLabel("Speed");
			bottomWest.add(speedLabel);
			bottomWest.add(trailSpeedSlider);
			
			aSlider = new JSlider(-30, 30);
			aSlider.setValue((int)(A * 10));
			aSlider.setMajorTickSpacing(5);
			aSlider.setPaintTicks(true);
//			aSlider.setPaintLabels(true);
			aSlider.addChangeListener(this);
			JLabel aLabel = new JLabel("A");
			bottomEast.add(aLabel);
			bottomEast.add(aSlider);
			
			bSlider = new JSlider(-30, -30);
			bSlider.setValue((int)(B * 10));
			bSlider.setMajorTickSpacing(5);
			bSlider.setPaintTicks(true);
//			bSlider.setPaintLabels(true);
			bSlider.addChangeListener(this);
			JLabel bLabel = new JLabel("B");
			bottomEast.add(bLabel);
			bottomEast.add(bSlider);
			
			cSlider = new JSlider(10, 30);
			cSlider.setValue((int)(C * 20));
			cSlider.setMajorTickSpacing(5);
			cSlider.setPaintTicks(true);
//			cSlider.setPaintLabels(true);
			cSlider.addChangeListener(this);
			JLabel cLabel = new JLabel("C");
			bottomEast.add(cLabel);
			bottomEast.add(cSlider);
			
			dSlider = new JSlider(10, 30);
			dSlider.setValue((int)(D * 20));
			dSlider.setMajorTickSpacing(5);
			dSlider.setPaintTicks(true);
//			dSlider.setPaintLabels(true);
			dSlider.addChangeListener(this);
			JLabel dLabel = new JLabel("D");
			bottomEast.add(dLabel);
			bottomEast.add(dSlider);
			
			bottomRow.add(bottomWest);
//			bottomRow.add(bottomEast);
			north.add(topRow, BorderLayout.NORTH);
			north.add(bottomRow, BorderLayout.SOUTH);
			frame.add(north, BorderLayout.NORTH);
			JPanel myCanvas = new JPanel(new GridLayout(1,1));
			
			myCanvas.add(canvas);

			frame.add(myCanvas, BorderLayout.CENTER);
			
			frame.setVisible(true);

			animator = new FPSAnimator(canvas, FPS);
//			animator.start(); 		
	}
	
	
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == startButton) {
			animate = true;
			animator.start();
		} else if (event.getSource() == stopButton) {
			animate = false;
			animator.stop();
		} else if (event.getSource() == resetButton) {
			reset();
		} else if (event.getSource() == quitButton) { System.exit(0); }
	}
	
	
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == trailLengthSlider) {
			TRAIL_LENGTH = trailLengthSlider.getValue();
		} else if (e.getSource() == trailSpeedSlider) {
			TRAIL_SPEED = trailSpeedSlider.getValue();
		} else if (e.getSource() == aSlider) {
			A = aSlider.getValue() / 10f;
		} else if (e.getSource() == bSlider ) {
			B = bSlider.getValue() / 10f;
		} else if (e.getSource() == cSlider ) {
			C = cSlider.getValue() / 20f;
		} else if (e.getSource() == dSlider ) {
			D = cSlider.getValue() / 20f;
		}
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		if( e.getKeyChar() == 'w' ) {
			viewZ -= 1;
			lookatZ -= 1;
		} else if( e.getKeyChar() == 's' ) {
			viewZ += 1;
			lookatZ += 1;
		} else if( e.getKeyChar() == 'a' ) {
			viewX -= 1;
			lookatX -= 1;
		} else if( e.getKeyChar() == 'd' ) {
			viewX += 1;
			lookatX += 1;
		} else if( e.getKeyChar() == 'r' ) {
			viewY += 1;
//			lookatY += 1;
		} else if( e.getKeyChar() == 'f') {
			viewY -= 1;
//			lookatY -= 1;
		} else if ( e.getKeyChar() == 'p' )
			System.out.printf("A = %1.3f, B = %1.3f, C = %1.3f, D = %1.3f", A, B, C, D);
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {}


	@Override
	public void keyReleased(KeyEvent e) {}


	public void display(GLAutoDrawable drawable) {
		update();
		render(drawable);
	}

	
	private void update() {
//		viewX += 0.5;
//		viewZ -= 0.1;
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(viewX, viewZ, viewY, lookatX, lookatY, lookatZ, 0f, 1f, 0f);  
	}
	
	
	private float[] latoocarfian(float X, float Y, float Z) {
		float newX = ((float) Math.sin((double) (lastY * B))) + C * ((float) Math.sin((double) (lastX * B)));
		float newY = ((float) Math.sin((double) (lastX * A))) + D * ((float) Math.sin((double) (lastY * A)));
		float P[] = {newX, newY, 0};
		lastX = newX;
		lastY = newY;
		return P;
	}
	
	
	private void reset() {
		initX = 0.1f;
		initY  = 0.1f;
		initZ = 0f;
		A = ((float) Math.random()) * 6f - 3f;
		B = ((float) Math.random()) * 6f - 3f;
		C = ((float) Math.random()) + 0.5f;
		D = ((float) Math.random()) * 2f - 0.5f;
		ITER = 50000;
		RADIUS = 0.005f;
		viewX = 1;
		viewY = 1;
		viewZ = 1;
		lastX = 0.1f;
		lastY = 0.1f;
		lastZ = 0.0f;
		TRAIL_LENGTH = 3000;
		TRAIL_SPEED = 450;
		
		aSlider.setValue((int)(A));
		bSlider.setValue((int)(B));
		cSlider.setValue((int)(C));
		trailLengthSlider.setValue((int)(TRAIL_LENGTH));
		trailSpeedSlider.setValue((int)(TRAIL_SPEED));
		
		animate = false;
		animator.stop();
	}

	
	private void render(GLAutoDrawable drawable) {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glColor3f(0f, 0f, 0f);
		
		float P[] = new float[3];
		int steps;
		if( animate ) {
			steps = TRAIL_LENGTH;
			P[0] = lastX;
			P[1] = lastY;
			P[2] = lastZ;
		} else {
			steps = ITER;
			P[0] = initX;
			P[1] = initY;
			P[2] = initZ;
		}
		
		for( int i = 0; i < steps; i++ ) {
//			float[] hue = {1f, ((float) i)/((float) STEPS), 0f};
//			gl.glColor3fv(hue, 0);
			
			float[] hue = {0.2f, 0.2f, 0.2f + ((float) i)/((float) steps)*0.5f};
			gl.glColor3fv(hue, 0);
			
			P = latoocarfian(P[0],P[1],P[2]);
			
			if( i == TRAIL_SPEED ) {
				lastX = P[0];
				lastY = P[1];
				lastZ = P[2];
			}
			
			gl.glPushMatrix();
				gl.glTranslatef(P[0], P[1], P[2]);
				GLUquadric quad0 = glu.gluNewQuadric();
//				glu.gluQuadricOrientation(quad0, glu.GLU_OUTSIDE);
//				glu.gluQuadricNormals(quad0, glu.GLU_SMOOTH);
				glu.gluSphere(quad0, RADIUS, 3, 3);
				glu.gluDeleteQuadric(quad0);
			gl.glPopMatrix();
		}
	}
	
	
	public void dispose(GLAutoDrawable drawable) { /* put the cleanup code here */ }

	
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		glu = new GLU();
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60f, 1f, 0.5f, 200f); 
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(viewX, viewY, viewZ, 0f, 0f, 0f, 0f, 0f, 1f);
		gl.glClearColor(BG[0], BG[1], BG[2], 1);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	}

	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// this is called when the window is resized
		gl.glViewport(0, 0, width, height);
		float aspect = width*1.0f/height;
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60f, aspect, 0.5f, 200f); 
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		
		
	}
	
}