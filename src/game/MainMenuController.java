package game;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable{

    private GameUtilities util = GameUtilities.GetInstance();

    public void StartGame(MouseEvent mouseEvent)
            throws IOException
    {
        util.PlaySFX("/button.wav");
        Main.currentStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("game.fxml"), null, new JavaFXBuilderFactory(), new Callback<Class<?>, Object>() {

            @Override
            public Object call(Class<?> param)
            {
                return new GameController();
            }
        })));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        util.StopMusic();
        util.PlayMusic("/vamps.mp3");
    }

    public void QuitGame(MouseEvent mouseEvent)
    {
        util.PlaySFX("/button.wav");
        Platform.exit();
    }
}
