package game.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BattleDAO {

    public static int insertBattle(String hero, String enemy) {
        String sql = "INSERT INTO battle (hero_name, enemy_name, started_at) " +
                     "VALUES (?, ?, NOW()) RETURNING battle_id";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, hero);
            ps.setString(2, enemy);

            // HANYA INI!! (executeQuery)
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("battle_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
