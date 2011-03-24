import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Blast {
	private BufferedImage rawImage;

	private BufferedImage[] sprites;

	public Blast(String file) {
		try {
			rawImage = ImageIO.read(new File(file));
			rawImage = rawImage.getSubimage(0, 0, 33, 200);
			loadGraphics();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void loadGraphics() throws IOException {
		sprites = new BufferedImage[5];
		for (int i = 0; i < 5; i++) {
			sprites[i] = readBlast(i);
		}
	}

	public BufferedImage getImage(int x) {
		return sprites[x];
	}

	public BufferedImage readBlast(int i) throws IOException {
		return rawImage.getSubimage(0, 40*i, 33, 40);
	}

}