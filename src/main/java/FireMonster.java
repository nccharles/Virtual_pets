import org.sql2o.Connection;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FireMonster extends Monster {
    private int fireLevel;
    public static final int MAX_FIRE_LEVEL = 10;
    public static final String DATABASE_TYPE = "fire";
    public Timestamp lastKindling;
    public FireMonster(String name, int personId) {
        this.name = name;
        this.personId = personId;
        playLevel = MAX_PLAY_LEVEL / 2;
        sleepLevel=MAX_SLEEP_LEVEL/2;
        foodLevel = MAX_FOOD_LEVEL / 2;
        fireLevel = MAX_FIRE_LEVEL / 2;
        timer = new Timer();
        type = DATABASE_TYPE;
    }
    public void startTimer(){
        FireMonster currentMonster = this;
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
    public int getFireLevel(){
        return fireLevel;
    }
    public void kindling(){
        if (fireLevel >= MAX_PLAY_LEVEL){
            throw new UnsupportedOperationException("You cannot give any more kindling!");
        }
        try(Connection con = DB.sql2o.open()) {
            String sql = "UPDATE monsters SET lastkindling = now() WHERE id = :id";
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        fireLevel++;
    }
    public Timestamp getLastKindling(){
        return lastKindling;
    }
    public boolean isAlive() {
        if (foodLevel <= MIN_ALL_LEVELS ||
                playLevel <= MIN_ALL_LEVELS ||
                fireLevel <= MIN_ALL_LEVELS ||
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
            fireLevel--;
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

    public static FireMonster find(int id) {
        try(Connection con = DB.sql2o.open()) {
            String sql = "SELECT * FROM monsters where id=:id";
            FireMonster monster = con.createQuery(sql)
                    .addParameter("id", id)
                    .throwOnMappingFailure(false)
                    .executeAndFetchFirst(FireMonster.class);
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
    public static List<FireMonster> all() {
        String sql = "SELECT * FROM monsters WHERE type='fire';";
        try(Connection con = DB.sql2o.open()) {
            return con.createQuery(sql)
                    .throwOnMappingFailure(false)
                    .executeAndFetch(FireMonster.class);
        }
    }
    @Override
    public boolean equals(Object otherMonster){
        if (!(otherMonster instanceof FireMonster)) {
            return false;
        } else {
            FireMonster newMonster = (FireMonster) otherMonster;
            return this.getName().equals(newMonster.getName()) &&
                    this.getPersonId() == newMonster.getPersonId();
        }
    }
}