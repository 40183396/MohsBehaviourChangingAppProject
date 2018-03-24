package com.napier.mohs.instagramclone.Models;

/**
 * Created by Mohs on 23/03/2018.
 */

public class Exercise {
    private String exercise_id, exercise_name, exercise_weight, exercise_reps;

    public Exercise() {
    }

    public Exercise(String exercise_id, String exercise_name, String exercise_weight, String exercise_reps) {
        this.exercise_id = exercise_id;
        this.exercise_name = exercise_name;
        this.exercise_weight = exercise_weight;
        this.exercise_reps = exercise_reps;
    }

    public String getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(String exercise_id) {
        this.exercise_id = exercise_id;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String getExercise_weight() {
        return exercise_weight;
    }

    public void setExercise_weight(String exercise_weight) {
        this.exercise_weight = exercise_weight;
    }

    public String getExercise_reps() {
        return exercise_reps;
    }

    public void setExercise_reps(String exercise_reps) {
        this.exercise_reps = exercise_reps;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exercise_id='" + exercise_id + '\'' +
                ", exercise_name='" + exercise_name + '\'' +
                ", exercise_weight='" + exercise_weight + '\'' +
                ", exercise_reps='" + exercise_reps + '\'' +
                '}';
    }
}
