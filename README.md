# Tanzania Excursions

Projet final de POO Java – M1 JUNIA – Imène HOULLA

## Objectif

Application console qui gère les réservations d'une petite agence d'excursions guidées autour d'Arusha et Moshi, en Tanzanie. J'ai choisi ce sujet après un volontariat de deux mois dans une école en Tanzanie, pendant lequel j'ai fait plusieurs de ces excursions. Les lieux, les prestations et les règles de l'application s'inspirent de ce que j'ai vu sur place.

L'agence propose trois types d'excursions : des randonnées (Kilimandjaro, cascades de Materuni, mont Meru), des safaris (Tarangire, Ngorongoro, lac Manyara) et des visites de ville (Arusha, Moshi).

## Lancer l'application

Prérequis : Java 21 et Maven.

Dans IntelliJ : ouvrir le projet, puis lancer la méthode `main` de `Application`.

En ligne de commande, depuis la racine du projet :

```
mvn compile
java -cp target/classes com.imene.excursions.Application
```

L'application doit être lancée depuis la racine du projet, car les fichiers CSV sont lus dans `src/main/resources`.

## Fonctionnalités

- Afficher les excursions à venir, avec les places réservées et les prestations incluses
- Afficher les clients
- Réserver une excursion pour un client
- Annuler une réservation
- Afficher les réservations d'un client
- Annuler les excursions des 2 prochains jours qui n'ont pas assez de participants

Règles appliquées lors d'une réservation :
- on ne peut pas réserver une excursion passée, annulée ou complète ;
- randonnée : le niveau sportif du client doit être suffisant ;
- safari : le client doit avoir au moins 6 ans le jour du safari ;
- un client ne peut pas avoir deux excursions le même jour.

Calcul du prix :
- randonnée : prix de base + lunch box ;
- safari : prix de base + droits d'entrée du parc + lunch box ;
- visite de ville : prix de base, moitié prix pour les moins de 12 ans.

## Classes principales

| Package | Classes | Rôle |
|---|---|---|
| `model` | `Excursion`, `Hike`, `Safari`, `CityTour`, `Customer`, `Reservation`, `MealIncluded`, enums `Level`, `Inclusion`, `ReservationStatus` | Objets métier et leurs règles |
| `service` | `ExcursionService`, `CustomerService`, `ReservationService` | Recherche, réservation, annulation |
| `loader` | `DataLoader`, `CsvDataLoader` | Chargement des données |
| `exception` | 4 exceptions personnalisées | Erreurs métier |
| `ui` | `ConsoleMenu` | Affichage et saisie |
| — | `Application` | Point d'entrée, assemble les différentes parties |

## Héritage

`Hike`, `Safari` et `CityTour` héritent de la classe `Excursion`. Une randonnée, un safari et une visite de ville sont tous des excursions : ils ont un identifiant, un nom, une date, un prix de base et un nombre de places. Cette partie commune est écrite une seule fois dans `Excursion`.

## Polymorphisme

`Excursion` déclare trois méthodes abstraites, que chaque sous-classe redéfinit à sa façon :
- `checkEligibility(Customer)` : niveau pour une randonnée, âge pour un safari, aucune condition pour une visite ;
- `calculatePrice(Customer)` : chaque type calcule son prix différemment ;
- `getInclusions()` : chaque type a ses propres prestations incluses.

`ReservationService` et `ConsoleMenu` manipulent uniquement des `Excursion`, sans `instanceof`. Par exemple, `excursion.checkEligibility(customer)` dans `ReservationService.book()` exécute la bonne règle selon le type réel de l'excursion.

## Classe abstraite et interface

**`Excursion` est une classe abstraite** : une excursion « en général » n'existe pas, c'est toujours une randonnée, un safari ou une visite. Elle contient les attributs et méthodes communs, et oblige chaque sous-classe à définir ses propres règles.

