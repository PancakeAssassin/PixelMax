
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import java.io.File;

public class PMDialog extends JDialog implements ActionListener {


	String filename;
	public String getFilename(){ return filename; }
	int tw;
	public int getTilewidth(){ return tw; }
	int th;
	public int getTileheight(){ return th; }
	String directory;
	public String getDirectory(){ return directory; }
	int numberOfThreads;
	public int getNumThreads(){ return numberOfThreads; }
	boolean gui;
	public boolean isGUI(){ return gui; }
	final boolean stay=false;
	public boolean getBools(){ return stay; }
	
	Button submit;
	Button dirButton;
	Button imgButton;
	JComboBox threadCount;
	JComboBox thCount;
	JComboBox twCount;
	JLabel imagename;
	JLabel srcDir;
	
	public PMDialog(JFrame parent, String title) {
		super(parent, title);

		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
		JPanel messagePane = new JPanel();
		getContentPane().add(messagePane);
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setLayout(gridbag);

        c.fill = GridBagConstraints.BOTH;    
        c.weightx = 1.0;
        makeLabel("Blue Print Image", gridbag, c);
        imagename = makeLabel("current image", gridbag, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        makeLabel("Tile Source Folder", gridbag, c);
        srcDir = makeLabel("current folder", gridbag, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        imgButton = makeButton("Choose Image", gridbag, c);
        imgButton.addActionListener(this);
        dirButton = makeButton("Choose Directory", gridbag, c);
        dirButton.addActionListener(this);
        String[] threads = { "1", "2", "3", "4" };
        threadCount = new JComboBox(threads);
        threadCount.setSelectedIndex(1);
        threadCount.addActionListener(this);
        add(threadCount);
        String[] dimensions = { "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75","80","85","90","95","100" };
        thCount = new JComboBox(dimensions);
        thCount.setSelectedIndex(1);
        thCount.addActionListener(this);
        add(thCount);
        twCount = new JComboBox(dimensions);
        twCount.setSelectedIndex(1);
        twCount.addActionListener(this);
        add(twCount);
        submit = makeButton("Submit", gridbag, c);
        submit.addActionListener(this);
        setSize(300, 100);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
		
	}
	
	protected Button makeButton(String name, GridBagLayout gridbag, GridBagConstraints c) {
         Button button = new Button(name);
         gridbag.setConstraints(button, c);
         add(button);
         return button;
     }
     
    protected JLabel makeLabel(String field, GridBagLayout gridbag, GridBagConstraints c) {
         JLabel label = new JLabel(field);
         gridbag.setConstraints(label, c);
         add(label);
         return label;
     }
    boolean ready = false;
    public boolean ready(){
    	return ready;
    } 
    
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == submit){
			System.out.println(getTilewidth());
			System.out.println(getTileheight());
			System.out.println(getFilename());
			System.out.println(getDirectory());
			System.out.println(getNumThreads());
			ready = true;
			this.dispose();
		}
		if(e.getSource() == imgButton){
			JFileChooser F = new JFileChooser("Open");
			F.setFileSelectionMode(JFileChooser.FILES_ONLY);
			F.showOpenDialog(null);
			File file = F.getSelectedFile();
			filename = file.getPath();
			imagename.setText(filename);
		}
		if(e.getSource() == dirButton){
			JFileChooser F = new JFileChooser("Open");
			F.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			F.showOpenDialog(null);
			File file = F.getSelectedFile();
			directory = file.getPath();
			srcDir.setText(directory);
		}
		if(e.getSource() == threadCount){
			JComboBox cb = (JComboBox)e.getSource();
			if(isValid((String)cb.getSelectedItem())){
        		numberOfThreads = Integer.parseInt((String)cb.getSelectedItem());
        	} else { System.out.println("Uh Oh, That wasn't an integer greater than zero and less than the max value for an int");}
		}
		if(e.getSource() == thCount){
			JComboBox cb = (JComboBox)e.getSource();
			if(isValid((String)cb.getSelectedItem())){
        		th = Integer.parseInt((String)cb.getSelectedItem());
        	} else { System.out.println("Uh Oh, That wasn't an integer greater than zero and less than the max value for an int");}
		}
		if(e.getSource() == twCount){
			JComboBox cb = (JComboBox)e.getSource();
			if(isValid((String)cb.getSelectedItem())){
        		tw = Integer.parseInt((String)cb.getSelectedItem());
        	} else { System.out.println("Uh Oh, That wasn't an integer greater than zero and less than the max value for an int");}
		}
	}
	public boolean isValid(String integer){
      for (int j = 0;j < integer.length();j++) 
         if (Character.isDigit(integer.charAt(j))) return true; 
      return false;
	}
}