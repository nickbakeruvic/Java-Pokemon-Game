/* NPCEntity.java
 * Represents an NPC object. When player collides with it, displays a message and allows the player to fight
 * 15/04/19
 * By Nick B and Luca Brolo
 */
public class NPCEntity extends Entity {

    private Game game; // the game in which the ship exists
    
    private int id; // each separate entity will have a unique id to tell it apart from others
    private static int counter = 1; // set id to counter and increment counter each time a new entity is made

    /* construct the entity
     * input: game - the game in which the entity is being created
     *        ref - a string with the name of the image associated to
     *              the sprite 
     *        x, y - initial location of the entity
     */
    public NPCEntity(Game g, String r, int newX, int newY) {
        super(r, newX, newY); // calls the constructor in Entity
        game = g;
        
        // id of doors to determine which door the player entered
        id = counter;
        counter ++;
    } // constructor

    public int getId() {
    	return id;
    } // getId
    
    // set the id
    public void setId(int num) {
    	id = num;
    } // setId
    
    // reset the counter for the id's for when the player teleports
    public static void resetId() {
    	counter = 1;
    } // resetId
    
    /* move
     * input: delta - time elapsed since last move (ms)
     * purpose: move the entity
     */
    public void move(long delta) {
    	// remove this entity if player teleoprted
        if (teleport) {
        	game.removeEntity(this);
        } // if        
        
        // make this entity move with the background
        this.setHorizontalMovement(parentHorizontalMovement);
        this.setVerticalMovement(parentVerticalMovement);

        super.move(delta); // calls the move method in Entity
    } // move

    // input: the entity that collided with it. If collided with a player, notify the game that it 
    // needs to teleport
    public void collidedWith(Entity other) {
        if (other instanceof playerEntity) {
            teleport = true;
        } // if
    } // collidedWith

} // NPCEntity class