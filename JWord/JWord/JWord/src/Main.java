
//Required imports
import java.util.List;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

public class Main implements ActionListener {
    // Creating the JFrame
    JFrame frame;
    JPanel title; // stores the header label
    JButton retry;
    JLabel header;

    JPanel grid; // Stores the colours for GREEN/YELLOW/BLACK
    static JLabel[] squares = new JLabel[30];

    JPanel keyboard; // Retain buttons for the user input
    static JTextField textfield; // user input

    // JButtons
    JButton deleteButton = new JButton("<"); // Backspace
    static JButton enterButton = new JButton("ENTER"); // Enter
    JButton[] letters = new JButton[26]; // Alphabet

    static int guesses = 0;
    static Font helvetica = new Font("Helvetica", Font.BOLD, 35); // Font used in wordle
    static Font helSmaller = new Font("Helvetica", Font.BOLD, 14); // Font for enter / backspace buttons
    static String userInput = "";
    static String answer = "";
    static int rowCount = 0; // For the grid
    static List<String> words = new ArrayList<>(); // possbile answers
    static List<String> allowable = new ArrayList<>(); // allowable words
    static String[] alphabet = { "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M"
    };

    // Color Palette taken from WORDLE
    static Color white = new Color(255, 255, 255);
    static Color offWhite = new Color(255, 255, 242);
    static Color black = new Color(0, 0, 0);
    static Color darkGrey = new Color(59, 59, 60);
    static Color keyGrey = new Color(221, 214, 218);
    static Color green = new Color(83, 141, 79);
    static Color yellow = new Color(181, 159, 59);

