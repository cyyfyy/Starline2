package game;

import java.awt.Image;

/**
 * A central reference point for creating resources for use in the game. 
 */
public class ResourceFactory {
	/** The single instance of this class to ever exist <singleton> */
	private static final ResourceFactory single = new ResourceFactory();

	/**
	 * Retrieve the single instance of this class 
	 *
	 * @return The single instance of this class 
	 */
	public static ResourceFactory get() {
		return single;
	}

	/** The window the game should use to render */
	private Java2DGameWindow window;

	/** 
	 * The default constructor has been made private to prevent construction of 
	 * this class anywhere externally. This is used to enforce the singleton 
	 * pattern that this class attempts to follow
	 */
	private ResourceFactory() {
	}

	/**
	 * Retrieve the game window that should be used to render the game
	 *
	 * @return The game window in which the game should be rendered
	 */
	public Java2DGameWindow getGameWindow() {
		// if we've yet to create the game window, create the appropriate one
		if (window == null) {
			window = new Java2DGameWindow();
		}
		return window;
	}

	/**
	 * Create or get a sprite which displays the image that is pointed
	 * to in the classpath by "ref"
	 * 
	 * @param ref A reference to the image to load
	 * @return A sprite that can be drawn onto the current graphics context.
	 */
	public Java2DSprite getSprite(Image ref) {
		if (window == null) {
			throw new RuntimeException("Attempt to retrieve sprite before game window was created");
		}
		return new Java2DSprite((Java2DGameWindow) window,ref);
	}
}