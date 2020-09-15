## 추가 쿼리 기능

### 수정 쿼리

아래는 Update를 이용해서 데이터를 수정하는 JPQL이다. 기존 SQL과의 차이점은 Select 쿼리와 마찬가지로 칼럼이 아닌 엔티티의 속성을 이용하는 것이다.

```java
em.getTransaction().begin();
Query q = em.createQuery("update Hotel h set h.name = :newName where h.name =: oldName");
q.setParameter("newName", "베스트웨스턴 구로");
q.setParameter("oldName", "베스트웨스턴 구로호텔");
q.executeUpdate();
em.getTransaction().commit();
```

JPQL의 update 쿼리를 사용하려면 트랜잭션 범위 안에서 실행해야 한다. 트랜잭션 범위 안에서 실행하지 않을 경우 JPQL을 실행할 떄 익셉션이 발생한다.

아래는 크리테리아를 사용한 예제이다.

```Java
em.getTransaction().begin();

CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaUpdate<Hotel> cu = cb.createCriteriaUpdate(Hotel.class);
Root<Hotel> h = cu.from(Hotel.class);
cu.set(h.get("name"), "베스트웨스턴 구로");
cu.set(h.get("grade"), Grade.STAR3);
cu.where(cb.equeal(h.get("name"), "베스트웨스턴 구로호텔"));

Query q = em.createQuery(cu);
query.executeUpdate();

em.getTransaction().commit();
```

### 삭제 쿼리

JPQL 삭제 쿼리는 아래와 같다.
```java
em.getTransaction().begin();
Query q = em.createQuery("delete Hotel h where h.name =: name");
q.setParameter("name", "베스트웨스턴 구로호텔");
q.executeUpdate();
em.getTransaction().commit();
```

크리테리아를 사용한 삭제 쿼리는 아래와 같다.

```Java
em.getTransaction().begin();

CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaDelete<Hotel> cu = cb.createCriteriaDelete(Hotel.class);
Root<Hotel> h = cu.from(Hotel.class);
cu.where(cb.equeal(h.get("name"), "베스트웨스턴 구로호텔"));

Query q = em.createQuery(cu);
query.executeUpdate();

em.getTransaction().commit();
```

### 수정/삭제 쿼리와 영속 컨텍스트

수정/삭제 쿼리를 실행할 때 주의할 점은 영속 컨텍스트에 보관된 객체는 이 쿼리에 영향을 받지 않는다는 점이다.
영속 컨텍스트에 보관된 엔티티에 수정 쿼리의 결과를 반영하고 싶다면 refresh() 메소드를 사용해야한다.

잘못된 쿼리
```java
em.getTransaction().begin();
Hotel hotel = em.find(Hotel.class, "H100-01");

Query q = em.createQuery("update Hotel h set h.name = :newName where h.id =: id");
q.setParameter("name", "베스트웨스턴 구로호텔");
q.setParameter("id", "H100-01");
q.executeUpdate();

hotel.getName() // 베스트웨스턴 구로호텔
em.getTransaction().commit();
```

올바른 쿼리
```java
em.getTransaction().begin();
Hotel hotel = em.find(Hotel.class, "H100-01");

Query q = em.createQuery("update Hotel h set h.name = :newName where h.id =: id");
q.setParameter("name", "베스트웨스턴 구로호텔");
q.setParameter("id", "H100-01");
q.executeUpdate();

em.refresh(hotel); // DB에서 데이터를 읽어와 엔티티에 반영
hotel.getName() // 베스트웨스턴 구로호텔
em.getTransaction().commit();
```


### 네이티브 쿼리

오라클 힌트처럼 DBMS에 특화된 기능이 필요하면 SQL을 사용해야한다. 이럴 때 필요한 것이 네이티브 쿼리이다.
네이티브 쿼리는 JPA에서 SLQ을 그대로 실행하고 그 결과를 조회할 수 있게 도와준다.

네이티브 쿼리를 실행한 결과는 Object로 받거나 엔티티 매핑으로 받을 수 있다.

#### Object 배열로 결과를 조회하는 네이티브 쿼리

