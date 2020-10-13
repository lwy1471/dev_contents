# Java Conccurent API - Intro

## 소개

일반적인 Sync-블로킹 모델의 경우 순차적으로 처리가 되기 때문에 효율이 떨어질 수 있다. 이를 위해 Java는 5부터 미래 시점에 결과를 얻을 수 있는 Future 인터페이스를 제공한다.


## 자바의 스레드

### Thread와 Runnable

![쓰레드 생명 주기](https://www.baeldung.com/wp-content/uploads/2018/02/Life_cycle_of_a_Thread_in_Java.jpg)

[출처](https://www.baeldung.com/java-thread-lifecycle)

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


![쓰레드 생명 주기](https://www.baeldung.com/wp-content/uploads/2016/08/2016-08-10_10-16-52-1024x572.png)

[출처](https://www.baeldung.com/thread-pool-java-and-guava)

Thread의 생명 주기를 직접 관리하는 것은 매우 피곤한 일이다. 또한 스레드는 생성 비용이 비싼 편인데 이를 해결하기 위해 주로 ThreadPool을 통해 스레드를 관리한다. Java concurrent api 중 하나인 Executors 클래스를 사용하면 손쉽게 ThreadPool을 생성할 수 있다. 아래는 사이즈 1크기의 스레드 풀을 갖는 executor의 예제이다.

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
Future 클래스는 결과 값 확인을 위해 isDone() 메소드와 get() 메소드를 제공한다.

isDone() 메소드는 서브 테스크 작업이 완료되면 true를 그렇지 않으면 false를 리턴한다. 사용자는 반복문을 통해 명시적으로 블로킹하여 결과 값을 받을 수 있다.
get() 메소드는 서브 테스크 작업이 완료될 때까지 무한정 기다린 뒤 결과 값을 받을 수 있는 메소드이다. 즉 리턴을 받을 때까지 코드는 멈추게 된다.

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

    // do something ...

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

위의 코드에서 서브 테스크의 결과 값이 리턴될 때까지 매인 스레드는 멈추게 된다. 이 때 get() 메소드에 인자 값으로 시간 값을 넣으면 해당 시간까지 기다린 후 TimeoutException을 발생시킨다.

```java

  public static void main(String[] args) {
    Callable<String> task = () -> {
      try {
        TimeUnit.MILLISECONDS.sleep(3000);
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
      // 1초 동안 결과가 오지 않으면 TimeoutException을 발생시킨다.
      String msg = future.get(1, TimeUnit.SECONDS);
      logger.info(msg);
    }catch (InterruptedException| ExecutionException| TimeoutException e) {
      e.printStackTrace();
    }
    String threadName = Thread.currentThread().getName();
    logger.info(String.format("%s task is done. ",threadName));

    executor.shutdown();
  }

```

### 다수의 작업 수행
Executors는 invokeAll() 메소드를 통해 여러 개의 Callable을 등록하고 결과를 수집할 수 있다. 이 메소드는 Callable 컬렉션을 받아들이고 future 리스트를 반환한다.

```java
public static void main(String[] args) {
    List<Callable<String>> callableList = Arrays.asList(
        () -> Thread.currentThread().getName(),
        () -> Thread.currentThread().getName(),
        () -> Thread.currentThread().getName(),
        () -> Thread.currentThread().getName()
    );

    ExecutorService executor = null;
    try {
      executor = Executors.newFixedThreadPool(4);
      List<Future<String>> futureList = executor.invokeAll(callableList);

      for(Future<String> future : futureList) {
        String msg = future.get();
        logger.info(msg);
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    String threadName = Thread.currentThread().getName();
    logger.info(String.format("%s task is done. ", threadName));

    executor.shutdown();
  }
```

### Executor Service의 종류

자바는 팩토리 메서드 패턴(Factory method pattern)으로 Executor Service를 통해 다양한 형태의 스레드풀을 설정할 수 있고 Executor의 자체의 생명 주기를 관리한다. (JVM은 기본적으로 안정한 방법으로 프로그램을 종료하기 위하여 Executor가 종료될 때 까지 메인 스레드를 종료하지 않고 대기한다.)

![Executor_class_diagram](https://programmer.group/images/article/63187986dbb7d8cdfcb5766563ec362e.jpg)

[출처](https://programmer.group/excutors-framework-for-j.u.c-threadpool-executor.html)

```java
// ThreadPoolExecutor의 execute 메소드

    /**
     * Executes the given task sometime in the future.  The task
     * may execute in a new thread or in an existing pooled thread.
     *
     * If the task cannot be submitted for execution, either because this
     * executor has been shutdown or because its capacity has been reached,
     * the task is handled by the current {@link RejectedExecutionHandler}.
     *
     * @param command the task to execute
     * @throws RejectedExecutionException at discretion of
     *         {@code RejectedExecutionHandler}, if the task
     *         cannot be accepted for execution
     * @throws NullPointerException if {@code command} is null
     */
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
        /*
         * Proceed in 3 steps:
         *
         * 1. If fewer than corePoolSize threads are running, try to
         * start a new thread with the given command as its first
         * task.  The call to addWorker atomically checks runState and
         * workerCount, and so prevents false alarms that would add
         * threads when it shouldn't, by returning false.
         *
         * 2. If a task can be successfully queued, then we still need
         * to double-check whether we should have added a thread
         * (because existing ones died since last checking) or that
         * the pool shut down since entry into this method. So we
         * recheck state and if necessary roll back the enqueuing if
         * stopped, or start a new thread if there are none.
         *
         * 3. If we cannot queue task, then we try to add a new
         * thread.  If it fails, we know we are shut down or saturated
         * and so reject the task.
         */
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
            reject(command);
    }



// ThreadPoolExecutor의 shutdown 메소드

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     *
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.  Use {@link #awaitTermination awaitTermination}
     * to do that.
     *
     * @throws SecurityException {@inheritDoc}
     */
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown(); // hook for ScheduledThreadPoolExecutor
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }


```

대표적인 executor Service는 아래와 같다.

- newSingleTheadExecutor();
  - 단일 스레드로 동작하는 Executor이다.
  - 등록된 작업은 설정도니 큐에서 지정하는 순서(FIFO, LIFO 등)에 따라 순차적으로 처리된다.

- newFixedThreadPool(100);
  - 처리할 작업이 등록되면 그에 따라 실제 작업할 스레드를 하나씩 생성한다.
  - 생성할 수 있는 최대 스레드 수는 제한되어있다.
  - 최대 스레드 수를 초과하면 더이상 스레드를 생성하지않는다.

- newCachedThreadPool();
  - 작업 요청이 왔을 때 쉬고 있는 스레드가 있는 경우 쉬는 스레드로 작업을 처리한다.
  - 사용 가능한 스레드가 없으면 신규 스레드를 생성하고 최대 스레드를 제한하지 않는다.
  - 스레드 수 제한이 없기 때문에 작업이 대기하는 경우가 없다. 작업의 내용이 가벼운 경우 사용하기 적합하다.
  - 기본적으로 60초 동안 사용되지 않는 스레드는 종료되고 캐시에서 제거된다.
  
- newSheduledThreadPool(1);
  - 아래에 자세히 소개

#### Scheduled Executors
하나의 작업을 주기적으로 실행시키고 싶을 때 scheduled thread pool을 사용하면 된다. ScheduledExecutorService는 주기적으로 작업을 돌리기 위하여 2개의 메소드를 제공한다.
하나는 scheduleAtFixedRate()이고 나머지는 scheduleWithFixedDelay()이다. 메소드 이름에서 알 수 있듯이 전자는 고정된 주기마다 작업을 실행시키고 후자는 작업이 끝난 후 특정 주기 후에 또다시 작업을 실행한다.

```java
  public static void main(String[] args) {

    long before = System.currentTimeMillis();

    Runnable task = () -> {
      try {
        // 하나의 작업마다 3초가 걸린다.
        TimeUnit.MILLISECONDS.sleep(3000);
        String threadName = Thread.currentThread().getName();

        logger.info(String.format("%d seconds later..", (System.currentTimeMillis() - before)/1000));
        logger.info(String.format("%s task is done. ", threadName));

      } catch (Exception e) {
        e.printStackTrace();
      }
    };

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    // 초기 대기 시간은 0초이고 작업이 끝나고 2초 뒤 다시 작업이 시작된다.
    // 3초, 8초, 13초, 18초 ... 로 작업이 완료된다.
    executor.scheduleWithFixedDelay(task, 0, 2, TimeUnit.SECONDS);

    String threadName = Thread.currentThread().getName();
    logger.info(String.format("%s task is done. ", threadName));
  }
```


### Future의 한계

Future 인터페이스를 통해 비동기 계산 결과를 받을 수 있는 방법은 2가지가 있다. 첫 번째는 필요할 때 while 문으로 isDone()을 검사하여 받는 방법이고, 두 번째는 get() 메소드를 호출하여 받을 수 있다. 하지만 두 방법 모두 스레드를 블록시키게 된다.
 또한 Future 인터페이스는 여러 쓰레드가 작업한 내용을 한꺼번에 관리할 수 있는 기능과 에러 처리에 대한 기능이 부족하다.

</br>
출처

[Java 8 Concurrency Tutorial: Threads and Executors](https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/)

[Life Cycle of a Thread in Java](https://www.baeldung.com/java-thread-lifecycle)