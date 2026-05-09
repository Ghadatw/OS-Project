import java.util.*;
import java.util.concurrent.*;

public class Main {
    // Shared queues between threads
    static Queue<PCB> jobQueue = new LinkedList<>();
    static Queue<PCB> readyQueue = new LinkedList<>();

    // Memory management
    static final int TOTAL_MEMORY = 2048;  // Total memory in MB
    static int usedMemory = 0;

    // Control flags for threads
    static volatile boolean jobLoadingDone = false;
    static volatile boolean simulationDone = false;

    // Total number of processes (set after reading the file)
    static int totalProcesses = 0;

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        // Display menu to the user
        System.out.println("CPU Scheduling Simulator");
        System.out.println("Choose Scheduling Algorithm:");
        System.out.println("1. Shortest Job First (SJF)");
        System.out.println("2. Round Robin (RR)");
        System.out.println("3. Priority Scheduling");
        int choice = sc.nextInt();

        // Start Thread 1: reads the input file and fills the job queue
        Thread t1 = new Thread(new JobLoader("job.txt"), "JobLoader");
        t1.start();

        // Start Thread 2: moves processes from job queue to ready queue
        Thread t2 = new Thread(new MemoryLoader(), "MemoryLoader");
        t2.start();

        // Wait for Thread 1 to finish reading the file
        t1.join();

        // Main thread executes the selected scheduling algorithm
        switch (choice) {
            case 1:
                Schedulers.runSJF();
                break;
            case 2:
                Schedulers.runRR();
                break;
            case 3:
                Schedulers.runPriority();
                break;
            default:
                System.out.println("Invalid choice!");
                System.exit(0);
        }

        // Signal Thread 2 to stop after the algorithm completes
        simulationDone = true;
        t2.join();

    }

    // release memory after a process terminates
    public static synchronized void releaseMemory(int mem) {
        usedMemory -= mem;
    }
}


// Thread 1: Job Loader (reads file) 
class JobLoader implements Runnable {
    private String filename;

    public JobLoader(String filename) {
        this.filename = filename;
    }

    @Override
       public void run() {
        try (java.io.BufferedReader br =
                 new java.io.BufferedReader(new java.io.FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Parse line format: ID:Burst:Priority;Memory
                String[] parts = line.split("[:;]");
                int id = Integer.parseInt(parts[0].trim());
                int burst = Integer.parseInt(parts[1].trim());
                int priority = Integer.parseInt(parts[2].trim());
                int memory = Integer.parseInt(parts[3].trim());

                // Create a new PCB for the process
                PCB process = new PCB(id, burst, priority, memory);

                // add to the job queue 
                synchronized (Main.jobQueue) {
                    Main.jobQueue.add(process);
                    Main.jobQueue.notifyAll();
                }
                count++;
                System.out.println("Thread 1: Process P" + id + " added to job queue");
            }
            Main.totalProcesses = count;
        } catch (Exception e) {
            System.err.println("Thread 1: Error reading file: " + e.getMessage());
        }
        Main.jobLoadingDone = true;
        System.out.println("Thread 1: Finished, total processes = " + Main.totalProcesses);
    }
}


// Thread 2: Memory Loader
class MemoryLoader implements Runnable {
    @Override
    public void run() {
        while (!Main.simulationDone) {
            synchronized (Main.jobQueue) {
                if (!Main.jobQueue.isEmpty()) {
                    PCB job = Main.jobQueue.peek();
                    // Check if enough memory is available
                    if (Main.usedMemory + job.memoryRequired <= Main.TOTAL_MEMORY) {
                        Main.jobQueue.poll();
                        Main.usedMemory += job.memoryRequired;
                        job.state = "ready";

                        // Move process from job queue to ready queue
                        synchronized (Main.readyQueue) {
                            Main.readyQueue.add(job);
                            Main.readyQueue.notifyAll();
                        }
                        System.out.println("Thread 2: P" + job.processId
                            + " loaded to ready queue (Memory used: "
                            + Main.usedMemory + "/" + Main.TOTAL_MEMORY + " MB)");
                    }
                }
            }

            // Small sleep to avoid busy waiting and high CPU usage
            try { Thread.sleep(1); } catch (InterruptedException e) {}

            // Exit condition: all jobs loaded and simulation is done
            if (Main.jobLoadingDone && Main.jobQueue.isEmpty() && Main.simulationDone) {
                break;
            }
        }
        System.out.println("Thread 2: Finished");
}
}