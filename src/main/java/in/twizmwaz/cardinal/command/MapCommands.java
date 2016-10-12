package in.twizmwaz.cardinal.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.chat.ChatConstant;
import in.twizmwaz.cardinal.chat.LocalizedChatMessage;
import in.twizmwaz.cardinal.repository.LoadedMap;
import in.twizmwaz.cardinal.repository.repositories.Repository;
import in.twizmwaz.cardinal.util.ChatUtil;
import in.twizmwaz.cardinal.util.Contributor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MapCommands {

    @Command(aliases = {"map", "mapinfo"}, flags = "lm:", desc = "Shows information about the currently playing map.", usage = "")
    public static void map(final CommandContext args, CommandSender sender) throws CommandException {
        LoadedMap mapInfo =
                args.hasFlag('m') ? GameHandler.getGameHandler().getRepositoryManager().getMap(Integer.parseInt(args.getFlag('m'))) :
                        args.argsLength() == 0 ? GameHandler.getGameHandler().getMatch().getLoadedMap() :
                                GameHandler.getGameHandler().getRepositoryManager().getMap(args.getJoinedStrings(0));

        if (mapInfo == null) {
            throw new CommandException(ChatConstant.ERROR_NO_MAP_MATCH.getMessage(ChatUtil.getLocale(sender)));
        }
        sender.sendMessage(ChatColor.RED + "" + ChatColor.STRIKETHROUGH + "----------" + (args.hasFlag('l') ? ChatColor.YELLOW + " #" + mapInfo.getId() + " " : "") + ChatColor.DARK_AQUA + " " + mapInfo.getName() + " " + ChatColor.GRAY + mapInfo.getVersion() + ChatColor.RED + " " + ChatColor.STRIKETHROUGH + "----------");
        sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ChatConstant.UI_MAP_OBJECTIVE.getMessage(ChatUtil.getLocale(sender)) + ": " + ChatColor.RESET + "" + ChatColor.GOLD + mapInfo.getObjective());
        if (mapInfo.getAuthors().size() > 1) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ChatConstant.UI_MAP_AUTHORS.getMessage(ChatUtil.getLocale(sender)) + ":");
            for (Contributor contributor : mapInfo.getAuthors()) {
                if (contributor.getContribution() != null) {
                    sender.sendMessage("  " + contributor.getDisplayName() + ChatColor.GRAY + " - " + ChatColor.ITALIC + contributor.getContribution());
                } else {
                    sender.sendMessage("  " + contributor.getDisplayName());
                }
            }
        } else {
            if (mapInfo.getAuthors().get(0).getContribution() != null) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ChatConstant.UI_MAP_AUTHOR.getMessage(ChatUtil.getLocale(sender)) + ": " + mapInfo.getAuthors().get(0).getDisplayName() + ChatColor.GRAY + " - " + ChatColor.ITALIC + mapInfo.getAuthors().get(0).getContribution());
            } else {
                sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ChatConstant.UI_MAP_AUTHOR.getMessage(ChatUtil.getLocale(sender)) + ": " + mapInfo.getAuthors().get(0).getDisplayName());
            }
        }
        if (mapInfo.getContributors().size() > 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ChatConstant.UI_MAP_CONTRIBUTORS.getMessage(ChatUtil.getLocale(sender)) + ":");
            for (Contributor contributor : mapInfo.getContributors()) {
                if (contributor.getContribution() != null) {
                    sender.sendMessage("  " + contributor.getDisplayName() + ChatColor.GRAY + " - " + ChatColor.ITALIC + contributor.getContribution());
                } else {
                    sender.sendMessage("  " + contributor.getDisplayName());
                }
            }
        }
        if (mapInfo.getRules().size() > 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ChatConstant.UI_MAP_RULES.getMessage(ChatUtil.getLocale(sender)) + ":");
            for (int i = 1; i <= mapInfo.getRules().size(); i++) {
                sender.sendMessage(ChatColor.WHITE + "" + i + ") " + ChatColor.GOLD + mapInfo.getRules().get(i - 1));
            }
        }
        sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ChatConstant.UI_MAP_MAX.getMessage(ChatUtil.getLocale(sender)) + ": " + ChatColor.RESET + "" + ChatColor.GOLD + mapInfo.getMaxPlayers());
        if (args.hasFlag('l')) {
            Repository repo = GameHandler.getGameHandler().getRepositoryManager().getRepo(mapInfo);
            sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Source: " + ChatColor.RESET + "" + ChatColor.GOLD + (repo == null ? "Unknown" : repo.getSource()));
            String mapPath = mapInfo.getFolder().getPath();
            if (repo != null)
                sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Folder: " + ChatColor.RESET + "" + ChatColor.GOLD + mapPath.substring(repo.getPath().length(), mapPath.length()));
        }
    }

    @Command(aliases = {"next", "nextmap", "nm", "mn", "mapnext"}, desc = "Shows next map.", usage = "")
    public static void next(final CommandContext cmd, CommandSender sender) {
        LoadedMap next = GameHandler.getGameHandler().getCycle().getMap();
        if (next.getAuthors().size() == 1) {
            sender.sendMessage(ChatColor.DARK_PURPLE + new LocalizedChatMessage(ChatConstant.GENERIC_MAP_NEXT, ChatColor.GOLD + next.getName() + ChatColor.DARK_PURPLE + " " + ChatConstant.MISC_BY.getMessage(ChatUtil.getLocale(sender)) + " " + ChatColor.RED + next.getAuthors().get(0).getName()).getMessage(ChatUtil.getLocale(sender)));
        } else if (next.getAuthors().size() > 1) {
            String result = ChatColor.DARK_PURPLE + new LocalizedChatMessage(ChatConstant.GENERIC_MAP_NEXT, ChatColor.GOLD + next.getName() + ChatColor.DARK_PURPLE + " " + ChatConstant.MISC_BY.getMessage(ChatUtil.getLocale(sender)) + " ").getMessage(ChatUtil.getLocale(sender));
            for (Contributor author : next.getAuthors()) {
                if (next.getAuthors().indexOf(author) < next.getAuthors().size() - 2) {
                    result = result + ChatColor.RED + author.getName() + ChatColor.DARK_PURPLE + ", ";
                } else if (next.getAuthors().indexOf(author) == next.getAuthors().size() - 2) {
                    result = result + ChatColor.RED + author.getName() + ChatColor.DARK_PURPLE + " " + ChatConstant.MISC_AND.getMessage(ChatUtil.getLocale(sender)) + " ";
                } else if (next.getAuthors().indexOf(author) == next.getAuthors().size() - 1) {
                    result = result + ChatColor.RED + author.getName();
                }
            }
            sender.sendMessage(result);
        }
    }
}
