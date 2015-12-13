# Datenstrukturen: #
  * **Features** für ein Review: Tupel (Label, Dictionary/HashMap)
  * **Gewichtsvektor**: Array mit Länge der Anzahl der Dimensionen (100.000?)


# Funktionen: #
  1. **Training**
    * Shards aufteilen (Map)
    * Einlesen von Datei -> Tupel s.o.
    * SGD Perceptron
    * Gewichtsvektoren sammeln (Reduce)
    * l2-Norm berechnen -> Auswahl
    * Mitteln über shards
  1. **Test**
    * Klassifikator
    * Evaluation -> Baselines

# Perceptrons: #
4 verschiedene Perceptron-Formen
  1. Multitask - shards = random
  1. Multitask - shards = Produktkategorien
  1. klassisch - für jede Kategorie einzeln (sollten denen im Paper entsprechen)
  1. klassisch - für alle Daten zusammen