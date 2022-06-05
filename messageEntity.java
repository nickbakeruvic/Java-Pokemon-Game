/* messageEntity.java
 * 15/04/19
 * Represents a message object. Does not interact with other entities or move. Ignored by collision detection.
 * By Nick Baker and Luca Brolo
 */
public class messageEntity extends Entity {


    private Game game; // the game in which the alien exists

    /* construct the entity
     * input: game - the game in which the entity is being created
     *        ref - a string with the name of the image associated to
     *              the sprite 
     *        x, y - initial location of the entity
     */
    public messageEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
    } // constructor

     // input: delta - time elapsed since last move (ms)
    // this doesn't need to move, it should stay in the same place
    public void move(long delta) {
    	// remove the entity if player teleported. Doesnt need to move
        if (teleport) {
        	game.removeEntity(this);
        } // if
    } // move


    // collisions handled elsewhere
    public void collidedWith(Entity other) {
    } // collidedWith

} // AlienEntity class