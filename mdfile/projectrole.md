4명 코드 역할분담
1번 팀원: 회원 / 로그인 / 매너온도 코드
담당 기능
회원가입
로그인
내 정보
매너온도
담당 클래스
User.java
Temperature.java
UserManager.java
LoginPanel.java
SignupPanel.java
MyPagePanel.java
구현할 것
- 회원 객체 생성
- 로그인 검증
- 사용자 정보 저장
- 매너온도 증가/감소
- 마이페이지에 온도 표시
2번 팀원: 물품 / 검색 / 등록 코드
담당 기능
물품 등록
물품 목록
검색
필터
정렬
물품 상세
담당 클래스
Item.java
ItemManager.java
Location.java
TimeSlot.java
ItemListPanel.java
ItemRegisterPanel.java
ItemDetailPanel.java
구현할 것
- 물품 객체 생성
- 물품 등록
- 물품 목록 출력
- 이름 검색
- 카테고리 필터
- 가격순/최신순 정렬
- 상세 페이지 연결
3번 팀원: 거래 / 상태 패턴 / 패널티 코드
담당 기능
대여 요청
예약 승인
대여 시작
반납 확인
문제 신고
패널티 적용
담당 클래스
Transaction.java
Rental.java

ItemState.java
AvailableState.java
ReservedState.java
RentedState.java

PenaltyPolicy.java
LatePenalty.java
DamagePenalty.java
NoReturnPenalty.java

TransactionPanel.java
ReportPanel.java
구현할 것
- Available → Reserved
- Reserved → Rented
- Rented → Available
- 연체 패널티
- 파손 패널티
- 미반납 패널티
- 거래 상태 관리
4번 팀원: GUI 메인 / 화면 전환 / 관리자 코드
담당 기능
메인 페이지
전체 화면 연결
찜하기
문의하기
관리자 페이지
신고 관리
사용자 관리
거래 관리
담당 클래스
Main.java
MainFrame.java
MainPanel.java
NavigationManager.java

Wishlist.java
Inquiry.java
Admin.java
AdminPanel.java
ReportManager.java
구현할 것
- 프로그램 실행 화면
- 버튼 누르면 화면 전환
- 찜하기 기능
- 문의하기 기능
- 관리자 신고 목록 확인
- 사용자 관리
- 거래 관리
역할분담표
팀원   코드 담당 영역   핵심 책임
1번   User / Login / Temperature   회원과 매너온도
2번   Item / Search / Register   물품 등록과 조회
3번   Rental / State / Penalty   거래 흐름과 디자인 패턴
4번   GUI / Admin / Navigation   화면 연결과 관리자 기능
같이 맞춰야 하는 공통 규칙
1. 패키지 구조 통일
src/
 ├─ main/
 │   └─ java/
 │       ├─ app/
 │       │   └─ Main.java
 │       ├─ domain/
 │       ├─ manager/
 │       ├─ state/
 │       ├─ strategy/
 │       ├─ transaction/
 │       └─ ui/
2. 클래스 이름 통일

각자 마음대로 이름 짓지 말고 이렇게 고정.

User
Temperature
Item
Location
TimeSlot
Review
Rental
Transaction
ItemState
PenaltyPolicy
3. 데이터 저장 방식 통일

처음에는 DB 말고 ArrayList로 통일.

private ArrayList<Item> items = new ArrayList<>();
private ArrayList<User> users = new ArrayList<>();
private ArrayList<Rental> rentals = new ArrayList<>();
개발 순서
1. 4명이 각자 클래스 뼈대 만들기
2. User, Item, Rental부터 먼저 연결
3. GUI 화면 만들기
4. 버튼 이벤트 연결
5. 상태 변경 테스트
6. 매너온도 변화 테스트
7. 관리자 페이지 연결
8. 최종 발표 시연
팀별 발표 포인트
1번
저는 회원 관리와 매너온도 기능을 구현했습니다.
Temperature 클래스를 별도로 분리해서 User가 온도 값을 직접 수정하지 않도록 했습니다.
2번
저는 물품 등록, 검색, 필터, 정렬 기능을 구현했습니다.
Item 객체는 이름, 카테고리, 위치, 가격, 상태 정보를 가집니다.
3번
저는 거래 흐름과 디자인 패턴을 구현했습니다.
아이템 상태는 State Pattern, 패널티는 Strategy Pattern으로 구현했습니다.
4번
저는 전체 GUI와 화면 전환, 관리자 기능을 구현했습니다.
각 버튼을 누르면 담당 기능 클래스와 연결되도록 구현했습니다.
최종 결론
1번: 회원 / 로그인 / 매너온도 코드
2번: 물품 / 검색 / 등록 코드
3번: 거래 / 상태 패턴 / 패널티 코드
4번: GUI / 관리자 / 화면 전환 코드