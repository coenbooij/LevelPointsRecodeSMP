package me.zoon20x.levelpoints.events;

import me.zoon20x.levelpoints.LevelPoints;
import me.zoon20x.levelpoints.containers.Player.PlayerData;
import me.zoon20x.levelpoints.containers.Settings.Blocks.BlockData;
import me.zoon20x.levelpoints.containers.Settings.Blocks.BlockRequired;
import me.zoon20x.levelpoints.containers.Settings.Blocks.BlockUtils;
import me.zoon20x.levelpoints.events.CustomEvents.EarnTask;
import me.zoon20x.levelpoints.events.CustomEvents.EventUtils;
import me.zoon20x.levelpoints.utils.DebugSeverity;
import me.zoon20x.levelpoints.utils.Formatter;
import me.zoon20x.levelpoints.utils.MessageUtils;
import me.zoon20x.levelpoints.utils.Permissions.PermissionUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class ExpEarningEvents implements Listener {

    @EventHandler
    public void onKillMob(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }
        EntityType entityType = event.getEntity().getType();

        Player player = event.getEntity().getKiller();
        PlayerData data = LevelPoints.getPlayerStorage().getLoadedData(player.getUniqueId());
        EventUtils.triggerEarnExpEvent(data, event, 1.0, player, EarnTask.Mobs);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        PlayerData data = LevelPoints.getPlayerStorage().getLoadedData(player.getUniqueId());
        Block block = event.getBlock();
        if (!LevelPoints.getLevelSettings().canPlace(block.getType(), block.getData(), data)) {
            BlockData blockData = BlockUtils.getBlockData(block.getType(), block.getData());
            if (LevelPoints.getLangSettings().isRequiredPlaceEnabled()) {
                Formatter formatter = new Formatter(player.getName(), data.getLevel(), data.getExp(), blockData.getRequiredEXP(BlockRequired.PLACE, data), data.getPrestige(), blockData.getPlaceRequired(), data.getProgress());
                String message = MessageUtils.getColor(LevelPoints.getLangSettings().getRequiredPlace());
                player.sendMessage(MessageUtils.format(message, formatter));
            }
            event.setCancelled(true);
            return;
        }

        EventUtils.triggerEarnExpEvent(data, event, 1.0, player, EarnTask.Blocks);
    }


    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PlayerData data = LevelPoints.getPlayerStorage().getLoadedData(player.getUniqueId());
        Block block = event.getBlock();


        EventUtils.triggerEarnExpEvent(data, event, 1.0, player, EarnTask.Blocks);
    }

    @EventHandler
    public void  onAdvancementmade(PlayerAdvancementDoneEvent e) {
        Player player = e.getPlayer();
        PlayerData data = LevelPoints.getPlayerStorage().getLoadedData(player.getUniqueId());
        //player.sendMessage("Advancement made " + e.getAdvancement().getKey().getKey());
        String advancement = e.getAdvancement().getKey().getKey();
        if(advancement.contains("story") || advancement.contains("adventure") || advancement.contains("husbandry") || advancement.contains("nether") || advancement.contains("end")) {
            //player.sendMessage("Advancement made " + e.getAdvancement().getKey().getKey());
        } else {
            return;
        }
        EventUtils.triggerEarnExpEvent(data, e, 10.0, player, EarnTask.Advancements);
    }
    @EventHandler
    public void onPlayerdeath(PlayerDeathEvent e) {
        Player killed = e.getEntity().getPlayer();
        Player killer = e.getEntity().getKiller();
        PlayerData datakilled = LevelPoints.getPlayerStorage().getLoadedData(killed.getUniqueId());

        if (killer instanceof Player) {
            PlayerData datakiller = LevelPoints.getPlayerStorage().getLoadedData(killer.getUniqueId());
            EventUtils.triggerEarnExpEvent(datakiller, e, 10.0, killer, EarnTask.Pvp);
        }

        EventUtils.triggerEarnExpEvent(datakilled, e, -10.0, killed, EarnTask.Pvp);

    }
}
