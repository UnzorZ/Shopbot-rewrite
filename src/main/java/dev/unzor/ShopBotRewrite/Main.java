package dev.unzor.ShopBotRewrite;

import dev.unzor.ShopBotRewrite.Utils.ConfigUtils.ConfigUtils;
import dev.unzor.ShopBotRewrite.Discord.Bot;
import dev.unzor.ShopBotRewrite.Discord.SQLiteUtil.SqlUtil;
import dev.unzor.ShopBotRewrite.Utils.UpdateUtils.Update;
import net.dv8tion.jda.api.JDA;

import javax.security.auth.login.LoginException;

public class Main {
    public static JDA jda;
    public static void main(String[] args) {
        Constants.debug = false;

        for (String arg : args) {
            switch (arg) {
                case "-debug" -> Constants.debug = true;
                case "-update" -> Update.update(); //TODO (xD esto no se va a hacer)
            }
        }
        SqlUtil.createTable();
        SqlUtil.registerItems();

        ConfigUtils.init();
        System.out.println("[+] Bot started");
        startBot();
    }

    public static void startBot() {
        if (jda == null) {
            try {
                jda = Bot.init();
            } catch (LoginException e) {
                e.printStackTrace();
            }

        }
    }
}