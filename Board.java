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

    // checks for three in a row of a piece
    WinStates CheckTripleLine(int x, int y, int dx, int dy)
    {
        Pieces side = board[x][y];
        if (side == Pieces.None) return WinStates.None;
        for (int d = 1; d < 3; d++) if (board[x + dx * d][y + dy * d] != side) return WinStates.None;
        if (side == Pieces.Red) return WinStates.WinRed;
        return WinStates.WinYellow;
    }

    // checks for any triples of a piece/color
    float[] CheckForNumTriple()
    {
        float[] numWins = {0, 0};
        Pieces side = board[0][0];

        // horizontals
        int numInRow;
        for (int y = 0; y < 6; y++)
        {
            numInRow = 0;
            for (int x = 0; x < 7; x++)
            {
                if (board[x][y] != side)
                {
                    numInRow = 1;
                    side = board[x][y];
                }
                else
                {
                    numInRow ++;
                    if (numInRow == 3 && side != Pieces.None)
                    {
                        if (side == Pieces.Red) numWins[0]++;
                        else numWins[1]++;
                        numInRow = 0;
                    }
                }
            }
        }

        // verticals
        for (int x = 0; x < 7; x++)
        {
            numInRow = 0;
            for (int y = 0; y < 6; y++)
            {
                if (board[x][y] != side)
                {
                    numInRow = 1;
                    side = board[x][y];
                }
                else
                {
                    numInRow ++;
                    if (numInRow == 3 && side != Pieces.None)
                    {
                        if (side == Pieces.Red) numWins[0]++;
                        else numWins[1]++;
                        numInRow = 0;
                    }
                }
            }
        }

        // diagonals
        for (int y = 0; y < 4; y++) for (int x = 0; x < 5; x++)
        {
            WinStates state = CheckTripleLine(x, y, 1, 1);
            if (state != WinStates.None)
            {
                if (state == WinStates.WinRed) numWins[0]++;
                else numWins[1]++;
            }
        }
        for (int y = 0; y < 4; y++) for (int x = 2; x < 7; x++)
        {
            WinStates state = CheckTripleLine(x, y, -1, 1);
            if (state != WinStates.None)
            {
                if (state == WinStates.WinRed) numWins[0]++;
                else numWins[1]++;
            }
        }

        return numWins;
    }

    // gets the quality of a board position with a given win state
    public float GetBoardQuality(Pieces piece, WinStates winState)
    {
        // finding the value that goes with that condition
        if (winState == WinStates.None)
        {
            float quality = 0;
            float[] triple = CheckForNumTriple();
            if (triple[0] != 0 || triple[1] != 0)
            {
                if (piece == Pieces.Red)
                {
                    quality += triple[0];
                    quality -= triple[1];
                }
                else
                {
                    quality -= triple[0];
                    quality += triple[1];
                }
                quality /= 10;
            }

            // getting the average position of the player and Ai
            int yellowX = 0;
            int yellowY = 0;
            int numYellow = 0;
            int redX = 0;
            int redY = 0;
            int numRed = 0;

            for (int x = 0; x < 7; x++)
            {
                for (int y = 0; y < 6; y++)
                {
                    Pieces tile = board[x][y];
                    if (tile == Pieces.Red)
                    {
                        redX += x;
                        redY += y;
                        numRed++;
                    }
                    else if (tile == Pieces.Yellow)
                    {
                        yellowX += x;
                        yellowY += y;
                        numYellow++;
                    }
                }
            }

            float averageRedX = 3 - Math.abs(3 - redX / (float)numRed);
            float averageRedY = 5 - redY / (float)numRed;

            float averageYellowX = 3 - Math.abs(3 - yellowX / (float)numYellow);
            float averageYellowY = 5 - yellowY / (float)numYellow;

            if (piece == Pieces.Red)
            {
                quality += (averageRedX + averageRedY) / 8;
                quality -= (averageYellowX + averageYellowY) / 8;
                quality += Math.random() - 0.5;
            }
            else
            {
                quality += (averageYellowX + averageYellowY) / 8;
                quality -= (averageRedX + averageRedY) / 8;
                quality += Math.random() - 0.5;
            }

            return quality;
        }
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
        for (int x = 0; x < 7; x++) render += "|" + (x + 1) + " |";
        render += "\n";

        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x < 7; x++)
            {
                // rendering the piece
                String color = pieceColors.get(board[x][y]);
                if (!color.equals("")) render += "|" + color + "⬤\u001B[0m" + "|";
                else render += "|  |";
            }
            render += "\n";
        }

        // returning the render
        return render;
    }
}
