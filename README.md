# Simon

An Android implementation of the classic [Simon](https://en.wikipedia.org/wiki/Simon_(game)) memory game, built with Kotlin and Jetpack Compose.
Final project for the **Embedded Systems Programming (ESP)** course.

## Features
- **Gameplay**: the app plays a growing sequence of colors (6 colored buttons, each with its own sound) and the player repeats it. One wrong tap ends the game.
- **Pause / resume** during a game.
- **Game history**: every finished game is saved locally. A list screen shows past games, and a detail screen shows the full sequence and the longest correct streak.
- **Portrait and landscape** layouts.
- **Localization**: Italian and English.

## Tech stack
- Kotlin, Jetpack Compose, Material 3
- MVVM with `ViewModel` and `SavedStateHandle`
- Navigation Compose (game → list → detail)
- Room for local persistence
- `SoundPool` for audio, coroutines for sequence timing

## State handling
The game survives activity recreation, including when the **"Don't keep activities"** developer option is on. What happens when the app goes to the background and comes back depends on its state:

| State when backgrounded | Behavior on resume |
|---|---|
| `comp_turn` (app is showing the sequence) | the sequence is replayed from the start |
| `player_turn` (player is repeating it) | the player resumes from where they left off |
| `paused` | the sequence is replayed from the start |

## Tested on
- Pixel 9 (physical device), Android 16
- Pixel 5 (emulator), Android 17

## Build
Open the project in Android Studio, or from the command line:

```bash
./gradlew assembleDebug
```

Requires min SDK 33, target SDK 36.

---

*Lorenzo Panizzolo*
