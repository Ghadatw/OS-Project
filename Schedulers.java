import java.util.*;

public class Schedulers {

    // Algorithm 1: Shortest Job First
    public static void runSJF() {
        List<PCB> completed = new ArrayList<>();
        List<int[]> gantt = new ArrayList<>(); // Stores {pid, start, end} for each execution

        ///////////////////////////////////////////////////////// TODO

        // Display results
        printGantt(gantt);
        printResultsTable(completed);
    }

    // Algorithm 2: Round Robin (Quantum = 5 ms)
    public static void runRR() {

        final int QUANTUM = 5;

        List<PCB> completed = new ArrayList<>();
        List<int[]> gantt = new ArrayList<>();

        Queue<PCB> rrQueue = new LinkedList<>();  // Local queue for RR rotation

        int currentTime = 0;


        // Continue until all processes are completed
        while (completed.size() < Main.totalProcesses) {

            // Pull any newly arrived processes from ready queue into local rrQueue
            synchronized (Main.readyQueue) {
                while (!Main.readyQueue.isEmpty()) {
                    rrQueue.add(Main.readyQueue.poll());
                }
            }

            // If no process available yet, wait briefly for Thread 2 to load more
            if (rrQueue.isEmpty()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
                continue;
            }

            // Get the next process from the front of the queue
            PCB current = rrQueue.poll();
            current.state = "running";

            // save first start time
            if (current.startTime == -1) {
                current.startTime = currentTime;
            }

            int start = currentTime;

            // execute process for quantum or remaining burst
            int executionTime = Math.min(QUANTUM, current.remainingBurst);

            current.remainingBurst -= executionTime;

            currentTime += executionTime;

            int end = currentTime;

            // save gantt chart info
            gantt.add(new int[] {
                    current.processId,
                    start,
                    end
            });

            // Pull any newly arrived processes before re-adding current to the queue
            synchronized (Main.readyQueue) {
                while (!Main.readyQueue.isEmpty()) {
                    rrQueue.add(Main.readyQueue.poll());
                }
            }

            if (current.remainingBurst == 0) {
                // Process finished execution
                current.state = "terminated";
                current.terminationTime = currentTime;
                current.turnaroundTime = current.terminationTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;
                completed.add(current);
                Main.releaseMemory(current.memoryRequired);
            } else {
                // Process still has remaining burst, send back to end of queue
                current.state = "ready";
                rrQueue.add(current);
            }
        }

        // Display results
        printGantt(gantt);
        printResultsTable(completed);

    }

    // Algorithm 3: Priority Scheduling
    public static void runPriority() {
        List<PCB> completed = new ArrayList<>();
        List<PCB> starvedList = new ArrayList<>(); // Tracks starved processes
        List<int[]> gantt = new ArrayList<>();
        int currentTime = 0;

        ///////////////////////////////////////////////////////// TODO

        // Display results
        printGantt(gantt);
        printResultsTable(completed);

        // Print Starvation Report (Required for Priority)

        ///////////////////////////////////////////////////////// TODO

    }

    // Print Gantt Chart
    static void printGantt(List<int[]> gantt) {
        System.out.println("\n Gantt Chart:");
        StringBuilder bar = new StringBuilder("|");
        StringBuilder times = new StringBuilder();

        // Build the visual bar with process IDs
        for (int[] entry : gantt) {
            bar.append(" P").append(entry[0]).append(" |");
        }
        System.out.println(bar);

        // Build the time axis below the bar
        times.append(gantt.get(0)[1]);
        for (int[] entry : gantt) {
            times.append("    ").append(entry[2]);
        }
        System.out.println(times);

        // Print detailed execution timeline
        System.out.println("\n Execution Details:");
        for (int[] entry : gantt) {
            System.out.println("Time " + entry[1] + " -> " + entry[2]
                    + " : P" + entry[0] + " executing (duration: "
                    + (entry[2] - entry[1]) + " ms)");
        }
    }

    // Print Results Table and Averages
    static void printResultsTable(List<PCB> processes) {
        // Sort processes by ID for cleaner output
        processes.sort((a, b) -> a.processId - b.processId);

        System.out.println("\n Results Table:");
        System.out.printf("%-5s %-8s %-8s %-12s %-10s %-12s%n",
                "PID", "Burst", "Start", "Termination", "Waiting", "Turnaround");
        System.out.println("------------------------------------------------------------");

        // Calculate totals while printing each row
        double totalWait = 0, totalTAT = 0;
        for (PCB p : processes) {
            System.out.printf("%-5d %-8d %-8d %-12d %-10d %-12d%n",
                    p.processId, p.burstTime, p.startTime,
                    p.terminationTime, p.waitingTime, p.turnaroundTime);
            totalWait += p.waitingTime;
            totalTAT += p.turnaroundTime;
        }

        // Print performance averages
        int n = processes.size();
        System.out.println("------------------------------------------------------------");
        System.out.printf("Average Waiting Time   : %.2f ms%n", totalWait / n);
        System.out.printf("Average Turnaround Time: %.2f ms%n", totalTAT / n);
    }

}