# Compte Rendu du Travail Pratique — Convertisseur de Devises Android Kotlin

**Auteur :** @TAHAR Haouet  
**Étudiante :** Ghada  
**Module :** Développement Mobile Android  
**Date :** Novembre 2026  
**Technologie :** Kotlin, Android SDK (API 24 - 34), View Binding, ConstraintLayout, TableLayout

---

## 1. Introduction et Objectifs du Projet

Le but de ce travail pratique est de concevoir et réaliser une application mobile native sous Android, intitulée **« Currency Converter »**. Cette application permet la conversion instantanée d'un montant d'une devise d'origine vers une devise de destination en appliquant des taux de change fixes par rapport à une devise pivot (l'Euro).

L'application répond à un ensemble d'exigences ergonomiques et fonctionnelles strictes :
- Construction d'une interface graphique bicolore structurée avec `ConstraintLayout` et `TableLayout`.
- Utilisation exclusive d'un clavier numérique personnalisé intégré à l'écran, bloquant l'usage du clavier virtuel système.
- Implémentation du **View Binding** pour garantir la sécurité du typage et éviter les appels obsolètes à `findViewById`.
- Utilisation de deux composants `Spinner` alimentés par un `ArrayAdapter` à partir d'un tableau de chaînes XML (`R.array.devises`).
- Gestion rigoureuse des règles de validation de saisie (maximum 10 caractères, point décimal unique, remplacement du 0 initial).
- Calculs de conversion précis avec arrondi à deux décimales et gestion des cas limites (montant nul, devises identiques, inversion).

---

## 2. Choix d'Architecture et d'Interface Graphique

### 2.1. Structure générale de l'écran (`activity_main.xml`)
L'interface est conçue pour respecter fidèlement la maquette du sujet :
1. **Zone d'affichage supérieure (~45 % de la hauteur de l'écran)** :
   - Fond turquoise uni `#2BB8E8`.
   - Titre centré en texte blanc (`20sp`, gras).
   - Deux lignes horizontales composées chacune d'un `Spinner` pour le choix de la devise et d'un `TextView` grand format (`48sp`) pour l'affichage de la valeur numérique.
   - Les montants saisi et converti sont soulignés par une ligne sombre de `2dp` (`underlineMontant` et `underlineResultat`), renforçant la lisibilité visuelle.
   - Un bouton d'inversion central (`ImageButton`, `btnInverser`) portant l'icône vectorielle de permutation verticale `ic_swap_vert`.
   - Un bouton de conversion stylisé (`Button`, `btnConvertir`) avec des bordures arrondies (forme pilule) et un texte contrasté.
