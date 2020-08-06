## Entity 클래스

### @Entity 어노테이션과 @Table 어노테이션

Entity는 JPA가 DB 테이블에 보관할 대상이다.
Entity Manager는 Entity 단위로 데이터를 조회, 저장, 삭제한다.

엔티티를 설정할 때에는 아래와 같이 @Entity 어노테이션을 붙인다.

```Java
@Entity
public class Member {
    ...
}
```

Entity를 DB와 매핑할 때 @Table 어노테이션을 사용하여 테이블을 지정할 수 있다.

```Java
@Entity
@Table(name="tbl_member")
public class Member {
    ...
}
```

Entity 클래스는 아래와 같은 제약 조건이 있다.
1. 인자가 없는 기본 생성자를 제공해야한다. 따라서 인자를 가진 생성자가 필요하다면 반드시 기본 생성자도 함께 정의해야한다.
2. 기본 생성자의 접근 범위는 public이나 default 이어야 한다.
3. 인터페이스나 열거타 타입을 엔티티로 지정할 수는 없다.
4. Entity 클래스가 final 이면 기능이 제약이 생긴다. JPA는 Entity 클래스를 기반으로 프록시 객체를 생성하는데 엔티티 클래스가 final이면 프록시를 생성할 수 없다.
5. Entity의 영속 대상 필드나 메서드가 final 이면 제약이 생긴다.


### @Id 어노테이션

Entity는 반드시 식별자가 있어야한다. 식별자를 설정하기 위해 @Id 어노테이션을 사용한다.
주로 DB 테이블의 Primay key 칼럼과 매핑할 필드에 @Id 어노테이션을 붙인다.

```Java
@Entity
@Table(name="tbl_member")
public class Member {

    @id
    private String email;
    ...
}
```

### 식별자의 생성 방식

#### 직접 할당 방식
어플리케이션 코드에서 직접 식별자를 생성하는 경우 식별자 필드에 @Id 어노테이션을 붙이면 된다. 주로 식별자가 정해진 규칙으로 생성될 때 사용된다.

#### 식별 칼럼 방식
mysql의 auto_increment처럼 DB가 제공하는 식별 칼럼을 사용하여 식별자를 생성할 경우 아래와 같이 @GeneratedValue 어노테이션을 통해 설정할 수 있다.

```Java
@Entity
@Table(name="tbl_member")
public class Member {

    @id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String email;
    ...
}
```



### @Basic 어노테이션과 영속 대상에서 제외하기

Entity의 필드 중 영속 대상을 지정하기 위해 @Basic 어노테이션을 붙이면 된다.
기본적으로 모든 Entity의 필드는 영속 대상이고 @Basic 어노테이션은 생략 가능하다.

필드를 영속 대상에서 제외하기 위해서는 필드에 transient 키워드를 사용하면 된다.

```Java
@Entity
@Table(name="tbl_member")
public class Member {

    @id
    private String email;

    transient private long timestamp;
    ...
}
```

또한 날짜와 시간 타입을 지원하는 @Temporal 어노테이션,
열거 타입을 지원하는 @Enumerated 어노테이션이 존재한다.


### @Column 어노테이션

필드에 @Column 어노테이션을 사용하면 테이블에 저장될 칼럼명을 변경할 수 있다.

```Java
@Entity
@Table(name="tbl_member")
public class Member {

    @id
    private String email;

    @Column(name = "address")
    private String addess;

    transient private long timestamp;
    ...
}
```
> insertable, updatable 속성을 통해 해당 칼럼을 읽기 전용으로 변경할 수 있다. 평소에는 사용되지 않지만
다대일, 다대다 관계에서 사용될 수 있다.


### @Access 어노테이션

식별자를 지정하는 @Id 어노테이션은 필드뿐만아니라 getter 메소드에도 적용할 수 있다.
```Java
@Entity
@Table(name="tbl_member")
public class Member {


    private String email;

    @Id
    public String getEmail() {
        return this.email;
    }
    ...
}
```

위와 같이 getter 메소드에 @Id 어노테이션을 적용하면 JPA는 DB로 불러온 데이터를 각각의 Entity 필드가 아닌 getter/setter 메소드를 이용하여 데이터를 처리한다.

@Id 어노테이션을 필드 또는 getter 메소드에 붙이는 방식에 따라 모든 칼럼 데이터를 읽고 쓰는 방식이 변경된다. 
하지만 특정 칼럼에 JPA가 읽고 쓰는 접근 방식을 지정하고 싶을 때에는 @Access 어노테이션을 사용할 수 있다.

```Java
@Entity
@Table(name="tbl_member")
public class Member {


    private String email;

    // 1. 기본적으로 모든 칼럼 데이터를 getter/setter 메소드를 통해 처리한다.
    @Id
    public String getEmail() {
        return this.email;
    }


    // 2. name 칼럼은 name 필드를 통해 데이터를 처리한다.
    @Access(AccessType.PROPERTY)
    @Column(name = "name")
    private String name;

    ...
}
```

