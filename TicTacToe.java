import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;


/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game.
 *
 * @author Lynn Marshall
 * @version November 8, 2012
 *
 * @author Abdullah Soboh (101220742)
 * @version April 8, 2023
 *
 * Citations:
 *
 * "O_ICON" “File:O.svg,” Wikimedia Commons. [Online]. Available: https://commons.wikimedia.org/wiki/File:O.svg. [Accessed: 06-Apr-2023]. 
 * "X_ICON" “File:X.svg,” Wikimedia Commons. [Online]. Available: https://commons.wikimedia.org/wiki/File:X.svg. [Accessed: 06-Apr-2023]. 
 * "xSound" “File:xSound.wav,”XKit. [Online]. Available: https://mixkit.co/free-sound-effects/move/. [Accessed: 06-Apr-2023]. 
 * "OSound" “File:oSound.wav,”XKit. [Online]. Available: https://mixkit.co/free-sound-effects/move/. [Accessed: 06-Apr-2023]. 
 * "winSound" “File:winSound.wav,”XKit. [Online]. Available: https://mixkit.co/free-sound-effects/win/. [Accessed: 06-Apr-2023]. 
 * "tieSound" “File:tieSound.wav,”XKit. [Online]. Available: https://mixkit.co/free-sound-effects/tie/. [Accessed: 06-Apr-2023]. 
 */
public class TicTacToe extends JFrame implements ActionListener {
    //TicTacToe core game variables
    private static final String PLAYER_X = "X";
    private static final String PLAYER_O = "O";
    private static final String EMPTY = " ";
    private static final String TIE = "TIE";
    private String[][] board = new String[3][3];
    private String player = PLAYER_X;
    private String winner = EMPTY;
    private int numFreeSquares = 9;
    //JLabel for status, timer and stats
    private JLabel statusBar;
    private JLabel timeElapsedLabel;
    private JLabel winBar;
    //Timer
    private Timer timer;
    //Time elapsed
    private int timeElapsed;
    //Sound variables
    private Clip xSound;
    private Clip oSound;
    private Clip winSound;
    private Clip tieSound;
    private final ImageIcon X_ICON = new ImageIcon("X.png");
    private final ImageIcon O_ICON = new ImageIcon("O.png");
    //variables for counting wins, losses, and ties
    private int XWins = 0; 
    private int OWins = 0;
    private int ties = 0;
    private JButton[][] buttons;

