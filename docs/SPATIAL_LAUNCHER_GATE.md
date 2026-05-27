# TV-SL — Spatial Launcher Design Lock

**Gate:** TV-SL  
**Status:** Open — defines the mandatory standard. Implementation spans multiple commits. Gate closes when all acceptance criteria in §VII are met.  
**Scope:** `GardenCanvas`, `FocusZone`, `PromptOrb`, `SpatialLauncherScreen` and every composable that makes up the main launcher surface.

---

## The Vision

The TAVI launcher is not a grid of icons. It is a living spatial ecosystem — apps exist at genuine 3D depths, breathe individually at their own rhythm, and respond to physics. The space has a visual language: foreground is crisp and brilliant, midground is soft and present, background is deep and hazy. State changes ripple outward from the center like light through water. The PromptOrb is a plasma entity, not a button.

Every pixel is intentional. Every animation has a physical reason. Every state has a color. Nothing on this screen is decorative — it is all information.

---

## I. Rendering Architecture

### Depth-of-Field System

Three genuine depth layers with distinct visual treatment:

| Layer | Scale | Alpha | Blur | Tilt Multiplier |
|---|---|---|---|---|
| Background | 0.25× | 0.12 × stage.alpha | `BlurMaskFilter(8f, NORMAL)` | 0.5× |
| Midground | 0.55× | 0.40 × stage.alpha | `BlurMaskFilter(2f, NORMAL)` | 1.0× |
| Foreground | 1.0× | 1.0 | none | 2.0× |

Background and midground are drawn on `Canvas`. Foreground nodes are Compose composables (AsyncImage icons in `FocusZone`).

**API 31+:** Wrap the background `Canvas` layer in:
```kotlin
Modifier.graphicsLayer {
    renderEffect = RenderEffect.createBlurEffect(8f, 8f, Shader.TileMode.CLAMP)
}
```
**API <31 fallback:** `BlurMaskFilter` inside `drawIntoCanvas` as currently used. Guard with `Build.VERSION.SDK_INT >= Build.VERSION_CODES.S`.

### Voxel Grid Enhancement

The grid is the spatial scaffold. It must feel alive.

- Vanishing point tracks accelerometer tilt — **already implemented ✓**
- Grid line color **responds to TaviState**: blue (Idle), green (Ready), amber (IntentUnclear/RiskDetected), red (Failed), purple (Private)
- Line weight varies with row proximity: lines near camera are `2f` stroke, distant are `0.5f`
- **Horizon glow**: radial gradient at vanishing point, radius `size.height * 0.35f`, color = state grid color at alpha 0.06. Drawn via `drawRadialGradient` on `Canvas`.

### Node Rendering

Current: flat polygon shapes. Required:

**Growth-stage glow aura** — before drawing the polygon, draw a `BlurMaskFilter` soft circle:
```kotlin
// aura
paint.blurMaskFilter = BlurMaskFilter(r * 1.8f, BlurMaskFilter.Blur.NORMAL)
paint.color = nodeColor.copy(alpha = nodeColor.alpha * 0.35f).toArgb()
canvas.drawCircle(x, y, r * 1.6f, paint)
paint.blurMaskFilter = null
// polygon
canvas.drawPath(path, polygonPaint)
```

**Individual breathing** — `animationPhaseOffset` is already on `GardenNode` ✓. Use it to compute per-node `breathAlpha` and `breathScale`:
```kotlin
val t = (timeMs / 8000f + node.animationPhaseOffset) % 1f
val breathAlpha = 0.85f + 0.15f * sin(t * 2 * PI).toFloat()
val breathScale = 1f + 0.04f * sin(t * 2 * PI).toFloat()
```
No two nodes breathe in unison.

**Growth-stage color table:**

| Stage | Saturation | Value | Character |
|---|---|---|---|
| SEED | 0.20f | 0.50f | Dim, nearly absent |
| SPROUT | 0.45f | 0.72f | Soft, growing |
| BLOOM | 0.65f | 0.88f | Vivid, present |
| CROWN | 0.82f | 1.00f | Brilliant, dominant |

**Crown special treatment:** draw a slowly rotating outer ring (reuse existing `nodeRotation` animation ✓) at `r * 1.35f` with stroke `1.5f` and `GlowAmber` tint.

---

## II. Animation System

### Physics-Based Node Entry

When nodes appear at first composition or scope change:
- `spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessLow)` on Y offset
- Entry starts at `baseY + 120f`, settles to `baseY`
- Staggered delays: node at index `i` starts after `i * 40ms`
- Use `LaunchedEffect(nodes)` + `Animatable` per node

### State-Transition Ripple

On every `TaviState` change, a circle radiates from screen center:
- `animateFloatAsState(1f, spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium))` on radius fraction (0 → 1.4× screen diagonal)
- Color = new state's ambient color (see §IV), alpha fades `0.22f → 0f` as radius grows
- Drawn via `Canvas.drawCircle` in `GardenCanvas` after grid, before nodes
- Triggered by keying on `taviState` passed as parameter to `GardenCanvas`

