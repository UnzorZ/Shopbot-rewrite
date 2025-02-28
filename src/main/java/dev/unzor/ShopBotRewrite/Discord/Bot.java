package dev.unzor.ShopBotRewrite.Discord;

import dev.unzor.ShopBotRewrite.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.util.Collections;

public class Bot {
    public static JDA jda;
    public static synchronized JDA init() throws LoginException {
        JDA jda = JDABuilder.createLight(Constants.Token, Collections.emptyList())
                .addEventListeners(new Listener(), new ContextMenuListener())
                .setActivity(Activity.playing(Constants.Activity))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("ping", "Calculate the ping of the bot"),
                Commands.slash("additem", "Creates an item")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL))
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "name", "Name of the item", true)
                        .addOption(OptionType.NUMBER, "price", "Price of the item", true)
                        .addOption(OptionType.STRING, "imageurl", "Image url displayed on the item", true)
                        .addOption(OptionType.NUMBER, "quantity", "Quantity of items you have")
                        .addOption(OptionType.STRING, "description", "Description of the item")
                        .addOption(OptionType.STRING, "color", "Color of the embed"),
                Commands.slash("creatediscount", "Creates a discount")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL))
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "id", "Id of the discount. For example: 20off", true)
                        .addOption(OptionType.STRING, "discountvalue", "Discount value", true)
                        .addOption(OptionType.STRING, "usagelimit", "Usage limit (unlimited by default)", false)
                        .addOption(OptionType.BOOLEAN, "active", "Is active (true by default)"),
                Commands.slash("removediscount", "Removes a discount")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL))
                        .setGuildOnly(true)
                        .addOption(OptionType.STRING, "id", "Id of the discount. For example: 20off", true),
                Commands.slash("adddiscount", "Adds a discount")
                        .addOption(OptionType.STRING, "id", "Id of the discount. For example: 20off", true),
                Commands.slash("getdiscountlist", "Gets a list of all the discounts")
                         .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL))
                         .setGuildOnly(true),
                Commands.message("Disable item")
                                .setGuildOnly(true),
                Commands.message("Enable item")
                                .setGuildOnly(true),
                Commands.message("Edit Quantity")
                                .setGuildOnly(true)
        ).queue();
        return jda;
    }
}
