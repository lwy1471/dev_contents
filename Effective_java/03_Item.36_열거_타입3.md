## 비트 필드 대신 EnumSet을 사용하라.

상수들이 집합으로 사용되어 상태 값을 표현할 때 비트 필드를 사용하곤 한다.

옛날 방식
```java
public class Text{
    public static final int STYLE_BOLD = 1 << 0;          // 1 ,  00000001 (2)
    public static final int STYLE_ITALIC = 1 << 1;        // 2 ,  00000010 (2)
    public static final int STYLE_UNDERLINE = 1 << 2;     // 4 ,  00000100 (2)
    public static final int STYLE_STRIKETHROUGH = 1 << 4; // 8 ,  00001000 (2)

    public void applyStyle(int style) {
        
        if(sytle & STYLE_BOLD == SYTLE_BOLD) {
            // do something.
        }
        if(sytle & STYLE_ITALIC == SYTLE_ITALIC) {
            // do something.
        }
        if(sytle & STYLE_UNDERLINE == SYTLE_UNDERLINE) {
            // do something.
        }
        if(sytle & STYLE_STRIKETHROUGH == SYTLE_STRIKETHROUGH) {
            // do something.
        }
    }

}
```

클라이언트 코드
```java
    text.applyStyles(STYLE_BOLD | STYLE_ITALIC); // 1 | 2 == 3
```

비트 필드를 사용하면 비트별 연산을 사용해 합집합과 교집합 같은 집합 연산을 효율적으로 수행할 수 있다. 하지만 비트 필드는 정수 열거 상수의 단점을 그대로 지니며, 추가로 아래와 같은 단점이 존재한다.

1. 비트 필드 값이 그대로 출력되면 해석하기가 훨씬 어렵다.
2. 비트 필드를 순회하기가 까다롭다.
3. 최대 몇 비트가 필요한지를 API 작성시 미리 예측하여 적절한 타입을 선택해야한다.

EnumSet 클래스는 이러한 비트 필드의 단점들을 해결해준다.
EnumSet의 장점은 아래와 같다.
1. Set 인터페이스를 완벽히 구현한다.
2. 타입이 안전한다.
3. 다른 Set 구현체와도 함께 사용할 수 있다.
4. 내부가 bit 벡터로 구현되어있기 때문에 비트 필드에 비견되는 성능을 보여준다.

앞의 예를 열거 타입과 EnumSet을 사용하여 수정한 결과는 아래와 같다.

```java
public class Text {
    public enum Sytle { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

    // 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋다.
    public void applyStyles(Set<Sytle> styles) {
        ...
    }
}
```

또한, EnumSet은 집합 생성을 위한 정적 팩토리를 제공한다.
아래는 EnumSet 인스턴스를 건네는 클라이언트 코드이다.
```java

text.applyStle(EnumSet.of(Style.BOLD, Style.ITALIC));

```


</br>
출처

[이펙티브 자바 3판(Effective Java 3/E)](https://blog.insightbook.co.kr/2018/10/24/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-3%ED%8C%90effective-java-3-e/)
