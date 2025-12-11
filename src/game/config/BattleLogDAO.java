package game.config;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class BattleLogDAO {

    public static void insertBattleLog(int battleId, String message) {
        String sql = "INSERT INTO battle_log(battle_id, message, created_at) VALUES(?, ?, NOW())";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, battleId);
            ps.setString(2, message);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}

