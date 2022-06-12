# Java-Pokemon-Game
Pokemon recreation created with Java's AWT Graphics using entirely copyright-free images. To play the game, download & run pokemon.jar 

&nbsp;
&nbsp;
&nbsp;



Screenshots:
https://imgur.com/gallery/yVwUYJ6


&nbsp;
&nbsp;
&nbsp;

Game.java - runs the game; runs an infinite loop that redraws / deletes images and allows player to move. All initializing of entities, processing key presses, movement, and interaction of entities is dealt with here.

Entity.java - Stores all information about an image on the screen - its location, height, width etc and is used to check if 2 entities collide. 

SpriteStore.java - Keeps track of all images that need to be drawn

Sprite.java - Store each individual sprite (image, height, width) and draws them when called

Pokemon.java - Stores all information about an individual pokemon i.e. their health, speed, attack, moves, etc.

PokemonList.java - Class with an arraylist of pokemon that represents the player's inventory; pokemon can be added or removed and attributes of specific pokemon the player holds can be accessed by index or name. 

PlayerEntity.java, BackgroundEntity.java, NPCEntity.java, cliffEntity.java, doorEntity.java, grassEntity.java, messageEntity.java, obstacleEntity.java, signEntity.java, tileEntity.java - Represent with different types of objects in the game with unique attributes ex. grassEntity rolls a dice every time the player moves over it to see if they find a wild pokemon

PokemonNames.txt - stores information of every pokemon which is accessed by Pokemon.java to initialize the pokemon's stats

PokemonMoves.txt - stores information of every move which is accessed by Pokemon.java to initialize the pokemon's moves
