/* doorEntity.java
 * Represents a door object. When player collides with it, it teleports the player to a room
 * 15/04/19
 * By Nick B and Luca Brolo
 */
public class doorEntity extends Entity {

    private double moveSpeed = 0; // vert speed shot moves
    private Game game; // the game in which the ship exists
    
    private int id; // unique id of each dor
    private static int counter = 1; // increments the id of each door by 1 each time a new dor created

    /* construct the entity
     * input: game - the game in which the entity is being created
     *        ref - a string with the name of the image associated to
     *              the sprite 
     *        x, y - initial location of the entity
     */
    public doorEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
        dy = moveSpeed;
        
        // id of doors to figure out which door the player entered
        id = counter;
        
        counter ++;
    } // constructor

    public int getId() {
    	return id;
    }
    
    // set the id
    public void setId(int num) {
    	id = num;
    }
    
    // reset the id counter. Used when player teleports
    public static void resetId() {
    	counter = 1;
    }
    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move shot
     */
    public void move(long delta) {
    	// remove itself if player teleported
        if (teleport) {
        	game.removeEntity(this);
        } // if        

        // move with the background
        this.setHorizontalMovement(parentHorizontalMovement);
        this.setVerticalMovement(parentVerticalMovement);

        super.move(delta); // calls the move method in Entity


    } // move

    public void collidedWith(Entity other) {

        if (other instanceof playerEntity) {
            teleport = true;
        } // if
    } // collidedWith

} // ShipEntity class