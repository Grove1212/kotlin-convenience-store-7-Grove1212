# kotlin-convenience-store-precourse

# 기능 요구 사항
구매자의 할인 혜택과 재고 상황을 고려하여 최종 결제 금액을 계산하고 안내하는 결제 시스템을 구현한다.

사용자가 입력한 상품의 가격과 수량을 기반으로 최종 결제 금액을 계산한다.
총구매액은 상품별 가격과 수량을 곱하여 계산하며, 프로모션 및 멤버십 할인 정책을 반영하여 최종 결제 금액을 산출한다.
구매 내역과 산출한 금액 정보를 영수증으로 출력한다.
영수증 출력 후 추가 구매를 진행할지 또는 종료할지를 선택할 수 있다.

- 사용자가 잘못된 값을 입력할 경우 IllegalArgumentException를 발생킨다.
- "[ERROR]"로 시작하는 에러 메시지를 출력 후 그 부분부터 입력을 다시 받는다.
- Exception이 아닌 IllegalArgumentException, IllegalStateException 등과 같은 명확한 유형을 처리한다.

## 재고 관리
- [x] 결제 가능 여부를 표시한다.
  - 상품의 재고와 사려는 개수를 비교해서 결제 가능한지 확인한다.
- [x] 결제된 수량만큼 해당 상품의 재고에서 차감한다.
  - [x] [예외] IllegalStateException: 재고보다 많은 수량을 구매하면 재고 부족 에러 발생

## 프로모션 할인(name,buy,get,start_date,end_date)
promotion checker
val promotions

- [ ] 해당 상품이 프로모션 대상인지 확인한다.
  - [ ] 해당 상품이 프로모션 할인 상품인지 확인한다.
  - [ ] 오늘 날짜가 프로모션 기간인지 아닌지 확인한다.
- //다음 커밋에서 Product 생성자 바꾸기
- 프로모션은 N개 구매 시 1개 무료 증정(Buy N Get 1 Free)의 형태로 진행된다.
- 1+1 또는 2+1 프로모션이 각각 지정된 상품에 적용되며, 동일 상품에 여러 프로모션이 적용되지 않는다.
- 프로모션 혜택은 프로모션 재고 내에서만 적용할 수 있다.
- 프로모션 기간 중이라면 프로모션 재고를 우선적으로 차감하며, 프로모션 재고가 부족할 경우에는 일반 재고를 사용한다.
- 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음을 안내한다.
- 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제하게 됨을 안내한다.

## 멤버십 할인
- 멤버십 회원은 프로모션 미적용 금액의 30%를 할인받는다.
- 프로모션 적용 후 남은 금액에 대해 멤버십 할인을 적용한다.
- 멤버십 할인의 최대 한도는 8,000원이다.

## 영수증 출력
- 영수증은 고객의 구매 내역과 할인을 요약하여 출력한다.
- 영수증 항목은 아래와 같다.
  - 구매 상품 내역: 구매한 상품명, 수량, 가격
  - 증정 상품 내역: 프로모션에 따라 무료로 제공된 증정 상품의 목록
  - 금액 정보
    - 총구매액: 구매한 상품의 총 수량과 총 금액
    - 행사할인: 프로모션에 의해 할인된 금액
    - 멤버십할인: 멤버십에 의해 추가로 할인된 금액
    - 내실돈: 최종 결제 금액
- 영수증의 구성 요소를 보기 좋게 정렬하여 고객이 쉽게 금액과 수량을 확인할 수 있게 한다.

# 입출력 요구 사항
    
## 입력
//입력값을 처리하는 부분 다른사람들은 어떻게 했지? InputProcessor 만들자
//입력테스트? val reader = BufferedReader(StringReader("not a number"))
//입력을 가공하기
- 구현에 필요한 상품 목록과 행사 목록을 파일 입출력을 통해 불러온다.
  - src/main/resources/products.md과 src/main/resources/promotions.md 파일을 이용한다.
  - 두 파일 모두 내용의 형식을 유지한다면 값은 수정할 수 있다.
- 구매할 상품과 수량을 입력 받는다. 상품명, 수량은 하이픈(-)으로, 개별 상품은 대괄호([])로 묶어 쉼표(,)로 구분한다.
    ```[콜라-10],[사이다-3]```

