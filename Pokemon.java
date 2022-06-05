/* Pokemon.java
 * 15/04/19
 * Represents a single instance of a pokemon from PokemonList.java
 * By Nick Baker and Luca Brolo
 */
public class Pokemon {

	private String name; // stats of the pokemon
	private int health;
	private int currentHealth;
	private int attack;
	private int speed;
	private int level = 1;
	
	private String attack1 = ""; // the names of each of the pokemon's attacks
	private String attack2 = "";
	private String attack3 = "";
	private String attack4 = "";
	
	private int power1; // power level (damage multiplier) of each attack
	private int power2;
	private int power3;
	private int power4;
	
	private int accuracy1; // the accuracy of each attack
	private int accuracy2;
	private int accuracy3;
	private int accuracy4;
	
	private int charges1; // the amount of times each attack can be used before it must be recharged
	private int charges2;
	private int charges3;
	private int charges4;
	
	
	private String type; // the type of the pokemon (grass, fire, water)
	private int numMoves; // the number of moves the pokemon has
	
	private int movesLearned = 0; // the number of moves the pokemon has learned
	
	
	// constructor for pokemon without level
	// input the name, health, attack, speed, type, and number of moves of the pokemon
	Pokemon(String nameOfPokemon, int healthOfPokemon, int attackOfPokemon, int speedOfPokemon, String typeOfPokemon, int numMovesOfPokemon) {
		name = nameOfPokemon;
		health = healthOfPokemon;
		currentHealth = healthOfPokemon;
		attack = attackOfPokemon;
		speed = speedOfPokemon;
		type = typeOfPokemon;
		numMoves = numMovesOfPokemon;
		
		initMoves(type); // add a random set of moves for the type
	} // constructor
	
	
	// constructor for pokemon with level
	// input the name, health, attack, speed, type, number of moves, and the level of the pokemon
	Pokemon(String nameOfPokemon, int healthOfPokemon, int attackOfPokemon, int speedOfPokemon, String typeOfPokemon, int numMovesOfPokemon, int levelOfPokemon) {
		name = nameOfPokemon;
		health = healthOfPokemon;
		currentHealth = healthOfPokemon;
		attack = attackOfPokemon;
		speed = speedOfPokemon;
		level = levelOfPokemon;
		type = typeOfPokemon;
		numMoves = numMovesOfPokemon;
		
		// level up the pokemon to the right level
		for (int i = 1; i < levelOfPokemon; i++) {
			levelUp();
		} // for
		
		// init moves
		initMoves(type); // add a random set of moves for the type
		
	} // constructor
	
	
	// subtract an inputted integer from the health of the pokemon
	public void subtractHealth(int healthToSubtract) {
		currentHealth -= healthToSubtract;
		if (currentHealth < 0) currentHealth = 0;
	} // subtractHealh
	
	// get methods
	public String getName() {
		return name;
	} // getName
	
	// remove 1 from a charge relating to a move
	// input the attack number
	public void decrementCharges(int num) {
		if (num == 1) {
			charges1--;
		} else if (num == 2) {
			charges2--;
		} else if (num == 3) {
			charges3--;
		} else if (num == 4) {
			charges4--;
		}  // if else
	} // decrementCharges
	
	// returns the amount of charges for each move
	// input the attack number 
	public int getCharges(int num) {
		if (num == 1) {
			return charges1;
		} else if (num == 2) {
			return charges2;
		} else if (num == 3) {
			return charges3;
		} else if (num == 4) {
			return charges4;
		} // if else
		return 0;
	} // getCharges
	
	// returns an attack name
	// input the attack number
	public String getAttack(int num) {
		if (num == 1) {
			return attack1;
		} else if (num == 2) {
			return attack2;
		} else if (num == 3) {
			return attack3;
		} else if (num == 4) {
			return attack4;
		}  // if else
		return "";
	} // getAttack
	
	// returns the power of a given attack
	// input the attack number
	public int getPower(int num) {
		if (num == 1) {
			return power1;
		} else if (num == 2) {
			return power2;
		} else if (num == 3) {
			return power3;
		} else if (num == 4) {
			return power4;
		} // if else
		return 0;
	} // getPower
	
	// returns the accuracy of a given attack
	// input the attack number
	public int getAccuracy(int num) {
		if (num == 1) {
			return accuracy1;
		} else if (num == 2) {
			return accuracy2;
		} else if (num == 3) {
			return accuracy3;
		} else if (num == 4) {
			return accuracy4;
		} // if else
		return 0;
	} // getAccuracy
	
	
	/***********************/
	
	// get methods
	
	public int getHealth() {
		return health;
	} // getHealth
	
	public int getCurrentHealth() {
		return currentHealth;
	} // getCurrentHealth
	
	public int getAttack() {
		return attack;
	} // getattack
	
	public int getSpeed() {
		return speed;
	} // getSpeed
	
	public int getLevel() {
		return level;
	} // getLevel
	
	public int getMovesLearned() {
		return movesLearned;
	} // getMovesLearned
	
	/***************************/
	
	// set methods
	public void setHealth(int newHealth) {
		health = newHealth;
	} // setHealth
	
	public void setCurrentHealth(int newCurrentHealth) {
		currentHealth = newCurrentHealth;
	} // setCurrentHealth
	
	public void setSpeed(int newSpeed) {
		speed = newSpeed;
	} // setSpeed
	
