package inkball;

import java.io.*;
import java.util.*;

/**
 * The LevelDisplay class manages the loading and representation of a game level.
 * It handles the initial layout of tiles and spawns defined in a specified layout file.
 */
public class LevelDisplay{
    private String layoutFile;
    private Tile[][] board;
    private App app;
    private List<Ball> balls;

    /**
     * Constructor for the LevelDisplay class.
     * Initializes the application reference, layout file, and board.
     *
     * @param app The main application instance.
     * @param layoutFile The path to the layout file.
     */
    public LevelDisplay(App app, String layoutFile){
        this.app = app;
        this.layoutFile = layoutFile;
        this.board = new Tile[18][App.BOARD_WIDTH];
        this.balls = balls;        
    }

    /**
     * Loads the level configuration from the layout file.
     * Initializes the board based on the characters defined in the file.
     */
    public void loadLevel(){
        try (BufferedReader br = new BufferedReader(new FileReader(layoutFile))){
            String line;
            for (int y = 0; (line=br.readLine())!=null && y < 18; y++){
                for (int x = 0; x < line.length() && x < App.BOARD_WIDTH; x++){
                    char c = line.charAt(x);

                    if (c == 'H' && (x + 1) < line.length()){ // hole condition
                        char nextChar = line.charAt(x + 1);
                        String combined = "H"+nextChar;
                        board[y][x] = createTile(combined, x, y);
                        board[y][x+1] = createTile("holeNumber", x+1, y);
                        board[y+1][x] = createTile("holeNumber", x, y+1);
                        board[y+1][x+1] = createTile("holeNumber", x+1, y+1);
                        x++;
                    } else if (c == 'B' && (x+1)<line.length()){ // ball condition
                        char nextChar = line.charAt(x + 1);
                        String combined = "B"+nextChar;
                        // System.out.println("the "+combined+" position is ("+x+","+y);
                        board[y][x] = createTile(combined, x, y);
                        board[y][x+1] = createTile("ballNumber", x+1, y);
                        x++; 
                    } else if (c == 'S'){ // spawn condition
                        board[y][x] = createTile(String.valueOf(c), x, y);
                    } else if (c == ' '){
                        if (board[y][x] == null){ // Check if the tile has been initialized
                            board[y][x] = createTile("tile", x, y);
                        }
                        
                    }else{
                        if (board[y][x] == null){ // Check if the tile has been initialized
                            board[y][x] = createTile(String.valueOf(c), x, y);
                            // System.out.println("new wall added "+ String.valueOf(c) + "at " + x + " " + y);
                        }
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a tile based on the type and position.
     *
     * @param c The type of the tile as a string.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return A Tile object initialized with the specified type and position.
     */
    public Tile createTile(String c, int x, int y){
        Tile tile = new Tile(x, y, c);
        // System.out.println("the pic of tile is " + app.getImage("tile"));
        tile.setImage(app.getImage("tile")); // Set default tile image
        if (c.equals("X")){
            tile.setImage(app.getImage("wall0"));
        } else if (c.equals("1")){
            tile.setImage(app.getImage("wall1"));
        } else if (c.equals("2")){
            tile.setImage(app.getImage("wall2"));
        } else if (c.equals("3")){
            tile.setImage(app.getImage("wall3"));
        } else if (c.equals("4")){
            tile.setImage(app.getImage("wall4"));
        } else if (c.equals("H0")){
            tile.setImage(app.getImage("hole0"));
        } else if (c.equals("H1")){
            tile.setImage(app.getImage("hole1"));
        } else if (c.equals("H2")){
            tile.setImage(app.getImage("hole2"));
        } else if (c.equals("H3")){
            tile.setImage(app.getImage("hole3"));
        } else if (c.equals("H4")){
            tile.setImage(app.getImage("hole4"));
        } else if (c.equals("holeNumber")){
            tile.setImage(null);
        } else if (c.equals("S")){
            tile.setImage(app.getImage("entrypoint"));
        } 
        return tile;
    }

    /**
     * Retrieves a list of spawn tiles from the board.
     *
     * @return A list of tiles that are spawn points.
     */
    public List<Tile> getSpawnTile(){
        List<Tile> spawnTiles = new ArrayList<>();
        for (Tile[] row : board){
            for (Tile tile : row){
                if (tile != null && tile.getType().equals("S")){
                    spawnTiles.add(tile);
                }
            }
        }
        return spawnTiles;
    }

    /**
     * Retrieves a list of ball position to be spawn from the board.
     *
     * @return A list of tiles that represent balls.
     */
    public List<Tile> getBallTile(){
        List<Tile> ballTiles = new ArrayList<>();
        for (Tile[] row : board){
            for (Tile tile : row){
                if (tile != null && tile.getType().startsWith("B")){
                    ballTiles.add(tile);
                }
            }
        }
        if (ballTiles.isEmpty()){
            // System.out.println("ball tile is null...");
        }
        return ballTiles;
    }

    // public List<Tile> getHoleTile(){
    //     List<Tile> holeTiles = new ArrayList<>();
    //     for (Tile[] row : board){
    //         for (Tile tile : row){
    //             if (tile != null && tile.getType().startsWith("H")){
    //                 holeTiles.add(tile);
    //             }
    //         }
    //     }
    //     if (holeTiles.isEmpty()){
    //         System.out.println("hole tile is empty");
    //     }
    //     return holeTiles;
    // }

    /**
     * Gets the current state of the game board.
     *
     * @return A 2D array representing the board tiles.
     */
    public Tile[][] getBoard() {
        return board;
    }
}