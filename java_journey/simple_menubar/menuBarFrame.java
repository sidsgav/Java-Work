package mini_projects;

import java.awt.FlowLayout;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

public class menuBarFrame extends JFrame implements ActionListener{

	JMenuBar menuBar;
	JMenu fileMenu;
	JMenu editMenu;
	JMenu helpMenu;
	JMenuItem loadItem;
	JMenuItem saveItem;
	JMenuItem exitItem;

	// can add images too:
	// ImageIcon = ...
	
	menuBarFrame(){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 500);
		this.setLayout(new FlowLayout());
		
		//loadIcon = new ImageIcon(File...);
		
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		helpMenu = new JMenu("Help");
		
		loadItem = new JMenuItem("Load");
		saveItem = new JMenuItem("Save");
		exitItem = new JMenuItem("Exit");

		loadItem.addActionListener(this);
		saveItem.addActionListener(this);
		exitItem.addActionListener(this);
		
		fileMenu.setMnemonic(KeyEvent.VK_F); // ALT + f for file
		editMenu.setMnemonic(KeyEvent.VK_E); // ALT + e for edit
		helpMenu.setMnemonic(KeyEvent.VK_H); // ALT + h for help
		loadItem.setMnemonic(KeyEvent.VK_L); // l for load
		saveItem.setMnemonic(KeyEvent.VK_S); // s for save
		exitItem.setMnemonic(KeyEvent.VK_E); // e for exit
		
		fileMenu.add(loadItem);
		fileMenu.add(saveItem);
		fileMenu.add(exitItem);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);

		this.setJMenuBar(menuBar);
		this.setVisible(true);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == loadItem)
		{
			System.out.println("File loaded.");
		}
		if(e.getSource() == saveItem)
		{
			System.out.println("File saved.");
		}
		if(e.getSource() == exitItem)
		{
			System.exit(0); 
		}
		
	}
	
}
