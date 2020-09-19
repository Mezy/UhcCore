package com.gmail.val59000mc.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesntExistException;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayersManager;
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ProtocolUtils{

    private static ProtocolUtils protocolUtils;

    private ProtocolUtils(){
        protocolUtils = this;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(UhcCore.getPlugin(), PacketType.Play.Server.PLAYER_INFO) {

            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacket().getPlayerInfoAction().read(0) != EnumWrappers.PlayerInfoAction.ADD_PLAYER){
                    return;
                }

                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);
                PlayersManager pm = GameManager.getGameManager().getPlayersManager();

                for (PlayerInfoData playerInfoData : playerInfoDataList) {
                    if (
                            playerInfoData == null ||
                            playerInfoData.getProfile() == null ||
                            Bukkit.getPlayer(playerInfoData.getProfile().getUUID()) == null
                    ){ // Unknown player
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    WrappedGameProfile profile = playerInfoData.getProfile();
                    UhcPlayer uhcPlayer;

                    try {
                        uhcPlayer = pm.getUhcPlayer(profile.getUUID());
                    }catch (UhcPlayerDoesntExistException ex){ // UhcPlayer does not exist
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    // No display-name so don't change player data.
                    if (!uhcPlayer.hasNickName()){
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    profile = profile.withName(uhcPlayer.getName());

                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getPing(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().write(0, newPlayerInfoDataList);
            }

        });
    }

    public static void register(){
        if (protocolUtils != null){
            ProtocolLibrary.getProtocolManager().removePacketListeners(UhcCore.getPlugin());
            protocolUtils = null;
        }

        new ProtocolUtils();
    }

    /***
     * This method is used to change the player display name using ProtocolLib
     * @param uhcPlayer The player you want to change the display-name for.
     * @param nickName The wanted nick-name, set to null to reset. (Make sure its not over 16 characters long!)
     */
    public static void setPlayerNickName(UhcPlayer uhcPlayer, String nickName){
        uhcPlayer.setNickName(nickName);

        try {
            // Make the player disappear and appear to update their name.
            updatePlayer(uhcPlayer.getPlayer());
        }catch (UhcPlayerNotOnlineException ex){
            // Don't update offline players
        }
    }

    /***
     * This method can be used to change the tab header and footer.
     * @param player The player to change the header / footer for
     * @param header The new header
     * @param footer The new footer
     */
    public static void setPlayerHeaderFooter(Player player, String header, String footer){
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(header));
        packet.getChatComponents().write(1, WrappedChatComponent.fromText(footer));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }catch (InvocationTargetException ex){
            ex.printStackTrace();
        }
    }

    private static void updatePlayer(Player player){
        for (Player all : player.getWorld().getPlayers()){
            all.hidePlayer(player);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), () -> {
            for (Player all : player.getWorld().getPlayers()){
                all.showPlayer(player);
            }
        }, 1);
    }

}