package de.hsbi.lockgame.io;

import java.io.IOException;
import javax.sound.sampled.*;

public final class MusicPlayer {

  /**
   * Draft.
   *
   * <p>Plays a WAV audio resource on a continuous loop.
   *
   * @param resourcePath e.g. "/audio/music.wav"
   */
  public static void playLoopFromResource(String resourcePath) {
    var t =
        new Thread(
            () -> {
              try (var in = MusicPlayer.class.getResourceAsStream(resourcePath)) {
                if (in == null) throw new IOException("audio file not found: " + resourcePath);

                try (var ais = AudioSystem.getAudioInputStream(in)) {
                  var clip = AudioSystem.getClip();
                  clip.open(ais);
                  clip.loop(Clip.LOOP_CONTINUOUSLY);
                  // Do not close the clip, otherwise the music will stop immediately.
                  // The thread ends here, but the clip continues running in its own system thread.
                }

              } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
              }
            },
            "MusicPlayerThread");

    t.setDaemon(true); // the program will still terminate even if only the music is running
    t.start();
  }
}
