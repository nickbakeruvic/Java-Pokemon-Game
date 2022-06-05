/* Entity.java
 * Deal with creation, storage, and movement of entities
 * An entity is any object that appears in the game.
 * 15/04/19
 * By Nick Baker and Luca Brolo
 */

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class Entity {

    protected double x; // current x location
    protected double y; // current y location
    protected Sprite sprite; // this entity's sprite
    protected double dx; // horizontal speed (px/s)  + -> right
    protected double dy; // vertical speed (px/s) + -> down

    protected static double parentHorizontalMovement = 0; // stores the movement of the background
    protected static double parentVerticalMovement = 0; // so that other entities move with it
    
    protected static boolean teleport = false; // true when a teleport needs to occur

    private static int teleportCounter = 0; // counts the number of times teleported. If it is even,
    										// the player is on the main screen

    protected static boolean playerMoving = false; // true when the player is moving

    private Rectangle me = new Rectangle(); // bounding rectangle of this entity
    private Rectangle him = new Rectangle(); // bounding rect. of other entities

    private int height; // stores the height and width of sprites created for reference to collision
    private int width;	// detections

    /* Constructor
     * input: reference to the image for this entity,
     *        initial x and y location to be drawn at
     */
    public Entity(String r, int newX, int newY) {
        x = newX;
        y = newY;
        sprite = (SpriteStore.get()).getSprite(r);
        
        height = (SpriteStore.get()).getSprite(r).getHeight();
        width = (SpriteStore.get()).getSprite(r).getWidth();
    } // constructor


    // reads files and returns a String array of the contents. Called in pokemonList, Pokemon, and
    // WildGrassTile to find names, stats, and moves of pokemon.
	public static String [] getFileContents(String fileName){
	
		String [] contents = null;
		int length = 0;
		try {
	
		   // input
		   String folderName = "/subFolder/"; // if the file is contained in the same folder as the .class file, make this equal to the empty string
		   String resource = fileName;
		
			// this is the path within the jar file
			InputStream input = PokemonList.class.getResourceAsStream(folderName + resource);
			if (input == null) {
				// this is how we load file within editor (eg eclipse)
				input = PokemonList.class.getClassLoader().getResourceAsStream(resource);
			} // if
			BufferedReader in = new BufferedReader(new InputStreamReader(input));	
		   
		   
		   
		   in.mark(Short.MAX_VALUE);  // see api
		
		   // count number of lines in file
		   while (in.readLine() != null) {
		     length++;
		   } // while
		
		   in.reset(); // rewind the reader to the start of file
		   contents = new String[length]; // give size to contents array
		
		   // read in contents of file and print to sc
		   
		   for (int i = 0; i < length; i++) {
		     contents[i] = in.readLine();
		   } // for
		   in.close();
		} catch (Exception e) {
		   System.out.println("File Input Error");
		} // try catch
		
		return contents;
	
	} // getFileContents
	
	// set methods for static variables
    public static void setTeleport(boolean newValue) {
    	teleport = newValue;
    } // setTeleport
    
    public static void setPlayerMoving(boolean a) {
        playerMoving = a;
    } // setPlayerMoving
    
    public static void setTeleported(boolean a) {
        teleport = a;
    } // setTeleported
    
    // get and set velocities
    public void setHorizontalMovement(double newDX) {
        dx = newDX;
    } // setHorizontalMovement

    public void setVerticalMovement(double newDY) {
        dy = newDY;
    } // setVerticalMovement
    
    // get methods for static variables
    public static boolean getTeleport() {
    	return teleport;
    } // getTeleport
    
    // get height / width of sprites
    public int getWidth() {
    	return width;
    } // getWidth
    
    public int getHeight() {
    	return height;
    } // getHeight

    public static int getTeleportCounter() {
        return teleportCounter;
    } // getTeleportCounter
    
    public static boolean getTeleported() {
        return teleport;
    } // getTeleported
    
    // get movement of backgrounds
    public double getHorizontalMovement() {
        return dx;
    } // getHorizontalMovement

    public double getVerticalMovement() {
        return dy;
    } // getVerticalMovement

    // get positions
    public int getX() {
        return (int) x;
    } // getX

    public int getY() {
        return (int) y;
    } // getY
    
    // increase the teleport counter by one. Called in game after teleporting.
    public static void incrementTeleportCounter() {
        teleportCounter++;
    } // incrementTeleportCounter
	
    // input the name of a pokemon, returns the name of the pokemon that that pokemon evolves into
    // or returns an empty String if there are no evolutions.
	public static String getNextEvolution(String name) {
		String str = ""; // The string that gets returned
		int index = 0; // stores the line of the file that the input name is located on
		
		// last line of the file & no evolutions
		if (name == "Allagon") return "";

		// finds the inputted name in the text file
		 for (int i = 0; i < 68; i++) {
			 str = Entity.getLinePokemon(i);
	      	  if(str.contains(name)) {
	      		  index = i;
	      		  break;
	      	  } // if
		 } // for
		
		// format the string so only the name of the pokemon is left
	 	str = Entity.getLinePokemon(index + 1);
	 	str = str.replaceAll("[0-9]","");
		str = str.replaceAll(" ","");
		str = str.replaceAll("GRASS", "");
		str = str.replaceAll("FIRE", "");
		str = str.replaceAll("WATER", "");
		str = str.trim();
		
		// as speed is the only stat which does not change with evolutions / levels and there are
		// no duplicate speeds, if the speeds match one evolves into the other
  		if (PokemonList.getSpeedStat(str) == PokemonList.getSpeedStat(name)) {
  			// if speeds match, return the name
  			return str;
  		} // if
  		// if speeds don't match, return empty string
  		return "";
	} // getNextEvolution
	
    // when given a line number, returns that line from pokemonNames.txt as a string
    public static String getLinePokemon(int index) {
        String [] fileContents = getFileContents("pokemonNames.txt");
        return fileContents[index];
     } // getPokemonName
    
    // when given a line number, returns that line from pokemonMoves.txt as a string
    public static String getLineMoves(int index) {
        String [] fileContents = getFileContents("pokemonMoves.txt");
        return fileContents[index];
     } // getPokemonName
	
    /*
     * Draw this entity to the graphics object provided at (x,y)
     */
    public void draw(Graphics g) {
        sprite.draw(g, (int) x, (int) y);
    } // draw
    
    /* move
     * input: delta - the amount of time passed in ms
     * output: none
     * purpose: after a certain amout of time has passed,
     *          update the location
     *          
     */
    public void move(long delta) {

    	// dont move if player teleported
        if (teleport) {
        	return;
        } // if
        // update location of entity based ov move speeds
        x += (delta * dx) / 1000;
        y += (delta * dy) / 1000;
    } // move

    /* Do the logic associated with this entity.  This method
     * will be called periodically based on game events.
     */
    public void doLogic() {}

    /* collidesWith
     * input: the other entity to check collision against
     * output: true if entities collide
     * purpose: check if this entity collides with the other.
     */
    public boolean collidesWith(Entity other) {
        me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
        him.setBounds(other.getX(), other.getY(),
            other.sprite.getWidth(), other.sprite.getHeight());
        return me.intersects(him);
    } // collidesWith

    /* collidedWith
     * input: the entity with which this has collided
     * purpose: notification that this entity collided with another
     * Note: abstract methods must be implemented by any class
     *       that extends this class
     */
    public abstract void collidedWith(Entity other);

} // Entity class