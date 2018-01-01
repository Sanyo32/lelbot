package me.shadorc.shadbot.utils.embed;

import me.shadorc.shadbot.Shadbot;
import me.shadorc.shadbot.utils.StringUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.EmbedBuilder;

public class LogBuilder {

	private final LogType type;
	private final String message;
	private final Exception err;
	private final String input;
	private final IChannel channel;

	public LogBuilder(LogType type, String message, Exception err, String input, IChannel channel) {
		this.type = type;
		this.message = message;
		this.err = err;
		this.input = input;
		this.channel = channel;
	}

	public LogBuilder(LogType type, String message, Exception err) {
		this(type, message, err, null, null);
	}

	public LogBuilder(LogType type, String message) {
		this(type, message, null, null, null);
	}

	public EmbedObject build() {
		EmbedBuilder builder = EmbedUtils.getDefaultEmbed()
				.setLenient(true)
				.withAuthorName(String.format("%s (Version: %s)", StringUtils.capitalize(type.toString()), Shadbot.getVersion()))
				.withDescription(message);

		if(err != null) {
			builder.appendField("Error type", err.getClass().getSimpleName(), false);
			builder.appendField("Error message", err.getMessage(), false);
		}

		if(input != null) {
			builder.appendField("Input", input, false);
		}

		builder.appendField("User warned", Boolean.toString(channel != null), false);

		return builder.build();
	}
}