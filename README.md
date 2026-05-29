# OOP Game — Kotlin / LibGDX 종스크롤 슈팅 게임

OOP with Kotlin 강의용 프로젝트.  
LibGDX 기반의 고정 화면 아케이드 종스크롤 슈팅 게임.

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

## 4. 조작법

| 키 | 동작 |
|---|---|
| ← → | 플레이어 좌우 이동 |
| ↑ ↓ | 플레이어 상하 이동 |
| Space | 총알 발사 (위) |
| S | 총알 발사 (아래) |
| Z | 폭탄 사용 |
| ESC | 종료 |

---

## 5. 게임 흐름

```
Stage 1 (드론 적 5마리) → Stage Clear
    ↓
Stage 2 (러쉬 적 7마리 순차 등장) → Stage Clear
    ↓
Stage 3 (보스전) → 보스 처치 시 Game Clear / HP 0 시 Game Over
```

- 스테이지 전환 시 스테이지 시작 연출이 짧게 표시됨
- 아이템(회복 / 폭탄)이 일정 간격으로 낙하

---

## 6. 프로젝트 구조

```
oop-game/
├── core/                                      플랫폼 독립 게임 코드
│   └── src/main/
│       ├── kotlin/com/oop/game/
│       │   ├── OopGame.kt                     LibGDX Game 진입점, 첫 월드 생성
│       │   ├── base/                          추상 베이스 클래스
│       │   │   ├── GameObject.kt              모든 게임 객체의 추상 부모 (위치, 충돌, 렌더)
│       │   │   ├── GameWorld.kt               게임 월드(=한 장면)의 추상 부모
│       │   │   └── InputHandler.kt            키 입력 래퍼
│       │   └── game/                          실제 게임 코드
│       │       ├── MainWorld.kt               게임 루프, 스테이지 관리, 충돌 처리
│       │       ├── Player.kt                  플레이어 (이동, 발사, 피격, 폭탄)
│       │       ├── Enemy.kt                   모든 적의 추상 부모 (HP, 점수, 속도)
│       │       ├── DroneEnemy.kt              드론 적 — 좌우 왕복 + 총알 발사
│       │       ├── RushEnemy.kt               러쉬 적 — 빠른 대각선 왕복
│       │       ├── BossEnemy.kt               보스 — 사인파 이동 + 파이어볼 발사
│       │       ├── Bullet.kt                  플레이어 총알 (위 방향)
│       │       ├── EnemyBullet.kt             드론이 발사하는 적 총알 (아래 방향)
│       │       ├── BombProjectile.kt          폭탄 투사체 (범위 폭발)
│       │       ├── Fireball.kt                보스 발사체
│       │       ├── HitEffect.kt               피격 이펙트 (blast.png, 0.25초)
│       │       └── Item.kt                    낙하 아이템 (HEAL / BOMB)
│       └── resources/
│           ├── stage1.png                     스테이지 1 배경
│           ├── stage2.png                     스테이지 2·3 배경
│           ├── player1.png                    플레이어 기본
│           ├── player2.png                    플레이어 이동 애니메이션
│           ├── player3.png                    플레이어 이동 애니메이션
│           ├── player_hurt.png                플레이어 피격 상태
│           ├── drone_enemy.png                드론 적 이미지
│           ├── rush_enemy.png                 러쉬 적 이미지
│           ├── boss_enemy.png                 보스 이미지
│           ├── bullet.png                     총알·적 총알 공용 이미지
│           ├── fireball.png                   보스 파이어볼 이미지
│           ├── bomb.png                       폭탄 / 폭탄 아이템 이미지
│           ├── bomb_background.png            HUD 폭탄 아이콘 배경
│           ├── blast.png                      폭발·피격 이펙트 이미지
│           ├── heal.png                       회복 아이템 이미지
│           ├── hp.png                         HUD HP 아이콘
│           ├── score.png                      HUD 점수 아이콘
│           ├── timer.png                      HUD 타이머 아이콘
│           └── tile.png                       타일 이미지
└── desktop/                                   데스크톱 런처
    └── src/main/kotlin/com/oop/game/desktop/
        └── DesktopLauncher.kt                 main() 진입점
```

---

## 7. 코드 수정 가이드

| 어떤 것을 바꾸려면? | 어디 파일을? |
|---|---|
| 창/월드 크기 | `OopGame.kt` 의 `screenWidth/Height` |
| 스테이지 구성·충돌 처리 | `game/MainWorld.kt` |
| 플레이어 이동·발사·폭탄 | `game/Player.kt` |
| 적 추가 | `game/Enemy.kt` 를 상속해 새 클래스 작성 후 `MainWorld` 에 등록 |
| 이미지 교체 | `core/src/main/resources/` 에 PNG 추가 → `Texture(Gdx.files.internal("파일명.png"))` |
