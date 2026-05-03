public class PCB {
    int processId;
    int burstTime;
    int priority;
    int memoryRequired;
    int waitingTime;
    int turnaroundTime;
    String state; // new, ready, running, terminated
    int startTime = -1;
    int terminationTime;

    // Used by: runRR() in Schedulers.java
    // Purpose: Tracks how much CPU burst is left after each quantum execution
    int remainingBurst;

    // Used by: runPriority() in Schedulers.java
    // Purpose: Stores the original priority before aging modifies it
    // (needed for the starvation report at the end)
    int originalPriority;

    // Used by: runPriority() in Schedulers.java
    // Purpose: Counts how long the process has been waiting in the ready queue
    // (needed to detect starvation and apply aging every 4 ms)
    int timeInReadyQueue = 0;

    // Used by: runPriority() in Schedulers.java
    // Purpose: Flag to mark a process as starved, prevents adding it to
    // the starved list more than once
    boolean starved = false;

    public PCB(int processId, int burstTime, int priority, int memoryRequired) {
        this.processId = processId;
        this.burstTime = burstTime;
        this.remainingBurst = burstTime;   
        this.priority = priority;
        this.originalPriority = priority; 
        this.memoryRequired = memoryRequired;
        this.state = "new";
        this.waitingTime = 0;
    }

    @Override
    public String toString() {
        return "P" + processId;
    }
}