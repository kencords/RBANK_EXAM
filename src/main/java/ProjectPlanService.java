package main.java;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProjectPlanService {

    private static Scanner scanner = new Scanner(System.in);
    Map<String, ProjectPlan> projectPlans = new HashMap<>();

    private LocalDateTime startDate;
    public void startScheduleApp() {
        int mainChoice = 0;

        while (mainChoice != 8) {
            System.out.println("---------PROJECT PLAN SCHEDULER---------");
            System.out.println("1. Add Project");
            System.out.println("2. Add Main Task");
            System.out.println("3. Add Dependency Task");
            System.out.println("4. Print Project List");
            System.out.println("5. Print Task List");
            System.out.println("6. Generate Schedule");
            System.out.println("7. Exit");

            System.out.print("Enter choice: ");
            mainChoice = scanner.nextInt();

            if (mainChoice == 1) {
                addProject();
            } else if (mainChoice == 2) {
                addMainTask();
            } else if (mainChoice == 3) {
                addDependencyTask();
            } else if (mainChoice == 4) {
                printProjectPlans();
            } else if (mainChoice == 5) {
                printTaskList(null, 0);
            } else if (mainChoice == 6) {
                ProjectPlan projectPlan = inputAndVerifyProjectPlan();
                if (projectPlan != null) {
                    startDate = LocalDateTime.now();
                    generateSchedule(projectPlan, projectPlan.getMainTask());
                    List<Task> subTasks = projectPlan.getMainTask().getSubTasks();
                    Schedule schedule = new Schedule(LocalDateTime.now(), LocalDateTime.now());
                    for (Task task : subTasks) {
                        if (task.getStartDate().isAfter(schedule.getStartDate())) {
                            schedule.setStartDate(task.getStartDate());
                            schedule.setEndDate(task.getEndDate());
                        }
                    }
                    projectPlan.getMainTask().setStartDate(schedule.getEndDate());
                    projectPlan.getMainTask().setEndDate(schedule.getEndDate().plusHours(projectPlan.getMainTask().getDuration()));
                    printSchedule(projectPlan);
                }
            } else if (mainChoice == 7) {
                System.exit(0);
            }
        }
    }

    private void addProject() {
        while (true) {
            System.out.println("********************************");
            System.out.print("Enter Project Name: ");
            String name = String.valueOf(scanner.next());

            ProjectPlan tmp = projectPlans.get(name);
            if (tmp != null) {
                System.out.println("Project Plan with name " + name +  " already exist");
                continue;
            }
            ProjectPlan projectPlan = new ProjectPlan(name);
            projectPlans.put(name, projectPlan);
            break;
        }

    }

    private void addMainTask() {
        boolean cnd = true;
        if (projectPlans.isEmpty()) {
            System.out.println("There are no existing project plans");
            return;
        }
        while (cnd) {
            ProjectPlan projectPlan = inputAndVerifyProjectPlan();
            if (projectPlan != null) {
                if (projectPlan.getMainTask() != null) {
                    System.out.println("Project Plan already has a Main Task: " + projectPlan.getMainTask());
                    return;
                }
                Task task = inputAndVerifyTask(projectPlan, true);
                inputDuration(task);

                projectPlan.setMainTask(task);
                projectPlan.getAllTasks().put(task.getName(), task);
                cnd = false;
            }
        }
    }

    private void addDependencyTask() {
        boolean cnd = true;
        if (projectPlans.isEmpty()) {
            System.out.println("There are no existing project plans");
            return;
        }
        while (cnd) {
            ProjectPlan projectPlan = inputAndVerifyProjectPlan();
            if (projectPlan == null) {
                System.out.println("Project Plan not found.");
            } else {
                Task subTask = inputAndVerifyTask(projectPlan, false);
                if (subTask == null) {
                    return;
                }
                inputDuration(subTask);

                projectPlan.getAllTasks().put(subTask.getName(), subTask);
                cnd = false;
            }
        }
    }

    private void printProjectPlans() {
        System.out.println("********************************");
        System.out.println("Project Plan List");
        for (var entry : projectPlans.entrySet()) {
            System.out.println("Project Name: <" + entry.getKey() + "> Number of Tasks: <" + entry.getValue().getAllTasks().size() + ">");
        }
    }

    private void printTaskList(Task task, int hierarchy) {
        if (task == null) {
            ProjectPlan projectPlan = inputAndVerifyProjectPlan();
            task = projectPlan.getMainTask();
            System.out.println("\nTask List for Project " + projectPlan.getName());
            System.out.println("Task Name: <" + task.getName() + "> Duration: <" + task.getDuration() + "> hours");
        }
        String prefix = "   ";
        for (int i = 0; i < hierarchy; i++) {
            prefix += "   ";
        }
        for (int i = 0; i < task.getSubTasks().size(); i++) {
            Task subTask = task.getSubTasks().get(i);
            System.out.println(prefix + "-Sub Task Name: <" + subTask.getName() + "> Duration: <" + subTask.getDuration() + "> hours");
            if (subTask.getSubTasks() != null && subTask.getSubTasks().size() > 0) {
                printTaskList(subTask, hierarchy + 1);
            }
        }
    }

    private Schedule generateSchedule(ProjectPlan projectPlan, Task mainTask) {
        if (mainTask.getSubTasks().size() == 0) {
            mainTask.setStartDate(startDate);
            mainTask.setEndDate(startDate.plusHours(mainTask.getDuration()));

            return new Schedule(startDate, startDate.plusHours(mainTask.getDuration()));
        } else {
            Schedule schedule = null;
            for (Task subTask : mainTask.getSubTasks()) {
                LocalDateTime taskStartDate = startDate;
                if (subTask.getSubTasks() != null && subTask.getSubTasks().size() > 0) {
                    schedule = generateSchedule(projectPlan, subTask);
                } else {
                    subTask.setStartDate(taskStartDate);
                    subTask.setEndDate(taskStartDate.plusHours(subTask.getDuration()));
                    schedule = new Schedule(subTask.getStartDate(), subTask.getEndDate());
                }
            }

            if (mainTask.getStartDate() == null || schedule.getEndDate().isAfter(mainTask.getStartDate())) {
                mainTask.setStartDate(schedule.getEndDate());
                mainTask.setEndDate(schedule.getEndDate().plusHours(mainTask.getDuration()));
            }
            return schedule;
        }
    }

    private void printSchedule(ProjectPlan projectPlan) {
        Task mainTask = projectPlan.getMainTask();
        System.out.println("********************************");
        System.out.println("Project " + projectPlan.getName() + " Schedule");
        System.out.println("Main Task: (" + mainTask + ")");
        System.out.println("Dependency Tasks: ");

        for (int i = 0; i < mainTask.getSubTasks().size(); i++) {
            printDependencies(mainTask.getSubTasks().get(i), 0);
        }
    }

    private void printDependencies(Task task, int hierarchy) {
        String prefix = "   ";
        for (int i = 0; i < hierarchy; i++) {
            prefix += "   ";
        }
        System.out.println(prefix + " Task: (" + task + ")");
        if (task.getSubTasks() != null && task.getSubTasks().size() > 0) {
            for (Task sub : task.getSubTasks()) {
                printDependencies(sub, hierarchy + 1);
            }
        }
    }
    private ProjectPlan inputAndVerifyProjectPlan() {
        System.out.println("********************************");
        System.out.print("Enter Project Name: ");
        String name = String.valueOf(scanner.next());

        ProjectPlan projectPlan = projectPlans.get(name);
        if (projectPlan == null) {
            System.out.println("Project Plan not found.");
            return null;
        }

        return projectPlan;
    }

    private Task inputAndVerifyTask(ProjectPlan projectPlan, Boolean isMain) {
        boolean cnd = true;
        if (!isMain && projectPlan.getMainTask() == null) {
            System.out.println("No Main Task available for this task");
            return null;
        }
        while (cnd) {
            System.out.println("********************************");
            System.out.print(isMain ? "Enter Main Task Name: " : "Enter Main/Dependency Task Name to add Task to: ");
            String name = String.valueOf(scanner.next());

            Task tmp = projectPlan.getAllTasks().get(name);;
            if (!isMain) {
                if (tmp == null) {
                    System.out.println("Task Not Found.");
                } else {
                    boolean cnd2 = true;
                    while (cnd2) {
                        System.out.println("********************************");
                        System.out.print("Enter Dependency Task Name: ");
                        name = String.valueOf(scanner.next());

                        String finalName = name;
                        long count = tmp.getSubTasks().stream().filter(task -> task.getName().equalsIgnoreCase(finalName)).count();
                        if (count > 0) {
                            System.out.println("Dependency Task already exist");
                        } else {
                            Task task = new Task(name, 0);
                            tmp.getSubTasks().add(task);

                            return task;
                        }
                    }
                }
            } else {
                if (tmp != null) {
                    System.out.println("Main Task already exist");
                    continue;
                }

                return new Task(name, 0);
            }
        }
        return null;
    }

    private void inputDuration(Task task) {
        System.out.print("Enter duration in hours: ");
        int duration = Integer.valueOf(scanner.next());

        task.setDuration(duration);
    }
}
