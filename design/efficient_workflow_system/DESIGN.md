---
name: Efficient Workflow System
colors:
  surface: '#faf8ff'
  surface-dim: '#d2d9f4'
  surface-bright: '#faf8ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f3ff'
  surface-container: '#eaedff'
  surface-container-high: '#e2e7ff'
  surface-container-highest: '#dae2fd'
  on-surface: '#131b2e'
  on-surface-variant: '#464554'
  inverse-surface: '#283044'
  inverse-on-surface: '#eef0ff'
  outline: '#777586'
  outline-variant: '#c7c4d7'
  surface-tint: '#5148d7'
  primary: '#2a14b4'
  on-primary: '#ffffff'
  primary-container: '#4338ca'
  on-primary-container: '#c1beff'
  inverse-primary: '#c3c0ff'
  secondary: '#505f76'
  on-secondary: '#ffffff'
  secondary-container: '#d0e1fb'
  on-secondary-container: '#54647a'
  tertiary: '#363b3e'
  on-tertiary: '#ffffff'
  tertiary-container: '#4d5255'
  on-tertiary-container: '#c1c5c9'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e3dfff'
  primary-fixed-dim: '#c3c0ff'
  on-primary-fixed: '#100069'
  on-primary-fixed-variant: '#372abf'
  secondary-fixed: '#d3e4fe'
  secondary-fixed-dim: '#b7c8e1'
  on-secondary-fixed: '#0b1c30'
  on-secondary-fixed-variant: '#38485d'
  tertiary-fixed: '#dfe3e7'
  tertiary-fixed-dim: '#c3c7cb'
  on-tertiary-fixed: '#171c1f'
  on-tertiary-fixed-variant: '#43474b'
  background: '#faf8ff'
  on-background: '#131b2e'
  surface-variant: '#dae2fd'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-lg:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  sidebar-width: 280px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 32px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Style et Image de Marque

Le système repose sur une esthétique **Corporate Modern** qui privilégie l'efficacité, la clarté et la collaboration. L'objectif est de créer un environnement de travail serein où la hiérarchie de l'information est immédiate, permettant aux utilisateurs de se concentrer sur l'exécution des tâches sans distraction visuelle superflue.

La personnalité de l'interface est à la fois rigoureuse et dynamique. Elle utilise des lignes épurées et une structure de grille stricte pour inspirer la confiance, tout en intégrant des accents colorés vibrants pour signaler l'urgence et l'importance des actions. L'expérience doit évoquer la précision technique et la fluidité opérationnelle.

## Couleurs

La palette est conçue pour structurer l'espace de travail de manière logique :

- **Indigo Profond (Primaire) :** Utilisé pour les actions principales, les états actifs et l'identité de navigation. Il assoit l'autorité et le professionnalisme.
- **Gris Ardoise (Neutres) :** Une gamme de gris bleutés est utilisée pour les arrière-plans et les textes. Le texte principal utilise l'ardoise foncée pour une lisibilité optimale, tandis que les fonds de page utilisent les nuances les plus claires pour réduire la fatigue oculaire.
- **Accents de Priorité :** Des couleurs vives (Rouge, Ambre, Bleu) sont strictement réservées au statut des tâches et aux indicateurs de performance, créant un contraste immédiat avec l'environnement neutre.

## Typographie

Le système utilise exclusivement la police **Inter**. Son dessin géométrique et ses détails humanistes assurent une lisibilité exceptionnelle sur écran, même pour les données denses.

Les titres utilisent des graisses semi-bold ou bold avec un espacement de caractères légèrement réduit pour un aspect plus compact et moderne. Les étiquettes (labels) sont traitées en majuscules avec un espacement généreux pour les différencier clairement du texte courant. Sur mobile, les tailles de titres sont systématiquement réduites pour préserver l'espace vertical et éviter les coupures de mots inesthétiques.

## Mise en page et Espacement

Le système adopte une grille fluide de 12 colonnes pour le contenu principal, complétée par une barre latérale fixe à gauche.

- **Rythme vertical :** Basé sur un module de 8px pour assurer une cohérence visuelle parfaite entre tous les éléments.
- **Barre latérale :** Fixée à 280px sur desktop, elle disparaît dans un menu "hamburger" sur mobile.
- **Conteneurs :** Les listes de tâches et les tableaux de bord utilisent des marges de 24px (gutter) pour séparer les modules.
- **Adaptativité :** Sur mobile, les marges extérieures sont réduites à 16px et les colonnes s'empilent verticalement pour maintenir la clarté des informations.

## Élévation et Profondeur

La hiérarchie visuelle est établie principalement par des **couches tonales** et des bordures subtiles plutôt que par des ombres portées lourdes.

- **Niveau 0 (Fond) :** Gris ardoise très clair (#F8FAFC) pour le canevas principal.
- **Niveau 1 (Cartes/Contenu) :** Fond blanc pur avec une bordure fine d'un pixel (Slate-200) pour détacher les éléments du fond.
- **Niveau 2 (Survol/Interaction) :** Ombre portée très diffuse (blur 12px, opacité 5%) pour indiquer l'interactivité d'une carte de tâche.
- **Niveau 3 (Modales/Popups) :** Ombre plus marquée pour créer une séparation nette avec le contenu en arrière-plan, accompagnée d'un voile (overlay) semi-transparent.

## Formes

Le système utilise un niveau de courbure **Rounded (0.5rem)** pour équilibrer le sérieux professionnel et l'accessibilité moderne.

- **Boutons et Champs de saisie :** Rayon de 0.5rem (8px).
- **Cartes de tâches :** Rayon de 1rem (16px) pour une apparence plus douce et distincte.
- **Badges et Chips :** Style "Pill" (entièrement arrondis) pour contraster avec les éléments structurels rectangulaires.

## Composants

### Cartes de tâches (Task Cards)
Les cartes sont les unités de base. Elles doivent comporter une bordure discrète, le titre de la tâche en `title-lg`, et des badges colorés pour la priorité. Les avatars des collaborateurs sont regroupés en bas à droite avec un léger chevauchement.

### Formulaires
Les champs de saisie utilisent un fond blanc, une bordure Slate-300 et une étiquette en `label-md` placée au-dessus. L'état de focus est marqué par une bordure Indigo-500 de 2px et un halo léger.

### Boutons
- **Primaire :** Fond Indigo, texte blanc, sans ombre.
- **Secondaire :** Bordure Slate-300, texte Slate-700.
- **Priorité :** Boutons d'action rapide utilisant les couleurs d'accentuation pour les décisions critiques.

### Barre latérale (Sidebar)
La navigation utilise un fond Slate-900 (très sombre) avec des icônes en gris clair. L'élément actif est mis en avant par une bordure gauche épaisse Indigo et un changement de couleur de l'icône.

### Listes et Tableaux
Utilisation de lignes horizontales fines pour séparer les éléments, avec un état de survol (hover) gris très clair pour guider l'œil de l'utilisateur.