    Main() throws IOException {
        frame = new JFrame("WORDLE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Needed to add this to close the window when programme
                                                              // is terminated

        frame.setResizable(false);
        BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/imgs/logo.png"));
        frame.setFont(helvetica); // FOnt of Wordle for frame
        frame.getContentPane().setBackground(white);
        frame.setSize(600, 900);
        frame.setLayout(null);
        frame.setIconImage(image);
        frame.setLocationRelativeTo(null);

        // Title Wordle
        title = new JPanel();
        title.setBounds(70, 5, 460, 50);
        title.setLayout(new BorderLayout(75, 10));
        title.setBorder(new MatteBorder(0, 0, 2, 0, black));
        title.setBackground(white);

        // Header Wordle
        header = new JLabel(" W O R D L E");
        header.setFont(helvetica);
        header.setForeground(black);

        // retry button - starts a new game
        retry = new JButton("\u21BB");
        retry.setFont(helvetica);
        retry.setForeground(black);
        retry.setFocusable(false);
        retry.setBackground(white);
        retry.setBorderPainted(false);
        retry.addActionListener(this);

        // add to frame
        title.add(header, BorderLayout.CENTER);
        title.add(retry, BorderLayout.EAST);

        // 5x6 grid to display results
        grid = new JPanel();
        grid.setBounds(145, 90, 300, 350);
        grid.setOpaque(true);
        grid.setBackground(white);
        grid.setLayout(new GridLayout(6, 5, 3, 3));
        for (int i = 0; i < squares.length; i++) {
            squares[i] = new JLabel("", SwingConstants.CENTER);
            squares[i].setFont(helvetica);
            squares[i].setBackground(white);
            squares[i].setForeground(offWhite);
            squares[i].setOpaque(true);
            squares[i].setBorder(new LineBorder(darkGrey, 2));
            grid.add(squares[i]);
        }

        // User input area
        textfield = new JTextField("");
        textfield.setTransferHandler(null);// stops copy/pasting into text field
        // Only allows alphabetical inputs
        textfield.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();

                if (!(Character.isLetter(ch))) {
                    e.consume();
                }
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    enterButton.doClick();
                }
                if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && textfield.getText().length() > 1) {
                    deleteButton.doClick();
                }
                if (textfield.getText().length() > 4) {
                    e.consume();
                }
            }
        });
        textfield.setBounds(142, 480, 300, 50);
        textfield.setHorizontalAlignment(JTextField.CENTER);
        textfield.setFont(helvetica);
        textfield.setEditable(true); // Does not allow keyboard entry, as keyboard
        // text can be lowercase, also removes input of invalid chars
        textfield.setBorder(new LineBorder(keyGrey, 4));
        textfield.setBackground(white);
        textfield.setForeground(black);

        // Keyboard
        keyboard = new JPanel();
        keyboard.setBounds(70, 550, 460, 185);
        keyboard.setBackground(white);
        keyboard.setLayout(new GridLayout(3, 1, 4, 4));

        // Split panel into 3 seperate panels
        JPanel row1 = new JPanel(), row2 = new JPanel(), row3 = new JPanel();
        kbPanel(row1);
        kbPanel(row2);
        kbPanel(row3);

        // row 1
        for (int i = 0; i < 10; i++) {
            letters[i] = new JButton(alphabet[i]);
            buttonProperties(letters[i]);
            letters[i].addActionListener(this);
            row1.add(letters[i]);
        }
        keyboard.add(row1);

        // row 2
        for (int i = 9; i < 20; i++) {
            if (i == 9) {
                JLabel fillLeft = new JLabel();
                row2.add(fillLeft);
            } else if (i == 19) {
                JLabel fillRight = new JLabel();
                row2.add(fillRight);
            } else {
                letters[i] = new JButton(alphabet[i]);
                buttonProperties(letters[i]);
                letters[i].addActionListener(this);
                row2.add(letters[i]);
            }
        }
        keyboard.add(row2);

        // row 3
        for (int i = 18; i < 27; i++) {
            if (i == 18) {
                enterButton = new JButton("\u2713"); // tick
                buttonProperties(enterButton);
                enterButton.addActionListener(this);
                enterButton.setFont(helSmaller);
                row3.add(enterButton);
            } else if (i == 26) {
                deleteButton = new JButton("\u2190"); // Backspace arrow
                buttonProperties(deleteButton);
                deleteButton.addActionListener(this);
                deleteButton.setFont(helSmaller);
                row3.add(deleteButton);
            } else {
                letters[i] = new JButton(alphabet[i]);
                buttonProperties(letters[i]);
                letters[i].addActionListener(this);
                row3.add(letters[i]);
            }
        }
        keyboard.add(row3);

        // Compelte the Frame
        frame.add(title);
        frame.add(grid);
        frame.getContentPane().add(keyboard);
        frame.add(textfield);
        frame.setVisible(true); // Required for window to open

    }

    public static void main(String[] args) throws IOException {

        File file = new File(".");
        for (String fileNames : file.list())
            System.out.println(fileNames);

        Path wordsPath = Paths.get("targetWords.txt"); // Get file with taregt words
        // in it
        Path allowPath = Paths.get("gameDictionary.txt");// allowable words
        words = Files.readAllLines(wordsPath, StandardCharsets.UTF_8); // fills array with words
        allowable = Files.readAllLines(allowPath, StandardCharsets.UTF_8);
        newWord();
        new Main();

    }

    // Possible Actions by the User
    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < letters.length; i++) { // Keyboard Input
            if (e.getSource() == letters[i] && textfield.getText().length() < 5) {
                textfield.setText(textfield.getText().concat(alphabet[i]));
            }
        }
        // detetcts delete button on KB and virtual KB
        if (e.getSource() == deleteButton) {
            userInput = textfield.getText();
            // Needed to delete the text in textfield with one buttom press.
            if (userInput.length() > 6 || textfield.getText().equals("INVALID WORD")) {
                textfield.setText("");
            }
            if (textfield.getText().length() < 0) {
                return;
            } else {
                // Will delete the last letter in the text field.
                textfield.setText(textfield.getText().substring(0, textfield.getText().length() - 0));
            }

        }
        // Ebnter button for KB
        if (e.getSource() == enterButton) {
            userInput = textfield.getText().toUpperCase();

            if (guesses < 6) {
                if (userInput.length() == 5 && allowable.contains(userInput.toLowerCase())) {
                    textfield.setText("");
                    guesses++;
                    checkGuess();

                } else if (userInput.length() == 5 && !allowable.contains(userInput.toLowerCase())) {
                    textfield.setText("INVALID WORD"); // Not a real word.
                }
            }
        }

        if (e.getSource() == retry) {
            textfield.setText("");
            resetGame();
        }
    }

    public static void checkGuess() {
        for (int i = 0; i < answer.length(); i++) {
            squares[i + rowCount].setText(String.valueOf(userInput.charAt(i)));
            if (userInput.charAt(i) == answer.charAt(i)) { // Turn green
                squares[i + rowCount].setBackground(green);
                squares[i + rowCount].setBorder(new LineBorder(green, 2));
            } else if (answer.contains(String.valueOf(userInput.charAt(i)))) { // Turn yellow
                squares[i + rowCount].setBackground(yellow);
                squares[i + rowCount].setBorder(new LineBorder(yellow, 2));
            } else {
                squares[i + rowCount].setBackground(darkGrey);

            }
        }
        rowCount += 5;
        if (answer.equals(userInput)) { // Player has won.
            textfield.setText("WELL DONE");
            guesses = 6; // game over
        } else if (guesses == 6) { // Player has lost.
            textfield.setText("Answer: " + answer);

        }
    }

    public static void kbPanel(JPanel row) { // Apply properties to each element
        row.setLayout(new GridLayout(1, 10, 4, 4));
        row.setBackground(white);
    }

    // resets the game
    public static void resetGame() {
        newWord();
        guesses = 0;
        for (JLabel i : squares) {
            i.setBackground(white);
            i.setText("");
            i.setBorder(new LineBorder(darkGrey, 2));
        }

    }

    // when a new game is started, select a new word.
    public static void newWord() {
        int randomWord = new Random().nextInt(words.size());
        answer = words.get(randomWord).toUpperCase();
        System.out.println(answer);
        rowCount = 0;
    }

    public static int greenOrangeOrGrey(int index) {
        if (userInput.charAt(index) == answer.charAt(index))
            return 2; // green
        else if (answer.contains(String.valueOf(userInput.charAt(index))))
            return 1; // orange
        else
            return 0; // grey
    }

    public static void buttonProperties(JButton b) {

        b.setFocusable(false);
        b.setBackground(keyGrey);
        b.setForeground(black);
        b.setFont(helvetica);
        b.setOpaque(true);
        b.setBorder(new LineBorder(white, 2));
    }

}
