# Rapport du Projet Alpano, CS 108
#### Maxence Jouve et Robin Mamie

## Parallélisation
Le projet a en partie été parallélisé.
Cette implémentation s'est principalement faite dans la classe `PanoramaComputer`, mais aussi dans `PanoramaComputerBean`.
Dans la première, la parallélisation sert à accélérer le calcul du Panorama.
Dans la seconde, elle permet de séparer le thread JavaFX du reste du calcul, permettant de pouvoir interagir avec l'interface graphique même lorsque le calcul est lancé.

## Sauvegarde/Chargement
Les paramètres peuvent être sauvegardés et chargés.
La sauvegarde crée également un fichier *png* en plus des fichiers sérialisés contenant le panorama ainsi que les noms des sommets.
Les images se trouvent dans le dossier *img* et les paramètres sauvgardés dans *save*.

Il suffit de donner un nom à la sauvegarde.
Si le nom existe déjà, une fenêtre demandant une confirmation s'affichera.

Le chargement offre à l'utilisateur une vue sur les paramètres sélectionnés, en appelant la méthode `toString()` de `PanoramaUserParameters`.

## SuperHgtDEM
Une autre façon de combiner les fichiers Hgt est proposée.
Le SuperHgtDiscreteElevationModel précharge tous les fichiers *hgt* du projet - ou seulement une partie si spécifié ainsi.
Ces fichiers sont ensuite chargés dans un tableau.
L'algorithme retrouvant l'index du fichier correspondant aux coordonnées entrées se fait par soustraction successive de ces dernières.
L'opération est bien plus rapide qu'une imbrication de CompositeDEM.

## Fichier .hgt - courbe de Hilbert

La classe `HilbertDiscreteElevationModel` s'occupe de créer des fichiers *hhgt* (pour *Hilbert hgt*) avant d'ensuite y retrouver les valeurs correspondant aux index entrés.

L'implémentation est peut-être optimisée en ce qui concerne le cache, mais le calcul prend beaucoup de temps, ce qui fait que tout gain éventuellement effectuée auparavant s'en retrouve perdu...

Un fichier *hhgt* est fourni de base, mais d'autres peuvent être créés afin de tester d'autres zones.
Les CompositeDEM peuvent aussi être utilisés.

## Changement dynamique de MNT

Pour tester les deux implémentations différentes des fichiers *hgt*, une option permettant de changer dynamiquement de MNT a été introduite.

## Ajouts de Labels au Panorama

L'utilisateur peut rajouter des Labels au Panorama.
Plusieurs données sont nécessaires à leur création :

- Son nom
- Sa longitude
- Sa latitude
- Sa "priorité" : le point est-il prioritaire par rapport aux autres sommets/labels ou non ?
Échelle allant de -5 à 5, où 0 est la priorité des sommets standards.

Afin de réaliser cela, la classe `Labelizable` a été créée. `Summit` - l'implémentation demandée des sommets - et `Place` en héritent.
`Labelizer` accepte donc tout `Labelizable`.
Les `Labelizer` ayant une priorité positive sont dessinés en rouge, une priotié nulle en noir et une priorité négative en bleu.

Les `Labelizer` ne sont par contre rechargés qu'après un redémarrage du programme. Ils se situent dans le dossier *plc*.

## Mode "graphismes allégés"

Ce mode ignore le calcul des pentes, ce qui rend le calcul environ 25% plus rapide, et dessine un panorama simplifié. Il s'agit de celui utilisé pour tester `ChannelPainter.maxDistanceToNeighbors()`.

## Auto-altitude

Si cette option est activée, lorsque l'utilisateur change de latitude ou de longitude, l'alitude se met automatiquement à jour afin que l'observateur se situe à environ 2m du sol. Bien sûr, l'utilisateur peut ensuite changer cette valeur à sa guise.

## "Voyager" dans le Panorama avec un clic droit

Un clic droit sur le panorama permet de charger les paramètres du point sélectionné, à la différence près que l'altitude est réglée à environ 10m du sol et que l'azimut central est dirigé vers l'observateur du précédent panorama. Tout cela permet de s'assurer que le Panorama soit potable.

## Informations dans la console

Les temps d'exécution du calcul du panorama, de sa création dans `PanoramaRenderer` ainsi que du calcul des Labels est affiché dans la console afin de comparer différentes implémentations du programme (bonus, paramètres,...).

Les paramètres y sont également affichés, tout cela afin d'en garder une trace écrite.
Toute sauvegarde ou changement de MNT est également loggé.