- 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량보다 적게 가져온 경우, 그 수량만큼 추가 여부를 입력받는다.
  - Y: 증정 받을 수 있는 상품을 추가한다.
  - N: 증정 받을 수 있는 상품을 추가하지 않는다.
  ```Y```
- 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제할지 여부를 입력받는다.
  - Y: 일부 수량에 대해 정가로 결제한다.
  - N: 정가로 결제해야하는 수량만큼 제외한 후 결제를 진행한다.
  ```Y```

- 멤버십 할인 적용 여부를 입력 받는다.
  - Y: 멤버십 할인을 적용한다.
  - N: 멤버십 할인을 적용하지 않는다.
  ```Y```
  
- 추가 구매 여부를 입력 받는다.
  - Y: 재고가 업데이트된 상품 목록을 확인 후 추가로 구매를 진행한다.
  - N: 구매를 종료한다.
  ```Y```
  
## 출력
- 환영 인사와 함께 상품명, 가격, 프로모션 이름, 재고를 안내한다. 만약 재고가 0개라면 재고 없음을 출력한다.
```  
안녕하세요. W편의점입니다.
  현재 보유하고 있는 상품입니다.

- 콜라 1,000원 10개 탄산2+1
- 콜라 1,000원 10개
- 사이다 1,000원 8개 탄산2+1
- 사이다 1,000원 7개
- 오렌지주스 1,800원 9개 MD추천상품
- 오렌지주스 1,800원 재고 없음
- 탄산수 1,200원 5개 탄산2+1
- 탄산수 1,200원 재고 없음
- 물 500원 10개
- 비타민워터 1,500원 6개
- 감자칩 1,500원 5개 반짝할인
- 감자칩 1,500원 5개
- 초코바 1,200원 5개 MD추천상품
- 초코바 1,200원 5개
- 에너지바 2,000원 5개
- 정식도시락 6,400원 8개
- 컵라면 1,700원 1개 MD추천상품
- 컵라면 1,700원 10개

구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])
```

- 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우, 혜택에 대한 안내 메시지를 출력한다.
```
현재 {상품명}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)
```

- 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우, 일부 수량에 대해 정가로 결제할지 여부에 대한 안내 메시지를 출력한다.
```
현재 {상품명} {수량}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)
```

- 멤버십 할인 적용 여부를 확인하기 위해 안내 문구를 출력한다.
```
멤버십 할인을 받으시겠습니까? (Y/N)
```

- 구매 상품 내역, 증정 상품 내역, 금액 정보를 출력한다.
```
==============W 편의점================
상품명		수량	금액
콜라		3 	3,000
에너지바 		5 	10,000
=============증	정===============
콜라		1
====================================
총구매액		8	13,000
행사할인			-1,000
멤버십할인			-3,000
내실돈			 9,000
```

- 추가 구매 여부를 확인하기 위해 안내 문구를 출력한다.
```
감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)
```

