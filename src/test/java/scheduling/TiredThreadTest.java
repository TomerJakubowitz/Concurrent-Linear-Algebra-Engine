package scheduling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class TiredThreadTest {

    @Test
    void testNewTask_ShouldExecuteRunnable() throws InterruptedException {
        TiredThread t = new TiredThread(1, 1.0);
        final boolean[] ran = {false};
        t.start();
        t.newTask(() -> ran[0] = true);
        Thread.sleep(50);
        assertTrue(ran[0]);
        t.shutdown();
        t.join();
    }

    @Test
    void testShutdown_ShouldStopThread() throws InterruptedException {
        TiredThread t = new TiredThread(1, 1.0);
        t.start();
        t.shutdown();
        t.join(200);
        assertFalse(t.isAlive());
    }

    @Test
    void testCompareTo_SameThread_ShouldReturnZero() {
        TiredThread t = new TiredThread(1, 1.0);
        assertEquals(0, t.compareTo(t));
    }

    @Test
    void testIsBusy_ShouldBeFalseAfterTaskCompletes() throws InterruptedException {
        TiredThread t = new TiredThread(1, 1.0);
        t.start();
        t.newTask(() -> {});
        Thread.sleep(50);
        assertFalse(t.isBusy());
        t.shutdown();
        t.join();
    }

    @Test
    void testNewTask_QueueFull_ShouldThrowException() throws InterruptedException {
        TiredThread t = new TiredThread(1, 1.0);
        t.start();
        t.newTask(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {}
        });
        Thread.sleep(50);
        t.newTask(() -> {});
        assertThrows(IllegalStateException.class, () -> {
            t.newTask(() -> {});
        });
        t.shutdown();
        t.join();
    }

    @Test
    void testTaskException_ShouldNotKillThread() throws InterruptedException {
        TiredThread t = new TiredThread(1, 1.0);
        t.start();
        t.newTask(() -> { throw new RuntimeException("Crash"); });
        Thread.sleep(50);
        assertTrue(t.isAlive());
        final boolean[] ran = {false};
        t.newTask(() -> ran[0] = true);
        Thread.sleep(50);
        assertTrue(ran[0]);
        t.shutdown();
        t.join();
    }

    @Test
    void testIdleTime_ShouldIncrease() throws InterruptedException {
        TiredThread t = new TiredThread(1, 1.0);
        t.start();
        Thread.sleep(100);
        t.newTask(() -> {});
        Thread.sleep(20);
        assertTrue(t.getTimeIdle() > 0);
        t.shutdown();
        t.join();
    }
   
}
