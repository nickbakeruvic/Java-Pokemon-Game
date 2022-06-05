/* backgroundEntity.java
 * 15/04/2019
 * This entity moves with player input. All other entities move based on this entity's
 * movement. Used to track the position of the backrgound and where player is.
 * By Nick Baker and Luca Brolo
 */
public class backgroundEntity extends Entity {

    private Game game; // the game in which the ship exists


    /* construct the entity
     * input: game - the game in which the entity is being created
     *        ref - a string with the name of the image associated to
     *              the sprite 
     *        x, y - initial location of the entity
     */
    public backgroundEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
    } // constructor

    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move background 
     */
    public void move(long delta) {

    	// if player has teleported, remove this entity and stop others from moving
        if (teleport) {
            game.removeEntity(this);
            parentHorizontalMovement = 0;
            parentVerticalMovement = 0;
        } // if

        // if player isn't moving, stop other entities from moving
        if (!playerMoving) {
            parentVerticalMovement = 0;
            parentHorizontalMovement = 0;
            return;
        } // if
       
        // set the movespeed so other entities move based on this entities movement
        parentHorizontalMovement = this.getHorizontalMovement();
        parentVerticalMovement = this.getVerticalMovement();
        super.move(delta); // calls the move method in Entity
    } // move


    // collisions handled elsewhere
    public void collidedWith(Entity other) {
    } // collidedWith    

} // backgroundEntity class