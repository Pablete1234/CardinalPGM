package in.twizmwaz.cardinal.util;

import com.google.common.collect.Lists;
import com.sk89q.minecraft.util.commands.CommandException;
import in.twizmwaz.cardinal.GameHandler;
import in.twizmwaz.cardinal.chat.ChatConstant;
import in.twizmwaz.cardinal.chat.ChatMessage;
import in.twizmwaz.cardinal.chat.LocalizedChatMessage;
import in.twizmwaz.cardinal.chat.UnlocalizedChatMessage;
import in.twizmwaz.cardinal.module.ModuleCollection;
import in.twizmwaz.cardinal.module.modules.chatChannels.AdminChannel;
import in.twizmwaz.cardinal.module.modules.chatChannels.GlobalChannel;
import in.twizmwaz.cardinal.module.modules.chatChannels.TeamChannel;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatUtil {

    public static BaseComponent baseComponentFromArray(BaseComponent[] array) {
        BaseComponent result = new TextComponent("");
        for (BaseComponent component : array) {
            result.addExtra(component);
        }
        return result;
    }

    public static void sendWarningMessage(Player player, String msg) {
        if (msg != null) player.sendMessage(ChatColor.YELLOW + " \u26A0 " + ChatColor.RED + msg);
    }

    public static void sendWarningMessage(Player player, ChatMessage msg) {
        if (msg != null)
            player.sendMessage(ChatColor.YELLOW + " \u26A0 " + ChatColor.RED + msg.getMessage(player.getLocale()));
    }

    public static String getWarningMessage(String msg) {
        if (msg == null) return null;
        else return ChatColor.YELLOW + " \u26A0 " + ChatColor.RED + ChatColor.translateAlternateColorCodes('`', msg);
    }

    public static String getLocale(CommandSender sender) {
        return sender instanceof Player ? ((Player) sender).getLocale() : Locale.getDefault().toString();
    }

    public static GlobalChannel getGlobalChannel() {
        return GameHandler.getGameHandler().getMatch().getModules().getModule(GlobalChannel.class);
    }

    public static AdminChannel getAdminChannel() {
        return GameHandler.getGameHandler().getMatch().getModules().getModule(AdminChannel.class);
    }

    public static ModuleCollection<TeamChannel> getTeamChannels() {
        return GameHandler.getGameHandler().getMatch().getModules().getModules(TeamChannel.class);
    }

    public static ChatColor getTimerColor(double time) {
        if (time <= 5) {
            return ChatColor.DARK_RED;
        } else if (time <= 30) {
            return ChatColor.GOLD;
        } else if (time <= 60) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.GREEN;
        }
    }

    public static ChatMessage toChatMessage(List<String> names) {
        return toChatMessage(names, ChatColor.RED, ChatColor.DARK_PURPLE);
    }

    public static ChatMessage toChatMessage(List<String> names, ChatColor nameColor, ChatColor extraColor) {
        int size = names.size();
        if (size == 1) {
            return new UnlocalizedChatMessage(nameColor + names.get(0));
        } else if (size > 1) {
            String first = "";
            for (String name : names) {
                int index = names.indexOf(name);
                if (index < size - 2) {
                    first += nameColor + name + extraColor + ", ";
                } else if (index == size - 2) {
                    first += nameColor + name + extraColor;
                } else if (index == size - 1) {
                    return new LocalizedChatMessage(ChatConstant.MISC_AND, first, nameColor + name + extraColor);
                }
            }
        }
        return new UnlocalizedChatMessage("");
    }

    /**
     * Breaks a raw string up into a series of lines. Words are wrapped using
     * spaces as decimeters and the newline character is respected.
     *
     * @param rawString The raw string to break.
     * @param lineLength The length of a line of text.
     * @return An array of word-wrapped lines.
     */
    public static String[] wordWrap(String rawString, int lineLength) {
        // A null string is a single line
        if (rawString == null) {
            return new String[] {""};
        }

        // A string shorter than the lineWidth is a single line
        if (rawString.length() <= lineLength && !rawString.contains("\n")) {
            return new String[] {rawString};
        }

        char[] rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<>();
        int lineColorChars = 0;

        for (int i = 0; i < rawChars.length; i++) {
            char c = rawChars[i];

            // skip chat color modifiers
            if (c == ChatColor.COLOR_CHAR) {
                word.append(ChatColor.getByChar(rawChars[i + 1]));
                lineColorChars += 2;
                i++; // Eat the next character as we have already processed it
                continue;
            }

            if (c == ' ' || c == '\n') {
                if (line.length() == 0 && word.length() - lineColorChars > lineLength) { // special case: extremely long word begins a line
                    for (String partialWord : word.toString().split("(?<=\\G.{" + lineLength + "})")) {
                        lines.add(partialWord);
                    }
                } else if (line.length() > 0 && line.length() + 1 + word.length() - lineColorChars > lineLength) { // Line too long...break the line
                    for (String partialWord : word.toString().split("(?<=\\G.{" + lineLength + "})")) {
                        lines.add(line.toString());
                        line = new StringBuilder(partialWord);
                    }
                    lineColorChars = 0;
                } else {
                    if (line.length() > 0) {
                        line.append(' ');
                    }
                    line.append(word);
                }
                word = new StringBuilder();

                if (c == '\n') { // Newline forces the line to flush
                    lines.add(line.toString());
                    line = new StringBuilder();
                }
            } else {
                word.append(c);
            }
        }

        if(line.length() > 0) { // Only add the last line if there is anything to add
            lines.add(line.toString());
        }

        // Iterate over the wrapped lines, applying the last color from one line to the beginning of the next
        if (lines.get(0).length() == 0 || lines.get(0).charAt(0) != ChatColor.COLOR_CHAR) {
            lines.set(0, ChatColor.WHITE + lines.get(0));
        }
        for (int i = 1; i < lines.size(); i++) {
            final String pLine = lines.get(i-1);
            final String subLine = lines.get(i);

            char color = pLine.charAt(pLine.lastIndexOf(ChatColor.COLOR_CHAR) + 1);
            if (subLine.length() == 0 || subLine.charAt(0) != ChatColor.COLOR_CHAR) {
                lines.set(i, ChatColor.getByChar(color) + subLine);
            }
        }

        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Paginates a list of objects and displays them to the sender
     *
     * @param sender     Who to show the paginated result.
     * @param header     The header shown as title.
     * @param index      Page index, what page the sender wants to see.
     * @param streamSize The size of the stream, can't get it from the steam because that would consume it.
     * @param pageSize   The size of each page (usually 8).
     * @param stream     The stream of objects to paginate.
     * @param toString   A function to convert the objects to chat messages.
     */
    public static <T> void paginate(CommandSender sender, ChatConstant header, int index, int streamSize, int pageSize,
                                    Stream<T> stream, Function<T, ChatMessage> toMessage, Function<T, String> toString) throws CommandException {
        int pages = (int) Math.ceil((streamSize + (pageSize - 1)) / pageSize);
        List<String> page;
        try {
            int current = pageSize * (index - 1);
            page = new Indexer().index(toString(paginate(stream, pageSize, index), toMessage,
                    ChatUtil.getLocale(sender), toString), current).collect(Collectors.toList());
            if (page.size() == 0) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new CommandException(new LocalizedChatMessage(ChatConstant.ERROR_INVALID_PAGE_NUMBER, pages + "")
                    .getMessage(ChatUtil.getLocale(sender)));
        }
        sender.sendMessage(Strings.padMessage(new LocalizedChatMessage(header, Strings.page(index, pages)).getMessage(ChatUtil.getLocale(sender))));
        page.forEach(sender::sendMessage);
    }

    public static <T> Stream<String> toString(Stream<T> stream, Function<T, ChatMessage> toChatMessage,
                                              String locale, Function<T, String> toString) {
        if (toChatMessage != null) {
            return stream.map(toChatMessage).map(msg -> msg.getMessage(locale));
        } else {
            return stream.map(toString);
        }
    }

    public static <T> Stream<T> paginate(Stream<T> stream, int pageSize, int index) {
        return stream.skip(pageSize * (index - 1)).limit(pageSize);
    }

    public enum ChannelType {
        GLOBAL, ADMIN, TEAM
    }

    private static class Indexer {

        private int index;

        private Stream<String> index(Stream<String> stream, int index) {
            this.index = index;
            return stream.map(str -> {
                this.index++;
                return str.replace("${index}", this.index + "");
            });
        }

    }

}
