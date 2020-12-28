package fr.duvam.media.player;

import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;

public class JFrameExample {
	public static void main(String s[]) {

		JFrame frame = new JFrame("JFrame Example");
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JLabel label = new JLabel("JFrame By Example");
		JButton button = new JButton();
		button.setText("Button");
		panel.add(label);
		panel.add(button);
		frame.add(panel);
		frame.setSize(200, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		EmbeddedMediaPlayerComponent videoPlayerComponent = new EmbeddedMediaPlayerComponent(null, null,
				new AdaptiveFullScreenStrategy(frame), null, null);
		EmbeddedMediaPlayer videoPlayer = videoPlayerComponent.mediaPlayer();

		frame.setContentPane(videoPlayerComponent);
		frame.setVisible(true);

		videoPlayer.controls().setRepeat(true);
		videoPlayer.media().play("/home/david/Vidéos/Matilda.mkv");

		GraphicsDevice screen[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		screen[0].setFullScreenWindow(frame);
		System.out.println("screen number : " + screen.length);
		if (screen.length > 0) {
			JFrame frame2 = new JFrame("Jframe2 Example");
			JPanel panel2 = new JPanel();
			panel2.setLayout(new FlowLayout());
			JLabel label2 = new JLabel("Jframe2 By Example");
			JButton button2 = new JButton();
			button.setText("Button");
			panel2.add(label2);
			panel2.add(button2);
			frame2.add(panel2);
			frame2.setSize(200, 300);
			frame2.setLocationRelativeTo(null);
			frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame2.setVisible(true);
			
			
			EmbeddedMediaPlayerComponent videoPlayerComponent2 = new EmbeddedMediaPlayerComponent(null, null,
					new AdaptiveFullScreenStrategy(frame), null, null);
			EmbeddedMediaPlayer videoPlayer2 = videoPlayerComponent.mediaPlayer();

			frame2.setContentPane(videoPlayerComponent2);
			frame2.setVisible(true);

			videoPlayer2.controls().setRepeat(true);
			videoPlayer2.media().play("/home/david/Vidéos/Higelin.mkv");
			
		}

	}
}