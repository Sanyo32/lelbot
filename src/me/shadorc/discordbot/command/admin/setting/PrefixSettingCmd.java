package me.shadorc.discordbot.command.admin.setting;

import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.data.DatabaseManager;
import me.shadorc.discordbot.data.Setting;
import me.shadorc.discordbot.exceptions.MissingArgumentException;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.Utils;
import me.shadorc.discordbot.utils.command.Emoji;
import sx.blah.discord.util.EmbedBuilder;

public class PrefixSettingCmd implements SettingCmd {

	private static final int PREFIX_MAX_LENGTH = 5;

	@Override
	public void execute(Context context, String arg) throws MissingArgumentException {
		if(arg == null) {
			throw new MissingArgumentException();
		}

		if(arg.contains(" ")) {
			BotUtils.sendMessage(Emoji.GREY_EXCLAMATION + " Prefix cannot contain space.", context.getChannel());
			return;
		}

		if(arg.length() > PREFIX_MAX_LENGTH) {
			BotUtils.sendMessage(Emoji.GREY_EXCLAMATION + " Prefix cannot contain more than " + PREFIX_MAX_LENGTH + " characters.", context.getChannel());
			return;
		}

		DatabaseManager.setSetting(context.getGuild(), Setting.PREFIX, arg);
		BotUtils.sendMessage(Emoji.CHECK_MARK + " '" + arg + "' is now the prefix for this server.", context.getChannel());
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = Utils.getDefaultEmbed()
				.withAuthorName("Help for setting: " + Setting.PREFIX.toString())
				.appendDescription("**" + this.getDescription() + "**")
				.appendField("Usage", "`" + context.getPrefix() + "settings " + Setting.PREFIX.toString() + " <prefix>`", false)
				.appendField("Argument", "**prefix** - Max length: 5, must not contain spaces", false)
				.appendField("Example", "`" + context.getPrefix() + "settings " + Setting.PREFIX.toString() + " !`", false);
		BotUtils.sendMessage(builder.build(), context.getChannel());
	}

	@Override
	public String getDescription() {
		return "Change Shadbot's prefix.";
	}
}
