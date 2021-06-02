# JetpackWithMVI

REST API 와의 다양한 상호작용을 하며(CRUD, Token Authentication, etc..)<br>
블로그를 게시할 수 있는 앱 입니다.


![jet-default](https://user-images.githubusercontent.com/16537977/120469801-cda0fa80-c3dd-11eb-93f0-71932ed2abee.gif)
![jet_detail](https://user-images.githubusercontent.com/16537977/120470182-3a1bf980-c3de-11eb-86fc-d195344c93ad.gif)

## Tech-stack
- 100% Kotlin Project
  - Coroutine & Flow  

- Jetpack
  - Navigation - 앱의 Navigation
  - LiveData - 앱의 라이프사이클에 맞는 데이터 갱신 관리
  - LifeCycle - 앱의 생명주기에 맞는 행동 관리
  - ViewModel - UI에 관련된 다수의 데이터를 생명주기에 맞게 관리
  - Room - 앱 내의 캐시 데이터 관리
  - Data Binding - 관찰 가능한 데이터를 UI 선언적으로 바인딩

- UI
  - Single Activity Architecture - 하나의 Activity에 다수의 프래그먼트 사용
  - Material Design 
  - RxBinding - UI 의 이벤트를 조합 및 변경 처리

- Test
  - Junit5 - Test 프레임워크   
  - Mockito-kotlin - Mock 프레임워크
  - okhttp_mockwebserver - API Mock 라이브러리 
  - Hamcrest - JUnit에 사용되는 Matcher 라이브러리

- Third Party
  - Glide - Image Loading 
  - Dagger2 - DI 라이브러리
  - Retrofit2 - Http 처리
  - ImagePicker - 앨범 이미지 Picker
  
## Architecture
![mvi_arc](https://user-images.githubusercontent.com/16537977/70466969-ab3f6100-1b07-11ea-922d-263f3ca3c78e.jpeg)
