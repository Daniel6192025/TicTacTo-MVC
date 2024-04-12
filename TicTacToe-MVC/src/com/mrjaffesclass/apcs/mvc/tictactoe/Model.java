package com.mrjaffesclass.apcs.mvc.tictactoe;

import com.mrjaffesclass.apcs.messenger.*;

/**
 * The model represents the data that the app uses.
 *
 * @author Roger Jaffe
 * @version 1.0
 */
public class Model implements MessageHandler {

    // Messaging system for the MVC
    private final Messenger mvcMessaging;

    // Model's data variables
    private boolean whoseMove;
    private boolean gameOver;
    private String[][] board;

    /**
     * Model constructor: Create the data representation of the program
     *
     * @param messages Messaging class instantiated by the Controller for local
     * messages between Model, View, and controller
     */
    public Model(Messenger messages) {
        mvcMessaging = messages;
    }

    /**
     * Initialize the model here and subscribe to any required messages
     */
    public void init() {
        this.mvcMessaging.subscribe("playerMove", this);
        this.mvcMessaging.subscribe("newGame", this);
        
        this.board = new String[3][3];
        this.newGame();
 
    }

    private void newGame() {
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[0].length; col++) {
                this.board[row][col] = "";
            }
        }
        this.whoseMove = false;
        this.gameOver = false;
    }
    
    private boolean draw() {
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[0].length; col++) {
                if (this.board[row][col].equals("")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String isWinner(String player) {
        String winner = "";
        for (int row = 0; row < this.board.length; row++) {
            for (int col = 0; col < this.board[0].length; col++) {
                if (this.board[0][col].equals(this.board[1][col]) && this.board[0][col].equals(this.board[2][col]) && !this.board[0][col].equals("")) {
                    winner = player;
                    return board[0][col];
                }
                if (this.board[row][0].equals(this.board[row][1]) && this.board[row][0].equals(this.board[row][2]) && !this.board[row][0].equals("")) {
                    winner = player;
                    return board[row][0];
                }
                if (this.board[0][0].equals(this.board[1][1]) && this.board[0][0].equals(this.board[2][2])) {
                    winner = player;
                    return board[0][0];
                }
                if (this.board[0][2].equals(this.board[1][1]) && this.board[0][2].equals(this.board[2][0])) {
                    winner = player;
                    return board[0][2];
                }
                if (!winner.equals("")) {
                    break;
                }
            }
        }
        return winner;
    }

    @Override
    public void messageHandler(String messageName, Object messagePayload) {
        // Display the message to the console for debugging
        if (messagePayload != null) {
            System.out.println("MSG: received by model: " + messageName + " | " + messagePayload.toString());
        } else {
            System.out.println("MSG: received by model: " + messageName + " | No data sent");
        }

        // playerMove message handler
        if (messageName.equals("playerMove")) {
            // Get the position string and convert to row and col
            String position = (String) messagePayload;
            Integer row = new Integer(position.substring(0, 1));
            Integer col = new Integer(position.substring(1, 2));
            // If square is blank...
            if (this.board[row][col].equals("") && !gameOver) {
                // ... then set X or O depending on whose move it is
                if (this.whoseMove) {
                    this.board[row][col] = "X";
                } else {
                    this.board[row][col] = "O";
                }
                
                String player = (this.whoseMove) ? "X" : "O";
                String winner = isWinner(player);
                    if(!winner.equals("")) {
                        this.mvcMessaging.notify("gameWon", player);
                        this.gameOver = true;
                    } else {
                        if (draw()) {
                            this.mvcMessaging.notify("hasTied", "");
                            this.gameOver = true;
                        }
                    }
                // Send the boardChange message along with the new board 
                this.mvcMessaging.notify("boardChange", this.board);
                this.whoseMove = !this.whoseMove;
                
            }

            // newGame message handler
        } else if (messageName.equals("newGame")) {
            // Reset the app state
            this.newGame();
            // Send the boardChange message along with the new board 
            this.mvcMessaging.notify("boardChange", this.board);
        }

    }

    /**
     * Reset the state for a new game
     */
}
