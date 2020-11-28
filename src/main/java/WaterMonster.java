import org.sql2o.Connection;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WaterMonster extends Monster {
    private int waterLevel;
    public Timestamp lastWater;
    public static final int MAX_WATER_LEVEL = 8;
    public static final String DATABASE_TYPE = "water";
    public WaterMonster(String name, int personId) {
        this.name = name;
        this.personId = personId;
        playLevel = MAX_PLAY_LEVEL / 2;
        sleepLevel=MAX_SLEEP_LEVEL/2;
        foodLevel = MAX_FOOD_LEVEL / 2;
        waterLevel = MAX_WATER_LEVEL / 2;
        timer = new Timer();
        type = DATABASE_TYPE;
    }
    public void startTimer(){
        WaterMonster currentMonster = this;
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                if (!currentMonster.isAlive()){
                    cancel();
                }
                depleteLevels();
            }
        };
        this.timer.schedule(timerTask, 0, 600);
    }
    public int getPlayLevel(){
        return playLevel;
    }
    public int getSleepLevel(){
        return sleepLevel;
    }
    public int getFoodLevel(){
        return foodLevel;
    }
    public int getWaterLevel(){
       return waterLevel++;
    }
    public void water(){
        if (waterLevel >= MAX_WATER_LEVEL){
            throw new UnsupportedOperationException("You cannot water your pet any more!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastwater = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        waterLevel++;
    }
    public boolean isAlive() {
        if (foodLevel <= MIN_ALL_LEVELS ||
                playLevel <= MIN_ALL_LEVELS ||
                waterLevel <= MIN_ALL_LEVELS ||
                sleepLevel <= MIN_ALL_LEVELS) {
            return false;
        }
        return true;
    }
    public void depleteLevels(){
        if (isAlive()){
            playLevel--;
            foodLevel--;
            sleepLevel--;
            waterLevel--;
        }
    }
    public void play(){
        if (playLevel >= MAX_PLAY_LEVEL){
            throw new UnsupportedOperationException("You cannot play with monster anymore!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastplayed = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        playLevel++;
    }

    public void sleep(){
        if (sleepLevel >= MAX_SLEEP_LEVEL){
            throw new UnsupportedOperationException("You cannot make your monster sleep anymore!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastslept = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        sleepLevel++;
    }

    public void feed(){
        if (foodLevel >= MAX_FOOD_LEVEL){
            throw new UnsupportedOperationException("You cannot feed your monster anymore!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastate = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        foodLevel++;
    }
    public String getName(){
        return name;
    }
    public int getPersonId(){
        return personId;
    }
    public int getId(){
        return id;
    }
    public static List<WaterMonster> all() {
        String sql = "SELECT * FROM monsters WHERE type='water';";
        try(Connection con = DB.sql2o.open()) {
            return con.createQuery(sql)
                    .throwOnMappingFailure(false)
                    .executeAndFetch(WaterMonster.class);
        }
    }
    public static WaterMonster find(int id) {
        try(Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM monsters where id=:id";
            WaterMonster monster = con.createQuery(sql)
                    .addParameter("id", id)
                    .throwOnMappingFailure(false)
                    .executeAndFetchFirst(WaterMonster.class);
            return monster;
        }
    }
    public Timestamp getBirthday(){
        return birthday;
    }
    public Timestamp getLastAte(){
        return lastAte;
    }
    public Timestamp getLastPlayed(){
        return lastPlayed;
    }
    public Timestamp getLastSlept(){
        return lastSlept;
    }
    public Timestamp getLastWater(){
        return lastWater;
    }
    @Override
    public boolean equals(Object otherMonster){
        if (!(otherMonster instanceof WaterMonster)) {
            return false;
        } else {
            WaterMonster newMonster = (WaterMonster) otherMonster;
            return this.getName().equals(newMonster.getName()) &&
                    this.getPersonId() == newMonster.getPersonId();
        }
    }
}