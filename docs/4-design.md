# Design del Sistema

Il design architetturale dell'intero sistema puo essere suddiviso in 3 macro componenti, i quali a loro volta sono suddivisi e descritti in componenti piu semplici che incapsulano un unico argomento. Le tre macro categorie di concetti che compongono il sistema sono:
1. _Engine_
2. _Environment_
3. _Cellular Automaton_ 

Nelle sezioni successive verrano elencate le diverse strategie utilizzate per sviluppare le diverse componenti, andando ad elencare le scelte effettuate nella  realizzazione delle diverse funzionalita per far sii che sia altamente personalizzabile, in modo da facilitare la creazione di simulazioni.

## Cellular Automaton

Il _Cellular Automaton_, puo essere visto come un contenitore di regole che fanno riferimento ai diversi stati che compongono il Cellular Automaton stesso. Per ogni _Cell_ che viene memorizzata all'interno dell'_Environment_ verra scelta una regola specifica, in modo da calcolare correttamente lo stato sucessivo o gli stati successivi, a seconda del Cellular Automaton che si sta modellando fino a quel momento (WaTor coinvolge sempre la modifca dello stato di due celle contemporaneamente).

Dal momento in cui esistono diverse categorie di _Cellular Automaton_, tra cui una specifica categoria la quale per un singolo stato di una _Cell_ potrebbero venire applicate piu di una regola contemporaneamente, si e deciso di utilizzare un _type parameter_ per la definizione della struttura dati nella quale andare a memorizzare le diverse regole che compongono il _Cellular Automaton_. A supporto di questa funzionalita inoltre si e deciso di implementare, tramite l'utilizzo di _mixin_, diverse strutture dati gia funzionanti da utilizzare per memorizzare lavorare su tali regole.

Il design che riguarda il _Cellular Automaton_ ha richiesto una serie di scelte importanti, affinche fosse possibile riuscire ad incapsulare la sua astrazione in una serie di diversi componenti. 

### Cell

Lo spazio di un ambiente è composto da un numero finito di celle. Ogni cella,
oltre ad avere assegnate un insieme di coordinate, ha il compito di mantenere
uno stato in un determinato istante di tempo. Lo **Stato** di una cella è
un'astrazione che dipende in base all'automa cellulare, ed esso può essere un
semplice stato "nominale" (e.g. `Dead`, `Alive`, ...) oppure può mantenere un
valore associato a quello stato (e.g. `Shark(chronon = 1, energy = 100)`,
`Fish(chronon = 1)`, ...). Per entrambi i casi, lo stato determina
l'unità fondamentale su cui si basano le regole e i comportamenti dell'automa.

Tramite quest'astrazione è possibile costruire una griglia di celle le quali
possono contenere un'informazione generica utile per la specifica del
comportamento dell'automa, mantenendo allo stesso tempo una posizione ben
definita all'interno dello spazio dell'ambiente stesso.

![Diagramma UML della componente `Cell`](./img/cell.png)

Nel diagramma è mostrata la struttura base delle componenti fondamentali di una
cella: una cella dipende dallo spazio dell'ambiente in cui risiede, riuscendo
così a definire una posizione all'interno di tale spazio. Successivamente, ad
una cella è assegnato uno e un solo stato.

### Neighbour

