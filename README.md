# OOP Game — Kotlin / LibGDX 게임 뼈대

OOP with Kotlin 강의용 프로젝트 뼈대.
LibGDX 기반의 고정 화면 아케이드 게임을 만들기 위한 코드와 데모 예제가 포함되어 있다.

---

## 1. 사전 준비

### Java JDK 17 (필수)
Mac:
```bash
brew install openjdk@17
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk \
    /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

Windows / Linux: <https://adoptium.net/> 에서 Temurin 17 설치.

확인:
```bash
java -version    # 17.x.x 가 보이면 OK
```

### IntelliJ IDEA Community Edition (권장)
<https://www.jetbrains.com/idea/download/> 에서 다운로드.

---

## 2. 프로젝트 열기

1. IntelliJ IDEA → **Open** → 압축 푼 폴더(`oop-game`) 선택
2. **Trust Project** 클릭
3. 우측 하단의 Gradle sync 진행바가 끝날 때까지 대기 (1~2분, 처음 한 번)
4. 프로젝트 SDK 가 17 인지 확인: **File → Project Structure → Project → SDK**

---

## 3. 게임 실행

### 방법 A — Gradle 패널 (권장)
1. IntelliJ 우측 사이드바의 **Gradle** 탭 열기
2. `oop-game → desktop → Tasks → application → run` 더블클릭

### 방법 B — 터미널
프로젝트 루트에서:
```bash
./gradlew desktop:run        # macOS / Linux
gradlew.bat desktop:run      # Windows
```

---

## 4. 데모 조작법

| 키 | 동작 |
|---|---|
| 화살표 키 | 플레이어(초록 삼각형) 이동 |
| WASD | 카메라 이동 — 월드가 화면보다 커서 탐험 가능 |
| ESC | 게임 오버 후 종료 |

플레이어가 빨간 적과 부딪히면 Game Over.

---

## 5. 프로젝트 구조

```
oop-game/
├── core/                                     플랫폼 독립 게임 코드
│   └── src/main/
│       ├── kotlin/com/oop/game/
│       │   ├── OopGame.kt                     LibGDX Game 상속, 첫 월드 띄움
│       │   ├── base/                          베이스 클래스 (직접 수정 불필요)
│       │   │   ├── GameObject.kt              모든 게임 객체의 추상 부모
│       │   │   ├── GameWorld.kt               게임 월드(=한 장면)의 추상 부모
│       │   │   └── InputHandler.kt            키 입력 래퍼
│       │   └── game/                          실제 게임 코드
│       │       ├── Enemy.kt                   모든 적의 추상 부모 (HP, 점수, 속도)
│       │       ├── DroneEnemy.kt              드론 적 (좌우 왕복)
│       │       ├── RushEnemy.kt               돌진 적 (빠른 왕복 + 수직 진동)
│       │       ├── BossEnemy.kt               보스 적 (사인파 이동)
│       │       ├── Player.kt                  플레이어 (이동, 발사, 피격)
│       │       ├── Bullet.kt                  플레이어 총알
│       │       ├── Fireball.kt                보스 발사체
│       │       └── MainWorld.kt               메인 게임 월드
│       └── resources/
│           ├── player1.png                    플레이어 이미지
│           ├── enemy.png                      드론 적 이미지
│           ├── drone_enemy.png                드론 적 이미지 (예비)
│           ├── rush_enemy.png                 돌진 적 이미지
│           ├── boss_enemy.png                 보스 이미지
│           ├── bullet.png                     총알 이미지
│           ├── tile.png                       타일 이미지
│           └── background.png                 배경 이미지
└── desktop/                                  데스크톱 전용 런처
    └── src/main/kotlin/com/oop/game/desktop/
        └── DesktopLauncher.kt                 main() 진입점
```

---

## 6. 코드 수정 가이드

| 어떤 것을 바꾸려면? | 어디 파일을? |
|---|---|
| 창/월드 크기 | `OopGame.kt` 의 `screenWidth/Height` |
| 게임 로직·충돌 처리 | `game/MainWorld.kt` |
| 플레이어 이동·발사·피격 | `game/Player.kt` |
| 적 추가 | `game/Enemy.kt` 를 상속해 새 클래스 작성 후 `MainWorld` 에 등록 |
| 이미지 교체 | `core/src/main/resources/` 에 PNG 추가 → `Texture(Gdx.files.internal("파일명.png"))` |

---

## 7. 자주 나오는 질문

**Q. 실행하면 "GLFW may only be used on the main thread" 에러가 뜬다.**
A. macOS 에서 `-XstartOnFirstThread` JVM 옵션이 필요한데, Gradle `application` 플러그인이 자동으로 적용한다. **Gradle 패널의 `run` task** 로 실행하거나, 직접 main 으로 실행하려면 Run Configuration 의 VM options 에 `-XstartOnFirstThread` 추가.

**Q. JDK 17 대신 21 을 써도 되나?**
A. 보통 동작하지만 검증된 건 17. 문제 생기면 17 로 맞추자.

**Q. 이미지가 화면에 안 보인다.**
A. PNG 파일이 `core/src/main/resources/` 에 있는지 확인. Gradle 빌드(`./gradlew core:processResources`) 가 한 번 돌아야 클래스패스에 올라간다.
