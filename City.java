public class City{
	private long age;
	private int population, worship, progress, size;
	private int x, y, height;
	private boolean suffering;
	private int time_since_growth;
	WorldTile[][] world;
	GamePanel panel;


	public City(int locx, int locy, WorldTile[][] map){
		age = 0;
		population = 10;
		size = 3;
		worship = 0;
		progress = 0;
		time_since_growth = 0;
		suffering = false;

		x = locx;
		y = locy;
		height = map[x/2][y/2].getHeight();

		world = map;
	}

	public void addPanel(GamePanel p){
		panel = p;
	}

	public void update(){
		age += 1;
		// population increase
		int newpop = 0;
		for(int i = 0; i < population / 10; i++){
			if((int)(Math.random()* 100) == 1){
				newpop += 1;
			}
		}

		int farmcount = 0;
		for(int x1 = -10; x1 < 10; x1++){
			for(int y1 = -10; y1 < 10; y1++){
				if(world[x/2+x1][y/2+y1].getBiome() == WorldTile.Biome.FARM){
					farmcount += 1;
				}
			}
		}
		newpop += farmcount / 2;

		population += newpop;
		if(population/10 > Math.pow(size, size)){
			size += 2;
		}

		progress += (population/25) + population*worship/100;
		
		if(progress >= population* population / 2){
			while(true){
				progress -= Math.pow(population, 1.5);
				int dx = (int)(Math.random() * 20) -10;
				int dy = (int)(Math.random() * 20) -10;
				if(world[x/2+dx][y/2+dy].getHeight() >= 128){
					world[x/2+dx][y/2+dy].setBiome(WorldTile.Biome.FARM);
					time_since_growth = 0;
					break;
				}
			}
		}else{
			time_since_growth++;
			if(time_since_growth> 1000){
				suffering = true;
			}
		}

		if(panel.getRain()){
			if(panel.getWaterLevel() >= height){
				suffering = true;
				if(population >= 20){
					population -= 10;
				}
			}else{
				// doubles progress in the rain
				progress += (population/50) + population*worship/100;
				population += newpop;
				if(suffering){
					suffering = false;
					worship += (int)(Math.random()*10) + 5;
					time_since_growth = 0;
				}
			}
		}

		if(panel.isArmaggedon()){
			suffering = true;
			population -= 10;
			if(population < 10){
				population = 10;
			}

		}
	}

	public long getAge(){return age;}
	public int getPopulation(){return population;}
	public int getWorship(){return worship;}
	public int getProgress(){return progress;}
	public int getSize(){return size;}
	public int getX(){return x;}
	public int getY(){return y;}
	public boolean ifSuffering(){return suffering;}
	public void setPopulation(int pop){population = pop;}

	public int getFarmCount(){
		int farmcount = 0;
		for(int x1 = -10; x1 < 10; x1++){
			for(int y1 = -10; y1 < 10; y1++){
				if(world[x/2+x1][y/2+y1].getBiome() == WorldTile.Biome.FARM){
					farmcount += 1;
				}
			}
		}
		return farmcount;
	}

	public int getWasteCount(){
		int farmcount = 0;
		for(int x1 = -10; x1 < 10; x1++){
			for(int y1 = -10; y1 < 10; y1++){
				if(world[x/2+x1][y/2+y1].getBiome() == WorldTile.Biome.WASTE){
					farmcount += 1;
				}
			}
		}
		return farmcount;
	}
}