import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class GUI extends JFrame implements ActionListener {

	private JMenuItem item1 = new JMenuItem("Open");
	private JMenuItem item2 = new JMenuItem("New Window");
	private JMenuItem item3 = new JMenuItem("Quit");
	
	public GUI(BufferedImage imageData, String title)
	{
		
		JFrame F = new JFrame(title);
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu ("File");
		
		item1.addActionListener(this);
		item2.addActionListener(this);
	    item3.addActionListener(this);
	
		menu1.add(item1);
		menu1.add(item2);
		menu1.add(item3);	
		
		menubar.add(menu1);
		
		F.setJMenuBar(menubar);
		F.setSize(300, 100);
		F.setVisible(true);
	
		if(imageData != null){
			F.add(new Draw(imageData));
		}
	}
	
	class Draw extends JPanel {
		BufferedImage graphic = null;
	    public Draw(BufferedImage imgIn) {
        	setBorder(BorderFactory.createLineBorder(Color.black));
        	graphic = imgIn;
    	}

    	public Dimension getPreferredSize() {
        	return new Dimension(250,200);
    	}

    	public void paintComponent(Graphics g) {
        	super.paintComponent(g);       
		    Graphics g2 = graphic.getGraphics();
            g.drawImage(graphic,0,0,null);
    	} 	
	}
	public void actionPerformed(ActionEvent E)
	{
		if(E.getSource() == item1){
			JFileChooser F = new JFileChooser("");
			F.setFileSelectionMode(JFileChooser.FILES_ONLY);
			F.showOpenDialog(null);
			File file = F.getSelectedFile();
			createWindow(imageCopy(file.getPath()),file.getPath());
		}
		if(E.getSource() == item2){
			createWindow();
		}
		if(E.getSource() == item3){
			System.exit(0);
		}
	}
	//create a new blank window
	public static void createWindow(BufferedImage imageData){
		new GUI(imageData,"");
	}
	public static void createWindow(BufferedImage imageData, String title){
		if(title == null) title = "";
		new GUI(imageData, title);
	}
	public static BufferedImage imageCopy(String path){
		BufferedImage img = null;
		try {
    		img = ImageIO.read(new File(path));
		} catch (IOException e) {
		}
	return img;
	}
	
	//create a new window with an imported image object
	public static void createWindow(){
		new GUI(null,"");
	}
	public static void main(String args[])
	{
		createWindow(imageCopy("/Users/adam/Documents/Academic/montclair/cmpt594/project/Untitled.jpeg"),"Untitled.jpeg");
	}
}
