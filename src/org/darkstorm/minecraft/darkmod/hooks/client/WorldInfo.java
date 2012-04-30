package org.darkstorm.minecraft.darkmod.hooks.client;

public interface WorldInfo {
	public long getRandomSeed();

	public void setRandomSeed(long randomSeed);

	public WorldType getWorldType();

	public void setWorldType(WorldType worldType);

	public int getSpawnX();

	public void setSpawnX(int spawnX);

	public int getSpawnY();

	public void setSpawnY(int spawnY);

	public int getSpawnZ();

	public void setSpawnZ(int spawnZ);

	public long getTime();

	public void setTime(long time);

	public long getLastPlayed();

	public void setLastPlayed(long lastPlayed);

	public long getSizeOnDisk();

	public void setSizeOnDisk(long sizeOnDisk);

	public String getLevelName();

	public void setLevelName(String levelName);

	public int getVersion();

	public void setVersion(int version);

	public boolean isRaining();

	public void setRaining(boolean raining);

	public int getRainTime();

	public void setRainTime(int rainTime);

	public boolean isThundering();

	public void setThundering(boolean thundering);

	public int getThunderTime();

	public void setThunderTime(int thunderTime);

	public SerializableMap getPlayer();

	public void setPlayer(SerializableMap player);

	public int getDimension();

	public void setDimension(int dimension);
}
