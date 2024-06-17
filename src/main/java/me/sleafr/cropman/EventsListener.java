package me.sleafr.cropman;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EventsListener implements Listener {

    public static boolean reals;
    private final CropMan plugin;
    private final boolean RealisticSeasons;
    GrowUtilities gu = new GrowUtilities();

    public EventsListener(CropMan plugin, boolean rs) {
        this.plugin = plugin;
        this.RealisticSeasons = rs;
        reals = rs;
    }

    @EventHandler
    public void onBlockGrow(BlockGrowEvent e){
        String worldName = e.getBlock().getWorld().getName();
        boolean shouldCancel = false;
        boolean shouldWither = false;

        if (!plugin.getConfigManager().getIgnoredWorlds().contains(worldName)){

            Material m = e.getBlock().getType();
            YamlConfiguration worldConfig = plugin.getConfigManager().getWorldConfig(worldName);

            // If block is disabled
            if (!gu.isDisabled(e.getBlock(), worldConfig)){

                // Check if pass the conditions
                if(RealisticSeasons){
                    if(!gu.checkRSConditions(worldConfig, e.getBlock())){
                        shouldCancel = true;
                        shouldWither = gu.rsWither(worldConfig, e.getBlock());
                    }
                }
            }
            // Check if pass growth chance
            if(!gu.checkGrowChance(worldConfig, m)){
                   shouldCancel = true;
                    if (gu.shouldWither(worldConfig, e.getBlock())){
                        shouldWither=true;
                    }
                }

            e.setCancelled(shouldCancel);
            if (shouldWither){
                gu.setWitheredBlock(e.getBlock(), worldConfig);
            }

        }

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){

        String worldName = e.getBlock().getWorld().getName();
        if (!plugin.getConfigManager().getIgnoredWorlds().contains(worldName)) {

            Material m = e.getBlock().getType();
            YamlConfiguration worldConfig = plugin.getConfigManager().getWorldConfig(worldName);

            if (worldConfig.isSet(e.getBlock().getType().toString())){
                if (gu.isDisabled(e.getBlock(), worldConfig) || !gu.shouldDropSeeds(worldConfig, e.getBlock())){
                    e.setDropItems(false);
                }
            }
        }
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent e){

        String worldName = e.getBlock().getWorld().getName();
        if (!plugin.getConfigManager().getIgnoredWorlds().contains(worldName)) {

            YamlConfiguration worldConfig = plugin.getConfigManager().getWorldConfig(worldName);

            if (worldConfig.isSet(e.getToBlock().getType().toString())){
                if (gu.isDisabled(e.getToBlock(), worldConfig) || !gu.shouldDropSeeds(worldConfig, e.getToBlock())){
                    e.setCancelled(true);
                    e.getToBlock().setType(Material.AIR, true);
                }
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e){

        String worldName = e.getBlock().getWorld().getName();
        if (!plugin.getConfigManager().getIgnoredWorlds().contains(worldName)) {

            Material m = e.getBlock().getType();
            YamlConfiguration worldConfig = plugin.getConfigManager().getWorldConfig(worldName);

            if (worldConfig.isSet(e.getBlock().getType().toString())){
                if (gu.isDisabled(e.getBlock(), worldConfig) || !gu.shouldDropSeeds(worldConfig, e.getBlock())){
                    for ( Block b : e.getBlocks()){
                        b.setType(Material.AIR);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e){

        String worldName = e.getBlock().getWorld().getName();
        if (!plugin.getConfigManager().getIgnoredWorlds().contains(worldName)) {

            Material m = e.getBlock().getType();
            YamlConfiguration worldConfig = plugin.getConfigManager().getWorldConfig(worldName);

            if (worldConfig.isSet(e.getBlock().getType().toString())){
                if (gu.isDisabled(e.getBlock(), worldConfig) || !gu.shouldDropSeeds(worldConfig, e.getBlock())){
                    for ( Block b : e.getBlocks()){
                        b.setType(Material.AIR);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockTrampling(EntityChangeBlockEvent e){

        String worldName = e.getBlock().getWorld().getName();
        if (!plugin.getConfigManager().getIgnoredWorlds().contains(worldName)) {

            YamlConfiguration worldConfig = plugin.getConfigManager().getWorldConfig(worldName);

            if (e.getBlock().getType() == Material.FARMLAND){
                Block up = e.getBlock().getRelative(0,1,0);

                if (!gu.shouldDropSeeds(worldConfig, up)){
                    up.setType(Material.AIR,true);
                }

            }

        }
    }
}
