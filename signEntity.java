/* signEntity.java
 * Represents a sign object. When player collides with it, displays a message
 * 15/04/19
 * By Nick B and Luca Brolo
 */
public class signEntity extends Entity {

    private double moveSpeed = 0; // vert speed shot moves
    private Game game; // the game in which the ship exists
    
    private String message;
    
    /* construct the shot
     * input: game - the game in which the shot is being created
     *        ref - a string with the name of the image associated to
     *              the sprite for the shot
     *        x, y - initial location of shot
     */
    public signEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
        dy = moveSpeed;
    } // constructor

    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move shot
     */
    public void move(long delta) {

    	// if player teleported, remove this entity
        if (teleport) {
        	game.removeEntity(this);
        } // if
        this.setHorizontalMovement(parentHorizontalMovement);
        this.setVerticalMovement(parentVerticalMovement);

        super.move(delta); // calls the move method in Entity


    } // move
    
    // set the message
    public void setMessage(String newMessage) {
    	message = newMessage;
    } // setMessage
    
    public String getMessage() {
    	return message;
    } // getMessage

    public void collidedWith(Entity other) {
    	// collisions handled elsewhere
    } // collidedWith

} // ShipEntity class