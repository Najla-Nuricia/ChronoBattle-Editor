
package game.config;

import java.sql.*;

public class DBHelper {
    private static final String URL = "jdbc:postgresql://localhost:5432/db_game";
    private static final String USER = "postgres";
    private static final String PASS = "ROOT";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            throw new RuntimeException("DB Connection failed: " + e.getMessage());
        }
    }
    
     public static int createNewBattle(String hero, String enemy) {
        String sql = "INSERT INTO battle(hero_name, enemy_name) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, hero);
            ps.setString(2, enemy);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // simpan log per battle
    public static void insertBattleLog(int battleId, String msg) {
        String sql = "INSERT INTO battle_log(battle_id, message) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, battleId);
            ps.setString(2, msg);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}

