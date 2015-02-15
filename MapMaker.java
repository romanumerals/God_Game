enum Tiles{
	OCEAN, BEACH, MOUNTAIN, GRASSLAND
}

public class MapMaker{

	public static WorldTile[][] makeTile(Tiles[][] map, int tx,int ty){
		WorldTile[][] tile = new WorldTile[10][10];
		WorldTile temp;
		Tiles up, down, left, right;

		if(map[tx][ty] == Tiles.OCEAN){
			for(int x = 0; x < 10; x++){
				for(int y = 0; y < 10; y++){
					tile[x][y] = new WorldTile(120, WorldTile.Biome.ROCK);
				}
			}
			return tile;
		}else if(map[tx][ty] == Tiles.GRASSLAND){
			for(int x = 0; x < 10; x++){
				for(int y = 0; y < 10; y++){
					tile[x][y] = new WorldTile(131, WorldTile.Biome.GRASSLAND);
				}
			}
			return tile;
		}
		return null;
	}

	public static WorldTile[][] makeIsland(){
		Tiles[][] template = makeTemplate();
		WorldTile[][]temp = new WorldTile[300][300];
		for(int x = 0; x < 30; x++){
			for(int y = 0; y < 30; y++){
				WorldTile[][] temp_tile = makeTile(template, x, y);
				for(int x1 = 0; x1 < 10; x1++){
					for(int y1 = 0; y1 < 10; y1++){
						temp[x*10+x1][y*10+y1] = temp_tile[x1][y1];
					}
				}
			}
		}
		// generates mountain
		// broken fix later

		int mx = (int)(Math.random()*150) + 75;
		int my = (int)(Math.random()*150) + 75;

		System.out.println("mountain");
		for(int m1x = mx-20; m1x < mx + 20; m1x++){
			for(int m1y = my-20; m1y < my + 20; m1y++){
				int dist = (int)(Math.sqrt((mx-m1x)*(mx-m1x) + (my-m1y)*(my-m1y)));
				int height =  130 + Math.max(35-dist, 0);

				temp[m1x][m1y].setHeight(height);
			}
		}

		// smothes it over
		for(int i = 0; i < 1; i++){
			// first loop is to do it pultiple times
			for(int x1 = 1; x1 < 299; x1++){
				for(int y1 = 1; y1 < 299; y1++){
					int sum = 0;
					for(int x = -1; x <= 1; x++){
						for(int y = -1; y <=1; y++){
							sum += temp[x1+x][y1+y].getHeight();
						}
					}
					sum /= 9;
					temp[x1][y1].setHeight(sum);
				}
			}
			// and again, but in reverse
			for(int x1 = 298; x1 > 1; x1--){
				for(int y1 = 298; y1  > 1; y1--){
					int sum = 0;
					for(int x = -1; x <= 1; x++){
						for(int y = -1; y <=1; y++){
							sum += temp[x1+x][y1+y].getHeight();
						}
					}
					sum /= 9;
					temp[x1][y1].setHeight(sum);

					if(sum >= 128  && sum <= 145 && temp[x1][y1].getBiome() == WorldTile.Biome.ROCK){
						temp[x1][y1].setBiome(WorldTile.Biome.GRASSLAND);
					}else if(sum > 145){
						temp[x1][y1].setBiome(WorldTile.Biome.ROCK);
					}

					if(temp[x1][y1].getHeight() < 128 && (besideWorldTile(temp, x1, y1, WorldTile.Biome.GRASSLAND)||(besideWorldTile(temp, x1, y1, WorldTile.Biome.BEACH) && (int)(Math.random()*4) == 1))){
						temp[x1][y1].setBiome(WorldTile.Biome.BEACH);
						temp[x1][y1].setHeight(128);
					}
				}
			}
		}

		return temp;
	}

	private static int bellNumGenerator(int low, int high, int accuracy){
		int sum = 0;
		for(int i = 1; i <= accuracy; i++){
			int val = ((int)(Math.random() * (high-low) / accuracy * 1.5)) + low + (((high-low)*(1-(1/accuracy)))/6);
			sum += val;
		}
		return sum / accuracy;

	}

	private static Tiles[][] makeTemplate(){
		Tiles[][] temp = new Tiles[30][30];
		int land_count = 275;

		for(int x = 0; x < 30; x++){
			for(int y = 0; y < 30; y++){
				temp[x][y] = Tiles.OCEAN;
			}
		}
		while(land_count > 0){
			int x = bellNumGenerator(0, 30, 2);
			int y = bellNumGenerator(0, 30, 2);
			if(temp[x][y] == Tiles.OCEAN){
				if((int)(Math.random()* 200) == 1){
					// change to mountain
					temp[x][y] = Tiles.GRASSLAND;
					land_count -= 1;
				}else{
					temp[x][y] = Tiles.GRASSLAND;
					land_count -= 1;
				}
			}
		}

		return temp;
	}

	private static boolean besideTile(Tiles[][] map, int xpos, int ypos, Tiles check){
		if(map[xpos-1][ypos] == check || map[xpos+1][ypos] == check){
			if(map[xpos][ypos-1] == check || map[xpos][ypos+1] == check){
				return true;
			}
		}
		return false;
	}

	private static boolean surroundedByTile(Tiles[][] map, int xpos, int ypos, Tiles check){
		if(map[xpos-1][ypos] == check && map[xpos+1][ypos] == check){
			if(map[xpos][ypos-1] == check && map[xpos][ypos+1] == check){
				return true;
			}
		}
		return false;
	}

	private static boolean besideWorldTile(WorldTile[][] map, int xpos, int ypos, WorldTile.Biome check){
		try{
			if(map[xpos-1][ypos].getBiome() == check || map[xpos+1][ypos].getBiome() == check){
				if(map[xpos][ypos-1].getBiome() == check || map[xpos][ypos+1].getBiome() == check){
					return true;
				}
			}
			return false;
		}catch(IndexOutOfBoundsException | NullPointerException e){return false;}
	}
}