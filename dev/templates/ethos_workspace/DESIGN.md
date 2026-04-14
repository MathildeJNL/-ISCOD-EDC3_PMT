# Design System Strategy: The Silent Orchestrator

## 1. Overview & Creative North Star
The objective of this design system is to transform the traditional, often cluttered project management interface into a high-end, editorial experience. We are moving away from "utility-first" design toward **"The Silent Orchestrator"**—a creative North Star that prioritizes cognitive ease, rhythmic layout, and a sense of calm authority.

This system rejects the "standard" SaaS aesthetic characterized by heavy borders and rigid, uniform grids. Instead, we embrace a **Tonal Editorial** approach. By leveraging intentional asymmetry, expansive white space, and a sophisticated pastel palette, we create a tool that feels less like a spreadsheet and more like a curated workspace. We use high-contrast typography scales and layered surfaces to guide the eye, ensuring that complexity never feels chaotic.

---

## 2. Colors & Tonal Logic
Our palette is a sophisticated blend of professional blues and soft, functional pastels. It is designed to reduce eye strain while providing clear semantic meaning.

### The "No-Line" Rule
To achieve a premium, custom feel, **1px solid borders are strictly prohibited for sectioning.** Boundaries must be defined solely through background color shifts or subtle tonal transitions. 
- A `surface-container-low` sidebar sitting on a `surface` background creates a clear, sophisticated boundary without the visual noise of a line.
- Use `surface-container-highest` only for the most critical interactive elements to provide a "tactual" feel.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers of fine paper. 
- **Base Layer:** `surface` (#f8f9fa)
- **Structural Sections:** `surface-container-low` (#f1f4f5)
- **Interactive Cards:** `surface-container-lowest` (#ffffff)
- **Active/Hover States:** `surface-container-high` (#e5e9eb)

By nesting a `surface-container-lowest` card within a `surface-container-low` wrapper, you create a natural lift that signals importance without needing heavy shadows.

---

## 3. Typography: The Editorial Voice
We use a dual-typeface system to balance professional authority with functional precision.

- **The Voice (Manrope):** Used for `display` and `headline` scales. Its geometric yet warm character provides a high-end editorial feel. Use `display-lg` (3.5rem) for high-level project metrics to create "hero" moments within a functional dashboard.
- **The Engine (Inter):** Used for `title`, `body`, and `label` scales. Inter is selected for its extreme legibility at small sizes. 

**Typography as Hierarchy:** Instead of bolding everything, use scale. A `headline-sm` in `on-surface-variant` is often more effective than a bold `title-sm` in `on-surface`.

---

## 4. Elevation & Depth
In this system, depth is a functional tool, not an artistic effect. We convey hierarchy through **Tonal Layering** and **Ambient Light.**

### The Layering Principle
Depth is achieved by stacking the surface-container tiers. 
- **Level 0:** `surface` (The canvas)
- **Level 1:** `surface-container` (The workspace)
- **Level 2:** `surface-container-lowest` (The active task card)

### Ambient Shadows
When a "floating" effect is required for modals or popovers, use extra-diffused shadows.
- **Shadow Specs:** Blur: 24px–32px | Opacity: 4%–6% | Color: `on-surface` (tinted).
- Shadows should never look "dark"; they should look like the absence of light beneath a physical object.

### The "Ghost Border" Fallback
If a border is required for extreme accessibility (e.g., input fields), use a **Ghost Border**: `outline-variant` (#adb3b5) at **20% opacity**. Never use 100% opaque borders.

---

## 5. Components

### Buttons
- **Primary:** `primary` (#396383) background with `on-primary` (#f6f9ff) text. Corner radius: `DEFAULT` (8px).
- **Secondary:** `secondary-container` (#c7eae1) background. This provides that "soft green" professional pastel touch.
- **Tertiary:** No background; use `primary` text. Padding should align with the 8px grid (e.g., 8px v / 16px h).

### Input Fields
Avoid the "boxed-in" look. Use `surface-container-high` as a subtle background fill with an `8px` corner radius. The label should use `label-md` and sit 4px above the field.

### Cards & Lists
**Forbid the use of divider lines.** 
- Separate list items using 8px or 16px of vertical white space.
- For Kanban boards, use `surface-container-low` for the column background and `surface-container-lowest` for the individual task cards. This creates a clear "container-within-container" logic.

### Chips (Status Indicators)
- **In-Progress:** `primary-container` (#aad3f9) text on `on-primary-fixed` background.
- **Review:** `tertiary-container` (#ecdcfd) text on `on-tertiary-fixed` background (The "Lavender" touch).
- **Completed:** `secondary-container` (#c7eae1) text on `on-secondary-fixed` background (The "Green" touch).

---

## 6. Do's and Don'ts

### Do
- **Do** use `8px` and `16px` increments for all spacing to maintain the rhythmic grid.
- **Do** use `surface-container-lowest` for cards to make them "pop" against the `surface` background.
- **Do** use `headline-lg` for empty states to make them feel like intentional editorial pages rather than "missing" data.

### Don't
- **Don't** use 1px solid borders to separate the sidebar from the main content; use a background color shift.
- **Don't** use pure black (#000000) for text. Use `on-surface` (#2d3335) for better readability and a "premium" ink feel.
- **Don't** use gradients or glassmorphism. Trust the tonal shifts and the quality of the typography to provide the "modern" feel.
- **Don't** crowd the interface. If in doubt, increase the white space between sections to `32px` or `48px`.