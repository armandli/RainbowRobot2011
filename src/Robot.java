import java.awt.image.BufferedImage;
//import java.io.File;
import java.io.IOException;
//import javax.imageio.ImageIO;

public class Robot {

	private BufferedImage portrait;
	private int redBattery;
	private int yellowBattery;

	public static int DEFAULT_HP = 10;
	public static final int SOUTH = 0;
	public static final int WEST = 1;
	public static final int NORTH = 2;
	public static final int EAST = 3;
	public static final int STATIONARY = 4;

	public static final int DEAD = 0;
	public static final int STANDING = 1;
	public static final int MOVING = 2;
	public static final int FIRING = 3;
	public static final int HURT = 4;
	public static final int ANIM_FRAMES = 5;

	private int hp;
	private int direction; /* 0 S, 1 W, 2 N, 3 E */
	private int status;
	private int x, y;
	private int fx, fy;
	private int cannon;

	private BufferedImage[][][] animation;

	public Robot(final int d, final int xi, final int yi) {
		x = xi;
		y = yi;
		direction = d;
		status = STANDING;
		hp = DEFAULT_HP;
		redBattery = 1;
		yellowBattery = 2;
	}

	/**
	 * returns the robot's portrait
	 * 
	 * @return
	 */
	public BufferedImage getPortrait() {
		return portrait;
	}

	/**
	 * Loads your robot's portrait. Please include this.
	 * 
	 * @param filename
	 */
	public void loadPicture(String filename) {
		try {
			RobotReader r = new RobotReader(filename);
			portrait = r.readRobot(0,0,48,48);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all the animations from a standardized file
	 * 
	 * @param filename
	 */
	public void loadGraphics(String filename) {
		animation = new BufferedImage[5][4][ANIM_FRAMES];
		final RobotReader r = new RobotReader(filename);
		try {

			for (int d = 0; d < 4; d++) {
				for (int a = 0; a < ANIM_FRAMES; a++) {
					animation[STANDING][d][a] = r.readRobot(3 + d, 0);
					animation[FIRING][d][a] = r.readRobot(7 + d, 6 + a);
					animation[MOVING][d][a] = r.readRobot(7 + d, 17 + a);
				}
				animation[DEAD][d][0] = r.readRobot(7 + d, 4);
				animation[HURT][d][0] = r.readRobot(7 + d, 4);
				for (int a = 1; a < ANIM_FRAMES; a++) {
					animation[DEAD][d][a] = r.readRobot(
							(int) Math.floor(d / 2), 10);
					animation[HURT][d][a] = r.readRobot(3 + d, 0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Ending Load...");
	}

	/**
	 * Get the robot's x-coord
	 * 
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the robot's y-coord
	 * 
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the x-coord targeted by the robot's cannon
	 * 
	 * @return
	 */
	public int getFiringX() {
		return fx;
	}

	/**
	 * Get the x-coord targeted by the robot's cannon
	 * 
	 * @return
	 */
	public int getFiringY() {
		return fy;
	}

	/**
	 * Get which way the robot is facing
	 * 
	 * @return
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Get the robot's hitpoints
	 * 
	 * @return
	 */
	public int getHP() {
		return hp;
	}

	/**
	 * Set the stand status
	 * 
	 * @param dir
	 */
	public void stand(int dir) {
		if (status != DEAD) {
			direction = dir;
			status = STANDING;
		}
	}

	/**
	 * Set the move status
	 * 
	 * @param dir
	 */
	public void move(int dir) {
		if (status != DEAD) {
			direction = dir;
			status = MOVING;
		}
	}

	/**
	 * Post-move clean-up
	 * 
	 */
	public void moved() {
		if (status != DEAD) {
			switch (direction) {
			case Robot.SOUTH:
				y++;
				break;
			case Robot.WEST:
				x--;
				break;
			case Robot.NORTH:
				y--;
				break;
			case Robot.EAST:
				x++;
				break;
			}
			status = STANDING;
		}

	}

	/**
	 * True if the robot still has HP
	 * 
	 * @return
	 */
	public boolean isAlive() {
		return !(status == DEAD);
	}

	/**
	 * True if the robot is, or is about to move
	 * 
	 * @return
	 */
	public boolean isMoving() {
		return status == MOVING;
	}

	/**
	 * True if the robot is, or is about to fire
	 * 
	 * @return
	 */
	public boolean isFiring() {
		return status == FIRING;
	}

	/**
	 * Fires an orbital cannon to a specific cell
	 * 
	 * @param can
	 *            the cannon (green, yellow, red)
	 * @param dir
	 *            Direction
	 * @param cordx
	 *            targeted x coord
	 * @param cordy
	 *            targeted y coord
	 * @return
	 */
	public boolean fire(int can, int dir, int cordx, int cordy) {
		if (status == DEAD) {
			return false;
		}
		if (can == Order.RED_CANNON) {
			if (redBattery <= 0) {
				status = STANDING;
				return false;
			} else {
				redBattery--;
			}
		}
		if (can == Order.YELLOW_CANNON) {
			if (yellowBattery <= 0) {
				status = STANDING;
				return false;
			} else {
				yellowBattery--;
			}
		}
		direction = dir;
		cannon = can;
		fx = cordx;
		fy = cordy;
		status = FIRING;
		return true;
	}

	/**
	 * Kills the robot
	 * 
	 * @param dir
	 */
	public void die(int dir) {
		hp = 0;
		direction = dir;
		status = DEAD;
	}

	/**
	 * Sees which cannon the robot is firing
	 * 
	 * @return
	 */
	public int getCannon() {
		return cannon;
	}

	/**
	 * Hits the robot with a cannon. Does damage
	 * 
	 * @param dir
	 * @param can
	 *            the cannon (green, yellow, red)
	 * @return
	 */
	public boolean hit(int dir, int can) {

		direction = dir;
		status = HURT;
		switch (can) {
		case Order.RED_CANNON:
			hp -= 5;
			break;
		case Order.YELLOW_CANNON:
			hp -= 3;
			break;
		case Order.GREEN_CANNON:
			hp--;
			break;
		}
		if (hp <= 0) {
			hp = 0; /* Dead is dead, don't reward for overkill */
			status = DEAD;
			return false;
		}
		return true;
	}

	/**
	 * Gets the image appropriate for the status, direction and the time-step
	 * 
	 * @param i
	 *            the time step
	 * @return
	 */
	public BufferedImage getImage(int i) {
		return animation[status][direction][i];
	}

}
