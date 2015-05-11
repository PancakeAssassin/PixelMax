import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;

public class PixelMax extends JFrame implements ActionListener {

	private JMenuItem item1 = new JMenuItem("Open");
	private JMenuItem item2= new JMenuItem("Save");
	private JMenuItem item3 = new JMenuItem("New Window");
	private JMenuItem item4 = new JMenuItem("Quit");
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
	private JMenuItem toolItem11 = new JMenuItem("Blend Images");
	private JMenuItem toolItem12 = new JMenuItem("Create PhotoMosaic");
	//photomosaic variables
	private JMenuItem toolItem13 = new JMenuItem("Configure PhotoMosaic...");
	public static final String[] threads = { "1", "2", "3", "4" }; /* This will affect performance, especially on a multicore system */
//	private int tileWidth = -1; /* set this sufficiently large to allow the hardware to produce results in a reasonable time */
//	private int tileHeight = -1; /* set this sufficiently large to allow the hardware to produce results in a reasonable time */
//	private String mosaicBluePrint = null;
//	private String mosaicFolder = null;
	PhotoMosaic temp;
	PMDialog dialog;
	BufferedImage windowImage;
	JFrame F;
	
	public PixelMax(BufferedImage imageData, String title)
	{
		
		F = new JFrame(title);
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu ("File");
		JMenu toolsMenu = new JMenu ("Tools");
		JMenu pmMenu = new JMenu("Photo Mosaic");

		
		item1.addActionListener(this);
		item2.addActionListener(this);
		item3.addActionListener(this);
	    item4.addActionListener(this);
	
		menu1.add(item1);
		menu1.add(item2);
		menu1.add(item3);
		menu1.add(item4);	
		
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
		
		toolItem12.addActionListener(this);
		toolItem13.addActionListener(this);
		
		pmMenu.add(toolItem12);
		pmMenu.add(toolItem13);
		
		menubar.add(pmMenu);
		
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
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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
            g.drawImage(graphic,0,0,null);
    	} 	
	}
	public void actionPerformed(ActionEvent E)
	{
		if(E.getSource() == item1){
			JFileChooser F = new JFileChooser("Open");
			F.setFileSelectionMode(JFileChooser.FILES_ONLY);
			F.showOpenDialog(null);
			File file = F.getSelectedFile();
			createWindow(imageCopy(file.getPath()),file.getPath());
		}
		if(E.getSource() == item2)
		{
			JFileChooser F = new JFileChooser("Save");
			int rval = F.showSaveDialog(null);
			if(rval == JFileChooser.APPROVE_OPTION)
			{
				String filename= F.getSelectedFile().toPath().toString();
				//String directory= F.getCurrentDirectory().toString();
				Utilities.saveImage(windowImage, filename);
			}
			if(rval== JFileChooser.CANCEL_OPTION)
			{
				
			}
		}
		if(E.getSource() == item3){
			createWindow();
		}
		if(E.getSource() == item4){
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
					BufferedImage image= Utilities.scale(windowImage, scale);
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
				BufferedImage image= Utilities.rotateBy90(windowImage, 90);
				createWindow(image, "Untitled");
			}
			//180 degree clockwise rotation
			if(E.getSource() == toolItem03){
				BufferedImage image = Utilities.rotateBy90(windowImage, 180);
				createWindow(image, "Untitled");
			}
			//270 degree clockwise rotation
			if(E.getSource() == toolItem04){
				BufferedImage image = Utilities.rotateBy90(windowImage, 270);
				createWindow(image, "Untitled");
			}
			//relfection over x axis
			if(E.getSource() == toolItem05){
				BufferedImage image = Utilities.reflect(windowImage, Utilities.REFLECT_OVER_X_AXIS);
				createWindow(image, "Untitled");
			}
			//reflection across y axis
			if(E.getSource() == toolItem06){
				BufferedImage image = Utilities.reflect(windowImage, Utilities.REFLECT_ACROSS_Y_AXIS);
				createWindow(image, "Untitled");
			}
			//image sharpening
			if(E.getSource() == toolItem07){
				BufferedImage image = Utilities.sharpenImage(windowImage);
				createWindow(image, "Untitled");
			}
			//image equalization
			if(E.getSource() == toolItem08){
				BufferedImage image = Utilities.equalizeImage(windowImage);
				createWindow(image, "Untitled");
			}
			//edge detection
			if(E.getSource() == toolItem09){
				BufferedImage image= Utilities.performCannyTransform(windowImage);
				createWindow(image, "Untitled");
			}
			//line detection
			if(E.getSource() == toolItem10){
				BufferedImage image= Utilities.lineTransform(windowImage);
				createWindow(image, "Untitled");
			}
			if(E.getSource() == toolItem11)
			{
				JFileChooser F = new JFileChooser("Open the image to blend");
				F.setFileSelectionMode(JFileChooser.FILES_ONLY);
				F.showOpenDialog(null);
				File file = F.getSelectedFile();
				BufferedImage otherImage= imageCopy(file.getPath());
				BufferedImage image= Utilities.blendImages(windowImage, otherImage);
				createWindow(image, "Untitled");
			}
		}
		//PhotoMosaic
		//blend images
		if(E.getSource() == toolItem12)
		{
			if(dialog.ready()){
				temp = new PhotoMosaic(dialog.getFilename(), dialog.getTilewidth(), 
				dialog.getTileheight(), dialog.getDirectory(), 
				dialog.getNumThreads(), true, true);
				System.out.println("poststatement");
				createWindow(temp.getResultImg(),"PhotoMosaic");
			} else { 
					System.out.println("You must make sure that PhotoMosaic is configured first.");
			}	
		}
		if(E.getSource() == toolItem13)
		{
			dialog = new PMDialog(new JFrame(), "Configure PhotoMosaic");
			dialog.setSize(300, 150);
		}
	}
	//create a new blank window
	public static void createWindow(BufferedImage imageData){
		new PixelMax(imageData,"");
	}
	public static void createWindow(BufferedImage imageData, String title){
		if(title == null) title = "";
		new PixelMax(imageData, title);
	}
	public static BufferedImage imageCopy(String path){
		BufferedImage img = null;
		try {
    		img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	return img;
	}
	
	//create a new window with an imported image object
	public static void createWindow(){
		new PixelMax(null,"");
	}
	public static void main(String args[])
	{
		createWindow(null, "Untitled");
	}
}