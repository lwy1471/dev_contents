## EntityManager

### find 메소드

```Java
public <T> T find(Class<T> entityClass, Object primaryKey);
```

엔티티 클래스와 엔티티의 Primary key를 통해 DB에서 데이터를 검색한다.

이와 비슷한 기능으로 getReferenece() 메소드가 있다.
차이점은 데이터가 존재하지 않을 경우 EntityNotFoundException을 발생시킨다.

```Java
public <T> T getReference(Class<T> entityClass, Object primaryKey);
```
또한 gerReference는 호출시 프록시 객체를 리턴하며 실제 쿼리는 최초로 데이터에 접근하는 시점에 발생한다.



### persist 메소드
새로운 엔티티를 DB에 저장할 때 persist() 메소드를 사용한다.
persist() 메소르르 실행하면 JPA는 알맞은 insert 쿼리를 실행하는데, 실제 쿼리를 실행하는 시점은 엔티티 클래서의 **식별자**를 생성하는 규칙에 따라 달라진다.

코드에서 직접 식별자를 생성하는 경우 트랜잭션을 커밋하는 시점에 insert 쿼리를 실행한다.

auto_increment(@GeneratedValue)와 같이 DB 테이블에 데이터를 넣어야 식별자를 구할 수 있을 때에는 persist()를 실행하는 시점에 insert 쿼리를 실행한다.

### remove 메소드
엔티티 객체를 제거한다.
```Java
public void remove(Object entity);
```
트랜잭션 커밋 시점에 실제 삭제를 위한 delete 쿼리를 실행한다.


</br>
출처

[JPA 프로그래밍 입문](https://www.kame.co.kr/nkm/detail.php?tcode=299&tbook_jong=3)
