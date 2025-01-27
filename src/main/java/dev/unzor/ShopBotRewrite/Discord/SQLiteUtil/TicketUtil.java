package dev.unzor.ShopBotRewrite.Discord.SQLiteUtil;

import dev.unzor.ShopBotRewrite.Constants;
import dev.unzor.ShopBotRewrite.Main;
import dev.unzor.ShopBotRewrite.Utils.ColorUtils;
import dev.unzor.ShopBotRewrite.Utils.EmbedUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.awt.*;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TicketUtil {
    static Logger LOGGER = Logger.getLogger(TicketUtil.class.getName());
    private static boolean foundCategory = false;
    static String categoryName = "Tickets";

    public static void createTicket(User user) {
        Guild guild = Main.jda.getGuildById(Constants.GUID);
        if (guild == null) {
            LOGGER.severe("Guild not found");
            EmbedUtil.sendEmbedToOwner(EmbedUtil.errorEmbedInCommand("Could not find guild", "createticket"));
            return;
        }

        guild.retrieveMember(user).queue(member -> {
            if (member == null) {
                LOGGER.severe("Member not found");
                EmbedUtil.sendEmbedToOwner(EmbedUtil.errorEmbedInCommand("Could not find author member. (May not be a member of the guild)", "createticket"));
                return;
            }

            for (Category category : guild.getCategories()) {
                if (category.getName().equalsIgnoreCase(categoryName)) {
                    foundCategory = true;
                    if (Constants.MinPurchase <= CartUtil.getTotal(user)) {
                        String formName = user.getName().replaceAll(" ", "-");
                        ChannelAction<TextChannel> channelAction = category.createTextChannel("Ticket-" + formName);
                        channelAction.addPermissionOverride(member,
                                        EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_EXT_EMOJI),
                                        null)
                                .queue(channel -> {
                                    channel.sendMessage(user.getAsMention()).queue();

                                    if (Constants.MentionRole) {
                                        channel.sendMessage(guild.getRoleById(Constants.ShopRole).getAsMention()).queue();
                                    }

                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setTitle(user.getName() + "'s order (Click here to pay)", Constants.paypallink);
                                    embed.setDescription(CartUtil.ticketMap.get(user.getId()));
                                    embed.setColor(ColorUtils.getRandomColor());

                                    EmbedBuilder embed2 = new EmbedBuilder();
                                    embed2.setTitle("Just in case...");
                                    embed2.setDescription(
                                           "Hi! \uD83D\uDE0A\n" +
                                           "Thank you for choosing us! We offer support in English, Spanish, and Portuguese.\n" +
                                           "\n" +
                                           "We’ve received your order \uD83C\uDF89! A delivery team member will contact you here within the next 40 minutes. If you don’t get a message after that, don’t worry—they might be resting or studying. They’ll get back to you as soon as possible.\n" +
                                           "\n" +
                                           "To speed up the process, please send your payment to this [PayPal link](" + Constants.paypallink + ") and upload a photo of the payment here.\n" +
                                           "\n" +
                                           "We accept PayPal and crypto only. For card payments, please use our website: " + Constants.website + ".\n" +
                                           "\n" +
                                           "Thanks again for choosing us! The best 2b2t store is processing your order \uD83D\uDE80.");
                                    embed2.setColor(ColorUtils.getRandomColor());

                                    channel.sendMessageEmbeds(embed.build()).queue();
                                    channel.sendMessageEmbeds(embed2.build()).addActionRow(
                                            Button.success("done", "Done").withEmoji(Emoji.fromUnicode("U+2705")),
                                            Button.danger("delete", "Delete").withEmoji(Emoji.fromUnicode("U+1F4A3")),
                                            Button.secondary("markasap", "Mark as ASAP").withEmoji(Emoji.fromUnicode("U+1F4EC"))
                                    ).queue(pm -> user.openPrivateChannel().queue(pm1 -> pm1.sendMessage("Ticket created").queue()));
                                });
                    } else {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Not enough money");
                        embed.setDescription("You need to have at least " + Constants.MinPurchase + Constants.Currency + " to create a ticket");
                        embed.setColor(Color.RED);
                        user.openPrivateChannel().queue(channel ->
                                channel.sendMessageEmbeds(embed.build()).queue(message ->
                                        message.delete().queueAfter(5, TimeUnit.SECONDS)));
                        return;
                    }
                }
            }

            if (!foundCategory) {
                LOGGER.severe("Could not find category:" + categoryName);
                EmbedUtil.sendEmbedToOwner(EmbedUtil.errorEmbedInCommand("Could not find category " + categoryName, "createticket"));
            }
        }, error -> {
            LOGGER.severe("Failed to retrieve member: " + error.getMessage());
            EmbedUtil.sendEmbedToOwner(EmbedUtil.errorEmbedInCommand("Failed to retrieve member: " + error.getMessage(), "createticket"));
        });
    }
}
