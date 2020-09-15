
#### select

```sql
select count(*) from ratings ;
```

#### load data 

```sql
load data inpath '/dualcore/ratings_2013.txt' into table ratings ;

select count(*) from ratings ;
```

#### create table
```sql
create external table employees ( 
    emp_id string, 
    fname string, 
    lname string, 
    address string, 
    city string, 
    state string, 
    zipcode string, 
    job_title string, 
    email string, 
    active string, 
    salary int 
) row format delimited fields terminated by '\t' 
location '/dualcore/employees'; 

select job_title, count(*) as num from accounting.employees group by job_title order by num desc limit 3 ; 
```


#### refresh metadata - impala sql
```sql
invalidate metadata ;
```

#### alter table column name

```sql
alter table suppliers change company name STRING ;
```


#### alter table name

```sql
alter table suppliers rename to vendors ;
```

#### create table with partition 
```sql 
create external table ads ( 
    campaign_id string, 
    display_date string, 
    display_time string, 
    keyword string, 
    display_site string, 
    placement string, 
    was_clicked tinyint, 
    cpc int 
) 
partitioned by (network tinyint) 
row format delimited fields terminated by '\t' location '/dualcore/ads' ;


# add partition 
alter table ads add partition (network=2) ;
		
# add partition
alter table ads add partition (network=1) ;
```


#### describe partition

```sql
show partitions ads 
```

#### insert data into partition
```sql
load data inpath '/dualcore/ad_data2/' into table ads partition (network=2) ;
		
load data inpath '/dualcore/ad_data1/' into table ads partition (network=1) ;
```

#### get partitioned data with select

```sql
select count(*) from ads where network=1 ;


select count(*) from ads where network=2 ;

```

#### create table from parquet-formated data
```sql
create external table latlon like parquet '/dualcore/latlon/part-m-00000.parquet'
stored as parquet ;
```

#### load data from parquet-formated data

```sql
load data inpath '/dualcore/latlon' into table latlon ;
		
load data inpath '/dualcore/lastion' into table latlon; 
	
```

1. 
select count(*) as num, name from products p, order_details d, orders o where p.prod_id = d.prod_id and d.order_id = o.order_id group by name order by num DESC limit 3;

select *, count(*) as num from products p, order_details d, orders o where p.prod_id = d.prod_id and d.order_id = o.order_id group by name order by num desc limit 3;

2.
select d.order_id, sum(p.price) as num from products p, order_details d where p.prod_id = d.prod_id group by order_id order by num desc limit 10;


3.
select to_date(o.order_date) as dates, sum(p.price) as revenue, sum(p.price)-sum(p.cost) as profit from products p, orders o, order_details d
where p.prod_id = d.prod_id and o.order_id = d.order_id 
group by dates;

bonus.
select year(o.order_date) as o_year, month(o.order_date) as o_month, to_date(o.order_date) as o_date, sum(p.price)-sum(p.cost) as profit 
from products p, orders o, order_details d
where p.prod_id = d.prod_id and o.order_id = d.order_id 
group by o_month, o_year, o_date
order by o_year, o_month, profit DESC
limit 20;