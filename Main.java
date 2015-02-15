import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.awt.MouseInfo;

public class Main extends JFrame implements ActionListener{
	private Timer clock;
	private GamePanel screen;
	private Gui menu;
	private WorldTile[][] map;
	private City atlantis;

	public Main(){
		super("Game");
		setSize(810, 625);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		clock = new Timer(20, this);
		map = MapMaker.makeIsland();
		atlantis = new City(300, 300, map);

		menu = new Gui(this);
		add(menu);
		screen = new GamePanel(this);
		add(screen);

		atlantis.addPanel(screen);

		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args){
		Main main = new Main();
	}

	public void actionPerformed(ActionEvent evt){
		screen.update();
		screen.repaint();
		menu.repaint();
	}

	public void start(){
		clock.start();
	}

	public WorldTile[][] getMap(){return map;}
	public WorldTile getMapAt(int x, int y){return map[x][y];}
	public void setMapAt(int x, int y, WorldTile w){map[x][y] = w;}
	public City getCity(){return atlantis;}
}



class Gui extends JPanel implements KeyListener, MouseListener{
	private Main mainFrame;
	private boolean[] keys;
	private City atlantis;



	public Gui(Main m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		mainFrame = m;
		atlantis = m.getCity();

		setSize(800, 600);
	}

	public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){}

    public void paintComponent(Graphics g){
    	g.setColor(new Color(111, 111, 111));
    	g.fillRect(600, 0, 210, 600);

    	g.setColor(new Color(77, 77, 77));
    	g.fillRect(620, 40, 160, 25);
    	g.fillRect(620, 90, 160, 25);
    	g.fillRect(620, 140, 160, 25);
    	g.fillRect(620, 190, 160, 25);

    	g.setColor(Color.BLACK);
    	g.drawString("Population:  " + atlantis.getPopulation(), 625, 58);
    	g.drawString("Worship:     " + atlantis.getWorship(), 625, 108);
    	g.drawString("Progress:    " + atlantis.getProgress(), 625, 158);
    	g.drawString("Suffering?   " + (atlantis.ifSuffering()? "SUFFERING" : "HAPPY"), 625, 208);
    }
    	
}



class GamePanel extends JPanel implements KeyListener, MouseListener{
	private Main mainFrame;
	private boolean ifMousePressed;
	private boolean[] keys;
	private City atlantis;
	private boolean rain;

	private int waterLevel;
	private int waterCounter;

	private boolean armageddon;
	private int[][] fireballs;

	public GamePanel(Main m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		ifMousePressed = false;
		mainFrame = m;
		atlantis = m.getCity();

		setSize(600,600);
        addKeyListener(this);
        addMouseListener(this);

		rain = false;
		waterCounter = 0;
		waterLevel = 128;

		armageddon = false;
		fireballs = new int[10][3];
	}

	public void addNotify(){
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }

