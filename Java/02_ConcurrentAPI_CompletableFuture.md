# Java Conccurent API - CompletableFuture

## 소개

CompletableFuture는 기존의 Future 인터페이스의 부족한 기능을 많이 보완한다. 대표적으로 작업간 파이프라인을 구성하거나 Future-ExecutorService의 boilerplate 코드를 제거할 수 있다. 또한 작업시 발생하는 Exception을 처리할 수 있습니다.

기본적인 사용법은 아래와 같다.

```java

CompletableFuture<Void> future = CompletableFuture.runAsync( () -> {
  try {
    TimeUnit.MILLISECONDS.sleep(3000);
  }catch(InterruptedException e) {
    e.printStackTrace();
  }
});

```

## 기본 메소드

CompletableFuture는 작업간 파이프라인을 구성하기 위해 다양한 메소드를 제공한다. 대표적인 메소드는 아래와 같다.

### runAsync()

반환 값이 없는 작업을 실행시키기 위한 메소드. Runnable 객체를 인자로 받는다.


```java

  CompletableFuture<Void> future = CompletableFuture.runAsync( () -> {
    try {
      TimeUnit.MILLISECONDS.sleep(3000);
    }catch(InterruptedException e) {
      e.printStackTrace();
    }
  });

  future.get();

```

### supplyAsync()

백그라운드에서 작업을 수행한 후 리턴을 받기 위해서 사용하는 메소드. Supplier<T> 객체를 인자로 받는다.
Supplier<T> 인터페이스는 @FunctionalInterface로서 T타입을 반환값을 가지는 메소드(get) 하나만 가지고 있다.
supplyAsync() 메소드 예제는 아래와 같다.

```java

  CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    try {
      TimeUnit.MILLISECONDS.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "supplyAsync()";
  });

  String result2 = future2.get();
  System.out.println(result2);

```


## Callback 메소드

CompletableFuture.get() 메소드는 Future가 완료될 때까지 블로킹된다. non-blocking 기능을 사용하기 위해서는 Callback 기능을 구현해야하며, CompletableFuture는 then~() 메소드를 통해
손쉽게 non-blocking을 지원한다.

### thenApply()

아래는 CompletableFuture<T> 의 결과에 thenApply() 메소드로 결합하는 예제이다. 첫번째 작업이 완료된 후 thenApply()에 명시된 작업을 순차적으로 실행한다.

```java
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
    sb.append(str+"\n");
    sb.append("Second task takes 0.3 seconds.");
    return sb.toString();
  });

  System.out.println(task2.get());
```

```
First task takes 0.5 seconds.
Second task takes 0.3 seconds.
```

CompletableFuture 메소드는 연속적으로 결합할 수 있다. 이를 callback chain으로 부른다.

```java

  CompletableFuture<String> sequenceTask = CompletableFuture.supplyAsync( () -> {
        try {
          TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return 0.5;
      }).thenApply( (subSec) -> {
        try {
          TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return subSec+0.3;
      }).thenApply( (totalSec) -> {
        try {
          TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        totalSec = totalSec + 0.1;
        return "The sequence of tasks takes " + totalSec + " seconds.";
      });

      System.out.println(sequenceTask.get());

```


### thenAccept()

Future 완료 후 callback 기능에서 아무것도 반환하지 않고 일부 코드를 실행하려면 thenAccept()와 thenRun() 메소드를 사용할 수 있다. 해당 메서드들은 주로 callback chain 마지막에 사용된다.
thenAccept() 메소드는 Consumer<T> 객체를 인자로 받는다(Consumer<T>는 반환값이 없다). 

```java

  CompletableFuture<Void> thenAcceptTask = CompletableFuture.supplyAsync( () -> {
    try {
      TimeUnit.MILLISECONDS.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "The callback chains takes 0.5 seconds.";
  }).thenAccept( result -> {
    System.out.println(result);
  });

```

## join()

join 메소드는 스레드가 종료되기까지 blocking하는 함수이며 Future의 get 메소드와 거의 동일하다. 차이점은 get 메소드는 checked exception을 던지기 때문에 반드시 try/catch구문으로 예외 처리를 해야한다.
아래는 CompletableFuture와 Steam API를 사용하여 여러 개의 작업을 생성한 뒤 join 메소드를 통해 작업이 완료되기까지 기다리는 예제이다. 

```java

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

```

```java
Task9 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-4]
Task8 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-15]
Task5 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-6]
Task3 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-13]
Task6 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-8]
...
CompletableFuture tasks take 5.013 seconds.
```

CompletableFuture에 task를 제출하고 실행하는 map, 결과를 받아오는 map 연산을 두 개의 스트림 파이프라인으로 처리하였다. 스트림 연산의 특성으로 인해 하나의 파이프라인으로 연산 처리한다면 동작이 순차적으로 이루어진다. 아래는 순차적으로 실행되는 코드 예제이다.

```java

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

```

```java
Task1 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-9]
Task2 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-9]
Task3 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-9]
Task4 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-9]
Task5 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-9]
Task6 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-9]
Task7 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-9]
...
CompletableFuture with synchronous tasks takes 50.246 seconds.
```

## CompletableFuture와 ParallelStream

Java는 8부터 Parallel Stream 기능을 제공한다. Parallel Stream 기능을 이용하면 손쉽게 멀티스레드 프로그램을 작성할 수 있고 가독성 또한 높일 수 있다.
하지만, Parallel은 하나의 Thread Pool에서 처리되기 때문에 성능 장애가 발생할 수 있으므로 조심히 사용해야한다. 아래는 Parallel Stream 을 활용한 비동기 작업 예제이다.

```java

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

```

```java
Task36 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-6]
Task4 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-10]
Task34 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-8]
Task37 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-2]
Task27 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-9]
Task8 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-11]
Task32 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-3]
Task16 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-1]
Task33 takes 1.0 seconds in [main]
Task45 takes 1.0 seconds in [ForkJoinPool.commonPool-worker-13]
...
ParallelStream tasks take 5.017 seconds.
```



## 커스텀 ExecutorService 
CompletableFuture는 기본적으로 ForkJoinPool을 사용한다. ForkJoinPool은 Java 7에서 소개되었으며 작동 방식은 분할과 정복 알고리즘과 비슷하다.
기본적으로 ExecutorService의 구현체이지만, 다른 점은 각 스레드들이 개별 큐를 가지고 있고 work-stealing 알고리즘이 적용된다는 점이다.

![ForkJoinPool](https://www.oracle.com/ocom/groups/public/@otn/documents/digitalasset/422590.png)

[출처](https://www.oracle.com/technical-resources/articles/java/fork-join.html)

아래는 ForkJoinPool이 아닌 다른 ExecutorService(FixedThreadPool)를 사용하는 예제이다.을 사용한 예제이다.


```java

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

```

```java
Task8 takes 1.0 seconds in [pool-1-thread-8]
Task1 takes 1.0 seconds in [pool-1-thread-1]
Task5 takes 1.0 seconds in [pool-1-thread-5]
Task3 takes 1.0 seconds in [pool-1-thread-3]
Task11 takes 1.0 seconds in [pool-1-thread-11]
Task4 takes 1.0 seconds in [pool-1-thread-4]
...
CompletableFuture tasks with the custom executor service take 1.081 seconds.
```


</br>
출처

[Guide To CompletableFuture](https://www.baeldung.com/java-completablefuture)
[Java 8 Concurrency Tutorial: Threads and Executors](https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/)
