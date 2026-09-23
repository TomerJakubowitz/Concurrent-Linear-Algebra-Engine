package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TiredExecutorTest {
    @Test
    void testSubmit_ExecutesSingleTask() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(1);
        final int[] counter = {0};
        executor.submit(() -> counter[0]++);
        Thread.sleep(50);
        assertEquals(1, counter[0]);
        executor.shutdown();
    }

    @Test
    void testSubmitAll_ExecutesAllTasks() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(2);
        final int[] counter = {0};
        Runnable[] tasks = new Runnable[5];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = () -> counter[0]++;
        }
        executor.submitAll(java.util.Arrays.asList(tasks));
        assertEquals(5, counter[0]);
        executor.shutdown();
    }

    @Test
    void testSubmitAll_BlocksUntilCompletion() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(1);
        final int[] counter = {0};
        Runnable[] tasks = {
                () -> counter[0]++,
                () -> counter[0]++
        };
        executor.submitAll(java.util.Arrays.asList(tasks));
        assertEquals(2, counter[0]);
        executor.shutdown();
    }

    @Test
    void testMultipleSubmits_AllTasksExecuted() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(3);
        final int[] counter = {0};
        executor.submit(() -> counter[0]++);
        executor.submit(() -> counter[0]++);
        executor.submit(() -> counter[0]++);
        Thread.sleep(50);
        assertEquals(3, counter[0]);
        executor.shutdown();
    }

    @Test
    void testSubmitAll_TaskThrowsException_ShouldNotHang() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(1);
        final int[] counter = {0};
        Runnable failTask = () -> { throw new RuntimeException("Fail"); };
        Runnable successTask = () -> counter[0]++;
        try {
            executor.submitAll(java.util.Arrays.asList(failTask, successTask));
        } catch (Exception e) {}
        assertEquals(1, counter[0]);
        executor.shutdown();
    }

    @Test
    void testSubmitAll_MoreTasksThanThreads() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(2);
        int totalTasks = 20;
        final int[] counter = {0};
        List<Runnable> tasks = new ArrayList<>();
        for(int i=0; i<totalTasks; i++) tasks.add(() -> counter[0]++);
        executor.submitAll(tasks);
        assertEquals(totalTasks, counter[0]);
        executor.shutdown();
    }

    @Test
    void testSubmitAll_EmptyList_ShouldReturnImmediately() throws InterruptedException {
        TiredExecutor executor = new TiredExecutor(1);
        assertDoesNotThrow(() -> executor.submitAll(Collections.emptyList()));
        executor.shutdown();
    }
    
}
