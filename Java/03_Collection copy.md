# Java의 Stream API

## Stream 개요

스트림 API는 다량의 데이터 처리 작업(순차적이든 병렬적이든)을 돕고자 자바 8에 추가되었다. 
이러한 스트림 API의 특징은 아래와 같다.

1. 스트림은 외부 반복을 통해 작업하는 컬렉션과 달리 내부 반복을 통해 작업을 수행한다.
1. 스트림은 재사용이 가능한 컬랙션과 달리 단 한번만 사용할 수 있다.
1. 스트림은 원본 데이터를 변경하지 않는다.
1. 스트림의 연산은 필터-맵 기반의 API를 사용하여 지연(lazy) 연산을 통해 성능을 최적화한다.
1. 스트림은 parallelStream() 메소드를 통해 손쉬운 병렬 처리를 지원한다.

스트림 안의 데이터 원소들은 객체나 기본 타입 값이다. 기본 타입 값으로는 int, long, double 이렇게 세 가지를 지원한다.

스트림 파이프라인은 소스 스트림에서 시작하여 중간 연산, 종단 연산으로 끝이난다. 각 중간 연산은 스트림을 어떠한 방식으로 변환(transform) 한다.

스트림 파이프라인은 지연 평가(lazy evaluation)된다. 평가는 종단 연산이 호출될 때 이뤄지며 종단 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다. 이러한 지연 평가를 통해 무한 스트림을 다룰 수 있게 된다.

스트림을 사용하기 좋은 형태는 아래와 같다.

1. 원소들의 시퀀스를 일관되게 변환한다.
1. 원소들의 시퀀스를 필터링한다.
1. 원소들의 시퀀스를 하나의 연산을 사용해 결합한다.
1. 원소들의 시퀀스를 컬랙션에 모은다.
1. 원소들의 시퀀스 중에서 특정 조건을 만족하는 원소를 찾는다.

## Stream 연산

### 생성 연산

스트림 API는 다음과 같은 데이터 소스에서 생성할 수 있다.

1. 컬랙션
1. 배열
1. 가변 매개변수
1. 지정된 범위의 연속된 정수
1. 특정 타입의 난수들.
1. 람다 표현식
1. 파일
1. 빈 스트림

### 중개 연산

생성 연산에 의해 생성된 스트림은 중개 연산을 통해 다른 스트림으로 변환된다.
중개 연산은 연속으로 연결해서 사용할 수 있다.

주요 메소드는 아래와 같다.

1. 스트림 필터링: filter(), distinct()
1. 스트림 변환: map(), flatMap()
1. 스트림 제한: limit(), skip()
1. 스트림 정렬: sorted()
1. 스트림 연산 결과 확인: peek()

#### filter()

주어진 조건에 맞는 요소만으로 구성된 새로운 스트림을 반환한다.

```java
    IntStream stream = IntStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    stream.filter( n -> n%2 !=0).forEach(System.out::println);
```

#### map()

스트림의 요소들을 주어진 함수의 인수로 전달하고 그 반환 값들로 이루어진 새로운 스트림을 반환한다.

```java
    Stream<String> stream = Stream.of("JAVA", "PYTHON", "GO", "RUBY", "JAVASCRIPT");
    stream.filter( s -> s.length() ).forEach(System.out::println);
```

#### sorted()

비교자를 이용하여 스트림의 요소들을 정렬한다.

```java
    Stream<String> stream = Stream.of("JAVA", "PYTHON", "GO", "RUBY", "JAVASCRIPT");
    stream.sorted().forEach(System.out::println);

    Stream<String> streamReverse = Stream.of("JAVA", "PYTHON", "GO", "RUBY", "JAVASCRIPT");
    streamReverse.sorted(Comparator.reverseOrder()).forEach(System.out::println);
```

### 최종 연산

중개 연산을 통해 변환된 스트림은 마지막 최종 연산을 통해 하나의 결과로 종하보딘다.
이떄 지연(lazy)되었던 모든 중개 연산들이 최종 연산 시에 모두 수행된다. 최종 연산이 완료된 스트림은 더이상 사용할 수 없다.
최종 연산의 주요 메소드는 아래와 같다.

