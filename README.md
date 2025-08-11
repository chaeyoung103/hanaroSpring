
🛒 쇼핑몰 백엔드 서비스
하나은행 Digital hana 路 7기 백엔드 실습 과제입니다.

✨ 주요 기능
👨‍💻 관리자 (Admin)
JWT 기반 로그인

상품 관리: 상품 등록, 전체 조회, 상세 조회, 수정, 삭제 (이미지 포함)

재고 관리: 특정 상품의 재고 수량 조정

주문 관리: 모든 사용자의 주문 내역을 상품명, 주문 상태로 검색 및 조회

매출 통계: 특정 날짜의 일일 매출 및 상품별 판매 통계 조회

회원 관리: 전체 회원 목록 조회 및 특정 회원 삭제

👤 사용자 (User)
JWT 기반 회원가입 및 로그인

상품 조회: 키워드를 통한 상품 검색 및 상세 정보 조회

장바구니: 장바구니에 상품 추가, 조회, 수량 변경, 삭제

주문: 장바구니 기반으로 주문 생성 및 본인 주문 내역 조회

🛠️ 기술 스택
언어: Java 21

프레임워크: Spring Boot 3

데이터베이스: MySQL, JPA(Hibernate)

쿼리: QueryDSL

인증/인가: Spring Security, JWT

빌드 도구: Gradle

API 문서화: Swagger (OpenAPI 3)

기타: Spring Batch, Spring Scheduler, Lombok

🚀 실행 방법
1. 사전 준비
   MySQL 데이터베이스가 설치되어 있어야 합니다.

hanarodb 스키마(데이터베이스)를 생성해주세요.

SQL

CREATE DATABASE hanarodb;
2. 설정
   src/main/resources/application.properties 파일을 생성합니다.

아래 내용을 복사하여 붙여넣고, 자신의 MySQL 계정 정보에 맞게 수정합니다.

Properties

# SERVER
server.port=8080

# DATABASE
spring.datasource.url=jdbc:mysql://localhost:3306/hanarodb
spring.datasource.username=[DB_사용자명]
spring.datasource.password=[DB_비밀번호]
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT SECRET KEY
jwt.secret.key=[32자 이상의 JWT 시크릿 키]

# FILE UPLOAD DIR
file.upload-dir=src/main/resources/static/

# ACTUATOR
management.server.port=9001
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoint.shutdown.enabled=true
3. 실행
   IntelliJ 등 IDE에서 HanaroApplication.java 파일을 실행합니다.

또는 터미널에서 ./gradlew bootRun 명령어를 실행합니다.

4. API 문서 확인
   애플리케이션 실행 후, 웹 브라우저에서 http://localhost:8080/swagger-ui/index.html 로 접속하여 모든 API를 확인하고 테스트할 수 있습니다.

✅ 주요 기능 확인 방법
1. 더미 데이터 생성
   src/test/java/com/example/hanaro/repository/ 경로의 UserRepositoryTest.java 와 ProductRepositoryTest.java 를 실행하면 관리자, 일반 유저, 상품 더미 데이터가 DB에 생성됩니다.

2. 로그 (Log) 확인
   콘솔 로그: IDE의 실행(Run) 창에서 실시간 로그를 확인할 수 있습니다.

파일 로그: 프로젝트 루트 경로에 logs 폴더가 생성되며, 그 안에 아래 두 파일이 생성됩니다.

business_product.log: 상품 관련 비즈니스 로직 로그

business_order.log: 주문 관련 비즈니스 로직 로그

3. 스케줄러 (Scheduler) 확인
   로그 확인: 애플리케이션 실행 중 콘솔 로그를 보면, 설정된 주기(5분, 15분, 1시간)마다 아래와 같은 로그가 찍히는 것을 확인할 수 있습니다.

결제완료->배송준비 스케줄러 실행

[주문 상태 변경] 결제완료 -> 배송준비: 총 N건 처리

DB 확인: order 테이블의 status와 updated_at 필드가 시간이 지남에 따라 자동으로 변경되는 것을 확인할 수 있습니다.

4. 배치 (Batch) 확인
   자동 실행: 매일 자정(00:00:00)에 자동으로 실행되며, BatchScheduler의 실행 로그가 콘솔에 남습니다.

수동 실행: 테스트를 위해 아래 API를 호출하여 즉시 실행할 수 있습니다. (관리자 권한 필요)

GET http://localhost:8080/batch/run (어제 날짜로 실행)

GET http://localhost:8080/batch/run?date=YYYY-MM-DD (특정 날짜로 실행)

결과 확인: 실행 후 daily_sales_stats와 daily_product_stats 테이블에 해당 날짜의 통계 데이터가 생성되었는지 확인합니다.

5. 액츄에이터 (Actuator) 확인
   주의: Actuator는 별도의 포트(9001)에서 실행됩니다.

웹 브라우저나 API 테스트 도구로 아래 주소에 접속하여 서버 상태를 확인할 수 있습니다.

서버 상태 확인: http://localhost:9001/actuator/health

성능 지표 확인: http://localhost:9001/actuator/metrics

환경 변수 확인: http://localhost:9001/actuator/env

스프링 빈 확인: http://localhost:9001/actuator/beans