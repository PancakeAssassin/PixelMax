import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class GUI extends JFrame implements ActionListener {

	private JMenuItem item1 = new JMenuItem("Open");
	private JMenuItem item2 = new JMenuItem("New Window");
	private JMenuItem item3 = new JMenuItem("Cancel");
	
	public GUI(BufferedImage imageData)
	{
		
		JFrame F = new JFrame("HI");
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu ("File");
		
		item1.addActionListener(this);
		item2.addActionListener(this);
	
	
		menu1.add(item1);
		menu1.add(item2);
	
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
		    Graphics2D   g2d=(Graphics2D)graphic.getGraphics();
            g2d.dispose();
            g2d.drawImage(graphic,null,0,0);
    	} 	
	}
	public void actionPerformed(ActionEvent E)
	{
		if(E.getSource() == item1){
			JFileChooser F = new JFileChooser(".");
			F.setFileSelectionMode(JFileChooser.FILES_ONLY);
			F.showOpenDialog(null);
			File file = F.getSelectedFile();
			imageCopy(file.getPath());
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
		new GUI(imageData);
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
		new GUI(null);
	}
	public static void main(String args[])
	{
		createWindow();
	}
}