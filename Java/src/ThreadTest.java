import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.Test;

public class ThreadTest {

  private int testSize = 50;

  public void test1() throws Exception {
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("runAsync()");
    });

    //future.get();
    TimeUnit.SECONDS.sleep(1);

    CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return "supplyAsync()";
    });

    String result2 = future2.get();
    System.out.println(result2);

    CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() ->
    {
      try {
        TimeUnit.MILLISECONDS.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return "First task takes 0.5 seconds.";
    });
    CompletableFuture<String> task2 = task1.thenApply(str -> {
      try {
        TimeUnit.MILLISECONDS.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      StringBuilder sb = new StringBuilder();
      sb.append(str + "\n");
      sb.append("Second task takes 0.3 seconds.");
      return sb.toString();
    });

    System.out.println(task2.get());

    CompletableFuture<String> sequenceTask = CompletableFuture.supplyAsync(() -> {
      try {
        TimeUnit.MILLISECONDS.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return 0.5;
    }).thenApply((subSec) -> {
      try {
        TimeUnit.MILLISECONDS.sleep(300);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return subSec + 0.3;
    }).thenApply((totalSec) -> {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      totalSec = totalSec + 0.1;
      return "The sequence of tasks takes " + totalSec + " seconds.";
    });

    System.out.println(sequenceTask.get());

    CompletableFuture<Void> thenAcceptTask = CompletableFuture.supplyAsync(() -> {
      try {
        TimeUnit.MILLISECONDS.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return "The callback chains takes 0.5 seconds.";
    }).thenAccept(result -> {
      System.out.println(result);
    });

    TimeUnit.SECONDS.sleep(1);
  }


  @Test
  public void test2() throws Exception {

    long start = System.currentTimeMillis();

    List<Task> tasks = new ArrayList<>();
    for(int i=1;i<=testSize;i++) {
      tasks.add(new Task("Task"+i));
    }

    List<CompletableFuture<Void>> taskStream = tasks
        .stream()
        .map(task -> CompletableFuture.runAsync(
            () -> {
              task.doTask();
              task.printDuration();
            }))
        .collect(Collectors.toList());

    taskStream.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList());

    long duration = (System.currentTimeMillis() - start);
    System.out.println("CompletableFuture tasks take " + duration / 1000.0 + " seconds.");
  }


  @Test
  public void test3() throws Exception {

    long start = System.currentTimeMillis();

    List<Task> tasks = new ArrayList<>();
    for(int i=1;i<=testSize;i++) {
      tasks.add(new Task("Task"+i));
    }

    List<Void> taskStream = tasks
        .stream()
        .map(task -> CompletableFuture.runAsync(
            () -> {
              task.doTask();
              task.printDuration();
            }))
        .map(CompletableFuture::join)
        .collect(Collectors.toList());

    long duration = (System.currentTimeMillis() - start);
    System.out.println("CompletableFuture with synchronous tasks takes " + duration / 1000.0 + " seconds.");
  }

  
  @Test
  public void test4() throws Exception {

    long start = System.currentTimeMillis();

    List<Task> tasks = new ArrayList<>();
    for(int i=1;i<=testSize;i++) {
      tasks.add(new Task("Task"+i));
    }

    ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
    List<CompletableFuture<Void>> taskStream = tasks
        .stream()
        .map(task -> CompletableFuture.runAsync(
            () -> {
              task.doTask();
              task.printDuration();
            }, executor))
        .collect(Collectors.toList());

    taskStream.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList());

    long duration = (System.currentTimeMillis() - start);
    System.out.println("CompletableFuture tasks with the custom executor service take " + duration / 1000.0 + " seconds.");
    executor.shutdown();

  }

  @Test
  public void test5() throws Exception {

    long start = System.currentTimeMillis();

    List<Task> tasks = new ArrayList<>();
    for(int i=1;i<=testSize;i++) {
      tasks.add(new Task("Task"+i));
    }

    tasks.parallelStream()
        .map(
            task -> {
              task.doTask();
              task.printDuration();
              return null;
            }
        ).collect(Collectors.toList());

    long duration = (System.currentTimeMillis() - start);
    System.out.println("ParallelStream tasks take " + duration / 1000.0 + " seconds.");
  }


  class Task {

    private String name;
    private double time;

    public Task(String name) {
      this.name = name;
      //this.time = Math.random() * 1000 * 5;
      this.time = 1000;
    }

    public String getName() {
      return name;
    }

    public float getTime() {
      return (float) (Math.round(this.time) / 1000.0);
    }

    public void doTask() {
      try {
        TimeUnit.MILLISECONDS.sleep((long) this.time);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }


    public void printDuration() {
      System.out.println(
          this.getName() + " takes " + this.getTime() + " seconds in [" + Thread
              .currentThread().getName() + "]");
    }

  }
}
