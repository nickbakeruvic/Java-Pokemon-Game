/* PokemonList.java
 * Deals with the pokemon array list and Pokemon.java
 * 15/4/19
 * By Nick Baker and Luca Brolo
 */

import java.util.*;
import java.io.*;


public class PokemonList {
	
	List<Pokemon> pokemonInventory = new ArrayList<Pokemon>();
	
	// add an element to the array list at level one
	// input the name of the pokemon to add
	public void addPokemon(String name) {
		int health = getHealthStat(name);
		int attack = getAttackStat(name);
		int speed = getSpeedStat(name);
		String type = getType(name);
		int numMoves = getNumMoves(name);
		
		Pokemon newPokemon = new Pokemon(name, health, attack, speed, type, numMoves);
		pokemonInventory.add(newPokemon);
		
	} // addAddress
	// add an element to the array list at a specific level
		// input the name of the pokemon to add and the level of the pokemon
	public void addPokemon(String name, int level) {
		int health = getHealthStat(name);
		int attack = getAttackStat(name);
		int speed = getSpeedStat(name);
		String type = getType(name);
		int numMoves = getNumMoves(name);
		
		Pokemon newPokemon = new Pokemon(name, health, attack, speed, type, numMoves, level);
		pokemonInventory.add(newPokemon);
	} // addAddress
	
	// remove all pokemon from the array list
	public void removeAllPokemon() {
		while (pokemonInventory.size() > 0) {
			pokemonInventory.remove(0);
		} // for
	} // removeAllPokemon
	
	// return the amount of pokemon player has
	public int getNumPokemon() {
		return pokemonInventory.size();
	}
	
	// get methods for specific index
	public String getName(int index) {
		return pokemonInventory.get(index).getName();
	} // getName
	
	public int getSpeedStat(int index) {
		return pokemonInventory.get(index).getSpeed();
	} // getSpeedStat
	
	public int getHealthStat(int index) {
		return pokemonInventory.get(index).getHealth();
	} // getHealthStat
	
	public int getAttackStat(int index) {
		return pokemonInventory.get(index).getAttack();
	} // getAttackstat
	
	public int getLevelStat(int index) {
		return pokemonInventory.get(index).getLevel();
	} // getLevel Stat
	
	public int getAccuracy(int index, int num) {
		return pokemonInventory.get(index).getAccuracy(num);
	} // getAccuracy
	
	
	public String getAttack(int index, int num) {
		return pokemonInventory.get(index).getAttack(num);
	} // getAttack
	
	
	public int getPower(int index, int num) {
		return pokemonInventory.get(index).getPower(num);
	} // getPower
	
	public int getCurrentHealthStat(int index) {
		return pokemonInventory.get(index).getCurrentHealth();
	} // currentHealtStat
	
	public void levelUp(int index) {
		pokemonInventory.get(index).levelUp();
	} // addExperience
	
	public void subtractHealthStat(int index, int healthToSubtract) {
		pokemonInventory.get(index).subtractHealth(healthToSubtract);
	} // subtractHealthstat
	
	// set the current health to max
	public void restoreHealth(int index) {
		pokemonInventory.get(index).restoreHealth();
	} // restoreHealth
	
	// set max charges and health for each pokemon
	public void healAll() {
		for (int i = 0; i < pokemonInventory.size(); i++) {
			pokemonInventory.get(i).restoreHealth();
			pokemonInventory.get(i).restoreCharges();
		} // for
	} // healAll
	
	// get methods for default stats
	public static  int getSpeedStat(String name) {
		String str = trimString(getLine(name));
	  	  
  	  	char first = str.charAt(6);
  	  	char second = str.charAt(7);
  	  
  	  	return Integer.parseInt("" + first + second);
	} // getSpeedStat
	
	public static int getNumMoves(String name) {
		String str = trimString(getLine(name));
	  	  
  	  	char first = str.charAt(8);
  	  
  	  	return Integer.parseInt("" + first);
        
	} // getHealthStat(int)
	
	public static  int getHealthStat(String name) {
		String str = trimString(getLine(name));
	  	  
  	  	char first = str.charAt(0);
  	  	char second = str.charAt(1);
  	  	char third = str.charAt(2);
  	  
  	  	return Integer.parseInt("" + first + second + third);
        
	} // getHealthStat(int)

	public static String getType(String name) {
		String line = getLine(name);
		
        if (line.contains("GRASS")) return "GRASS";
        if (line.contains("WATER")) return "WATER";
        if (line.contains("FIRE")) return "FIRE";
        
        return "GRASS";
	} // getType
	
	// determines the type advantage between two pokemon. Input two pokemon names, outputs the 
	// effectiveness multiplier
	public static double getEffectiveness(String name1, String name2) {
		String type1 = getType(name1);
		String type2 = getType(name2);
		if (type1 == "GRASS") {
			if (type2.equals("FIRE")) return 0.5;
			if (type2.equals("WATER")) return 1.5;
		} else if (type1 == "FIRE") {
			if (type2.equals("WATER")) return 0.5;
			if (type2.equals("GRASS")) return 1.5;
		} else {
			if (type2.equals("GRASS")) return 0.5;
			if (type2.equals("FIRE")) return 1.5;
		} // if
		
		return 1.0;
	} // getEffectiveness
	
	public static int getAttackStat(String name) {
		String str = trimString(getLine(name));
  	  
  	  	char first = str.charAt(3);
  	  	char second = str.charAt(4);
  	  	char third = str.charAt(5);
  	  
  	  	return Integer.parseInt("" + first + second + third);
	} // getAttackStat
	
	private static String getLine(String name) {
		String str = "";
		
		 for (int i = 0; i < 68; i++) {
	      	  str = Entity.getLinePokemon(i);
	      	  if(str.contains(name)) {
	      		  break;
	      	  }
       } // for
		
 	  	return str;
	} // getLine
	
	// trims the string so it is just the stats
	private static String trimString(String str) {
		 str = str.replaceAll("[A-Z]","");
	    str = str.replaceAll("[a-z]","");
	    str = str.replaceAll("\\s", "");
	    str = str.trim();
	    return str;
	} // trimString
	
	public void addMove(int index, String type) {
		pokemonInventory.get(index).initMoves(type);
	} // addMove
	
	public int getMovesLearned(int index) {
		return pokemonInventory.get(index).getMovesLearned();
	} // getMovesLearned
	
	public int getCharges(int index, int num) {
		return pokemonInventory.get(index).getCharges(num);
	} // getCharges
	
	
	public void decrementCharges(int index, int num) {
		pokemonInventory.get(index).decrementCharges(num);
	} // decrementCharges

	
	public int getMaxCharges(String name) {
		return pokemonInventory.get(0).getCharges(name);
	} // getMaxCharges
	
	public String getNextEvolution(String name) {
		
		String str = "";
		int index = 0;
		
		 for (int i = 0; i < 68; i++) {
			 str = Entity.getLinePokemon(i);
	      	  if(str.contains(name)) {
	      		  index = i;
	      		  break;
	      	  } // if
		 } // for
		 
		 if (index + 1 >= 68) return "";
		
	 	str = Entity.getLinePokemon(index + 1);
	 	str = str.replaceAll("[0-9]","");
		str = str.replaceAll(" ","");
		str = str.replaceAll("GRASS", "");
		str = str.replaceAll("FIRE", "");
		str = str.replaceAll("WATER", "");
		str = str.trim();
		
  		if (getSpeedStat(str) == getSpeedStat(name)) {
  			return str;
  		} // if
  		return "";
  		
	} // getNextEvolution
} // PokemonList
