/* grassEntity.java
 * Represents a grass tile object. When player collides with it, has chance to let them fi ght a wild pokemon
 * 15/04/19
 * By Nick B and Luca Brolo
 */

public class grassEntity extends Entity {

    private Game game; // the game in which the ship exists

    private static String pokemonName = ""; // the name of the pokemon that player finds in grass
    private static boolean waiting = false; // if player has to respond to a message, don't find new pokemon
    private static boolean foundPokemon = false; // true if a pokemon has been found

    /* construct the entity
     * input: game - the game in which the entity is being created
     *        ref - a string with the name of the image associated to
     *              the sprite 
     *        x, y - initial location of the entity
     */
    public grassEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
    } // constructor
    
    // get methods
    public static String getPokemonName() {
    	return pokemonName;
    } // getPokemonName
    
    public static boolean getFoundPokemon() {
    	return foundPokemon;
    } // getFoundPokemon
    
    public static boolean getWaiting() {
    	return waiting;
    } // getWaiting
    
    // set foundPokemon to new value
    public static void setFoundPokemon(boolean newValue) {
    	foundPokemon = newValue;
    } // setFoundPokemon

    // reverses the value of waiting
    public static void toggleWaiting() {
    	waiting = !waiting;
    } // toggleWaiting
    
    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move shot
     */
    public void move(long delta) {
        if (Entity.getTeleport()) game.removeEntity(this);
        
        this.setHorizontalMovement(parentHorizontalMovement);
        this.setVerticalMovement(parentVerticalMovement);

        super.move(delta); // calls the move method in Entity
    } // move

    // collisions
    public void collidedWith(Entity other) {
    	// if collided with a player and not waiting, chance to find a pokemon in the grass
        if (other instanceof playerEntity && !waiting) {
        
        	// ~1/5 chance to find a pokemon when walking to a new grass tile
    	   int random = (int)(Math.random() * 500 + 1);
           
    	   if (random < 67) {
    		   // get a random line from pokemonNames.txt
    		   pokemonName = Entity.getLinePokemon(random);
    		   // format that line to get only the name
        	   pokemonName = pokemonName.replaceAll("[0-9]","");
        	   pokemonName = pokemonName.replaceAll(" ","");
        	   pokemonName = pokemonName.replaceAll("GRASS", "");
        	   pokemonName = pokemonName.replaceAll("FIRE", "");
        	   pokemonName = pokemonName.replaceAll("WATER", "");
        	   pokemonName = pokemonName.trim();
        	   foundPokemon = true;
           } // if 
               
        } // if
        
    } // collidedWith

} // WildGrassTile class

