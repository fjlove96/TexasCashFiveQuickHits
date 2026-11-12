# Texas Cash Five Quick Hits - App Implementation Plan

We will build a feature-rich, modern Android application for Texas Cash Five using Jetpack Compose, Material 3, and MVVM architecture.

## User Review Required

> [!NOTE]
> The app will feature a Quick Pick number generator (5 numbers from 1 to 41), a ticket saving feature, and a clean Material 3 UI with lottery ball styling.

## Open Questions

- None at this stage. Standard Texas Cash Five rules (5/41 numbers) will be used.

## Proposed Changes

### Build Configuration & Dependencies
- Update [build.gradle.kts](file:///C:/Users/fjlov/AndroidStudioProjects/TexasCashFiveQuickHits/app/build.gradle.kts) to add Jetpack Compose dependencies (Compose BOM, Material 3, ViewModel, Activity Compose).

### Domain & Logic
- [NEW] [LotteryNumberGenerator.kt](file:///C:/Users/fjlov/AndroidStudioProjects/TexasCashFiveQuickHits/app/src/main/java/com/example/texascashfivequickhits/model/LotteryNumberGenerator.kt): Generates sorted unique random numbers between 1 and 41.
- [NEW] [CashFiveViewModel.kt](file:///C:/Users/fjlov/AndroidStudioProjects/TexasCashFiveQuickHits/app/src/main/java/com/example/texascashfivequickhits/ui/CashFiveViewModel.kt): Manages UI state, generated quick picks, and saved tickets.

### UI Implementation
- [NEW] [MainActivity.kt](file:///C:/Users/fjlov/AndroidStudioProjects/TexasCashFiveQuickHits/app/src/main/java/com/example/texascashfivequickhits/MainActivity.kt): Entry point hosting the Compose UI.
- [NEW] [CashFiveScreen.kt](file:///C:/Users/fjlov/AndroidStudioProjects/TexasCashFiveQuickHits/app/src/main/java/com/example/texascashfivequickhits/ui/CashFiveScreen.kt): Material 3 Composable screen showing Quick Pick generator, number balls, and saved tickets list.

## Verification Plan

### Automated Tests
- Unit test for `LotteryNumberGenerator` ensuring exactly 5 unique numbers between 1 and 41 are generated and sorted.
- ViewModel state tests.

### Manual Verification
- Deploy to emulator/device and test generating Quick Picks and saving tickets.