1. 요소의 출력: forEach()
1. 요소의 소모: reduce()
1. 요소의 검색: findFirst(), findAny()
1. 요소의 검사: anyMatch(), allMatch(), noneMatch()
1. 요소의 통계: count(), min(), max()
1. 요소의 연산: sum(), average()
1. 요소의 수집: collect()

#### forEach()

보통 스트림의 모든 요소를 출력하는 용도르 사용된다.

#### reduce()

람다 함수를 인수로 받아 첫 번째와 두 번째 요소에 연산을 수행한 뒤 그 결과와 세 번째 요소를 가지고 다시 연산을 수행한다.
위의 방법으로 모든 요소를 소모하여 연산을 수행한다.

#### findFirst(), findAny()

스트림에서 첫 번째 요소를 참조하는 Optional 객체를 반환한다.


#### count(), min(), max()

해당 스트림의 총 개수를 long 타입 값으로 반환한다.
min/max는 가장 큰 값, 가장 작은 값의 요소를 참조하는 Optional 객체를 반환한다.

#### collect()

스트림의 요소들 다양한 형태의 Collectors 객체로 수집/반환한다.

1. 스트림을 배열이나 컬렉션으로 변환 : toArray(), toCollection(), toList(), toSet(), toMap()
1. 요소의 통계와 연산 메소드와 같은 동작을 수행 : counting(), maxBy(), minBy(), summingInt(), averagingInt() 등
1. 요소의 소모와 같은 동작을 수행 : reducing(), joining()
1. 요소의 그룹화와 분할 : groupingBy(), partitioningBy()


#### 메소드 표

스트림 API에서 사용할 수 있는 메소드들은 아래와 같다.

|메소드|설명|
|:--|:--|
| void forEach(Consumer<? super T> action)|스트림의 각 요소에 대해 해당 요소를 소모하여 명시된 동작을 수행함.|
| Optional<T> reduce(BinaryOperator<T> accumulator) | 처음 두 요소를 가지고 연산을 수행한 뒤, 그 결과와 다음 요소를 가지고 또다시 연산을 수행함. 이런 식으로 해당 스트림의 모든 요소를 소모하여 연산을 수행하고, 그 결과를 반환함.
| Optional<T> findFirst() | 해당 스트림에서 첫 번째 요소를 참조하는 Optional 객체를 반환함. |
| boolean anyMatch(Predicate<? super T> predicate) | 해당 스트림의 일부 요소가 특정 조건을 만족할 경우에 true를 반환함. |
| boolean allMatch(Predicate<? super T> predicate)	| 해당 스트림의 모든 요소가 특정 조건을 만족할 경우에 true를 반환함. |
| boolean noneMatch(Predicate<? super T> predicate)	| 해당 스트림의 모든 요소가 특정 조건을 만족하지 않을 경우에 true를 반환함. |
| long count()	| 해당 스트림의 요소의 개수를 반환함.|
| Optional<T> max(Comparator<? super T> comparator)	| 해당 스트림의 요소 중에서 가장 큰 값을 가지는 요소를 참조하는 Optional 객체를 반환함.|
|Optional<T> min(Comparator<? super T> comparator)	| 해당 스트림의 요소 중에서 가장 작은 값을 가지는 요소를 참조하는 Optional 객체를 반환함. |
|T sum() | 해당 스트림의 모든 요소에 대해 합을 구하여 반환함.|
| Optional<T> average()	| 해당 스트림의 모든 요소에 대해 평균값을 구하여 반환함. |
|<R,A> R collect(Collector<? super T,A,R> collector)	| 인수로 전달되는 Collectors 객체에 구현된 방법대로 스트림의 요소를 수집함.|


## Strema Pipeline 프로그래밍 핵심

스트림 파이프라인 프로그래밍의 핵심은 부작용 없는 함수 객체에 있다. 스트림뿐 아니라 스트림 관련 객체에 건네지는 모든 함수 객체가 부작용이 없는 '순수 함수'로 구성되어야 한다.
순수 함수란 입력만이 결과에 영향을 주는 함수를 말하며 다른 가변 상태를 참조하지 않고 함수도 외부의 상태를 변경하지 않는다.