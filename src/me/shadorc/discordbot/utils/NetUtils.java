package me.shadorc.discordbot.utils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import me.shadorc.discordbot.Shadbot;
import me.shadorc.discordbot.data.Config;
import me.shadorc.discordbot.data.Config.APIKey;
import sx.blah.discord.api.IShard;

public class NetUtils {

	/**
	 * @param url - the String representing URL
	 * @return the Document from url
	 * @throws IOException
	 */
	public static Document getDoc(String url) throws IOException {
		return Jsoup.connect(url)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.91 Safari/537.36 Vivaldi/1.92.917.35")
				.timeout(Config.DEFAULT_TIMEOUT)
				.get();
	}

	/**
	 * @param url - the String representing URL
	 * @return the response from url
	 * @throws IOException
	 */
	public static Response getResponse(String url) throws IOException {
		return Jsoup.connect(url)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.91 Safari/537.36 Vivaldi/1.92.917.35")
				.timeout(Config.DEFAULT_TIMEOUT)
				.ignoreContentType(true)
				.ignoreHttpErrors(true)
				.execute();
	}

	/**
	 * @param url - the String representing URL
	 * @return the body as a String from url
	 * @throws IOException
	 */
	public static String getBody(String url) throws IOException {
		return NetUtils.getResponse(url).body();
	}

	/**
	 * @param stringUrl - the String to check
	 * @return true if stringUrl is a valid URL, false otherwise
	 */
	public static boolean isValidURL(String stringUrl) {
		try {
			new URL(stringUrl).openConnection().connect();
			return true;
		} catch (IllegalArgumentException | IOException err) {
			return false;
		}
	}

	public static void postStats() {
		for(IShard shard : Shadbot.getClient().getShards()) {
			NetUtils.postStatsOn("https://bots.discord.pw", APIKey.BOTS_DISCORD_PW_TOKEN, shard);
			NetUtils.postStatsOn("https://discordbots.org", APIKey.DISCORD_BOTS_ORG_TOKEN, shard);
		}
	}

	private static void postStatsOn(String homeUrl, APIKey token, IShard shard) {
		try {
			JSONObject content = new JSONObject()
					.put("shard_id", shard.getInfo()[0])
					.put("shard_count", Shadbot.getClient().getShardCount())
					.put("server_count", shard.getGuilds().size());

			Map<String, String> header = new HashMap<>();
			header.put("Content-Type", "application/json");
			header.put("Authorization", Config.get(token));

			String url = homeUrl + "/api/bots/" + Shadbot.getClient().getOurUser().getLongID() + "/stats";
			Document response = Jsoup.connect(url)
					.method(Method.POST)
					.ignoreContentType(true)
					.headers(header)
					.requestBody(content.toString())
					.post();

			LogUtils.info("Stats posted to " + homeUrl + " (Response: " + response.text() + ")");
		} catch (Exception err) {
			LogUtils.info("An error occurred while posting stats. (" + err.getClass().getSimpleName() + ": " + err.getMessage() + ")");
		}
	}
}
