package main.java;

import java.util.HashMap;
import java.util.Map;

public class ProjectPlan {
    private String name;
    private Map<String, Task> allTasks = new HashMap();
    private Task mainTask;

    public ProjectPlan(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Task> getAllTasks() {
        return this.allTasks;
    }

    public void setAllTasks(Map<String, Task> allTasks) {
        this.allTasks = allTasks;
    }

    public Task getMainTask() {
        return this.mainTask;
    }

    public void setMainTask(Task mainTask) {
        this.mainTask = mainTask;
    }
}
