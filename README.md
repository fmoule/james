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
## Usage
### Création de la plateforme
Pour la creation de la plateforme, il suffit de suivre créer une classe Java exécutable 
c'est à dire avec une méthode main et d'utiliser la classe <b>PluginManager</b><br />
Puis d'ajouter l'agentPlugin permettant de gérer les agents à démarrer.<br />
Voici un exemple de code Java :
<br />
<br />
<pre>
<code>
public static void main(final String args[]) {
        final PluginManager pluginManager = new PluginManager("pluginManager");
        try {
            pluginManager.addPlugin(createConfigPlugin("configPlugin", args));
            pluginManager.addPlugin(createAgentPlugin("agentPlugin"));
            pluginManager.start();
        } catch (final Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
}
</code>
</pre>
### Liste des agents et leur typologie

#### Agent Web
Ce type d'agent a pour objectif d'exposer des API's REST et donc des sites internet. <br />

