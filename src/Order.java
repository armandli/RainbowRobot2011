/**
 * This is the data structure that you will use to store your commands to the
 * robot.
 * 
 * There are three cannons. The green cannon has unlimited ammo, and does a
 * single hit point worth of damage on a successful hit. The yellow cannon has
 * two rounds, and does 3 hitpoints of damage on a sucessful hit. The red cannon
 * has a single round, and does 5 hitpoints of damage on a succesful hit.
 * 
 * @author epz
 * 
 */

public class Order {
	public static final int DO_NOTHING = 0;
	public static final int GREEN_CANNON = 1;
	public static final int YELLOW_CANNON = 2;
	public static final int RED_CANNON = 3;

	private int order;
	private int fireX;
	private int fireY;

	/**
	 * Order a cannon to be fired to a particular location, or tell the robot to
	 * do nothing
	 * 
	 * @param ord
	 *            the cannon to be fired, or the order to do nothing
	 * @param x
	 *            the x-coord for the action
	 * @param y
	 *            the y-coord for the action
	 */
	public Order(int ord, int x, int y) {
		order = ord;
		fireX = x;
		fireY = y;
	}

	/**
	 * Orders an action targetted to (0,0). Only really useful for the
	 * DO_NOTHING command
	 * 
	 * @param ord
	 *            the order for the robot
	 */
	public Order(int ord) {
		order = ord;
		fireX = 0;
		fireY = 0;
	}

	/**
	 * The targeted x-coord
	 * 
	 * @return
	 */
	public int getX() {
		return fireX;
	}

	/**
	 * The targeted y-coord
	 * 
	 * @return
	 */
	public int getY() {
		return fireY;
	}

	/**
	 * Returns the order given
	 * 
	 * @return
	 */
	public int getAction() {
		return order;
	}

}