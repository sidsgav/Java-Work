package mini_projects;

import java.awt.*;
import javax.swing.*;

public class progressBarDemo {
	
	JFrame frame = new JFrame();
	JProgressBar bar = new JProgressBar(0, 500);
	
	progressBarDemo(){
		
		bar.setValue(0);
		bar.setBounds(0, 0, 420, 50);
		bar.setStringPainted(true);
		frame.setFont(new Font("MV Boli", Font.BOLD, 25));
		frame.setForeground(Color.red);
		frame.setBackground(Color.black);
		
		frame.add(bar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(420, 420);;
		frame.setLayout(null);
		frame.setVisible(true);
		
		fill();
		
	}
	
	public void fill() {
		int counter = 500;
		
		while(counter > 0)
		{
			bar.setValue(counter);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter -= 1;
		}
		bar.setString("Done!");
	}

}
