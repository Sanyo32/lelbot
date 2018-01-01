package me.shadorc.shadbot.command.currency;

import me.shadorc.shadbot.core.command.AbstractCommand;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.core.command.annotation.Command;
import me.shadorc.shadbot.core.command.annotation.RateLimited;
import me.shadorc.shadbot.data.db.Database;
import me.shadorc.shadbot.exception.MissingArgumentException;
import me.shadorc.shadbot.utils.BotUtils;
import me.shadorc.shadbot.utils.FormatUtils;
import me.shadorc.shadbot.utils.command.Emoji;
import me.shadorc.shadbot.utils.embed.HelpBuilder;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;

@RateLimited
@Command(category = CommandCategory.CURRENCY, names = { "coins", "coin" })
public class CoinsCmd extends AbstractCommand {

	@Override
	public void execute(Context context) throws MissingArgumentException {
		String str;
		if(context.getMessage().getMentions().isEmpty()) {
			str = String.format("You have **%s**.",
					FormatUtils.formatCoins(Database.getDBUser(context.getGuild(), context.getAuthor()).getCoins()));
		} else {
			IUser user = context.getMessage().getMentions().get(0);
			int coins = Database.getDBUser(context.getGuild(), user).getCoins();
			str = String.format("%s has **%s**.",
					user.getName(), FormatUtils.formatCoins(coins));
		}
		BotUtils.sendMessage(Emoji.PURSE + " " + str, context.getChannel());
	}

	@Override
	public EmbedObject getHelp(Context context) {
		return new HelpBuilder(this, context.getPrefix())
				.setDescription("Show how much coins you or another user have.")
				.addArg("@user", true)
				.build();
	}
}