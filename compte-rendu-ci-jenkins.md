# Compte rendu - Pipeline d'integration continue Jenkins

Team : Masutti - Mattera - Shin

## 1. Contexte du projet

Ce projet consiste a mettre en place une chaine d'integration continue pour une application Java Maven contenant volontairement des mauvaises pratiques de developpement.

Les objectifs etaient les suivants :

- automatiser la compilation, les tests et le packaging avec Jenkins ;
- connecter Jenkins a SonarQube ;
- analyser la qualite et la securite du code ;
- corriger les problemes detectes ;
- obtenir un pipeline Jenkins en succes avec un Quality Gate SonarQube valide ;
- realiser le bonus Docker en construisant et lancant une image de l'application.

Environnement utilise :

- application Java 17 avec Maven ;
- Jenkins lance dans Docker ;
- SonarQube lance localement ;
- depot GitHub utilise comme source du pipeline ;
- Docker utilise pour construire et lancer l'image applicative.


## 2. Mise en place du pipeline Jenkins

Le pipeline Jenkins a ete configure a partir du fichier `Jenkinsfile` present dans le depot GitHub.

Les principales etapes du pipeline sont :

1. recuperation du code source depuis GitHub ;
2. compilation Maven ;
3. execution des tests unitaires ;
4. generation du rapport de couverture JaCoCo ;
5. analyse SonarQube ;
6. verification du Quality Gate ;
7. packaging Maven de l'application.

Le pipeline utilise les outils Jenkins suivants :

- JDK 17 ;
- Maven ;
- plugin SonarQube Scanner ;
- plugin JUnit pour publier les resultats de tests.


## 3. Etat initial du projet

Lors des premiers lancements, le pipeline Jenkins etait en echec.

Les problemes constates etaient les suivants :

- le `Jenkinsfile` contenait des blocs vides, ce qui empechait Jenkins de compiler le pipeline ;
- le stage Docker echouait car la commande `docker` n'etait pas disponible dans le conteneur Jenkins ;
- le Quality Gate SonarQube passait ensuite en erreur apres analyse du code ;
- le code Java contenait plusieurs mauvaises pratiques et failles potentielles.

Exemples de messages observes dans Jenkins :

```text
No steps specified for branch
```

```text
docker: not found
ERROR: script returned exit code 127
```

```text
Quality gate is 'ERROR'
ERROR: Pipeline aborted due to quality gate failure
Finished: FAILURE
```

![Pipeline Jenkins en echec](screenshots/Build_failed_jenkins_v2.png)

![Pipeline Jenkins en echec 2](screenshots/Build_failed_jenkins.png)


## 4. Audit SonarQube initial

SonarQube a permis d'identifier plusieurs problemes dans le code source.

Les principaux points releves etaient :

- utilisation de mots de passe ou identifiants en dur ;
- utilisation de `System.out.println` au lieu d'un logger ;
- risque d'injection SQL par concatenation de chaine ;
- gestion manuelle des ressources JDBC ;
- blocs `catch` ou traitements d'erreur insuffisants ;
- code mort ou variables inutilisees ;
- complexite inutile dans certaines methodes ;
- couverture de tests insuffisante.


![Issues SonarQube](screenshots/SonarQube_Issues.png)

![SecurityHotSpot SonarQube](screenshots/SonarQube_SecurityHotspot.png)

![CodeSmells SonarQube](screenshots/SonarQube_CodeSmells.png)


## 5. Journal des corrections

