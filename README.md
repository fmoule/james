# James
## Introduction
## Installation
### Récupération et paramétrage du projet
#### De Gitlab
 * Pour la récupération du projet depuis le dépot Gitlab, il suffit de lancer la commande suivante :
   <pre><code>git clone git@gitlab.com:fmoule/james.git</code></pre>
#### De Github
 * Pour la récupération du projet depuis le dépot Github, il suffit de lancer la commande suivante :
   <pre><code>git clone git@github.com:fmoule/james.git</code></pre>
#### Compilation du projet
 * Se placer dans le dossier : cd james
 * Lancer la compilation :
   <pre><code>mvn clean install</code></pre>
#### Intellij
Pour la configurer le projet pour IntelliJ, il suffit de suivre les instructions suivantes :
 * Se placer dans le dossier du projet : <pre><code>cd james</code></pre>
 * Paramétrer le projet pour IntelliJ : <pre><code>mvn idea:idea</code></pre>
## Lancement
### Composants JADE 
#### Conteneur principal
Pour lancer le conteneur principal, il suffit de lancerja la commande suivante :
<pre><code>java -cp &lt classpath &gt jade.Boot -gui</code></pre>
## Resources documentaires
