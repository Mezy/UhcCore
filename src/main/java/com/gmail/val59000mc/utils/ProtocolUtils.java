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
import com.gmail.val59000mc.players.UhcPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                    try {
                        profile = profile.withName(getPlayerName(profile.getUUID()));
                    }catch (UhcPlayerDoesntExistException ex){ // UhcPlayer does not exist
                        newPlayerInfoDataList.add(playerInfoData);
                        continue;
                    }

                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getPing(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().write(0, newPlayerInfoDataList);
            }

        });
    }

    private String getPlayerName(UUID uuid) throws UhcPlayerDoesntExistException{
        return GameManager.getGameManager().getPlayersManager().getUhcPlayer(uuid).getName();
    }

    public static void register(){
        if (protocolUtils != null){
            ProtocolLibrary.getProtocolManager().removePacketListeners(UhcCore.getPlugin());
            protocolUtils = null;
        }

        new ProtocolUtils();
    }

    public static void changePlayerName(UhcPlayer uhcPlayer, String newName){
        uhcPlayer.setName(newName);

        Player player;

        try {
            player = uhcPlayer.getPlayer();
        }catch (UhcPlayerNotOnlineException ex){
            // Don't update offline players
            return;
        }

        for (Player all : player.getWorld().getPlayers()){
            all.hidePlayer(player);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(UhcCore.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (Player all : player.getWorld().getPlayers()){
                    all.showPlayer(player);
                }
            }
        }, 1);
    }

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

}