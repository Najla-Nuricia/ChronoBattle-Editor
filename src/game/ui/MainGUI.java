package game.ui;

import game.contracts.abstracts.Character;
import game.contracts.abstracts.Enemy;
import game.contracts.interfaces.AttackStrategy;
import game.contracts.interfaces.Skill;
import game.models.chars.BossMonster;
import game.models.chars.Monster;
import game.models.chars.Player;
import game.models.skills.SkillFactory;
import game.models.strategies.*;
import game.simulation.Battle;
import game.models.effects.Burn;
import game.models.effects.Regen;
import game.models.effects.Shield;
import game.contracts.interfaces.StatusEffect;
import game.config. *;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MainGUI extends JFrame {

    private final List<Player> players = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();

    private final DefaultTableModel heroTableModel =
            new DefaultTableModel(new Object[]{"Name","HP","ATK","Level","Skill","Strategy"},0);

    private final DefaultTableModel enemyTableModel =
            new DefaultTableModel(new Object[]{"Name","HP","ATK","Boss","Threat","Strategy"},0);

    private final JTextArea battleLog = new JTextArea(20, 60);

    private final String[] SKILL_NAMES = {"FireStrike", "Heal", "PiercingStrike"};
    private final String[] STRATEGY_NAMES = {"CriticalHitStrategy", "FixedStrategy", "LevelScaledStrategy"};

    public MainGUI() {
        super("Game Editor & Battle Simulator (integrated)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 720);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Heroes", createHeroesPanel());
        tabs.addTab("Enemies/Boss", createEnemiesPanel());
        tabs.addTab("Battle Simulation", createBattlePanel());

        add(tabs);
    }

    // =====================================================================
    // =========================== HERO PANEL ===============================
    // =====================================================================
    private JPanel createHeroesPanel() {
    JPanel p = new JPanel(new BorderLayout());
    JTable table = new JTable(heroTableModel);
    p.add(new JScrollPane(table), BorderLayout.CENTER);

    JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JTextField name = new JTextField(10);
    JTextField hp = new JTextField(5);
    JTextField atk = new JTextField(5);
    JTextField levelField = new JTextField(5);

    JComboBox<String> skillCombo = new JComboBox<>(SKILL_NAMES);
    JComboBox<String> strategyCombo = new JComboBox<>(STRATEGY_NAMES);

    // === EFFECT ===
    String[] EFFECTS = {"None", "Burn", "Regen", "Shield"};
    JComboBox<String> effectCombo = new JComboBox<>(EFFECTS);
    JTextField effectVal1 = new JTextField(4); // dmg / heal / reduce
    JTextField effectVal2 = new JTextField(4); // duration
    JButton addEffectBtn = new JButton("Add Effect");

    // current selected hero effect list
    List<StatusEffect> pendingEffects = new ArrayList<>();

    addEffectBtn.addActionListener(e -> {
        try {
            String eff = (String) effectCombo.getSelectedItem();
            int v1 = Integer.parseInt(effectVal1.getText().trim());
            int v2 = Integer.parseInt(effectVal2.getText().trim());

            StatusEffect fx = null;

            switch (eff) {
                case "Burn": fx = new Burn(v1, v2); break;
                case "Regen": fx = new Regen(v1, v2); break;
                case "Shield": fx = new Shield(v1, v2); break;
            }

            if (fx != null) {
                pendingEffects.add(fx);
                System.out.println("DEBUG add effect: " + fx.getClass().getSimpleName());
                System.out.println("DEBUG pendingEffects size = " + pendingEffects.size());

                JOptionPane.showMessageDialog(this, "Effect added: " + fx.description());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid effect: " + ex.getMessage());
        }
    });
    
    //load hero button
    JButton loadBtn = new JButton("Load Hero");
    loadBtn.addActionListener(e -> {
        List<Player> list = HeroDAO.loadAllHeroes();

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No heroes found.");
            return;
        }

     heroTableModel.setRowCount(0);
    

        for (Player pl : list) {
            
            String skillText = "";
        for (Skill sk : pl.getSkills()) {
            skillText += sk.name() + ", ";
        }
        if (!skillText.isEmpty()) {
            skillText = skillText.substring(0, skillText.length() - 2); 
        }
            heroTableModel.addRow(new Object[]{
                    pl.getName(),
                    pl.getHealth(),
                    pl.getAttackPower(),
                    pl.getLevel(),
                    skillText,
                    pl.getStrategy().getClass().getSimpleName()
            });
        }

    });
    
    form.add(loadBtn); 


    JButton addBtn = new JButton("Add Hero");
    addBtn.addActionListener(e -> {
        try {
            String n = name.getText().trim();
            int h = Integer.parseInt(hp.getText().trim());
            int a = Integer.parseInt(atk.getText().trim());
            int lvl = Integer.parseInt(levelField.getText().trim());

            String skillName = (String) skillCombo.getSelectedItem();
            String strategyName = (String) strategyCombo.getSelectedItem();
            AttackStrategy strat = StrategyFactory.create(strategyName);

            Player player = new Player(n, h, a, lvl, strat);

            // add skill
            Skill sk = SkillFactory.create(skillName, player);
            if (sk != null) player.addSkill(sk);

            // add pending effects
            for (StatusEffect fx : pendingEffects)
                player.addEffect(fx);

            players.add(player);
            
            // === INSERT TO DB ===
                int strategyId = HeroDAO.getStrategyId(strategyName);
                int heroId = HeroDAO.insertHero(player, strategyId);

                // insert skill
                int skillId = HeroDAO.getSkillId(skillName);
                HeroDAO.insertSkill(heroId, skillId);

                // insert effects
                for (StatusEffect fx : pendingEffects) {
                String effectName = fx.getClass().getSimpleName();
                int effId = HeroDAO.getEffectId(effectName);
                System.out.println("DEBUG effectId = " + effId);

                HeroDAO.insertEffect(heroId, effId, fx);
            }



            heroTableModel.addRow(new Object[]{
                    n, h, a, lvl,
                    sk != null ? sk.name() : "(none)",
                    strat != null ? strat.getClass().getSimpleName() : "(none)"
            });

            name.setText(""); hp.setText(""); atk.setText(""); levelField.setText("");
            effectVal1.setText(""); effectVal2.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    });

    JButton editBtn = new JButton("Edit Selected");
    editBtn.addActionListener(e -> {
    int r = table.getSelectedRow();
    if (r < 0) return;

    try {
        Player old = players.get(r);

        String newName = JOptionPane.showInputDialog(this, "Name:", old.getName());
        if (newName == null) return;

        int newHp = Integer.parseInt(JOptionPane.showInputDialog(this, "Max HP:", old.getHealth()));
        int newAtk = Integer.parseInt(JOptionPane.showInputDialog(this, "Attack:", old.getAttackPower()));
        int newLevel = Integer.parseInt(JOptionPane.showInputDialog(this, "Level:", old.getLevel()));
       
        String[] strategies = {
        "CriticalHitStrategy",
        "LevelScaledStrategy",
        "FixedStrategy"
        };

        String chosen = (String) JOptionPane.showInputDialog(
                this,
                "Choose Attack Strategy:",
                "Edit Strategy",
                JOptionPane.PLAIN_MESSAGE,
                null,
                strategies,
                strategies[0]
        );

        AttackStrategy newStrategy = null;

        if (chosen != null) {
            switch (chosen) {
                case "CriticalHitStrategy":
                    newStrategy = new CriticalHitStrategy();
                    break;

                case "LevelScaledStrategy":
                    newStrategy = new LevelScaledStrategy(newLevel);
                    break;

                case "FixedStrategy":
                    newStrategy = new FixedStrategy();
                    break;
            }
        } else {
            // fallback, jika user cancel
            newStrategy = old.getStrategy();
        }
            //add new player
        Player newP = new Player(newName, newHp, newAtk, newLevel, newStrategy);
        
        String[] skills = {
        "FireStrike",
        "Heal",
        "PiercingStrike"
        };

        String chosenSkill = (String) JOptionPane.showInputDialog(
                this,
                "Choose Skill:",
                "Edit Skill",
                JOptionPane.PLAIN_MESSAGE,
                null,
                skills,
                skills[0]
        );

        Skill newSkill = null;

        if (chosenSkill != null) {
            newSkill = SkillFactory.create(chosenSkill, newP);
        }

        // assign skill baru 
        try {
            newP.getClass().getMethod("addSkill", Skill.class).invoke(newP, newSkill);
        } catch (Exception ignore) {}


        players.set(r, newP);

        heroTableModel.setValueAt(newName, r, 0);
        heroTableModel.setValueAt(newHp,   r, 1);
        heroTableModel.setValueAt(newAtk,  r, 2);
        heroTableModel.setValueAt(newLevel,r, 3);
        heroTableModel.setValueAt(newSkill != null ? newSkill.name() : "(none)", r, 4);
        heroTableModel.setValueAt(newStrategy.getClass().getSimpleName(), r, 5);
        
        // UPDATE DB
        
        int heroId = HeroDAO.getHeroIdByName(old.getName());
        int newStrategyId = HeroDAO.getStrategyId(newStrategy.getClass().getSimpleName());
        HeroDAO.updateHero(heroId, newP, newStrategyId);


    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Edit failed: " + ex.getMessage());
    }
    

});


    form.add(new JLabel("Name:")); form.add(name);
    form.add(new JLabel("HP:")); form.add(hp);
    form.add(new JLabel("ATK:")); form.add(atk);
    form.add(new JLabel("Level:")); form.add(levelField);
    form.add(new JLabel("Skill:")); form.add(skillCombo);
    form.add(new JLabel("Strategy:")); form.add(strategyCombo);

    // === EFFECT UI ===
    form.add(new JLabel("Effect:")); 
    form.add(effectCombo);
    form.add(new JLabel("V1:")); form.add(effectVal1);
    form.add(new JLabel("V2:")); form.add(effectVal2);
    form.add(addEffectBtn);

    form.add(addBtn);
    form.add(editBtn);
    
    
    JScrollPane formScroll = new JScrollPane(form);
    formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    formScroll.setPreferredSize(new Dimension(0, 50)); // tinggi total yg muncul
    p.add(formScroll, BorderLayout.SOUTH);

    return p;
}


    // =====================================================================
    // ======================= ENEMY PANEL (NO SKILL) ======================
    // =====================================================================
   private JPanel createEnemiesPanel() {

    JPanel p = new JPanel(new BorderLayout());
    JTable table = new JTable(enemyTableModel);
    p.add(new JScrollPane(table), BorderLayout.CENTER);

    JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JTextField name = new JTextField(10);
    JTextField hp = new JTextField(5);
    JTextField atk = new JTextField(5);
    JTextField threatField = new JTextField(5);

    JCheckBox isBossBox = new JCheckBox("Boss");

    JComboBox<String> strategyCombo = new JComboBox<>(STRATEGY_NAMES);

    // === EFFECTS ===
    String[] EFFECTS = {"None", "Burn", "Regen", "Shield"};
    JComboBox<String> effectCombo = new JComboBox<>(EFFECTS);
    JTextField effectVal1 = new JTextField(4);
    JTextField effectVal2 = new JTextField(4);
    JButton addEffectBtn = new JButton("Add Effect");

    List<StatusEffect> pendingEffects = new ArrayList<>();

    addEffectBtn.addActionListener(e -> {
        try {
            String eff = (String) effectCombo.getSelectedItem();
            int v1 = Integer.parseInt(effectVal1.getText().trim());
            int v2 = Integer.parseInt(effectVal2.getText().trim());

            StatusEffect fx = null;

            switch (eff) {
                case "Burn": fx = new Burn(v1, v2); break;
                case "Regen": fx = new Regen(v1, v2); break;
                case "Shield": fx = new Shield(v1, v2); break;
            }

            if (fx != null) {
                pendingEffects.add(fx);
                JOptionPane.showMessageDialog(this, "Effect added: " + fx.description());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid effect: " + ex.getMessage());
        }
    });
    
    // === LOAD ENEMY BUTTON ===
    JButton loadEnemyBtn = new JButton("Load Enemies");
    loadEnemyBtn.addActionListener(e -> {

        List<Enemy> list = EnemyDAO.loadAllEnemies();

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No enemies found.");
            return;
        }

        enemyTableModel.setRowCount(0); // clear table

        for (Enemy en : list) {

            // build effects text
            String fxText = "";
            for (StatusEffect fx : en.getEffects()) {
                fxText += fx.getClass().getSimpleName() + ", ";
            }
            if (!fxText.isEmpty()) {
                fxText = fxText.substring(0, fxText.length() - 2);
            }

            enemyTableModel.addRow(new Object[]{
                    en.getName(),
                    en.getHealth(),
                    en.getAttackPower(),
                    (en instanceof BossMonster),
                    en.getThreatLevel(),
                    en.getStrategy().getClass().getSimpleName(),
                    fxText
            });
        }

    });
    form.add(loadEnemyBtn);

    
    JButton addBtn = new JButton("Add Enemy");
    addBtn.addActionListener(e -> {
        try {
            String n = name.getText().trim();
            int h = Integer.parseInt(hp.getText().trim());
            int a = Integer.parseInt(atk.getText().trim());
            int threat = Integer.parseInt(threatField.getText().trim());

            boolean isBoss = isBossBox.isSelected();
            String strategyName = (String) strategyCombo.getSelectedItem();
            AttackStrategy strat = StrategyFactory.create(strategyName);

            Enemy enemy = isBoss ?
                    new BossMonster(n, h, a, threat, strat) :
                    new Monster(n, h, a, threat, strat);


            for (StatusEffect fx : pendingEffects)
                enemy.addEffect(fx);

            // 1) Strategy → get ID
            int strategyId = EnemyDAO.getStrategyId(strategyName);

            // 2) Enemy → insert → return enemy_id
            int enemyId = EnemyDAO.insertEnemy(enemy, strategyId);

            // 3) Insert all status effects
            for (StatusEffect fx : pendingEffects) {
                String effectName = fx.getClass().getSimpleName();
                int effectId = EnemyDAO.getEffectId(effectName);

                System.out.println("DEBUG: effectId = " + effectId);

                if (effectId != -1) {
                    EnemyDAO.insertEffect(enemyId, effectId, fx);
                }
            }

            // clear pending effect list
            pendingEffects.clear();

            enemies.add(enemy);

            enemyTableModel.addRow(new Object[]{
                    n, h, a, isBoss, threat,
                    strat != null ? strat.getClass().getSimpleName() : "(none)"
            });

            name.setText("");
            hp.setText("");
            atk.setText("");
            threatField.setText("");
            isBossBox.setSelected(false);
            effectVal1.setText("");
            effectVal2.setText("");


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    });


    JButton editBtn = new JButton("Edit Selected");
    editBtn.addActionListener(e -> {
        int r = table.getSelectedRow();
        if (r < 0) return;

        try {
            Enemy old = enemies.get(r);

            // === BASIC INFO ===
            String newName = JOptionPane.showInputDialog(this, "Name:", old.getName());
            if (newName == null) return;

            int newHp = Integer.parseInt(
                    JOptionPane.showInputDialog(this, "Max HP:", old.getHealth())
            );
            int newAtk = Integer.parseInt(
                    JOptionPane.showInputDialog(this, "Attack:", old.getAttackPower())
            );
            int newThreat = Integer.parseInt(
                    JOptionPane.showInputDialog(this, "Threat (1–5):", old.getThreatLevel())
            );

            boolean isBoss = JOptionPane.showConfirmDialog(
                    this,
                    "Is Boss?",
                    "Boss Status",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION;


            // === STRATEGY EDIT ===
            String[] strategies = {
                    "CriticalHitStrategy",
                    "LevelScaledStrategy",
                    "FixedStrategy"
            };

            String chosen = (String) JOptionPane.showInputDialog(
                    this,
                    "Choose Attack Strategy:",
                    "Edit Strategy",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    strategies,
                    strategies[0]
            );

            AttackStrategy newStrategy = null;

            if (chosen != null) {
                switch (chosen) {
                    case "CriticalHitStrategy":
                        newStrategy = new CriticalHitStrategy();
                        break;

                    case "LevelScaledStrategy":
                        newStrategy = new LevelScaledStrategy(1); // Enemy tidak punya level
                        break;

                    case "FixedStrategy":
                        newStrategy = new FixedStrategy();
                        break;
                }
            } else {
                newStrategy = old.getStrategy();
            }

            // === REBUILD ENEMY BARU ===
            Enemy newEnemy;

            if (isBoss) {
                newEnemy = new BossMonster(newName, newHp, newAtk, newThreat, newStrategy);
            } else {
                newEnemy = new Monster(newName, newHp, newAtk, newThreat, newStrategy);
            }

            enemies.set(r, newEnemy);

            // === UPDATE TABLE ===
            enemyTableModel.setValueAt(newName, r, 0);
            enemyTableModel.setValueAt(newHp, r, 1);
            enemyTableModel.setValueAt(newAtk, r, 2);
            enemyTableModel.setValueAt(isBoss, r, 3);
            enemyTableModel.setValueAt(newThreat, r, 4);
            enemyTableModel.setValueAt(newStrategy.getClass().getSimpleName(), r, 5);
            
            int enemyId = EnemyDAO.getEnemyIdByName(old.getName());
            int newStrategyId = EnemyDAO.getStrategyId(newStrategy.getClass().getSimpleName());

            // newE = enemy baru hasil edit (Monster atau BossMonster)
            EnemyDAO.updateEnemy(enemyId, newEnemy, newStrategyId);


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Edit failed: " + ex.getMessage());
        }
    });


    form.add(new JLabel("Name:")); form.add(name);
    form.add(new JLabel("HP:")); form.add(hp);
    form.add(new JLabel("ATK:")); form.add(atk);
    form.add(new JLabel("Threat:")); form.add(threatField);
    form.add(isBossBox);

    form.add(new JLabel("Strategy:")); form.add(strategyCombo);

    // === EFFECT UI ===
    form.add(new JLabel("Effect:")); 
    form.add(effectCombo);
    form.add(new JLabel("V1:")); form.add(effectVal1);
    form.add(new JLabel("V2:")); form.add(effectVal2);
    form.add(addEffectBtn);

    form.add(addBtn);
    form.add(editBtn);
    
    JScrollPane formScroll = new JScrollPane(form);
    formScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    formScroll.setPreferredSize(new Dimension(0, 50)); // tinggi total yg muncul
    p.add(formScroll, BorderLayout.SOUTH);
    return p;
}


    // =====================================================================
    // ========================== BATTLE PANEL =============================
    // =====================================================================
    private JPanel createBattlePanel() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> heroSel = new JComboBox<>();
        JComboBox<String> enemySel = new JComboBox<>();
        JButton simulate = new JButton("Simulate Battle");
        JButton refresh = new JButton("Refresh Lists");

        // === REFRESH LISTS FROM DATABASE ===
        refresh.addActionListener(e -> {
            heroSel.removeAllItems();
            enemySel.removeAllItems();

            // load ALL heroes from DB
            List<Player> heroList = HeroDAO.loadAllHeroes();
            for (Player pl : heroList) heroSel.addItem(pl.getName());

            // load ALL enemies from DB
            List<Enemy> enemyList = EnemyDAO.loadAllEnemies();
            for (Enemy en : enemyList) enemySel.addItem(en.getName());
        });

        // === SIMULATE BATTLE ===
        simulate.addActionListener(e -> {
        String heroName = (String) heroSel.getSelectedItem();
        String enemyName = (String) enemySel.getSelectedItem();

        if (heroName == null || enemyName == null) {
            JOptionPane.showMessageDialog(this, "Select hero and enemy first!");
            return;
        }
        
        List<Player> heroList = HeroDAO.loadAllHeroes();
        List<Enemy> enemyList = EnemyDAO.loadAllEnemies();

        Player selectedHero = heroList.stream()
                   .filter(pl -> pl.getName().equals(heroName))
                   .findFirst().orElse(null);

        Enemy selectedEnemy = enemyList.stream()
                   .filter(en -> en.getName().equals(enemyName))
                   .findFirst().orElse(null);

        if (selectedHero == null || selectedEnemy == null) {
            JOptionPane.showMessageDialog(this, "Failed to load fighter data.");
            return;
        }

        List<Character> teamA = List.of(selectedHero);
        List<Character> teamB = List.of(selectedEnemy);
        
            int battleId = BattleDAO.insertBattle(heroName, enemyName);
            if (battleId == -1) {
                JOptionPane.showMessageDialog(this, "Failed to create battle record!");
                return;
            }

        PrintStream oldOut = System.out;
        TextAreaOutputStream taos = new TextAreaOutputStream(battleLog);
        PrintStream ps = new PrintStream(taos, true);
        System.setOut(ps);

        new Thread(() -> {
            try {
                Battle battle = new Battle(teamA, teamB);
                battle.run();

                // === 2. Setelah selesai, save seluruh log ===
                String fullLog = taos.getFullLog();
                BattleLogDAO.insertBattleLog(battleId, fullLog);

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> battleLog.append("Battle error: " + ex.getMessage() + "\n"));
            } finally {
                System.setOut(oldOut);
            }
        }).start();
    });

        top.add(new JLabel("Hero:")); top.add(heroSel);
        top.add(new JLabel("Enemy:")); top.add(enemySel);
        top.add(simulate);
        top.add(refresh);

        battleLog.setEditable(false);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(battleLog), BorderLayout.CENTER);
        return p;
    }


    // =====================================================================
    // ======================== TEXTAREA OUTPUT ============================
    // =====================================================================
    static class TextAreaOutputStream extends OutputStream {
    private final JTextArea textArea;
    private final StringBuilder buffer = new StringBuilder();

    TextAreaOutputStream(JTextArea ta) {
            this.textArea = ta;
        }

        @Override
        public void write(int b) {
            buffer.append((char) b);
            SwingUtilities.invokeLater(() -> textArea.append(String.valueOf((char) b)));
        }

        @Override
        public void write(byte[] b, int off, int len) {
            String s = new String(b, off, len);
            buffer.append(s);
            SwingUtilities.invokeLater(() -> textArea.append(s));
        }

        public String getFullLog() {
            return buffer.toString();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
        
    }
}
