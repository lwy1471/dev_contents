# Spark Streaming

Spark core에서 확장된 프래임워크로써 실시간 데이터처리를 지원한다.
데이타를 작은 배치로 나누어 RDD로 만들고 이를 Dstream(Discretized Stream)이라고 부른다.

- 초단위 지연
- 확장성
- Fault tolerance
- Easy to develop
- 다양한 입력 소스 (Sockets, Flume, Kafka, Twitter ...)

아래와 같은 Usecase에 활용될 수 있다.

- Continuous ETL
- Website monitoring
- Fraud detection
- Ad monetization
- Social media analysis
- Financial market trends


## DStream Operation
Dstream Operation은 스트림에서 생성된 모든 RDD에 적용되며 2가지 형태가 존재한다.

- Transformations: 기존의 DStream으로부터 새로운 DStream을 생성함
- Ouput operations: 데이터를 file system, db, console 등으로 저장함

### Dstream Transformation
대부분의 RDD Transformation operation이 가능하다.

- Regular Transformations: map, flatMap, filter
- Pair Transformations: reduceByKey, groupByKey, join

#### map
RDD의 각 행별로 파라미터인 lambda 함수를 실행한 후 새로운 RDD를 리턴

#### flatMap
콜랙션 형태의 입력을 하나로 합친 후 lambda 함수를 실행한다. faltten 함수와 map 함수를 합친 것과 같다.


#### filter
주어진 조건에 해당하는 데이터만 선별하여 새로운 RDD를 리턴



# Write a Spark Streaming Application

```python
import sys
from pyspark import SparkContext
from pyspark.streaming import StreamingContext

# Given an RDD of KB requests, print out the count of elements
def printRDDcount(rdd): print "Number of KB requests: "+str(rdd.count())

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print >> sys.stderr, "Usage: StreamingLogs.py <hostname> <port>"
        sys.exit(-1)
    
    # get hostname and port of data source from application arguments
    hostname = sys.argv[1]
    port = int(sys.argv[2])
     
    # Create a new SparkContext
    sc = SparkContext()

    # Set log level to ERROR to avoid distracting extra output
    sc.setLogLevel("ERROR")

    ssc = StreamingContext(sc, 1)

    mystream = ssc.socketTextStream(hostname, port)
    userReq = mystream \
      .filter( lambda line: 'KBDOC' in line )


    userReq.pprint(5)
    userReq.foreachRDD(lambda t,r: printRDDcount(r))

    userReq.saveAsTextFiles("/loudacre/streamlog/kblogs")

    # Start the streaming context and then wait for application to terminate
    ssc.start()
    ssc.awaitTermination()
```


# Process Multiple Batches with Spark Streaming

```python
import sys
from pyspark import SparkContext
from pyspark.streaming import StreamingContext

# Given an array of new counts, add up the counts 
# and then add the sum to the old counts and return the new total
def updateCount(newCounts, state): 
    if state == None: return sum(newCounts)
    else: return state + sum(newCounts)

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print >> sys.stderr, "Usage: StreamingLogsMB.py <hostname> <port>"
        sys.exit(-1)
    
    # get hostname and port of data source from application arguments
    hostname = sys.argv[1]
    port = int(sys.argv[2])
     
    # Create a new SparkContext
    sc = SparkContext()

    # Set log level to ERROR to avoid distracting extra output
    sc.setLogLevel("ERROR")

    # Create and configure a new Streaming Context 
    # with a 1 second batch duration
    ssc = StreamingContext(sc,1)

    # Create a DStream of log data from the server and port specified    
    logs = ssc.socketTextStream(hostname,port)

    # Every two seconds, display the total number of requests over the 
    # last 5 seconds
    logs.countByWindow(5,2).pprint()
    
    # Bonus: Display the top 5 users every second

    # Enable checkpointing (required for state operations)
    ssc.checkpoint("logcheckpt")
    
    # Count requests by user ID for every batch
    userreqs = logs \
        .map(lambda line: (line.split(" ")[2],1)) \
        .reduceByKey(lambda v1,v2: v1+v2)

    # Update total user requests
    totalUserreqs = userreqs \
        .updateStateByKey(lambda newCounts, state: updateCount(newCounts, state))

    # Sort each state RDD by hit count in descending order    
    topUserreqs=totalUserreqs \
       .map(lambda (k,v):(v,k)) \
       .transform(lambda rdd: rdd.sortByKey(False)) \
       .map(lambda (k,v):(v,k)) 

    topUserreqs.pprint()
    
   
    ssc.start()
    ssc.awaitTermination()
```