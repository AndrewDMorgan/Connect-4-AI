
// a thread to check branch quality
public class SearchThread extends Thread
{
    // the values required for the branch quality check
    private Board board;
    private Board.Pieces AI;
    private int turn;
    private int x;
    private volatile float output;  // the output of the check

    // initializes the values
    public void init(Board board, Board.Pieces AI, int turn, int x)
    {
        this.board = board;
        this.AI = AI;
        this.turn = turn;
        this.x = x;
    }

    // getters for the output and x position
    public float GetOutput() {  return output;  }

    public int GetX() {  return x;  }

    // gets the quality of a branch (turn = 0 is X and 1 is O)
    public void run()
    {
        // setting the output variable to the value of the branch quality check
        output = Main.GetBranchQuality(board, AI, turn, 0);
    }
}
