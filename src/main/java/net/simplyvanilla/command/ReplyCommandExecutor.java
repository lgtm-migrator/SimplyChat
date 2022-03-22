package net.simplyvanilla.command;

import net.simplyvanilla.state.PlayerState;
import net.simplyvanilla.state.PlayerStateManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplyCommandExecutor implements CommandExecutor {

    private final String helpMessage;
    private final String noReceiverMessage;
    private final String receiverNotOnlineMessage;

    private final MessageCommandExecutor messageCommandExecutor;

    private final PlayerStateManager playerStateManager;

    public ReplyCommandExecutor(String helpMessage, String noReceiverMessage, String receiverNotOnlineMessage, MessageCommandExecutor messageCommandExecutor, PlayerStateManager playerStateManager) {
        this.helpMessage = helpMessage;
        this.noReceiverMessage = noReceiverMessage;
        this.receiverNotOnlineMessage = receiverNotOnlineMessage;
        this.messageCommandExecutor = messageCommandExecutor;
        this.playerStateManager = playerStateManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player sender)) {
            commandSender.sendMessage("This command is only for players.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(helpMessage.replaceAll("\\[label]", label));
            return true;
        }

        PlayerState playerState = playerStateManager.getPlayerState(sender.getUniqueId());

        if (playerState.getLastMessageSender().isEmpty()) {
            sender.sendMessage(noReceiverMessage);
            return true;
        }

        Player receiver = Bukkit.getPlayer(playerState.getLastMessageSender().get());

        if (receiver == null) {
            sender.sendMessage(receiverNotOnlineMessage.replaceAll("\\[receiver]", args[0]));
            return true;
        }

        String message = String.join(" ", args);
        messageCommandExecutor.message(sender, receiver, message);
        return true;
    }

}
