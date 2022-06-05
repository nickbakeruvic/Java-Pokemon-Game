/* Game.java
 * Space Invaders Main Program
 *
 */

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Game extends Canvas {

	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true while waiting to start the game
	
	private ArrayList<Entity> entities = new ArrayList<Entity>(); // list of all entities
	private ArrayList<Entity> obstacles = new ArrayList<Entity>(); // categorises each entity
	private ArrayList<Entity> grass = new ArrayList<Entity>(); 	   // for efficiency when checking
	private ArrayList<Entity> bridges = new ArrayList<Entity>();   // for collisions
	private ArrayList<Entity> cliffs = new ArrayList<Entity>(); 
	private ArrayList<Entity> signs = new ArrayList<Entity>(); 
	private ArrayList<Entity> doors = new ArrayList<Entity>();
	private ArrayList<Entity> NPCEntities = new ArrayList<Entity>();
	private ArrayList<Entity> removeEntities = new ArrayList<Entity>(); // list of entities to remove
	
	PokemonList pokemonInventory = new PokemonList(); // stores the player's pokemon & stats etc
	PokemonList trainerPokemon = new PokemonList(); // stores the trainer / wild pokemon & stats etc
	
	// entities that can't be initialised elsewhere
	public Entity background; // this tile's position is stored and used as reference for everything else
	public Entity player; // the player
	public Entity messageBox; // the message box when you find a pokemon or hit a trainer
	public Entity collisionBox; // this is created to check if things collide with it
	
	public Entity door1; // the doors
	public Entity door2;
	public Entity door3;
	public Entity door4;
	public Entity door5; // the doors
	public Entity door6;
	public Entity door7;
	public Entity door8;
	public Entity door9; // the doors
	public Entity door10;
	public Entity door11;
	public Entity door12;
	public Entity door13;
	
	private String trainerName; // name of trainer player is fighting
	private String nameOfEnemyPokemon = ""; // name of pokemon player is fighting
	
	private String playerDirection = "down"; // direction player is facing for animations
	
	private String message = ""; // for displaying messages at different points in the game
	private String message1 = ""; 
	private String message2 = "";
	private String message3 = "";
	private String message4 = "";
	
	private double moveSpeed = 128; // hor. vel. of background (px/s)
	
	private int counter = 0; // for cycling through images for player animation
	int backgroundX = 0; // stores the coordinates of the background so that things stay in the same 
	int backgroundY = 0; // spot when you go in a door / grass as when you go out
	int teleportStages = 0; // when teleporting stores the "stage" as different things have to be done
							// at each stage
	private int numTrainerPokemon = 0; // the index of the pokemon that the trainer is using currently
	private int trainerNum; // the ID of the trainer so there can be unique messages and pokemon
							// for each ID
	private int indexPokemonChosen = 0; // index of chosen pokemon when fighting another pokemon
	private int spacesRight= 0; // the coordinates of the pokemon the user has selected when they 
	private int spacesDown = 0; // are choosing a pokemon to battle with
	private int spacesRightInitialised = 0; // the maximum coordinates the player could select
	private int spacesDownInitialised = 0;
	
	// keys pressed
	private static boolean leftPressed = false; // true if corresponding arrow keys or WASD pressed
	private static boolean rightPressed = false;
	private static boolean upPressed = false;
	private static boolean downPressed = false;
	private static boolean sprintPressed = false;
	
	private static boolean onePressed = false; // true if corresponding keys are pressed
	private static boolean twoPressed = false;
	private static boolean xPressed = false;
	private static boolean ePressed = false; 
	private static boolean zeroPressed = false;
	private static boolean enterPressed = false;
	
	private boolean chooseStartingPokemon = true; // true when the player needs to select a starting
												// pokemon. When true, calls chooseStartingPokemon()
	private boolean showMessage = false; // true when the message box needs to be shown
	private boolean displayMenu = false; // true if displaying monster menu while not fighting
	boolean waitingForResponse = false; // waiting for player to choose options after 
										// finding a pokemon or talking to a trainer
	
	private boolean logicRequiredThisLoop = false; // true if logic needed

	private boolean choosingPokemon = false; // stage one of fighting a pokemon
	private boolean justSwitched = false; // true when player switches pokemon when fighting
	private boolean fightingPokemon = false; // when in the process of fighting a pokemon
	private boolean fightingTrainer = false; // when player is fighting a trainer specifically
	private boolean inBattle = false; // when player is actually in battle against a pokemon
	private boolean fightingWildPokemon = false; // true when player is fighting a monster
														// that isn't a trainer's
	private boolean isPlayerTurn; // true if it is the player's turn during battle
	private boolean enemyAttackChosen = false; // when enemy pokemon has selected a move
	private boolean playerAttackChosen = false; // when player has selected a move and hit enter
												// while fighting a pokemon
	private static double effectiveness = 1; // damage multiplier that changes based on type
										  // advantages when fighting pokemon
	private boolean evolvePokemon = false; // whether or not a pokemon needs to evolve after the fight
	private int evolveIndex; // index of the pokemon that needs to evolve
	
	private boolean enterDoor1 = false; // stores which door the player collided with to determine
	private boolean enterDoor2 = false; // which room to initialise
	private boolean enterDoor3 = false;
	private boolean enterDoor4 = false;
	private boolean enterDoor5 = false; // stores which door the player collided with to determine
	private boolean enterDoor6 = false; // which room to initialise
	private boolean enterDoor7 = false;
	private boolean enterDoor8 = false;
	private boolean enterDoor9 = false; // stores which door the player collided with to determine
	private boolean enterDoor10 = false; // which room to initialise
	private boolean enterDoor11 = false;
	private boolean enterDoor12 = false;
	private boolean enterDoor13 = false;
	
	private boolean inRoom = false; // true when player is in a room
	
	private boolean teleportFinished = false; // when "teleporting" is over start displaying stuff
	private int gameResult;
	

	// Construct the game and set it running.
	public Game() {
		// create a frame to contain game
		JFrame container = new JFrame("Pokemon");

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the game
		panel.setPreferredSize(new Dimension(1920, 1080));
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setBounds(0, 0, 1920, 1080);
		panel.add(this);

		// Tell AWT not to bother repainting canvas since that will
		// be done using graphics acceleration
		setIgnoreRepaint(true);

		// make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// if user closes window, shutdown game and jre
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// add key listener to this canvas
		addKeyListener(new KeyInputHandler());

		// request focus so key events are handled by this canvas
		requestFocus();

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// start the game
		gameLoop();
	} // constructor

	// remove all entities from all array lists
	private void removeAllEntities() {
		// remove entities that are already initialised
		entities.remove(background);
		entities.remove(player);
		entities.remove(messageBox);
		
		// remove each singular element of each array
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			entities.remove(entity);
		} // for

		for (int i = 0; i < obstacles.size(); i++) {
			Entity entity = (Entity) obstacles.get(i);
			obstacles.remove(entity);
		} // for
		
		for (int i = 0; i < grass.size(); i++) {
			Entity entity = (Entity) grass.get(i);
			grass.remove(entity);
		} // for
		
		for (int i = 0; i < bridges.size(); i++) {
			Entity entity = (Entity) bridges.get(i);
			bridges.remove(entity);
		} // for
		
		for (int i = 0; i < doors.size(); i++) {
			Entity entity = (Entity) doors.get(i);
			doors.remove(entity);
		} // for
		
		for (int i = 0; i < cliffs.size(); i++) {
			Entity entity = (Entity) cliffs.get(i);
			cliffs.remove(entity);
		} // for
		
		for (int i = 0; i < signs.size(); i++) {
			Entity entity = (Entity) signs.get(i);
			signs.remove(entity);
		} // for
		
		for (int i = 0; i < NPCEntities.size(); i++) {
			Entity entity = (Entity) NPCEntities.get(i);
			NPCEntities.remove(entity);
		} // for
		
		entities.removeAll(removeEntities);
		obstacles.removeAll(removeEntities);
		grass.removeAll(removeEntities);
		bridges.removeAll(removeEntities);
		doors.removeAll(removeEntities);
		cliffs.removeAll(removeEntities);
		signs.removeAll(removeEntities);
		NPCEntities.removeAll(removeEntities);
		removeEntities.clear();
	} // removeAllEntities
	
	
	// do everything needed to start a battle
	public void initBattle() {
		// reset
		trainerPokemon.removeAllPokemon();
		numTrainerPokemon = 0;
		
		// set names & pokemon
		if (fightingWildPokemon) {
			trainerName = "wild pokemon ";
			trainerPokemon.addPokemon(nameOfEnemyPokemon);
		} else if (trainerNum == 1) {
			trainerName = "The Weeping Prince";
			trainerPokemon.addPokemon("Agnidon");
			trainerPokemon.addPokemon("Allagon");
			trainerPokemon.addPokemon("Gectile");
			trainerPokemon.addPokemon("Drokoro");
			trainerPokemon.addPokemon("Sapragon");
			trainerPokemon.addPokemon("Grintrock");
		} else if (trainerNum == 2) {
			trainerName = "The Malevolent Queen";
			trainerPokemon.addPokemon("Criniotherme");
			trainerPokemon.addPokemon("Aardart");
			trainerPokemon.addPokemon("Sharpfin");
			trainerPokemon.addPokemon("Chillimp");
			trainerPokemon.addPokemon("Chloragon");
			trainerPokemon.addPokemon("Nudikill");
		} // else
		// start battle
		fightingPokemon = true;
		choosingPokemon = true;
		teleportFinished = true;
	} // initBattle

	/*
	 * initEntities input: none output: none purpose: Initialise the starting state
	 * of the background and player entities. Each entity will be added to the array
	 * of entities in the game.
	 */
	private void initEntities() {
		
		background = new backgroundEntity(this, "images/grass.jpg", backgroundX, backgroundY);
		entities.add(background);

		initBackground();
		
		player = new playerEntity(this, "images/standing_north.png", 925, 460);
		entities.add(player);

		initRoads();
		initTrees();
		initFlowers();
		
		Entity house = new obstacleEntity(this, "images/house1.png", backgroundX + 1110, backgroundY + 300);
		entities.add(house);
		obstacles.add(house);
		
		Entity house2 = new obstacleEntity(this, "images/house2.png", backgroundX + 512, backgroundY + 300);
		entities.add(house2);
		obstacles.add(house2);
		
		Entity lab = new obstacleEntity(this, "images/lab.png", backgroundX + 740, backgroundY + 672);
		entities.add(lab);
		obstacles.add(lab);
		
		Entity house3 = new obstacleEntity(this, "images/house1.png", backgroundX + 1110, backgroundY - 2388);
		entities.add(house3);
		obstacles.add(house3);
		
		Entity house4 = new obstacleEntity(this, "images/house2.png", backgroundX + 512, backgroundY - 3028);
		entities.add(house4);
		obstacles.add(house4);
		
		Entity house5 = new obstacleEntity(this, "images/house1.png", backgroundX - 2148, backgroundY - 1620);
		entities.add(house5);
		obstacles.add(house5);
		
		Entity house6 = new obstacleEntity(this, "images/house2.png", backgroundX - 3160, backgroundY - 3594);
		entities.add(house6); 
		obstacles.add(house6);
		
		Entity house7 = new obstacleEntity(this, "images/house1.png", backgroundX - 2160, backgroundY - 2325);
		entities.add(house7); 
		obstacles.add(house7);
		
		Entity house8 = new obstacleEntity(this, "images/house1.png", backgroundX - 2700 - 46*2, backgroundY + 300 - 3062 + (3*64));
		entities.add(house8);
		obstacles.add(house8);
		Entity pokecenter = new obstacleEntity(this, "images/pokecenter.png", backgroundX + 576,
				backgroundY - 2390);
		entities.add(pokecenter);
		obstacles.add(pokecenter);
		
		Entity pokecenter2 = new obstacleEntity(this, "images/pokecenter.png",
				backgroundX - 2020, backgroundY - 2823);
		entities.add(pokecenter2);
		obstacles.add(pokecenter2);
		
		Entity shop = new obstacleEntity(this, "images/pokeshop.png", backgroundX + 1110,
				backgroundY - 3028); 
		entities.add(shop);
		obstacles.add(shop);
		
		Entity shop2 = new obstacleEntity(this, "images/pokeshop.png", backgroundX - 1920,
				backgroundY - 3146);
		entities.add(shop2);
		obstacles.add(shop2);
		
		Entity gym = new obstacleEntity(this, "images/gym.png", backgroundX - 2598, backgroundY - 3540);
		entities.add(gym);
		obstacles.add(gym);
		
		//house
		door1 = new doorEntity(this, "images/door.png", backgroundX + 1156, backgroundY + 480);
		entities.add(door1);
		doors.add(door1);
		
		//gym
		door2 = new doorEntity(this, "images/door.png", backgroundX - 2460, backgroundY - 3284);
		entities.add(door2);
		doors.add(door2);
		
		//house
		door3 = new doorEntity(this, "images/door.png", backgroundX + 660, backgroundY + 480);
		entities.add(door3);
		doors.add(door3);
		
		//lab
		door4 = new doorEntity(this, "images/door.png", backgroundX + 940, backgroundY + 850);
		entities.add(door4);
		doors.add(door4);
		
		//house
		door5 = new doorEntity(this, "images/door.png", backgroundX + 660, backgroundY - 2850);
		entities.add(door5);
		doors.add(door5);
		
		//house
		door6 = new doorEntity(this, "images/door.png", backgroundX + 1160, backgroundY - 2210);
		entities.add(door6);
		doors.add(door6);
		
		//house
		door7 = new doorEntity(this, "images/door.png", backgroundX - 2740, backgroundY - 2391);
		entities.add(door7);
		doors.add(door7);
		
		//house
		door8 = new doorEntity(this, "images/door.png", backgroundX - 3006, backgroundY - 3416);
		entities.add(door8);
		doors.add(door8);
		
		//house
		door9 = new doorEntity(this, "images/door.png", backgroundX - 2110, backgroundY - 2146);
		entities.add(door9);
		doors.add(door9);
		
		//pokecenter
		door10 = new doorEntity(this, "images/door.png", backgroundX + 640, backgroundY - 2209);
		entities.add(door10);
		doors.add(door10);
		
		//pokecenter
		door11 = new doorEntity(this, "images/door.png", backgroundX - 1960, backgroundY - 2642);
		entities.add(door11);
		doors.add(door11);
		
		//pokeshop
		door12 = new doorEntity(this, "images/door.png", backgroundX - 1820, backgroundY - 2962);
		entities.add(door12);
		doors.add(door12);
		
		//pokeshop
		door13 = new doorEntity(this, "images/door.png",  backgroundX + 1210, backgroundY - 2844);
		entities.add(door13);
		doors.add(door13);
		
	} // initEntities
	

	// initialise down all the background grass tiles
	public void initBackground() {
		
		for (int i = 0; i < 48; i++) {
			for (int j = 0; j < 96; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/grass.jpg",
						backgroundX + (i * 64) + 420 - (16*64), backgroundY + j*64 - 2048 - 2048);
				entities.add(backgroundTile);
			} // inner for
		} // outer for
		
		for (int i = 0; i < 50; i++) {
			for (int j = 0; j < 26; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/grass.jpg",
						backgroundX + (i * 64) + 420 - 1024, backgroundY + j*64 - 2048 + 2048 - 64);
				entities.add(backgroundTile);
			} // inner for
		} // outer for
		
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 64; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/grass.jpg",
						backgroundX + (i * 64) - 2652, backgroundY +  j*64 - 3072 - 1024);
				entities.add(backgroundTile);
				
			} // inner for
		} // outer for
		
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 64; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/grass.jpg",
						backgroundX + (i * 64) - 5724, backgroundY + j*64 - 3584 - 1024);
				entities.add(backgroundTile);
			} // inner for
		} // outer for
		
	} // initBackground
	
	// initialise all flowers
	public void initFlowers() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 2; j++) {
				Entity flower = new tileEntity(this, "images/redflower.png", backgroundX
						+ i * 40 + 225 +(8*64), backgroundY + 810 + 64 + j*45);
				entities.add(flower);
			} // inner for
		} // outer for
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				Entity flower = new tileEntity(this, "images/blueflower.png", backgroundX
						+ i * 40 + (7*64) + 60, backgroundY + 810 + 64 + j*45 - (6*64));
				entities.add(flower);
			} // inner for
		} // outer for
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				Entity flower = new tileEntity(this, "images/yellowflower.png", backgroundX
						+ i * 40 + 244 +(8*64) +(7*64), backgroundY + 810 + 64 + j*45 - (6*64));
				entities.add(flower);
			} // inner for
		} // outer for
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Entity flower = new tileEntity(this, "images/redflower.png", backgroundX
						+ i * 40 + 244 +(8*64) +(6*64) -(6*46), backgroundY + 810 + 32 + j*45 - (55*64));
				entities.add(flower);
			} // inner for
		} // outer for
	}
	
	// initialise all trees
	public void initTrees() {
		Entity tree;
		
		// first town
		for (int i = 0; i < 23; i++) {
			for (int j = 0; j < 40; j++) {
				// top row
				if(i!=11 && i !=12 && j < 2) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX
							+ (i * 46) - 251 + 644 , backgroundY + (j*64) -64);
					entities.add(tree);
					obstacles.add(tree);
				} // if
				tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + i * 46 + 644,
						backgroundY + 2176 - j*64 -64 - 1024 - 64+2496);
				entities.add(tree);
				obstacles.add(tree);
				
				if (i < 17) {
					if (j == 0 && i != 2 && i != 16 ) {
		
					} else { 
						tree = new obstacleEntity(this, "images/tree.png", backgroundX + 2112
							+ j * 46 - 64 - 644 - 46, backgroundY - 515 + i*64 -128 + 384 + (64*3));
						entities.add(tree);
						obstacles.add(tree);
					} // if else
					if (j == 39 && i != 2 && i != 16 ) {
						
					} else {
						
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251
								+ j * 46 + 644 - 138 + (46*3) - 1794 + 46, backgroundY - 515
								+ i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if else
						
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251
							+ j * 46 + 644 - 138 + (46*3) - 1794 + 46, backgroundY - 515
							+ i*64 -64 + 384 + (64*3) - 64 + 1024);
					entities.add(tree);
					obstacles.add(tree);
					
					tree = new obstacleEntity(this, "images/tree.png", backgroundX + 2112 
							+ j * 46 - 64 - 644 - 46, backgroundY - 515 + i*64 -128 + 384
							+ (64*3) +1024);
					entities.add(tree);
					obstacles.add(tree);
				} // outer if
				
			} // inner for
		} // outer for 
		
		//route between first and second towns
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 23; j++) {
					//left
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
							- 138 + (46*3) - 1794 + 46 + 746 - 10, backgroundY - 515 - 1024 - 1024 + 64
							+ i*64 -64 -64 + 384 + (64*3) - 64);
					entities.add(tree);
					obstacles.add(tree);
					
					//right
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
							- 138 + (46*3) - 1794 + 46 + 746 - 10 + 2048 - 25, backgroundY - 64 - 515
							- 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
					entities.add(tree);
					obstacles.add(tree);
					
					if(j<2 && i>27) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<10 && i==31) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<2 && i==24) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					}
					if(j<2 && i==22) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<3 && i==23) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==2 && i==30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j<8&&i==30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) + 644,
								backgroundY - 64 - 2048 - 1024 + 64 + i*64 -64 + 384 + (64*3) - (64*7));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j<8&&i==30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - 46 - (6*46),
								backgroundY - 64 - 2048 - 1024 + 64 + i*64 -64 + 384 + (64*3) - (64*7));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==7&&i>25) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46),
								backgroundY - 67 - 2048 - 1024 + 64 + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==6&&i>26) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46),
								backgroundY - 67 - 2048 - 1024 + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==5&&i>27) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46),
								backgroundY - 67 - 2048 - 1024 - 64 + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==4&&i>28) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY
								- 67 - 2048 - 1024 -( 64*2 )+ i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					}
					if(j==3&&i>29) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY
								- 67 - 2048 - 1024 -( 64*3 ) + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					}
					if(j==2&&i>29) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY
								- 67 - 2048 - 1024 -( 64*3 ) + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					}
					
					if(j==1&&i>29) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY
								- 67 - 2048 - 1024 -( 64*3 ) + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					}
					if(j==0&&i>30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								- 138 + (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY
								- 67 - 2048 - 1024 -( 64*4 ) + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==8 && i==30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==1 && i>29) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46)
								- (13*46), backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64
								+ 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					
					if(j==2 && i==28) {
						Entity cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251
								+ j * 46 + 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46)
								- 160 -64, backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384
								+ (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
					} // if
					
					if(j==2 && i==22) {
						Entity cliff = new obstacleEntity(this, "images/cliff.png", backgroundX
								- 251 + j * 46 + 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25
								- 46 - (7*46) - 160 -64, backgroundY - 64 - 515 - 1024 - 1024 + 64
								+ i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46
								+ 524 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46)
								- 160 -64, backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384
								+ (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						
					} // if
					if(j==2 && i==21) {
						Entity cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46
								+ 450 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64,
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46 + 344 
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64,
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46 + 224
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64, 
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						
					} // if
					if(i<27 && i > 21 && j == 0) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(i<26 && i > 22 && j == 1) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(i<25 && i > 23 && j == 1) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46) + 46,
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(i<25 && i > 23 && j == 1) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46
								+ 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (14*46) + 46,
								backgroundY - 515 - 1024 - 1024 - 64 + i*64 -64 + 384 + (64*11) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<5 && i>24 && i<31) {
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 + 46 - (7*46),
								backgroundY + 64 - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
					} // if
					
					if(j<3 && i>22 && i<25) {
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 + 46 - (6*46) +20,
								backgroundY + 64 - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
					} // if
					if(j<2 && i>22 && i<25) {
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 + 46 - (6*46) +20 + 32,
								backgroundY - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
					} // if
					
					if(j<8 && i>24 && i<31) {
						if(i<27 &&j>5) {
							
						}else {
					
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 + 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46)
								, backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
						} // if
					} // outer if
					
					if(j<5 && i>22 && i<25) {

						if(j>2) {
							Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
									+ j * 32 + 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
									backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
							entities.add(grassTile);
							grass.add(grassTile);
						} // if
					} // outer if
					
					if(j<6 && i>22 && i<27) {

						if(j>2) {
							Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
									+ j * 32 + 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
									backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*20) - 32);
							entities.add(grassTile);
							grass.add(grassTile);
						} // if
					} // outer if
					
					
					if(j<7 && i>24 && i<31) {
						if(i<27 &&j>5) {
							
						}else {
					
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 + 644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*10) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
						} // if else
					} // if
					
					
					if(j<7 && i>24 && i<29) {
					
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 + 644 - 138 - (46*9) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*10) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
	
					} // if
					
					if(j>1 && j<4 && i>22 && i<25) {
						
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 + 644 - 138 - (46*9) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024 + 64 + i*32 -128 + 384 + (64*10) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
	
					} // if
					
					
					if(i<24 && i > 21 && j == 0) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46) , backgroundY
								- 64 - 515 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(i == 22 && j == 0) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
								- 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46) , backgroundY
								- 64 - 515 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
				
					//right
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644 - 138
							+ (46*3) - 1794 + 46 + 746 - 10 + 2048 - 25, backgroundY - 64 - 515- 1024 - 1024
							- 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
					entities.add(tree);
					obstacles.add(tree);
					
					if(j<2 && i>27) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), backgroundY - 64
								- 1024- 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<10 && i==31) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), backgroundY - 64
								- 1024- 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<2 && i==24) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), backgroundY
								- 64- 1024 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<2 && i==22) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), backgroundY
								- 64 - 1024- 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<3 && i==23) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), backgroundY
								- 64- 1024 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==2 && i==30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), backgroundY 
								- 64- 1024 - 515 - 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j<8&&i==30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*4) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) + 644, backgroundY
								- 64 - 1024- 2048 - 1024 + 64 + i*64 -64 + 384 + (64*3) - (64*7));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j<8&&i==30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*4) - 1794 + 746 - 10 + 2048 - 25 - 46 - (6*46), backgroundY 
								- 64 - 2048 - 1024- 1024 + 64 + i*64 -64 + 384 + (64*3) - (64*7));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==7&&i>25) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY - 67
								- 2048- 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==6&&i>26) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY - 67
								- 2048- 1024 - 1024 + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==5&&i>27) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY - 67
								- 2048- 1024 - 1024 - 64 + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==4&&i>28) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY - 67
								- 2048- 1024 - 1024 -( 64*2 )+ i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==3&&i>29) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY - 67
								- 2048- 1024 - 1024 -( 64*3 ) + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==2&&i>29) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY - 67
								- 2048- 1024 - 1024 -( 64*3 ) + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==1&&i>29) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY - 67
								- 2048- 1024 - 1024 -( 64*3 ) + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==0&&i>30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 - 138
								+ (46*12) - 1794 + 746 - 10 + 2048 - 25 - 46 - (1*46), backgroundY - 67
								- 2048 - 1024- 1024 -( 64*4 ) + i*64 -64 + 384 + (64*3) - (64*2));
						entities.add(tree);
						obstacles.add(tree);
					} // if
				
					if(j==8 && i==30) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), backgroundY 
								- 64 - 515 - 1024 - 1024 - 1024+ 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==1 && i>29) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - (13*46),
								backgroundY - 64 - 515 - 1024 - 1024- 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					
					if(j==2 && i==28) {
						Entity cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64, 
								backgroundY - 64 - 515 - 1024- 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
					} // if
					
					if(j==2 && i==22) {
						Entity cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64,
								backgroundY - 64 - 515 - 1024- 1024 - 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46 + 524 - 138
								+ (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64, backgroundY
								- 64 - 515 - 1024 - 1024- 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						
					} // if
					if(j==2 && i==21) {
						Entity cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46 + 450 - 138
								+ (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64, backgroundY
								- 64 - 515 - 1024 - 1024- 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46 + 344 - 138 
								+ (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64, backgroundY 
								- 64 - 515 - 1024 - 1024- 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251 + j * 46 + 224 - 138
								+ (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46) - 160 -64, backgroundY
								- 64 - 515 - 1024 - 1024- 1024 + 64 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(cliff);
						cliffs.add(cliff);
						
					} // if
					if(i<27 && i > 21 && j == 0) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644 - 138
								+ (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46) , backgroundY 
								- 64 - 515 - 1024 - 1024 + 64 - 1024+ i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(i<26 && i > 22 && j == 1) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46) , backgroundY
								- 64 - 515 - 1024 - 1024 + 64- 1024 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(i<25 && i > 23 && j == 1) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644 
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46) + 46 ,
								backgroundY - 64 - 515 - 1024 - 1024 + - 102464 + i*64 -64 + 384 + (64*3) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(i<25 && i > 23 && j == 1) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (14*46) + 46 , backgroundY
								- 515 - 1024 - 1024 - 64- 1024 + i*64 -64 + 384 + (64*11) - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j<5 && i>24 && i<31) {
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 + 46 - (7*46),
								backgroundY + 64 - 515 - 1024 - 1024- 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
					} // if
					
					if(j<3 && i>22 && i<25) {
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251 
								+ j * 32 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 + 46 - (6*46) +20,
								backgroundY + 64 - 515 - 1024 - 1024- 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
					} // if
					if(j<2 && i>22 && i<25) {
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 + 46 - (6*46) +20 + 32,
								backgroundY - 515 - 1024 - 1024- 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
					} // if
					
					if(j<8 && i>24 && i<31) {
						if(i<27 &&j>5) {
							
						}else {
					
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251 
								+ j * 32 + 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024- 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
						} // if
					} // if
					
					if(j<5 && i>22 && i<25) {

						if(j>2) {
							Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251 
									+ j * 32 + 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
									backgroundY - 64 - 515 - 1024 - 1024- 1024 + 64 + i*32 -128 + 384 + (64*16) - 32);
							entities.add(grassTile);
							grass.add(grassTile);
						} // if
					} // if

					if(j<6 && i>22 && i<27) {

						if(j>2) {
							Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
									+ j * 32 + 644 - 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
									backgroundY - 64 - 515 - 1024 - 1024- 1024 + 64 + i*32 -128 + 384 + (64*20) - 32);
							entities.add(grassTile);
							grass.add(grassTile);
						} // if
					} // if
					
					
					if(j<7 && i>24 && i<31) {
						if(i<27 &&j>5) {
							
						}else {
					
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 + 644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46),
								backgroundY - 64 - 515 - 1024 - 1024- 1024 + 64 + i*32 -128 + 384 + (64*10) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
						} // if
					} // if
					
					
					if(j<7 && i>24 && i<29) {
					
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251 
								+ j * 32 + 644 - 138 - (46*9) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), 
								backgroundY - 64 - 515 - 1024- 1024 - 1024 + 64 + i*32 -128 + 384 + (64*10) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
	
					} // if
					
					if(j>1 && j<4 && i>22 && i<25) {
						
						Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
								+ j * 32 + 644 - 138 - (46*9) - 1794 + 746 - 10 + 2048 - 25 - 46 - (7*46), 
								backgroundY - 64 - 515 - 1024- 1024 - 1024 + 64 + i*32 -128 + 384 + (64*10) - 32);
						entities.add(grassTile);
						grass.add(grassTile);
	
					} // if
					
					
					if(i<24 && i > 21 && j == 0) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 + 644
								- 138 + (46*3) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46) , backgroundY
								- 64 - 515 - 1024 - 1024- 1024 - 64 + i*64 -64 + 384 - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(i == 22 && j == 0) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
								- 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (13*46) , backgroundY 
								- 64 - 515 - 1024 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64);
						entities.add(tree);
						obstacles.add(tree);
					} // if
			} // inner for
		} // outer for
		
		// second town
		for(int i = 0; i < 32; i++) {
			for(int j = 0; j < 32; j++) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138 
							+ (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) , backgroundY - 64 - 515
							- 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 -(10*64));
					entities.add(tree);
					obstacles.add(tree);
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*4) - 1794 + 746 - 10 + 2048 - 25 , backgroundY - 64 - 515 - 2048 - 1024 - 1024
							- 64 + i*64 -64 + 384 - 64 -(10*64));
					entities.add(tree);
					obstacles.add(tree);
					if(j==1 && i != 25 && i != 24 && i > 16) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
								+ (46*2) - 1794 + 746 - 10 + 2048 - 25 , backgroundY + 64 - 515 - 2048 - 1024
								- 1024+ i*64+ 384);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==1 && i == 17) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
								+ (46*1) - 1794 + 746 - 10 + 2048 - 25 , backgroundY + 64 - 515 - 2048 - 1024
								- 1024+ i*64+ 384);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==1 && i == 31) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
								+ (46*1) - 1794 + 746 - 10 + 2048 - 25 , backgroundY + 64 - 515 - 2048 - 1024
								- 1024+ i*64+ 384);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==1 && i == 17) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
								- 138  - (46*17) - 1794 + 746 - 10 + 2048 - 25 , backgroundY + 64 - 515 - 2048
								- 1024 - 1024+ i*64+ 384);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==1 && i == 31) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 
								- 138 - (46*17) - 1794 + 746 - 10 + 2048 - 25 , backgroundY + 64 - 515 - 2048
								- 1024 - 1024+ i*64+ 384);
						entities.add(tree);
						obstacles.add(tree);
					} // if
					
					if(j==9 && i<15 && i !=7 && i != 8) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
								- 138 - (46*27) - 1794 + 746 - 10 + 2048 - 25 , backgroundY - 64 - 515 - 2048
								- 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
						entities.add(tree);
						obstacles.add(tree);
					} // if
					if(j==9 && i<15 && i !=7 && i != 8) {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
								- 138 - (46*26) - 1794 + 746 - 10 + 2048 - 25 , backgroundY - 64 - 515 - 2048
								- 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
						entities.add(tree);
						obstacles.add(tree);
					} // if
				
			} // inner for
		} // outer for
		
		// route between second and third towns
		for(int i = 0; i<32; i++) {
			for(int j = 0; j<35; j++) {
				tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
						+ (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
						- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 -(10*64));
				entities.add(tree);
				obstacles.add(tree);
				tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
						+ (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
						- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(37*64));
				entities.add(tree);
				obstacles.add(tree);
				
				if(i==31&&j>14) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==30&&j>20) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==29&&j>20) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==28&&j>24) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==27&&j>28) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==26&&j>32) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				
				if(i==17&&j>16&&j<23) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==20&&j>15&&j<22) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==21&&j>16&&j<23) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==22&&j>16&&j<23) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				}if(i==23&&j>17&&j<24) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
							+ (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
							- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i>20&&j==5||i>20&&j==4) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
							- 138 + (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
							backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i>23&&j==3) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
							- 138 + (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
							backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i>26&&j==2) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
							- 138 + (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
							backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i>23&&j==6) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
							- 138 + (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
							backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i>26&&j==7) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
							- 138 + (46*13) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
							backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i>23&&j<=6) {
					if(j==5&&i>24 ||j==6&&i>24) {
						
					}else {
						tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
								- 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
								backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
						entities.add(tree);
						obstacles.add(tree);
					} // if
				} // if
				if(j==0&&i==19||j==1&&i==19) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
							- 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
							backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(j==0 && i==20) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
							- 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
							backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(j>=0&&j<=6&&i==17 || j>=0&&j<=6&&i==18) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
							- 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46),
							backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(5*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				
				
				if(j>2&&j<13 && i>28 && i<31) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32 -64 + 384
							- 96 +(10*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				if(j>9&&j<14 && i>24 && i<29) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32 -64 + 384
							- 96 +(10*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>6&&j<16 && i<=31&& i>=22) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32 -64 + 384
							- 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				if(j>6&&j<10 && i>=18&& i<=22) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32 -64 + 384
							- 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>21&&j<31 && i>=13&& i<=15) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32 -64 + 384
							- 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				if(j>19&&j<31 && i==12) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46) + (32*4), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32
							-64 + 384 - 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>19&&j<31 && i>=4 && i<= 7) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (28*46) + (32*5), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32
							-64 + 384 - 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>21&&j<33 && i<=11&& i>=10) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46) + (32*4), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32
							-64 + 384 - 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>18&&j<23 && i<=19&& i>=18) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46) + (32*4), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32
							-64 + 384 - 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				if(j>18&&j<21 && i<=21&& i>=20) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46)
							- (35*46) + (32*4), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32
							-64 + 384 - 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>19&&j<30 && i<=21&& i>=20) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 755 + 2048 - 25 - (7*46) - (24*46)
							- (35*46) + (32*4), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32
							-64 + 384 - 64 +(26*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				if(j>19&&j<36 && i<=19&& i>=17) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX - 251
							+ j * 32 +  644 - 138 + (46*4) - 1794 + 755 + 2048 - 25 - (7*46) - (24*46)
							- (35*46) + (32*4), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*32
							-64 + 384 - 64 +(26*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j==19 && i==18) {
					
					Entity cliff = new obstacleEntity(this, "images/cliff.png", backgroundX - 251
							+ j *32 +  644 - 138 - (46*5) - 1794 + 746 - 10 + 2048 - 25 - (7*46)
							- (24*46) - (35*46) + (32*4), backgroundY - 64 - 515 - 2048 - 1024
							- 1024 - 64 + i*32 -64 + 384 - 64 +(21*64));
					entities.add(cliff);
					cliffs.add(cliff);

				} // if
				
				if(j>19&&j<32 && i<=16&& i>=15) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX 
							- 251 + j * 32 +  644 - 138 + (46*4) - 1794 + 755 + 2048 - 25 - (7*46)
							- (24*46) - (35*46) + (32*4), backgroundY - 64 - 515 - 2048 - 1024 
							- 1024 - 64 + i*32 -64 + 384 - 64 +(26*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>19&&j<25 && i<=14&& i>=12) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX 
							- 251 + j * 32 +  644 - 138 + (46*4) - 1794 + 755 + 2048 - 25 - (7*46
									) - (24*46) - (35*46) + (32*4), backgroundY - 64 - 515 - 2048
							- 1024 - 1024 - 64 + i*32 -64 + 384 - 64 +(26*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>18&&j<25 && i<=17&& i>=16) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX
							- 251 + j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25
							- (7*46) - (24*46) - (35*46) + (32*4), backgroundY - 64 - 515 - 2048
							- 1024 - 1024 - 64 + i*32 -64 + 384 - 64 +(21*64));
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				if(j>1&&j<13 && i>=30 &&i<=31) {
					
					Entity grassTile = new grassEntity(this, "images/tall_grass.png", backgroundX 
							- 251 + j * 32 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25
							- (7*46) - (24*46) - (35*46), backgroundY - 64 - 515 - 2048 - 1024
							- 1024 - 64 + i*32 -64 + 384 - 96 +(10*64)+32);
					entities.add(grassTile);
					grass.add(grassTile);

				} // if
				
				
				if(i==0&&j==27) {
					Entity water = new obstacleEntity(this, "images/water/topleft.png", backgroundX
							- 251 - 24 + j * 64 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048
							- 25 - (7*46) - (24*46) - (35*46), backgroundY - 64 - 515 - 2048
							- 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				} // if
				if(i>0&&i<4&&j==27) {
					if(i==3) {
						Entity water = new obstacleEntity(this, "images/water/bottomleft.png",
								backgroundX - 251 - 24 + j * 64 +  644 - 138 + (46*4) - 1794
								+ 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY
								- 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
						entities.add(water);
						obstacles.add(water);
					}else {
						Entity water = new obstacleEntity(this, "images/water/left.png", backgroundX
								- 251 - 24 + j * 64 +  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048
								- 25 - (7*46) - (24*46) - (35*46), backgroundY - 64 - 515 - 2048
								- 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
						entities.add(water);
						obstacles.add(water);
					} // if
				} // if
				if(i==0&&j==28) {
					Entity water = new obstacleEntity(this, "images/water/top.png", backgroundX
							- 251 + j * 64 - 24+  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048
							- 25 - (7*46) - (24*46) - (35*46), backgroundY - 64 - 515 - 2048
							- 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				} // if
				if(i==0&&j==29) {
					Entity water = new obstacleEntity(this, "images/water/top.png", backgroundX 
							- 251 + j * 64 - 24+  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 
							- 25 - (7*46) - (24*46) - (35*46), backgroundY - 64 - 515 - 2048 
							- 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				} // if
				if(i==0&&j==30) {
					Entity water = new obstacleEntity(this, "images/water/topright.png", backgroundX
							- 251 + j * 64 - 24+  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25
							- (7*46) - (24*46) - (35*46), backgroundY - 64 - 515 - 2048 - 1024
							- 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				} // if
				if(i==0&&j==31) {
					tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 64
							- 24+  644 - 138 + (46*4) - 1794 + 746  + 2048 - 19 - (7*46) - (24*46)
							- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64
							+ 384 - 64 +(22*64));
					entities.add(tree);
					obstacles.add(tree);
				} // if
				if(i==1&&j==31) {
					Entity water = new obstacleEntity(this, "images/water/topright.png", backgroundX
							- 253 + j * 64 - 24+  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25
							- (7*46) - (24*46) - (35*46), backgroundY - 66 - 515 - 2048 - 1024 - 1024
							- 64 + i*64 -64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				} // if
				if(i>=1&&i<=2&&j<=30&&j>=28) {
					Entity water = new obstacleEntity(this, "images/water/water.png", backgroundX 
							- 253 + j * 64 - 24+  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 
							- (7*46) - (24*46) - (35*46), backgroundY - 66 - 515 - 2048 - 1024 - 1024
							- 64 + i*64 -64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				} // if
				if(i==3&&j<=30&&j>=28) {
					Entity water = new obstacleEntity(this, "images/water/bottom.png", backgroundX
							- 253 + j * 64 - 24+  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25
							- (7*46) - (24*46) - (35*46), backgroundY - 66 - 515 - 2048 - 1024 - 1024
							- 64 + i*64 -64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				} // if
				if(i==2&&j==31) {
					Entity water = new obstacleEntity(this, "images/water/right.png", backgroundX - 253
							+ j * 64 - 24+  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46)
							- (24*46) - (35*46), backgroundY - 66 - 515 - 2048 - 1024 - 1024 - 64 + i*64
							-64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				}
				if(i==3&&j==31) {
					Entity water = new obstacleEntity(this, "images/water/bottomright.png", backgroundX - 253 + j * 64 - 24+  644 - 138 + (46*4) - 1794 + 746 - 10 + 2048 - 25 - (7*46) - (24*46) - (35*46), backgroundY - 66 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(22*64));
					entities.add(water);
					obstacles.add(water);
				} // if
			} // inner for
		} // outer for
		
	// third town
	for(int i=0; i<32;i++) {
		for(int j = 0; j<=35; j++) {
			tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
					+ (46*4) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) - (35*46), backgroundY - 64
					- 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 -(18*64));
			entities.add(tree);
			obstacles.add(tree);
			
			tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
					+ (46*4) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) - (35*46), backgroundY - 64
					- 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 -(13*64)+(24*64)+(28*64));
			entities.add(tree);
			obstacles.add(tree);
			
			tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644 - 138
					+ (46*4) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) - (35*46)- (35*46), 
					backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 -(17*64)
					+(24*64));
			entities.add(tree);
			obstacles.add(tree);
			
			tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
					- 138 + (46*4) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) - (35*46)- (35*46),
					backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 -(17*64)
					+(34*64));
			entities.add(tree);
			obstacles.add(tree);
			
			if(j==35 && i >= 20) {
				tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
						- 138 + (46*4) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) - (35*46), 
						backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 -(6*64));
				entities.add(tree);
				obstacles.add(tree);
			}
			if(j==34 && i >= 25) {
				tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
						- 138 + (46*4) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) - (35*46), 
						backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 -(11*64));
				entities.add(tree);
				obstacles.add(tree);
			}
			
			if(j>=34 && i >= 22) {
				tree = new obstacleEntity(this, "images/tree.png", backgroundX - 251 + j * 46 +  644
						- 138 + (46*4) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) - (35*46), 
						backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64 +(7*64));
				entities.add(tree);
				obstacles.add(tree);
			}
			
			
			if(j==23 && i == 26) {
				Entity water = new obstacleEntity(this, "images/water/topleft.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) 
						- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			
			if(j==23 && i >= 27&& i <= 29) {
				Entity water = new obstacleEntity(this, "images/water/left.png", backgroundX - 251 
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 
						+ 384 - 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			if(j==23 && i == 30) {
				Entity water = new obstacleEntity(this, "images/water/bottomleft.png", backgroundX
						- 251 + j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 - 64
						-(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			if(j>=24 &&j<=29 && i == 30) {
				Entity water = new obstacleEntity(this, "images/water/bottom.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			if(j==30 && i == 30) {
				Entity water = new obstacleEntity(this, "images/water/bottomright.png", backgroundX
						- 251 + j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46)
						- (24*46) - (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64
						+ i*64 -64 + 384 - 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			if(j==30 && i <= 29&& i >= 26) {
				Entity water = new obstacleEntity(this, "images/water/right.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			if(j==30 && i == 25) {
				Entity water = new obstacleEntity(this, "images/water/topright.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 62 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			
			if(j>=24 &&j<=29 && i >= 27&& i <= 29) {
				Entity water = new obstacleEntity(this, "images/water/water.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 62 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			
			if(j>=24 &&j<=29 && i >= 27&& i <= 29) {
				Entity water = new obstacleEntity(this, "images/water/water.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384 
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			
			if(j==26 && i == 25) {
				Entity water = new obstacleEntity(this, "images/water/topleft.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 62 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			if(j>=24 &&j<=25 && i == 26) {
				Entity water = new obstacleEntity(this, "images/water/top.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 64 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 
						+ 384 - 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			if(j>=27 &&j<=29 && i == 25) {
				Entity water = new obstacleEntity(this, "images/water/top.png", backgroundX - 251
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46)
						- (35*46), backgroundY - 62 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			if(j>=26 &&j<=29 && i == 26) {
				Entity water = new obstacleEntity(this, "images/water/water.png", backgroundX - 251 
						+ j * 64 +  644 - 138 - (46*5) - 1794 - 10 + 2048 - 15 - (27*46) - (24*46) 
						- (35*46), backgroundY - 62 - 515 - 2048 - 1024 - 1024 - 64 + i*64 -64 + 384
						- 64 -(11*64));
				entities.add(water);
				obstacles.add(water);
			}
			
			if(j==0 && i == 0) {
				Entity water = new obstacleEntity(this, "images/water/topleft.png", backgroundX + j*64
						- 2700 - 46*3 - 64*6, backgroundY + 300 - 3062 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i==0 && j <= 4&& j > 0) {
				Entity water = new obstacleEntity(this, "images/water/top.png", backgroundX + j*64
						- 2700 - 46*3 - 64*6, backgroundY + 300 - 3062 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i==0 && j == 5) {
				Entity water = new obstacleEntity(this, "images/water/topright.png", backgroundX + j*64
						- 2700 - 46*3 - 64*6, backgroundY + 300 - 3062 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i>=1 && i<=12 &&j == 0) {
				Entity water = new obstacleEntity(this, "images/water/left.png", backgroundX + j*64 
						- 2700 - 46*3 - 64*6, backgroundY + 300 - 3062 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i>=1 && i<=9 &&j == 5) {
				Entity water = new obstacleEntity(this, "images/water/right.png", backgroundX + j*64
						- 2700 - 46*3 - 64*6, backgroundY + 300 - 3062 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i==13 &&j == 0) {
				Entity water = new obstacleEntity(this, "images/water/bottomleft.png", backgroundX
						+ j*64 - 2700 - 46*3 - 64*6, backgroundY + 300 - 3062 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i==10 &&j == 5) {
				Entity water = new obstacleEntity(this, "images/water/water.png", backgroundX 
						+ j*64 - 2700 - 46*3 - 64*6, backgroundY + 300 - 3062 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i==10 &&j >= 6&&j <= 9) {
				Entity water = new obstacleEntity(this, "images/water/top.png", backgroundX +
						j*64 - 2702 - 46*3 - 64*6, backgroundY + 300 - 3064 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i==10 &&j == 10) {
				Entity water = new obstacleEntity(this, "images/water/topright.png", backgroundX + j*64
						- 2702 - 46*3 - 64*6, backgroundY + 300 - 3064 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i>=11 &&i<=12 &&j == 10) {
				Entity water = new obstacleEntity(this, "images/water/right.png", backgroundX + j*64
						- 2702 - 46*3 - 64*6, backgroundY + 300 - 3064 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i==13 &&j == 10) {
				Entity water = new obstacleEntity(this, "images/water/bottomright.png", backgroundX
						+ j*64 - 2702 - 46*3 - 64*6, backgroundY + 300 - 3064 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i==13 &&j < 10&&j > 0) {
				Entity water = new obstacleEntity(this, "images/water/bottom.png", backgroundX
						+ j*64 - 2702 - 46*3 - 64*6, backgroundY + 300 - 3064 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i>=1 &&i<=12 &&j > 0&&j < 5) {
				Entity water = new obstacleEntity(this, "images/water/water.png", backgroundX + j*64
						- 2702 - 46*3 - 64*6, backgroundY + 300 - 3064 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i>=1 &&i<=12 &&j > 0&&j < 6) {
				Entity water = new obstacleEntity(this, "images/water/water.png", backgroundX + j*64
						- 2706 - 46*3 - 64*6, backgroundY + 300 - 3064 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			if(i>=11 &&i<=12 &&j > 4&&j < 11) {
				Entity water = new obstacleEntity(this, "images/water/water.png", backgroundX + j*64
						- 2708 - 46*3 - 64*6, backgroundY + 300 - 3064 - (2*64) +i*64);
				entities.add(water);
				obstacles.add(water);
			}
			
			
			
		}
	}
		
		
	} // initTrees

	public void initRoads() {
		Entity road;
		
		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 2; i++) {
				road = new tileEntity(this, "images/road.png", backgroundX - 1920 + i*64 +64,
						backgroundY + 300 - 3072 - (3*64)+j*64);
				entities.add(road);
			} // inner for
		} // outer for
		
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 9; i++) {
				road = new tileEntity(this, "images/road.png", backgroundX - 1920 + i*64 -64*6, 
						backgroundY + 300 - 3072 + (2*64)+j*64);
				entities.add(road);
			} // inner for
		} // outer for
		
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 2; i++) {
				road = new tileEntity(this, "images/road.png", backgroundX - 1920 + i*64 -64*6, 
						backgroundY + 300 - 3072 + (4*64)+j*64);
				entities.add(road);
			} // inner for
		} // outer for
		
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 2; i++) {
				road = new tileEntity(this, "images/road.png", backgroundX - 1920 + i*64 -64*4, 
						backgroundY + 300 - 3072 + (10*64)+j*64);
				entities.add(road);
			} // inner for
		} // outer for
		
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 3; i++) {
				road = new tileEntity(this, "images/road.png", backgroundX - 1920 + i*64 -64*9, 
						backgroundY + 300 - 3072 + (2*64)+j*64);
				entities.add(road);
			} // inner for
		} // outer for
		
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 2; i++) {
				road = new tileEntity(this, "images/road.png", backgroundX - 1920 + i*64 -64*9,
						backgroundY + 300 - 3072 - (8*64)+j*64);
				entities.add(road);
			} // inner for
		} // outer for
		
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 9; i++) {
				road = new tileEntity(this, "images/road.png", backgroundX - 1920 + i*64 -64*18,
						backgroundY + 300 - 3072 - (7*64)+j*64);
				entities.add(road);
			} // inner for
		} // outer for
		
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 2; i++) {
				road = new tileEntity(this, "images/road.png", backgroundX - 1920 + i*64 -64*18,
						backgroundY + 300 - 3072 - (10*64)+j*64);
				entities.add(road);
			} // inner for
		} // outer for
		
		
	} // initRoads


	
	
	public void initRoom1() {
		background = new backgroundEntity(this, "images/labfloortiles.png", 10000, 10000);
		entities.add(background);
	
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/housetile.png", 571 + i * 30,
						200 + j*30);
				entities.add(backgroundTile);
			} // inner for
		} // outer for
		
		Entity door = new doorEntity(this, "images/door.png", 925, 505);
		entities.add(door);
		doors.add(door);
		Entity mat = new tileEntity(this, "images/floormat.png", 925, 465);
		entities.add(mat);
		Entity rug = new tileEntity(this, "images/rug.png", 625, 345);
		entities.add(rug);
		Entity table = new obstacleEntity(this, "images/diner.png", 605, 345);
		entities.add(table);
		obstacles.add(table);
		Entity fridge = new obstacleEntity(this, "images/fridge.png", 571, 200);
		entities.add(fridge);
		obstacles.add(fridge);

	} // initRoom1
	
	public void initGym() {
		
		background = new backgroundEntity(this, "images/house1.png", 835, 275);
		entities.add(background);
		
		for (int i = 0; i < 22; i++) {
			for (int j = 0; j < 35; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/gymtile.png", 571 + i * 32,
						200 + j*32 - 25*32);
				entities.add(backgroundTile);
			} // inner for
		} // outer for
		
		
		Entity front = new obstacleEntity(this, "images/gym/singlefront.png", 571, 200 -32);
		entities.add(front);
		obstacles.add(front);
		Entity back = new obstacleEntity(this, "images/gym/singleback.png", 571, 200 - 21*32);
		entities.add(back);
		obstacles.add(back);
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 35; j++) {
				if(i==0&&j<22&&j>3) {
					Entity middle = new obstacleEntity(this, "images/gym/singlemiddle.png", 571 + i * 32, 200 + j*32 - 25*32 + 12);
					entities.add(middle);
					obstacles.add(middle);
				} // if
			} // for
		} // for
		
		Entity topleft = new obstacleEntity(this, "images/gym/topleft.png", 571 + 4*32, 92 -4*32);
		entities.add(topleft);
		obstacles.add(topleft);
		
		Entity topright = new obstacleEntity(this, "images/gym/topright.png", 571 + 6*32, 92 -4*32);
		entities.add(topright);
		obstacles.add(topright);
		
		topright = new obstacleEntity(this, "images/gym/topright.png", 571 + 8*32, 228 -4*32);
		entities.add(topright);
		obstacles.add(topright);
		
		Entity right = new obstacleEntity(this, "images/gym/right.png", 571 + 6*32, 168 -4*32);
		entities.add(right);
		obstacles.add(right);
		right = new obstacleEntity(this, "images/gym/right.png", 571 + 6*32, 108 -4*32);
		entities.add(right);
		obstacles.add(right);
		right = new obstacleEntity(this, "images/gym/right.png", 571 + 8*32, 242 -4*32);
		entities.add(right);
		obstacles.add(right);
		
		Entity full = new obstacleEntity(this, "images/gym/full.png", 571 + 6*32, 232 -4*32);
		entities.add(full);
		obstacles.add(full);
		
	
		
		Entity left = new obstacleEntity(this, "images/gym/left.png", 571 + 4*32, 228 -4*32);
		entities.add(left);
		obstacles.add(left);
		
		left = new obstacleEntity(this, "images/gym/left.png", 571 + 4*32, 168 -4*32);
		entities.add(left);
		obstacles.add(left);
		
		left = new obstacleEntity(this, "images/gym/left.png", 571 + 4*32, 108 -4*32);
		entities.add(left);
		obstacles.add(left);
		
		Entity bottomleft = new obstacleEntity(this, "images/gym/bottomleft.png", 571 + 4*32, 200 -32);
		entities.add(bottomleft);
		obstacles.add(bottomleft);
		
		Entity bottom = new obstacleEntity(this, "images/gym/bottom.png", 571 + 6*32, 196 -28);
		entities.add(bottom);
		obstacles.add(bottom);
		
		Entity bottomright = new obstacleEntity(this, "images/gym/bottomright.png", 571 + 8*32, 200 -32);
		entities.add(bottomright);
		obstacles.add(bottomright);
		
		bottomright = new obstacleEntity(this, "images/gym/bottomright.png", 571 + 14*32, 200 -32);
		entities.add(bottomright);
		obstacles.add(bottomright);
		
		bottomleft = new obstacleEntity(this, "images/gym/bottomleft.png", 571 + 12*32, 200 -32);
		entities.add(bottomleft);
		obstacles.add(bottomleft);
		
		topleft = new obstacleEntity(this, "images/gym/singletopleft.png", 571 + 12*32, 168 -36);
		entities.add(topleft);
		obstacles.add(topleft);
		
		topright = new obstacleEntity(this, "images/gym/singletopright.png", 571 + 14*32, 168 -36);
		entities.add(topright);
		obstacles.add(topright);
		
		
		bottomright = new obstacleEntity(this, "images/gym/bottomright.png", 571 + 20*32, 200 -32);
		entities.add(bottomright);
		for (int i = 0; i < 22; i++) {
			for (int j = 0; j < 35; j++) {
				if(i==21&&j<22&&j>3) {
					right = new obstacleEntity(this, "images/gym/right.png", 571 + i*32-32, 200 + j*32 - 24*32 -4);
					entities.add(right);
					obstacles.add(right);
				} // if
			} // for
		} // for
		topright = new obstacleEntity(this, "images/gym/singletopright.png", 571 + 21*32-32, 200 - 21*32);
		entities.add(topright);
		obstacles.add(full);
		
		for (int i = 0; i < 22; i++) {
			for (int j = 0; j < 35; j++) {
				if(i<=19&&i>=6&&j==9) {
					Entity top = new obstacleEntity(this, "images/gym/top.png",571 + i*32-32, 200 + j*32 - 30*32);
					entities.add(top);
					obstacles.add(top);
				} // if
			} // for
		} // for
		
		topleft = new obstacleEntity(this, "images/gym/singletopleft.png", 571 + 5*32-32, 200 - 21*32);
		entities.add(topleft);
		obstacles.add(topleft);
		
		left = new obstacleEntity(this, "images/gym/left.png", 571 + 5*32-32, 200 - 20*32);
		entities.add(left);
		obstacles.add(left);
		
		bottomleft = new obstacleEntity(this, "images/gym/bottomleft.png", 571 + 5*32-32, 200 - 19*32);
		entities.add(bottomleft);
		obstacles.add(bottomleft);
		
		for (int i = 0; i < 22; i++) {
			for (int j = 0; j < 35; j++) {
				if(i<=11&&i>=6&&j==19) {
					bottom = new obstacleEntity(this, "images/gym/bottom.png", 571 + i*64-6*32, 200 - j*32);
					entities.add(bottom);
					obstacles.add(bottom);
				} // if
			} // for
		} // for
		
		for (int i = 0; i < 22; i++) {
			for (int j = 0; j < 35; j++) {
				if(i==12&&j>=18&&j<=22) {
					left = new obstacleEntity(this, "images/gym/left.png",571 + i*64-6*32, 576 - j*68+16*32);
					entities.add(left);
					obstacles.add(left);
				} // if
			} // for
		} // for
		
		for (int i = 0; i < 22; i++) {
			for (int j = 0; j < 35; j++) {
				if(i<=17&&i>=12&&j==9) {
					Entity top = new obstacleEntity(this, "images/gym/top.png",571 + i*32-32, 176 + j*32 - 17*32);
					entities.add(top);
					obstacles.add(top);
				} // if
			} // for
		} // for
		topleft = new obstacleEntity(this, "images/gym/singletopleft.png",571 + 11*32-32, 176 + 9*32 - 17*32);
		entities.add(topleft);
		obstacles.add(topleft);
		
		bottomleft = new obstacleEntity(this, "images/gym/bottomleft.png",571 + 11*32-32, 176 + 10*32 - 17*32);
		entities.add(bottomleft);
		obstacles.add(bottomleft);
		
		left = new obstacleEntity(this, "images/gym/left.png",571 + 13*32-32, 150 + 11*32 - 17*32);
		entities.add(left);
		obstacles.add(left);
		
		bottomleft = new obstacleEntity(this, "images/gym/bottomleft.png",571 + 13*32-32, 170 + 12*32 - 17*32);
		entities.add(bottomleft);
		obstacles.add(bottomleft);
		
		bottom = new obstacleEntity(this, "images/gym/bottom.png",571 + 15*32-32, 170 + 12*32 - 17*32);
		entities.add(bottom);
		obstacles.add(bottom);
		
		bottomright = new obstacleEntity(this, "images/gym/bottomright.png",571 + 17*32-32, 170 + 12*32 - 17*32);
		entities.add(bottomright);
		obstacles.add(bottomright);
		
		left = new obstacleEntity(this, "images/gym/left.png",571 + 19*32-32, 138 + 13*32 - 17*32);
		entities.add(left);
		obstacles.add(left);
		
		left = new obstacleEntity(this, "images/gym/left.png",571 + 19*32-32, 138 + 15*32 - 17*32);
		entities.add(left);
		obstacles.add(left);
		
		left = new obstacleEntity(this, "images/gym/left.png",571 + 19*32-32, 138 + 17*32 - 17*32);
		entities.add(left);
		obstacles.add(left);
		
		bottomleft = new obstacleEntity(this, "images/gym/bottomleft.png", 571 + 18*32, 200 -32);
		entities.add(bottomleft);
		obstacles.add(bottomleft);
		
		full = new obstacleEntity(this, "images/gym/full.png", 571 + 18*32, 220 -9*32);
		entities.add(full);
		obstacles.add(full);
		
		full = new obstacleEntity(this, "images/gym/full.png", 571 + 18*32, 234 -9*32);
		entities.add(full);
		obstacles.add(full);
		
		full = new obstacleEntity(this, "images/gym/full.png", 571 + 16*32, 234 -9*32);
		entities.add(full);
		obstacles.add(full);
		
		full = new obstacleEntity(this, "images/gym/full.png", 571 + 14*32, 234 -9*32);
		entities.add(full);
		obstacles.add(full);
		
		full = new obstacleEntity(this, "images/gym/full.png", 571 + 16*32, 220 -9*32);
		entities.add(full);
		obstacles.add(full);
		
		full = new obstacleEntity(this, "images/gym/full.png", 571 + 14*32, 220 -9*32);
		entities.add(full);
		obstacles.add(full);
		
		full = new obstacleEntity(this, "images/gym/boi.png", 571 + 12*32, 220 -9*32);
		entities.add(full);
		obstacles.add(full);
		
		full = new obstacleEntity(this, "images/gym/yeet.png", 571 + 6*32, 244 -22*32);
		entities.add(full);
		obstacles.add(full);
		
		
		bottomleft = new obstacleEntity(this, "images/gym/bottomleft.png", 571 + 12*32, 200 -32*13);
		entities.add(bottomleft);
		obstacles.add(bottomleft);
		
		left = new obstacleEntity(this, "images/gym/left.png", 571 + 12*32, 220 -32*15);
		entities.add(left);
		obstacles.add(left);
		
		topleft = new obstacleEntity(this, "images/gym/singletopleft.png", 571 + 12*32, 200 -32*15);
		entities.add(topleft);
		obstacles.add(topleft);
		
		bottomright = new obstacleEntity(this, "images/gym/bottomright.png", 571 + 14*32, 200 -32*13);
		entities.add(bottomright);
		obstacles.add(bottomright);
		
		right = new obstacleEntity(this, "images/gym/right.png", 571 + 14*32, 220 -32*15);
		entities.add(right);
		obstacles.add(right);
		
		topright = new obstacleEntity(this, "images/gym/singletopright.png", 571 + 14*32, 200 -32*15);
		entities.add(topright);
		obstacles.add(topright);
	
	
		bottomleft = new obstacleEntity(this, "images/gym/bottomleft.png", 571 + 4*32, 200 -32*12);
		entities.add(bottomleft);
		obstacles.add(bottomleft);
		topleft = new obstacleEntity(this, "images/gym/singletopleft.png", 571 + 4*32, 200 -32*13);
		entities.add(topleft);
		obstacles.add(topleft);
		Entity top = new obstacleEntity(this, "images/gym/topboi.png", 571 + 6*32, 200 -32*13);
		entities.add(top);
		obstacles.add(topright);
		bottom = new obstacleEntity(this, "images/gym/bottom.png", 571 + 6*32, 200 -32*12);
		entities.add(bottom);
		obstacles.add(bottom);
		bottomright = new obstacleEntity(this, "images/gym/bottomright.png", 571 + 8*32, 200 -32*12);
		entities.add(bottomright);
		obstacles.add(bottomright);
		Entity middle = new obstacleEntity(this, "images/gym/singlemiddle.png", 571 + 8*32, 188 -32*15);
		entities.add(middle);
		obstacles.add(middle);
		middle = new obstacleEntity(this, "images/gym/yoy.png", 571 + 8*32, 176 -32*12);
		entities.add(middle);
		obstacles.add(middle);
		top = new obstacleEntity(this, "images/gym/singleback.png", 571 + 8*32, 204 -32*16);
		entities.add(top);
		obstacles.add(top);
		Entity platform = new tileEntity(this, "images/gym/platform.png", 571 + 6*32, -32*20);
		entities.add(platform);
		
		if (gameResult != 1 && gameResult != 2) {
			Entity trainer1 = new NPCEntity(this, "images/sprites/enemy1.png", 580 + 7*32, -32*20);
			entities.add(trainer1);
			NPCEntities.add(trainer1);
			
			Entity trainer2 = new NPCEntity(this, "images/sprites/enemy2.png", 600 + 12*32, 50 + -32*21);
			entities.add(trainer2);
			NPCEntities.add(trainer2);
		} // if
		
		
		Entity door = new doorEntity(this, "images/door.png", 925, 515);
		entities.add(door);
		doors.add(door);


	} // initRoom1
	
	public void initlab() {
		background = new backgroundEntity(this, "images/labfloortiles.png", 10000, 10000);
		entities.add(background);
		
		labBackground();
		
		
		Entity a = new tileEntity(this, "images/labfloortiles.png", 0, 0);
		entities.add(a);
		
		Entity door = new doorEntity(this, "images/door.png", 925, 510); 
		entities.add(door);
		doors.add(door);

	} // initlab
	
	public void initRoom4() {
		
		background = new backgroundEntity(this, "images/shoptiles.png", 10000, 10000);
		entities.add(background);
		
		Entity barrier = new obstacleEntity(this, "images/houseBarrier.png", 300, 100);
		entities.add(barrier);
		obstacles.add(barrier);
		
		shopBackground();
		
		
		Entity a = new tileEntity(this, "images/shoptiles.png", 0, 0);
		entities.add(a);
		
		Entity door = new doorEntity(this, "images/door.png", 925, 510); 
		entities.add(door);
		doors.add(door);
		
		Entity counter1 = new obstacleEntity(this, "images/shopcounter.png", 570, 273); 
		entities.add(counter1);
		obstacles.add(counter1);

	} // initRoom3
	public void initCenter() {
		restorePokemonHealth();
		
		background = new backgroundEntity(this, "images/centertiles.png", 10000, 10000);
		entities.add(background);
	
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/centertiles.png", 571 + i * 32,
						200 + j*32);
				entities.add(backgroundTile);
			} // inner for
		} // outer for
		
		Entity door = new doorEntity(this, "images/door.png", 925, 505);
		entities.add(door);
		doors.add(door);
	}//initcenter
	
	public void labBackground() {
		
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/labfloortiles.png",
						571 + i * 33, 177 + j*33);
				entities.add(backgroundTile);
			} // inner for
		} // outer for
		
	} // initBackground

	public void shopBackground() {
	
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				
				Entity backgroundTile = new tileEntity(this, "images/shoptiles.png",
						571 + i * 32, 177 + j*32);
				entities.add(backgroundTile);
			} // inner for
		} // outer for
	
	} // initBackground


	/*
	 * Notification from a game entity that the logic of the game should be run at
	 * the next opportunity
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	} // updateLogic

	/*
	 * Remove an entity from the game. It will no longer be moved or drawn.
	 */
	public void removeEntity(Entity entity) {
		removeEntities.add(entity);
	} // removeEntity

	// show a message. Called when player collides with a sign
	public void initSignMessage(String newMessage) {
		message = newMessage;
		message1 = "";
		message2 = "";
		message3 = "";
		message4 = "";
		
		showMessage = true;
	} // initSignMessage
	
	// displays a message. Called when user collides with a trainer. Passed the trainer's ID.
	public void initMessage(int messageNum) {
		showMessage = true;
		waitingForResponse = true;
		
		// stop player from moving
		upPressed = false;
		downPressed = false;
		leftPressed = false;
		rightPressed = false;
		
		// set the message
		if (messageNum == 1) {
			message = "Hi! I'm Trainer Rainey! Would you like to battle me?";
		} else if (messageNum == 2) {
			message = "Hi! I'm Trainer Kevin! Would you like to battle me?";
		}
		
		message2 = "1 - Yes";
		message3 = "2 - No";
		message4 = "";
	} // initMessage(int)

	// displays a message. Called when the user finds a pokemon in the grass. Passed the pokemon's name.
	public void initMessage(String pokemon) {
		showMessage = true;
		// stop playe from moving
		upPressed = false;
		downPressed = false;
		leftPressed = false;
		rightPressed = false;
		// stop player from finding another pokemon until he deals with this one

		// set variables
		grassEntity.setFoundPokemon(false);
		waitingForResponse = true;
		fightingWildPokemon = true;
		nameOfEnemyPokemon = pokemon;
		message = "You found a wild " + pokemon + "!";
		message2 = "1 - Fight";
		message3 = "2 - Run";
		message4 = "";
		showMessage = true;
	} // initMessage(String)
	

	// create an entity at player's location to check if it collides with objects
		public boolean checkCollisions(String direction) {
			boolean collided = false;
			// reset variables to false
			showMessage = false;
			fightingTrainer = false;
			inBattle = false;
			

			// create entity at right location relative to player's direction for collision checking
			if (direction.equals("up")) collisionBox = new playerEntity(this, "images/collisionBottom.png", 
					player.getX() + 5, player.getY() - 5);
			if (direction.equals("right")) collisionBox = new playerEntity(this, "images/collisionSide.png",
					player.getX() + player.getWidth() - 5, player.getY() + 5);
			if (direction.equals("left")) collisionBox = new playerEntity(this, "images/collisionSide.png",
					player.getX() - 15, player.getY() + 5);
			if (direction.equals("down")) collisionBox = new playerEntity(this, "images/collisionBottom.png",
					player.getX() + 5, player.getY() + player.getHeight() - 5);
			
			entities.add(collisionBox);
			
			// collisions (brute force but optimized)
			
			// check collisions with doors
			for (int i = 0; i < doors.size(); i++) {
				Entity other = (Entity) doors.get(i);
				if (other.collidesWith(collisionBox)) {
					other.collidedWith(collisionBox);
					
					// notify the game that player is now in a room
					inRoom = !inRoom;
					// based on the id of the door, make the right variable true
					if (((doorEntity)other).getId() == 1) {
						enterDoor1 = true;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} else if (((doorEntity)other).getId() == 2) {
						enterDoor1 = false;
						enterDoor2 = true;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} else if (((doorEntity)other).getId() == 3) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = true;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} else if (((doorEntity)other).getId() == 4) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = true;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 5) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = true;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 6) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = true;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 7) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = true;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 8) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = true;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 9) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = true;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 10) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = true;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 11) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = true;
						enterDoor12 = false;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 12) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = true;
						enterDoor13 = false;
					} // else if
					else if (((doorEntity)other).getId() == 13) {
						enterDoor1 = false;
						enterDoor2 = false;
						enterDoor3 = false;
						enterDoor4 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						enterDoor10 = false;
						enterDoor11 = false;
						enterDoor12 = false;
						enterDoor13 = true;
					} // else if
					
					// remove everything
					entities.remove(collisionBox);
					entities.removeAll(removeEntities);
					obstacles.removeAll(removeEntities);
					grass.removeAll(removeEntities);
					doors.removeAll(removeEntities);
					cliffs.removeAll(removeEntities);
					signs.removeAll(removeEntities);
					NPCEntities.removeAll(removeEntities);
					removeEntities.clear();
					
					return false;
				} // if
			} // for
			
			// player can go down cliffs but not up or across them
			for (int i = 0; i < cliffs.size(); i++) {
				Entity other = (Entity) cliffs.get(i);
				if (other.collidesWith(collisionBox) && !direction.equals("down")) {
					collided = true;
					break;
				} // if
			} // for
			
			// checks collisions with obstacles
			if (!collided) {
				for (int i = 0; i < obstacles.size(); i++) {
					Entity other = (Entity) obstacles.get(i);
					if (other.collidesWith(collisionBox)) {
						collided = true;
						break;
					} // if
				} // for
			} // if
			
			// check if collided with grass
			if (!collided) {
				for (int i = 0; i < grass.size(); i++) {
					Entity other = (Entity) grass.get(i);
					if (other.collidesWith(collisionBox)) {
						other.collidedWith(collisionBox);
						entities.remove(collisionBox);
						break;
					} // if
				} // for
				
				// check collisions with signs
				for (int i = 0; i < signs.size(); i++) {
					Entity other = (Entity) signs.get(i);
					if (other.collidesWith(collisionBox)) {
						if (!showMessage) initSignMessage(((signEntity)other).getMessage());
						break;
					} // if
				} // for
				
				// check collisions with NPC's
				for (int i = 0; i < NPCEntities.size(); i++) {
					Entity other = (Entity) NPCEntities.get(i);
					if (other.collidesWith(collisionBox)) {
						trainerNum = ((NPCEntity)(other)).getId();
						initMessage(trainerNum);
						waitingForResponse = true;
						fightingTrainer = true;
						
						entities.remove(collisionBox);
						showMessage = true;
						return true;
					} // if
				} // for
				
			} // if
			
			if (inRoom && !collided) {
				for (int i = 0; i < entities.size(); i++) {
					Entity other = (Entity) entities.get(i);
					if (other instanceof tileEntity && other.collidesWith(collisionBox)) {
						collided = true;
					} // if
				} // for
				
				collided = !collided;
			} // if
			
			// remove all relevant entities
			entities.remove(collisionBox);
			entities.removeAll(removeEntities);
			obstacles.removeAll(removeEntities);
			grass.removeAll(removeEntities);
			doors.removeAll(removeEntities);
			cliffs.removeAll(removeEntities);
			signs.removeAll(removeEntities);
			NPCEntities.removeAll(removeEntities);
			removeEntities.clear();
			
			// if player didn't collide with an obstacle, return true allowing player to move
			return collided;
		} // checkCollisions
	
	// displays 6 pokemon for the player to choose from and lets player choose one by pressing enter
	// called when player is on main screen and goes to the lab for the first time
	public void chooseStartingPokemon() {
		
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		Entity newPokemon; 
		
		// draw overlay
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 830, 1920, 250);
		
		g.setColor(Color.black);
		g.fillRect(1600, 830, 10, 250);
		g.fillRect(0, 820, 1920, 10);
		g.fillRect(0, 830, 10, 250);
		g.fillRect(1910, 830, 10, 250);
		g.fillRect(0, 1050, 1920, 10);

		// draw images of pokemon player can choose from
		newPokemon = new messageEntity(this, "images/monsterSprites/Rockitten_large.png", 50, 835);
		newPokemon.draw(g);
		
		newPokemon = new messageEntity(this, "images/monsterSprites/Budaye_large.png", 300, 835);
		newPokemon.draw(g);
		
		newPokemon = new messageEntity(this, "images/monsterSprites/Agnite_large.png", 550, 835);
		newPokemon.draw(g);
		
		newPokemon = new messageEntity(this, "images/monsterSprites/Grintot_large.png", 800, 835);
		newPokemon.draw(g);
		
		newPokemon = new messageEntity(this, "images/monsterSprites/Nudiflot_large.png", 1050, 835);
		newPokemon.draw(g);
		
		newPokemon = new messageEntity(this, "images/monsterSprites/Tweesher_large.png", 1300, 835);
		newPokemon.draw(g);
		
		// draw box around selected monster
		g.setColor(Color.DARK_GRAY);
		g.fillRect(50 + spacesRight*250, 835 , 10, 200);
		g.fillRect(50 + spacesRight*250, 835, 200, 10);
		g.fillRect(50 + spacesRight*250, 1025, 200, 10);
		g.fillRect(250 + spacesRight*250, 835, 10, 200);
		
		// display stats of the selected monster
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 30));
		g.setColor(Color.DARK_GRAY);
		g.drawString(pokemonInventory.getName(spacesRight) + ":", 1625, 855);
		g.drawString("Type: " + PokemonList.getType(pokemonInventory.getName(spacesRight)), 1625, 890);
		g.drawString("Health: " + pokemonInventory.getCurrentHealthStat(spacesRight) + " / "
				+ pokemonInventory.getHealthStat(spacesRight), 1625, 925);
		g.drawString("Attack: " + pokemonInventory.getAttackStat(spacesRight), 1625, 960);
		g.drawString("Speed: " + pokemonInventory.getSpeedStat(spacesRight), 1625, 995);
		g.drawString("Will learn " + PokemonList.getNumMoves(pokemonInventory.getName(spacesRight))
			+ " moves", 1625, 1030);
		
		// move selection 
		if (leftPressed) {
			if (spacesRight > 0) spacesRight -= 1;
		} else if (rightPressed) {
			if (spacesRight < 5) spacesRight += 1;
		} else if (enterPressed) {
			// add the pokemon to the player's inventory
			chooseStartingPokemon = false;
			pokemonInventory.removeAllPokemon();
			
			switch (spacesRight) {
			case 0: pokemonInventory.addPokemon("Rockitten");
				break;
			case 1: pokemonInventory.addPokemon("Budaye");
				break;
			case 2: pokemonInventory.addPokemon("Agnite");
				break;
			case 3: pokemonInventory.addPokemon("Grintot");
				break;
			case 4: pokemonInventory.addPokemon("Nudiflot");
				break;
			case 5: pokemonInventory.addPokemon("Tweesher");
				break;
				
			} // switch
			
			spacesRight = 0;
			spacesDown = 0;
		} // else if
		
		strategy.show();
		g.dispose();
		
		// remove the large image of the selected monster
		//entities.remove(monsterSelected);
		entities.removeAll(removeEntities);
		removeEntities.clear();
		
		// reset keys pressed
		upPressed = false;
		downPressed = false;
		leftPressed = false;
		rightPressed = false;
	} // chooseStartingPokemon
	
	// make all pokemon full health. Called when player enters a pokecenter
	public void restorePokemonHealth() {
		pokemonInventory.healAll();
	} // restorePokemonHealth
	
	public void evolvePokemon() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics(); // add the graphics
		String name = pokemonInventory.getName(evolveIndex);
		String newName = pokemonInventory.getNextEvolution(name);
		
		// if there isn't a next evolution don't evolve anything
		if (newName == "") {
			evolvePokemon = false;
			fightingPokemon = false;
			teleportFinished = false;
			Entity.setTeleport(true);
			grassEntity.setFoundPokemon(false);
			spacesRight = 0;
			spacesDown = 0;
			waitingForResponse = false;
			message1 = "";
			message2 = "";
			message3 = "";
			message4 = "";
		} else {
		
			// level up the pokemon
			pokemonInventory.levelUp(indexPokemonChosen);
			
			// add entities
			Entity backgroundBattle = new messageEntity(this, "images/backgroundEvolve.jpg", 0, 0);
			entities.add(backgroundBattle);
			Entity playerPokemon1 = new messageEntity(this, "images/monsterSprites/"
				+ name + "_large.png", 860, 150);
			Entity playerPokemon2 = new messageEntity(this, "images/monsterSprites/"
				+ newName + "_large.png", 860, 500);
			entities.add(playerPokemon1);
			entities.add(playerPokemon2);
			
			// draw everything
			backgroundBattle.draw(g);
			playerPokemon1.draw(g);
			playerPokemon2.draw(g);
			g.setColor(Color.black);
			g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 40));
			g.drawString("Your " +  name + " evolved into a " +  pokemonInventory.getName(evolveIndex)
				+ "!", 960 - g.getFontMetrics().stringWidth("Your " +  name + " evolved into a " +
				pokemonInventory.getName(evolveIndex) + "!") / 2, 150);
			
			strategy.show();
			
			// wait for player to respond
			try {Thread.sleep(2000);} catch (Exception e) {};
			
			// reset variables
			enterPressed = false;
			evolvePokemon = false;
			fightingPokemon = false;
			teleportFinished = false;
			Entity.setTeleport(true);
			grassEntity.setFoundPokemon(false);
			spacesRight = 0;
			spacesDown = 0;
			waitingForResponse = false;
			message1 = "";
			message2 = "";
			message3 = "";
			message4 = "";
		} // if else
	} // evolvePokemon();
	/*
	 * gameLoop input: none output: none purpose: Main game loop. Runs throughout
	 * game play. Responsible for the following activities: - calculates speed of
	 * the game loop to update moves - moves the game entities - draws the screen
	 * contents (entities, text) - updates game events - checks input
	 */

	public void gameLoop() {
		long lastLoopTime = 0;

		lastLoopTime = System.currentTimeMillis();
		String winner = "";
		
		startGame();
		
		// add all the pokemon that the player can use from so stats can be viewed easily
		pokemonInventory.addPokemon("Rockitten");
		pokemonInventory.addPokemon("Budaye");
		pokemonInventory.addPokemon("Agnite");
		pokemonInventory.addPokemon("Grintot");
		pokemonInventory.addPokemon("Nudiflot");
		pokemonInventory.addPokemon("Tweesher");
		
		// redraws screen multiple times per second
		while (true) {
			
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics(); // add the graphics

			// "teleport" if needed COMMENT
			if (Entity.getTeleported()) {

				if (teleportStages == 0) {
					g.setColor(Color.white);
					g.fillRect(0, 0, 1920, 1080);
					g.dispose();
					strategy.show();
					removeAllEntities();
					teleportStages++;
				} else if (teleportStages == 1) {
					removeAllEntities();
					teleportStages++;
				} else if (teleportStages == 2) {
					removeAllEntities();
					teleportStages++;
				} else if (teleportStages == 3) {
					removeAllEntities();
					doorEntity.resetId();
					NPCEntity.resetId();
					teleportStages = 0;
					Entity.setTeleported(false);
					Entity.incrementTeleportCounter();
					showMessage = false;
					if (gameResult == 1 || gameResult == 2) {
						initGym();
						displayMessage();
					} else if (Entity.getTeleportCounter() % 2 == 0 && !fightingPokemon && !fightingTrainer) {
						fightingWildPokemon = false;
						initEntities();
						grassEntity.setFoundPokemon(false);
					} else if (fightingTrainer) {
						initBattle();
					} else if (fightingPokemon) {
						entities.remove(player);
						removeEntities.clear();
						choosingPokemon = true;

						teleportFinished = true;
					} else if (enterDoor1||enterDoor3||enterDoor5||enterDoor6||enterDoor7||enterDoor8
							||enterDoor9) {
						enterDoor1 = false;
						enterDoor3 = false;
						enterDoor5 = false;
						enterDoor6 = false;
						enterDoor7 = false;
						enterDoor8 = false;
						enterDoor9 = false;
						player = new playerEntity(this, "images/standing_south.png", 925, 460);
						entities.add(player);
						Entity character = new playerEntity(this, "images/transparent.png", 925, 460);
						entities.add(character);
						initRoom1();
					} else if (enterDoor2) {
						enterDoor2 = false;
						player = new playerEntity(this, "images/standing_south.png", 925, 460);
						entities.add(player);
						Entity character = new playerEntity(this, "images/transparent.png", 925, 460);
						entities.add(character);
						initGym();
					}  else if (enterDoor4) {
						enterDoor4 = false;
						player = new playerEntity(this, "images/standing_south.png", 1255, 720);
						entities.add(player);
						Entity character = new playerEntity(this, "images/transparent.png", 925, 460);
						entities.add(character);
						initlab();
					}else if (enterDoor10 || enterDoor11) {
						enterDoor10 = false;
						enterDoor11 = false;
						player = new playerEntity(this, "images/standing_south.png", 1255, 720);
						entities.add(player);
						Entity character = new playerEntity(this, "images/transparent.png", 925, 460);
						entities.add(character);
						initCenter();
					}  // else if (background.getTeleportCounter())

				} // else if (teleportStages)

			 } else {
				g.setColor(Color.black);
				g.fillRect(0, 0, 1920, 1080);
			 } // else if (background.teleported())
			
			 if (gameResult == 1 || gameResult == 2) {
				displayMessage();
			 } // if
			 // player is choosing their first monster COMMENT
			 if (fightingPokemon && teleportFinished) {

				Color newGray = new Color(48, 48, 48);
				g.setColor(newGray);
				g.fillRect(0, 0, 1920, 1080);
				
				g.setColor(Color.white);
				g.drawString("Press \" x \" at any Time to Exit the Fight",
						(1920 - g.getFontMetrics().stringWidth("Press \" x \" at any Time to Exit the Fight"))
							/ 2, 50);

				// if the player is choosing a pokemon to use
				if (choosingPokemon) {
					
					indexPokemonChosen = displayPokemonMenu();

					g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 20));
					g.drawString("Select a pokemon by using WASD or arrow keys. Use enter to confirm your selection: ",
							(1920 - g.getFontMetrics().stringWidth("Select a pokemon by using WASD or arrow keys."
									+ " Use enter to confirm your selection: ")) / 2, 100);
					
					
					// player chose a pokemon, reset variables, move to next stage
					if (enterPressed) {
						if (pokemonInventory.getCurrentHealthStat(indexPokemonChosen) > 0) {
							justSwitched = true;
							
							removeAllEntities();
							choosingPokemon = false;
							spacesRight = 0;
							spacesDown = 0;
							inBattle = true;

							// determine who goes first based on speeds of pokemon involved
							if (pokemonInventory.getSpeedStat(indexPokemonChosen) > 
									trainerPokemon.getSpeedStat(numTrainerPokemon)) {
								isPlayerTurn = true;
							} else if (pokemonInventory.getSpeedStat(indexPokemonChosen)
									< trainerPokemon.getSpeedStat(numTrainerPokemon)) {
								isPlayerTurn = false;
							} else {
								// random
								int random = (int)(Math.random() * 2 + 1);
								if (random == 1) isPlayerTurn = true;
								if (random == 2) isPlayerTurn = false;
							} // if else (speed)
							
						} // if
					} // if (enterPressed)

				} else if (inBattle) {
					// when the player is actually fighting another pokemon or trainer
					
					effectiveness = PokemonList.getEffectiveness(pokemonInventory.getName(indexPokemonChosen),
							trainerPokemon.getName(numTrainerPokemon));
					// add entities / draw things
					Entity backgroundBattle = new messageEntity(this, "images/backgroundTest.jpg", 0, 0);
					entities.add(backgroundBattle);
					
					Entity playerPokemon = new messageEntity(this, "images/monsterSprites/"
							+ pokemonInventory.getName(indexPokemonChosen) + "_large.png", 560, 550);
					entities.add(playerPokemon);
					
					Entity enemyPokemon = new messageEntity(this, "images/monsterSprites/"
							+ trainerPokemon.getName(numTrainerPokemon) + "_large.png", 1300, 275);
					entities.add(enemyPokemon);
					
					backgroundBattle.draw(g);
					playerPokemon.draw(g);
					enemyPokemon.draw(g);
					
					g.setColor(Color.white);
					g.fillRect(0, 800, 1920, 280);
					
					
					if (justSwitched) {
						// when player switches pokemon
						message1 = "You sent out your " + pokemonInventory.getName(indexPokemonChosen)
							+ " lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen) + "!";
						
						g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25));
						g.setColor(Color.black);
						g.drawString(message1, 100, 850);
						displayHealthbars();
						strategy.show();
						
						// wait for player response
						enterPressed = false;
						try {Thread.sleep(100);} catch (Exception e) {};
						while (!enterPressed) {};
						enterPressed = false;
						
						// reset variables
						justSwitched = false;
						playerAttackChosen = false;
						enemyAttackChosen = false;
						
					} else if (winner == "player") {
						// when player wins
						// reset variables
						playerAttackChosen = false;
						enemyAttackChosen = false;
						
						if (numTrainerPokemon < 5 && !fightingWildPokemon) {
							// if the trainer still has pokemon left
							// draw things
							message1 = "Trainer " + trainerName + " sent out his "
									+ trainerPokemon.getName(numTrainerPokemon + 1) + " lvl. "
									+ trainerPokemon.getLevelStat(numTrainerPokemon + 1) + "!";
							
							g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25));
							g.setColor(Color.black);
							g.drawString(message1, 100, 850);
							
							numTrainerPokemon ++;
							strategy.show();
							
							// wait for player response
							enterPressed = false;
							try {Thread.sleep(100);} catch (Exception e) {};
							while (!enterPressed) {};
							enterPressed = false;
							
							// reset variables
							winner = "";
							isPlayerTurn = false;
							
						} else {
							// if player won and trainer has no more pokemon
							
							// add the pokemon to your inventory
							if (fightingWildPokemon) {
								message1 = "You beat & caught the wild  " + trainerPokemon.getName(0);
								pokemonInventory.addPokemon(nameOfEnemyPokemon);
								fightingWildPokemon = false;
							} else {
								message1 = "You beat " + trainerName + "!";
								gameResult = 1;
							} // else if
							
							// draw things
							g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25));
							g.setColor(Color.black);
							g.drawString(message1, 100, 850);
							displayHealthbars();
							strategy.show();
							
							// wait for player response
							enterPressed = false;
							try {Thread.sleep(100);} catch (Exception e) {};
							while (!enterPressed) {};
							enterPressed = false;
							
							// level up / evolve your pokemon
							if (pokemonInventory.getLevelStat(indexPokemonChosen) == 1
									|| pokemonInventory.getLevelStat(indexPokemonChosen) == 2) {
								evolvePokemon = true;
								evolveIndex = indexPokemonChosen;
							} else {
								for (int i = 0; i < pokemonInventory.getNumPokemon(); i++) {
									pokemonInventory.levelUp(i);
								} // for
							}
							
							// reset variables
							fightingPokemon = false;
							Entity.setTeleport(true);
							teleportFinished = false;
							waitingForResponse = false;
							enterPressed = false;
							inBattle = false;
							fightingTrainer = false;
							spacesRight = 0;
							spacesDown = 0;
							winner = "";
							
							if (evolvePokemon) evolvePokemon();
							continue;
						} // if else
						
						
					} else if (winner == "enemy") {
						// if opponent won
						
						// reset variables
						playerAttackChosen = false;
						enemyAttackChosen = false;
						
						// check if the player has any more pokemon to keep fighting
						boolean chooseNewPokemon = false;
						for (int i = 0; i < pokemonInventory.getNumPokemon(); i++) {
							if (pokemonInventory.getCurrentHealthStat(i) > 0) chooseNewPokemon = true;
						} // for
						
						if (!chooseNewPokemon) {
							// if player can't keep fighting
							
							// draw things
							message1 = "You lost to " + trainerName + "!";
							gameResult = 2;
							if (fightingWildPokemon) {
								message1 = "You lost to the wild " + trainerPokemon.getName(0);
								fightingWildPokemon = false;
								gameResult = 0;
							}
							
							g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25));
							g.setColor(Color.black);
							g.drawString(message1, 100, 850);
							

							// reset variables
							fightingPokemon = false;
							Entity.setTeleport(true);
							teleportFinished = false;
							enterPressed = false;
							inBattle = false;
							fightingTrainer = false;
							spacesRight = 0;
							spacesDown = 0;
							winner = "";
							
							displayHealthbars();
							strategy.show();
							// wait for player response
							enterPressed = false;
							try {Thread.sleep(100);} catch (Exception e) {};
							while (!enterPressed) {};
							enterPressed = false;
					
							spacesRight = 0;
							spacesDown = 0;
							winner = "";
							continue;
						} else {
							// if player can keep fighting
							
							// change stages
							choosingPokemon = true;
							inBattle = false;
							playerAttackChosen = false;
							
							// draw things
							g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25));
							g.setColor(Color.black);
							g.drawString("Press enter to choose another pokemon or press \" x \" to exit the fight ",
									100, 850);
							
							playerPokemon = new messageEntity(this, "images/monsterSprites/"
									+ pokemonInventory.getName(indexPokemonChosen) + "_large.png", 560, 550);
							entities.add(playerPokemon);
							
							enemyPokemon = new messageEntity(this, "images/monsterSprites/"
									+ trainerPokemon.getName(numTrainerPokemon) + "_large.png", 1300, 275);
							entities.add(enemyPokemon);
							
							playerPokemon.draw(g);
							enemyPokemon.draw(g);
							displayHealthbars();
							strategy.show();
							
							
							// wait for player to choose an option
							enterPressed = false;
							try {Thread.sleep(100);} catch (Exception e) {};
							while (!enterPressed && !xPressed) {};
							enterPressed = false;
							xPressed = false;
					
							// reset variables
							spacesRight = 0;
							spacesDown = 0;
							winner = "";
							continue;
						} // if else (chooseNewPokemon)
						
					} else if (isPlayerTurn) {
						// when battling and player has not yet selected a move
						// allow selection of moves
						 if (leftPressed && spacesRight > 0) {
							spacesRight -= 1;
						} else if (rightPressed && spacesRight < 1) {
							spacesRight += 1;
						} else if (downPressed && spacesDown < 1) {
							spacesDown += 1;
						} else if (upPressed && spacesDown > 0) {
							spacesDown -= 1;
						} else if (enterPressed) {
							// only allow player to select moves that the pokemon has learned
							if (spacesRight == 0 && spacesDown == 0
									&& pokemonInventory.getCharges(indexPokemonChosen,  1) > 0) {
								playerAttackChosen = true;
								pokemonInventory.decrementCharges(indexPokemonChosen, 1);
							} else if (spacesRight == 1 && spacesDown == 0 &&
									pokemonInventory.getCharges(indexPokemonChosen,  2) > 0) {
								playerAttackChosen = true;
								pokemonInventory.decrementCharges(indexPokemonChosen, 2);
							} else if (spacesRight == 0 && spacesDown == 1 &&
									pokemonInventory.getCharges(indexPokemonChosen,  3) > 0) {
								if (PokemonList.getNumMoves(pokemonInventory.getName(indexPokemonChosen)) >= 3) {
									playerAttackChosen = true;
									pokemonInventory.decrementCharges(indexPokemonChosen, 3);
								} // if
							} else if (spacesRight == 1 && spacesDown == 1
									&& pokemonInventory.getCharges(indexPokemonChosen,  4) > 0) {
								if (PokemonList.getNumMoves(pokemonInventory.getName(indexPokemonChosen)) == 4) {
									playerAttackChosen = true;
									pokemonInventory.decrementCharges(indexPokemonChosen, 4);
								} // if
							} // if else
							
						}// if else
						
						if (playerAttackChosen) {
							// after player has hit enter and selected a move, use that move
							// use random numbers to determine if the moves hits, , subtract health
							// from the enemy, then draw relevant messages
							int random = (int)(Math.random() * 100 + 1);
							String effectivenessMessage = "";
							
							if (effectiveness == 0.5) effectivenessMessage = "It was not very effective!";
							if (effectiveness == 1.5) effectivenessMessage = "It was super effective!";
							
							
							
							if (spacesRight == 0 && spacesDown == 0) {
								
								if (random <= pokemonInventory.getAccuracy(indexPokemonChosen, 1)) {
									trainerPokemon.subtractHealthStat(numTrainerPokemon,
											(int)((double)((pokemonInventory.getPower(indexPokemonChosen, 1)
											* pokemonInventory.getAttackStat(indexPokemonChosen)/100))
											*effectiveness));
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ "  lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen)
										+ " did " + (int)((double)((pokemonInventory.getPower(indexPokemonChosen, 1)
										* pokemonInventory.getAttackStat(indexPokemonChosen)/100))*effectiveness) + " damage to the enemy " + trainerPokemon.getName(numTrainerPokemon) + "!";
								} else {
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ "  lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen)
										+ " missed!";
								} // if else
								
							} else if (spacesRight == 1 && spacesDown == 0) {
								
								if (random <= pokemonInventory.getAccuracy(indexPokemonChosen, 2)) {
									trainerPokemon.subtractHealthStat(numTrainerPokemon,(int)((double)
											((pokemonInventory.getPower(indexPokemonChosen, 2)
											* pokemonInventory.getAttackStat(indexPokemonChosen)/100))
											*effectiveness));
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ "  lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen)
										+ " did " + (int)((double)
										((pokemonInventory.getPower(indexPokemonChosen, 2)
										* pokemonInventory.getAttackStat(indexPokemonChosen)/100))
										*effectiveness) + " damage to the enemy "
										+ trainerPokemon.getName(numTrainerPokemon) + "!";
								} else {
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ "  lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen)
										+ " missed!";
								} // if else
								
								
							} else if (spacesRight == 0 && spacesDown == 1) {
								
								if (random <= pokemonInventory.getAccuracy(indexPokemonChosen, 3)) {
									trainerPokemon.subtractHealthStat(numTrainerPokemon,(int)((double)
											((pokemonInventory.getPower(indexPokemonChosen, 3)
											* pokemonInventory.getAttackStat(indexPokemonChosen)/100))
											*effectiveness));
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ "  lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen)
										+ " did " + (int)((double)
											((pokemonInventory.getPower(indexPokemonChosen, 3)
											* pokemonInventory.getAttackStat(indexPokemonChosen)/100))
											*effectiveness) + " damage to the enemy "
											+ trainerPokemon.getName(numTrainerPokemon) + "!";
								} else {
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ "  lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen)
										+ " missed!";
								} // if else
								
							} else if (spacesRight == 1 && spacesDown == 1) {
								if (random <= pokemonInventory.getAccuracy(indexPokemonChosen, 4)) {
										trainerPokemon.subtractHealthStat(numTrainerPokemon,(int)
										((double)((pokemonInventory.getPower(indexPokemonChosen, 4)
										* pokemonInventory.getAttackStat(indexPokemonChosen)/100))
										*effectiveness));
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ "  lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen)
										+ " did " + (int)((double)
										((pokemonInventory.getPower(indexPokemonChosen, 4)
										* pokemonInventory.getAttackStat(indexPokemonChosen)/100))
										*effectiveness) + " damage to the enemy "
										+ trainerPokemon.getName(numTrainerPokemon) + "!";
								} else {
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
									+ "  lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen)
									+ " missed!";
								} // if else
							} // if else
							
							g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25));
							g.setColor(Color.black);
							g.drawString(message1, 100, 850);
							g.drawString(effectivenessMessage, 100, 950);
							isPlayerTurn = false;
							
							displayHealthbars();
							strategy.show();
							
							// wait for player response
							enterPressed = false;
							try {Thread.sleep(100);} catch (Exception e) {};
							do {} while (!enterPressed);
							enterPressed = false;
							
							// if enemy has no more health, player wins
							if (trainerPokemon.getCurrentHealthStat(numTrainerPokemon) <= 0) {
								winner = "player";
							} // if
							
						} else {
							// when player is selecting a move
							
							// display moves
							g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25));
							message1 = pokemonInventory.getAttack(indexPokemonChosen, 1);
							message2 = pokemonInventory.getAttack(indexPokemonChosen, 2);
							message3 = "--------------------------";
							message4 = "--------------------------";
							
							if (PokemonList.getNumMoves(pokemonInventory.getName(indexPokemonChosen))
									>= 3) {
								message3 = pokemonInventory.getAttack(indexPokemonChosen, 3);
							} // if
							if (PokemonList.getNumMoves(pokemonInventory.getName(indexPokemonChosen))
									== 4) {
								message4 = pokemonInventory.getAttack(indexPokemonChosen, 4);
							} // if
							
							// draw all the moves and make moves green if selected and red if can't be used
							g.setColor(Color.black);
							if (spacesRight == 0 && spacesDown == 0) {
								g.setColor(Color.green); 
							} // if
							
							if (spacesRight == 0 && spacesDown == 0
									&& pokemonInventory.getCharges(indexPokemonChosen, 1) == 0) {
								g.setColor(Color.red);
							} // if
							
							g.drawString(message1, 100, 850);
							g.setColor(Color.black);
							if (spacesRight == 1 && spacesDown == 0) {
								g.setColor(Color.green);
							} // if
							
							if (spacesRight == 0 && spacesDown == 0 &&
									pokemonInventory.getCharges(indexPokemonChosen, 2) == 0) {
								g.setColor(Color.red);
							} // if
							
							g.drawString(message2, 600, 850);
							g.setColor(Color.black);
							if (spacesRight == 0 && spacesDown == 1) {
								g.setColor(Color.green);
							} // if
							
							if (spacesRight == 0 && spacesDown == 0
									&& pokemonInventory.getCharges(indexPokemonChosen, 3) == 0 && !message3.contains("-")) {
								g.setColor(Color.red);
							} // if
							
							g.drawString(message3, 100, 950);
							g.setColor(Color.black);
							if (spacesRight == 1 && spacesDown == 1) { 
								g.setColor(Color.green);
							} // if
							
							if (spacesRight == 0 && spacesDown == 0 
									&& pokemonInventory.getCharges(indexPokemonChosen, 4) == 0 && !message4.contains("-")) {
								g.setColor(Color.red);
							} // if
							
							g.drawString(message4, 600, 950);
							
							g.setColor(Color.black);
							g.fillRect(1100, 800, 10, 280);
							
							// display information about selected move
							if (spacesRight == 0 && spacesDown == 0) {
								message1 = "Has " + pokemonInventory.getAccuracy(indexPokemonChosen, 1)
									+ "% chance to do " + (int)(pokemonInventory.getPower(indexPokemonChosen, 1)
										* pokemonInventory.getAttackStat(indexPokemonChosen)/100)
									+ " damage. " + pokemonInventory.getCharges(indexPokemonChosen, 1)
									+ " / " + pokemonInventory.getMaxCharges(pokemonInventory.getAttack(indexPokemonChosen, 1))
									+ " charges left";
							} else if (spacesRight == 1 && spacesDown == 0) {
								message1 = "Has " + pokemonInventory.getAccuracy(indexPokemonChosen, 2)
									+ "% chance to do " + (int)(pokemonInventory.getPower(indexPokemonChosen, 2)
									* pokemonInventory.getAttackStat(indexPokemonChosen)/100) + " damage. "
									+ pokemonInventory.getCharges(indexPokemonChosen, 2) + " / "
									+ pokemonInventory.getMaxCharges(pokemonInventory.getAttack(indexPokemonChosen, 2))
									+ " charges left";
							} else if (spacesRight == 0 && spacesDown == 1) {
								if (PokemonList.getNumMoves(pokemonInventory.getName(indexPokemonChosen)) >= 3) {
									message1 = "Has " + pokemonInventory.getAccuracy(indexPokemonChosen, 3)
										+ "% chance to do " + (int)(pokemonInventory.getPower(indexPokemonChosen, 3)
										* pokemonInventory.getAttackStat(indexPokemonChosen)/100) + " damage. "
										+ pokemonInventory.getCharges(indexPokemonChosen, 3) + " / "
										+ pokemonInventory.getMaxCharges(pokemonInventory.getAttack(indexPokemonChosen, 3))
										+ " charges left";
								} else {
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ " hasn't learned that move yet!";
								}
							} else if (spacesRight == 1 && spacesDown == 1) {
								if (PokemonList.getNumMoves(pokemonInventory.getName(indexPokemonChosen)) == 4) {
									message1 = "Has " + pokemonInventory.getAccuracy(indexPokemonChosen, 4)
										+ "% chance to do " + (int)(pokemonInventory.getPower(indexPokemonChosen, 4)
										* pokemonInventory.getAttackStat(indexPokemonChosen)/100) + " damage. "
										+ pokemonInventory.getCharges(indexPokemonChosen, 4) + " / "
										+ pokemonInventory.getMaxCharges(pokemonInventory.getAttack(indexPokemonChosen, 4))
										+ " charges left";
								} else {
									message1 = "Your " + pokemonInventory.getName(indexPokemonChosen)
										+ " hasn't learned that move yet!";
								}
							} // if else
							
							g.drawString(message1, 1150, 850);
							
						} // if else (playerAttackChosen)
						
					} else {
						// if it is the opponent's turn, randomly select a move and use it + display it
						int random = (int)(Math.random()
								* PokemonList.getNumMoves(trainerPokemon.getName(numTrainerPokemon)) + 1);
						
						String effectivenessMessage = "";
						double enemyEffectiveness = 2.0 - effectiveness;
						if (enemyEffectiveness == 0.5) effectivenessMessage = "It was not very effective!";
						if (enemyEffectiveness == 1.5) effectivenessMessage = "It was super effective!";
						
						if (random == 1) {
							
							random = (int)(Math.random() * 100 + 1);
							if (random <= trainerPokemon.getAccuracy(numTrainerPokemon, 1)) {
								pokemonInventory.subtractHealthStat(indexPokemonChosen, (int)(((double)
										(trainerPokemon.getPower(numTrainerPokemon, 1)
										* trainerPokemon.getAttackStat(numTrainerPokemon)/100)
										*enemyEffectiveness)));
								message1 = "The enemy " + trainerPokemon.getName(numTrainerPokemon)
									+ " did " + (int)((double)(trainerPokemon.getPower(numTrainerPokemon, 1)
									* trainerPokemon.getAttackStat(numTrainerPokemon)/100)*enemyEffectiveness)
									+ " damage to your " + pokemonInventory.getName(indexPokemonChosen)
									+ " lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen) + "!";
							} else {
								message1 = "The enemy " + trainerPokemon.getName(numTrainerPokemon)
									+ " missed!";
							} // if else
							
						} else if (random == 2) {
							
							random = (int)(Math.random() * 100 + 1);
							
							if (random <= trainerPokemon.getAccuracy(numTrainerPokemon, 2)) {
								pokemonInventory.subtractHealthStat(indexPokemonChosen, (int)(((double)
										(trainerPokemon.getPower(numTrainerPokemon, 2)
										* trainerPokemon.getAttackStat(numTrainerPokemon)/100)
										*enemyEffectiveness)));
								message1 = "The enemy " + trainerPokemon.getName(numTrainerPokemon)
									+ " did " + (int)((double)(trainerPokemon.getPower(numTrainerPokemon, 2)
									* trainerPokemon.getAttackStat(numTrainerPokemon)/100)*enemyEffectiveness)
									+ " damage to your " + pokemonInventory.getName(indexPokemonChosen)
									+ " lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen) + "!";
							} else {
								message1 = "The enemy " + trainerPokemon.getName(numTrainerPokemon)
									+ " missed!";
							} // if else
							
						} else if (random == 3) {
							
							if (random <= trainerPokemon.getAccuracy(numTrainerPokemon, 3)) {
								pokemonInventory.subtractHealthStat(indexPokemonChosen, (int)(((double)
										(trainerPokemon.getPower(numTrainerPokemon, 3)
										* trainerPokemon.getAttackStat(numTrainerPokemon)/100)
										*enemyEffectiveness)));
								message1 = "The enemy " + trainerPokemon.getName(numTrainerPokemon)
									+ " did " + (int)((double)(trainerPokemon.getPower(numTrainerPokemon, 3)
									* trainerPokemon.getAttackStat(numTrainerPokemon)/100*enemyEffectiveness))
									+ " damage to your " + pokemonInventory.getName(indexPokemonChosen)
									+ " lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen) + "!";
							} else {
								message1 = "The enemy " + trainerPokemon.getName(numTrainerPokemon) + " missed!";
							} // if else
						
						} else if (random == 4) {
							
							if (random <= trainerPokemon.getAccuracy(numTrainerPokemon, 4)) {
								pokemonInventory.subtractHealthStat(indexPokemonChosen, (int)(((double)
										(trainerPokemon.getPower(numTrainerPokemon, 4)
										* trainerPokemon.getAttackStat(numTrainerPokemon)/100))
										*enemyEffectiveness));
								message1 = "The enemy " + trainerPokemon.getName(numTrainerPokemon)
									+ " did " + (int)((double)(trainerPokemon.getPower(numTrainerPokemon, 4)
									* trainerPokemon.getAttackStat(numTrainerPokemon)/100)*enemyEffectiveness)
									+ " damage to your " + pokemonInventory.getName(indexPokemonChosen)
									+ " lvl. " + pokemonInventory.getLevelStat(indexPokemonChosen) + "!";
							} else {
								message1 = "The enemy " + trainerPokemon.getName(numTrainerPokemon) + " missed!";
							} // if else
						
						} // if else
						
						// set variables
						enemyAttackChosen = true;
						isPlayerTurn = true;
						
						// display things
						g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 25));
						g.setColor(Color.black);
						g.drawString(message1, 100, 850);
						g.drawString(effectivenessMessage, 100, 950);
						displayHealthbars();
						strategy.show();
						
						// wait for player response
						enterPressed = false;
						try {Thread.sleep(100);} catch (Exception e) {};
						do {} while (!enterPressed);
						enterPressed = false;
						
						// if player's pokemon runs out of health, declare a winner
						if (pokemonInventory.getCurrentHealthStat(indexPokemonChosen) <= 0) {
							winner = "enemy";
						} // if
					} // if else (isPlayerTurn)
					
					// at the end of each loop, reset attacks chosen
					if (playerAttackChosen || enemyAttackChosen) {
						playerAttackChosen = false;
						enemyAttackChosen = false;
					} // if
					
					// show healthbars
					displayHealthbars();
					strategy.show();
				
				} // if else (fightingPokemon stages)
				
				// allow player to switch pokemon
				if (ePressed) {
					choosingPokemon = true;
					inBattle = false;
					playerAttackChosen = false;
					isPlayerTurn = false;
				} // if


				// remove dead entities
				entities.removeAll(removeEntities);
				obstacles.removeAll(removeEntities);
				grass.removeAll(removeEntities);
				bridges.removeAll(removeEntities);
				doors.removeAll(removeEntities);
				cliffs.removeAll(removeEntities);
				signs.removeAll(removeEntities);
				NPCEntities.removeAll(removeEntities);
				removeEntities.clear();;

				// run logic if required
				if (logicRequiredThisLoop) {
					for (int i = 0; i < entities.size(); i++) {
						Entity entity = (Entity) entities.get(i);
						entity.doLogic();
					} // for
					logicRequiredThisLoop = false;
				} // if

				g.dispose();
				strategy.show();

				// exit the fight
				if (xPressed) { 
					// reset variables & set teleport to true
					fightingPokemon = false;
					teleportFinished = false;
					Entity.setTeleport(true);
					grassEntity.setFoundPokemon(false);
					spacesRight = 0;
					spacesDown = 0;
					waitingForResponse = false;
					fightingTrainer = false;
					message1 = "";
					message2 = "";
					message3 = "";
					message4 = "";
				} // if 


				// remove dead entities
				entities.removeAll(removeEntities);
				removeEntities.clear();

				// run logic if required
				if (logicRequiredThisLoop) {
					for (int i = 0; i < entities.size(); i++) {
						Entity entity = (Entity) entities.get(i);
						entity.doLogic();
					} // for
					logicRequiredThisLoop = false;
				} // if

				g.dispose();
				strategy.show();

			} else {
				
				// while player is on main screen of the game
				
				// if player has found a pokemon in the grass, display a messagebox
				if (grassEntity.getFoundPokemon()) {
					initMessage(grassEntity.getPokemonName());
				} // if (foundPokemon)
				
				// add a bunch of pokemon to the inventory. For testing purposes and to make it
				// easier to win the game.
				if (zeroPressed) {
					pokemonInventory.addPokemon("Nudikill");
					pokemonInventory.addPokemon("Flambear");
					pokemonInventory.addPokemon("Drokoro");
					pokemonInventory.addPokemon("Aviator");
					pokemonInventory.addPokemon("Grintrock");
					pokemonInventory.addPokemon("Sharpfin");
					pokemonInventory.addPokemon("Pyraminx");
					pokemonInventory.addPokemon("Tigrock");
					pokemonInventory.addPokemon("Frondly");
					pokemonInventory.addPokemon("Agnidon");
				} // if
				
				// allow player to see a menu of their pokemon
				if (ePressed) {
					// toggle displayMenu when e is pressed
					displayMenu = !displayMenu;
					if (!displayMenu) {
						// let player move, reset movement keys
						moveSpeed = 64;
						waitingForResponse = false;
						upPressed = false;
						downPressed = false;
						leftPressed = false;
						rightPressed = false;
					} else {
						// stop player from moving
						moveSpeed = 0;
						waitingForResponse = true;
					} // if
					
					ePressed = false;
					sprintPressed = false;
				} else if (onePressed) {
					// initialise the fight
					fightingTrainer = true;
					waitingForResponse = false; 
					Entity.setTeleport(true);
					winner = "";
					numTrainerPokemon = 0;
					onePressed = false;
					showMessage = false;
					g.dispose();
					strategy.show();
				} else if (twoPressed) {
					// COMMENT
					waitingForResponse = false;

					// use an item
					showMessage = false;
					twoPressed = false;
					fightingWildPokemon = false;
				} // if else

				// remove dead entities if there isn't a messagebox & allow player to find pokemon
				if (!waitingForResponse && !waitingForKeyPress && !showMessage) {
					grassEntity.toggleWaiting();
					entities.removeAll(removeEntities);
					obstacles.removeAll(removeEntities);
					grass.removeAll(removeEntities);
					bridges.removeAll(removeEntities);
					doors.removeAll(removeEntities);
					cliffs.removeAll(removeEntities);
					signs.removeAll(removeEntities);
					NPCEntities.removeAll(removeEntities);
					removeEntities.clear();
				} // if

				// store background's position so it stays in same place before and after
				// teleporting. Only stores position when on main screen
				if (Entity.getTeleportCounter() % 2 == 0) {
					backgroundX = background.getX();
					backgroundY = background.getY();
				} // if
				
				// calc. time since last update, will be used to calculate
				// entities movement
				long delta = System.currentTimeMillis() - lastLoopTime;
				lastLoopTime = System.currentTimeMillis();

				// move each entity
				if (!waitingForResponse) {
					for (int i = 0; i < entities.size(); i++) {
						Entity entity = (Entity) entities.get(i);
						entity.move(delta);
					} // for
				} // if
				
				// draw all entities
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					if (entity.getX() > -200 && entity.getY() > -200 && entity.getX() < 2120
						&& entity.getY() < 1280) {
						entity.draw(g);
					}
				} // for
				
				// if necessary, let player choose their starting pokemon
				if (chooseStartingPokemon) {
					chooseStartingPokemon();
				} // if
				
				// display the pokemon menu if needed. This has to go after entities are drawn so that 
				// the menu gets drawn in front of everything else.
				if (displayMenu) {
					g.setColor(Color.white);
					g.fillRect(400, 200, 1120, 680);
					
					displayPokemonMenu();
				} // if
			
				strategy.show();


				// run logic if required
				if (logicRequiredThisLoop) {
					for (int i = 0; i < entities.size(); i++) {
						Entity entity = (Entity) entities.get(i);
						entity.doLogic();
					} // for
					logicRequiredThisLoop = false;
				} // if

				// show message if needed, otherwise remove the messagebox
				if (showMessage ) {
					messageBox = new obstacleEntity(this, "images/textbox.png", 633, 535);
					messageBox.draw(g);
					g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 20));
					g.setColor(Color.white);
					g.drawString(message, (1920 - g.getFontMetrics().stringWidth(message)) / 2, 570);
					g.drawString(message2, (1920 - g.getFontMetrics().stringWidth(message2)) / 2, 595);
					g.drawString(message3, (1920 - g.getFontMetrics().stringWidth(message3)) / 2, 620);
					g.drawString(message4, (1920 - g.getFontMetrics().stringWidth(message4)) / 2, 645);
					
				} else {
					entities.remove(messageBox);
					entities.removeAll(removeEntities);
					removeEntities.clear();
				} // if else

				// clear graphics and flip buffer
				g.dispose();
				strategy.show();

				// stop background from moving until user makes it
				background.setHorizontalMovement(0);
				background.setVerticalMovement(0);
				
				// allows player to go faster when shift is pressed
				if (sprintPressed) {
					moveSpeed = 256;
				} else {
					moveSpeed = 64;
				} // if else
				
				if (!chooseStartingPokemon) {
					// respond to user moving background
					if ((leftPressed) && (!rightPressed)) {
						playerDirection = "left";
						Entity.setPlayerMoving(true);
						// stop player from moving diagonally
						upPressed = false;
						downPressed = false;
						// cycle through animations
						entities.remove(player);
						if (counter == 1) {
							player = new playerEntity(this, "images/animation1_west.png", 925, 460);
						} else if (counter == 2) {
							player = new playerEntity(this, "images/animation2_west.png", 925, 460);
						} else if (counter == 3) {
							player = new playerEntity(this, "images/animation3_west.png", 925, 460);
							counter = 0;
						} // else if
						counter++;
						entities.add(player);
						
						// only let player move if it isn't colliding with something
						if (!checkCollisions(playerDirection)) {
							background.setHorizontalMovement(moveSpeed);
						} // if
	
					} else if ((rightPressed) && (!leftPressed)) {
						
						playerDirection = "right";
						Entity.setPlayerMoving(true);
						// stop player from moving diagonally
						upPressed = false;
						downPressed = false;
						// cycle through animations
						entities.remove(player);
						if (counter == 1) {
							player = new playerEntity(this, "images/animation1_east.png", 925, 460);
						} else if (counter == 2) {
							player = new playerEntity(this, "images/animation2_east.png", 925, 460);
						} else if (counter == 3) {
							player = new playerEntity(this, "images/animation3_east.png", 925, 460);
							counter = 0;
						} // else if
						
						entities.add(player);
						counter++;
						
						// only let player move if it isn't colliding with something
						if (!checkCollisions(playerDirection)) {
							background.setHorizontalMovement(-moveSpeed);
						} // if
						
					} else if ((upPressed) && (!downPressed)) {
						playerDirection = "up";
						Entity.setPlayerMoving(true);
						// stop player from moving diagonally
						rightPressed = false;
						leftPressed = false;
	
						// cycle through animations
						entities.remove(player);
						if (counter == 1) {
							player = new playerEntity(this, "images/animation1_north.png", 925, 460);
						} else if (counter == 2) {
							player = new playerEntity(this, "images/animation2_north.png", 925, 460);
						} else if (counter == 3) {
							player = new playerEntity(this, "images/animation3_north.png", 925, 460);
							counter = 0;
						} // else if
						counter++;
	
						entities.add(player);
						
						// only let player move if it isn't colliding with something
						if (!checkCollisions(playerDirection)) {
							background.setVerticalMovement(moveSpeed);
						} // if
	
					} else if ((downPressed) && (!upPressed)) {
						
						playerDirection = "down";
						Entity.setPlayerMoving(true);
						// stop player from moving diagonally
						rightPressed = false;
						leftPressed = false;
	
						// cycle through animations'
						entities.remove(player);
						if (counter == 1) {
							player = new playerEntity(this, "images/animation1_south.png", 925, 460);
						} else if (counter == 2) {
							player = new playerEntity(this, "images/animation2_south.png", 925, 460);
						} else if (counter == 3) {
							player = new playerEntity(this, "images/animation3_south.png", 925, 460);
							counter = 0;
						} // else if
						counter++;
	
						entities.add(player);
						
						// only let player move if it isn't colliding with something
						if (!checkCollisions(playerDirection)) {
							background.setVerticalMovement(-moveSpeed);
						} // if
	
					} else if (!leftPressed && !rightPressed && !upPressed && !downPressed) {
						Entity.setPlayerMoving(false);
						
						// cycle through animations
						entities.remove(player);
						if (playerDirection.equals("up")) {
							player = new playerEntity(this, "images/standing_north.png", 925, 460);
						} else if (playerDirection.equals("down")) {
							player = new playerEntity(this, "images/standing_south.png", 925, 460);
						} else if (playerDirection.equals("left")) {
							player = new playerEntity(this, "images/standing_west.png", 925, 460);
						} else if (playerDirection.equals("right")) {
							player = new playerEntity(this, "images/standing_east.png", 925, 460);
						} // else if
						
						counter = 0;
						entities.add(player);
					} // if else
				} // if
			

			} // if

			// pause
			if (!Entity.getTeleported()) {
				try {Thread.sleep(100);} catch (Exception e) {}
			} // if
			
		} // while
		
	} // gameLoop

	public void displayMessage() {
		showMessage = true;
		if (gameResult == 1) {
			message = "You won the game!";
			message2 = "You have truly proven your prowess as a monster trainer";
			message3 = "And your name will be cemented in legend forevermore.";
			message4 = "[game over]";
		} else {
			message = "You lost the game!";
			message2 = "The strength of your opponent was too much. With this ";
			message3 = "crushing defeat, you decide to quit the monster training world.";
			message4 = "[game over]";
		} // if else
		
		moveSpeed = 0;
		
	} // displayMessage
	
	// display an overlay of all player's pokemons with their stats & moves. Returns an integer 
	// representation of the pokemon they have selected. Called when player is choosing their
	// monster and when displaying the menu in the main game screen
	public int displayPokemonMenu() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		Entity monsterSelected; // to draw an image of the selected monster
		int indexSelected; // the selected index (which gets returned)
		int numMoves; // the number of moves the selected monster has
		
		// draw overlay
		g.setColor(Color.black);
		g.fillRect(390, 190, 1140, 700);
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(400, 200, 1120, 680);
		
		g.setColor(Color.black);
		g.fillRect(390, 670, 1140, 10);

		// display pictures of all pokemon in player's inventory
		for (int i = 0; i < pokemonInventory.getNumPokemon(); i++) {
			Entity newPokemon; 
			
			// draw images in the right places and store the maximum index
			if (i < 11) {
				newPokemon = new messageEntity(this, "images/monsterSprites/" + pokemonInventory.getName(i)
					+ ".png", 420 + i * 100, 230);
				entities.add(newPokemon);
				
				spacesRightInitialised = i;
				spacesDownInitialised = 0;
			} else if (i < 22) {
				newPokemon = new messageEntity(this, "images/monsterSprites/" + pokemonInventory.getName(i)
					+ ".png", 420 + i * 100 - 1100, 330);
				entities.add(newPokemon);
				
				spacesDownInitialised = 1;
			} else if (i < 33){
				newPokemon = new messageEntity(this, "images/monsterSprites/" + pokemonInventory.getName(i)
					+ ".png", 420 + i * 100 - 2200, 430);
				entities.add(newPokemon);

				spacesDownInitialised = 2;
			} else {
				newPokemon = new messageEntity(this, "images/monsterSprites/" + pokemonInventory.getName(i)
					+ ".png", 420 + i * 100 - 3300, 530);
				entities.add(newPokemon);
				
				spacesDownInitialised = 3;
			} // else if
			
			// draw the entity created
			newPokemon.draw(g);
			
			// remove the entity after drawing it
			if (newPokemon != null) entities.remove(newPokemon);
			entities.removeAll(removeEntities);
			removeEntities.clear();
		} // for
		
		// move selection making sure the selection can't go farther than the last pokemon owned
		if (leftPressed) {
			if (spacesRight > 0) spacesRight -= 1;
			leftPressed = false;
		} else if (rightPressed) {
			if (spacesRight < spacesRightInitialised && spacesRight + 1 + 11*(spacesDown)
						< pokemonInventory.getNumPokemon()) spacesRight += 1;
			rightPressed = false;
		} else if (downPressed) {
			if (spacesDown < spacesDownInitialised && spacesRight + 11*(spacesDown + 1)
					< pokemonInventory.getNumPokemon()) spacesDown += 1;
			downPressed = false;
		} else if (upPressed) {
			if (spacesDown > 0) spacesDown -= 1;
			upPressed = false;
		} // if else
		
		// set variables
		indexSelected = spacesRight + 11*(spacesDown);
		numMoves = PokemonList.getNumMoves(pokemonInventory.getName(indexSelected));
		
		// draw box around selected monster
		g.setColor(Color.DARK_GRAY);
		if (pokemonInventory.getCurrentHealthStat(indexSelected) == 0) g.setColor(Color.red);
		g.fillRect(400 + spacesRight*100, 200 + spacesDown*100, 10, 110);
		g.fillRect(400 + spacesRight*100, 200 + spacesDown*100, 100, 10);
		g.fillRect(400 + spacesRight*100, 300 + spacesDown*100, 100, 10);
		g.fillRect(500 + spacesRight*100, 200 + spacesDown*100, 10, 110);
		
		// draw large image of the selected monster
		monsterSelected = new messageEntity(this, "images/monsterSprites/"
				+ pokemonInventory.getName(indexSelected) + "_large.png", 410, 675);
		monsterSelected.draw(g);
		
		// display stats & moves of the selected monster
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 20));
		g.setColor(Color.DARK_GRAY);
		g.drawString(pokemonInventory.getName(indexSelected) + " lvl. "
				+ pokemonInventory.getLevelStat(indexSelected) + ":", 670, 710);
		g.drawString("Health: " + pokemonInventory.getCurrentHealthStat(indexSelected)
			+ " / " + pokemonInventory.getHealthStat(indexSelected), 670, 760);
		g.drawString("Attack: " + pokemonInventory.getAttackStat(indexSelected), 670, 810);
		g.drawString("Speed: " + pokemonInventory.getSpeedStat(indexSelected), 670, 860);
		
		g.drawString(pokemonInventory.getAttack(indexSelected, 1) + ": "
				+ pokemonInventory.getAccuracy(indexSelected, 1) + "% chance to do "
				+ (int)(pokemonInventory.getPower(indexSelected, 1)
				* pokemonInventory.getAttackStat(indexSelected)/100) + " damage ("
				+ pokemonInventory.getCharges(indexSelected, 1) + " / "
				+ pokemonInventory.getMaxCharges(pokemonInventory.getAttack(indexSelected, 1))
				+ " charges)", 900, 710);
		g.drawString(pokemonInventory.getAttack(indexSelected, 2) + ": "
				+ pokemonInventory.getAccuracy(indexSelected, 2) + "% chance to do "
				+ (int)(pokemonInventory.getPower(indexSelected, 2)
				* pokemonInventory.getAttackStat(indexSelected)/100)
				+ " damage (" + pokemonInventory.getCharges(indexSelected, 2)
				+ " / " + pokemonInventory.getMaxCharges(pokemonInventory.getAttack(indexSelected, 2))
				+ " charges)", 900, 760);
		if (numMoves >= 3) g.drawString(pokemonInventory.getAttack(indexSelected, 3)
				+ ": " + pokemonInventory.getAccuracy(indexSelected, 3) + "% chance to do "
				+ (int)(pokemonInventory.getPower(indexSelected, 3)
				* pokemonInventory.getAttackStat(indexSelected)/100) + " damage ("
				+ pokemonInventory.getCharges(indexSelected, 3) + " / "
				+ pokemonInventory.getMaxCharges(pokemonInventory.getAttack(indexSelected, 3))
				+ " charges)", 900, 810);
		if (numMoves == 4) g.drawString(pokemonInventory.getAttack(indexSelected, 4) + ": "
				+ pokemonInventory.getAccuracy(indexSelected, 4) + "% chance to do "
				+ (int)(pokemonInventory.getPower(indexSelected, 4)
				* pokemonInventory.getAttackStat(indexSelected)/100) + " damage ("
				+ pokemonInventory.getCharges(indexSelected, 4) + " / "
				+ pokemonInventory.getMaxCharges(pokemonInventory.getAttack(indexSelected, 4))
				+ " charges)", 900, 860);
		
		strategy.show();
		g.dispose();
		
		// remove the large image of the selected monster
		entities.remove(monsterSelected);
		entities.removeAll(removeEntities);
		removeEntities.clear();
		
		// return the index of the selected monster
		return indexSelected;
	} // displayMonsterMenu

	
	// displays the health bars. Called while in battle. 
	public void displayHealthbars() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		
		// percentage representation of the respective healths
		double healthPercentAlly = ((double)pokemonInventory.getCurrentHealthStat(indexPokemonChosen)/
				((double)pokemonInventory.getHealthStat(indexPokemonChosen)));
		double healthPercentEnemy = ((double)trainerPokemon.getCurrentHealthStat(numTrainerPokemon)/
				((double)trainerPokemon.getHealthStat(numTrainerPokemon)));
		
		// draw background for the healthbar
		Entity box1 = new tileEntity(this, "images/bar.png", 780, 535);
		Entity box2 = new tileEntity(this, "images/bar.png", 1520, 260);
		box1.draw(g);
		box2.draw(g);
		
		// draw the names + health of each pokemon
		String type1 = PokemonList.getType(trainerPokemon.getName(numTrainerPokemon)).toLowerCase();
		String type2 = PokemonList.getType(pokemonInventory.getName(indexPokemonChosen)).toLowerCase();
		
		
		
		g.setColor(Color.black);
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 17));
		g.drawString(trainerPokemon.getName(numTrainerPokemon) + " (" + type1 + ")  -  "
				+ trainerPokemon.getCurrentHealthStat(numTrainerPokemon) + " / "
				+ trainerPokemon.getHealthStat(numTrainerPokemon), 1530, 255);
		g.setColor(Color.white);
		g.drawString(pokemonInventory.getName(indexPokemonChosen)+ " (" + type2 + ")  -  "
				+ pokemonInventory.getCurrentHealthStat(indexPokemonChosen) + " / "
				+ pokemonInventory.getHealthStat(indexPokemonChosen), 790, 530);
		
		// display health bars based on the respective health percentages
		g.setColor(Color.green);
		g.fillRect(800, 550, (int)(healthPercentAlly*160.0), 8);
		g.fillRect(1540, 275, (int)(healthPercentEnemy*160.0), 8);
	} // displayHealthbars
	
	/*
	 * startGame input: none output: none purpose: start a fresh game, clear old
	 * data
	 */
	private void startGame() {
		// clear out any existing entities and initalize a new set
		entities.clear();

		// blank out any keyboard settings that might exist
		leftPressed = false;
		leftPressed = false;
		rightPressed = false;
		upPressed = false;

		onePressed = false;
		twoPressed = false;

		xPressed = false;
		ePressed = false;
		enterPressed = false;
		
		initEntities();
				
	} // startGame

	/*
	 * inner class KeyInputHandler handles keyboard input from the user
	 */
	private class KeyInputHandler extends KeyAdapter {


		/*
		 * The following methods are required for any class that extends the abstract
		 * class KeyAdapter. They handle keyPressed, keyReleased and keyTyped events.
		 */
		public void keyPressed(KeyEvent e) {

			// respond to movement inputs only when not waiting for player's response to a message
			if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				if (!waitingForResponse || displayMenu) leftPressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				if (!waitingForResponse || displayMenu) rightPressed = true;
			} // if
			
			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
				if (!waitingForResponse || displayMenu) upPressed = true;
			} // if
			
			if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
				if (!waitingForResponse || displayMenu) downPressed = true;
			} // if
			
			if (e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_SHIFT) {
				sprintPressed = true;
			} // if

			/********************************************************************************/

			// respond to numbers pressed only when the menu isn't displayed
			if (!displayMenu && e.getKeyCode() == KeyEvent.VK_1) {
				onePressed = true;
			} // if

			if (!displayMenu && e.getKeyCode() == KeyEvent.VK_2) {
				twoPressed = true;
			} // if

			if (!displayMenu && e.getKeyCode() == KeyEvent.VK_0) {
				zeroPressed = true;
			} // if
			/**********************************************************************************/

			// respond to other keypresses 
			if (e.getKeyCode() == KeyEvent.VK_X) {
				xPressed = true;
			} // if

			if ( e.getKeyCode() == KeyEvent.VK_E) {
				ePressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				enterPressed = true;
			} // if
		} // keyPressed

		public void keyReleased(KeyEvent e) {
			// if waiting for keypress to start game, do nothing
			if (waitingForResponse) {
				return;
			} // if

			// respond to keys being released
			if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				leftPressed = false;
			} // if
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				sprintPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				rightPressed = false;
			} // if
			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
				upPressed = false;
			} // if
			if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
				downPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_1) {
				onePressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_2) {
				twoPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_0) {
				zeroPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_X) {
				xPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_E) {
				ePressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				enterPressed = false;
			} // if
		} // keyReleased

		public void keyTyped(KeyEvent e) {

			// if escape is pressed, end game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			} // if escape pressed

		} // keyTyped

	} // class KeyInputHandler

	/**
	 * Main Program
	 */
	public static void main(String[] args) {
		// instantiate this object
		new Game();
	} // main
} // Game