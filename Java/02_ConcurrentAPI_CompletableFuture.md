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

## 커스텀 ExecutorService 
CompletableFuture는 기본적으로 ForkJoinPool을 사용한다. ForkJoinPool은 스레드풀의 일종이며 큰 업무를 작은 업무 단위로 쪼개어 병렬로 실행한 후 결과를 취합하는 방식이다.



</br>
출처

[Guide To CompletableFuture](https://www.baeldung.com/java-completablefuture)
[Java 8 Concurrency Tutorial: Threads and Executors](https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/)
