package com.gmail.val59000mc.threads;

import com.gmail.val59000mc.UhcCore;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.World;

public abstract class ChunkLoaderThread implements Runnable {

    private final World world;
    private final int restEveryNumOfChunks, restDuration;

    private final int maxChunk;
    private int x, z;
    private final int totalChunksToLoad;
    private int chunksLoaded;

    public ChunkLoaderThread(World world, int size, int restEveryNumOfChunks, int restDuration) {
        this.world = world;
        this.restEveryNumOfChunks = restEveryNumOfChunks;
        this.restDuration = restDuration;

        maxChunk = Math.round(size/16f) + 1;

        totalChunksToLoad = (2*maxChunk+1)*(2*maxChunk+1);

        x = -maxChunk;
        z = -maxChunk;
    }

    public abstract void onDoneLoadingWorld();

    @Override
    public void run() {
        int loaded = 0;
        while(x <= maxChunk && z <= maxChunk && loaded < restEveryNumOfChunks){
            PaperLib.getChunkAtAsync(world, x, z, true);
            loaded++;
            z++;
        }

        chunksLoaded += loaded;

        // Not yet done loading all chunks
        if(x < maxChunk){
            // Done loading chunk row
            if(z > maxChunk){
                z = -maxChunk;
                x++;
            }

            Bukkit.getLogger().info("[UhcCore] Loading map "+getLoadingState()+"% - "+chunksLoaded+"/"+totalChunksToLoad+" chunks loaded");
            Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), this, restDuration);
        }
        // Done loading all chunks
        else{
            onDoneLoadingWorld();
        }
    }

    public void printSettings(){
        Bukkit.getLogger().info("[UhcCore] Generating environment "+world.getEnvironment().toString());
        Bukkit.getLogger().info("[UhcCore] Loading a total "+Math.floor(totalChunksToLoad)+" chunks, up to chunk ( "+maxChunk+" , "+maxChunk+" )");
        Bukkit.getLogger().info("[UhcCore] Resting "+restDuration+" ticks every "+restEveryNumOfChunks+" chunks");
        Bukkit.getLogger().info("[UhcCore] Loading map "+getLoadingState()+"%");
    }

    private String getLoadingState(){
        double percentage = 100*(double)chunksLoaded/totalChunksToLoad;
        return world.getEnvironment()+" "+(Math.floor(10*percentage)/10);
    }

}