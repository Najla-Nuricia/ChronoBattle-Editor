
package game.config;


import game.contracts.interfaces.AttackStrategy;
import game.models.chars.Player;
import game.contracts.interfaces.Skill;
import game.contracts.interfaces.StatusEffect;
import static game.models.effects.effectFactory.createEffect;
import game.models.skills.SkillFactory;
import game.models.strategies.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HeroDAO {

    // ================= INSERT HERO =================
    public static int insertHero(Player p, int strategyId) {

    String sql = "INSERT INTO heroes (name, hp, attack, level, strategy_id) VALUES (?,?,?,?,?) RETURNING id";

    try (Connection c = DBHelper.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setString(1, p.getName());
        ps.setInt(2, p.getHealth());
        ps.setInt(3, p.getAttackPower());
        ps.setInt(4, p.getLevel());
        ps.setInt(5, strategyId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;

    }

    // ================ INSERT SKILL ==================
        public static int getSkillId(String skillName) {
            String sql = "SELECT id FROM skills WHERE name=?";

            try (Connection c = DBHelper.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, skillName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }
        
        public static void insertSkill(int heroId, int skillId) {
            String sql = "INSERT INTO hero_skills (hero_id, skill_id) VALUES (?,?)";

            try (Connection c = DBHelper.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, heroId);
                ps.setInt(2, skillId);
                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    // ================ INSERT EFFECT =================
        public static int getEffectId(String effectName) {
            String sql = "SELECT id FROM status_effects WHERE name=?";

            try (Connection c = DBHelper.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, effectName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        public static void insertEffect(int heroId, int effectId, StatusEffect fx) {
            String sql = "INSERT INTO hero_effects (hero_id, effect_id, value1, value2) VALUES (?,?,?,?)";

            try (Connection c = DBHelper.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, heroId);
                ps.setInt(2, effectId);
                ps.setInt(3, fx.getValue1());
                ps.setInt(4, fx.getValue2());
                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    // ===================== UPDATE ======================
        public static int getHeroIdByName(String name) {
            String sql = "SELECT id FROM heroes WHERE name=?";

            try (Connection c = DBHelper.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) return rs.getInt("id");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1; // not found
        }
        
        public static void updateHero(int heroId, Player p, int strategyId) {
        String sql = "UPDATE heroes SET name=?, hp=?, attack=?, level=?, strategy_id=? WHERE id=?";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setInt(2, p.getHealth());
            ps.setInt(3, p.getAttackPower());
            ps.setInt(4, p.getLevel());
            ps.setInt(5, strategyId);
            ps.setInt(6, heroId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== STRATEGY ID ======================
    public static int getStrategyId(String name) {
        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id FROM strategies WHERE name=?")) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    
    private static List<StatusEffect> loadHeroEffects(int heroId) {
        List<StatusEffect> list = new ArrayList<>();

        String sql = "SELECT se.name, he.value1, he.value2 " +
                     "FROM hero_effects he " +
                     "JOIN status_effects se ON he.effect_id = se.id " +
                     "WHERE he.hero_id = ?";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, heroId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int v1 = rs.getInt("value1");
                int v2 = rs.getInt("value2");

                list.add(createEffect(name, v1, v2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    
    private static List<String> loadHeroSkills(int heroId) {
        List<String> list = new ArrayList<>();

        String sql = "SELECT sk.name FROM hero_skills hs " +
                     "JOIN skills sk ON hs.skill_id = sk.id " +
                     "WHERE hs.hero_id = ?";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, heroId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
}

    
public static List<Player> loadAllHeroes() {
    Map<Integer, Player> heroMap = new LinkedHashMap<>();

    // Query untuk ambil hero + strategy + skill
    String sql = "SELECT h.id AS hero_id, h.name AS hero_name, h.hp, h.attack, h.level, " +
                 "s.name AS strategy_name, sk.name AS skill_name " +
                 "FROM heroes h " +
                 "JOIN strategies s ON h.strategy_id = s.id " +
                 "LEFT JOIN hero_skills hs ON hs.hero_id = h.id " +
                 "LEFT JOIN skills sk ON hs.skill_id = sk.id " +
                 "ORDER BY h.id";

    try (Connection c = DBHelper.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int heroId = rs.getInt("hero_id");

            Player p;
            if (!heroMap.containsKey(heroId)) {
                // Create Strategy
                AttackStrategy strategy = StrategyFactory.create(rs.getString("strategy_name"));

                // Create Player
                p = new Player(
                        rs.getString("hero_name"),
                        rs.getInt("hp"),
                        rs.getInt("attack"),
                        rs.getInt("level"),
                        strategy
                );

                heroMap.put(heroId, p);
            } else {
                p = heroMap.get(heroId);
            }

            // Tambahkan skill jika ada
            String skillName = rs.getString("skill_name");
            if (skillName != null) {
                Skill sk = SkillFactory.create(skillName, p);
                if (sk != null && !p.getSkills().contains(sk)) {
                    p.addSkill(sk);
                }
            }
        }

        // Load efek tiap hero
        for (Map.Entry<Integer, Player> entry : heroMap.entrySet()) {
            int heroId = entry.getKey();
            Player p = entry.getValue();
            List<StatusEffect> effects = loadHeroEffects(heroId);
            for (StatusEffect fx : effects) {
                p.addEffect(fx);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return new ArrayList<>(heroMap.values());
}

  


}

