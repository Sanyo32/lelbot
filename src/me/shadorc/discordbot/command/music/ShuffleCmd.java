package me.shadorc.discordbot.command.music;

import me.shadorc.discordbot.command.AbstractCommand;
import me.shadorc.discordbot.command.CommandCategory;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.command.Role;
import me.shadorc.discordbot.exceptions.MissingArgumentException;
import me.shadorc.discordbot.music.GuildMusicManager;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.TextUtils;
import me.shadorc.discordbot.utils.Utils;
import me.shadorc.discordbot.utils.command.Emoji;
import me.shadorc.discordbot.utils.command.RateLimiter;
import sx.blah.discord.util.EmbedBuilder;

public class ShuffleCmd extends AbstractCommand {

	public ShuffleCmd() {
		super(CommandCategory.MUSIC, Role.USER, RateLimiter.DEFAULT_COOLDOWN, "shuffle");
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		GuildMusicManager musicManager = GuildMusicManager.getGuildMusicManager(context.getGuild());

		if(musicManager == null || musicManager.getScheduler().isStopped()) {
			BotUtils.sendMessage(TextUtils.NO_PLAYING_MUSIC, context.getChannel());
			return;
		}

		musicManager.getScheduler().shufflePlaylist();
		BotUtils.sendMessage(Emoji.CHECK_MARK + " Playlist shuffled.", context.getChannel());
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = Utils.getDefaultEmbed(this)
				.appendDescription("**Shuffle current playlist.**");
		BotUtils.sendMessage(builder.build(), context.getChannel());
	}

}
