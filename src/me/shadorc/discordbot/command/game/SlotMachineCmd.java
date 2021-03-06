package me.shadorc.discordbot.command.game;

import me.shadorc.discordbot.command.AbstractCommand;
import me.shadorc.discordbot.command.CommandCategory;
import me.shadorc.discordbot.command.Context;
import me.shadorc.discordbot.command.Role;
import me.shadorc.discordbot.data.DatabaseManager;
import me.shadorc.discordbot.exceptions.MissingArgumentException;
import me.shadorc.discordbot.stats.StatsManager;
import me.shadorc.discordbot.utils.BotUtils;
import me.shadorc.discordbot.utils.FormatUtils;
import me.shadorc.discordbot.utils.MathUtils;
import me.shadorc.discordbot.utils.TextUtils;
import me.shadorc.discordbot.utils.Utils;
import me.shadorc.discordbot.utils.command.RateLimiter;
import sx.blah.discord.util.EmbedBuilder;

public class SlotMachineCmd extends AbstractCommand {

	private enum SlotOptions {
		CHERRIES(70),
		BELL(700),
		GIFT(10000);

		private final int gain;

		SlotOptions(int gain) {
			this.gain = gain;
		}

		public int getGain() {
			return gain;
		}
	}

	private static final int PAID_COST = 10;

	private static final SlotOptions[] SLOTS_ARRAY = new SlotOptions[] {
			SlotOptions.CHERRIES, SlotOptions.CHERRIES, SlotOptions.CHERRIES, SlotOptions.CHERRIES, // Winning chance : 12.5%
			SlotOptions.BELL, SlotOptions.BELL, SlotOptions.BELL, // Winning chance : 5.3%
			SlotOptions.GIFT }; // Winning chance : 0.2%

	public SlotMachineCmd() {
		super(CommandCategory.GAME, Role.USER, RateLimiter.GAME_COOLDOWN, "slot_machine", "slot-machine", "slotmachine");
		this.setAlias("sm");
	}

	@Override
	public void execute(Context context) throws MissingArgumentException {
		if(DatabaseManager.getCoins(context.getGuild(), context.getAuthor()) < PAID_COST) {
			BotUtils.sendMessage(TextUtils.notEnoughCoins(context.getAuthor()), context.getChannel());
			return;
		}

		SlotOptions slot1 = SLOTS_ARRAY[MathUtils.rand(SLOTS_ARRAY.length)];
		SlotOptions slot2 = SLOTS_ARRAY[MathUtils.rand(SLOTS_ARRAY.length)];
		SlotOptions slot3 = SLOTS_ARRAY[MathUtils.rand(SLOTS_ARRAY.length)];

		int gains = -PAID_COST;
		if(Utils.allEqual(slot1, slot2, slot3)) {
			gains = slot1.getGain();
		}

		DatabaseManager.addCoins(context.getChannel(), context.getAuthor(), gains);
		StatsManager.increment(this.getFirstName(), gains);

		StringBuilder message = new StringBuilder(
				":" + slot1.toString().toLowerCase() + ": :" + slot2.toString().toLowerCase() + ": :" + slot3.toString().toLowerCase() + ":"
						+ "\nYou " + (gains > 0 ? "win" : "have lost") + " **" + FormatUtils.formatCoins(Math.abs(gains)) + "** !");
		BotUtils.sendMessage(message.toString(), context.getChannel());
	}

	@Override
	public void showHelp(Context context) {
		EmbedBuilder builder = Utils.getDefaultEmbed(this)
				.appendDescription("**Play slot machine.**")
				.appendField("Cost", "A game costs **" + PAID_COST + " coins**.", false)
				.appendField("Gains", "You can win **" + SlotOptions.CHERRIES.getGain() + "**, **" + SlotOptions.BELL.getGain() + "** or **"
						+ FormatUtils.formatCoins(SlotOptions.GIFT.getGain()) + "** ! Good luck.", false);
		BotUtils.sendMessage(builder.build(), context.getChannel());
	}
}
