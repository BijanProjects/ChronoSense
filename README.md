# ⏱️ ChronoSense

**Understand your time. Reflect on your hours. Live intentionally.**

ChronoSense is a production-grade Android journaling app that helps you reflect on how you spend each interval of your waking hours. Built with **Clean Architecture**, an **O(S+E) two-pointer merge engine**, and a **KMP-ready domain layer** — structured for massive-scale deployment and future iOS release.

---

## ✨ Features

### 📅 Day View
- Timeline of customizable time slots across your waking hours
- Color-coded mood indicators powered by the `Mood` enum design system
- Completion progress bar with live percentage
- Auto-scroll to the currently active interval
- Swipe between days with animated transitions

### 📊 Month View
- Calendar grid highlighting days with entries
- **Most Repeated Activities** — frequency bars driven by O(n) single-pass aggregation
- **Mood Distribution** — proportional cards from `MonthInsight.aggregate()`
- Summary stats: total entries, active days, top activity

### ✍️ Entry Screen
- Rich text description of how you spent each interval
- **Mood Selector** — 7 animated moods with spring animations and semantic roles
- **Activity Tags** — 10 categories via `ActivityTag` enum with icons
- Upsert semantics (create or edit in one flow)

### ⚙️ Settings
- **Wake Time** & **Sleep Time** — define your waking hours
- **Check-in Interval** — 30m, 1h, 1.5h, 2h (default), 3h, 4h
- **Notification Reminders** — AlarmManager alarms scheduled per remaining interval
- Notifications reschedule automatically on device boot via `BootReceiver`

---

## 🏗️ Architecture — Clean Architecture + MVVM

```
com.chronosense/
│
├── domain/                     ← Pure Kotlin — ZERO Android imports (KMP-ready)
│   ├── model/
│   │   ├── Mood.kt             # Enum: emoji, label, colorHex, sortOrder
│   │   ├── ActivityTag.kt      # Enum: 10 tags with icon + color
│   │   ├── JournalEntry.kt     # Domain model with Mood & ActivityTag types
│   │   ├── TimeSlot.kt         # Interval slot with optional entry
│   │   ├── DaySummary.kt       # O(n) aggregation via groupingBy/eachCount
│   │   ├── MonthInsight.kt     # O(n) single-pass frequency maps
│   │   └── UserPreferences.kt  # Wake/sleep time, interval, notifications
│   ├── repository/
│   │   ├── JournalRepository.kt        # Interface — Flow queries + suspend commands
│   │   └── PreferencesRepository.kt    # Interface — observe + atomic update
│   └── usecase/
│       ├── GetTimeSlotsUseCase.kt      # Combines entries + prefs → IntervalEngine
│       ├── SaveEntryUseCase.kt         # Validates, trims, preserves createdAt
│       └── GetMonthInsightsUseCase.kt  # Combines entries + prefs → MonthInsight
│
├── core/
│   ├── algorithm/
│   │   └── IntervalEngine.kt   # O(S + E·logE) two-pointer merge for slot generation
│   ├── common/
│   │   └── ChronoResult.kt     # Sealed: Success/Error/Loading with map/flatMap
│   ├── analytics/
│   │   └── AnalyticsEvent.kt   # Typed events + AnalyticsTracker interface
│   └── di/
│       └── AppModule.kt        # Manual DI with lazy delegates (Koin migration path)
│
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt      # Room v2, JournalEntryEntity, destructive migration
│   │   ├── JournalDao.kt       # @Upsert, Flow-based queries, composite indices
│   │   └── Converters.kt       # Minimal — entity uses String columns
│   ├── model/
│   │   └── JournalEntryEntity.kt   # Room entity: String dates, indices on (date, startTime)
│   ├── mapper/
│   │   └── EntityMapper.kt     # Bidirectional: JournalEntryEntity ↔ JournalEntry
│   ├── cache/
│   │   └── DayCache.kt         # LRU (14-day) with synchronized access-order eviction
│   ├── preferences/
│   │   └── UserPreferencesStore.kt  # DataStore → UserPreferences mapping
│   └── repository/
│       ├── JournalRepositoryImpl.kt     # Room-backed, warms cache on observe
│       └── PreferencesRepositoryImpl.kt # DataStore-backed
│
├── notification/
│   ├── NotificationScheduler.kt    # Batch scheduling via IntervalEngine slots
│   ├── AlarmReceiver.kt            # Builds rich notification with deep-link
│   └── BootReceiver.kt             # goAsync() + SupervisorJob for re-scheduling
│
├── ui/
│   ├── design/
│   │   └── tokens/Spacing.kt       # 4dp-grid: Spacing, Radius, Elevation
│   ├── theme/
│   │   ├── Color.kt                # getMoodColor(Mood?) + palette
│   │   ├── Type.kt                 # Inter/Poppins typography scale
│   │   └── Theme.kt                # Light/Dark + Material You dynamic colors
│   ├── components/
│   │   ├── TimeSlotCard.kt         # Domain TimeSlot, mood accent bar, a11y labels
│   │   ├── MoodSelector.kt         # Mood enum, spring animations, RadioButton roles
│   │   └── TagSelector.kt          # ActivityTag enum, FilterChip, LazyRow
│   ├── navigation/
│   │   ├── Screen.kt               # Sealed destinations with icons
│   │   └── NavGraph.kt             # Bottom nav + entry deep-link route
│   └── screens/
│       ├── day/                     # DayUiState, auto-scroll to active slot
│       ├── entry/                   # SaveEntryUseCase, Mood/ActivityTag types
│       ├── month/                   # MonthInsight, CalendarGrid, mood bars
│       └── settings/               # UserPreferences.INTERVAL_OPTIONS, atomic updates
│
├── ChronoSenseApp.kt       # lazy AppModule + notification channel
└── MainActivity.kt         # Edge-to-edge, permission request, single setContent
```