	public void setLevel(int newLevel) {
		int oldLevel = level;
		level = newLevel;
		
		if (level > oldLevel) {
			currentHealth *= 1.05*(oldLevel - level);
			health *= 1.05*(level - oldLevel);
			attack *= 1.05*(level - oldLevel);
		} else if (level < oldLevel) {
			currentHealth /= 1.05*(oldLevel - level);
			health /= 1.05*(oldLevel - level);
			attack /= 1.05*(oldLevel - level);
		} // if else
		
	} // setLevel
	
	/***************************/
	
	// make current health equal to max health
	public void restoreHealth() {
		currentHealth = health;
	} // restoreHealth
	
	// make current charges of moves the max charges
	public void restoreCharges() {
		charges1 = getCharges(attack1);
		charges2 = getCharges(attack2);
		charges3 = getCharges(attack3);
		charges4 = getCharges(attack4);
	} // restoreCharges
	

	// level up the pokemon and increase its stats. Evolve it if necessary
	public void levelUp() {
		level += 1;
		
		currentHealth *= 1.05;
		health *= 1.05;
		attack *= 1.05;
		if (level == 2 || level == 3) {
			evolve();
		} // if
	} // addExperience
	
	// evolve the pokemon
	public void evolve() {
		// only evolve if there is a next evolution
		if (!Entity.getNextEvolution(name).equals("")) {
			name = Entity.getNextEvolution(name);
		
			health *= 1.20;
			currentHealth = health;
			attack *= 1.20;
			numMoves++;
			
			initMoves(type);
		} // if
	} // evolve
	
	// add random moves of the right type to the pokemon. Also set charges, accuracy, and power
	// for each of the moves
	public void initMoves(String type) {
		
		String line = "";
		
		String[] listOfMoves = new String[26];
		int index = 0;
		
		// make a string index of all possible moves names
        for (int i = 0; i < 82; i++) {
	      	  line = Entity.getLineMoves(i);
	      	  if(line.contains(type)) {
	      		  listOfMoves[index] = line;
	      		  
	      		  listOfMoves[index] = listOfMoves[index].replaceAll(type,"");
	      		  listOfMoves[index] = listOfMoves[index].replaceAll("[0-9]",  "");
	      		  listOfMoves[index] = listOfMoves[index].trim();
	      		  
	      		  index++;
	      	  } // if
        } // for
        
        
        // assign names to respective attacks 
        if (movesLearned == 0) {
	    	attack1 = listOfMoves[(int)(Math.random() * 25 + 1)];
	    	
	    	do {
	    		attack2 = listOfMoves[(int)(Math.random() * 25 + 1)];
	    	} while (attack1.equals(attack2)); // while
	    	
	    	movesLearned = 2;
	    	
	    	// assign powers, accuracies, and charges to each move
	    	power1 = getPower(attack1);
	    	accuracy1 = getAccuracy(attack1);
	    	charges1 = getCharges(attack1);
	    	power2 = getPower(attack2);
	    	accuracy2 = getAccuracy(attack2);
	    	charges2 = getCharges(attack2);
        }
    	
        // assign names to respective attacks 
    	if (numMoves >= 3 && movesLearned == 2) {
    		
	    	do {
	    		attack3 = listOfMoves[(int)(Math.random() * 25 + 1)];
	    	} while (attack1.equals(attack3) || attack2.equals(attack3)); // while
	    	movesLearned++;
	    	
	    	power3 = getPower(attack3);
	    	accuracy3 = getAccuracy(attack3);
	    	charges3 = getCharges(attack3);
    	} // if
    	// assign names to respective attacks 
    	if (numMoves == 4 && movesLearned == 3) {
	    	do {
	    		attack4 = listOfMoves[(int)(Math.random() * 25 + 1)];
	    	} while (attack1.equals(attack4) || attack2.equals(attack4) || attack3.equals(attack4)); // while
	    	
	    	movesLearned++;
	    	
	    	power4 = getPower(attack4);
	    	accuracy4 = getAccuracy(attack4);
	    	charges4 = getCharges(attack4);
    	} // if
        	
      } // initMoves
	
	// input a move name. Returns the power from pokemonMoves.txt
	public int getPower(String name) {
		String line = getLine(name);
		
  	  	line = "" + line.charAt(0) + line.charAt(1) + line.charAt(2);
		return Integer.parseInt(line);
	} // getPower
	
	// input a move name. Returns the accuracy from pokemonMoves.txt
	public int getAccuracy(String name) {
		String line = getLine(name);
  	  	
  	  	line = "" + line.charAt(3) + line.charAt(4) + line.charAt(5);
		return Integer.parseInt(line);
	} // getPower
	
	// input a move name. Returns the charges from pokemonMoves.txt
	public int getCharges(String name) { 
		String line = getLine(name);
  	  	
  	  	line = "" + line.charAt(6) + line.charAt(7);
		return Integer.parseInt(line);
	} // getPower
	
	// input a move name and returns all the stats of that move
	private String getLine(String name) {
		String str = "";
		
		// find the right line
		 for (int i = 0; i < 82; i++) {
	      	  str = Entity.getLineMoves(i);
	      	  if(str.contains(name)) {
	      		  break;
	      	  } // if
       } // for
		
		// format the string
		str = str.replaceAll("[A-Z]","");
	    str = str.replaceAll("[a-z]","");
	    str = str.replaceAll("\\s", "");
 	  	
 	  	return str;
	} // getLine
	
} // Pokemon class