	public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
    	if(e.getKeyCode() == KeyEvent.VK_R){
    		if(rain){
    			rain = false;
    		}else{
    			rain = true;
    		}
    	}else if(e.getKeyCode() == KeyEvent.VK_A){
    		armageddon = true;
    	}else if (e.getKeyCode() == KeyEvent.VK_M){
    		armageddon = false;
    	}

        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {ifMousePressed = false;}
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){ifMousePressed = true;}

    public void update(){
    	atlantis.update();

    	if(armageddon){
    		rain = false;
    		for(int j = 0; j < 10; j++){
    			int[] i = fireballs[j];
    			if(i[0] == 0 && i[1] == 0){
    				i[0] = (int)(Math.random() * 578) + 10;
    				i[1] = (int)(Math.random() * 578) + 10;
    				i[2] = 255;
    			}else{
    				i[2] -=1;
    				if(mainFrame.getMapAt(i[0]/2, i[1]/2).getHeight() >= i[2]){
    					for(int x = -5; x <= 5; x++){
    						for(int y = -5; y <= 5; y++){
    							if(Math.abs(x)+Math.abs(y) <= 5){
    								WorldTile bombard = mainFrame.getMapAt(i[0]/2+x, i[1]/2+y);
    								bombard.setHeight(bombard.getHeight() - 5);
    								bombard.setBiome(WorldTile.Biome.WASTE);
    							}
    						}
    					}
    					fireballs[j] = new int[3];
    				}
    			}
    		}
    	}

    	if(rain){
    		waterCounter += 1;
    		if(waterCounter > 500){
    			waterLevel+= 1;
    			waterCounter = 0;
    		}
    	}else{
    		waterCounter -= 1;
    		if(waterCounter < -500){
				waterLevel -= 1;
				waterCounter = 0;
			}
    	}

    	if(ifMousePressed){
    		Point mouse = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
			int x = (mouse.x - offset.x) / 2;
			int y = (mouse.y - offset.y) / 2;
			if(x < 600 && y < 600){
				for(int x1 = -2; x1 <= 2; x1++){
					for(int y1 = -2; y1 <= 2; y1++){
						if(Math.abs(x1) + Math.abs(y1) <= 2){
							WorldTile temp = mainFrame.getMapAt(x-x1, y-y1);
							temp.setBiome(WorldTile.Biome.WASTE);
						}
					}
				}
			}
    	}
    }

    public boolean getRain(){return rain;}
    public int getWaterLevel(){return waterLevel;}

    public void paintComponent(Graphics g){ 	
    	g.setColor(Color.BLACK);
    	g.fillRect(0, 0, getWidth(), getHeight());
    	WorldTile temp;
    	for(int x = 0; x < 300; x++){
			for(int y = 0; y < 300; y++){
				temp = mainFrame.getMapAt(x, y);
				if(temp.getHeight() < waterLevel){
					g.setColor(Color.BLUE);
					g.fillRect(x * 2, y * 2, 2, 2);
				}else if(temp.getBiome() == WorldTile.Biome.GRASSLAND){
					g.setColor(new Color(100, 150, 0));
					g.fillRect(x * 2, y *2, 2, 2);
				}else if(temp.getBiome() == WorldTile.Biome.BEACH){
					g.setColor(new Color(238, 221, 130));
					g.fillRect(x * 2, y *2, 2, 2);
				}else if(temp.getBiome() == WorldTile.Biome.FARM){
					g.setColor(new Color(0, 255, 0));
					g.fillRect(x * 2, y *2, 2, 2);
				}else if(temp.getBiome() == WorldTile.Biome.ROCK){
					int shade = temp.getHeight();
					g.setColor(new Color(shade, shade, shade));
					g.fillRect(x * 2, y * 2, 2, 2);
				}else if(temp.getBiome() == WorldTile.Biome.WASTE){
					int shade = temp.getHeight();
					g.setColor(new Color(shade, shade - 25, shade -50));
					g.fillRect(x * 2, y * 2, 2, 2);
				}
			}
		}
		g.setColor(new Color(255, 222, 173));
		int x = atlantis.getX();
		int y = atlantis.getY();
		int size = atlantis.getSize();

		g.fillRect(x-size, y-size, size * 2, size * 2);

		g.setColor(new Color(139, 69, 19));
		//towers
		g.fillRect(x-size, y-size, 2, 2);
		g.fillRect(x+size-2,y-size, 2, 2);
		g.fillRect(x-size, y+size-2, 2, 2);
		g.fillRect(x+size-2, y+size-2, 2, 2);
		//walls
		g.fillRect(x-size+1, y-size+1, 1, size*2-3);
		g.fillRect(x-size+1, y-size+1, size*2-3, 1);
		g.fillRect(x-size+1, y+size-2, size*2-2, 1);
		g.fillRect(x+size-2, y-size+1, 1, size*2-2);

		if(rain){
			g.setColor(new Color(0, 0, 255, 50));
			g.fillRect(0, 0, getHeight(), getHeight());
		}else if(armageddon){
			g.setColor(Color.RED);
			for(int[] i : fireballs){
				g.fillOval(i[0]-5, i[1]-5, 10, 10);
				if(mainFrame.getMapAt(i[0]/2, i[1]/2).getHeight() >= i[2] - 5){
					g.fillOval(i[0]-10, i[1]-10, 20, 20);
					if(mainFrame.getMapAt(i[0]/2, i[1]/2).getHeight() >= i[2] - 1){
						g.drawOval(i[0] - 15, i[1] - 15, 30, 30);
					}
				}
			}
			g.setColor(new Color(255, 0, 0, 100));
			g.fillRect(0, 0, getHeight(), getHeight());
		}
    }
}