# Design del Sistema

Il design sviluppato per il sistema puo essere suddiviso in 3 componenti principali, ovvero:
1. _Engine_
2. _Environment_
3. _Cellular Automaton

Nelle sezioni successive verrano elencate le diverse strategie utilizzate per sviluppare l'intero sistema, andando ad elencare le scelte effettuate al fine di realizzare un programma che sia altamente personalizzabile al fine di facilitare la creazione di simulazioni di automi cellulari.

## Cellular Automaton

Il design che riguarda il Cellular Automaton ha richiesto una serie di scelte importanti, affinche fosse possibile riuscire ad incapsulare l'astrazione del Cellular Automaton in una serie di diversi componenti.

### Cell

Partendo dal concetto piu generale, ogni Cellular Automaton si compone di diversi stati, i quali vengono assunti dalle diverse celle che sono memorizzate all'interno del tensore dell' Environment. Ogni Cellula, si compone di una posizione nello spazio e di uno specifico Stato, il quale determinera assieme ai vicini della cella stessa il suo stato sucessivo. Per modellare questo concetto si scelto di realizzare un trait specifico, denominato _Cell_, il quale al suo interno definisce due componenti, una Posizione ed uno Stato.


[TODO: aggiungere immagine trait Cell]

### Neighbour

Come accennato in precedenza, un concetto fondamentale nella modellazione degli automi cellulari e il concetto di 'vicini'. 