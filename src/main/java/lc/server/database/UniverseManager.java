package lc.server.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import lc.common.LCLog;
import lc.common.stargate.StargateCharsetHelper;
import lc.common.util.math.ChunkPos;
import lc.server.StargateAddress;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class UniverseManager {

	/** Default Stargate address size */
	private final static int ADDRESS_WIDTH = 9;

	/** Disk IO provider */
	private final RecordIO jsonAgent;

	/** Current working file */
	private File workFile;
	/** The random number generator */
	private Random worldRandom;
	/** Heap for all registered addresses */
	private ArrayList<StargateRecord> recordHeap = new ArrayList<StargateRecord>();
	/** Dictionary of all allocated character maps */
	private ArrayList<Long> characterMap = new ArrayList<Long>();

	/** Default constructor */
	public UniverseManager() {
		jsonAgent = new RecordIO();
	}

	/**
	 * Called by the system when a server is being started.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void loadUniverse(FMLServerStartingEvent event) {
		WorldServer overworld = event.getServer().worldServerForDimension(0);
		File cwd = overworld.getSaveHandler().getWorldDirectory();
		File dataDir = new File(cwd, "lanteacraft");
		if (!dataDir.exists())
			dataDir.mkdir();

		workFile = new File(dataDir, "addresses.json");
		if (workFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(workFile);
				recordHeap.clear();
				jsonAgent.readMap(fis, recordHeap);
				fis.close();
			} catch (IOException ioex) {
				LCLog.fatal("Problem reading Stargate address database.", ioex);
			}
		}

		buildIndex();
	}

	/**
	 * Called by the system when a server is being stopped.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void unloadUniverse(FMLServerStoppingEvent event) {
		try {
			FileOutputStream fos = new FileOutputStream(workFile);
			jsonAgent.writeMap(fos, recordHeap);
			recordHeap.clear();
			fos.close();
		} catch (IOException ioex) {
			LCLog.fatal("Problem saving Stargate address database.", ioex);
		}
	}

	/**
	 * Called by the system when a dimension is being loaded.
	 * 
	 * @param load
	 *            The load event.
	 */
	public void loadGalaxy(WorldEvent.Load load) {
	}

	/**
	 * Called by the system when a dimension is being unloaded.
	 * 
	 * @param unload
	 *            The unload event.
	 */
	public void unloadGalaxy(WorldEvent.Unload unload) {
	}

	/**
	 * Called by the system when the dimension is being auto-saved.
	 * 
	 * @param save
	 *            The save event.
	 */
	public void autosaveGalaxy(WorldEvent.Save save) {
	}

	/**
	 * Build the reference cache.
	 */
	public void buildIndex() {
		characterMap.clear();
		for (StargateRecord record : recordHeap)
			characterMap.add(record.address.getILongValue());
	}

	public boolean isAddressAllocated(StargateAddress address) {
		return characterMap.contains(address.getILongValue());
	}

	public StargateAddress putAddress(StargateAddress address, int dimension, ChunkPos chunk) {
		StargateRecord record = new StargateRecord();
		record.address = address;
		record.dimension = dimension;
		record.chunk = chunk;
		recordHeap.add(record);
		characterMap.add(address.getILongValue());
		return address;
	}

	public StargateAddress findAddress(int dimension, ChunkPos chunk) {
		for (StargateRecord record : recordHeap) {
			if (record.chunk.equals(chunk))
				return record.address;
		}
		return putAddress(getFreeAddress(ADDRESS_WIDTH), dimension, chunk);
	}

	public StargateAddress getFreeAddress(int width) {
		ArrayList<Character> address = new ArrayList<Character>();
		while (true) {
			while (address.size() < width) {
				char z = StargateCharsetHelper.singleton().index(worldRandom.nextInt(36));
				if (address.contains(z))
					continue;
				address.add(z);
			}
			StargateAddress addr = new StargateAddress(address.toArray(new Character[0]));
			if (!isAddressAllocated(addr))
				return addr;
		}
	}

	public StargateRecord findRecord(StargateAddress address) {
		if (!isAddressAllocated(address))
			return null;
		for (StargateRecord record : recordHeap) {
			if (record.address.equals(address))
				return record;
		}
		return null;
	}
}
