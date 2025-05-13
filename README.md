Heart Rate Monitor — Jetpack Compose BLE Demo
A showcase Android application that connects to a Bluetooth LE heart-rate strap, visualises live HR data, logs each workout and presents a clean-architecture implementation of Hilt DI, Jetpack Compose, Navigation, Room and Coroutines.

📸 Key features
Feature	Screenshot*
Splash ➜ Profile ➜ Main shell flow with nested graphs	(add in repo)
Device-picker overlay (bottom sheet) that scans only while visible
Tabbed UI (Home / Activity / Settings) powered by a single HRMScaffold
Live HR display with colour-coded zone background
Activity details screen – zone bar chart + HR line chart, all in pure Compose Canvas
Room persistence of session summary and raw HR graph

* Add actual screenshots/gifs after cloning.

🛠 Tech stack
Layer	Libraries / tools
UI	Jetpack Compose 1.6, Material 3, Navigation-Compose,
DI	Hilt 2.49
BLE	BluetoothLeScanner, custom BleManager (Kotlin Coroutines channel)
Persistence	Room 2.6 (activity_summary, activity_graph)
Architecture	Clean Architecture (data → domain → presentation) + single-activity, multi-graph navigation
Charts	Custom Compose Canvas (no third-party chart lib)
Permissions	ActivityResultContracts.RequestMultiplePermissions() with graceful “deny” handling
Testing	JUnit 5, Turbine for Flow tests (samples in :test)

🔖 Architecture quick tour
app/
├─ data/
│   ├─ entity/       Room entities
│   ├─ dao/          Room DAOs
│   ├─ ble/          BleManager + repository impl
│   └─ mapper/       Entity ↔︎ Domain mappers
├─ domain/
│   ├─ model/        BleDevice, ActivitySession, HRPoint
│   └─ usecase/      ScanForDevices, ConnectToDevice, ObserveHeartRate…
├─ presentation/
│   ├─ navigation/   Root graph, MainShell, nested tab graphs
│   ├─ viewmodel/    BleViewModel, HomeViewModel, ActivityDetailsVM…
│   ├─ screen/       Composables grouped by feature
│   └─ components/   HRMScaffold, BottomNavBar, charts, chips…
└─ util/             Color palette, date helpers, permission manager
Single BLE connection is owned by BleViewModel (activity scope), exposed via StateFlows and shared across tabs.

Scanning is lifecycle-safe: starts only when the overlay sheet is shown; cancelled when hidden (awaitClose in repository).

PermissionManager gatekeeps all required runtime permissions before the Nav graph is composed.

🚀 Build & run

git clone https://github.com/avinashyerrala-dev/HeartRateMonitor-Android.git
cd HeartRateMonitor
./gradlew installDebug        # or run via Android Studio Hedgehog+
Requirements

Android Studio Hedgehog | AGP 8.3 | Kotlin 1.9+

Device running Android 8 (Oreo, API 26) or newer with BLE; chart colours tuned for dark & light themes.

⚙️ Runtime permissions
Purpose	Permission (API level)
Bluetooth scan/connect	BLUETOOTH_SCAN, BLUETOOTH_CONNECT
HR service discovery	ACCESS_FINE_LOCATION (≤ Android 12)
Notifications (optional)	POST_NOTIFICATIONS (Android 13+)

PermissionManager requests them on first launch and guides the user to App Settings if permanently denied.

📈 Data model

ActivitySummaryEntity
├─ id (PK)         LONG    auto-gen
├─ activityType    TEXT    (Running / Cycling / etc.)
├─ startTimestamp  LONG    ms
├─ endTimestamp    LONG
├─ minHeartRate    INT
├─ maxHeartRate    INT
├─ zone1-5TimeMs   LONG
└─ caloriesBurned  DOUBLE

ActivityGraphEntity
├─ sessionId (PK, FK→summary.id)
└─ heartRateGraphJson STRING  // List<HRPoint>(timestamp,bpm)

🛣 Navigation map
Splash → UserProfile → MainShell
├─ home_graph       (Home, details…)
├─ activity_graph   (History list, Details)
└─ settings_graph   (Prefs)
Back-stack is preserved per tab; re-selecting current tab pops to root.

🗺 Roadmap / TODO
BLE background reconnection service

Export session as GPX/TCX

Wear OS companion

Unit tests for ViewModels (sample included)

