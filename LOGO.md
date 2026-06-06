# 🎨 Logo & identité visuelle — guide de livraison

Ce document décrit **ce dont j'ai besoin de ta part** pour intégrer ton logo dans
l'application QCounter, et **les couleurs** déjà utilisées dans l'app.

> En pratique : crée les fichiers décrits ci-dessous (idéalement en **SVG**), dépose-les
> dans le dossier `QCounter/assets-logo/` (crée-le si besoin) ou envoie-les moi, et je
> m'occupe de la conversion + intégration (icône de lancement, logo de la barre, etc.).

---

## 1. Ce que j'attends (par ordre de priorité)

### A. Icône de lancement « adaptative » (Android 8+) — **indispensable**
Android compose l'icône à partir de **deux calques séparés**, puis applique lui-même un
masque (cercle, arrondi, etc.). Il me faut donc **deux fichiers distincts** :

| Calque | Contenu | Canevas | Zone utile |
|--------|---------|---------|------------|
| **Avant-plan** (`foreground`) | Le symbole seul, **fond transparent** | 108 × 108 dp | Garder tout le dessin **dans le cercle central de 66 dp** (≈ les 2/3 du centre). Les bords sont rognés par le masque. |
| **Arrière-plan** (`background`) | Couleur unie ou motif simple, **plein cadre** | 108 × 108 dp | Tout le carré (pas de transparence) |

- Format idéal : **SVG** (ou PDF vectoriel). À défaut, **PNG transparent 432 × 432 px**.
- Ne mets **pas** d'ombre portée ni de coins arrondis dans tes fichiers : Android s'en charge.

### B. Version monochrome (Android 13+ « icônes thématisées ») — recommandé
Le **même symbole** que l'avant-plan, mais **en une seule couleur unie** (blanc ou noir),
fond transparent, même canevas 108 × 108 dp / zone utile 66 dp. Android la recolore selon
le fond d'écran de l'utilisateur.

### C. Logo affiché dans l'app (barre du haut) — recommandé
Un **symbole carré** (ou avec le mot « QCounter ») qui s'affiche en haut de l'écran
d'accueil. Format **SVG** de préférence. S'il est carré et lisible à petite taille
(~36 dp), je peux réutiliser l'avant-plan + arrière-plan ; mais une version pensée pour
petit format est mieux.

### D. (Optionnel) Icône Play Store
Si publication un jour : **PNG 512 × 512 px, 32 bits, sans transparence**.

---

## 2. Contraintes & conseils

- **Simplicité** : l'icône doit rester lisible à 48 dp. Évite les détails fins et le texte
  dans l'icône de lancement.
- **Contraste** : le symbole de l'avant-plan doit ressortir nettement sur la couleur
  d'arrière-plan (voir palette ci-dessous).
- **Cohérence de marque** : l'app utilise une identité **bleu ciel**. Idéalement, l'icône
  reprend ce bleu (arrière-plan) avec un symbole blanc — mais tu es libre.
- **Centrage** : pour l'avant-plan, laisse une marge tout autour (zone de sécurité 66 dp),
  sinon le symbole sera coupé sur les masques en cercle.

### Ce qu'il y a actuellement (placeholder à remplacer)
Un « Q » blanc stylisé (anneau + petite queue) sur fond bleu ciel. Fichiers concernés que
je remplacerai par ta version :
- `app/src/main/res/drawable/ic_launcher_foreground.xml` (avant-plan)
- `app/src/main/res/drawable/ic_launcher_background.xml` (arrière-plan)
- `app/src/main/res/drawable/ic_logo.xml` (logo de la barre du haut)
- les `mipmap-anydpi*/ic_launcher*.xml` (repli pour Android 7)

---

## 3. 🎨 Palette de couleurs utilisée dans l'app

### Couleurs de marque (bleu ciel)
| Rôle | Hex | Aperçu |
|------|-----|--------|
| **Bleu ciel — primaire / marque** | `#0288D1` | fond de l'icône, logo, en-têtes, accent primaire |
| Bleu ciel clair (thème sombre) | `#8ED1F2` | primaire en mode sombre |
| Conteneur bleu doux (clair) | `#CDE8FA` | fond de la carte « héro » (mode clair) |
| Texte sur conteneur (clair) | `#06324A` | texte/chiffres dans la carte héro |
| Conteneur bleu (sombre) | `#0B3A52` | carte héro (mode sombre) |
| Texte sur conteneur (sombre) | `#CDE8FA` | texte dans la carte héro (sombre) |

### Accents des indicateurs (3 palettes sélectionnables en réglages)
Les couleurs des **chiffres / indicateurs** sont configurables par l'utilisateur parmi
3 presets fixes (le logo et le thème restent bleu ciel) :

| Preset | Directe | Question | Esquive |
|--------|---------|----------|---------|
| **Ciel** (défaut) | `#0288D1` (bleu ciel) | `#E53935` (rouge) | `#64748B` (gris ardoise) |
| **Forêt** | `#059669` (émeraude) | `#D97706` (ambre) | `#475569` (ardoise) |
| **Crépuscule** | `#4F46E5` (indigo) | `#DB2777` (magenta) | `#0D9488` (sarcelle) |

### Neutres
| Rôle | Hex |
|------|-----|
| Blanc (symbole sur fond coloré) | `#FFFFFF` |

> Ces valeurs sont définies dans
> `app/src/main/java/ma/wave/qcounter/ui/theme/Color.kt`. Si tu choisis une teinte de bleu
> différente pour le logo, dis-le moi : j'alignerai le thème de l'app pour rester cohérent.

---

## 4. Récapitulatif des fichiers à me fournir

- [ ] `foreground` (symbole, fond transparent) — **SVG** ou PNG 432×432
- [ ] `background` (fond plein) — **SVG** ou PNG 432×432 (ou juste une couleur hex)
- [ ] `monochrome` (symbole une couleur, transparent) — *recommandé*
- [ ] `logo` barre du haut (carré, lisible petit) — *recommandé*
- [ ] icône Play Store 512×512 — *optionnel*

Envoie-les tels quels, je gère l'intégration. 🙌
