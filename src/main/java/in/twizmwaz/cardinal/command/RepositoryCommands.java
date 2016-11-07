package in.twizmwaz.cardinal.command;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import in.twizmwaz.cardinal.chat.ChatConstant;
import in.twizmwaz.cardinal.repository.LoadedMap;
import in.twizmwaz.cardinal.repository.RepositoryManager;
import in.twizmwaz.cardinal.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;

public class RepositoryCommands {

    @Command(aliases = {"rotation", "rot", "rota", "maprot", "maprotation"}, desc = "Shows the current rotation.", usage = "[page]")
    public static void rotation(final CommandContext cmd, CommandSender sender) throws CommandException {
        ChatUtil.paginate(sender, ChatConstant.UI_ROTATION_CURRENT, cmd.getInteger(0, 1), RepositoryManager.get().getRotation().size(),
                8, RepositoryManager.get().getRotation().stream(), LoadedMap::toIndexedMessage, null);
    }

    @Command(aliases = {"maps", "maplist", "ml"}, flags = "l", desc = "Shows all currently loaded maps.", usage = "[page]")
    public static void maps(final CommandContext cmd, CommandSender sender) throws CommandException {
        ChatUtil.paginate(sender, ChatConstant.UI_MAPLOADED, cmd.getInteger(0, 1), RepositoryManager.get().getMapSize(),
                8, RepositoryManager.get().getLoadedStream().sorted(Comparator.comparing(LoadedMap::getName)),
                cmd.hasFlag('l') ? LoadedMap::toIndexedLongMessage : LoadedMap::toIndexedMessage, null);
    }

    @Command(aliases = {"repositories", "repos", "repo", "maprepo"}, desc = "Shows all currently loaded repos.", usage = "[page]")
    public static void repos(final CommandContext cmd, CommandSender sender) throws CommandException {
        ChatUtil.paginate(sender, ChatConstant.UI_REPOLOADED, cmd.getInteger(0, 1), RepositoryManager.get().getRepos().size(),
                8, RepositoryManager.get().getRepos().stream(), null, repo ->
                        "${index}. " + ChatColor.YELLOW + "#" + repo.getId() + " " + ChatColor.GOLD + repo.getSource());
    }

}
