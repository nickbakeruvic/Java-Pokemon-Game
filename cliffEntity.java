/* cliffEntity.java
 * Represents a cliff object (player can walk down a cliff but not up or sideways through one)
 * 15/04/19
 * By Nick Baker and Luca Brolo
 */
public class cliffEntity extends Entity {

    private Game game; // the game in which the ship exists

    /* construct the entity
     * input: game - the game in which the entity is being created
     *        ref - a string with the name of the image associated to
     *              the sprite 
     *        x, y - initial location of the entity
     */
    public cliffEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
    } // constructor

    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move shot
     */
    public void move(long delta) {

    	// remove itself if player teleported
        if (teleport) {
        	game.removeEntity(this);
        } // if
        
        // move with the backgroun
        this.setHorizontalMovement(parentHorizontalMovement);
        this.setVerticalMovement(parentVerticalMovement);

        super.move(delta); // calls the move method in Entity


    } // move

    public void collidedWith(Entity other) {
    	// collisions handled in checkCollisions() in Game
    } // collidedWith

} // ShipEntity class