| Probleme detecte | Fichier concerne | Correction apportee |
| --- | --- | --- |
| Mot de passe en dur | `UserService.java` | Suppression du secret code en dur et utilisation de variables d'environnement. |
| Identifiants applicatifs en dur | `Main.java`, `UserService.java` | Recuperation des valeurs via `System.getenv`. |
| `System.out.println` | `Main.java`, `UserService.java` | Remplacement par `java.util.logging.Logger`. |
| Injection SQL possible | `UserService.java` | Remplacement de la concatenation SQL par un `PreparedStatement`. |
| Ressources JDBC fermees manuellement | `UserService.java` | Utilisation de `try-with-resources`. |
| Gestion d'erreur faible | `UserService.java` | Journalisation des erreurs via le logger. |
| Code mort et variables inutilisees | `UserService.java` | Suppression des variables inutiles et de la methode trop complexe. |
| Tests insuffisants | `UserServiceTest.java`, `MainTest.java` | Ajout de tests JUnit supplementaires. |
| Dependence a une vraie base de donnees | `UserServiceTest.java`, `pom.xml` | Ajout d'une base H2 en memoire pour tester JDBC sans MySQL externe. |
| Stage Docker bloquant Jenkins | `Jenkinsfile` | Retrait du build Docker du pipeline principal, conserve comme bonus local. |


## 6. Tests et couverture

Les tests unitaires ont ete renforces pour couvrir les principaux comportements de l'application.

Tests ajoutes ou ameliores :

- connexion refusee si les identifiants ne sont pas configures ;
- connexion acceptee avec des identifiants valides fictifs ;
- connexion refusee avec un mauvais mot de passe fictif ;
- recherche utilisateur impossible si le nom est vide ;
- recherche utilisateur impossible si la configuration base de donnees est absente ;
- test JDBC avec une base H2 en memoire ;
- test du point d'entree `Main`.

L'utilisation de H2 permet de tester la requete SQL preparee sans dependre d'une vraie base MySQL externe.

## 7. Resultat final du pipeline

Apres correction, le pipeline Jenkins s'execute avec succes.

Les etapes suivantes sont validees :

- checkout du depot GitHub ;
- compilation Maven ;
- execution des tests JUnit ;
- generation du rapport JaCoCo ;
- analyse SonarQube ;
- Quality Gate valide ;
- packaging Maven.

Message final attendu dans Jenkins :

```text
Finished: SUCCESS
```

![Overview Jenkins en succes](screenshots/Build_success_jenkins_overview.png)

![Build Jenkins en succes](screenshots/Build_success_jenkins.png)

![SonarQube Overview success](screenshots/SonarQube_Overview_success.png)

![SonarQube Issues success](screenshots/SonarQube_Issues_success.png)

## 8. Bonus Docker

Le fichier `Dockerfile` permet de creer une image Docker de l'application Java.

Commandes utilisees :

```powershell
docker build -t epsi/bad-practices-app:latest .
```

```powershell
docker run --name bad-practices-app epsi/bad-practices-app:latest
```

Lors du lancement du conteneur, l'application demarre correctement puis se termine normalement, car il s'agit d'une application console et non d'un service web permanent.

Sortie observee :

```text
INFO: Demarrage de l'application...
INFO: Aucun identifiant de connexion fourni.
WARNING: La connexion a la base de donnees n'est pas configuree.
```

Pour afficher la preuve d'execution :

```powershell
docker ps -a
```


## 9. Bilan technique

La mise en place du pipeline permet maintenant de detecter automatiquement les problemes suivants :

- erreurs de compilation ;
- echecs de tests unitaires ;
- baisse de couverture ;
- code smells ;
- vulnerabilites ou security hotspots ;
- non-respect du Quality Gate SonarQube.

Le pipeline protege donc l'application contre les regressions futures. Une modification qui degrade la qualite du code peut etre bloquee automatiquement avant d'etre consideree comme livrable.

## 10. Conclusion

Le projet a permis de mettre en place une chaine d'integration continue complete autour d'une application Java Maven.

Le code initial contenait plusieurs mauvaises pratiques. Apres audit SonarQube, les problemes principaux ont ete corriges, les tests ont ete renforces et le pipeline Jenkins est passe en succes.

Le bonus Docker a egalement ete realise avec la construction et l'execution locale de l'image de l'application.

