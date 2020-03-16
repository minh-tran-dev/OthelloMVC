package game;

public class GameCalculation
{
    private static GameField field = GameField.GetInstance();

    //Calculate the starting field, which will be visually set
    public static void CalculateStartingField()
    {
        for (int i = 0; i < field.map.length;i++)
        {
            for (int j = 0; j < field.map[i].length;j++)
            {
                field.map[i][j] = 0;
            }
        }
        field.map[3][3] = 2;
        field.map[3][4] = 1;
        field.map[4][3] = 1;
        field.map[4][4] = 2;
    }

    //returns how many pieces a player has on the field
    public static int GetPoints(int player)
    {
        int result = 0;
        for (int i = 0; i < field.map.length;i++)
            for (int j = 0; j < field.map[i].length; j++)
            {
                if (field.map[i][j] == player)
                    result+=1;
            }

        return result;
    }

    // returns if a player has valid plays this turn
    public static boolean CanPlay(int player)
    {
        CalculateValidPlays(player);
        int result = 0;
        for (int i = 0; i < field.map.length;i++)
            for (int j = 0; j < field.map[i].length; j++)
            {
                if (field.map[i][j] == 3)
                    result+=1;
            }

        return result > 0;
    }

    //sets valid plays on the game grid for a player
    public static void CalculateValidPlays(int player)
    {

        int lookup;
        for (int i = 0; i < field.map.length;i++)
            for (int j = 0; j < field.map[i].length; j++)
            {
                if (field.map[i][j] == player)
                {
                    CheckTilesPlus(i,j,1,0,player);
                    CheckTilesPlus(i,j,-1,0,player);
                    CheckTilesPlus(i,j,1,1,player);
                    CheckTilesPlus(i,j,-1,1,player);

                    CheckTilesX(i,j,1,1,player);
                    CheckTilesX(i,j,-1,1,player);
                    CheckTilesX(i,j,1,-1,player);
                    CheckTilesX(i,j,-1,-1,player);
                }
            }

    }

    //resets all previous valid plays
    public static void ResetValidPlays()
    {
        for(int  i = 0; i < field.map.length;i++)
            for (int j = 0; j < field.map[i].length;j++)
            {
                if (field.map[i][j] == 3)
                    field.map[i][j] = 0;
            }
    }

    //sets the piece on the board and turns all tiles , that are applicable
    public static void SetPlayerInput(int x , int y, int player)
    {

        TurnTiles(x,y,player);
        field.map[x][y] = player;
    }

    // turns all tiles, calculated for a piece
    private static void TurnTiles(int x , int y,int player)
    {
        TurnTilesPlus(x,y,1,1,player);
        TurnTilesPlus(x,y,-1,1,player);
        TurnTilesPlus(x,y,1,0,player);
        TurnTilesPlus(x,y,-1,0,player);

        TurnTilesX(x,y,1,1, player);
        TurnTilesX(x,y,-1,1, player);
        TurnTilesX(x,y,1,-1, player);
        TurnTilesX(x,y,-1,-1, player);
    }

    //turns all tiles in horizontal and vertical direction
    private static void TurnTilesPlus(int x,int y,int direction,int hOrV,int player)
    {
        boolean same = false;
        boolean canFlip = false;

        int target;
        int standing;
        int maxValue;
        if (hOrV == 0)
        {
            target = x + direction;
            maxValue = field.maxX;
            standing = y;
        }
        else
        {
            target = y + direction;
            maxValue = field.maxY;
            standing = x;
        }
        if (target <= maxValue && target >= 0)
        {
           canFlip = CanFlip(target,standing,direction,hOrV,player);

            while (!same && canFlip)
            {
                if (hOrV == 0)
                    same = TurnTile(target,y,player);
                else
                    same = TurnTile(x,target,player);
                target += direction;
            }
        }
    }

    // turns all tiles in diagonal directions
    private static void TurnTilesX(int x , int y, int directionX, int directionY, int player)
    {
        boolean same = false;
        boolean canFlip = false;

        x+=directionX;
        y+=directionY;

        if (x < field.maxX && x >= 0  && y < field.maxY && y >= 0)
        {
            canFlip = CanFlipX(x,y,directionX,directionY,player);

            while(!same && canFlip)
            {
                same = TurnTile(x,y,player);
                x+= directionX;
                y+= directionY;
            }
        }
    }