**`MealIncluded` est une interface** : seules les randonnées et les safaris incluent une lunch box. Ce n'est pas une caractéristique de toutes les excursions, donc elle n'a pas sa place dans `Excursion`. L'interface définit le prix de la lunch box à un seul endroit, avec une méthode `default` pour ne pas le répéter dans `Hike` et `Safari`.

## Exceptions personnalisées

- `ExcursionFullException` : l'excursion n'a plus de place
- `ConditionNotMetException` : le client ne remplit pas la condition (niveau ou âge)
- `DateConflictException` : le client a déjà une excursion ce jour-là
- `InvalidReservationException` : excursion passée ou annulée, réservation introuvable ou déjà annulée

Ce sont des exceptions vérifiées (`extends Exception`) : le menu est obligé de les gérer et affiche un message clair à l'utilisateur.

Les exceptions standards sont aussi utilisées : `IllegalArgumentException` quand un objet est créé avec des données invalides, `IllegalStateException` pour protéger l'état d'un objet, et `IOException` si un fichier est introuvable.

## Chargement des données

Au démarrage, `Application` charge deux fichiers CSV de `src/main/resources` grâce à `CsvDataLoader` :
- `excursions.csv` : une ligne par excursion. La colonne `type` (`HIKE`, `SAFARI`, `CITY_TOUR`) indique quelle classe créer, et la colonne `extra` contient le niveau requis (randonnée) ou les droits du parc (safari) ;
- `customers.csv` : une ligne par client.

Une ligne mal remplie est ignorée avec un message, sans arrêter le programme. Si un fichier est introuvable, l'application affiche une erreur et s'arrête.

## Principes SOLID

- **S (responsabilité unique)** : chaque classe a un seul rôle. Le modèle porte les règles, les services gèrent les opérations, le loader lit les fichiers, le menu affiche, et `Application` assemble le tout.
- **O (ouvert/fermé)** : pour ajouter un nouveau type d'excursion, il suffit de créer une sous-classe d'`Excursion` et de l'ajouter au loader. Les services et le menu n'ont pas à être modifiés.
- **L (substitution de Liskov)** : `Hike`, `Safari` et `CityTour` peuvent être utilisées partout où une `Excursion` est attendue.
- **I (ségrégation des interfaces)** : `CityTour` n'implémente pas `MealIncluded`, elle n'a donc pas de méthode de repas qui ne la concerne pas.
- **D (inversion des dépendances)** : les services reçoivent leurs données par leur constructeur, et `Application` utilise l'interface `DataLoader`. On pourrait passer au JSON en créant une autre classe, sans modifier le reste.

## Décisions techniques

1. **Le prix est calculé au moment de la réservation et enregistré dans `Reservation`.** Il ne change plus ensuite, même si les données de l'excursion évoluent.
2. **La date du jour est passée en paramètre** (`isPast(today)`, `book(..., today)`) au lieu d'appeler `LocalDate.now()` partout. Les tests utilisent ainsi une date fixe et donnent toujours le même résultat.
3. **Un seul enum `Level`** sert à la fois pour le niveau du client et le niveau requis d'une randonnée, ce qui rend la comparaison simple. Une randonnée de niveau `ADVANCED` est considérée comme difficile et inclut des porteurs.
4. **L'âge est calculé à partir de la date de naissance, à la date de l'excursion.** C'est l'âge le jour de l'excursion qui compte pour le safari et la réduction enfant.
5. **Les règles métier sont vérifiées dans `ReservationService`**, qui lance les exceptions personnalisées. Les classes du modèle se protègent en plus avec des `IllegalStateException`, en dernière sécurité.

## Lancer les tests

Dans IntelliJ : clic droit sur `src/test/java` → **Run 'All Tests'**.

En ligne de commande :

```
mvn test
```

Les tests JUnit 5 couvrent les règles des classes du modèle et de `ReservationService` : cas normaux, cas limites (enfant qui a pile 6 ans, 12e anniversaire le jour de la visite, excursion complète) et cas d'erreur.