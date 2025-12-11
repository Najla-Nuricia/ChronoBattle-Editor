package game.config;

import game.contracts.interfaces.StatusEffect;
import game.contracts.interfaces.AttackStrategy;
import game.contracts.abstracts.Enemy;
import game.models.chars.Monster;
import game.models.chars.BossMonster;
import game.models.strategies.StrategyFactory;

import java.sql.*;
import java.util.*;

import static game.models.effects.effectFactory.createEffect;

public class EnemyDAO {

    // =========== INSERT ENEMY ===========
    public static int insertEnemy(Enemy e, int strategyId) {
        String sql =
                "INSERT INTO enemies (name, hp, attack, threat, is_boss, strategy_id) " +
                "VALUES (?,?,?,?,?,?) RETURNING id";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, e.getName());
            ps.setInt(2, e.getHealth());
            ps.setInt(3, e.getAttackPower());
            ps.setInt(4, e.getThreatLevel());
            ps.setBoolean(5, e instanceof BossMonster);
            ps.setInt(6, strategyId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    // =========== INSERT EFFECT ===========
    public static int getEffectId(String name) {
        String sql = "SELECT id FROM status_effects WHERE name=?";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("id");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void insertEffect(int enemyId, int effectId, StatusEffect fx) {
        String sql = "INSERT INTO enemy_effects (enemy_id, effect_id, value1, value2) VALUES (?,?,?,?)";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, enemyId);
            ps.setInt(2, effectId);
            ps.setInt(3, fx.getValue1());
            ps.setInt(4, fx.getValue2());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========== UPDATE ===========
    
    public static int getEnemyIdByName(String name) {
        String sql = "SELECT id FROM enemies WHERE name=?";

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

    public static void updateEnemy(int id, Enemy e, int strategyId) {

        String sql =
                "UPDATE enemies SET name=?, hp=?, attack=?, threat=?, is_boss=?, strategy_id=? WHERE id=?";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, e.getName());
            ps.setInt(2, e.getHealth());
            ps.setInt(3, e.getAttackPower());
            ps.setInt(4, e.getThreatLevel());
            ps.setBoolean(5, e instanceof BossMonster);
            ps.setInt(6, strategyId);
            ps.setInt(7, id);

            ps.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // =========== STRATEGY ID ===========
    public static int getStrategyId(String name) {
        String sql = "SELECT id FROM strategies WHERE name=?";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    // =========== LOAD EFFECTS FOR ENEMY ===========
    private static List<StatusEffect> loadEnemyEffects(int enemyId) {

        List<StatusEffect> effects = new ArrayList<>();

        String sql =
                "SELECT se.name, ee.value1, ee.value2 " +
                "FROM enemy_effects ee " +
                "JOIN status_effects se ON se.id = ee.effect_id " +
                "WHERE ee.enemy_id=?";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, enemyId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String name = rs.getString("name");
                int v1 = rs.getInt("value1");
                int v2 = rs.getInt("value2");

                effects.add(createEffect(name, v1, v2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return effects;
    }

    // =========== LOAD ALL ENEMIES ===========
    public static List<Enemy> loadAllEnemies() {

        Map<Integer, Enemy> map = new LinkedHashMap<>();

        String sql =
                "SELECT e.id AS id, e.name, e.hp, e.attack, e.threat, e.is_boss, " +
                "s.name AS strategy_name " +
                "FROM enemies e " +
                "JOIN strategies s ON s.id = e.strategy_id " +
                "ORDER BY e.id";

        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("name");

                int hp = rs.getInt("hp");
                int atk = rs.getInt("attack");
                int threat = rs.getInt("threat");
                boolean isBoss = rs.getBoolean("is_boss");

                String stratName = rs.getString("strategy_name");
                AttackStrategy strat = StrategyFactory.create(stratName);

                Enemy e = isBoss ?
                        new BossMonster(name, hp, atk, threat, strat) :
                        new Monster(name, hp, atk, threat, strat);

                map.put(id, e);
            }

            // load effects
            for (var entry : map.entrySet()) {
                int id = entry.getKey();
                Enemy e = entry.getValue();
                List<StatusEffect> fx = loadEnemyEffects(id);
                fx.forEach(e::addEffect);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(map.values());
    }

}
