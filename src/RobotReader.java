//import java.awt.GraphicsConfiguration;
//import java.awt.GraphicsEnvironment;
//import java.awt.Image;
//import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RobotReader {
	private BufferedImage rawImage;

	public RobotReader(String file) {
		try {
			rawImage = ImageIO.read(new File(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public BufferedImage readRobot(int i, int j) throws IOException {
		return rawImage.getSubimage(40 * i, 40 * j, 32, 31);
	}
	public BufferedImage readRobot(int x,int y, int w, int h) throws IOException {
		return rawImage.getSubimage(x,y,w,h);
	}

}
