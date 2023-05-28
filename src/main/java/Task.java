package main.java;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String name;
    private int duration;
    private List<Task> subTasks;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Task(String name, int duration) {
        this.name = name;
        this.duration = duration;
        this.subTasks = new ArrayList();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Task> getSubTasks() {
        return this.subTasks;
    }

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String toString() {
        String start = this.startDate != null ? this.startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        String end = this.endDate != null ? this.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
        return "Task Name: <" + this.name + "> Duration: <" + this.duration + " hours> Start Date: <" + start + "> End Date: <" + end + ">";
    }
}
