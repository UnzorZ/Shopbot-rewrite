package dev.unzor.ShopBotRewrite.Discord.Commands;

import dev.unzor.ShopBotRewrite.Constants;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.Discount;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.SqlUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class CreateDiscountCommand {
    public static void createDiscount(SlashCommandInteractionEvent event) {
        if (event.getMember().getRoles().stream().noneMatch(role -> role.getId().equals(Constants.ShopRole))) {
            event.reply("You do not have the shop role").setEphemeral(true).queue();
            return;
        }

        String id = event.getOption("id", OptionMapping::getAsString);
        String discountValue = event.getOption("discountvalue", OptionMapping::getAsString);
        String usageLimit = event.getOption("usagelimit", OptionMapping::getAsString);
        boolean isActive = event.getOption("active", OptionMapping::getAsBoolean);

        int discountValueInt = discountValue == null ? 0 : Integer.parseInt(discountValue);
        int usageLimitInt = usageLimit == null ? 0 : Integer.parseInt(usageLimit);

        if (id == null || discountValueInt == 0) {
            event.reply("Invalid parameters").setEphemeral(true).queue();
            return;
        } else if (discountValueInt > 100) {
            event.reply("Discount value cannot be greater than 100").setEphemeral(true).queue();
            return;
        } else if (discountValueInt < 1) {
            event.reply("Discount value cannot be less than 1").setEphemeral(true).queue();
            return;
        }

        Discount discount = new Discount(id, discountValueInt, usageLimitInt, 0, isActive);
        SqlUtil.createDiscount(discount);
        event.reply("Discount created!").setEphemeral(true).queue();
    }
}
