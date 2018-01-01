package me.shadorc.shadbot.command.utils;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import me.shadorc.shadbot.Config;
import me.shadorc.shadbot.core.command.AbstractCommand;
import me.shadorc.shadbot.core.command.CommandCategory;
import me.shadorc.shadbot.core.command.Context;
import me.shadorc.shadbot.core.command.annotation.Command;
import me.shadorc.shadbot.core.command.annotation.RateLimited;
import me.shadorc.shadbot.exception.MissingArgumentException;
import me.shadorc.shadbot.utils.BotUtils;
import me.shadorc.shadbot.utils.ExceptionUtils;
import me.shadorc.shadbot.utils.NetUtils;
import me.shadorc.shadbot.utils.StringUtils;
import me.shadorc.shadbot.utils.TextUtils;
import me.shadorc.shadbot.utils.embed.HelpBuilder;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

@RateLimited
@Command(category = CommandCategory.UTILS, names = { "wiki", "wikipedia" })
public class WikiCmd extends AbstractCommand {

	@Override
	public void execute(Context context) throws MissingArgumentException {
		if(!context.hasArg()) {
			throw new MissingArgumentException();
		}

		try {
			// Wiki api doc https://en.wikipedia.org/w/api.php?action=help&modules=query%2Bextracts
			JSONObject mainObj = new JSONObject(NetUtils.getBody("https://en.wikipedia.org/w/api.php?"
					+ "format=json"
					+ "&action=query"
					+ "&titles=" + NetUtils.encode(context.getArg())
					+ "&redirects=true"
					+ "&prop=extracts"
					+ "&explaintext=true"
					+ "&exintro=true"
					+ "&exsentences=5"));

			JSONObject pagesObj = mainObj.getJSONObject("query").getJSONObject("pages");
			String pageId = pagesObj.names().getString(0);
			JSONObject resultObj = pagesObj.getJSONObject(pageId);

			if("-1".equals(pageId) || resultObj.getString("extract").isEmpty()) {
				BotUtils.sendMessage(TextUtils.noResult(context.getArg()), context.getChannel());
				return;
			}

			String extract = StringUtils.truncate(resultObj.getString("extract"), EmbedBuilder.DESCRIPTION_CONTENT_LIMIT);

			EmbedBuilder builder = new EmbedBuilder()
					.withAuthorName("Wikipedia")
					.withAuthorIcon("https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Mohapedia.png/842px-Mohapedia.png")
					.withUrl("https://fr.wikipedia.org/wiki/" + resultObj.getString("title").replace(" ", "_"))
					.withColor(Config.BOT_COLOR)
					.withTitle(resultObj.getString("title"))
					.appendDescription(extract);
			BotUtils.sendMessage(builder.build(), context.getChannel());

		} catch (JSONException | IOException err) {
			ExceptionUtils.handle("getting Wikipedia information", context, err);
		}
	}

	@Override
	public EmbedObject getHelp(Context context) {
		return new HelpBuilder(this, context.getPrefix())
				.setDescription("Show Wikipedia description for a search.")
				.addArg("search", false)
				.build();
	}

}