### PromptOrb Plasma

Replace flat `TaviAccent` FAB background with:
- `InfiniteTransition` rotates a `SweepGradient` at 0.3 RPM (one full rotation = 200s)
- 4-stop: `[stateColor, TaviAccent, stateColor.copy(alpha=0.7f), TaviAccent]`
- Drawn via `Canvas` inside a `Box` with `CircleShape` clip
- Outer glow: `Modifier.shadow(elevation = stateElevation, shape = CircleShape, ambientColor = stateColor)` — elevation scales with urgency:

| State | Elevation |
|---|---|
| Idle | 8.dp |
| Ready / Capture | 12.dp |
| IntentUnclear | 16.dp |
| RiskDetected | 24.dp |
| ActNow | 20.dp |
| Blocked / Failed | 8.dp |
| Private | 14.dp |

- Tap spring: `animate*AsState` scale `1f → 0.88f → 1f` on press/release via `Interaction` collection

### FocusZone

- Breathing drives **both** border stroke width AND `graphicsLayer { scaleX = 1f + 0.008f * breathFactor }` — a whisper of scale, not visible as zoom but felt as alive
- **Scope ring**: when `currentScope != null`, a second outer ring appears at `32.dp + 6.dp` gap, pulsing at 3s rhythm in scope-keyed color, with scope name in `Barlow Condensed 10sp` at `Alignment.TopCenter` relative to the FocusZone box
- **Background scrim**: FocusZone background uses `SpaceNavy.copy(alpha = 0.58f)` with a `Modifier.blur(4.dp)` behind it on API 31+ (blur the content behind, not the card itself)

---

## III. Interaction Model

| Gesture | Target | Result |
|---|---|---|
| Single tap | FocusZone node | Intent clarifier (Cluster 6) → launch |
| Long press | FocusZone node | Toggle spatial anchor |
| Long press | Canvas background | Open Warden |
| Single tap | PromptOrb | Expand / collapse orb |
| Edge drag | Screen edge | Page navigation to FossilDeck / BotWorkspaces |

**Not yet required (Phase 2+):**
- Drag to reposition node between layers
- Pinch to shift the depth focus plane
- Swipe-up on node → park in Want Shelf (Cluster 16)
- Double-tap node → direct handoff to current AI bot

### Haptic Choreography

| Event | Haptic constant |
|---|---|
| Node tap | `HapticFeedbackConstants.KEYBOARD_TAP` |
| Long press → anchor | `HapticFeedbackConstants.LONG_PRESS` |
| Risk detected | `HapticFeedbackConstants.REJECT` (API 30+) |
| Intent clarifier appear | `HapticFeedbackConstants.CONTEXT_CLICK` |
| Execution success | `HapticFeedbackConstants.CONFIRM` (API 30+) |
| Emergency off | `HapticFeedbackConstants.REJECT` × 2, 200ms apart |

---

## IV. State Visual Language

Every `TaviState` has a consistent signature across all surfaces. Colors are passed as parameters — no hardcoded colors in leaf composables.

| State | Grid color | FocusZone ring | PromptOrb | Background dim | State elevation |
|---|---|---|---|---|---|
| Idle | `Color(0xFF1A2840)` | `BreathBlue` | `TaviAccent` | 0% | 8.dp |
| Ready | `Color(0xFF1A3A2A)` | `BreathTeal` | `BreathBlue` | 0% | 12.dp |
| Capture | `Color(0xFF1A2840)` | `BreathBlue` | `TaviAccent` | 40% | 12.dp |
| IntentUnclear | `Color(0xFF3A2A10)` | `GlowAmber` | `GlowAmber` | 0% | 16.dp |
| RiskDetected | `Color(0xFF3A1A1A)` | `RiskRed` | `RiskRed.copy(0.7f)` | 20% | 24.dp |
| ActNow | `Color(0xFF0A2030)` | `BreathBlue` | `BreathBlue` | 60% | 20.dp |
| Blocked | `Color(0xFF1A1A1A)` | `FallbackGrey` | `FallbackGrey` | 30% | 8.dp |
| Failed | `Color(0xFF3A0A0A)` | `RiskRed` | `RiskRed` | 40% | 8.dp |
| Private | `Color(0xFF1A0A2A)` | `PrivatePurple` | `PrivatePurple` | 10% | 14.dp |
| Fallback | `Color(0xFF111111)` | `FallbackGrey` | `FallbackGrey` | 20% | 8.dp |

The `background dim` is a `Box` with `Color.Black.copy(alpha = dimAlpha)` at `zIndex 1.5f`, between `GardenCanvas` and the FocusZone — only present when `dimAlpha > 0`. It uses `animateFloatAsState` so the dim transitions smoothly on state change.

---

## V. Performance Contract

