---
name: Sartorial Excellence
colors:
  surface: '#131313'
  surface-dim: '#131313'
  surface-bright: '#393939'
  surface-container-lowest: '#0e0e0e'
  surface-container-low: '#1c1b1b'
  surface-container: '#201f1f'
  surface-container-high: '#2a2a2a'
  surface-container-highest: '#353534'
  on-surface: '#e5e2e1'
  on-surface-variant: '#d0c5af'
  inverse-surface: '#e5e2e1'
  inverse-on-surface: '#313030'
  outline: '#99907c'
  outline-variant: '#4d4635'
  surface-tint: '#e9c349'
  primary: '#f2ca50'
  on-primary: '#3c2f00'
  primary-container: '#d4af37'
  on-primary-container: '#554300'
  inverse-primary: '#735c00'
  secondary: '#eebd8e'
  on-secondary: '#472a06'
  secondary-container: '#64421d'
  on-secondary-container: '#dfaf81'
  tertiary: '#d0cdcd'
  on-tertiary: '#313030'
  tertiary-container: '#b4b2b2'
  on-tertiary-container: '#454544'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#ffe088'
  primary-fixed-dim: '#e9c349'
  on-primary-fixed: '#241a00'
  on-primary-fixed-variant: '#574500'
  secondary-fixed: '#ffdcbd'
  secondary-fixed-dim: '#eebd8e'
  on-secondary-fixed: '#2c1600'
  on-secondary-fixed-variant: '#61401b'
  tertiary-fixed: '#e5e2e1'
  tertiary-fixed-dim: '#c8c6c5'
  on-tertiary-fixed: '#1c1b1b'
  on-tertiary-fixed-variant: '#474746'
  background: '#131313'
  on-background: '#e5e2e1'
  surface-variant: '#353534'
typography:
  display-lg:
    fontFamily: Playfair Display
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Playfair Display
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-md:
    fontFamily: Playfair Display
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Hanken Grotesk
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  headline-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 8px
  sm: 16px
  md: 24px
  lg: 32px
  xl: 48px
  gutter: 16px
  margin-mobile: 20px
  margin-desktop: 64px
---

## Brand & Style

The design system is crafted to evoke a sense of **bespoke luxury, precision, and exclusivity**. It serves a discerning clientele who values craftsmanship and the tactile heritage of high-end tailoring. 

The aesthetic is a fusion of **Minimalism** and **Modern Corporate**, utilizing a "Quiet Luxury" approach where empty space is as important as the content. The interface must feel like a premium atelier: dark, focused, and impeccably organized. High-contrast accents in gold and tan provide a "halo effect" against the charcoal background, guiding the user through the digital tailoring process with the same care a master tailor would provide in person.

## Colors

This design system employs a sophisticated dark-mode palette to minimize eye strain and highlight the rich textures of fabrics shown in the UI.

- **Primary (Gold):** Used exclusively for high-priority actions, active states, and branding. It represents the "gold standard" of the service.
- **Secondary (Tan/Tobacco):** Used for subtle accents, category headers, and secondary interactions. It evokes the warmth of leather and classic workshop materials.
- **Backgrounds:** A tiered system of depth starting with **Deep Charcoal (#121212)** for the base, rising to **Surface Charcoal (#1E1E1E)** for cards and interactive elements.
- **Accents:** High-fidelity strokes and borders use a muted gold-tinted grey to maintain a luxury feel without overwhelming the user.

## Typography

The typography strategy relies on the contrast between the classic, authoritative **Playfair Display** (Serif) and the technical, modern **Hanken Grotesk** (Sans-serif).

- **Serif for Heritage:** Use for all headlines and brand moments to signal tradition and elegance.
- **Sans-Serif for Utility:** Use for body text, measurements, and functional labels to ensure clarity and high readability in the dark interface.
- **Letter Spacing:** Apply generous tracking to uppercase labels to mimic the branding found on high-end luxury labels.

## Layout & Spacing

The design system utilizes a **fixed-column grid** (4 columns for mobile, 12 for desktop) with generous margins to create a focused, centered experience.

- **The Golden Ratio:** Use the 8pt grid system but prioritize the "Golden Ratio" for vertical spacing between primary sections to maintain a graceful rhythm.
- **Focus Areas:** In the 3D editor and fabric selection screens, use "Breathable Padding" (minimum 32px) to separate controls from the product visualization.
- **Information Density:** Keep density low. Information should be revealed progressively to avoid clutter, reflecting the focused nature of a personalized fitting.

## Elevation & Depth

Visual hierarchy is achieved through **Tonal Layering** and **Subtle Glassmorphism**.

- **Stacked Surfaces:** Backgrounds use `#121212`. Cards use `#1E1E1E`. Floating elements like fabric selectors use `#2A2A2A`.
- **Soft Glows:** Instead of traditional black shadows, use extremely soft, low-opacity gold-tinted glows (Primary Color at 5-10% opacity) for high-elevation elements like "Confirm Selection" buttons.
- **Outer Borders:** Use 1px borders in a muted gold (`#D4AF37` at 20% opacity) to define containers without creating visual noise.
- **Backdrop Blur:** Use 16px to 24px of blur on navigation bars and modal overlays to maintain context while keeping the user focused on the active task.

## Shapes

The shape language is **Refined and Structured**. While the primary aesthetic is architectural, soft corners are used to make the digital experience feel approachable.

- **Standard Radius:** 8px (0.5rem) for cards and input fields.
- **Large Radius:** 16px (1rem) for bottom sheets and main product containers.
- **Interactive Elements:** Buttons should use a "Soft" 8px radius. Do not use pill shapes for primary buttons; maintain a rectangular form factor to mirror the structured lines of a suit.

## Components

### Buttons
- **Primary:** Gold background (`#D4AF37`) with black text. No border. Solid and confident.
- **Secondary:** Transparent background with a 1px Tan border.
- **Tertiary:** Text-only in Gold, all-caps with generous letter spacing.

### Input Fields
- Dark grey background (`#1E1E1E`) with a bottom-only 1px border. When focused, the border transitions to Gold. Labels should always be visible (top-aligned).

### Chips & Selectors
- Used for fabric types and sizes. Unselected: Dark surface with grey text. Selected: Gold border with a subtle 5% gold background tint.

### Lists & Steps
- Tailoring is a process. Use a vertical "Progress Rail" with gold nodes to indicate the current step (Measurements -> Fabric -> Style -> Finalize).

### Cards
- Use for "Recent Orders" or "Style Inspiration." Cards should have no shadow; instead, use a subtle 1px border to separate them from the background.

### 3D Preview Container
- The most important component. A full-bleed or large-format window with a subtle radial gradient (Dark Grey to Black) to create a "spotlight" effect on the suit.