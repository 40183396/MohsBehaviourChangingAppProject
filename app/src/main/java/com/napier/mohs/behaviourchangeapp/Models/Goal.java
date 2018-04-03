package com.napier.mohs.behaviourchangeapp.Models;

/**
 * Created by Mohs on 03/04/2018.
 */

public class Goal {
    private String goal_id, goal_name, goal_weight, current_weight;

    public Goal() {
    }

    public Goal(String goal_id, String goal_name, String goal_weight, String current_weight) {
        this.goal_id = goal_id;
        this.goal_name = goal_name;
        this.goal_weight = goal_weight;
        this.current_weight = current_weight;
    }

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public String getGoal_name() {
        return goal_name;
    }

    public void setGoal_name(String goal_name) {
        this.goal_name = goal_name;
    }

    public String getGoal_weight() {
        return goal_weight;
    }

    public void setGoal_weight(String goal_weight) {
        this.goal_weight = goal_weight;
    }

    public String getCurrent_weight() {
        return current_weight;
    }

    public void setCurrent_weight(String current_weight) {
        this.current_weight = current_weight;
    }

    @Override
    public String toString() {
        return "Goal{" +
                "goal_id='" + goal_id + '\'' +
                ", goal_name='" + goal_name + '\'' +
                ", goal_weight='" + goal_weight + '\'' +
                ", current_weight='" + current_weight + '\'' +
                '}';
    }
}
