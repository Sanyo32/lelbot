package me.shadorc.discordbot.command.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import me.shadorc.discordbot.command.AbstractCommand;
import me.shadorc.discordbot.command.CommandCategory;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.command.Role;
import me.shadorc.discordbot.data.Config;
import me.shadorc.discordbot.data.Config.APIKey;
import me.shadorc.discordbot.exceptions.MissingArgumentException;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.ExceptionUtils;
import me.shadorc.discordbot.utils.MathUtils;
import me.shadorc.discordbot.utils.StringUtils;
import me.shadorc.discordbot.utils.Utils;
import me.shadorc.discordbot.utils.command.Emoji;
import me.shadorc.discordbot.utils.command.RateLimiter;
import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;
import net.aksingh.owmjapis.OpenWeatherMap.Units;
import sx.blah.discord.util.EmbedBuilder;

public class WeatherCmd extends AbstractCommand {

	private final SimpleDateFormat dateFormatter;

	public WeatherCmd() {
		super(CommandCategory.UTILS, Role.USER, RateLimiter.DEFAULT_COOLDOWN, "weather");
		this.dateFormatter = new SimpleDateFormat("MMMMM d, yyyy - hh:mm aa", Locale.ENGLISH);
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		if(!context.hasArg()) {
			throw new MissingArgumentException();
		}

		try {
			OpenWeatherMap owm = new OpenWeatherMap(Units.METRIC, Config.get(APIKey.OPENWEATHERMAP_API_KEY));
			CurrentWeather weather = owm.currentWeatherByCityName(context.getArg());

			if(weather.isValid()) {
				String clouds = StringUtils.capitalize(weather.getWeatherInstance(0).getWeatherDescription());
				float windSpeed = weather.getWindInstance().getWindSpeed() * 3.6f;
				String windDesc = this.getWindDesc(windSpeed);
				String rain = weather.hasRainInstance() ? String.format("%.1f mm/h", weather.getRainInstance().getRain3h()) : "None";
				float humidity = weather.getMainInstance().getHumidity();
				float temperature = weather.getMainInstance().getTemperature();

				EmbedBuilder builder = Utils.getDefaultEmbed()
						.withAuthorName("Weather for: " + weather.getCityName())
						.withThumbnail("https://image.flaticon.com/icons/svg/494/494472.svg")
						.withUrl("http://openweathermap.org/city/" + weather.getCityCode())
						.appendDescription("Last update on " + dateFormatter.format(weather.getDateTime()))
						.appendField(Emoji.CLOUD + " Clouds", clouds, true)
						.appendField(Emoji.WIND + " Wind", windDesc + "\n" + String.format("%.1f", windSpeed) + " km/h", true)
						.appendField(Emoji.RAIN + " Rain", rain, true)
						.appendField(Emoji.DROPLET + " Humidity", humidity + "%", true)
						.appendField(Emoji.THERMOMETER + " Temperature", String.format("%.1f", temperature) + "°C", true);

				BotUtils.sendMessage(builder.build(), context.getChannel());
			} else {
				BotUtils.sendMessage(Emoji.MAGNIFYING_GLASS + " City not found.", context.getChannel());
			}
		} catch (IOException err) {
			ExceptionUtils.manageException("getting weather information", context, err);
		}
	}

	private String getWindDesc(float windSpeed) {
		String windDesc;
		if(windSpeed < 1) {
			windDesc = "Calm";
		} else if(MathUtils.inRange(windSpeed, 1, 6)) {
			windDesc = "Light air";
		} else if(MathUtils.inRange(windSpeed, 6, 12)) {
			windDesc = "Light breeze";
		} else if(MathUtils.inRange(windSpeed, 12, 20)) {
			windDesc = "Gentle breeze";
		} else if(MathUtils.inRange(windSpeed, 20, 29)) {
			windDesc = "Moderate breeze";
		} else if(MathUtils.inRange(windSpeed, 29, 39)) {
			windDesc = "Fresh breeze";
		} else if(MathUtils.inRange(windSpeed, 39, 50)) {
			windDesc = "Strong breeze";
		} else if(MathUtils.inRange(windSpeed, 50, 62)) {
			windDesc = "Near gale";
		} else if(MathUtils.inRange(windSpeed, 62, 75)) {
			windDesc = "Gale";
		} else if(MathUtils.inRange(windSpeed, 75, 89)) {
			windDesc = "Strong gale";
		} else if(MathUtils.inRange(windSpeed, 89, 103)) {
			windDesc = "Storm";
		} else if(MathUtils.inRange(windSpeed, 103, 118)) {
			windDesc = "Violent storm";
		} else {
			windDesc = "Hurricane";
		}
		return windDesc;
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = Utils.getDefaultEmbed(this)
				.appendDescription("**Show weather report for a city.**")
				.appendField("Usage", "`" + context.getPrefix() + this.getFirstName() + " <city>`", false);
		BotUtils.sendMessage(builder.build(), context.getChannel());
	}
}
