/**
 * Implements an individual square in a game of connect 4.  Each
 * square is either empty, or is occupied by one of two players.
 * @author Norm Krumpe
 */

import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;

public class Square extends JPanel {

	// Sets the color of the grid itself, traditionally
	// yellow in the board game.
	public static final Color BACK_COLOR = new Color(200, 200, 0);

	// The size of the boundary surrounding the circle. A value of
	// 0 would mean the circle touches the square on all 4 sides
	public static final int BOUNDARY = 5;

	// The color of the circle in an unoccupied square
	public static final Color EMPTY_COLOR = Color.gray;

	// The colors for each player
	private Color playerColor1, playerColor2;

	// The current occupant. 0 means empty, and 1 and 2 represent
	// players 1 and 2 respectively
	private int occupant;

	/**
	 * Constructs a new square with colors specified for players 1 and 2. The
	 * square is initally empty.
	 * 
	 * @param playerColor1
	 *            the color for player 1's piece
	 * @param playerColor2
	 *            the color for player 2's piece
	 * @throws IllegalStateException
	 *             if playerColor1 and playerColor2 are the same or if either of
	 *             those colors matches BACK_COLOR or EMPTY_COLOR
	 */
	public Square(Color playerColor1, Color playerColor2) {
		super();

		if (playerColor1.equals(playerColor2)
				|| playerColor1.equals(BACK_COLOR)
				|| playerColor1.equals(EMPTY_COLOR)
				|| playerColor2.equals(BACK_COLOR)
				|| playerColor2.equals(EMPTY_COLOR)) {
			throw new IllegalStateException(
					"Invalid player colors.  player1, player2, BACK_COLOR, "
							+ "and EMPTY_COLOR must all be different.");
		}

		this.playerColor1 = playerColor1;
		this.playerColor2 = playerColor2;
		this.occupant = 0;
		setBackground(BACK_COLOR);

		// A border around the square
		Border blackline = BorderFactory.createLineBorder(Color.BLACK, 1);
		setBorder(blackline);
	}

	/**
	 * Sets the occupant to the specified value.
	 * 
	 * @param occupant
	 *            the occupant of the square (0=empty, 1=player1, 2=player2)
	 * @throws IllegalArgumentException
	 *             if the occupant is not 0, 1, or 2
	 */
	public void setOccupant(int occupant) {
		if (occupant < 0 || occupant > 2)
			throw new IllegalArgumentException("Invalid occupant: " + occupant
					+ ", must be 0, 1, or 2");
		this.occupant = occupant;
		setToolTipText("Player " + occupant);
		repaint();
	}

	/**
	 * Gets the current occupant of this square
	 * 
	 * @return the current occupant of this square (0=empty, 1=player1,
	 *         2=player2)
	 */
	public int getOccupant() {
		return occupant;
	}

	/**
	 * Draws the circle in the square
	 * 
	 * @param g
	 *            the Graphics object that will do the painting
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		switch (occupant) {
		case 0:
			g.setColor(EMPTY_COLOR);
			break;
		case 1:
			g.setColor(playerColor1);
			break;
		case 2:
			g.setColor(playerColor2);
		}

		g.fillOval(BOUNDARY, BOUNDARY, this.getWidth() - 2 * BOUNDARY,
				this.getHeight() - 2 * BOUNDARY);

	}

}
