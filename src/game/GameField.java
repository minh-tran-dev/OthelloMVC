package game;

// game field as singleton
public class GameField
{
    int maxX = 8;
    int maxY = 8;
    int[][] map = new int[maxX][maxY];

    private static GameField instance;

    private GameField() {}

    public static GameField GetInstance()
    {
        if (instance == null)
        {
            instance = new GameField();
        }
        return instance;
    }







}
