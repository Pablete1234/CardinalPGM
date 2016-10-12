package in.twizmwaz.cardinal.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.chat.ChatConstant;
import in.twizmwaz.cardinal.chat.LocalizedChatMessage;
import in.twizmwaz.cardinal.match.MatchState;
import in.twizmwaz.cardinal.module.modules.cycleTimer.CycleTimerModule;
import in.twizmwaz.cardinal.module.modules.timeLimit.TimeLimit;
import in.twizmwaz.cardinal.repository.LoadedMap;
import in.twizmwaz.cardinal.repository.RepositoryManager;
import in.twizmwaz.cardinal.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CycleCommand {

    @Command(aliases = {"cycle"}, desc = "Cycles the world and loads a new world.", usage = "[time] [map]", flags = "fnm:")
    @CommandPermissions("cardinal.match.cycle")
    public static void cycle(final CommandContext cmd, CommandSender sender) throws CommandException {
        if (GameHandler.getGameHandler().getMatch().getState().equals(MatchState.STARTING)) {
            throw new CommandException(ChatConstant.ERROR_CYCLE_DURING_MATCH.getMessage(ChatUtil.getLocale(sender)));
        }
        processCycle(cmd, sender);
        LoadedMap map = cmd.hasFlag('m') ? GameHandler.getGameHandler().getRepositoryManager().getMap(Integer.parseInt(cmd.getFlag('m'))) :
                cmd.argsLength() > 1 ? getMap(cmd.getJoinedStrings(1).replace(" -f", "").replace("-f ", "")) :
                        GameHandler.getGameHandler().getCycle().getMap();
        if (map == null)
            throw new CommandException(new LocalizedChatMessage(ChatConstant.ERROR_NO_MAP_MATCH).getMessage(ChatUtil.getLocale(sender)));
        else setCycleMap(map);
        CycleTimerModule timer = GameHandler.getGameHandler().getMatch().getModules().getModule(CycleTimerModule.class);
        timer.setOriginalState(GameHandler.getGameHandler().getMatch().getState());
        timer.setCancelled(true);
        timer.cycleTimer(cmd.argsLength() > 0 ? cmd.getInteger(0) : 30);
    }

    @Command(aliases = {"setnext", "sn"}, desc = "Sets the next map.", usage = "[map]", flags = "m:")
    @CommandPermissions("cardinal.match.setnext")
    public static void setNext(final CommandContext cmd, CommandSender sender) throws CommandException {
        LoadedMap nextMap =
                cmd.hasFlag('m') ? GameHandler.getGameHandler().getRepositoryManager().getMap(Integer.parseInt(cmd.getFlag('m'))) :
                        cmd.argsLength() > 0 ? GameHandler.getGameHandler().getRepositoryManager().getMap(cmd.getJoinedStrings(0)) : null;
        if (nextMap == null) {
            throw new CommandException(ChatConstant.ERROR_NO_MAP_MATCH.getMessage(ChatUtil.getLocale(sender)));
        }
        setCycleMap(nextMap);
        sender.sendMessage(ChatColor.DARK_PURPLE + new LocalizedChatMessage(ChatConstant.GENERIC_MAP_SET, ChatColor.GOLD + nextMap.getName() + ChatColor.DARK_PURPLE).getMessage(ChatUtil.getLocale(sender)));
    }

    @Command(aliases = {"recycle", "rc"}, desc = "Cycles to the current map.", usage = "[time]", flags = "fn")
    @CommandPermissions("cardinal.match.cycle")
    public static void recycle(final CommandContext cmd, CommandSender sender) throws CommandException {
        if (GameHandler.getGameHandler().getMatch().getState().equals(MatchState.STARTING)) {
            throw new CommandException(new LocalizedChatMessage(ChatConstant.ERROR_CYCLE_DURING_MATCH).getMessage(ChatUtil.getLocale(sender)));
        }
        processCycle(cmd, sender);
        setCycleMap(GameHandler.getGameHandler().getMatch().getLoadedMap());
        CycleTimerModule timer = GameHandler.getGameHandler().getMatch().getModules().getModule(CycleTimerModule.class);
        timer.setOriginalState(GameHandler.getGameHandler().getMatch().getState());
        timer.setCancelled(true);
        timer.cycleTimer(cmd.argsLength() > 0 ? cmd.getInteger(0) : 30);
    }

    private static LoadedMap getMap(String input) {
        final String search = input.toLowerCase().replaceAll(" ", "");
        return RepositoryManager.get().getLoadedStream().filter(
                map -> map.getName().replaceAll(" ", "").equalsIgnoreCase(search)).findFirst().orElseGet(
                () -> RepositoryManager.get().getLoadedStream().filter(
                map -> map.getName().toLowerCase().replaceAll(" ", "").startsWith(search)).findFirst().orElse(null));
    }

    private static void setCycleMap(LoadedMap map) {
        GameHandler.getGameHandler().getCycle().setMap(map);
    }

    private static void processCycle(CommandContext cmd, CommandSender sender) throws CommandException {
        if (GameHandler.getGameHandler().getMatch().isRunning()) {
            if (!cmd.hasFlag('f')) {
                throw new CommandException(ChatConstant.ERROR_CYCLE_DURING_MATCH.getMessage(ChatUtil.getLocale(sender)));
            }
            if (cmd.hasFlag('n')) {
                GameHandler.getGameHandler().getMatch().end();
            } else {
                GameHandler.getGameHandler().getMatch().end(TimeLimit.getMatchWinner());
            }
        }
    }
}
