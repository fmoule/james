# poc-agent

## Installation
### Récupération et paramétrage du projet
 * Pour la récupération du projet, il suffit de lancer la commande suivante :
   <pre><code>git clone git@bitbucket.org:clearchannelfrance/poc-agent.git</code></pre>
 * Se placer dans le dossier : cd poc-agent
 * Lancer la compilation :
   <pre><code>mvn clean install</code></pre>
   
#### Intellij
Pour la configurer le projet pour IntelliJ, il suffit de suivre les instructions suivantes :
 * Se placer dans le dossier du projet : <pre><code>cd poc-agent</code></pre>
 * Paramétrer le projet pour IntelliJ : <pre><code>mvn idea:idea</code></pre>


## Lancement

### Composants JADE 
#### Conteneur principal
Pour lancer le conteneur principal, il suffit de lancerja la commande suivante :
<pre><code>java -cp &lt classpath &gt jade.Boot -gui</code></pre>
