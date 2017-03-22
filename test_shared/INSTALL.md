# Procédure d'installation

Pour utiliser le repository de test, le plus simple est de cloner le repo (je vous renvoie ici à la documentation git si vous ne savez pas ce que cela signifie) dans un dossier nommé `test_shared` placé au même niveau que `src` (donc directement dans le dossier du projet Alpano).
Puis, dans Eclipse, après avoir rafraichit le projet (avec la touche `F5`), de convertir le dossier `test_shared` en dossier source.

Vous devriez maintenant pouvoir exécuter les tests comme des tests Junit.


## Pour les groupes qui ont fait de leur projet Alpano un repo git

Si votre projet est synchronisé avec git, prenez bien soin, *avant de cloner le repo de test*, d'ajouter à votre fichier .gitignore (situé à la racine du projet git) la ligne

```
*/test_shared/*
```

Elle aura pour conséquence d'empêcher git de tracker les changements fait dans le dossier `test_shared` (comportement désiré puisque `test_shared` est lui même un repo).


## Des erreurs apparaissent

Si vous avez des erreurs qui apparaissent une fois le repo de test importé, envoyez moi un mail à [yves.zumbach@epfl.ch](yves.zumbach@epfl.ch) et je verai s'il est possible de corriger (bien que tous les efforts possibles ont été fait pour qu'une telle situation ne se produisen pas).
La plupart des erreurs proviennent de collision de nom (difficile à éviter surtout avec les classes imbriquées...).
