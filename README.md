# CurrencyConverter — Application Android de Conversion de Devises

Application Android développée en **Kotlin** sous **Android Studio**, permettant de convertir instantanément un montant d'une devise vers une autre à partir de taux de change fixes en utilisant l'Euro (EUR) comme devise pivot.

Réalisé dans le cadre du TP Android Kotlin par **@TAHAR Haouet**.

---

## 📱 Fonctionnalités et Règles Métier

- **Interface moderne et responsive** :
  - **Zone d'affichage supérieure** (~45% de l'écran) sur fond bleu turquoise uni (`#2BB8E8`).
  - **Clavier numérique personnalisé** en zone inférieure avec `TableLayout` (4 lignes × 3 colonnes) sur fond clair.
- **Sélection des devises** :
  - Deux `Spinner` alimentés dynamiquement par un `ArrayAdapter` depuis la ressource `R.array.devises` (`strings.xml`).
  - Sélection par défaut : **USD** en devise source, **EUR** en devise cible.
- **Saisie sécurisée via clavier in-app** :
  - La saisie se fait uniquement par les touches de l'application (pas de clavier virtuel système).
  - Montant initial affiché à `0` : le premier chiffre saisi remplace le zéro.
  - La touche « **C** » (en rouge clair `#F0727A`) réinitialise le montant à `0` et vide le résultat.
  - **Contrainte de décimale** : un seul point décimal est autorisé (les pressions suivantes sur « . » sont ignorées).
  - **Limite de taille** : la saisie est restreinte à **10 caractères maximum**.
- **Logique de conversion (Devise pivot : EUR)** :
  - Formule : 
    $$\text{montant en EUR} = \frac{\text{montant}}{\text{taux(source)}}$$
    $$\text{résultat} = \text{montant en EUR} \times \text{taux(cible)}$$
  - Cas particulier : si la source et la cible sont identiques, $\text{résultat} = \text{montant}$ sans erreur.
  - Si le montant saisi vaut 0, le clic sur **CONVERTIR** affiche un `Toast` : *« Veuillez saisir un montant »*.
  - Le résultat est formaté avec un arrondi strict à **2 décimales** (`String.format(Locale.US, "%.2f", ...)`).
- **Inversion rapide des devises** :
  - Clic sur le bouton d'inversion (`btnInverser`) : intervertit les deux sélections de `Spinner`.
  - Si un résultat est déjà affiché, la conversion est automatiquement recalculée.
- **Architecture & Bonnes pratiques** :
  - **View Binding** activé (`buildFeatures { viewBinding = true }`).
  - **Orientation bloquée en portrait** (`android:screenOrientation="portrait"`).
  - Gestion du cycle de vie et persistance d'état avec `onSaveInstanceState`.
  - Zéro valeur en dur dans les fichiers XML (`strings.xml` et `colors.xml` centralisés).

---

## 💱 Table des Taux de Change Fixes

| Devise | Code ISO | Taux pour 1 EUR |
| :--- | :---: | :---: |
| Euro | **EUR** | 1.00 |
| Dollar américain | **USD** | 1.17 |
| Dinar tunisien | **TND** | 3.40 |
| Livre sterling | **GBP** | 0.87 |
| Yen japonais | **JPY** | 172.50 |
| Dollar canadien | **CAD** | 1.62 |

---

## 🧪 Cas de Test Validés

| Montant Saisi | Devise Source | Devise Cible | Résultat Attendu | Statut |
| :---: | :---: | :---: | :---: | :---: |
| **120** | USD | EUR | **102.56** | ✅ Validé |
| **100** | EUR | TND | **340.00** | ✅ Validé |
| **50** | GBP | USD | **67.24** | ✅ Validé |
| **10** | EUR | EUR | **10.00** | ✅ Validé |

---

## 🛠️ Structure du Projet

```text
CurrencyConverter/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/currencyconverter/
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── bg_btn_convertir.xml
│   │   │   │   │   └── ic_swap_vert.xml
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── spinner_item.xml
│   │   │   │   │   └── spinner_dropdown_item.xml
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml
│   │   │   │       ├── strings.xml
│   │   │   │       └── themes.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/example/currencyconverter/
│   │       └── CurrencyConverterTest.kt
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml
├── screenshots/
│   ├── 01_demarrage.png
│   ├── 02_test_120_USD_EUR.png
│   ├── 03_test_100_EUR_TND.png
│   ├── 04_test_50_GBP_USD.png
│   └── 05_test_10_EUR_EUR.png
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
└── COMPTE_RENDU.md
```

---

## 🚀 Exécution

1. Ouvrir le dossier du projet dans **Android Studio**.
2. Laisser Gradle synchroniser les dépendances (`Sync Project with Gradle Files`).
3. Lancer sur l'émulateur Android (ex. `Pixel 7 API 34+`) en cliquant sur **Run 'app'** (`Shift + F10`).
