import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class GUI extends JFrame implements ActionListener {

	private JMenuItem item1 = new JMenuItem("Open");
	private JMenuItem item2 = new JMenuItem("New Window");
	private JMenuItem item3 = new JMenuItem("Quit");
	
	private JMenuItem toolItem01 = new JMenuItem("Scale");
	private JMenuItem toolItem02 = new JMenuItem("Rotate 90");
	private JMenuItem toolItem03 = new JMenuItem("Rotate 180");
	private JMenuItem toolItem04 = new JMenuItem("Rotate 270");
	private JMenuItem toolItem05 = new JMenuItem("Reflect over X axis");
	private JMenuItem toolItem06 = new JMenuItem("Reflect over Y axis");
	private JMenuItem toolItem07 = new JMenuItem("Sharpen");
	private JMenuItem toolItem08 = new JMenuItem("Equalize");
	private JMenuItem toolItem09 = new JMenuItem("Edge Detection");
	private JMenuItem toolItem10 = new JMenuItem("Line Detection");
	private JMenuItem toolItem11 = new JMenuItem("Blend Image");
	
	BufferedImage windowImage;
	JFrame F;
	
	public GUI(BufferedImage imageData, String title)
	{
		

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		F = new JFrame(title);
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu ("File");
		JMenu toolsMenu = new JMenu ("Tools");

		
		item1.addActionListener(this);
		item2.addActionListener(this);
	    item3.addActionListener(this);
	
		menu1.add(item1);
		menu1.add(item2);
		menu1.add(item3);	
		
		menubar.add(menu1);
		
		toolItem01.addActionListener(this);
		toolItem02.addActionListener(this);
		toolItem03.addActionListener(this);
		toolItem04.addActionListener(this);
		toolItem05.addActionListener(this);
		toolItem06.addActionListener(this);
		toolItem07.addActionListener(this);
		toolItem08.addActionListener(this);
		toolItem09.addActionListener(this);
		toolItem10.addActionListener(this);
		toolItem11.addActionListener(this);
		
		toolsMenu.add(toolItem01);
		toolsMenu.add(toolItem02);
		toolsMenu.add(toolItem03);
		toolsMenu.add(toolItem04);
		toolsMenu.add(toolItem05);
		toolsMenu.add(toolItem06);
		toolsMenu.add(toolItem07);
		toolsMenu.add(toolItem08);
		toolsMenu.add(toolItem09);
		toolsMenu.add(toolItem10);
		toolsMenu.add(toolItem11);
		
		menubar.add(toolsMenu);
		
		F.setJMenuBar(menubar);
		F.setSize(300, 100);
		F.setVisible(true);
	
		
		if(imageData != null){
			F.add(new Draw(imageData));
			F.setSize(imageData.getWidth(), imageData.getHeight());
			windowImage = imageData;
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
		if(windowImage != null)
		{
			//scale image
			if(E.getSource() == toolItem01){
				//String s= "How much do you want to scale the image by?";
				String str = (String)JOptionPane.showInputDialog("Enter image scale: ");
				String[] val = str.split("\\s+");
				
				try{
					float scale=  Float.parseFloat(val[0]);
					BufferedImage image= ImageProc.scale(windowImage, scale);
					createWindow(image, "Untitled");
				}
				catch(NumberFormatException e)
				{
					JOptionPane.showMessageDialog(F,
						    "Please enter a numerical value",
						    "Input Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
			//90 degree clockwise rotation
			if(E.getSource() == toolItem02){	
				BufferedImage image= ImageProc.rotateBy90(windowImage, 90);
				createWindow(image, "Untitled");
			}
			//180 degree clockwise rotation
			if(E.getSource() == toolItem03){
				BufferedImage image = ImageProc.rotateBy90(windowImage, 180);
				createWindow(image, "Untitled");
			}
			//270 degree clockwise rotation
			if(E.getSource() == toolItem04){
				BufferedImage image = ImageProc.rotateBy90(windowImage, 270);
				createWindow(image, "Untitled");
			}
			//relfection over x axis
			if(E.getSource() == toolItem05){
				BufferedImage image = ImageProc.reflect(windowImage, ImageProc.REFLECT_OVER_X_AXIS);
				createWindow(image, "Untitled");
			}
			//reflection across y axis
			if(E.getSource() == toolItem06){
				BufferedImage image = ImageProc.reflect(windowImage, ImageProc.REFLECT_ACROSS_Y_AXIS);
				createWindow(image, "Untitled");
			}
			//image sharpening
			if(E.getSource() == toolItem07){
				BufferedImage image = ImageProc.sharpenImage(windowImage);
				createWindow(image, "Untitled");
			}
			//image equalization
			if(E.getSource() == toolItem08){
				BufferedImage image = ImageProc.equalizeImage(windowImage);
				createWindow(image, "Untitled");
			}
			//edge detection
			if(E.getSource() == toolItem09){
				BufferedImage image= ImageProc.performCannyTransform(windowImage);
				createWindow(image, "Untitled");
			}
			//line detection
			if(E.getSource() == toolItem10){
				BufferedImage image= ImageProc.lineTransform(windowImage);
				createWindow(image, "Untitled");
			}
			//blend images
			if(E.getSource() == toolItem11)
			{
				
			}
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