- 사용자가 잘못된 값을 입력했을 때, "[ERROR]"로 시작하는 오류 메시지와 함께 상황에 맞는 안내를 출력한다.
  - 구매할 상품과 수량 형식이 올바르지 않은 경우: [ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.
  - 존재하지 않는 상품을 입력한 경우: [ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.
  - 구매 수량이 재고 수량을 초과한 경우: [ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.
  - 기타 잘못된 입력의 경우: [ERROR] 잘못된 입력입니다. 다시 입력해 주세요.
    /*
- 입력 자체 에러(문자열, int형 등 의도된 입력이 아닌 경우) // 이 경우는 입력 뷰에서 처리
- 기능 수행 에러(우리가 정한 규칙에 위배되는 입력인 경우)// 이 경우는 해당 객체에서 처리
  */

# 프로그래밍 요구 사항 1
- Kotlin 1.9.24에서 실행 가능해야 한다.
- Java 코드가 아닌 Kotlin 코드로만 구현해야 한다.
- 프로그램 실행의 시작점은 Application의 main()이다.
- build.gradle.kts 파일은 변경할 수 없으며, 제공된 라이브러리 이외의 외부 라이브러리는 사용하지 않는다.
- 프로그램 종료 시 System.exit() 또는 exitProcess()를 호출하지 않는다.
- 프로그래밍 요구 사항에서 달리 명시하지 않는 한 파일, 패키지 등의 이름을 바꾸거나 이동하지 않는다.
- 코틀린 코드 컨벤션을 지키면서 프로그래밍한다.
- 기본적으로 Kotlin Style Guide를 원칙으로 한다.

# 프로그래밍 요구 사항 2
- indent(인덴트, 들여쓰기) depth를 3이 넘지 않도록 구현한다. 2까지만 허용한다.
  - 예를 들어 while문 안에 if문이 있으면 들여쓰기는 2이다.
  - 힌트: indent(인덴트, 들여쓰기) depth를 줄이는 좋은 방법은 함수(또는 메서드)를 분리하면 된다.
- 함수(또는 메서드)가 한 가지 일만 하도록 최대한 작게 만들어라.
- JUnit 5와 AssertJ를 이용하여 정리한 기능 목록이 정상적으로 작동하는지 테스트 코드로 확인한다.
  - 테스트 도구 사용법이 익숙하지 않다면 아래 문서를 참고하여 학습한 후 테스트를 구현한다.
```
  JUnit 5 User Guide
  AssertJ User Guide
  AssertJ Exception Assertions
  Guide to JUnit 5 Parameterized Tests
```

# 프로그래밍 요구 사항 3
- else를 지양한다.
  - 때로는 if/else, when문을 사용하는 것이 더 깔끔해 보일 수 있다. 어느 경우에 쓰는 것이 적절할지 스스로 고민해 본다.
  - 힌트: if 조건절에서 값을 return하는 방식으로 구현하면 else를 사용하지 않아도 된다.
- Enum 클래스를 적용하여 프로그램을 구현한다.
- 구현한 기능에 대한 단위 테스트를 작성한다. 단, UI(System.out, System.in, Scanner) 로직은 제외한다.
  
# 프로그래밍 요구 사항 4
- 함수(또는 메서드)의 길이가 10라인을 넘어가지 않도록 구현한다.
- 함수(또는 메서드)가 한 가지 일만 잘 하도록 구현한다.
- 입출력을 담당하는 클래스를 별도로 구현한다.

# 피드백
- TDD 하기
- 코드의 가독성과 유지보수성을 높일 수 있도록 노력하기
- 예외 상황에 대해 고민하기(큰 숫자 입력도 예외)
- 단일 책임 원칙 지키기 : 비즈니스 로직과 UI로직을 분리하기(inputView, outputView), 로그 메시지같은 것은 toString메서드 오버라이딩하기
- 연관성이 있는 상수는 static final 대신 **enum 활용하기**
  - 관련 상수를 그룹화 하고, 각 상수에 관련된 속성과 행동을 부여할 수 있기 때문 : 코드의 가독성과 유지보수성을 크게 향상시킨다.
- 값이 변경되지 않으면 val 키워드를 사용하기
- 객체의 상태 접근을 제한아는 캡슐화를 하기 위해 변수의 접근제어자를 private로 설정하기 **해당 변수는 객체 내에서만 관리된다.**
- 객체는 객체답게 사용하기
    - 로또에서 데이터를 꺼내지 말고 스스로 데이터를 처리해 의미 있는 정보를 던지도록 한다.
    - 예: 숫자가 포함되어있는지 확인한다, 당첨 번호와 몇 개가 일치하는지 확인한다.
- 필드의 수를 줄이기 위해 노력하기
    - 필드의 수가 많아지면 객체의 복잡도가 증가하고 관리가 어려워지며, 버그 가능성도 높아진다. 필드에 중복이 있거나 불필요한 필드는 최소화한다.
- 성공 케이스 뿐만이 아니라 예외 케이스도 테스트하기
- 테스트 코드도 리팩터링을 통해 지속적으로 개선하기
  - 반복적으로 수행하는 부분이 있다면 중복을 제거하여 유지보수성을 높이고 가독성을 향상시켜야 한다. 
  - 파라미터 값만 바뀐다면, 파라미터화된 테스트를 통해 중복을 줄이기
- 테스트를 위한 코드는 구현 코드에서 분리되어야 한다.(테스트를 위해 구현 코드를 변경하지 말자)
    - 테스트를 위해 접근제어자를 바꾸는 경우,
    - 테스트 코드에서만 사용되는 메서드
- 단위 테스트 하기 어려운 코드를 단위 테스트 하기 좋은 코드로 바꾸자.
  - 테스트하기 어려운 의존성을 외부에서 주입하거나 분리하여 테스트 가능한 상태로 만들기(매개변수 추가하기)