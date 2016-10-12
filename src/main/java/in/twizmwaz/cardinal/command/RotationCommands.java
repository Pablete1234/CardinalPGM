package in.twizmwaz.cardinal.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import in.twizmwaz.cardinal.chat.ChatConstant;
import in.twizmwaz.cardinal.chat.LocalizedChatMessage;
import in.twizmwaz.cardinal.repository.LoadedMap;
import in.twizmwaz.cardinal.repository.RepositoryManager;
import in.twizmwaz.cardinal.util.ChatUtil;
import in.twizmwaz.cardinal.util.Contributor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RotationCommands {

    @Command(aliases = {"rotation", "rot", "rota", "maprot", "maprotation"}, desc = "Shows the current rotation.", usage = "[page]")
    public static void rotation(final CommandContext cmd, CommandSender sender) throws CommandException {
        int index = cmd.argsLength() == 0 ? 1 : cmd.getInteger(0);
        int pages = (int) Math.ceil((RepositoryManager.get().getRotation().size() + 7) / 8);
        List<String> page;
        try {
            page = ChatUtil.paginate(RepositoryManager.get().getRotation().stream(), null, 8, index, map -> {
                String result = "";
                if (map.getAuthors().size() == 1) {
                    result = ChatColor.GOLD + map.getName() + ChatColor.DARK_PURPLE + " " + new LocalizedChatMessage(ChatConstant.MISC_BY).getMessage(ChatUtil.getLocale(sender)) + " " + ChatColor.RED + map.getAuthors().get(0).getName();
                } else if (map.getAuthors().size() > 1) {
                    result = ChatColor.GOLD + map.getName() + ChatColor.DARK_PURPLE + " " + new LocalizedChatMessage(ChatConstant.MISC_BY).getMessage(ChatUtil.getLocale(sender)) + " ";
                    for (Contributor author : map.getAuthors()) {
                        if (map.getAuthors().indexOf(author) < map.getAuthors().size() - 2) {
                            result += ChatColor.RED + author.getName() + ChatColor.DARK_PURPLE + ", ";
                        } else if (map.getAuthors().indexOf(author) == map.getAuthors().size() - 2) {
                            result += ChatColor.RED + author.getName() + ChatColor.DARK_PURPLE + " " + new LocalizedChatMessage(ChatConstant.MISC_AND).getMessage(ChatUtil.getLocale(sender)) + " ";
                        } else if (map.getAuthors().indexOf(author) == map.getAuthors().size() - 1) {
                            result += ChatColor.RED + author.getName();
                        }
                    }
                }
                if (cmd.hasFlag('l')) {
                    result = ChatColor.YELLOW + "#" + map.getId() + " " + result;
                }
                return ChatColor.WHITE + "${index}. " + result;
            }).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CommandException("Invalid page number specified! Maximum page number is " + pages + ".");
        }
        if (page.size() == 0) throw new CommandException("Invalid page number specified! Maximum page number is " + pages + ".");
        sender.sendMessage(ChatColor.RED + "------------- " + ChatColor.WHITE + new LocalizedChatMessage(ChatConstant.UI_ROTATION_CURRENT).getMessage(ChatUtil.getLocale(sender)) + ChatColor.DARK_AQUA + " (" + ChatColor.AQUA + index + ChatColor.DARK_AQUA + " of " + ChatColor.AQUA + pages + ChatColor.DARK_AQUA + ") " + ChatColor.RED + "-------------");
        page.forEach(sender::sendMessage);
    }

    @Command(aliases = {"maps", "maplist", "ml"}, flags = "l", desc = "Shows all currently loaded maps.", usage = "[page]")
    public static void maps(final CommandContext cmd, CommandSender sender) throws CommandException {
        int index = cmd.argsLength() == 0 ? 1 : cmd.getInteger(0);
        int pages = (int) Math.ceil((RepositoryManager.get().getMapSize() + 7) / 8);
        List<String> page;
        try {
            page = ChatUtil.paginate(RepositoryManager.get().getLoadedStream(), Comparator.comparing(LoadedMap::getName), 8, index, map -> {
                String result = "";
                if (map.getAuthors().size() == 1) {
                    result = ChatColor.GOLD + map.getName() + ChatColor.DARK_PURPLE + " " + new LocalizedChatMessage(ChatConstant.MISC_BY).getMessage(ChatUtil.getLocale(sender)) + " " + ChatColor.RED + map.getAuthors().get(0).getName();
                } else if (map.getAuthors().size() > 1) {
                    result = ChatColor.GOLD + map.getName() + ChatColor.DARK_PURPLE + " " + new LocalizedChatMessage(ChatConstant.MISC_BY).getMessage(ChatUtil.getLocale(sender)) + " ";
                    for (Contributor author : map.getAuthors()) {
                        if (map.getAuthors().indexOf(author) < map.getAuthors().size() - 2) {
                            result += ChatColor.RED + author.getName() + ChatColor.DARK_PURPLE + ", ";
                        } else if (map.getAuthors().indexOf(author) == map.getAuthors().size() - 2) {
                            result += ChatColor.RED + author.getName() + ChatColor.DARK_PURPLE + " " + new LocalizedChatMessage(ChatConstant.MISC_AND).getMessage(ChatUtil.getLocale(sender)) + " ";
                        } else if (map.getAuthors().indexOf(author) == map.getAuthors().size() - 1) {
                            result += ChatColor.RED + author.getName();
                        }
                    }
                }
                if (cmd.hasFlag('l')) {
                    result = ChatColor.YELLOW + "#" + map.getId() + " " + result;
                }
                return ChatColor.WHITE + "${index}. " + result;
            }).collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new CommandException("Invalid page number specified! Maximum page number is " + pages + ".");
        }
        if (page.size() == 0) throw new CommandException("Invalid page number specified! Maximum page number is " + pages + ".");
        sender.sendMessage(ChatColor.RED + "--------------- " + ChatColor.WHITE + new LocalizedChatMessage(ChatConstant.UI_MAPLOADED).getMessage(ChatUtil.getLocale(sender)) + ChatColor.DARK_AQUA + " (" + ChatColor.AQUA + index + ChatColor.DARK_AQUA + " of " + ChatColor.AQUA + pages + ChatColor.DARK_AQUA + ") " + ChatColor.RED + "---------------");
        page.forEach(sender::sendMessage);
    }

}
