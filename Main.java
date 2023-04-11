import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        // creating a scanner for user input
        Scanner scanner = new Scanner(System.in);

        // creating a board
        Board board = new Board();

        // the current persons turn (0 == red, 1 == yellow)
        int turn = 0;

        // the state of the game
        Board.WinStates gameState = Board.WinStates.None;

        // running the game while there isn't a win
        System.out.println(board.RenderBoard());
        while (gameState == Board.WinStates.None)
        {
            // checking which players turn it is
            if (turn == 0)  // reds turn
            {
                System.out.println("(Player) Red's turn");

                // placing a piece in a valid place
                boolean validPlace = false;
                while (!validPlace)
                {
                    // getting the x coord to drop it at
                    System.out.println("x");
                    int x = scanner.nextInt() - 1;

                    // checking if the spot is valid
                    if (board.GetPiece(x, 0) == Board.Pieces.None)
                    {
                        // dropping a piece and leaving the loop
                        board.DropPiece(Board.Pieces.Red, x);
                        validPlace = true;
                    }
                }
            }
            else  // yellows turn
            {
                System.out.println("(AI) Yellow's turn");

                // the threads running the branch quality checks
                ArrayList<SearchThread> threads = new ArrayList<>();

                // looping through all cells
                for (int x = 0; x < 7; x++)
                {
                    // checking if the cell is empty
                    if (board.GetPiece(x, 0) == Board.Pieces.None)
                    {
                        // running the threads to get the branches quality
                        Board newBoard = new Board();
                        newBoard.SetBoard(board.GetBoardCopy());
                        newBoard.DropPiece(Board.Pieces.Yellow, x);
                        SearchThread thread = new SearchThread();
                        thread.init(newBoard, Board.Pieces.Yellow, 1 - turn, x);
                        thread.start();
                        threads.add(thread);
                    }
                }

                // finding the best spots to place a piece
                float bestQuality = -10;
                ArrayList<Integer> bestX = new ArrayList<>();
                int numThreads = threads.size();
                for (int i = 0; i < numThreads; i++)
                {
                    threads.get(i).join();
                    float quality = threads.get(i).GetOutput();
                    if (quality > bestQuality)
                    {
                        // setting the best quality and the position of it
                        bestQuality = quality;
                        bestX = new ArrayList<>();
                    }
                    if (quality >= bestQuality)
                    {
                        bestX.add(threads.get(i).GetX());
                    }
                }

                // choosing a random index from bestX and bestY
                int index = Math.min((int) (Math.random() * bestX.size()), bestX.size() - 1);

                // placing the piece
                board.DropPiece(Board.Pieces.Yellow, bestX.get(index));
            }

            // rendering the board
            System.out.println(board.RenderBoard());

            // updating the games state
            gameState = board.CheckWin();

            // swapping the turn
            turn = 1 - turn;
        }

        // printing the win/loose/tie condition
        if (gameState == Board.WinStates.WinRed) System.out.println("You won!!");
        if (gameState == Board.WinStates.WinYellow) System.out.println("The AI won.");
        else System.out.println("It's a tie...");
    }

    // gets the quality of a branch (turn = 0 is X and 1 is O)
    public static float GetBranchQuality(Board board, Board.Pieces AI, int turn, int depth)
    {
        // getting the side
        Board.Pieces side = turn == 0 ? Board.Pieces.Red : Board.Pieces.Yellow;

        // something went wrong or there is a win/tie/loose
        Board.WinStates winState = board.CheckWin();
        if (depth > 7 || winState != Board.WinStates.None) return board.GetBoardQuality(AI, winState);

        // getting the quality of all branches
        if (turn == 0)  // Red's turn / the players turn
        {
            // the worst option
            float worst = 5;

            // looping through all spots and finding a valid choice
            for (int x = 0; x < 7; x++)
            {
                // checking that the cell is empty
                if (board.GetPiece(x, 0) == Board.Pieces.None)
                {
                    // adding this as a new branch
                    Board newBoard = new Board();
                    newBoard.SetBoard(board.GetBoardCopy());
                    newBoard.DropPiece(side, x);
                    float quality = GetBranchQuality(newBoard, AI, 1 - turn, depth + 1);

                    // checking if a new worst was found
                    if (quality < worst)
                    {
                        worst = quality;
                        if (worst == -2) return worst;  // the worst it can get no reason to keep searching
                    }
                }
            }

            // returning the worst
            return worst;
        }

        // Yellow's turn / the AI's turn

        // the best option
        float best = -5;

        // looping through all spots and finding a valid choice
        for (int x = 0; x < 7; x++)
        {
            // checking that the cell is empty
            if (board.GetPiece(x, 0) == Board.Pieces.None)
            {
                // adding this as a new branch
                Board newBoard = new Board();
                newBoard.SetBoard(board.GetBoardCopy());
                newBoard.DropPiece(side, x);
                float quality = GetBranchQuality(newBoard, AI, 1 - turn, depth + 1);

                // checking if the new best was found
                if (quality > best)
                {
                    best = quality;
                    if (best == 2) return best;  // the best it can get no reason to keep searching
                }
            }
        }

        // returning the best
        return best;
    }
}
