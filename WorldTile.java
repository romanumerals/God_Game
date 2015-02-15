public class WorldTile{
	private int height;
	private Biome biome;

	public enum Biome{
		BEACH, ROCK, GRASSLAND, WASTE, FARM
	}	

	public WorldTile(int h, Biome b){
		height = h;
		biome = b;
	}

	public int getHeight(){return height;}
	public Biome getBiome(){return biome;}
	public void setHeight(int h){height = h;}
	public void setBiome(Biome b){biome = b;}
}