import java.util.HashMap;

public class Board
{
    // the pieces that can be placed
    public enum Pieces
    {
        None,
        Red,
        Yellow
    }

    // the colors each piece is (ascii colors)
    private HashMap<Pieces, String> pieceColors;

    // the board
    private Pieces[][] board;

    // the constructor
    public Board()
    {
        // adding the colors for each piece
        pieceColors = new HashMap<>();
        pieceColors.put(Pieces.None, "");
        pieceColors.put(Pieces.Red, "\u001B[31m");
        pieceColors.put(Pieces.Yellow, "\u001B[33m");

        // creating a blank board
        board = new Pieces[7][6];
        for (int x = 0; x < 7; x++)
        {
            for (int y = 0; y < 6; y++)
            {
                board[x][y] = Pieces.None;
            }
        }
    }

    // gets and sets pieces on the board
    public Pieces GetPiece(int x, int y) {  return board[x][y];  }
    public void SetPiece(Pieces piece, int x, int y) {  board[x][y] = piece;  }

    // drops a piece on the board (at the bottom)
    public void DropPiece(Pieces piece, int x)
    {
        // looping through the column
        for (int y = 1; y < 6; y++)
        {
            // checking if there is a piece in the way
            if (board[x][y] != Pieces.None)
            {
                // placing the piece and leaving the function
                board[x][y - 1] = piece;
                return;
            }
        }

        // placing the piece at the bottom
        board[x][5] = piece;
    }

    // gets and sets the whole board
    public Pieces[][] GetBoard() {  return board;  }
    public void SetBoard(Pieces[][] board) {  this.board = board;  }

    // gets a complete copy of the board
    public Pieces[][] GetBoardCopy()
    {
        // creating the new board
        Pieces[][] newBoard = new Pieces[7][6];

        // copying over the array
        for (int x = 0; x < 7; x++)
        {
            // copying over the layer
            System.arraycopy(board[x], 0, newBoard[x], 0, 6);
        }

        // returning the new board
        return newBoard;
    }

    // the win states
    public enum WinStates
    {
        None,
        WinRed,
        WinYellow,
        Tie
    }

    // checks for a win along a line
    public WinStates CheckLine(int x, int y, int dX, int dY)
    {
        // getting the start piece
        Pieces startPiece = board[x][y];

        // checking if it's air
        if (startPiece == Pieces.None) return WinStates.None;

        // looping along the path and checking if it's all the same piece type
        for (int l = 1; l < 4; l++)
        {
            // moving the spot being checked
            x += dX;
            y += dY;

            // checking for a non-win
            if (startPiece != board[x][y]) return WinStates.None;
        }

        // checking which side won
        if (startPiece == Pieces.Red) return WinStates.WinRed;
        return WinStates.WinYellow;
    }

    // checks for a win/loose/tie
    public WinStates CheckWin()
    {
        // looping through all the layers/rows and checking for a vertical win
        for (int yOff = 0; yOff < 3; yOff++)
        {
            for (int x = 0; x < 7; x++)
            {
                // getting the win state for the line
                WinStates line = CheckLine(x, yOff, 0, 1);

                // checking if there was a win, and returning it if there was
                if (line != WinStates.None) return line;
            }
        }

        // looping through all the layers/rows and checking for a horizontal win
        for (int xOff = 0; xOff < 4; xOff++)
        {
            for (int y = 0; y < 6; y++)
            {
                // getting the win state for the line
                WinStates line = CheckLine(xOff, y, 1, 0);

                // checking if there was a win, and returning it if there was
                if (line != WinStates.None) return line;
            }
        }

        // looping through the right diagonals
        for (int xOff = 0; xOff < 4; xOff++)
        {
            for (int yOff = 0; yOff < 3; yOff++)
            {
                // getting the win state for the line
                WinStates line = CheckLine(xOff, yOff, 1, 1);

                // checking if there was a win, and returning it if there was
                if (line != WinStates.None) return line;
            }
        }

        // looping through the left diagonals
        for (int xOff = 3; xOff < 7; xOff++)
        {
            for (int yOff = 0; yOff < 3; yOff++)
            {
                // getting the win state for the line
                WinStates line = CheckLine(xOff, yOff, -1, 1);

                // checking if there was a win, and returning it if there was
                if (line != WinStates.None) return line;
            }
        }

        // checking for a tie, looping through all pieces
        boolean isTie = true;
        for (int x = 0; x < 7; x++)
        {
            for (int y = 0; y < 6; y++)
            {
                // checking for a non-blank
                if (board[x][y] == Pieces.None)
                {
                    // setting the tie to false and leaving the loops
                    isTie = false;
                    x = 7;
                    y = 6;
                }
            }
        }

        // checking if it is a tie
        if (isTie) return WinStates.Tie;

        // no win state was found
        return WinStates.None;
    }

    // gets the quality of a board position
    public int GetBoardQuality(Pieces piece)
    {
        // getting the current win condition
        WinStates winState = CheckWin();

        // finding the value that goes with that condition
        if (winState == WinStates.None) return 0;  // neutral
        if (winState == WinStates.Tie) return -1;  // not good but not terrible
        if ((piece == Pieces.Red && winState == WinStates.WinRed) || (piece == Pieces.Yellow && winState == WinStates.WinYellow)) return 2;  // very good
        return -2;  // not good, lost
    }

    // gets the quality of a board position with a given win state
    public int GetBoardQuality(Pieces piece, WinStates winState)
    {
        // finding the value that goes with that condition
        if (winState == WinStates.None) return 0;  // neutral
        if (winState == WinStates.Tie) return -1;  // not good but not terrible
        if ((piece == Pieces.Red && winState == WinStates.WinRed) || (piece == Pieces.Yellow && winState == WinStates.WinYellow)) return 2;  // very good
        return -2;  // not good, lost
    }

    // gets the color for a piece
    public String GetColor(Pieces piece) {  return pieceColors.get(piece);  }

    // renders the board
    public String RenderBoard()
    {
        // the render
        String render = "";

        // looping through the board and rendering it
        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x < 7; x++)
            {
                // rendering the piece
                render += pieceColors.get(board[x][y]) + "[]\u001B[0m";
            }
            render += "\n";
        }

        // returning the render
        return render;
    }
}