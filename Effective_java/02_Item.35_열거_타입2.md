## ordial 메서드 대신 인스턴스 필드를 사용하라

대부분의 열거 타입 상수는 하나의 정숫값에 대응된다.
그리고 열거 타입은 모든 상수가 열거 타입에서 몇 번째 위치인지를 반환하는 ordial 메소드를 가지고 있다.

이런 이유로 열거 타입 상수와 연결된 정수값을 사용하기 위해 ordial 메소드를 이용하는 경우가 있는데, 이는 아래와 같은 문제점이 있다.

1. 상수 선언 순서를 바꾸면 문제가 발생한다.
2. 이미 사용 중인 정수와 값이 같은 상수는 추가할 방법이 없다.
3. 값을 중간에 비워둘 수 없다.


잘못된 방법
```java
    public enum Ensemble {
        SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET, OCTET, NONET, DECTET;

        public int numberOfMusicians() {return ordial() + 1;}
    }
```

올바른 방법
```java
    public enum Ensemble {
        SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5), SEXTET(6), SEPTET(7), OCTET(8), NONET(9), DECTET(10);

        private final int numberOfMusicians;

        Ensemble(int size) {this.numberOfMusicians = size;}
        public int numberOfMusicians() {return numberOfMusicians;}
    }
```

</br>
출처

[이펙티브 자바 3판(Effective Java 3/E)](https://blog.insightbook.co.kr/2018/10/24/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-3%ED%8C%90effective-java-3-e/)
