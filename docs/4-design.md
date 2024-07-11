# Design del Sistema

Il design sviluppato per il sistema puo essere suddiviso in 3 componenti principali, ovvero:
1. _Engine_
2. _Environment_
3. _Cellular Automaton

Nelle sezioni successive verrano elencate le diverse strategie utilizzate per sviluppare l'intero sistema, andando ad elencare le scelte effettuate al fine di realizzare un programma che sia altamente personalizzabile al fine di facilitare la creazione di simulazioni di automi cellulari.

## Cellular Automaton

Il Cellular Automaton, puo essere visto come un contenitore di regole che fanno riferimento ai diversi stati che compongono il Cellular Automaton Stesso. Per ogni _Cell_ che viene memorizzata all'interno dell'Environment verra scelta una regola specifica, in modo da calcolare correttamente lo stato sucessivo o gli stati successivi, a seconda del Cellular Automaton che si sta modellando fino a quel momento (WaTor coinvolge sempre la modifca dello stato di due celle contemporaneamente).

Il design che riguarda il Cellular Automaton ha richiesto una serie di scelte importanti, affinche fosse possibile riuscire ad incapsulare la sua astrazione in una serie di diversi componenti.

### Cell

Partendo dal concetto piu generale, ogni Cellular Automaton si compone di diversi stati, i quali vengono assunti dalle diverse celle che sono memorizzate all'interno del tensore dell' Environment. Ogni Cellula, si compone di una posizione nello spazio e di uno specifico Stato, il quale determinera assieme ai vicini della cella stessa il suo stato sucessivo.

[TODO: aggiungere immagine trait Cell]

### Neighbour

Come accennato in precedenza, un concetto fondamentale nella modellazione degli automi cellulari e il concetto di 'vicini'. Per la modellazione di questa idea e stato necessario sviluppare in maniera separata da _Cell_, il concetto di _Neighbour_, il quale viene modellato tramite due aspetti principali:
1. _center_: rappresenta la cella per il quale viene calcolato il vicinato;
2. _neighbourhood_: rappresenta la collezione di celle che compone il vicinato della cella centrale, questa collezione non fa riferimento ad una specifica regola con la quale individuare i vicini, dal momento in cui diversi Cellular Automaton potrebbero utilizzare regole diverse per individuare il proprio vicinato.

[TODO: aggiungere immagine trait Neighbour]

### Rule

L'aspetto piu importante, necessario in oltre, a modellare un Cellular Automaton e il concetto di _Rule_, una regola permette di specificare il comportamento di un Cellular Automaton in un preciso istante, piu in particolare una specifica _Cell_ ed la sua _Neighbour_ e possibile calcolare il nuovo stato. Per la rappresentazione di questo concetto si e voluto sfruttare l'aspetto funzionale di Scala, andando a rappresentare la regola come una funzione con un Input ed un Output generico. Ogni regola in oltre fa riferimento ad uno specifico _State_ del Cellular Automaton. Grazie a questo tipo di modellazione e stato possibile rappresentare il concetto di modifica di piu _Cell_ contemporaneamente.

[TODO: aggiungere il trait Rule]

## Environment

Il secondo macro concetto che e stato affrontato per il simulatore, e stato l'Environment. Come discusso in precedenza il _Cellular Automaton_ fa riferimento ad un insieme di _Cell_ le quali devono essere memorizzate in una qualche struttura dati, dal momento in cui lo user potrebbe voler modellare lo spazio in cui vengono salvate le _Cell_ con specifiche strutture dati. 
