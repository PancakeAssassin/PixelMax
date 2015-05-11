/* After researching several methods to produce photomosaics, I found one design
* pattern that I particularly agreed with. *
* This code is based in part on a more elaborate design for a PhotoMosaic *
* program featured here: http://www.rictin.com/img-mosaic-generator by Richard Tingstad. *
* A lot of time went into making major changes. */

import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
public class PhotoMosaic {

	private final int windowWidth = 1200;
	private final int windowHeight = 600;
	private String[][][] filenames;
	private BufferedImage img;
	private BufferedImage preview;
	private int[][][] d, v, dv;
	private long[][][] dp;
	private int tw, th, w, h, width, height, n, wt, ht;
	private JFrame frame;
	private int numberOfThreads;
	private boolean gui;

	public BufferedImage getImgSrc(){ return img; }
	public BufferedImage getResultImg(){ return preview; }

	public PhotoMosaic(String filename, int tw, int th, String directory, int numberOfThreads, boolean gui, boolean stay) {
		if (numberOfThreads < 1) {
			System.err.println("Number of threads can not be less than 1.");
			System.exit(1);
		}
		this.numberOfThreads = numberOfThreads;
		this.tw = tw;
		this.th = th;
		this.gui = gui;
		img = read(filename);
		if (img == null) System.exit(1);
		if (img.getWidth() < tw || img.getHeight() < th) {
			System.err.println("Selected image cannot be smaller than the tiles which compose it.");
			System.exit(1);
		}
		n = 3;
		width = img.getWidth();
		height = img.getHeight();
		final boolean[] stop = new boolean[1];
		if (gui) {
			preview = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_3BYTE_BGR);
			preview.getGraphics().drawImage(img, 0, 0, windowWidth, windowHeight, 0, 0, img.getWidth(), img.getHeight(), null);
			frame = new JFrame("PixelMax");
			if (img.getWidth() > img.getHeight()) frame.setSize(800, img.getHeight() * 800 / img.getWidth());
			else frame.setSize(img.getWidth() * 600 / img.getHeight(), 600);
			Prev panel = new Prev(preview);
			panel.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent arg0) {
					if (!stop[0] && JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(frame,"Do you want to stop reading tiles and finish picture?", "End?", JOptionPane.YES_NO_OPTION)) stop[0] = true;
				}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
			});
			frame.getContentPane().add(panel, BorderLayout.CENTER);
			frame.addWindowListener(new WindowListener() {
				public void	windowActivated(WindowEvent e) {}
				public void	windowClosed(WindowEvent e) {}
				public void	windowClosing(WindowEvent e) {
					if (stop[0])
						System.exit(0);
					//stay = false;
				}
				public void	windowDeactivated(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void	windowIconified(WindowEvent e) {}
				public void	windowOpened(WindowEvent e) {}
			});
			frame.setVisible(true);
		}

		w = width / tw;
		h = height / th;

		if (numberOfThreads > h) {
			System.out.println("Using " + h + " threads.");
			this.numberOfThreads = h;
		}

		filenames = new String[w][h][n];
		v = new int[w][h][n]; 
		d = new int[w][h][n]; 
		for (int i = 0; i < d.length; ++i) {
			for (int j = 0; j < d[0].length; ++j) {
				for (int k = 0; k < n; ++k) {
					d[i][j][k] = Integer.MAX_VALUE;
				}
			}
		}

		wt = tw * windowWidth / (w * tw);//img.getWidth();
		ht = th * windowHeight / (h * th);//img.getHeight();
		File file = new File(directory);
		if (!file.exists() || !file.isDirectory()) {
			System.err.println(directory + " is not a directory.");
			System.exit(1);
		}
		File[] list = file.listFiles();
		int c = 0;
		int p = -1;
		int depth = 0;
		Map<Integer, Integer> indices = new HashMap<Integer, Integer>();
		while(true) {
			p++;
			boolean br = false;
			while (true) { 
				while (p >= list.length) {
					if (depth == 0) {
						br = true;
						break;
					}
					p = indices.get(--depth) + 1;
					file = file.getParentFile();
					list = file.listFiles();
				}
				if (br) break;
				if (list[p].isDirectory()) {
					file = list[p];
					list = file.listFiles();
					if (list == null) {
						file = file.getParentFile();
						list = file.listFiles();
						p++;
					}
					else {
						indices.put(depth++, p);
						p = 0;
					}
				}
				if (list != null && p < list.length && !list[p].isDirectory()) break;
			}
			if (br) break;
			BufferedImage t = read(list[p]);
			if (t == null) continue;
			final BufferedImage t2 = new BufferedImage(tw, th, BufferedImage.TYPE_3BYTE_BGR);
			t2.getGraphics().drawImage(t, 0, 0, tw, th, 0, 0, t.getWidth(), t.getHeight(), new Color(0,0,0), null);
			final String[] f = new String[1];
			try { f[0] = list[p].getCanonicalPath(); }
			catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			if (numberOfThreads == 1) {
				doWork(0, t2, c, f[0]);
			}
			else {
				final ArrayList<Thread> threads = new ArrayList<Thread>(numberOfThreads);
				final int k = c;
				for (int i = 0; i < numberOfThreads; i++) {
					final int number = i;
					Thread r = new Thread(new Runnable() {
						public void run() {
							doWork(number, t2, k, f[0]);
						}
					});
					r.start();
					threads.add(r);
				}
				for (Thread r : threads) {
					try {
						r.join();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if (stop[0]) break;
			System.out.println(c++);
		}
		if (c == 0) {
			System.err.println("No tiles successfully read.");
			System.exit(1);
		}

		dp = new long[w][h][n];
		dv = new int[w][h][n];

		long min = Long.MAX_VALUE;
		for (int k = 0; k < n; k++) {
			dp[0][0][k] = d[0][0][k];
			dv[0][0][k] = 1983;
		}

		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				if (i == 0 && j == 0) {
					continue;
				}
				for (int m = 0; m < n; m++) { // What's the cost of using m in i,j?
					min = Integer.MAX_VALUE;

					if (i > 0) {
						for (int k = 0; k < n; k++) {
							if (dp[i - 1][j][k] < min) {
								if (v[i - 1][j][k] == v[i][j][m]) continue;
								if (j > 0) {
									int t = k;
									for (int x = i - 1; x >= 0; x--) t = dv[x][j][t];
									for (int x = w - 1; x > i; x--) t = dv[x][j - 1][t];
									if (v[i][j - 1][t] == v[i][j][m]) continue;
								}
								min = dp[i - 1][j][k];
								dv[i][j][m] = k;
							}
						}
					} else {
						for (int k = 0; k < n; k++) {
							if (dp[w - 1][j - 1][k] < min) {
								int t = k;
								for (int x = w - 1; x > 0; x--) t = dv[x][j - 1][t];
								if (v[i][j - 1][t] == v[i][j][m]) continue;
								min = dp[w - 1][j - 1][k];
								dv[i][j][m] = k;
							}
						}
					}
					if (min == Integer.MAX_VALUE) { dp[i][j][m] = min;} 
					else { dp[i][j][m] = min + d[i][j][m]; }
				}
			}
		}

		min = Long.MAX_VALUE;
		int k = 0;
		for (int m = 0; m < n; m++) {
			if (dp[w - 1][h - 1][m] < min) {
				min = dp[w - 1][h - 1][m];
				k = m;
			}
		}
		System.out.println("Total diff: " + min);
		for (int j = h - 1; j > -1; j--) {
			for (int i = w - 1; i > -1; i--) {
				if (v[i][j][0] != v[i][j][k]) {
					v[i][j][0] = v[i][j][k];
					filenames[i][j][0] = filenames[i][j][k];

					if (gui) {
						BufferedImage t = read(filenames[i][j][k]);
						if (t == null) { preview.getGraphics().fillRect(i * wt, j * ht, wt, ht); }
						else { preview.getGraphics().drawImage(t, i*wt, j*ht, (i+1)*wt, (j+1)*ht, 0, 0, t.getWidth(), t.getHeight(), new Color(0,0,0), null);}
						frame.repaint();
					}
				}
				k = dv[i][j][k];
			}
		}
		System.out.println("All done!");
		stop[0] = true;
		if (!stay) System.exit(0);
	}

	private void doWork(int number, BufferedImage t, int c, String filename) {
		int diff;
		for (int j = number; j < h; j += numberOfThreads) {
			for (int i = 0; i < w; ++i) {
				diff = match(t, i, j);
				if (diff < d[i][j][n - 1]) {
					boolean better = diff < d[i][j][0];
					add(i, j, diff, c, filename);
					if (better && gui) {
						preview.getGraphics().drawImage(t, i * wt, j*ht, (i+1)*wt, (j+1)*ht, 0, 0, t.getWidth(), t.getHeight(), null);
						frame.repaint();
					}
				}
			}
		}
	}

	private void add(int i, int j, int diff, int m, String filename) {
		boolean added = false;
		for (int k = 0; k < 3; ++k) {
			if (diff < d[i][j][k]) {
				if (!added) {
					added = true;
				}
				int t = d[i][j][k];
				d[i][j][k] = diff;
				diff = t;
				t = v[i][j][k];
				v[i][j][k] = m;
				m = t;
				String s = filenames[i][j][k];
				filenames[i][j][k] = filename;
				filename = s;
			}
		}
	}

	private int abs(int n) {
		if (n >= 0) return n;
		return -n;
	}

	private int match(BufferedImage tile, int i, int j) {
		int offx = i * tw;
		int offy = j * th;
		Color c1, c2;
		int diff = 0;
		for (int y = 0; y < th; y++) {
			for (int x = 0; x < tw; x++) {
				c1 = new Color(tile.getRGB(x, y));
				c2 = new Color(img.getRGB(offx + x, offy + y));
				diff += abs(c1.getRed() - c2.getRed()) + abs(c1.getGreen() - c2.getGreen()) + abs(c1.getBlue() - c2.getBlue());
				if (diff >= d[i][j][n - 1]) { return Integer.MAX_VALUE; }
			}
		}
		return diff;
	}

	private BufferedImage read(String filename) {
		try { return read(new File(filename)); }
		catch (Exception e) {
			System.err.println("Error reading " + filename);
			e.printStackTrace();
		}
		return null;
	}

	private BufferedImage read(File file) {
		BufferedImage t = null;
		try {
			t = ImageIO.read(file);
			while (t.getHeight() == -1 || t.getWidth() == -1) {
				try { Thread.sleep(100); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		catch (Exception e) {
			System.err.println("Error reading " + file);
			e.printStackTrace();
			return null;
		}
		System.out.println("Read " + file);
		return t;
	}
	class Prev extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BufferedImage img;
		public Prev(BufferedImage img) {
			super();
			this.img =img;
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), 0, 0, img.getWidth(), img.getHeight(), null);
		}
	}
}