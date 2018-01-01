package me.shadorc.shadbot.listener.music;

import org.jsoup.Jsoup;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import me.shadorc.shadbot.music.GuildMusicManager;
import me.shadorc.shadbot.utils.BotUtils;
import me.shadorc.shadbot.utils.FormatUtils;
import me.shadorc.shadbot.utils.LogUtils;
import me.shadorc.shadbot.utils.StringUtils;
import me.shadorc.shadbot.utils.command.Emoji;

public class AudioEventListener extends AudioEventAdapter {

	private final GuildMusicManager musicManager;

	private int errorCount;

	public AudioEventListener(GuildMusicManager musicManager) {
		super();
		this.musicManager = musicManager;
		this.errorCount = 0;
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		BotUtils.sendMessage(String.format(Emoji.MUSICAL_NOTE + " Currently playing: **%s**", FormatUtils.formatTrackName(track.getInfo())),
				musicManager.getChannel());
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			errorCount = 0; // Everything seems to be fine, reset error count.
			if(!musicManager.getScheduler().nextTrack()) {
				musicManager.end();
			}
		}
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException err) {
		errorCount++;

		String errMessage = Jsoup.parse(StringUtils.remove(err.getMessage(), "Watch on YouTube")).text().trim();

		if(errorCount <= 3) {
			BotUtils.sendMessage(String.format(Emoji.RED_CROSS + " Sorry, %s. I'll try to play the next available song.",
					errMessage.toLowerCase()), musicManager.getChannel());
		}

		if(errorCount == 3) {
			BotUtils.sendMessage(Emoji.RED_FLAG + " Too many errors in a row, I will ignore them until finding a music that can be played.",
					musicManager.getChannel());
			LogUtils.infof("{Guild ID: %d} Too many errors in a row. They will be ignored until music can be played.",
					musicManager.getChannel().getGuild().getLongID());
		}

		LogUtils.infof("{Guild ID: %d} %sTrack exception: %s",
				musicManager.getChannel().getGuild().getLongID(), errorCount > 3 ? "(Ignored) " : "", errMessage);

		if(!musicManager.getScheduler().nextTrack()) {
			musicManager.end();
		}
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		BotUtils.sendMessage(Emoji.RED_EXCLAMATION + " Music seems stuck, I'll try to play the next available song.", musicManager.getChannel());
		LogUtils.warnf("{Guild ID: %d} Music stuck, skipping it.", musicManager.getChannel().getGuild().getLongID());

		if(!musicManager.getScheduler().nextTrack()) {
			musicManager.end();
		}
	}
}