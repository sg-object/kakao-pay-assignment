# kakao-pay-assignment

## 주요 기능
* MongoDB를 활용한 쿠폰 관리
* JWT를 이용한 인증 처리

## 개발 환경
* OS : Windows 10 Pro
* DB : MongoDB 4.2.7
* IDE : Eclipse 2020-03
* ETC : Docker 19.03.8

## Version
* Spring Boot : 2.3.0.RELEASE
* JJWT : 0.9.1
* Swagger : 2.9.2
* MongoDB Driver : 4.0.3

## 문제해결 전략
1. **쿠폰 상세 정보 collection 분리**  
MongoDB와 Elasticsearch 같은 NoSQL은 Join 기능을 제공하지 않기 때문에 RDBMS에서 Join으로 처리하는 데이터를 Embedded Document나 Nested Field를 이용해 Root Document가 모든 데이터를 가지고 있다. 하지만 MongoDB의 경우 단일 Document가 RAM을 많이 사용하는 것을 방지하기 위해 Document의 최대 용량을
16MB로 제한한다. 그래서 Embedded Document에 저장되는 정보를 최소화하고 그외 추가 정보는 다른 collection에 저장하고, 추가 정보가 필요한 경우 
Embeddee Document에 저장 되어 있는 다른 collection 정보로 추가 정보를 조회하는 방법을 사용했다.
이 프로젝트에서는 issues collection의 collection, coupon field를 이용해 coupons_yyyy_MM_dd collection에서 추가 정보를 가져 올 수있다.
collection 분리를 coupon의 앞자리의 Hash값으로 대역을 나누어서 처리하는 방법도 생각했지만, 특정 값으로 나누어 버리면 자칫 잘못하면 정보가 몇몇 특정 collectiondmfh 
쏠리는 경우가 발생 할수도 있다고 판단해 관리하기가 비교적 쉬운 날짜 기준으로 collection을 분리했다.
(coupons_yyyy_MM_dd는 쿠폰 발급 날짜로 생성한 collection)  

2. **만료 알림과 처리를 위하 collection 분리**  
쿠폰 상세 정보를 다른 collection으로 분리 한것과 같이 쿠폰의 만료 날짜 기준으로 collection을 나누었고, 알림 메세지 발송시 알림 발송 기준이 되는 만료전 
날짜값의 collection 정보를 이용해 알림을 처리한다.

## 추가 고려 및 고도화 항목
1. **MongoDB Replica Set 구성**  
현재 프로젝트는 단일 MongoDB Server를 기준으로 개발을 진행했다. 그러나 NoSQL의 특성상 RDBMS와 같은 Transaction 처리가 완벽하지 않다. 
4.0 버전 이전까지는 Single Document Transaction을 지원했지만, 4.0 이후 버전은 Multi Document Transaction 기능이 추가 되었다.
그러나 Multi Document Transaction은 단일 서버에서는 작동이 않되고, Replica Set이라는 MongoDB Cluster를 구성해야 사용할수 있다.
Replica Set은 메인 저장소 역할을 하는 Master와 백업 저장소 역할을 하는 Slave, Cluster를 관리하는 Arbiter로 구성된다. 
지금 진행되고 있는 프로젝트 구조상 Multi Document Transaction 기능이 꼭 필요하기 때문에 Replica Set 구성은 필수다.

2. **Scheduler Clustering**  
현재 프로젝트에서는 2개의 Spring Scheduler가 동작한다. 하지만 실제 프로젝트의 경우 단일 서버로만 운영하는 곳보다는 서버 이중화를 통해 안정적으로 운영을 
하기 때문에 Scheduler도 Clustering이 되어야 한다. 대표적으로 DB를 활용한 Quartz Schedule Cluster 구성이 있고, Spring에서도 Spring Scheduler Lock을 
이용해 구성할수 있다.

## 사전 준비 (MongoDB 설치)
1. **Docker 명령어**  
docker run --name mongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=P@ssw0rd -d mongo:4.2.7

2. **Docker Compose**  
Project 폴더에서 /src/main/resources/docker/docker-compose.yml 경로의 docker-compose.yml 파일 사용.  
명령어 : docker-compose up -d

## MongoDB Collection Diagram
![K-20200605-52236-8](https://user-images.githubusercontent.com/49360550/83811341-988f9880-a6f4-11ea-9be7-61200ede3c9f.jpg)

## Scheduler
1. ExpireNoticeTask  
발급된 쿠폰중 만료 3일전에 알림 메세지를 발송하는 Scheduler (실제 발송은 이루어지지 않으며 Log로만 출력)  
2. DeleteIssuedCouponTask  
매일 1번씩 실행되고, 전날 생성 및 발급이 되었으면 coupon collection에서 해당 쿠폰은 삭제 처리

## Test (Swagger)
* Test는 Swagger를 이용
* Project 구동 후 Swagger URL 접속
* URL : http://localhost:8080/swagger-ui.html  

![K-20200605-61723-2](https://user-images.githubusercontent.com/49360550/83811346-99c0c580-a6f4-11ea-9af8-2a2a72c44b21.jpg)

