package game;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable
{
    public GridPane grid;
    public GameField field = GameField.GetInstance();
    public GameUtilities util = GameUtilities.GetInstance();
    public Label playerLbl;
    public Label bPtsLbl;
    public Label wPtsLbl;
    public Label bPassLbl;
    public Label wPassLbl;
    public Label winnerLbl;

    private int player;
    private boolean selected;
    private boolean passed;
    private Node currentTile;
    private boolean gameOver;



    //sets up the gamegrid with invisible pieces
    public void FillMap()
    {
        for(int x=0;x<field.map.length;x++ )
        {
            for(int y=0;y<field.map[x].length;y++)
            {
                Circle circle = new Circle();
                circle.setFill(Color.BLACK);
                circle.setRadius(20);
                circle.setMouseTransparent(true);
                circle.setVisible(false);
                grid.add(circle,x,y);
            }
        }
    }

    //visually sets the calculated gamefield
    public void SetMap()
    {
        int x;
        int y;
        int value;

        for ( Node gridCircle : grid.getChildren())
        {
            if (gridCircle == null || !(gridCircle instanceof Circle))
                continue;
            x = grid.getColumnIndex(gridCircle);
            y = grid.getRowIndex(gridCircle);
            value = field.map[x][y];

            switch(value)
            {
                case 0: gridCircle.setVisible(false);
                    break;
                case 1: gridCircle.setVisible(true);
                        ((Circle) gridCircle).setFill(Color.BLACK);
                    break;
                case 2: gridCircle.setVisible(true);
                        ((Circle) gridCircle).setFill(Color.GHOSTWHITE);
                    break;
                case 3: gridCircle.setVisible(true);
                    ((Circle) gridCircle).setFill(Color.ORANGE);
                    break;
                default :gridCircle.setVisible(false);
                    break;
            }
        }
    }

    //ends a players turn
    public void FinishTurn(MouseEvent mouseEvent)
    {
        if(selected && !gameOver)
        {
            passed = false;
            HandleTurn();
            util.PlaySFX("/button.wav");
        }
    }

    //passes a players turn
    public void PassTurn(MouseEvent mouseEvent)
    {
        if(!GameCalculation.CanPlay(player) && !gameOver)
        {
                if (passed)
                    gameOver = true;
                Deselect();
                passed = true;
                HandleTurn();
                util.PlaySFX("/button.wav");


        }
    }

    // does all calculations for player turn
    public void HandleTurn()
    {
        if (currentTile != null)
        {
            GameCalculation.SetPlayerInput(grid.getColumnIndex(currentTile),grid.getRowIndex(currentTile),player);
            util.PlaySFX("/shuffle.wav");
        }
        if (GameCalculation.CheckGameOver())
            gameOver = true;
        Deselect();
        GameCalculation.ResetValidPlays();
        ChangePlayer();
        SetLabels();
        GameCalculation.CalculateValidPlays(player);
        SetMap();
    }

    // changes players
    public void ChangePlayer()
    {
        player = 3 - player;
    }

    // grid click behaviour
    public void OnMouseClicked(MouseEvent mouseEvent)
    {
        int colCount = (int)Math.floor(mouseEvent.getPickResult().getIntersectedPoint().getX() / (grid.getWidth() /8));
        int rowCount = (int)Math.floor(mouseEvent.getPickResult().getIntersectedPoint().getY() / (grid.getHeight() /8 ));

        for (Node temp : grid.getChildren() )
        {
            if (temp == null || !(temp instanceof Circle))
                continue;


            if( grid.getColumnIndex(temp) == colCount && grid.getRowIndex(temp) == rowCount)
            {
                //System.out.println( grid.getColumnIndex(temp) + "  "+ grid.getRowIndex(temp) );
                if (field.map[grid.getColumnIndex(temp)][grid.getRowIndex(temp)] == 3)
                {
                    temp.setVisible(false);
                    Select(temp,player);
                    temp.setVisible(true);
                }

            }
        }
    }

    // select a game piece
    public void Select(Node tile, int player)
    {
        if (currentTile != null)
            Deselect();
        if (!selected)
        {
            if (player == 1)
                ((Circle)tile).setFill(Color.BLACK);
            else
                ((Circle)tile).setFill(Color.GHOSTWHITE);
            currentTile = tile;
            selected = true;
            util.PlaySFX("/move.wav");
        }
    }

    // deselect a game piece
    public void Deselect()
    {
        if (selected)
        {
            ((Circle)currentTile).setFill(Color.ORANGE);
            currentTile = null;
            selected=false;
        }
    }

    // sets/refreshes all labels to corresponding values
    public void SetLabels()
    {
        String playerStr;
        if (player == 1)
            playerStr = "Black";
        else
            playerStr = "White";

        if (passed)
        {
            if (player ==1)
                wPassLbl.setText("passed");
            else
                bPassLbl.setText("passed");
        }
        else
        {
            wPassLbl.setText("");
            bPassLbl.setText("");
        }
        if(gameOver)
        {
            if (GameCalculation.GetPoints(1) == GameCalculation.GetPoints(2))
            {
                winnerLbl.setText("DRAW");
            }
            else if (GameCalculation.GetPoints(1) > GameCalculation.GetPoints(2))
            {
                winnerLbl.setText("Winner : BLACK");
            }
            else
                winnerLbl.setText("Winner : WHITE");
        }

        bPtsLbl.setText(String.valueOf(GameCalculation.GetPoints(1)));
        wPtsLbl.setText(String.valueOf(GameCalculation.GetPoints(2)));

        playerLbl.setText(playerStr);
    }


    // runs on start on scene opening
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        grid.setStyle("-fx-background-color: #228B22");
        FillMap();
        GameCalculation.CalculateStartingField();
        GameCalculation.CalculateValidPlays(1);
        SetMap();
        player= 1;
        selected = false;
        passed = false;
        gameOver = false;
        winnerLbl.setText("");
        SetLabels();


        util.StopMusic();
        util.PlayMusic("/Big Enough.mp3");



    }


    //returns to main menu
    public void BacktoMainMenu(MouseEvent mouseEvent)
            throws IOException
    {
        util.PlaySFX("/button.wav");
        Main.currentStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("main menu.fxml"), null, new JavaFXBuilderFactory(), new Callback<Class<?>, Object>() {

            @Override
            public Object call(Class<?> param)
            {
                return new MainMenuController();
            }
        })));
    }
}
