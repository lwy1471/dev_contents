#

## @Override 어노테이션을 일관되게 사용하라.

@Override 어노테이션을 일관되게 사용하면 여러가지 버그들을 예방해준다.

아래의 예제를 살펴보자. 아래의 예제는 영어 알파벳 2개로 구성된 문자열을 표현한다.

```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second){
        this.first = first;
        this.second = second;
    }

    public boolean equals(Bigram b){
        return b.first == first && b.second == second;
    }

    public int hashCode(){
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for(int i=0; i<10; i++){
            for(char ch = 'a'; ch<='z'; ch++){
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size());
    }
}

```

Main 메서드를 살펴보면 알파벳 a-z를 10번 순회하며 Set에 add한다. Set은 중복을 허용하지 않아 결과는 26이 되어야하지만 실제 결과는 260이 나온다.
이 오류의 원이는 equals 메서드에 있다. 코드 작성자는 Bigram 중복 체크를 위해 equals와 hashCode를 재정의 하려고 하였다.
하지만 실제로 Object클래스의 equals(Object o)를 재정의 한 것이 아닌 Overloading을 해버렸다. 실제 재정의를 위한 코드는 아래와 같다.

```java

@Override
public boolean equals(Object o) {
    if(!(o instanceof Biagram))
        return false;
    Biagram b = (Biagram) o;
    return b.first == first && b.second == second;
}
```

재정의가 필요한 메소드에 @Override 어노테이션을 작성하면 위와 같은 Human Error를 방지할 수 있다.

재정의한 모든 메서드에 @Override 어노테이션을 의식적으로 달면 여러분이 실수했을 때 컴파일러가 바로 알려줄 것이다.
예외는 한가지 뿐이다. 구체 클래스에서 상위 클래스의 추상 메서드를 재정의한 경우엔 이 어노테이션을 달지 않아도 된다.

</br>
출처

[이펙티브 자바 3판(Effective Java 3/E)](https://blog.insightbook.co.kr/2018/10/24/%EC%9D%B4%ED%8E%99%ED%8B%B0%EB%B8%8C-%EC%9E%90%EB%B0%94-3%ED%8C%90effective-java-3-e/)