### Key Algorithmic Decisions

| Algorithm | Complexity | Location |
|---|---|---|
| Slot generation + entry matching | **O(S + E·logE)** two-pointer merge | `IntervalEngine.generateSlots()` |
| Active slot lookup | **O(1)** arithmetic | `IntervalEngine.findActiveSlotIndex()` |
| Day summary aggregation | **O(n)** single-pass via `groupingBy` | `DaySummary.from()` |
| Month insights aggregation | **O(n)** single-pass with HashMap freq | `MonthInsight.aggregate()` |
| Day cache eviction | **O(1)** amortized LRU with `ArrayDeque` | `DayCache` |
| Mood/Tag enum lookup | **O(1)** via pre-built companion maps | `Mood.fromEmoji()` / `ActivityTag.fromLabel()` |

### Tech Stack

| Layer | Technology |
|---|---|
| Language | **Kotlin 2.0** |
| UI | **Jetpack Compose** + Material 3 + Material You |
| Architecture | **Clean Architecture** + MVVM + Use Cases |
| DI | Manual `AppModule` with lazy delegates |
| Database | **Room 2.6.1** with KSP, @Upsert, composite indices |
| Preferences | **DataStore** with atomic `update(transform)` |
| Notifications | **AlarmManager** (exact + allow-while-idle) |
| Caching | LRU `DayCache` (14 days, synchronized) |
| Analytics | Pluggable `AnalyticsTracker` interface |
| Min SDK | **26** (Android 8.0) |
| Target SDK | 34 |

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio Hedgehog** (2023.1.1) or newer
- **JDK 17**
- Android SDK with API 34

### Setup

```bash
git clone <repo-url>
cd ChronoSense
```

1. **Open in Android Studio** — File → Open → select project root
2. Let Gradle sync complete (wrapper included: Gradle 8.7)
3. Select device/emulator (API 26+) → **Run ▶️**

---

## 🌍 Cross-Platform Migration Path (KMP)

The domain layer has **zero Android imports** — it's pure Kotlin ready for Kotlin Multiplatform:

| Step | Effort | Description |
|---|---|---|
| 1. Create KMP module | Low | Extract `domain/` into `:shared` module |
| 2. Replace `java.time` | Low | Swap to `kotlinx-datetime` (API-compatible) |
| 3. Shared use cases | None | Use cases are already platform-agnostic |
| 4. iOS UI | Medium | SwiftUI screens consuming shared ViewModels |
| 5. DI migration | Low | Replace `AppModule` with `koin-core` multiplatform |

---

## 🎨 Design Philosophy

- **Minimal** — Clean surfaces, 4dp spacing grid, generous whitespace
- **Engaging** — Spring animations, auto-scroll to now, color-coded moods
- **Accessible** — Semantic roles, content descriptions, 48dp touch targets
- **Dark Mode** — Full dark theme with deep indigo surfaces
- **Material You** — Dynamic color support on Android 12+

---

## 📄 License

This project is for personal/educational use.
