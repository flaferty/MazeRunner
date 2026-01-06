# WASD Maze Runner Project
## Code Structure:
### MapClass:
- Loads and manages textures for various map elements like walls, traps, enemies, and items.
- Renders the game map and its elements based on their current state and position.
- Handles interactions with map elements such as picking up keys, hearts, and lemons,
and detecting collisions with traps, enemies, and walls.
- Manages enemy movements and animations.
- Supports dynamic placement of items like hearts and lemons on the map.
- Provides utility methods to check map element states and positions.
as we used 
### GameScreen:
- Manages rendering of game elements and animations.
- Handles user inputs for character movements and interactions.
- Checks for and responds to collisions with game objects (keys, hearts, lemons, enemies, exit).
- Manages the camera to follow the character.
- Displays HUD elements like health, keys collected, and level timer.
- Manages transitions to other screens (pause, next level, game over).
 ### MazeRunnerGame:
- Manages transitions between different screens like menu, game, and victory screens.
- Handles loading and disposing of resources such as textures and sounds.
- Controls character animations for movements and attacks.
- Manages game levels and overall game state.
### GameObject:
- Object types like Enemy, Trap, Key extend the
    GameObject class and share common methods to load their animations.
- Character:
- Controls character movement with boundary checks to prevent walking through walls or off the map.
- Checks for and handles interactions with traps and enemies, including health reduction and cooldown management.
- Manages the character's health, including increasing or decreasing health and checking game-over conditions.
- Sets the character's initial position to the map's entry point.
- Handles picking up keys and toggling the character's 'has key' status.
### Screens:
-  MenuScreen - allows the user to start a new game, load a map or exit the game;
- PauseScreen - pauses the game and has same options as MenuScreen;
- GameOverScreen - allows the user to go back to the MenuScreen;
- VictoryScreen - allows the user to go back to the MenuScreen;

## How to use:

- From the Main Menu the user can choose whether to start a new game or choose a map from a file.
- The game can read and load any .properties file, as long as it is  named in the format "level-*n*.properties".
- The objective of the game is to pick up a key which can open an exit and afterward the player automatically goes to the next level.
- If the player dies / wins, they can go back to the main menu and restart the game.
## Gameplay:

* **Movement:** ```WASD``` and ```arrows``` for moving in different directions.
* **View Stats:** Press ```TAB``` to view elapsed time and level.
* **Menu:** Press ```Esc``` to access the game menu.
* **Attack:** Press  ```Z``` to attack an enemy.

## Extra features added:
- A heart has a random chance to spawn on the map and increases the health of the
  character when picked up.
- A lemon has a random chance to spawn on the map and when picked up, it
  increases the speed of the character.
- Character is moving by ```WASD``` controls or alternatively by ```arrows```.
- By pressing ```TAB``` the user can see the elapsed time and current level.
- Walls with different textures;
- 2 types of traps ( fire and slime);