    // checks if there are valid plays in diagonal and vertical direction
    private static void CheckTilesPlus(int x,int y,int direction,int hOrV,int player)
    {
        int target;
        int standing;
        int maxValue;
        if (hOrV == 0)
        {
            target = x + direction;
            maxValue = field.maxX;
            standing = y;
        }
        else
        {
            target = y + direction;
            maxValue = field.maxY;
            standing = x;
        }
        int pos;
        if (target < maxValue && target >= 0)
        {
            pos = GetPos(target,standing,direction,hOrV,player);
            if ( pos > -1)
            {
                if (hOrV == 0)
                    field.map[pos][standing] = 3;
                else
                    field.map[standing][pos] = 3;
            }
        }
    }

    //checks if there are valid plays in diagonal directions
    private static void CheckTilesX(int x, int y, int directionX, int directionY, int player)
    {
        int oppositePlayer = 3-player;
        boolean opFound = false;
        x+=directionX;
        y+=directionY;

        while (x < field.maxX && x >= 0  && y < field.maxY && y >= 0)
        {
            if (field.map[x][y] == 3)
                break;
            if (field.map[x][y] == 0)
                if (opFound)
                {
                    field.map[x][y] = 3;
                    break;
                }
                else
                    break;
            if(field.map[x][y] == player)
                if (opFound)
                    break;
            if(field.map[x][y] == oppositePlayer)
                opFound = true;

            x+=directionX;
            y+=directionY;
        }
    }


    //returns the position of a valid tile in horizontal or vertical position
    private static int GetPos(int target, int standing, int direction, int hOrV, int player)
    {
        int ceiling = GetBound(direction,  hOrV);
        int oppositePlayer = 3-player;
        boolean opFound = false;

        while (target != ceiling)
        {
            if (hOrV == 0)
            {
                if (field.map[target][standing] == 3)
                    return target;
                if (field.map[target][standing] == 0)
                {
                    if(opFound)
                        return target;
                    else
                        return -1;
                }
                if (field.map[target][standing] == player)
                {
                    if (opFound)
                        return -1;
                }
                if (field.map[target][standing] == oppositePlayer)
                {
                    opFound = true;
                }

                target+=direction;
            }
            else
            {

                if (field.map[standing][target] == 3)
                    return target;
                if (field.map[standing][target] == 0)
                {
                    if(opFound)
                        return target;
                    else
                        return -1;
                }
                if (field.map[standing][target] == player)
                {
                    if (opFound)
                        return -1;
                }
                if (field.map[standing][target] == oppositePlayer)
                {
                    opFound = true;
                }

                target+=direction;
            }
        }
        return -1;
    }



    // checks if a row or column of enemy pieces can be flipped horizontal or vertical
    private static boolean CanFlip(int target, int standing, int direction, int hOrV, int player)
    {
        int ceiling = GetBound(direction,hOrV);
        while (target != ceiling)
        {
            if (hOrV == 0)
            {
                if (field.map[target][standing] == player)
                    return true;
            }
            else
            {
                if (field.map[standing][target] == player)
                    return true;
            }
            target += direction;
        }
        return false;
    }

    // checks if diagonal enemy pieces can be flipped
    private static boolean CanFlipX(int x, int y, int directionX, int directionY, int player)
    {
        while (x < field.maxX && x >= 0  && y < field.maxY && y >= 0)
        {
            if(field.map[x][y] == player)
                return true;

            x+=directionX;
            y+=directionY;
        }
        return false;
    }


    //returns how far an array can be iterated through
    private static int GetBound(int direction, int hOrV)
    {
        int result;
        if (hOrV == 0)
            result = field.maxY;
        else
            result = field.maxX;

        if (direction > 0)
            return result;
        else
            return -1;

    }

    // turns a tile to opposite color
    private static boolean TurnTile(int x, int y , int player)
    {
        int oppositePlayer = 3 - player;
        if (field.map[x][y] == oppositePlayer)
        {
            field.map[x][y] = player;
            return false;
        }
        else if (field.map[x][y] == player)
            return true;
        return false;
    }

    //checks if there is no more space to set game pieces
    public static boolean CheckGameOver()
    {
        for(int  i = 0; i < field.map.length;i++)
            for (int j = 0; j < field.map[i].length;j++)
            {
                if (field.map[i][j] == 0)
                    return false;
            }
        return true;
    }
}
