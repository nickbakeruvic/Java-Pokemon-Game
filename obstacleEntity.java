/* obstacleEntity.java
 * Represents an obstacle object. When player collides with it, the player can't move through it
 * 15/04/19
 * By Nick B and Luca Brolo
 */

public class obstacleEntity extends Entity {

    private Game game; // the game in which the ship exists

    /* construct a new obstacle entity
     * input: game - the game in which the shot is being created
     *        ref - a string with the name of the image associated to
     *              the sprite for the shot
     *        x, y - initial location of shot
     */
    public obstacleEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
    } // constructor

    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move shot
     */
    public void move(long delta) {

    	// remove the entity if player teleported
        if (teleport) {
        	game.removeEntity(this);
        } // if
        
        // make it move with the background
        this.setHorizontalMovement(parentHorizontalMovement);
        this.setVerticalMovement(parentVerticalMovement);

        super.move(delta); // calls the move method in Entity
    } // move

    // collisions handled elsewhere
    public void collidedWith(Entity other) {
    } // collidedWith

} // ShipEntity class