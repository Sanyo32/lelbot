package me.shadorc.discordbot.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import me.shadorc.discordbot.utils.LogUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class LottoDataManager {

	private static final File LOTTERY_DATA_FILE = new File("lotto_data.json");

	@SuppressWarnings("ucd")
	private static JSONObject dataObj;

	static {
		if(!LOTTERY_DATA_FILE.exists()) {
			try (FileWriter writer = new FileWriter(LOTTERY_DATA_FILE)) {
				JSONObject defaultObj = new JSONObject();
				defaultObj.put(JSONKey.USERS.toString(), new JSONArray());
				defaultObj.put(JSONKey.POOL.toString(), 0);
				writer.write(defaultObj.toString(Config.INDENT_FACTOR));
				writer.flush();

			} catch (IOException err) {
				LogUtils.LOGGER.error("An error occurred during lotto data file creation. Exiting.", err);
				System.exit(1);
			}
		}

		try (InputStream stream = LOTTERY_DATA_FILE.toURI().toURL().openStream()) {
			dataObj = new JSONObject(new JSONTokener(stream));
		} catch (JSONException | IOException err) {
			LogUtils.LOGGER.error("An error occurred during lotto data file initialisation. Exiting.", err);
			System.exit(1);
		}
	}

	public static synchronized void addToPool(int coins) {
		int pool = (int) Math.max(0, Math.min(Config.MAX_COINS, dataObj.optInt(JSONKey.POOL.toString()) + Math.ceil(coins / 100f)));
		dataObj.put(JSONKey.POOL.toString(), pool);
	}

	public static synchronized void addPlayer(IGuild guild, IUser user, int num) {
		JSONObject playerObj = new JSONObject()
				.put(JSONKey.GUILD_ID.toString(), guild.getLongID())
				.put(JSONKey.USER_ID.toString(), user.getLongID())
				.put(JSONKey.NUM.toString(), num);
		dataObj.getJSONArray(JSONKey.USERS.toString()).put(playerObj);
	}

	public static int getPool() {
		return dataObj.getInt(JSONKey.POOL.toString());
	}

	public static JSONArray getPlayers() {
		return dataObj.getJSONArray(JSONKey.USERS.toString());
	}

	public static JSONObject getHistoric() {
		return dataObj.optJSONObject(JSONKey.HISTORIC.toString());
	}

	public static void setHistoric(int winnersCount, int pool, int num) {
		dataObj.put(JSONKey.HISTORIC.toString(), new JSONObject().put(JSONKey.HISTORIC_WINNERS_COUNT.toString(), winnersCount)
				.put(JSONKey.HISTORIC_POOL.toString(), pool)
				.put(JSONKey.HISTORIC_NUM.toString(), num));
	}

	public static void reset() {
		dataObj.put(JSONKey.USERS.toString(), new JSONArray());
		dataObj.put(JSONKey.POOL.toString(), 0);
	}

	public static void save() {
		try (FileWriter writer = new FileWriter(LOTTERY_DATA_FILE)) {
			writer.write(dataObj.toString(Config.INDENT_FACTOR));
			writer.flush();

		} catch (IOException err) {
			LogUtils.error("Error while saving lotto data.", err);
		}
		LogUtils.info("Lotto data saved.");
	}
}
