package com.napier.mohs.instagramclone.Models;

/**
 * Created by Mohs on 23/03/2018.
 */

public class Exercise {
    private String exercise_id, exercise_name, unit;

    public Exercise(String exercise_id, String exercise_name, String unit) {
        this.exercise_id = exercise_id;
        this.exercise_name = exercise_name;
        this.unit = unit;
    }

    public Exercise() {
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exercise_id='" + exercise_id + '\'' +
                ", exercise_name='" + exercise_name + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
