## int 상수 대신 열거 타입을 사용하라

열거 타입은 일정 개수의 상수 값을 정의한 다음 그 외의 값은 허용하지 않는 타입이다.


가장 쉬운 선언 방법은 아래와 같은 정수 열거 패턴이 있다.

### 정수 열거 패턴 (int enum pattern)
```java
public static final int MERCURY = 0;
public static final int VENUS = 1;
public static final int EARTH = 2;
public static final int MARS = 3;
public static final int JUPITER = 4;
public static final int SATURN = 5;
public static final int URANUS = 6;
public static final int NEPTUNE =7;
```

위의 패턴은 작성하기 쉽지만 많은 단점이 있다.

1. 평범한 상수를 나열한 것이라 컴파일하면 그 값이 클라이언트 파일에 그대로 새겨진다.
2. 정수 상수를 문자열로 출력하기가 다소 까다롭다.
3. 열거 그룹에 속한 상수를 순회하는 방법이 마땅치 않다.

 JAVA는 **Enum type**을 제공하여 위의 단점을 보완한다.




### Enum Type

열거 타입의 특징 및 장점은 아래와 같다.

1. 열거 타입 자체는 클래스이며, 상수 하나당 자신의 인스턴스 하나씩 만들어 public static final 필드로 공개한다.
2. 열거 타입은 public 생성자를 제공하지 않으므로 사실상 final이다.
3. 열거 타입으로 만들어진 인스턴스들은 딱 하나씩만 존재한다.
(싱글턴은 원소가 하나뿐인 열거 타입이라 할 수 있고, 열거 타입은 싱글턴을 일반화한 형태라고 볼 수 있다.)
4. 상수 값들을 순회할 수 있다.
5. 타입 컴파일타임 안정성을 제공한다.
6. 메서드나 필드를 추가할 수 있다.




### 열거 타입에 메서드나 필드를 사용하는 예
위 예제에서 보인 태양계 행성들을 열거 타입으로 구현해보자.
각 행성에는 질량과 반지름이 있고 질량과 반지름으로 표면 중력을 계산할 수 있다.
코드는 아래와 같다.




```Java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6), 
    EARTH(5.975e+24, 6.378e6),
    MARS(6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN(5.685e+26, 6.027e7),
    URANUS(8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    private final double mass;
    private final double radius;

    private final double surfaceGravity;

    // 중력상수(단위: m^3 / kg s^2)
    private static final double G = 6.67300E-11;

    // 생성자
    Planet(double mess, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass() { return mass; }
    public double radius() { return radius; }
    public double surfaceGravity() { return surfaceGravity; }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity;
    }
}

```
*열거 타입은 근본적으로 불변이라 모든 필드는 final이어야 한다.*


위와 같이 상수를 Enum type으로 작성하면 values 메소드를 통해 순회할 수 있다.

```Java
public class WeightTable {
    public static void main(String[] args) {
        double EARTH_WEIGHT = 69.3;
        double mass = EARTH_WEIGHT / Planet.EARTH.surfaceGravity();
        for (Planet p : Planet.values()) {
            System.out.println("%s에서의 무게는 %f입니다. \n", p, p.surfaceWeight(mass));
        }
    }
}
```

아래는 토비 스프링에서 사용자 레벨 관리를 위해 Enum type을 사용한 예제이다.

```Java
public enum Level {
  GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);

  private final int value;
  private final Level nextLevel;

  Level(int value, Level nextLevel) {
    this.value = value;
    this.nextLevel = nextLevel;
  }

  public int intValue() {
    return value;
  }

  public Level nextLevel() {
    return this.nextLevel;
  }
}


public class User {
    
    int login;
    int recommend;

    Level level;

    public void upgradeLevel() {
        Level nextLevel = this.level.nextLevel();

        if (nextLevel == null)
            throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");

        else
            this.level = nextLevel;
        }
    }

    ...
}
```


</br>
출처

[이펙티브 자바 3판(Effective Java 3/E)](https://blog.insightbook.co.kr/2018/10/24/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-3%ED%8C%90effective-java-3-e/)
