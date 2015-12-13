# Forschungsplan SWP2013-1 Sentiment Classification #
Mirko Hering, Julia Kreutzer, Jasmin Schröck

## Ziel ##

Das Ziel unseres Projekts besteht darin, mit Hilfe von Multitask-Learning mit verteilter l1/l2-Regularisierung Features zu gewinnen, die für alle Tasks der Testdaten aussagekräftig sind. Dieses Lernverfahren wollen wir auf bewertete Produktrezensionen von Amazon.com anwenden, die verschiedenen Produktkategorien zugeordnet sind. Diese Produktkategorien entsprechen den Tasks des Multi-Tasks-Learnings.

## Lösungsansatz ##

### Methoden ###

Als Lernalgorithmus verwenden wir den Stochastic Gradient Descent Algorithmus mit einer Perceptron-Zielfunktion, die auf dem Hinge-Loss basiert. Die einzelnen Instanzen werden jeweils binär klassifiziert, positiv oder negativ.

Wir orientieren uns an dem Vorgehen von (Simianer et al. 2012). Hier wird das Verfahren für Statistical Machine Translation genutzt, dies wollen wir nun auf das Anwendungsgebiet der Sentiment Classification übertragen.

Um den Algorithmus für Multi-Task-Learning zu nutzen, trainieren wir die Gewichte parallel auf den Tasks. Nach jeder Epoche bestimmt ein taskübergreifendes Update mithilfe von l1/l2-Regularisierung die Gewichte für die aussagekräftigsten Features.
Durch dieses Update erhoffen wir uns, taskspezifisches Overfitting zu vermeiden.

Parameter für den Lernalgorithmus sind die Lernrate, die Anzahl der Epochen und die Anzahl der auszuwählenden Features durch die l1/l2-Regularisierung.
Um die optimalen Parameter zu bestimmen, richten wir uns hier zunächst nach dem oben genannten Paper.

### Daten ###

Wir nutzen für unser Projekt das Multi-Domain Sentiment Dataset (version 2.0); verfügbar unter dem Link http://www.cs.jhu.edu/~mdredze/datasets/sentiment/

Dabei orientieren wir uns an den Daten von (Blitzer et al. 2007).

Bei den Daten handelt es sich um englischsprachige Produktrezensionen von Amazon.com. Wir verwenden vier Kategorien: Bücher, DVDs, Küchengeräte und Elektronik. Von jeder Kategorie liegen etwa 4000-6000 Rezensionen vor. Jede Rezension ist enthält eine manuelle Bewertung von null bis fünf Sternen; Bewertungen größer drei werden positiv gelabelt, kleiner drei negativ, Bewertungen gleich drei erhalten kein Label.

Die Daten sind vorverarbeitet und liegen, eine Zeile pro Rezension, in folgendem Format vor:

`feature <count> ... feature <count> #label#: <label>`

Die Features bestehen aus Unigrammen und Bigrammen; insgesamt gibt es 100.000 Features bzw. Dimensionen.

Wir nutzen davon die Daten aus "processed\_acl". Diese sind in die vier Kategorien geteilt, wobei es zu jeder Kategorie jeweils zwei Dateien "positive.review" und "negative.review" gibt, die je 1000 positive bzw. 1000 negative Rezensionen enthalten.
Diese Daten werden noch in Test- und Trainingsdaten unterteilt. Analog zu (Blitzer et al. 2007) nehmen wir eine Aufteilung von 1600 (Training) zu 400 (Test) vor.

### Tools ###
Zur Implementierung des Projektes nutzen wir die Programmiersprache Java.

Wir wählen das Apache Hadoop Framework für die Ausführung der verteilten l1/l2-Regularisierung auf dem ICL Test Cluster.

## Evaluation ##
Wir führen eine Klassifikation der Testdaten mit dem trainierten Perceptron durch.

Für die Evaluation vergleichen wir die Ergebnisse mit zwei verschiedenen Baselines.
Die erste Baseline ergibt sich durch das getrennte Training auf einzelnen Tasks.
Die zweite durch das Training auf allen Daten zusammen, d.h. ohne Aufteilung in Tasks.

Das Evaluationsmaß ist die Accuracy, hier der Anteil der korrekt klassifizierten Samples an der gesamten klassifizierten Samplemenge.

Damit wollen wir herausfinden, ob die durch Multi-Task-Learning gewonnenen Gewichtsvektoren bessere Klassifikation ermöglichen als das taskspezifische oder das taskunabhängige Training.

## Referenzen ##
Simianer et al. 2012:
"Joint Feature Selection in Distributed Stochastic Learning for Large-Scale Discriminative Training in SMT" (P.Simianer, S. Riezler, C. Dyer. In Proceedings of the 50th Annual Meeting of the Association for Computational Linguistics (ACL 2012))

Blitzer et al. 2007:
"Domain Adaptation for Sentiment Classification" (John Blitzer, Mark Dredze, Fernando Pereira. Biographies, Bollywood, Boom-boxes and Blenders. Association of Computational Linguistics (ACL), 2007)