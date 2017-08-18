package me.shadorc.discordbot.command.music;

import me.shadorc.discordbot.Config;
import me.shadorc.discordbot.Emoji;
import me.shadorc.discordbot.MissingArgumentException;
import me.shadorc.discordbot.Shadbot;
import me.shadorc.discordbot.command.AbstractCommand;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.music.GuildMusicManager;
import me.shadorc.discordbot.utils.BotUtils;
import sx.blah.discord.util.EmbedBuilder;

public class ShuffleCmd extends AbstractCommand {

	public ShuffleCmd() {
		super(Role.USER, "shuffle");
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		GuildMusicManager.getGuildAudioPlayer(context.getGuild()).getScheduler().shufflePlaylist();
		BotUtils.sendMessage(Emoji.CHECK_MARK + " Playlist has been shuffled.", context.getChannel());
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = new EmbedBuilder()
				.withAuthorName("Help for " + this.getNames()[0] + " command")
				.withAuthorIcon(Shadbot.getClient().getOurUser().getAvatarURL())
				.withColor(Config.BOT_COLOR)
				.appendDescription("**Shuffle current playlist.**");
		BotUtils.sendEmbed(builder.build(), context.getChannel());
	}

}