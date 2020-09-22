# Java Conccurent API - CompletableFuture

```java
public class AsyncTest {

  private static Logger logger = Logger.getLogger(AsyncTest.class);

  private ExecutorService executorService;

  public static void main(String[] args) throws Exception {
    AsyncTest asyncTest = new AsyncTest();
    Future<String> future = asyncTest.calculateAsync();

    // do something
    asyncTest.doSomething();

    String result = future.get();
    logger.info("Future result : " + result);

    asyncTest.executorService.shutdown();
  }

  public Future<String> calculateAsync() throws Exception {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    executorService = Executors.newCachedThreadPool();
    executorService.submit(() -> {
      TimeUnit.MILLISECONDS.sleep(500);
      completableFuture.complete("hello");
      return null;
    });

    return completableFuture;
  }

  public void doSomething() {
    logger.info("do something..");
  }
}

```


</br>
출처

[Guide To CompletableFuture](https://www.baeldung.com/java-completablefuture)
[Java 8 Concurrency Tutorial: Threads and Executors](https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/)
