# VYRO STUDIO — Android starter (v0.1)

## Included in this first MVP
- Dark VYRO Studio Compose UI
- Android video picker (`video/*`)
- Local video preview/playback using AndroidX Media3 ExoPlayer
- Searchable catalogue containing 1,000 named preset entries
- My FX recipe creator (name, type, intensity) saved locally using SharedPreferences

## Important scope note
The 1,000 entries are catalogue placeholders, not 1,000 fully implemented GPU effects. In this MVP, custom effects are saved as recipes; they are not yet applied to the video. Timeline editing, real-time effect rendering, captions, audio mixing, and export are planned next milestones.

## Build
1. Install Android Studio (Ladybug or newer) and Android SDK 35.
2. Open this folder in Android Studio.
3. Allow Gradle sync.
4. Run the `app` configuration on an Android 8.0+ device/emulator.

Package: `com.vyro.studio`
Version: `0.1.0`

## Next engineering milestones
1. Non-destructive multi-track timeline and trim/split operations.
2. GPU shader effects with adjustable parameters and keyframes.
3. Preset recipe schema/versioning and apply-to-clip workflow.
4. Subtitle/audio tracks and export pipeline.
5. AI features (some require on-device models or a backend and may incur compute costs).
