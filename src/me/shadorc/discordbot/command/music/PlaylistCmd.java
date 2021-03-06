package me.shadorc.discordbot.command.music;

import java.util.concurrent.BlockingQueue;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import me.shadorc.discordbot.command.AbstractCommand;
import me.shadorc.discordbot.command.CommandCategory;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.command.Role;
import me.shadorc.discordbot.exceptions.MissingArgumentException;
import me.shadorc.discordbot.music.GuildMusicManager;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.FormatUtils;
import me.shadorc.discordbot.utils.StringUtils;
import me.shadorc.discordbot.utils.TextUtils;
import me.shadorc.discordbot.utils.Utils;
import me.shadorc.discordbot.utils.command.RateLimiter;
import sx.blah.discord.util.EmbedBuilder;

public class PlaylistCmd extends AbstractCommand {

	public PlaylistCmd() {
		super(CommandCategory.MUSIC, Role.USER, RateLimiter.DEFAULT_COOLDOWN, "playlist");
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		GuildMusicManager musicManager = GuildMusicManager.getGuildMusicManager(context.getGuild());

		if(musicManager == null || musicManager.getScheduler().isStopped()) {
			BotUtils.sendMessage(TextUtils.NO_PLAYING_MUSIC, context.getChannel());
			return;
		}

		EmbedBuilder embed = Utils.getDefaultEmbed()
				.withAuthorName("Playlist")
				.withThumbnail("http://icons.iconarchive.com/icons/dtafalonso/yosemite-flat/512/Music-icon.png")
				.appendDescription(this.formatPlaylist(musicManager.getScheduler().getPlaylist()));
		BotUtils.sendMessage(embed.build(), context.getChannel());
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = Utils.getDefaultEmbed(this)
				.appendDescription("**Show current playlist.**");
		BotUtils.sendMessage(builder.build(), context.getChannel());
	}

	private String formatPlaylist(BlockingQueue<AudioTrack> queue) {
		if(queue.isEmpty()) {
			return "**The playlist is empty.**";
		}

		StringBuilder playlist = new StringBuilder("**" + StringUtils.pluralOf(queue.size(), "music") + " in the playlist:**\n");

		int count = 1;
		for(AudioTrack track : queue) {
			String name = "\n\t**" + count + ".** " + FormatUtils.formatTrackName(track.getInfo());
			if(playlist.length() + name.length() < 1800) {
				playlist.append(name);
				count++;
			} else {
				playlist.append("\n\t...");
				break;
			}
		}
		return playlist.toString();
	}
}