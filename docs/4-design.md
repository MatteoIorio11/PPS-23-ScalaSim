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

Il secondo macro concetto che e stato affrontato per il simulatore, e stato l'Environment. L'_Environment_ fa sempre riferimento ad un singolo _Cellular Automaton_, in questo modo sara possibile fornire il _Neighbour_ appropriato dal momento in cui solo l'_Environment_ e a conoscenza della disposizione delle varie _Cell_ nello spazio.

### Astrazione dello spazio

Un aspetto molto importante riguardante l'_Environment_ e la modalita con la quale rappresentare lo spazio in cui salvare le _Cell_. Per fare in modo di lasciare maggiore liberta allo user, nel definirsi la propria struttura dati con la quale rappresentare lo spazio si e deciso di sfruttare un meccanismo di Scala ovvero i _type_ parameters. Attraverso l'utilizzo di questo modalita sara possibile utilizzare qualsiasi tipo di struttura dati per modellare lo spazio in cui salvare le _Cell_ riguardanti la simulazione.

### Configurazione tramite Mixin
La modellazione dello spazio tramite il _type_ ha reso possibile l'utilizzo dei _Mixin_. La configurazione della struttura dati con la quale modellare lo spazio e definito tramite uno o piu trait che vanno a comporre il _Cake Pattern_, attraverso il quale vengono configurate le diverse informazioni dell'_Environment_.

Questo meccanismo viene in oltre utilizzato per modellare la geometria dello spazio del _Cellular Automaton_. Ovvero ogni _Cellular Automaton_ ha la propria concezione di spazio, ad esempio un semplice rettangolo, o uno spazio piu complesso come quello toroidale. Anche per ls modellazione di questo comportamento dell'_Environment_ vengono utilizzati i _Mixin_ attraverso cui e possibile specificare i diversi comportamenti riguardanti lo spazio geometrico ed inoltre le varie modalita con la quale percepire lo spazio stesso.

## Engine

L'ultimo componente necessario alla realizzazione dell'intero sistema e l'_Engine_, ovvero il motore attraverso il quale e possibile sviluppare l'intera simulazione, apportando modifiche all'_Environment_ e facendo evolvere lo stato della simulazione ad ogni nuova iterazione. Ogni _Engine_ al suo interno deve fare riferimento ad un singolo _Environment_, cosi che si faccia riferimento sempre ad una sola simulazione.

Dal momento in cui possono esserci diverse modalita con la quale voler eseguire la simulazione, ad esempio visualizzazione real time con GUI, simulazione con output finale un video etc etc, si e deciso di sviluppare le diverse modalita di esecuzione tramite l'utilizzo dei _Mixin_.

