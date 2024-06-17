package me.sleafr.cropman;

import me.casperge.realisticseasons.api.SeasonsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GrowUtilities {

    String rs = ".conditions.RealisticSeasons";

    public boolean isDisabled(Block b, YamlConfiguration c){
        if(c.isSet(b.getType()+".disabled")){
            return c.getBoolean(b.getType() + ".disabled");
        }
        return false;
    }

    public boolean checkGrowChance(YamlConfiguration worldConfig, Material b){
        double r = Math.random();
        double growChance = 1.0;

        if (worldConfig.isSet(b.toString())){
            if (worldConfig.isSet(b+".growChance")){
                growChance = worldConfig.getDouble(b+".growChance");
            }
        }

        return growChance>r;
    }

    public boolean shouldWither(YamlConfiguration worldConfig, Block b){
        double witherChance = 0;
        double storm = 1;
        double f;
        double r = Math.random();

        Ageable age = (Ageable)b.getBlockData();
        if (worldConfig.isSet(b.getType()+".withering.witherChance")){
            witherChance = (worldConfig.getDouble(b.getType()+".withering.witherChance"));
        }
        if (worldConfig.isSet(b.getType()+".withering.witherStormMultiplier")){
            storm = (worldConfig.getDouble(b.getType()+".withering.witherStormMultiplier"));
        }
        f = witherChance - (( witherChance / (age.getMaximumAge() ))*(age.getAge()-1) );

        if(b.getWorld().isThundering()){
            f = f*storm;
        }

        return r < f;
    }

    public boolean shouldDropSeeds(YamlConfiguration c, Block b){

        if (c.isSet(b.getType()+".dropSeedMinAge") && c.isInt(b.getType()+".dropSeedMinAge")){
            int min = c.getInt(b.getType()+".dropSeedMinAge");
            int age;
            Ageable a = (Ageable) b.getBlockData();
            age = a.getAge();
            return age>=min;
        }

        return true;
    }

    public void setWitheredBlock(Block b, YamlConfiguration c){
        Block relDown = b.getRelative(0,-1,0);
        if (c.isSet(b.getType()+".withering.witherBlock")){

            String wb = c.getString(b.getType()+".withering.witherBlock");

            if (wb!=null && Material.matchMaterial(wb)!=null){
                b.setType(Material.valueOf(wb.toUpperCase()));
            } else {
                randomByFaceDown(relDown, b);
            }

        }
    }

    private void randomByFaceDown(Block rel, Block b){
        if (rel.getType()== (Material.FARMLAND)){
            int i = new Random().nextInt(3);
            switch (i) {
                case 1:
                    b.setType(Material.SHORT_GRASS);
                    break;
                case 2:
                    b.setType(Material.FERN);
                    break;
                default:
                    b.setType(Material.DEAD_BUSH);
            }
        } else {
            b.setType(Material.AIR);}
    }

    public boolean checkRSConditions(YamlConfiguration c, Block b){
        boolean s = true;
        boolean e = true;
        boolean a = true;

        if (c.isSet(b.getType()+rs)){

            if (c.isSet(b.getType()+rs+".seasons")){
                String activeSeason = SeasonsAPI.getInstance().getSeason(b.getWorld()).toString().toUpperCase();

                List<String> seasonList = new ArrayList<>();

                 for (String sc : c.getStringList(b.getType()+rs+".seasons")){
                     seasonList.add(sc.toUpperCase());
                    }


                s=(seasonList.contains(activeSeason));


            }

            if (c.isSet(b.getType()+rs+".events")){
                List<String> events = c.getStringList((b.getType()+rs+".events"));
                List<String> active = SeasonsAPI.getInstance().getActiveEvents(b.getWorld());

                for (String ae : active){

                    // is set
                    if (c.isSet(b.getType()+rs+".diesIfEventListed") && c.getBoolean(b.getType()+rs+".diesIfEventListed")){
                        // kill
                        if (events.contains(ae)){
                            e=false;
                        }
                        // is not set but event isn't listed
                    } else if (!events.contains(ae)){
                        // kills
                        e=false;
                    }
                }


            }

            if (c.isSet(b.getType()+rs+".airTemperature")){
                int mintemp, maxtemp;
                int airtemp = SeasonsAPI.getInstance().getAirTemperature(b.getLocation());

                if (c.isSet(b.getType()+rs+".airTemperature.min")){
                    mintemp = c.getInt(b.getType()+rs+".airTemperature.min");
                } else { mintemp = -99999;}
                if (c.isSet(b.getType()+rs+".airTemperature.max")){
                    maxtemp = c.getInt(b.getType()+rs+".airTemperature.max");
                } else { maxtemp = 99999;}

                a = (airtemp>=mintemp) && (airtemp<=maxtemp);

            }
            return s && e && a;
        }

        return true;

    }

    public boolean rsWither(YamlConfiguration c, Block b){
        boolean withers = false;

        if (c.isSet(b.getType()+rs+".diesOffseason")){
            withers = c.getBoolean(b.getType()+rs+".diesOffseason");
        }
        if (c.isSet(b.getType()+rs+".diesIfEventListed") &&
                c.getBoolean(b.getType() + rs+".diesIfEventListed")){

            List<String> ListedEvents = c.getStringList(b.getType()+rs+".events");
            List<String> activeEvents = SeasonsAPI.getInstance().getActiveEvents(b.getWorld());
            for (String le: ListedEvents){
                if (activeEvents.contains(le)) {
                    withers = true;
                    break;
                }
            }
        }

        return withers;
    }

}