2. **Zone inférieure du clavier numérique (~55 % de la hauteur de l'écran)** :
   - Fond clair `#F8F9FA`.
   - Utilisation d'un `TableLayout` de 4 lignes (`TableRow`) par 3 colonnes avec la propriété `android:stretchColumns="*"` pour une répartition égale et fluide de toutes les touches.
   - Les touches numériques sont stylisées sans bordure (`?android:attr/borderlessButtonStyle`), avec une taille de `32sp` et la couleur turquoise `#1BA6C9`.
   - La touche d'effacement « **C** » est mise en évidence avec la couleur rouge clair `#F0727A`.

### 2.2. Respect des bonnes pratiques Android
- **Externalisation totale des ressources** : Aucune valeur (ni texte, ni couleur) n'est codée en dur dans le fichier de layout XML. Tous les textes proviennent de `res/values/strings.xml` et les couleurs de `res/values/colors.xml`.
- **Verrouillage de l'orientation** : Comme exigé, `android:screenOrientation="portrait"` est configuré dans le `AndroidManifest.xml` afin d'éviter tout dysfonctionnement d'affichage sur les formats non adaptés.
- **Persistance de l'état** : Implémentation de `onSaveInstanceState` pour garantir la conservation du montant, du résultat et des sélections de devises en mémoire.

---

## 3. Logique Métier et Algorithmes de Conversion

### 3.1. Gestion de la saisie personnalisée (`ajouterCaractere`)
La saisie numérique via les boutons personnalisés est soumise aux règles suivantes :
- **Longueur maximale** : Si le texte du `tvMontant` atteint déjà 10 caractères, tout nouvel appui est ignoré afin de prévenir les débordements d'affichage ou les nombres trop grands.
- **Unicité du point décimal** : Si le caractère demandé est `.` et que le texte actuel contient déjà un point, la fonction s'interrompt immédiatement.
- **Remplacement du zéro initial** : À l'état initial, le montant affiché est `"0"`. Lorsque l'utilisateur tape un chiffre (1 à 9), ce chiffre remplace le `"0"`. En revanche, si l'utilisateur appuie sur `.`, la chaîne devient `"0."`.

### 3.2. Formule de calcul avec devise pivot (EUR)
Tous les taux sont exprimés pour 1 EUR :
- EUR : 1.00
- USD : 1.17
- TND : 3.40
- GBP : 0.87
- JPY : 172.50
- CAD : 1.62

Pour convertir un montant $M$ d'une devise $S$ (source) vers une devise $C$ (cible) :
1. Conversion vers la devise pivot (EUR) :
   $$M_{\text{EUR}} = \frac{M}{\text{taux}[S]}$$
2. Conversion de l'EUR vers la devise cible :
   $$\text{Résultat} = M_{\text{EUR}} \times \text{taux}[C]$$

**Cas particuliers gérés :**
- Si $S = C$ : $\text{Résultat} = M$ directement, sans opération intermédiaire.
- Si $M = 0$ : Détection lors du clic sur `btnConvertir` et affichage d'un message d'alerte `Toast` : *« Veuillez saisir un montant »*.
- Arrondi : Le résultat est toujours formaté avec `String.format(Locale.US, "%.2f", resultat)` pour garantir deux décimales exactes avec le point comme séparateur décimal standard.

### 3.3. Mécanisme d'inversion des devises
Lors de l'appui sur `btnInverser` :
1. Les indices des sélections des deux `Spinner` sont récupérés et échangés via `setSelection()`.
2. Si un résultat était déjà présent à l'écran, la méthode relance automatiquement le calcul avec les devises inversées, assurant une expérience utilisateur fluide et réactive.

---

## 4. Résultats des Tests et Validation

Les 4 cas de test imposés par le sujet ont été testés et validés par des tests unitaires JUnit (`CurrencyConverterTest.kt`) et sur émulateur :

| Cas n° | Montant initial | Devise Source | Devise Cible | Calcul intermédiaire | Résultat obtenu | Résultat attendu | Validation |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| 1 | 120 | USD | EUR | $120 / 1.17 \times 1.00 = 102.564...$ | **102.56** | 102.56 | ✅ Conforme |
| 2 | 100 | EUR | TND | $100 / 1.00 \times 3.40 = 340.00$ | **340.00** | 340.00 | ✅ Conforme |
| 3 | 50 | GBP | USD | $50 / 0.87 \times 1.17 = 67.241...$ | **67.24** | 67.24 | ✅ Conforme |
| 4 | 10 | EUR | EUR | $10 \text{ (identiques)}$ | **10.00** | 10.00 | ✅ Conforme |

---

## 5. Difficultés Rencontrées et Solutions Apportées

1. **Intégration du clavier in-app sans appel du clavier système** :
   - *Problème* : L'utilisation de champs `EditText` traditionnels déclenche automatiquement l'apparition du clavier logiciel Android, ce qui est contraire aux objectifs du TP.
   - *Solution* : Utilisation de composants `TextView` non éditables avec gestion programmatique des saisies via les écouteurs de clics des touches du `TableLayout`.
2. **Alignement et soulignement des zones d'affichage** :
   - *Problème* : Obtenir un soulignement élégant et fidèle à la maquette tout en conservant une taille de texte imposante (`48sp`).
   - *Solution* : Ajout de vues séparées `View` de hauteur `2dp` contraintes dynamiquement sous les `TextView` correspondants, garantissant un rendu identique sur toutes les densités d'écran.
3. **Mise à jour immédiate lors de l'inversion des devises** :
   - *Problème* : L'appel à `setSelection()` sur un `Spinner` est asynchrone, ce qui pouvait fausser un recalcul immédiat basé sur `selectedItem`.
   - *Solution* : Récupération directe des devises cibles inversées depuis l'adaptateur (`adapter.getItem(position)`) avant l'exécution du calcul.

---

## 6. Conclusion

L'application développée répond à l'ensemble du cahier des charges avec une fidélité visuelle absolue, un code propre, modulaire et entièrement commenté. Les tests unitaires et fonctionnels confirment la précision et la robustesse du convertisseur.
