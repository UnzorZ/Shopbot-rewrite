package dev.unzor.ShopBotRewrite.Utils;

import dev.unzor.ShopBotRewrite.Constants;
import dev.unzor.ShopBotRewrite.Main;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class EmbedUtil {
    public static EmbedBuilder errorEmbed(String description) {
        EmbedBuilder error = new EmbedBuilder();
        error.setTitle("ERROR");
        error.setDescription("Description: " + description);
        error.setColor(Color.RED);
        return error;
    }

    public static EmbedBuilder errorEmbedInCommand(String description, String action) {
        EmbedBuilder error = new EmbedBuilder();
        error.setTitle("ERROR");
        error.setDescription("An error has occured in the command or action someone have requested.\n **Action:** " + action + "\n**Description:** " + description);
        error.setColor(Color.RED);
        return error;
    }
    public static void sendEmbedToOwner(EmbedBuilder embed) {
        Main.jda.getUserById(Constants.OwnerID).openPrivateChannel().queue(channel -> channel.sendMessageEmbeds(embed.build()).queue());
    }
}
