/**
 * Implements a game of Connect 4, where players take turns
 * dropping checkers down columns.  The first player to get
 * 4 in a row is the winner.
 * @author Nathan Anneken
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class Connect4 extends JFrame {

	// Default colors for players
	public static final Color PLAYER_1_COLOR = Color.RED;
	public static final Color PLAYER_2_COLOR = Color.BLACK;

	private int rows, columns, playMore;
	private JPanel squarePanel, buttonPanel;
	private JLabel status;
	private Square[][] squares;
	private JButton[] columnButtons;

	/**
	 * Constructs a Connect 4 grid with a specified number of rows and columns.
	 * 
	 * @param rows
	 *            the number of rows of squares
	 * @param columns
	 *            the number of columns of squares
	 */
	public Connect4(int rows, int columns) {
		super("Let's play Connect 4!");
		this.rows = rows;
		this.columns = columns;
		this.playMore = JOptionPane.YES_OPTION;

		frameSetup();
		squarePanelSetup();
		buttonPanelSetup();
		statusSetup();
		createCursor(1);
		setVisible(true);
	}

	/**
	 * Creates a cursor that is the size of a "checker" and that changes color
	 * depending on the players turn
	 * 
	 * @param turn
	 *            The player whose turn it is
	 */
	private void createCursor(int turn) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension dim = kit.getBestCursorSize(48, 48);
		BufferedImage buffered = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_ARGB);
		Shape circle = new Ellipse2D.Float(0, 0, dim.width - 3, dim.height - 3);
		Graphics2D g = buffered.createGraphics();
		if (turn == 1)
			g.setColor(PLAYER_1_COLOR);
		else
			g.setColor(PLAYER_2_COLOR);
		g.fill(circle);
		int centerX = (dim.width - 1) / 2;
		int centerY = (dim.height - 1) / 2;
		g.dispose();
		Cursor cursor = kit.createCustomCursor(buffered, new Point(centerX,
				centerY), "myCursor");
		setCursor(cursor);
	}

	/**
	 * Sets up the details for the frame.
	 */
	private void frameSetup() {
		setLayout(new BorderLayout());
		setResizable(false);
		setBounds(0, 0, 400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	/**
	 * Sets up the details for the square panel itself.
	 */
	private void squarePanelSetup() {
		// The layout is based on the number of rows
		squarePanel = new JPanel(new GridLayout(rows, 0));

		// Each square in the grid is a Square object
		squares = new Square[rows][columns];
		for (int row = 0; row < squares.length; row++) {
			for (int cell = 0; cell < squares[0].length; cell++) {
				squares[row][cell] = new Square(PLAYER_1_COLOR, PLAYER_2_COLOR);
				squarePanel.add(squares[row][cell]);
			}
		}
		add(squarePanel, BorderLayout.CENTER);
	}

	/**
	 * Sets up the buttons at the top of each column
	 */
	private void buttonPanelSetup() {
		buttonPanel = new JPanel(new GridLayout(1, 0));
		columnButtons = new JButton[columns];
		for (int i = 0; i < columns; i++) {
			columnButtons[i] = new JButton("" + i);
			columnButtons[i].setToolTipText("Move in column " + i);
			columnButtons[i].addActionListener(new ButtonMover());
			buttonPanel.add(columnButtons[i]);
		}
		add(buttonPanel, BorderLayout.NORTH);
	}

	/**
	 * Finds the first available open square in the desired column
	 * 
	 * @param col
	 *            The column that either player has put a checker into
	 * @return The lowest(highest row) position available
	 */
	private int findOpenSquare(int col) {
		for (int a = 1; a <= rows; a++) {
			if (squares[rows - a][col].getOccupant() == 0) {
				return (rows - a);
			}
		}
		// This return should never be called, as if the final spot
		// in the row is chosen, the button will be deactivated
		return -1;
	}

	/**
	 * Determines if either player has four sequential tiles in a row
	 * 
	 * @return Whether or not either player has four tiles in a row
	 */
	private boolean checkRow() {
		int numberInARow = 0;
		for (int row = 0; row < squares[0].length; row++) {
			for (int column = 0; column < squares.length - 1; column++) {
				// Checks to see if two tiles next to each other in the same row
				// Have the same occupant
				if ((squares[column][row].getOccupant() == squares[column + 1][row]
						.getOccupant())
						&& (squares[column][row].getOccupant() != 0)) {
					numberInARow++;
				} else {
					numberInARow = 0;
				}
				// Checks to see if three sequential tiles with the same
				// occupant
				// Have been found
				if (numberInARow == 3) {
					for (int a = 0; a < 4; a++) {
						highlightWinner(column + 1 - a, row);
					}
					return true;
				}
			}
			numberInARow = 0;
		}
		return false;
	}

	/**
	 * Creates a Cyan Oval over a given JPanel to indicate a winning tile
	 * 
	 * @param column
	 *            The column number of the tile
	 * @param row
	 *            The row number of the tile
	 */
	private void highlightWinner(int column, int row) {
		Graphics g = squares[column][row].getGraphics();
		g.setColor(Color.CYAN);
		g.fillOval(Square.BOUNDARY, Square.BOUNDARY,
				squares[column][row].getWidth() - 2 * Square.BOUNDARY,
				squares[column][row].getHeight() - 2 * Square.BOUNDARY);
		RepaintManager.currentManager(squares[column][row])
				.markCompletelyClean(squares[column][row]);

	}

	/**
	 * Determines if either player has four sequential tiles in a column
	 * 
	 * @return Whether either player has four sequential tiles in a column
	 */
	private boolean checkColumn() {
		int numberInARow = 0;
		for (int column = 0; column < squares.length; column++) {
			for (int row = 0; row < squares[0].length - 1; row++) {
				// Checks to see if tiles in the same column have the same
				// occupant
				if ((squares[column][row].getOccupant() == squares[column][row + 1]
						.getOccupant())
						&& (squares[column][row].getOccupant() != 0)) {
					numberInARow++;

				} else {
					numberInARow = 0;
				}
				if (numberInARow == 3) {
					for (int a = 0; a < 4; a++) {
						highlightWinner(column, row + 1 - a);
					}
					return true;
				}
			}
			numberInARow = 0;
		}
		return false;
	}

	/**
	 * Checks to see if either player has four sequential tiles in the diagonal
	 * direction
	 * 
	 * @return Whether or not either player has four tiles in a row diagonally
	 */
	private boolean checkDiagonal() {
		// Checks for any four in a row diagonally from bottom left to top right
		for (int row = rows - 1; row >= 3; row--) {
			for (int col = 0; col < columns - 3; col++) {
				if (squares[row][col].getOccupant() != 0
						&& squares[row][col].getOccupant() == squares[row - 1][col + 1]
								.getOccupant()
						&& squares[row][col].getOccupant() == squares[row - 2][col + 2]
								.getOccupant()
						&& squares[row][col].getOccupant() == squares[row - 3][col + 3]
								.getOccupant()) {
					for (int a = 0; a < 4; a++) {
						highlightWinner(row - a, col + a);
					}
					return true;
				}
			}
		}
		// Checks for any four in a row diagonally from top left to bottom right
		for (int row = 0; row < rows - 3; row++) {
			for (int col = 0; col < columns - 3; col++) {
				if (squares[row][col].getOccupant() != 0
						&& squares[row][col].getOccupant() == squares[row + 1][col + 1]
								.getOccupant()
						&& squares[row][col].getOccupant() == squares[row + 2][col + 2]
								.getOccupant()
						&& squares[row][col].getOccupant() == squares[row + 3][col + 3]
								.getOccupant()) {
					for (int a = 0; a < 4; a++) {
						highlightWinner(row + a, col + a);
					}
					return true;
				}
			}
		}

		// If neither checks returns true, there are no diagonal sequences of
		// four
		return false;
	}

	/**
	 * Determines if either player has four tiles in a row in any direction
	 * 
	 * @return Whether either player has one in any direction
	 */
	private boolean checkWin() {
		if (checkRow())
			return true;
		if (checkColumn())
			return true;
		if (checkDiagonal())
			return true;
		return false;
	}

	/**
	 * Inner class action listener that finds which button was pressed,
	 * determines the first available panel that can be filled in that button's
	 * column, then updates the panel according to whose turn it is.
	 * 
	 */
	public class ButtonMover implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int a = 0; a <= rows; a++) {
				if (e.getSource().equals(columnButtons[a])) {
					// If the tile goes into the top-most space, the button
					// is deactivated
					if (findOpenSquare(a) == 0)
						columnButtons[a].setEnabled(false);
					updateSquare(findOpenSquare(a), a, getPlayerTurn());
				}
			}

		}
	}

	/**
	 * Returns the player whose turn it is and sets the bottom panel to the
	 * specified player number
	 * 
	 * @return An int representation of which players turn it is
	 */
	private int getPlayerTurn() {
		if (status.getText() == "Player 1's turn...") {
			status.setText("Player 2's turn...");
			return 1;
		} else {
			status.setText("Player 1's turn...");
			return 2;
		}

	}

	/**
	 * Sets up the status label at the bottom of the frame, which keeps track of
	 * which player has the move.
	 */
	private void statusSetup() {
		status = new JLabel("Player 1's turn...", JLabel.CENTER);
		add(status, BorderLayout.SOUTH);
	}

	/**
	 * Updates a specified square by indicating which player should now occupy
	 * that square Also updates the cursor depending upon whose turn it is and
	 * checks to see if either player has four tiles in a row or if the board is
	 * full with no winner, resulting in a tie
	 * 
	 * @param row
	 *            the row of the square
	 * @param col
	 *            the column of the square
	 * @param player
	 *            which player should occupy that square (0=empty, 1=player1,
	 *            2=player2)
	 */
	public void updateSquare(int row, int col, int player) {
		squares[row][col].setOccupant(player);
		if (player == 1)
			createCursor(2);
		if (player == 2)
			createCursor(1);
		boolean aTie = true;
		for (int a = 0; a <= rows; a++) {
			if (columnButtons[a].isEnabled())
				aTie = false;
		}
		if (aTie) {
			JOptionPane.showMessageDialog(null, "It's A Tie", "Game Over",
					JOptionPane.WARNING_MESSAGE);
			playMore = JOptionPane.showConfirmDialog(null,
					"Do you want to play again?", "Yes",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (playMore == JOptionPane.YES_OPTION) {
				for (int ro = 0; ro < squares[0].length; ro++) {
					for (int cell = 0; cell < squares.length; cell++) {
						squares[cell][ro].setOccupant(0);
					}
					columnButtons[ro].setEnabled(true);
				}
			}
		}
		if (checkWin()) {
			for (int a = 0; a <= rows; a++) {
				columnButtons[a].setEnabled(false);
			}
			JOptionPane.showMessageDialog(null, "Player " + player + " Wins!",
					"Game Over", JOptionPane.WARNING_MESSAGE);
			playMore = JOptionPane.showConfirmDialog(null,
					"Do you want to play again?", "Yes",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (playMore == JOptionPane.YES_OPTION) {
				for (int ro = 0; ro < squares[0].length; ro++) {
					for (int cell = 0; cell < squares.length; cell++) {
						squares[cell][ro].setOccupant(0);
					}
					columnButtons[ro].setEnabled(true);
				}
			}
		}

	}

	/**
	 * Starts a game of Connect 4
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		new Connect4(6, 7);
	}

}