| Metric | Target | How to measure |
|---|---|---|
| Frame time at rest | ≤ 16ms | Android Studio GPU profiler |
| Frame time during state transition | ≤ 22ms | Same |
| Frame time during node entry spring | ≤ 18ms | Same |
| Foreground node count | max 5 | GardenRepository limit |
| Midground node count | max 15 | Repository param |
| Background node count | max 30 | Repository param |
| Canvas `Paint()` allocations in draw | 0 | Reuse pre-allocated `Paint` objects in `remember {}` |
| Canvas `Path()` allocations in draw | 0 | `Path` objects in `remember {}`, reset each frame via `rewind()` |
| `InfiniteTransition` instances per screen | max 2 | One shared for Canvas animations, one for FocusZone breath |
| `Animatable` for node entry | 1 per node, released after settling | `LaunchedEffect` scope |

**Strict rule:** No `State` reads inside `graphicsLayer {}` or `drawWithContent {}` lambdas. All animated values must be read before the lambda entry and captured as `val` locals.

**Adaptive quality:** If device cannot sustain 60fps (measured via `android.view.Choreographer` frame callback), reduce background node count by 10 per dropped frame until floor of 5. Restore on next GC cycle.

---

## VI. Aesthetic Non-Negotiables

1. **SpaceBlack** (`#0A0A0F`) background. Not `Color.Black`. The blue undertone creates depth. Never change this.
2. **Node labels**: `Barlow Condensed`, 10sp, `Color.White.copy(alpha = 0.85f)`. Minimum 10 characters before truncation. Never all-caps.
3. **Icon treatment**: `CircleShape` clip, 56.dp in FocusZone foreground. Anchor indicator: 8.dp `TaviAccent` dot, top-right corner. Crown-stage gets a 1.5dp `GlowAmber` ring at icon edge.
4. **Rounded corners** on FocusZone: `32.dp`. Background blur bleeds 4.dp outside this radius.
5. **Every transition has a semantic easing:**
   - Node entry (casual): `spring(dampingRatio=0.68f, stiffness=StiffnessLow)`
   - State ripple (alert): `spring(dampingRatio=0.80f, stiffness=StiffnessMedium)`
   - Urgent overlay (preflight, clarifier): `tween(180ms, FastOutSlowInEasing)`
   - Panel dismiss: `tween(120ms, LinearOutSlowInEasing)`
6. **Panels** (`ClipPanel`, `SnippetPanel`, `CapsulePanel`, `IntentClarifierCard`): `DepthMid` container color with `1.dp` border keyed to state color at `alpha 0.25f`. No hard borders in Idle state.
7. **Text in panels**: `TaviAccent` for primary content, `FallbackGrey` for meta (timestamp, type label), `BreathBlue` for action text.
8. The launcher must look and feel like nothing else on Android. Comparable reference points: Linear app's spatial model, Mercury Weather's fluid state, Clearance's brutalist restraint. TAVI is none of these. It is its own category.

---

## VII. Acceptance Criteria

Gate closes when all of the following pass:

| ID | Criterion | Verification |
|---|---|---|
| SL-01 | Background nodes are visually distinct from foreground — blurred, dim, small | Visual inspection with 30 bg nodes |
| SL-02 | Five foreground nodes breathe at five different rhythms simultaneously | Visual — no synchronized pulsing ever |
| SL-03 | Every TaviState change produces a ripple animation from screen center | Trigger each of the 9 states |
| SL-04 | PromptOrb shows a rotating gradient, not a flat color | Visual inspection |
| SL-05 | PromptOrb shadow elevation visibly increases in RiskDetected vs Idle | Side-by-side screenshot |
| SL-06 | Scope ring appears with scope label when a scope is active | Set scope tag; verify labeled ring |
| SL-07 | Grid color matches state color table in §IV for at least Idle, RiskDetected, Private | Trigger each; verify |
| SL-08 | 60fps at rest with 5 fg + 15 mid + 30 bg nodes | GPU profiler ≤16ms/frame |
| SL-09 | Zero `Paint` or `Path` allocations inside draw lambdas | Allocation Tracker in profiler |
| SL-10 | FocusZone breathing drives both border stroke and subtle scale | Visual inspection — scale must be felt, not seen |
| SL-11 | Crown-stage node has a rotating outer ring; SEED node is dim and small | Use test nodes at each stage |
| SL-12 | `TaviState.Capture` dims background 40% when intent clarifier is active | Tap any FocusZone node |

---

## Implementation Order (Recommended)

1. **SL-Grid** (§I voxel grid enhancement): state-keyed colors, weight gradient, horizon glow
2. **SL-Nodes** (§I node rendering): aura blur, individual breathing, stage colors, Crown ring
3. **SL-PromptOrb** (§II plasma orb): rotating gradient, state-keyed elevation, tap spring
4. **SL-Ripple** (§II state ripple): Canvas circle animation keyed to state
5. **SL-FocusZone** (§II FocusZone): scale breathing, scope ring, background scrim
6. **SL-StateColor** (§IV): wire state → color → all sub-composables
7. **SL-Performance** (§V): `Paint`/`Path` pre-allocation, adaptive node count
8. **SL-Haptics** (§III): haptic choreography wired to interaction events
