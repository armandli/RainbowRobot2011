import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends Canvas implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1672573762381125969L;

	private int turnCounter;
	private BufferStrategy strategy;
	private Robot[] robots;
	private RobotStrategy[] robotStrategies;

	public String getStrategyName(int n)
	{
		try{
		return robotStrategies[n].getName();
		} catch(Exception e) {}
		return "";
	}
	private Blast greenCannon;
	private Blast yellowCannon;
	private Blast redCannon;
	private BufferedImage floor;

	private final int tilex = 40;
	private final int tiley = 40;
	private final int framex = 240;
	private final int framey = 240;
	private final int buffery = 140;
	public static final int DX = 10;
	public static final int DY = 10;

	private final int gridx = framex / tilex;
	private final int gridy = framey / tiley;

	private boolean graphicsEnabled = true;
	private BufferedImage backgroundIm;
	private final int animationStep = 150;
	private final int turnStep = 100;

	private Random rand;
	private boolean gameOver;
	private JFrame frame;

	private double[][][] beliefs;

	/**
	 * Constructor, and initialization
	 * 
	 */
	
	private RobotStrategy getStrategy(String name)
	{
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		try
		{
			return (RobotStrategy) (loader.loadClass(name).newInstance());
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public Game()
	{
		initGame("MyRobotStrategy","MyRobotStrategy",0,0);
	}

	public Game(String strat1, String strat2,int x, int y, boolean pGraphicsEnabled)
	{
		graphicsEnabled = pGraphicsEnabled;
		initGame(strat1, strat2, x, y);
	}

	private void initGame(String strat1, String strat2,int x, int y) 
	{
		gameOver = false;

		turnCounter = 1;

		rand = new Random();

		robotStrategies = new RobotStrategy[2];

		beliefs = new double[2][6][6];

		/* Init the two robots */
		int startX1, startX2, startY1, startY2;
		while(true)
		{
			startX1 = rand.nextInt(6);
			startX2 = rand.nextInt(6);
			startY1 = rand.nextInt(6);
			startY2 = rand.nextInt(6);
			if((Math.abs(startX1-startX2)+Math.abs(startY1-startY2))>=4) break;
		}

		Robot robot1 = new Robot(1, startX1, startY1);
		Robot robot2 = new Robot(1, startX2, startY2);
		robotStrategies[0] = getStrategy(strat1);
		robotStrategies[1] = getStrategy(strat2);
		try{
			robotStrategies[0].initializeBeliefState(startX1, startY1);
			robotStrategies[1].initializeBeliefState(startX2, startY2);
			beliefs[0] = robotStrategies[0].getBeliefState();
			beliefs[1] = robotStrategies[1].getBeliefState();
		}catch(Exception e) {}

		if (graphicsEnabled){
			greenCannon = new Blast("Sprites/green_cannon.PNG");
			yellowCannon = new Blast("Sprites/yellow_cannon.PNG");
			redCannon = new Blast("Sprites/red_cannon.PNG");

			robot1.loadGraphics("Sprites/blue_robot_sheet.png");
			robot1.loadPicture("Sprites/blue_robot_sheet.png");

			robot2.loadGraphics("Sprites/yellow_robot_sheet.png");
			robot2.loadPicture("Sprites/yellow_robot_sheet.png");

			
			frame = new JFrame("Rainbow Robot Arena Deathmatch");

			final JPanel panel = (JPanel) frame.getContentPane();
			panel.setPreferredSize(new Dimension(framex, framey + buffery));
			panel.setLayout(null);

			setBounds(0,0, framex, framey + buffery);
			panel.add(this);

			frame.setLocation((framex+10)*x, (framey+buffery+30)*y);

			frame.pack();
			frame.setResizable(false);
			frame.setVisible(true);

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(final WindowEvent e) {
					System.exit(0);
				}
			});

			/* Double buffer, for reducing flicker */
			createBufferStrategy(2);
			strategy = getBufferStrategy();

			requestFocus();

			try {
				floor = ImageIO.read(new File("Sprites/floortile.PNG"));
			} catch (final IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		robots = new Robot[2];
		robots[0] = robot1;
		robots[1] = robot2;
	}

	/**
	 * Closes the frame
	 * 
	 */
	public void close() {
		if( graphicsEnabled ){
			frame.dispose();
		}
	}

	/**
	 * Draws the background image
	 * 
	 * @return background image
	 */
	public BufferedImage createBackground() {
		final BufferedImage bg = new BufferedImage(framex, framey,
				BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = bg.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, framex, framey + buffery);
		/* tiles the floor */
		for (int h = 0; h < gridx; h++) {
			for (int v = 0; v < gridy; v++) {
				g.drawImage(floor, null, h * 40, v * 40);
			}
		}
		return bg;
	}

	/**
	 * Updates the background
	 * 
	 * @param g
	 * @param bg
	 *            the pre-drawn background
	 */
	public void drawBackground(final Graphics2D g, final BufferedImage bg) {
		g.drawImage(bg, null, 0, 0);
		drawBeliefState(g, beliefs[0], beliefs[1]);
		g.setColor(Color.BLUE);
		for (int r = 0; r < 2; r++) {
			g.drawImage(robots[r].getPortrait(), null, 0, framey + 5 + 60 * r);
			g.drawString(robotStrategies[r].getName()+ ": " + robots[r].getHP() + " ("
					+ robots[r].getX() + "," + robots[r].getY() + ")", 70,
					framey + 20 + 60 * r);

		}
		g.setColor(Color.RED);
		g.drawString("Turn " + turnCounter, 0, framey + buffery);
	}

	/**
	 * Draws the bargraph for the entire grid
	 * 
	 * @param g
	 * @param player1
	 *            belief matrix for player one
	 * @param player2
	 *            belief matrix for player two
	 */
	public void drawBeliefState(Graphics2D g, double[][] player1,
			double[][] player2) {
		for (int x = 0; x < gridx; x++) {
			for (int y = 0; y < gridy; y++) {
				drawSingleBeliefState(g, x, y, player1[x][y], player2[x][y]);
			}
		}
	}

	/**
	 * Draws the bargraph for a single cell
	 * 
	 * @param g
	 * @param x
	 *            grid x
	 * @param y
	 *            grid y
	 * @param player1
	 *            player one's belief that player two is in this cell
	 * @param player2
	 *            player two's belief that player one is in this cell
	 */

	public void drawSingleBeliefState(final Graphics2D g, int x, int y,
			double player1, double player2) {
		/*System.out.println("X " + x + ", Y " + y + " p (" + player1 + ","
				+ player2 + ")");*/
		y++;
		g.setColor(Color.BLUE);
		int h = (int) Math.ceil(40 * player1);
		g.fillRect(tilex * x, tiley * y - h, 20, h);
		g.setColor(Color.YELLOW);
		h = (int) Math.ceil(40 * player2);
		g.fillRect(tilex * x + 20, tiley * y - h, 20, h);
	}

	/**
	 * Stationary animation
	 * 
	 * @param g
	 */
	public void animate(Graphics2D g, boolean[][] sensors) {
		for (int t = 0; t < Robot.ANIM_FRAMES; t++) {
			drawBackground(g, backgroundIm);

			for (int r = 0; r < 2; r++) {
				if (robots[r].isFiring()) {
					if (r == 0) {
						drawCrosshairs(g, robots[r].getFiringX(), robots[r]
								.getFiringY(), Color.BLUE);
					} else {
						drawCrosshairs(g, robots[r].getFiringX(), robots[r]
								.getFiringY(), Color.YELLOW);
					}

				}
				g.drawImage(robots[r].getImage(t), tilex * robots[r].getX(),
						tiley * robots[r].getY(), null);
				if (robots[r].isFiring() && t < (Robot.ANIM_FRAMES - 1)) {

					Blast blaster;
					switch (robots[r].getCannon()) {
					case Order.GREEN_CANNON:
						blaster = greenCannon;
						break;
					case Order.RED_CANNON:
						blaster = redCannon;
						break;
					case Order.YELLOW_CANNON:
						blaster = yellowCannon;
						break;
					default:
						blaster = null;
						break;
					}

					if(blaster != null)
						g.drawImage(blaster.getImage(t), null, tilex
							* robots[r].getFiringX() + 5, tiley
							* robots[r].getFiringY()
							- (Robot.ANIM_FRAMES - t - 2) * 2 * DY - 10);
				}
			}
			drawSensors(g, sensors);

			strategy.show();
			this.update(g);
			sleep(animationStep);
		}
		for (int r = 0; r < 2; r++) {
			robots[r].stand(robots[r].getDirection());
		}
	}

	private static void sleep(int millis) 
	{
		try {
			Thread.sleep(millis);
		} catch (final Exception e) {
		}
	}
	/**
	 * Moving animation
	 * 
	 * @param g
	 */
	public void animateMove(Graphics2D g) {

		for (int t = 0; t < Robot.ANIM_FRAMES; t++) {
			drawBackground(g, backgroundIm);
			for (int r = 0; r < 2; r++) {
				int x = 0, y = 0;

				if (robots[r].isMoving()) {
					switch (robots[r].getDirection()) {
					case Robot.SOUTH:
						x = 0;
						y = DY;
						break;
					case Robot.WEST:
						x = -DX;
						y = 0;
						break;
					case Robot.NORTH:
						x = 0;
						y = -DY;
						break;
					case Robot.EAST:
						x = DX;
						y = 0;
						break;
					}
				}

				g.drawImage(robots[r].getImage(t), tilex * robots[r].getX() + t
						* x, tiley * robots[r].getY() + t * y, null);
			}

			strategy.show();
			this.update(g);
			try {
				Thread.sleep(animationStep);
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * Single frame
	 */
	public void singleStep(Graphics2D g) {
		drawBackground(g, backgroundIm);

		for (int r = 0; r < 2; r++) {
			g.drawImage(robots[r].getImage(0), tilex * robots[r].getX(), tiley
					* robots[r].getY(), null);
		}

		strategy.show();
		this.update(g);
		try {
			Thread.sleep(animationStep);
		} catch (final Exception e) {
		}
	}

	/**
	 * Draws the sensor information
	 * 
	 * @param g
	 * @param sensors
	 */
	public void drawCrosshairs(Graphics2D g, int x, int y, Color c) {
		/* Cross */
		int[] xp = { 5, 5, 20, 10, 30, 20, 35, 35, 20, 30, 10, 20 };
		int[] yp = { 10, 30, 20, 35, 35, 20, 30, 10, 20, 5, 5, 20 };
		for (int i = 0; i < 12; i++) {
			xp[i] += tilex * x;
			yp[i] += tiley * y;
		}
		g.setColor(c);
		g.fillPolygon(xp, yp, 12);
		g.setColor(Color.BLACK);
		g.drawPolygon(xp, yp, 12);
	}

	public void drawSensors(Graphics2D g, boolean[][] sensors) {
		for (int r = 0; r < 2; r++) {
			g.setColor(Color.GREEN);
			g.drawOval(tilex * robots[r].getX(), tiley * robots[r].getY(), 31,
					31);

			if (sensors[r][Robot.SOUTH]) {
				/*System.out.println("SOUTH sensor");*/
				g.setColor(Color.RED);
				g.fillOval(tilex * robots[r].getX() + 13, tiley
						* robots[r].getY() + 29, 5, 5);
				g.setColor(Color.BLACK);
				g.drawOval(tilex * robots[r].getX() + 13, tiley
						* robots[r].getY() + 29, 5, 5);
			}
			if (sensors[r][Robot.WEST]) {
				/*System.out.println("WEST sensor");*/
				g.setColor(Color.RED);
				g.fillOval(tilex * robots[r].getX() - 2, tiley
						* robots[r].getY() + 13, 5, 5);
				g.setColor(Color.BLACK);
				g.drawOval(tilex * robots[r].getX() - 2, tiley
						* robots[r].getY() + 13, 5, 5);
			}
			if (sensors[r][Robot.NORTH]) {
				/*System.out.println("NORTH sensor");*/
				g.setColor(Color.RED);
				g.fillOval(tilex * robots[r].getX() + 13, tiley
						* robots[r].getY() - 2, 5, 5);
				g.setColor(Color.BLACK);
				g.drawOval(tilex * robots[r].getX() + 13, tiley
						* robots[r].getY() - 2, 5, 5);
			}
			if (sensors[r][Robot.EAST]) {
				/*System.out.println("EAST sensor");*/
				g.setColor(Color.RED);
				g.fillOval(tilex * robots[r].getX() + 29, tiley
						* robots[r].getY() + 13, 5, 5);
				g.setColor(Color.BLACK);
				g.drawOval(tilex * robots[r].getX() + 29, tiley
						* robots[r].getY() + 13, 5, 5);
			}
		}
	}

	/**
	 * Generates the return code. 1 means player 1 wins 0 means a tie -1 means
	 * player 2 wins
	 * 
	 * @return
	 */
	public int returnCode() {
		// Only tie of the robots have the same HP
		int r=0;
		if(robots[0].getHP() > robots[1].getHP()) r= 1;
		if(robots[1].getHP() > robots[0].getHP()) r= -1;
/*		if (robots[0].isAlive()) {
			return 1;
		}
		if (robots[1].isAlive()) {
			return -1;
		}*/
		//System.out.println(""+robots[0].getHP()+","+robots[1].getHP()+": "+r);
		return r;
	}

	/**
	 * Noisy update of movement
	 * 
	 * @param r
	 *            Robot r
	 * @param dir
	 */

	/**
	 * The main game loop
	 * 
	 * @return
	 */
	public int gameLoop() {
		Graphics2D g = null;
		if( graphicsEnabled ){
			backgroundIm = createBackground();
		}

		while (true) {
			if( graphicsEnabled ){
				g = (Graphics2D) strategy.getDrawGraphics();
				drawBackground(g, backgroundIm);
	
				singleStep(g);
			}

			/*----FIRING PHASE----*/

			/* Sensor calculations */
			int deltaX = robots[1].getX() - robots[0].getX();
			int deltaY = robots[1].getY() - robots[0].getY();
			double hypo = Math.abs(deltaX) + Math.abs(deltaY);
			boolean[][] sensors = new boolean[2][4]; /* robot, sensor */

			/*System.out.println("dX=" + deltaX + " dY=" + deltaY + " H=" + hypo
					+ " Sum=" + hypo);*/

			Order[] orders = new Order[2];
			boolean validShot[] = new boolean[2];
			for (int r = 0; r < 2; r++) {
				/* calculate sensors */
				int i = -2 * r + 1; /* 1 if r == 0, -1 if r == 1 */
				sensors[r][Robot.SOUTH] = rand.nextDouble() <= Math.max(0, Math
						.min((i) * deltaY / hypo, 1));
				sensors[r][Robot.WEST] = rand.nextDouble() <= Math.max(0, Math
						.min((-i) * deltaX / hypo, 1));
				sensors[r][Robot.NORTH] = rand.nextDouble() <= Math.max(0, Math
						.min((-i) * deltaY / hypo, 1));
				sensors[r][Robot.EAST] = rand.nextDouble() <= Math.max(0, Math
						.min((i) * deltaX / hypo, 1));
				
				/* Get orders */
				orders[r] = robotStrategies[r].integrateEvidenceAndGiveOrder(sensors[r], robots[r]
						.getX(), robots[r].getY());
				
				/* Get updated beliefs */
				try{
					beliefs[r] = robotStrategies[r].getBeliefState();
				} catch(Exception e) {}

				/* Facing for firing */
				if (orders[r].getAction() != Order.DO_NOTHING) {
					int dir = 0;
					if (Math.abs(deltaX) >= Math.abs(deltaY)) {
						if (deltaX > 0) {
							dir = Robot.EAST;
						} else {
							dir = Robot.WEST;
						}
					} else {
						if (deltaY > 0) {
							dir = Robot.SOUTH;
						} else {
							dir = Robot.NORTH;
						}
					}
					validShot[r] = robots[r].fire(orders[r].getAction(), (dir + 2 * r) % 4,
							orders[r].getX(), orders[r].getY());
				} else {
					robots[r].stand(robots[r].getDirection());
				}
			}

			if( graphicsEnabled ){
				animate(g, sensors);
			}

			/*
			 * Hit - No friendly fire
			 */

			for (int r = 0; r < 2; r++) {
				if(!validShot[1-r]) continue;
				Order o = orders[1-r];
				Robot robot = robots[r];
				if (o.getAction() > 0 && o.getX() == robot.getX()
						&& o.getY() == robot.getY()) {
					/* Resolve damage */
					boolean alive = robot.hit(robot.getDirection(),
							robots[1 - r].getCannon());
					/* Check if anyone's dead */
					gameOver = gameOver || !alive;
				}
			}
			
			if( graphicsEnabled ){
				animate(g, sensors);
			}
			
			if (gameOver) {
				return returnCode();
			}

			/* MOVE */
			for (int r = 0; r < 2; r++) {
				int x = robots[r].getX();
				int y = robots[r].getY();
				double roll = rand.nextDouble();
				double pMove = 0.12;
				if(roll < pMove)
				{
					if(x > 0) robots[r].move(Robot.WEST);
					continue;
				}
				if(roll < pMove*2)
				{
					if(x < gridx-1) robots[r].move(Robot.EAST);
					continue;
				}
				if(roll < pMove*3)
				{
					if(y > 0) robots[r].move(Robot.NORTH);
					continue;
				}				
				if(roll < pMove*4)
				{
					if(y < gridy-1) robots[r].move(Robot.SOUTH);
					continue;
				}
				robots[r].stand(robots[r].getDirection());
			}

			if( graphicsEnabled ){
				animateMove(g);
			}
			for (int r = 0; r < 2; r++) {
				if (robots[r].isMoving()) {
					robots[r].moved();
				}
			}
			
			/* Check for collisions */
			if (robots[0].getX() == robots[1].getX()
					&& robots[0].getY() == robots[1].getY()) {
				robots[0].die(robots[0].getDirection());
				robots[1].die(robots[0].getDirection());
				
				if( graphicsEnabled ){
					animate(g, sensors);
					sleep(2000);
				}
				
				gameOver = true;
			}

			if (gameOver) {
//				sleep(finalStep);
				return returnCode();
			}

			/* Next Turn! */
			turnCounter++;
			try {
				if( graphicsEnabled ){
					Thread.sleep(turnStep);
				}
			} catch (final Exception e) {
			}

		}
	}
	
	public void destroy() { frame.dispose(); }
	public void run() { gameLoop(); }

	public RobotStrategy[] getRobotStrategies() {
		return robotStrategies;
	}
}
