# Eyenalyze


### Introduction

---

Cette application a été créée pour permettre la détection précoce de TSA (Trouble du Spectre de l'Autisme) chez les jeunes enfants grâce à une technologie d'eye-tracking.  
Elle permet à un opérateur, du domaine médical ou non, de venir sélectionner une expérience et de la lancer.  
L'application se charge de l'exportation des résultats afin qu'ils puissent être classifiés grâce à un algorithme de machine learning.  


### Installation

---

Vous devez aller sur le [github du projet](https://github.com/Guigz15/Sircus.git) afin de récupérer le lien vous permettant d'ajouter ce projet à IntelliJ (ou tout autre IDE).  
Pour ce qui est de la suite, je vais décrire les prochaines étapes à faire sur IntelliJ, sachant que c'est l'IDE utilisé durant tout le projet :  
* Créer une configuration Maven
  * Dans le champ ***Run*** mettre : **exec:java**
  * Cliquer sur ***Modify options*** et ***Add before launch task*** et mettre : **install**
* Brancher l'eye-tracker via USB-C ou USB-A avec l'adaptateur fourni dans la boîte mais assurez vous de le connecter également à une source d'électricité, seulement dans le cas de l'USB-A. 
* Vous pouvez alors lancer le projet en cliquant sur **Run** dans la barre d'outils ou encore Maj+F10.


### Création et lancement du JAR

---

1. **Créer un artifact dans IntelliJ**
   * Cliquer d'abord sur ***Fichier***, puis ***Structure du projet***
   * Ensuite, cliquer sur l'onglet ***Artifacts*** puis ***Ajouter***
   * Enfin, sélectionner ***JAR*** puis ***à partir des modules avec les dépendances*** et ***OK***
2. **Exécuter l'artifact**
   * Assurez-vous de la présence du fichier **MANIFEST.MF** qui doit se trouver au sein d'un dossier nommé **META-INF**
   * Cliquer sur ***Construire*** puis ***Construire les artifacts***
   * Ensuite, cliquer sur le nom de l'artifact que vous venez de créer et faites ***Construire***
   * Enfin, vous devriez trouver le JAR dans un dossier nommé **out**
3. **Mis à jour du JAR avec les fichiers Python**
   * Ouvrir un terminal dans le dossier où se trouve le JAR
   * Copier les fichiers Python nécessaires au fonctionnement de l'eye-tracker
   * Taper la commande suivante dans le terminal : `C:\Users\21701304t\Downloads\openjdk-16_windows-x64_bin\jdk-16\bin\jar uf sircus.jar PatientCalibration.py TobiiCalibration.py TobiiAcquisition.py`
   * Le chemin de ***jar*** dépend d'où vous avez placé le dossier *Java*
4. **Lancement du JAR**
   * Ouvrir un terminal dans le dossier où se trouve le JAR et les fichiers Python
   * Ajouter les dossiers medias, metaSequence, result et sequences
   * Enfin, exécuter la commande suivante : `C:\Users\21701304t\Downloads\openjdk-16_windows-x64_bin\jdk-16\bin\javaw.exe --module-path "C:\Users\21701304t\Downloads\openjfx-18.0.1_windows-x64_bin-sdk\javafx-sdk-18.0.1\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.media -jar sircus.jar`
   * Le premier chemin dépend d'où vous avez placé le dossier *Java* et le second dépend d'où vous avez placé le dossier *JavaFX*


### Contribution

---

Equipe de projet du S8 2021-2022 : 
* Guillaume CAZIN
* Guillaume ELAMBERT
* Hugo JULIEN
* Alexandre MILLARD
* Antoine ROURA
* Angèle ROUSSEL
* Quentin SCHAU


