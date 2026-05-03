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
///////////////////////////////////////////////////////TODO
    }
}


// Thread 2: Memory Loader
class MemoryLoader implements Runnable {
    @Override
    public void run() {
        ///////////////////////////////////////////////////////////TODO
    }
}