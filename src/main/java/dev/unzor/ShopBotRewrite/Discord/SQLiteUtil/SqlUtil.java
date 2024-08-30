package dev.unzor.ShopBotRewrite.Discord.SQLiteUtil;

import dev.unzor.ShopBotRewrite.Utils.EmbedUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.sqlite.SQLiteErrorCode;

import java.sql.*;
import java.util.ArrayList;

public class SqlUtil {
    private static Connection connect(){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:items.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return c;
    }
    public static void createTable() {
        Connection c = connect();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sqlStmt = "CREATE TABLE IF NOT EXISTS ITEMS " +
                    "(NAME TEXT NOT NULL, " +
                    " PRICE  DOUBLE NOT NULL, " +
                    " MSGID TEXT, " +
                    " CHANID TEXT, " +
                    " QUANTITY INT)";
            stmt.executeUpdate(sqlStmt);
            stmt.close();

            sqlStmt = "CREATE TABLE IF NOT EXISTS discounts (" +
                    "    id TEXT," +
                    "    discount_value TINYINT CHECK (discount_value BETWEEN 1 AND 100)," +
                    "    max_uses INT CHECK (max_uses >= 0)," +
                    "    current_uses INT DEFAULT 0 CHECK (current_uses >= 0)," +
                    "    is_active BOOLEAN DEFAULT TRUE" +
                    ");";
            stmt.executeUpdate(sqlStmt);
            stmt.close();
            c.close();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void useDiscount(String discountId) {
        new Thread(() -> {
            boolean success = false;
            int retryCount = 0;
            int maxRetries = 3;
            int sleepTime = 2000; // 2 segundos

            while (!success && retryCount < maxRetries) {
                try (Connection c = connect();
                     PreparedStatement pStmt = c.prepareStatement("UPDATE discounts SET current_uses = current_uses + 1 WHERE id = ?")) {

                    pStmt.setString(1, discountId);
                    pStmt.executeUpdate();
                    System.out.println("Discount usage updated successfully.");
                    success = true; // Si se completa, marcamos como exitoso

                } catch (SQLException e) {
                    if (e.getErrorCode() == SQLiteErrorCode.SQLITE_BUSY.code) {
                        System.out.println("Database is busy, retrying in " + sleepTime + "ms...");
                        retryCount++;
                        try {
                            Thread.sleep(sleepTime); // Dormimos el hilo antes de reintentar
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        e.printStackTrace();
                        break;
                    }
                }
            }

            if (!success) {
                System.out.println("Failed to update discount after " + retryCount + " attempts.");
            }
        }).start();
    }





    public static void createDiscount(Discount discount) {
        Connection c = connect();
        PreparedStatement pStmt = null;
        try {
            // Preparamos la consulta SQL
            pStmt = c.prepareStatement(
                    "INSERT INTO discounts (id, discount_value, max_uses, current_uses, is_active) " +
                            "VALUES (?, ?, ?, ?, ?)"
            );

            // Establecemos los valores del descuento
            pStmt.setString(1, discount.getId());
            pStmt.setInt(2, discount.get());
            pStmt.setInt(3, discount.getUsageLimit());
            pStmt.setInt(4, discount.getCurrentUses());
            pStmt.setBoolean(5, discount.isActive());

            // Ejecutamos la inserción
            pStmt.executeUpdate();

            // Cerramos la conexión
            c.close();

            System.out.println("Discount created successfully.");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void removeDiscount(String id) {
        Connection c = connect();
        PreparedStatement pStmt = null;
        try {
            // Preparamos la consulta SQL
            pStmt = c.prepareStatement(
                    "DELETE FROM discounts WHERE id = ?"
            );

            // Establecemos el ID del descuento
            pStmt.setString(1, id);

            // Ejecutamos la actualización
            pStmt.executeUpdate();

            // Cerramos la conexión
            c.close();

            System.out.println("Discount removed successfully.");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static ArrayList<Discount> getDiscounts(){
        Connection c = connect();
        PreparedStatement pStmt = null;
        try {
            // Preparamos la consulta SQL
            pStmt = c.prepareStatement(
                    "SELECT * FROM discounts"
            );

            // Ejecutamos la consulta
            ResultSet rs = pStmt.executeQuery();

            // Si la consulta devuelve resultados
            ArrayList<Discount> discounts = new ArrayList<>();
            while (rs.next()){
                Discount discount = new Discount(rs.getString("id"), rs.getInt("discount_value"), rs.getInt("max_uses"), rs.getInt("current_uses"), rs.getBoolean("is_active"));
                discounts.add(discount);
            }

            rs.close();
            pStmt.close();
            c.close();

            return discounts;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    public static Discount getDiscount(String id) {
        Connection c = connect();
        PreparedStatement pStmt = null;
        try {
            // Preparamos la consulta SQL
            pStmt = c.prepareStatement(
                    "SELECT * FROM discounts WHERE id = ?"
            );

            // Establecemos el ID del descuento
            pStmt.setString(1, id);

            // Ejecutamos la consulta
            ResultSet rs = pStmt.executeQuery();

            // Si la consulta devuelve resultados
            if (rs.next()) {
                // Creamos un objeto Discount con los resultados
                Discount discount = new Discount(rs.getString("id"), rs.getInt("discount_value"), rs.getInt("max_uses"), rs.getInt("current_uses"), rs.getBoolean("is_active"));
                c.close();
                return discount;
            } else {
                // Si no hay resultados, devolvemos null
                c.close();
                return null;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void addItem(Item i) {
        Connection c = connect();
        PreparedStatement pStmt = null;
        try {
            pStmt = c.prepareStatement("INSERT INTO ITEMS VALUES(?, ?, ?, ?, ?)");

            pStmt.setString(1,i.getName());
            pStmt.setDouble(2,i.getPrice());
            pStmt.setString(3,i.getMsgId());
            pStmt.setString(4,i.getChannelId());
            pStmt.setInt(5, i.getQuantity());
            pStmt.executeUpdate();
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void delItem(String msgId) {
        Connection c = connect();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("DELETE from ITEMS where MSGID="+msgId);
            stmt.close();
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void registerItems() {
        Connection c = connect();
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ITEMS;");
            while (rs.next()){
                ItemManager.loadItem(new Item(rs.getString("NAME"),rs.getDouble("PRICE"),rs.getString("MSGID"),rs.getString("CHANID"), rs.getInt("QUANTITY")));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
