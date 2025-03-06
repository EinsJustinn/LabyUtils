package com.rappytv.labyutils.bukkit.commands;

import com.rappytv.labyutils.bukkit.LabyUtilsBukkit;
import net.labymod.serverapi.integration.voicechat.VoiceChatPlayer;
import net.labymod.serverapi.integration.voicechat.model.VoiceChatMute;
import net.labymod.serverapi.server.bukkit.LabyModPlayer;
import net.labymod.serverapi.server.bukkit.LabyModProtocolService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LabyVoice implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String prefix, String[] args) {
        boolean canMutePermanently = sender.hasPermission("labyutils.voice.mute.permanent");
        boolean canMuteTemporarily = sender.hasPermission("labyutils.voice.mute.temporary");
        boolean canUnmute = sender.hasPermission("labyutils.voice.unmute");
        if(args.length == 0) {
            sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cUsage: /labyvoice <permamute/tempmute/unmute>");
            return false;
        }
        if(args[0].equalsIgnoreCase("permamute")) {
            if(!canMutePermanently) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cYou are not allowed to use this command!");
                return false;
            }
            if(args.length < 2) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cPlease enter a player name!");
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player was not found!");
                return false;
            }
            LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(target.getUniqueId());
            if(labyPlayer == null) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player does not use LabyMod!");
                return false;
            }
            VoiceChatPlayer voicePlayer = labyPlayer.getIntegrationPlayer(VoiceChatPlayer.class);
            if(voicePlayer == null) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player does not use VoiceChat!");
                return false;
            }
            if(voicePlayer.isMuted()) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player is already muted!");
                return false;
            }

            String reason = null;
            if(args.length > 2) {
                reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            }

            voicePlayer.mute(VoiceChatMute.create(target.getUniqueId(), reason));
        } else if(args[0].equalsIgnoreCase("tempmute")) {
            if(!canMuteTemporarily) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cYou are not allowed to use this command!");
                return false;
            }
            if(args.length < 2) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cPlease enter a player name!");
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player was not found!");
                return false;
            }
            LabyModPlayer labyPlayer = LabyModProtocolService.get().getPlayer(target.getUniqueId());
            if(labyPlayer == null) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player does not use LabyMod!");
                return false;
            }
            VoiceChatPlayer voicePlayer = labyPlayer.getIntegrationPlayer(VoiceChatPlayer.class);
            if(voicePlayer == null) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player does not use VoiceChat!");
                return false;
            }
            if(voicePlayer.isMuted()) {
                sender.sendMessage(LabyUtilsBukkit.getPrefix() + "§cThis player is already muted!");
                return false;
            }

            String reason = null;
            if(args.length > 3) {
                reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            }

            voicePlayer.mute(VoiceChatMute.create(target.getUniqueId(), reason));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String prefix, String[] args) {
        List<String> tab = new ArrayList<>();
        boolean canMutePermanently = sender.hasPermission("labyutils.voice.mute.permanent");
        boolean canMuteTemporarily = sender.hasPermission("labyutils.voice.mute.temporary");
        boolean canUnmute = sender.hasPermission("labyutils.voice.unmute");
        if(args.length == 1) {
            if(canMutePermanently) {
                tab.add("permamute");
            }
            if(canMuteTemporarily) {
                tab.add("tempmute");
            }
            if(canUnmute) {
                tab.add("unmute");
            }
            return tab;
        } else if(args.length == 2) {
            if(canMutePermanently || canMuteTemporarily || canUnmute) {
                // Null returns an in-built player list
                return null;
            }
        } else if(args.length == 3) {
            if(canMuteTemporarily && args[0].equalsIgnoreCase("tempmute")) {
                tab.add("6h");
                tab.add("3d");
                tab.add("1w");
            }
        }
        return tab;
    }
}
