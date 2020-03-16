package game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


//utilities, right now it only manages sound
//as singleton instance
public class GameUtilities
{
    private static GameUtilities instance;
    private GameUtilities(){}

    MediaPlayer player;
    MediaPlayer sound;

    public static GameUtilities GetInstance()
    {
        if(instance == null)
            instance = new GameUtilities();
        return instance;
    }

    public void PlayMusic(String name)
    {
        Media media = new Media(this.getClass().getResource(name).toString());
        player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setVolume(0.2);
        player.play();
    }

    public void PlaySFX(String name)
    {
        Media media = new Media(this.getClass().getResource(name).toString());
        sound = new MediaPlayer(media);
        sound.setVolume(4);
        sound.play();
    }

    public void StopMusic()
    {
        if(player != null)
            player.stop();
    }
}