아래는 네이티브 쿼리를 실행하고 Object 배열로 결과를 받는 예제이다.
```java
Query q = em.createNativeQuery(
    "select id, name, grade from hotel where grade = :grade order by id asc limit :first, :max"
    );

q.setParameter("grade", "STAR4");
q.setParameter("first", 0);
q.setParameter("max", 1);


List<Object[]> results = q.getResultList();
for(Object[] row : results) {
    String id = (String) row[0];
    String name = (String) row[1];
    String grade = (String) row[2];
    System.out.printf("%s %s %s\n", id, name, grade);
}
```

#### 엔티티 매핑으로 결과 조회
엔티티 클래스를 사용하면 네이티브 쿼리를 실행한 결과를 엔티티 객체로 조회할 수 있다. 예제는 아래와 같다.
```java
em.getTransation().begin();

Query q = em.createNativeQuery(
    "select id, name, grade from hotel where grade = :grade order by id asc",
    Hotel.class);

q.setParameter("grade", "STAR4");

List<Hotel> results = q.getResultList();
for(Hotel hotel : results) {
    System.out.printf("%s %s %s\n", hotel.getId(), hotel.getName(), hotel.getGrade());
}

em.getTransaction().commit();
```

#### 네임드 네이티브 쿼리 사용
네이티브 쿼리도 JPQL과 마찬가지로 네임드 네이티브 쿼리를 사용할 수 있다.
아래는 어노테이션을 이용해서 네임드 네이티브 쿼리를 실행하는 예제이다.

```java

@NamedQuery(
    name="Hotel.all", resultCLass = Hotel.class,
    query = "select h from Hotel order by id asc"
)
public class Hotel{
    @id
    private String id;
    ...
}
```


### 하이버네이트 @Subselect

하이버네이트는 JPA 확장 기능으로 @Subselect를 제공한다. @Subselect는 쿼리 결과를 @Entity로 매핑할 수 있는 기능이다.

```java
@Entity
@Immutable
@Subselect("select s.id, s.name, d.dours_op as hoursOperation from sight s, sight_detail d"+
           "where s.id = d.sight_id")
@Synchronize({"Sight", "sight_detail"})
public class BestSightSummary {
    @Id
    private Long id;
    private String name;
    private String hoursOperation;
    ...
}
```

@Subselect는 하이버네이트 전용 어노테이션이다. 이 어노테이션을 사용하면 테이블이 아닌 쿼리 결과를
@Entity로 매핑할 수 있다. 하이버네이트는 엔티티를 실제 테이블에서 매핑하는 것이 아니라 @Subselect의 결과를 매핑한다.
마치 DBMS가 여러 테이블을 조인해서 조회한 결과를 뷰를 사용해서 보여주는 것과 비슷하다.

@Immutable, @Synchronize 또한 하이버네이트 전용 어노테이션이다. 각 어노테이션의 역할은 아래와 같다.

- @Immutable: @Subselect와 매핑된 엔티티는 실제 테이블이 없기 때문에 update 쿼리를 실행할 수 없다. @Immutable 어노테이션을 사용하면 엔티티의 변경 내역을 DB에 반영하지 않고 무시한다.
- @Synchronize: @Subselect 쿼리를 실행하기 전에 명시한 엔티티의 변경 내역을 검사하여 플러쉬한 뒤 Subselect 쿼리를 실행한다.

아래는 @Subselect 쿼리 사용 예제이다.

```java
Sight sight = em.find(Sight.class, 1L);
sight.setName("새이름");

TypedQuery<BestSightSummary> q = em.createQuery(
    "select s from BestSightSummary s where s.id = :id", BestSightSummary.class
);

q.setParameter("id", 1L);
List<BestSightSummary> summaries = q.getResultList();

)

```

위의 코드를 실행하면 아래와 같은 형태로 sql이 실행된다. @Subselect의 쿼리가 from 절의 서브 쿼리로 사용되는 것을 볼 수 있다.

```sql
select bs.id, bs.hoursOperation, bs.name
from (
    select s.id, s.name, d.dours_op as hoursOperation from Sight s, sight_detail d where s.id = d.sight_id
) bs
where bs.id=?
```


</br>
출처

[JPA 프로그래밍 입문](https://www.kame.co.kr/nkm/detail.php?tcode=299&tbook_jong=3)
