import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public final class SnakeGame extends JPanel implements ActionListener {

    private final int WIDTH = 800;  // Width of the game window
    private final int HEIGHT = 600;  // Height of the game window
    private final int UNIT_SIZE = 20;  // Size of one grid unit (smaller for smoother movement)
    private final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);  // Total number of grid units
    private final int DELAY = 75;  // Delay reduced for smoother movement (faster game)
    private final int[] x = new int[GAME_UNITS];  // Array to store the x-coordinates of the snake body
    private final int[] y = new int[GAME_UNITS];  // Array to store the y-coordinates of the snake body
    private int bodyParts = 3;  // Initial size of the snake
    private int foodEaten;  // Score counter
    private int foodX;  // X-coordinate of the food
    private int foodY;  // Y-coordinate of the food
    private char direction = 'R';  // Snake's starting direction (right)
    private boolean running = false;  // Is the game currently running?
    private Timer timer;  // Timer to control the game loop
    private final Random random;  // Random object to generate random positions
    private final JTextField scoreFontField;  // Text field to adjust the font size of the score

    public SnakeGame() {
        random = new Random();  // Create a new Random object
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));  // Set the size of the game panel
        this.setBackground(Color.BLACK);  // Set background color to black
        this.setFocusable(true);  // Make the game panel focusable to capture key events
        this.addKeyListener(new MyKeyAdapter());  // Add a key listener for snake control
        scoreFontField = new JTextField("30");  // Text field to adjust score font size
        this.add(scoreFontField, BorderLayout.SOUTH);  // Add the text field to the bottom
        startGame();  // Start the game
    }

    // Start the game by initializing food, starting the timer, and setting running to true
    public void startGame() {
        newFood();  // Generate a new food
        running = true;  // Set the game state to running
        timer = new Timer(DELAY, this);  // Set up a timer with a delay to control game speed
        timer.start();  // Start the timer
    }

    // Override the paintComponent method to draw the snake, food, and score
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Call the superclass method to ensure the panel is cleared
        draw(g);  // Call the draw method to draw game elements
    }

    // Draw the game elements (snake, food, score)
    public void draw(Graphics g) {
        if (running) {
            // Draw the food as a white oval
            g.setColor(Color.WHITE);  // Set the color to white for the food
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);  // Draw the food at its coordinates

            // Draw the snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.RED);  // Set the snake head color to red
                } else {
                    g.setColor(Color.RED);  // Set the snake body color to red
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);  // Draw each part of the snake
            }

            // Draw the score at the top-right corner
            String fontSizeText = scoreFontField.getText();  // Get the font size from the text field
            int fontSize;
            try {
                fontSize = Integer.parseInt(fontSizeText);  // Convert the input to an integer
            } catch (NumberFormatException e) {
                fontSize = 30;  // Default font size if the input is invalid
            }
            g.setColor(Color.WHITE);  // Set color for the score text
            g.setFont(new Font("Arial", Font.BOLD, fontSize));  // Set the font with the input size
            FontMetrics metrics = getFontMetrics(g.getFont());  // Get font metrics for positioning
            // Draw the score at the top-right with padding
            g.drawString("Score: " + foodEaten, WIDTH - metrics.stringWidth("Score: " + foodEaten) - 10, metrics.getHeight());
        } else {
            gameOver(g);  // If the game is over, call gameOver method to display game over message
        }
    }

    // Generate a new food position randomly within the grid
    public void newFood() {
        foodX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;  // Generate X-coordinate for food
        foodY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;  // Generate Y-coordinate for food
    }

    // Move the snake in the current direction
    public void move() {
        // Shift the body parts to follow the head
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];  // Move the body part to the previous part's position (x)
            y[i] = y[i - 1];  // Move the body part to the previous part's position (y)
        }

        // Move the head of the snake in the direction
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;  // Move up
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;  // Move down
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;  // Move left
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;  // Move right
                break;
        }
    }

    // Check if the snake has eaten the food
    public void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {  // If snake's head is at the food's position
            bodyParts++;  // Increase snake size
            foodEaten++;  // Increase score
            newFood();  // Generate new food
        }
    }

    // Check for collisions with the snake's body or borders
    public void checkCollisions() {
        // Check if the snake's head has collided with its body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;  // Stop the game if there is a collision
            }
        }

        // Check for border collisions
        if (x[0] < 0 || x[0] > WIDTH || y[0] < 0 || y[0] > HEIGHT) {
            running = false;  // Stop the game if the snake hits the border
        }

        if (!running) {
            timer.stop();  // Stop the timer when the game is over
        }
    }

    // Display Game Over message
    public void gameOver(Graphics g) {
        g.setColor(Color.WHITE);  // Set color to white for text
        g.setFont(new Font("Arial", Font.BOLD, 50));  // Set font size for Game Over message
        FontMetrics metrics = getFontMetrics(g.getFont());  // Get font metrics for positioning
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);  // Center the message
        g.drawString("Final Score: " + foodEaten, (WIDTH - metrics.stringWidth("Final Score: " + foodEaten)) / 2, HEIGHT / 2 + 50);  // Display final score
    }

    // Override the actionPerformed method for the timer
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {  // If the game is running
            move();  // Move the snake
            checkFood();  // Check if the snake has eaten the food
            checkCollisions();  // Check for collisions
        }
        repaint();  // Repaint the game window to update the visuals
    }

    public int getWIDTH() {
        return WIDTH;
    }

    // KeyAdapter to capture arrow key events for controlling the snake
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // Change the direction of the snake based on key pressed
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R') {
                        direction = 'L';  // Move left
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L') {
                        direction = 'R';  // Move right
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D') {
                        direction = 'U';  // Move up
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U') {
                        direction = 'D';  // Move down
                    }
                }
            }
        }
    }

    // Main method to create the game window
    public static void main(String[] args) {
        JFrame frame = new JFrame();  // Create a new JFrame for the game window
        SnakeGame game = new SnakeGame();  // Create an instance of the SnakeGame
        frame.add(game);  // Add the SnakeGame to the frame
        frame.setTitle("Snake Game");  // Set the title of the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close the application when the window is closed
        frame.setResizable(false);  // Make the window non-resizable
        frame.pack();  // Size the window to fit the game panel
        frame.setVisible(true);  // Make the window visible
        frame.setLocationRelativeTo(null);  // Center the window on the screen
    }
}