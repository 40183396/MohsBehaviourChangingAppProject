package com.napier.mohs.instagramclone.Models;

/**
 * Created by Mohs on 23/03/2018.
 */

public class Workout {
    private String workout_id, user_id, date_created;
    private Exercise exercise;
    long sequence, num_sets, num_reps;

    public Workout(String workout_id, String user_id, String date_created, Exercise exercise, long sequence, long num_sets, long num_reps) {
        this.workout_id = workout_id;
        this.user_id = user_id;
        this.date_created = date_created;
        this.exercise = exercise;
        this.sequence = sequence;
        this.num_sets = num_sets;
        this.num_reps = num_reps;
    }

    public Workout() {
    }

    public String getWorkout_id() {
        return workout_id;
    }

    public void setWorkout_id(String workout_id) {
        this.workout_id = workout_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getNum_sets() {
        return num_sets;
    }

    public void setNum_sets(long num_sets) {
        this.num_sets = num_sets;
    }

    public long getNum_reps() {
        return num_reps;
    }

    public void setNum_reps(long num_reps) {
        this.num_reps = num_reps;
    }

    @Override
    public String toString() {
        return "Workout{" +
                "workout_id='" + workout_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", date_created='" + date_created + '\'' +
                ", exercise=" + exercise +
                ", sequence=" + sequence +
                ", num_sets=" + num_sets +
                ", num_reps=" + num_reps +
                '}';
    }
}