    /**
     * TicTacToe initializes everything up for a new game. Creates a JFrame GUI with a 3X3 grid of buttons and JLabel of text
     * declaring the current player and stats.
     */
    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(425, 425);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Add the menu bar
        setJMenuBar(createMenuBar());

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        buttons = new JButton[3][3]; //Creating button GUI
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].addActionListener(this);
                boardPanel.add(buttons[row][col]);
            }
        }
        timer = new Timer(1000, e -> { //creating a game timer with a delay of 1000ms (1 second)
            timeElapsed++;
            timeElapsedLabel.setText("Time: " + timeElapsed + " seconds");
        });
        timeElapsedLabel = new JLabel("Time: " + timeElapsed + " seconds");

        add(boardPanel, BorderLayout.CENTER);
        winBar = new JLabel("X-Wins: "+ XWins + " O-Wins: " + OWins + " Ties: " + ties);
        statusBar = new JLabel("Player X's turn");
        add(statusBar, BorderLayout.SOUTH);
        add(winBar, BorderLayout.EAST);
        add(timeElapsedLabel, BorderLayout.NORTH);
        setResizable(false);
        clearBoard();
        timer.start();
        loadSounds();

    }

    /**
     * createMenuBar creates and adds the menu bar to the JFrame generated in TicTacToe()
     * @return JMenuBar, the Game menu present in the GUI
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        // Adding a MouseAdapter as MouseListener to the gameMenu
        gameMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Change colour to blue when hovered
                gameMenu.setForeground(Color.BLUE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // Change back colour when not hovered
                gameMenu.setForeground(Color.BLACK);
            }
        });
        JMenuItem newGameMenuItem = new JMenuItem("New Game"); //Creating drop down menu newGame
        JMenuItem quitMenuItem = new JMenuItem("Quit"); //Creating drop down menu quit
        JMenuItem switchMenuItem = new JMenuItem("Switch Sides"); //Creating drop down menu Switch Sides

        newGameMenuItem.addActionListener(e -> {
            clearBoard();
            statusBar.setText("Player X's turn");
            player = PLAYER_X;
        });

        quitMenuItem.addActionListener(e -> System.exit(0));

        switchMenuItem.addActionListener(e -> {
            clearBoard();
            if (player == PLAYER_O){
                player = PLAYER_X;
                statusBar.setText("Player X's turn");

            } else {
                player = PLAYER_O;
                statusBar.setText("Player O's turn");
            }
        });
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); // to save typing
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK)); //quit shortcut
        newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK)); //newGame shortcut
        switchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK)); //switchSides shortcut


        gameMenu.add(newGameMenuItem);
        gameMenu.add(switchMenuItem);
        gameMenu.add(quitMenuItem);
        menuBar.add(gameMenu);

        return menuBar;
    }

    /**
     * clearBoard resets the board and all other game parameters
     */
    private void clearBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = EMPTY;
                buttons[row][col].setText(EMPTY);
                buttons[row][col].setIcon(null);
                buttons[row][col].setEnabled(true);
                timeElapsed = 0;
                timer.start(); //Restart the game timer
            }
        }
        winner = EMPTY;
        numFreeSquares = 9;
    }

    /**
     * actionPerformed is called whenever the user clicks on any of the GUI's buttons.
     * @param e  an ActionEvent button clicked
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (source == buttons[row][col] && board[row][col] == EMPTY) {
                    board[row][col] = player; //set the chosen board to player if empty
                    //Setting the player icons and sounds
                    if (player == PLAYER_X) {
                        buttons[row][col].setIcon(X_ICON);
                        xSound.setFramePosition(0);
                        xSound.start();
                    }
                    if (player == PLAYER_O){
                        buttons[row][col].setIcon(O_ICON);
                        oSound.setFramePosition(0);
                        oSound.start();
                    }
                    numFreeSquares--;


                    if (haveWinner(row, col)) {
                        winner = player;
                        statusBar.setText("Player " + player + " wins with a time of: " + timeElapsed + " seconds!" );
                        winSound.setFramePosition(0); //play win sound from frame position 0
                        winSound.start();
                        //Update which player won and the Jlabel winBar
                        if (winner == PLAYER_X) {
                            XWins += 1;
                            winBar.setText("X-Wins: "+ XWins + " O-Wins: " + OWins + " Ties: " + ties );

                        }
                        if (winner == PLAYER_O) {
                            OWins += 1;
                            winBar.setText("X-Wins: "+ XWins + " O-Wins: " + OWins + " Ties: " + ties );

                        }
                        disableButtons();
                        timer.stop();
                    } else if (numFreeSquares == 0) {
                        //Update the statusBar and winBar
                        winner = TIE;
                        statusBar.setText("It's a tie! The game took: " + timeElapsed + " seconds.");
                        disableButtons(); //Disable all buttons, game done
                        timer.stop();// End timer
                        ties += 1;
                        winBar.setText("X-Wins: "+ XWins + " O-Wins: " + OWins + " Ties: " + ties );
                        tieSound.setFramePosition(0); //Play tie sound from frame position 0
                        tieSound.start();
                    } else {
                        if (player == PLAYER_X) {
                            player = PLAYER_O;
                        } else {
                            player = PLAYER_X;
                        }
                        statusBar.setText("Player " + player + "'s turn");
                    }
                }
            }
        }

    }

    /**
     * disableButtons disables all the buttons in the TicTacToe GUI
     */
    private void disableButtons() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setEnabled(false);
            }
        }
    }

    /**
     * haveWinner a boolean returns wether a winner was found or not
     * @param row the row to be selected
     * @param col the column to he selected
     * @return true or false if a diagonal, horizontal or vertical is created
     */
    private boolean haveWinner(int row, int col) {
        if (numFreeSquares > 4) return false;

        if (board[row][0].equals(board[row][1]) && board[row][0].equals(board[row][2])) return true;
        if (board[0][col].equals(board[1][col]) && board[0][col].equals(board[2][col])) return true;
        if (row == col) {
            if (board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) {
                return true;
            }
        }

        if (row == 2 - col) {
            return board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0]);
        }

        return false;
    }

    /**
     * loadSound loads the sound file into the clip variable
     * @param filename the Filename (.wav) to be loaded
     * @return a Clip variable loaded with the sound file
     */
    private Clip loadSound(String filename) {
        Clip clip = null;
        try {
            File file = new File(filename);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
        return clip;
    }

    /**
     * loadSounds initializes loadSound method on each clip variable
     */
    private void loadSounds() {
        xSound = loadSound("xSound.wav");
        oSound = loadSound("oSound.wav");
        winSound = loadSound("winSound.wav");
        tieSound = loadSound("tieSound.wav");
    }

    /**
     * main Our main method. Runs the TicTacToe game with a nimbus look and feel. Catches any ClassNotFoundException,
     * InstantiationException, IllegalAccessException and UnsupportedLookAndFeelException errors.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("Menu[MouseOver].textForeground", Color.BLUE); //Set Game menu to blue
            UIManager.put("Menu[MouseOver].background", Color.YELLOW);
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace(); //Catch any possible errors that might arise
        }
        TicTacToe game = new TicTacToe();
        game.setVisible(true);
    }
}

