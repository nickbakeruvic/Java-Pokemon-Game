/* doorEntity.java
 * Represents a tile object. Should not interact with other entities, ignored by collisions, 
 * but it does move with the background
 * 15/04/19
 * By Nick B and Luca Brolo
 */
public class tileEntity extends Entity {

    private double moveSpeed = 0; // vert speed shot moves
    private Game game; // the game in which the ship exists
    
    private int id;

    /* construct the shot
     * input: game - the game in which the shot is being created
     *        ref - a string with the name of the image associated to
     *              the sprite for the shot
     *        x, y - initial location of shot
     */
    public tileEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
        dy = moveSpeed;
    } // constructor

    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move shot
     */
    public void move(long delta) {
        if (teleport) {
        	game.removeEntity(this);
        } // if        
        
        this.setHorizontalMovement(parentHorizontalMovement);
        this.setVerticalMovement(parentVerticalMovement);

        super.move(delta); // calls the move method in Entity


    } // move

    public void collidedWith(Entity other) {
    	// collisions not handled
    } // collidedWith

} // ShipEntity class