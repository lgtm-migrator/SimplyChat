package net.simplyvanilla.simplychat.command;

import net.simplyvanilla.simplychat.state.PlayerStateManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MessageCommandExecutor implements CommandExecutor {

    private final String receiverNotFoundMessage;

    private final String senderMessageFormat;
    private final String receiverMessageFormat;

    private final PlayerStateManager playerStateManager;

    public MessageCommandExecutor(String receiverNotFoundMessage, String senderMessageFormat, String receiverMessageFormat, PlayerStateManager playerStateManager) {
        this.receiverNotFoundMessage = receiverNotFoundMessage;
        this.senderMessageFormat = senderMessageFormat;
        this.receiverMessageFormat = receiverMessageFormat;
        this.playerStateManager = playerStateManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player sender)) {
            commandSender.sendMessage("This command is only for players.");
            return true;
        }

        if (args.length < 2) {
            return false;
        }

        Player receiver = Bukkit.getPlayer(args[0]);

        if (receiver == null) {
            sender.sendMessage(receiverNotFoundMessage.replaceAll("\\[receiver]", args[0]));
            return true;
        }

        StringBuilder message = new StringBuilder();

        for(int i = 1; i < args.length; i++) {
            message.append(args[i].replace("\\", "\\\\")).append(" ");
        }

        message(sender, receiver, message.toString());
        return true;
    }

    public void message(Player sender, Player receiver, String message) {
        sender.sendMessage(MessageFormat.expandInternalPlaceholders(sender.getName(), receiver.getName(), message, PlaceholderAPI.setPlaceholders(sender, senderMessageFormat)));
        receiver.sendMessage(MessageFormat.expandInternalPlaceholders(sender.getName(), receiver.getName(), message, PlaceholderAPI.setPlaceholders(receiver, receiverMessageFormat)));

        playerStateManager.getPlayerState(receiver.getUniqueId()).setLastMessageSender(sender.getUniqueId());
    }

}
