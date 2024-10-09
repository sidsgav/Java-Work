package mini_projects;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import javax.swing.JFrame;

public class fileChooserFrame extends JFrame implements ActionListener{
	
	
	JButton button;
	
	fileChooserFrame(){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		
		button = new JButton("Select File");
		button.addActionListener(this);
		
		this.add(button);
		this.pack();
		this.setVisible(true);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == button)
		{
			JFileChooser fileChooser = new JFileChooser();
			
			//fileChooser.setCurrentDirectory(new File("path of whatever(Desktop etc)..."));
			
			//int response = fileChooser.showOpenDialog(null); // select file to open
			int response = fileChooser.showSaveDialog(null); // select file to save

			
			if(response == JFileChooser.APPROVE_OPTION)
			{
				File file = new File(fileChooser.getSelectedFile().getAbsoluteFile(), null);
				System.out.println(file);
				
			}
			
		}
		
	}
	

}
