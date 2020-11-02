package com.gmail.val59000mc.configuration;

import com.gmail.val59000mc.exceptions.ParseException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.lobby.pvp.zone.RectangleZone;
import com.gmail.val59000mc.lobby.pvp.zone.SphereZone;
import com.gmail.val59000mc.lobby.pvp.zone.Zone;
import com.gmail.val59000mc.utils.JsonItemStack;
import com.gmail.val59000mc.utils.JsonItemUtils;
import com.gmail.val59000mc.utils.equipment.Equipment;
import com.gmail.val59000mc.utils.equipment.EquipmentContainer;
import com.gmail.val59000mc.utils.equipment.EquipmentSlot;
import com.gmail.val59000mc.utils.equipment.slot.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class LobbyPvpConfiguration {

    private final GameManager gameManager;

    private boolean enabled;
    private List<Zone> zones;

    private EquipmentContainer equipmentContainer;

    private boolean useCustomRespawnLocation = false;
    private Location customRespawnLocation;

    public LobbyPvpConfiguration(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public boolean load(JsonObject json) {
        if (json == null) return false;

        try {
            tryParseConfiguration(json);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.enabled = false;
            return false;
        }
    }

    private void tryParseConfiguration(JsonObject json) {
        this.enabled = json.get("enabled").getAsBoolean();
        if (!this.enabled) return;

        tryParseZones(json.get("zones"));
        tryParseEquipment(json.get("equipment"));

        useCustomRespawnLocation = json.get("use_custom_respawn_location").getAsBoolean();
        if (useCustomRespawnLocation) {
            JsonObject customRespawnLocationObject = json.get("custom_respawn_location").getAsJsonObject();

            double x = customRespawnLocationObject.get("x").getAsDouble() + .5;
            double y = customRespawnLocationObject.get("y").getAsDouble();
            double z = customRespawnLocationObject.get("z").getAsDouble() + .5;

            float pitch = 0f;
            float yaw = 0f;

            try {
                pitch = customRespawnLocationObject.get("pitch").getAsFloat();
                yaw = customRespawnLocationObject.get("yaw").getAsFloat();
            }
            catch (Exception ignore) {
            }

            customRespawnLocation = new Location(null, x, y, z, yaw, pitch);
        }
    }

    private void tryParseZones(JsonElement element) {
        zones = new ArrayList<>();

        JsonArray zones = element.getAsJsonArray();
        for (JsonElement zoneElement : zones) {
            JsonObject zoneObject = zoneElement.getAsJsonObject();

            String type = zoneObject.get("type").getAsString();
            JsonObject parameters = zoneObject.get("parameters").getAsJsonObject();

            switch (type) {
                case "rectangle":
                    int x1 = parameters.get("x1").getAsInt();
                    int y1 = parameters.get("y1").getAsInt();
                    int z1 = parameters.get("z1").getAsInt();

                    int x2 = parameters.get("x2").getAsInt();
                    int y2 = parameters.get("y2").getAsInt();
                    int z2 = parameters.get("z2").getAsInt();

                    this.zones.add(new RectangleZone(
                            x1, y1, z1,
                            x2, y2, z2
                    ));
                    break;
                case "sphere":
                    int x = parameters.get("x").getAsInt();
                    int y = parameters.get("y").getAsInt();
                    int z = parameters.get("z").getAsInt();

                    int r = parameters.get("r").getAsInt();

                    this.zones.add(new SphereZone(
                            x, y, z,
                            r
                    ));
                    break;
            }
        }
    }

    private void tryParseEquipment(JsonElement element) {
        List<Equipment> equipments = new ArrayList<>();

        JsonArray equipment = element.getAsJsonArray();
        for (JsonElement equipmentElement : equipment) {
            JsonObject equipmentObject = equipmentElement.getAsJsonObject();

            String slot = equipmentObject.get("slot").getAsString();
            EquipmentSlot equipmentSlot;

            switch (slot) {
                case "helmet":
                    equipmentSlot = new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.HELMET);
                    break;
                case "chestplate":
                    equipmentSlot = new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.CHESTPLATE);
                    break;
                case "leggings":
                    equipmentSlot = new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.LEGGINGS);
                    break;
                case "boots":
                    equipmentSlot = new ArmorEquipmentSlot(ArmorEquipmentSlot.SlotType.BOOTS);
                    break;
                case "offhand":
                    equipmentSlot = new OffhandEquipmentSlot();
                    break;
                default:
                    int i = slot.indexOf(".");
                    if (i != -1) {
                        try {
                            i = Integer.parseInt(slot.substring(i));
                        } catch (NumberFormatException ignore) {
                            i = -1;
                        }
                    }

                    if (slot.startsWith("hotbar")) {
                        equipmentSlot = new HotbarEquipmentSlot(i);
                    }
                    else if (slot.startsWith("storage")) {
                        equipmentSlot = new StorageEquipmentSlot(i);
                    }
                    else {
                        equipmentSlot = new InventoryEquipmentSlot(i);
                    }
                    break;
            }

            JsonObject itemObject = equipmentObject.get("item").getAsJsonObject();
            try {
                JsonItemStack jsonItem = JsonItemUtils.getItemFromJson(itemObject);
                equipments.add(new Equipment(
                        equipmentSlot, jsonItem
                ));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        equipmentContainer = new EquipmentContainer(equipments);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public EquipmentContainer getEquipmentContainer() {
        return equipmentContainer;
    }

    public boolean isUseCustomRespawnLocation() {
        return useCustomRespawnLocation;
    }

    public Location getCustomRespawnLocation() {
        return customRespawnLocation;
    }

}
