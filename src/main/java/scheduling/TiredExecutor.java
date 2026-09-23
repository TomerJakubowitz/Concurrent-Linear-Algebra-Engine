package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        for (int i = 0; i < workers.length; i++) {
            double fatigueFactor = 0.5 + Math.random();
            workers[i] = new TiredThread(i, fatigueFactor);
            idleMinHeap.add(workers[i]);
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        // TODO
        try {
            inFlight.incrementAndGet();
            TiredThread thread = idleMinHeap.take();
            Runnable wrapped = () -> {
                thread.setBusy(true);
                long start = System.nanoTime();
                try {
                    task.run();
                } finally {
                    long end = System.nanoTime();
                    thread.addTimeUsed(end - start);
                    thread.setBusy(false);
                    if(inFlight.decrementAndGet() == 0){
                        synchronized (this) {
                            notifyAll();
                        }
                    }
                    idleMinHeap.put(thread);
                }
            };     
            try{
                thread.newTask(wrapped);
            }catch(Exception e){
                inFlight.decrementAndGet();
                idleMinHeap.put(thread);
                throw e;
            }       
        } catch (InterruptedException e) {
            inFlight.decrementAndGet();
            Thread.currentThread().interrupt();
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        for(Runnable t: tasks){
            submit(t);
        }
        synchronized (this) {
            while(inFlight.get() > 0){
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for(TiredThread thread: workers){
            thread.shutdown();
        }
        for(TiredThread thread: workers){
            thread.join();
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String report = "Fairness: " + calculateFairness()+ "\n" + "Worker Report:\n";
        for(TiredThread w : workers){
            report += "Worker " + w.getWorkerId()
                + ", time used: " + w.getTimeUsed()
                + ", time idle: " + w.getTimeIdle()
                + ", fatigue: " + w.getFatigue()
                + "\n";
        }
        return report;
    }
    private synchronized double calculateFairness(){
        double sum = 0;
        for(TiredThread w: workers){
            sum += w.getFatigue();
        }
        double avg = sum / workers.length;
        double fairness = 0;
        for(TiredThread w: workers){
            double diff = w.getFatigue() - avg;
            fairness += diff * diff;
        }
        return fairness;
    }
}
