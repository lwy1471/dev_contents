# Java Conccurent API - Intro

## 소개
일반적인 Sync-블로킹 모델의 경우 순차적으로 처리가 되기 때문에 효율이 떨어질 수 있다. 이를 위해 Java는 5부터 미래 시점에 결과를 얻을 수 있는 Future 인터페이스를 제공한다. 하지만 Future 인터페이스 또한 여러 계산을 합치거나 에러를 처리하기 쉽지 않은 단점이 있다. 이러한 단점을 보완하기 위해 Java 8의 Concurrency API에서 소개된 CompletableFuture에 대해 알아보자.


## 자바의 스레드

### Thread와 Runnable
자바는 JDK 1.0부터 Thread를 문법적으로 지원하였다. 사용자는 단지 Runnable 인터페이스를 구현하기만 하면 된다. 

```java
  public static void main(String[] args) {
    Runnable task = () -> {
      try {
        TimeUnit.MILLISECONDS.sleep(500);
        String threadName = Thread.currentThread().getName();
        logger.info(String.format("%s task is done. ",threadName));
      }catch (InterruptedException e) {
        e.printStackTrace();
      }
    };

    Thread thread = new Thread(task);
    thread.start();
    
    String threadName = Thread.currentThread().getName();
    logger.info(String.format("%s task is done. ",threadName));
  }
```

스레드의 특성상 코드가 언제 실행될지는 예측이 불가능하므로 작업 순서를 가늠하는 것은 어려움이 있다. 스레드간 작업의 순서를 보장하기 위해 간혹 위의 코드와 같이 Sleep() 메소드로 블로킹을 적용하는 경우가 있는데, 이는 비효율적이고 추가적인 에러가 발생할 수 있으며 무엇보다 보기에 좋지 않다.

### Executors
Thread는 생성 생명 주기를 직접 관리하는 것은 매우 피곤한 일이다. 또한 스레드는 생성 비용이 비싼 편인데 이를 해결하기 위해 주로 ThreadPool을 통해 스레드를 관리한다. Java concurrent api 중 하나인 Executors 클래스를 사용하면 손쉽게 ThreadPool을 생성할 수 있다. 아래는 사이즈 1크기의 스레드 풀을 갖는 executor의 예제이다.

```java
public static void main(String[] args) {
    Runnable task = () -> {
      try {
        TimeUnit.MILLISECONDS.sleep(500);
        String threadName = Thread.currentThread().getName();
        logger.info(String.format("%s task is done. ",threadName));
      }catch (InterruptedException e) {
        e.printStackTrace();
      }
    };

    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.submit(task);

    String threadName = Thread.currentThread().getName();
    logger.info(String.format("%s task is done. ",threadName));

    // Executors는 반드시 명시적으로 종료시켜야 한다.
    executor.shutdown();
  }
```

### Callable and Futures
ExecutorService의 submit() 메소드의 인자로는 Runnable뿐만 아니라 Callable도 지원한다. Callable은 Runnable과 거의 동일하지만 리턴 타입을 갖는 것이 차이점이다. Callable도 Runnable과 같이 Executor Service를 통해 실행될 수 있다. Executors의 submit 함수는 블록킹 함수가 아니다. 그렇다면 스레드가 작업을 완료한 결과 값을 받는 방법은 무엇일까? 결과를 받는 방법은 Future라는 클래스에 있다. Executors는 submit  함수의 리턴 값으로 미래의 작업 결과를 받을 수 있는 Future라는 티켓을 발급해준다.

```java
 public static void main(String[] args) {
    Callable<String> task = () -> {
      try {
        TimeUnit.MILLISECONDS.sleep(500);
        String threadName = Thread.currentThread().getName();
        String msg = String.format("%s task is done. ",threadName);

        return msg;
      }catch (InterruptedException e) {
        e.printStackTrace();
      }
      return null;
    };

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<String> future = executor.submit(task);

    try {
      String msg = future.get();
      logger.info(msg);
    }catch (InterruptedException| ExecutionException e) {
      e.printStackTrace();
    }
    String threadName = Thread.currentThread().getName();
    logger.info(String.format("%s task is done. ",threadName));

    executor.shutdown();
  }
```

### Future의 한계
Future 인터페이스를 통해 비동기 계산 결과를 받을 수 있는 방법은 2가지가 있다. 첫 번째는 필요할 때 while 문으로 isDone()을 검사하여 받는 방법이고, 두 번째는 get() 메소드를 호출하여 받을 수 있다. 하지만 두 방법 모두 스레드를 블록시키게 된다.
 또한 Future 인터페이스는 여러 쓰레드가 작업한 내용을 한꺼번에 관리할 수 있는 기능과 에러 처리에 대한 기능이 부족하다.

</br>
출처

[Java 8 Concurrency Tutorial: Threads and Executors](https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/)