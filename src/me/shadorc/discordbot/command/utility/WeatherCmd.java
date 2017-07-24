package me.shadorc.discordbot.command.utility;

import java.awt.Color;

import il.ac.hit.finalproject.classes.IWeatherDataService;
import il.ac.hit.finalproject.classes.Location;
import il.ac.hit.finalproject.classes.WeatherData;
import il.ac.hit.finalproject.classes.WeatherDataServiceFactory;
import il.ac.hit.finalproject.classes.WeatherDataServiceFactory.service;
import me.shadorc.discordbot.command.Command;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.utility.BotUtils;
import me.shadorc.discordbot.utility.Log;
import me.shadorc.discordbot.utility.Utils;
import sx.blah.discord.util.EmbedBuilder;

public class WeatherCmd extends Command{

	public WeatherCmd() {
		super(false, "meteo", "météo", "weather");
	}

	@Override
	public void execute(Context context) {
		if(context.getArg() == null) {
			BotUtils.sendMessage(":grey_exclamation: Merci d'indiquer le nom d'une ville.", context.getChannel());
			return;
		}

		IWeatherDataService dataService = WeatherDataServiceFactory.getWeatherDataService(service.OPEN_WEATHER_MAP);
		try {
			WeatherData data = dataService.getWeatherData(new Location(context.getArg(), "FR"));

			EmbedBuilder builder = new EmbedBuilder()
					.withAuthorName("Météo pour la ville de " + data.getCity().getName())
					.withDesc("Dernière mise à jour le " + data.getLastUpdate().getValue())
					.withThumbnail("https://image.flaticon.com/icons/svg/494/494472.svg")
					.withAuthorIcon(context.getAuthor().getAvatarURL())
					.withColor(new Color(170, 196, 222))
					.appendField(":cloud: Nuages", Utils.capitalize(Utils.translate("en", "fr", data.getClouds().getValue())), true)
					.appendField(":wind_blowing_face: Vent", Utils.translate("en", "fr", data.getWind().getSpeed().getName()) + "\n" + Float.parseFloat(data.getWind().getSpeed().getValue())*3.6f + " km/h", true)
					.appendField(":cloud_rain: Précipitations", (data.getPrecipitation().getMode().equals("no") ? "Aucune" : data.getPrecipitation().getValue()), true)
					.appendField(":thermometer: Température", data.getTemperature().getValue() + "°C", true)
					.withFooterText("Informations provenant du site OpenWeatherMap");

			BotUtils.sendEmbed(builder.build(), context.getChannel());

		} catch (Exception e) {
			Log.error("Une erreur est survenue lors de la récupération des données météorologiques.", e, context.getChannel());
		}
	}

}
