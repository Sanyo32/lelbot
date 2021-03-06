package me.shadorc.discordbot.utils.game;

import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.data.DatabaseManager;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.FormatUtils;
import me.shadorc.discordbot.utils.StringUtils;
import me.shadorc.discordbot.utils.TextUtils;
import me.shadorc.discordbot.utils.command.Emoji;

public class GameUtils {

	/**
	 * @param betStr - the bet to check
	 * @param context - the context
	 * @return betStr has an Integer if it's a valid bet, null otherwise
	 */
	public static Integer parseBetOrWarn(String betStr, int maxValue, Context context) {
		if(!StringUtils.isPositiveInt(betStr)) {
			BotUtils.sendMessage(Emoji.GREY_EXCLAMATION + " Invalid bet.", context.getChannel());
			return null;
		}

		int bet = Integer.parseInt(betStr);
		if(DatabaseManager.getCoins(context.getGuild(), context.getAuthor()) < bet) {
			BotUtils.sendMessage(TextUtils.notEnoughCoins(context.getAuthor()), context.getChannel());
			return null;
		}

		if(bet > maxValue) {
			BotUtils.sendMessage(Emoji.BANK + " Sorry, you can't bet more than **"
					+ FormatUtils.formatCoins(maxValue) + "**.", context.getChannel());
			return null;
		}

		return bet;
	}
}