Gran parte dei comportamenti degli automi cellulari è caratterizzato dal
concetto di vicinato di una cella. In letteratura esistono vari generi di
vicinato, in particolare si ricordano il [vicinato di Moore](https://en.wikipedia.org/wiki/Moore_neighborhood)
e il [vicinato di Von Neumann](https://en.wikipedia.org/wiki/Von_Neumann_neighborhood).
Esistono però numerosi automi dove vengono impiegate strategie differenti per
l'individuazione dei vicini di una cella (e.g. *Rule110*). È quindi
fondamentale rendere il sistema capace di rappresentare sia vicinati standard
come i due sopracitati, ma allo stesso tempo permettere in modo semplice la
definizione di vicinati *custom*.

In generale, il concetto di `Neighbour` presenta due componenti principali:
1. centro: rappresenta la cella per il quale viene calcolato il vicinato;
2. vicinato: rappresenta la collezione di celle che compone il vicinato della
   cella centrale; questa collezione non fa riferimento ad una specifica regola
   con la quale individuare i vicini, dal momento in cui diversi automi
   cellulari potrebbero utilizzare regole diverse per individuare il proprio
   vicinato.

Durante il corso di questo documento e all'interno del progetto, si fa spesso
riferimento a concetti di posizione _relativa_ e _assoluta_: con i due termini
si indicano due modalità differenti per esprimere le posizioni dei vicini
componenti un vicinato rispetto al centro. Nel caso in cui si parli di
posizione relativa, si intende che le posizioni assunte dai vicini saranno
relative al centro del vicinato, dove quest'ultimo si assume abbia coordinate
pari all'origine. In questo caso, per esempio, la posizione appena al di sopra
del centro in uno spazio bidimensionale assumerà coordinate relative pari a
(-1, 0), in quanto si troverà una riga precedente al centro ma sulla stessa
colonna. D'altra parte, quando si parla di posizioni assolute, queste fanno
riferimento alle coordinate assolute di tutte le celle del vicinato rispetto
l'ambiente della simulazione. Risulta quindi possibile con questa astrazione
definire una certa configurazione di un vicinato tramite posizioni relative, e
quindi individuare il vicinato stesso di una certa cella tramite il valore
delle coordinate di quest'ultima (i.e. il centro del vicinato) e i valori delle
posizioni relative.

![Diagramma UML della componente `Neighbour` e la sua interazione con l'ambiente e la componente `NeighbourhoodLocator` per la localizzazione di vicinati.](./img/neighbour.png)

Nel diagramma UML soprastante è mostrato come già descritto il concetto di
`Neighbour`. L'ambiente calcola un vicinato a partire da una cella che ne
costituirà il centro. Per il calcolo del vicinato, l'ambiente si avvale di un
`NeighbourhoodLocator`, il quale definisce i pattern e le configurazioni che un
determinato vicinato deve assumere, definendolo in termini di posizioni
relative. Infine, quest'ultimo espone un ulteriore metodo per il passaggio da
posizioni relative a posizioni assolute in riferimento all'ambiente.

### Rule

Il comportamento di un automa cellulare è definito attraverso un insieme di
regole, le quali prendendo in input una o più celle dell'ambiente, calcolano un
nuovo stato della cella per l'iterazione successiva. Data la natura variabile
delle regole di un automa cellulare, deve essere possibile generalizzare al
meglio il concetto di regola applicabile ad un determinato stato. Per le
considerazioni effettuate nella sezione precedente inoltre, la maggior parte
delle regole di un automa sono basate su un insieme di stati dei vicini di una
cella. Per questo motivo si è arrivati alla modellazione mostrata nel diagramma
UML sottostante.

![Diagramma UML della modellazione di `Rule`, comprendendo le sue specializzazioni e varianti](./img/rule.png)

Nel diagramma UML, `Rule` rappresenta il concetto più generico di regola,
la quale non è altro che la definizione di una funzione di trasformazione.
Ogni regola è associata ad un `matcher`, il quale rappresenta il parametro
che permette di decidere se applicare la funzione o meno. Una ragionevole
specializzazione di una regola generica è rappresentata da `NeighbourRule`,
la quale è riassumibile da una funzione che prende in input un vicinato,
e se il centro ha lo stesso stato specificato dal parametro `matcher` e il
vicinato soddisfa la regola, allora restituisce in output la nuova
cella rappresentante il nuovo centro del vicinato. `MultipleOutputRule`
rappresenta una regola generica il cui output è composto da una collezione
di output, mentre `MultipleOutputNeighbourRule` ha lo stesso obiettivo
di `NeighbourRule` con la differenze che il risultato dell'applicazione
della regola è composto da un insieme di celle. Questo può risultare
utile per tutte quelle regole che prevedono la modifica simultanea di più
celle in base allo stato di un centro, oppure per modellare il concetto
di movimento di una certa entità all'interno dello spazio (e.g. l'automa
cellulare WaTor).

L'aspetto piu importante, necessario in oltre, a modellare un Cellular Automaton e il concetto di _Rule_, una regola permette di specificare il comportamento di un Cellular Automaton in un preciso istante, piu in particolare una specifica _Cell_ ed la sua _Neighbour_ e possibile calcolare il nuovo stato. Per la rappresentazione di questo concetto si e voluto sfruttare l'aspetto funzionale di Scala, andando a rappresentare la regola come una funzione con un Input ed un Output generico. Ogni regola in oltre fa riferimento ad uno specifico _State_ del Cellular Automaton. Grazie a questo tipo di modellazione e stato possibile rappresentare il concetto di modifica di piu _Cell_ contemporaneamente.

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

## Design Interfaccia Grafica
[TODO: VINCI]
