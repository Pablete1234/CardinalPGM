package in.twizmwaz.cardinal.module.modules.fancyTab;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import in.twizmwaz.cardinal.event.CycleCompleteEvent;
import in.twizmwaz.cardinal.event.PlayerChangeTeamEvent;
import in.twizmwaz.cardinal.event.PlayerNameUpdateEvent;
import in.twizmwaz.cardinal.event.TeamNameChangeEvent;
import in.twizmwaz.cardinal.module.TaskedModule;
import in.twizmwaz.cardinal.module.modules.team.TeamModule;
import in.twizmwaz.cardinal.util.Teams;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FancyTabModule implements TaskedModule {

    private final String defaultSkin = "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=";
    private final String defaultSignature = "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw=";

    private final Property defaultProperty = new Property("textures", defaultSkin, defaultSignature);

    private int columnsPerTeam = 0;

    private long last = 0;

    private HashMap<Player, List<Property>> playerSlots = new HashMap<>();

    public FancyTabModule() {
        this.columnsPerTeam = 4 / (Teams.getTeams().size() - 1);
        if (columnsPerTeam == 0) columnsPerTeam = 1;
    }

    @Override
    public void unload() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() > last) {
            last = System.currentTimeMillis() + 10000;
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                for (Player view : Bukkit.getOnlinePlayers()) {
                    sendTabListPacket(viewer, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, getProfile(getPos(viewer, view), null), null, getPlayerPing(view));
                }
            }
        }
    }

    @EventHandler
    public void onCycleComplete(CycleCompleteEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            resetTab(player);
        }
        updateAll();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateAll();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        playerSlots.remove(event.getPlayer());
        updateAll();
    }

    @EventHandler
    public void onTeamChange(PlayerChangeTeamEvent event){
        resetTab(event.getPlayer());
        updateAll();
    }

    @EventHandler
    public void onTeamChangeName(TeamNameChangeEvent event){
        updateAll();
    }

    @EventHandler
    public void onDisplayNameChange(PlayerNameUpdateEvent event){
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            sendTabListPacket(viewer, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, getProfile(getPos(viewer, event.getPlayer()), null), event.getPlayer().getPlayerListName(), getPlayerPing(event.getPlayer()));
        }
    }

    private void createFakeList(Player player){
        playerSlots.put(player, new ArrayList<Property>());
        for (int i = 0; i < 80; i++) {
            createTabListSlot(player, "", i, defaultProperty);
        }
    }

    private void resetTab(Player player){
        for (int i = 0; i < 80; i++) {
            updateTabListSlot(player, "", i, 0, defaultProperty, -1);
        }
    }

    private void updateAll () {
        for (Player player : Bukkit.getOnlinePlayers()){
            update(player);
        }
    }

    private void update (Player player) {
        if (!playerSlots.containsKey(player)) createFakeList(player);
        TeamModule prioritized = Teams.getTeamByPlayer(player).orNull();
        int col = prioritized != null && prioritized.isObserver() ? 0 : columnsPerTeam;

        int biggestTeamCol = 18 - obsRows();

        if (prioritized != null && !prioritized.isObserver()) renderTeam(player, prioritized, 0, biggestTeamCol);
        for (TeamModule team : Teams.getTeams()){
            if (!team.isObserver() && !team.equals(prioritized)) {
                renderTeam(player, team, col, biggestTeamCol);
                col = col + columnsPerTeam;
                if (col > 3) break;
            }
        }
        renderObs(player, 20 - obsRows());
    }

    private int obsRows () {
        int biggestTeamCol = biggestTeam() + (columnsPerTeam - 1)/ columnsPerTeam;

        int maxObsRows = 18 - biggestTeamCol;
        int obsRows = (Teams.getTeamById("observers").get().size() + 3 )/ 4;
        if (obsRows > maxObsRows) obsRows = maxObsRows;
        return obsRows;
    }

    private void renderTeam(Player player, TeamModule team, int col, int maxRows){
        updateTabListSlot(player, getTeamTitle(team), 0, col);
        int row = team.contains(player) ? 2 : 1;
        int colOffset = 0;
        if (team.contains(player)) updateTabListSlot(player, player.getPlayerListName(), 1, col, getPlayerSkin(player), getPlayerPing(player));
        for (Player render : (List<Player>)team) {
            if (render.equals(player)) continue;
            updateTabListSlot(player, render.getPlayerListName(), row, col + colOffset, getPlayerSkin(render), getPlayerPing(render));
            row++;
            if (row > maxRows){
                row = 1;
                colOffset++;
                if (colOffset > columnsPerTeam) return;
            }
        }
        updateTabListSlot(player, "", row, col);
    }

    private void renderObs(Player player, int row){
        TeamModule team = Teams.getTeamById("observers").get();
        int col = team.contains(player) ? 1 : 0;
        if (team.contains(player)) updateTabListSlot(player, player.getPlayerListName(), row, 0, getPlayerSkin(player), getPlayerPing(player));
        for (int i = 0; i < 4; i++) {
            updateTabListSlot(player, "                        ", row - 1, i);
        }
        for (Player render : (List<Player>)team) {
            if (render.equals(player)) continue;
            updateTabListSlot(player, render.getPlayerListName(), row, col, getPlayerSkin(render), getPlayerPing(render));
            col++;
            if (col > 3){
                col = 0;
                row++;
                if (row > 19) return;
            }
        }
        if (row > 19) return;
        for (int i = 0; i < 4; i++) {
            updateTabListSlot(player, "", row, col);
            col++;
            if (col > 3) return;
        }
    }
    private int biggestTeam () {
        int biggestTeam = 0;
        for (TeamModule team : Teams.getTeams()) {
            if (!team.isObserver()&& team.size() > biggestTeam) biggestTeam = team.size();
        }
        return biggestTeam;
    }

    private String getTeamTitle(TeamModule team) {
        return team.size() + "" + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + team.getMax() + " " + team.getColor() + ChatColor.BOLD + team.getName();
    }

    private void updateTabListSlot(Player player, String display, int row, int col) {
        updateTabListSlot(player, display, row, col, defaultProperty, -1);
    }

    private void updateTabListSlot(Player player, String display, int row, int col, Property texture, int ping) {
        int i = row + col * 20;
        if (i > 79) return;

        display = display.equals(player.getPlayerListName()) ? display.replace(player.getName(), ChatColor.BOLD + player.getName()) : display;

        GameProfile game = getProfile(i, texture);

        if (!playerSlots.get(player).get(i).equals(texture)){
            sendTabListPacket(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, game, display, ping);
            sendTabListPacket(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, game, display, ping);
            playerSlots.get(player).set(i, texture);
        }
        sendTabListPacket(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, game, display, ping);
    }

    private void createTabListSlot(Player player, String display, int i, Property texture){
        GameProfile game = getProfile(i, texture);
        sendTabListPacket(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, game, display, -1);
        sendTabListPacket(player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, game, display, -1);
        playerSlots.get(player).add(texture);
    }

    private GameProfile getProfile(int i, Property texture) {
        GameProfile game = new GameProfile(UUID.fromString("00000000-0000-0000-0000-0000000000" + (i < 10 ? "0" + i : i)), "TabView" + (i < 10 ? "0" + i : i));
        game.getProperties().put("textures", texture);
        return game;
    }

    private void sendTabListPacket(Player player, PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, GameProfile game, String displayName, int ping) {
        PacketPlayOutPlayerInfo listPacket = new PacketPlayOutPlayerInfo();

        try {
            Field a = listPacket.getClass().getDeclaredField("a");
            a.setAccessible(true);
            a.set(listPacket, action);

            Field b = listPacket.getClass().getDeclaredField("b");
            b.setAccessible(true);
            List<PacketPlayOutPlayerInfo.PlayerInfoData> dataList = Lists.newArrayList();

            dataList.add(new PacketPlayOutPlayerInfo.PlayerInfoData(game, (ping != -1 ? ping : 1000), WorldSettings.EnumGamemode.SURVIVAL, IChatBaseComponent.ChatSerializer.a("{text:\"" + displayName + "\"}")));

            b.set(listPacket, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(listPacket);
    }

    private Property getPlayerSkin (Player player) {
        return getPlayerSkin(((CraftPlayer) player).getProfile());
    }

    private Property getPlayerSkin (GameProfile profile){
        for(Property property : profile.getProperties().get("textures")) {
            return new Property("textures", property.getValue(), property.getSignature());
        }
        return defaultProperty;
    }

    private int getPlayerPing (Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    private int getPos(Player viewer, Player player) {
        TeamModule playerTeam = Teams.getTeamByPlayer(player).get();
        TeamModule viewerTeam = Teams.getTeamByPlayer(viewer).get();
        int row = 0;
        int col = 0;
        if (playerTeam.isObserver()) { //player is in obs
            if (player.equals(viewer)) return 20 - obsRows();
            List<Player> observers = new ArrayList<>();
            observers.addAll((List<Player>)playerTeam);
            observers.remove(viewer);
            int inObs = observers.indexOf(player) + (viewerTeam.isObserver() ? 1 : 0);
            row = (inObs / 4);
            col = inObs - (row * 4);
            return rowAndCol(20 - obsRows() + row, col);
        } else {
            if (player.equals(viewer)) return 1;
            if (playerTeam.equals(viewerTeam)){ //Player and viewer in the same team
                List<Player> team = new ArrayList<>();
                team.addAll((List<Player>)playerTeam);
                team.remove(viewer);
                int inTeam = team.indexOf(player) + 1;
                int biggestTeamCol = 18 - obsRows();
                col = inTeam / biggestTeamCol;
                if (col > columnsPerTeam) return 80;
                row = inTeam - (col * biggestTeamCol);
                return rowAndCol(row + 1, col);
            } else { //Player and viewer in different teams
                List<TeamModule> teams = Teams.getTeams();
                teams.remove(Teams.getTeamById("observers").get());
                if (!viewerTeam.isObserver())teams.remove(viewerTeam);

                List<Player> team = new ArrayList<>();
                team.addAll((List<Player>) playerTeam);
                int inTeam = team.indexOf(player);
                int biggestTeamCol = 18 - obsRows();
                col = inTeam / biggestTeamCol;
                if (col > columnsPerTeam) return 80;
                row = inTeam - (col * biggestTeamCol);
                col += (teams.indexOf(playerTeam) + (viewerTeam.isObserver() ? 0 : 1)) * columnsPerTeam;
                return rowAndCol(row + 1, col);
            }
        }
    }

    private int rowAndCol (int row, int col) {
        return  row + col * 20;
    }

    private void setPlayerPart(Player player){
        CraftPlayer display = new CraftPlayer((CraftServer)Bukkit.getServer(), (EntityPlayer)player);
    }

    public Player makeVirtualPlayer(GameProfile profile, boolean hat) throws Exception{
        CraftServer cserver = (CraftServer) Bukkit.getServer();
        List<World> worlds = cserver.getWorlds();
        CraftWorld w = (CraftWorld) worlds.get(0);
        Location location = new Location(w, 0, 0, 0);
        MinecraftServer mcserver = cserver.getServer();
        WorldServer worldServer = mcserver.getWorldServer(0);
        PlayerInteractManager pim = new PlayerInteractManager(worldServer);
        CraftPlayer player = new CraftPlayer(cserver, new EntityPlayer(mcserver, worldServer, profile, pim));

        return player;
    }
}
