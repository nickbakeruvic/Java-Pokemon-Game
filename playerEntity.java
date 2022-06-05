/* playerEntity.java
 * Represents the player. Doesn't move; collisions handled in other classes
 * 15/04/19
 * By Nick B and Luca Brolo
 */
public class playerEntity extends Entity {


	private Game game; // the game in which the alien exists

	/* construct the entity
     * input: game - the game in which the entity is being created
     *        ref - a string with the name of the image associated to
     *              the sprite 
     *        x, y - initial location of the entity
     */
    public playerEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
    } // constructor

    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move alien
     */
    public void move(long delta) {
    	return;
    } // move


    // collisions handled elsewhere
    public void collidedWith(Entity other) {
    } // collidedWith

} // playerEntity class