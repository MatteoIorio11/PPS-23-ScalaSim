# Requisiti e Specifiche di Progetto

## Modello di Dominio

Il progetto `ScalaSim` si pone di realizzare un simulatore di [**Automi
Cellulari**](https://it.wikipedia.org/wiki/Automa_cellulare) capace di
supportare varie tipologie di questi ultimi, provvedendo a fornire meccanismi e
componenti generali ed estendibili per fare in modo di adattare il software
costruito in base alle esigenze delle simulazioni.

Un *automa cellulare* è un modello matematico che consiste in una griglia
costituita da celle che rappresentano delle entità che evolvono nel tempo.
Ciascuna di queste celle può assumere un insieme finito di stati e in ogni
unità di tempo la griglia viene aggiornata seguendo delle regole definite dalla
tipologia di automa simulato.

## Requisiti

Durante l'analisi del dominio, sono stati individuati i seguenti requisiti del
sistema software da realizzare.

### Business

L'obiettivo del progetto è la creazione di un simulatore di automi cellulari
all'interno di un ambiente finito. L'ambiente è suddiviso in *celle*, le quali
durante la simulazione possono subire un cambiamento di stato dettato da un
insieme di regole o comportamenti definiti da uno specifico automa cellulare.
L'applicazione o meno di una regola può dipendere sia dallo stato della cella
su cui viene applicata, sia da un insieme di fattori che caratterizzano
l'ambiente e la simulazione in un certo istante di tempo. Generalmente tra
questi fattori possono essere considerati lo stato del vicinato di una cella, o
il numero di step che la simulazione ha effettuato in un dato momento. Una
regola deve quindi essere definita nella maniera più generale possibile in modo
tale da poter rappresentare, potenzialmente, un qualsiasi comportamento di un
automa cellulare.

Il simulatore prodotto dovrà implementare almeno 3 dei seguenti automi
cellulari:

- [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s*Game*of*Life)
- [Wa-Tor](https://en.wikipedia.org/wiki/Wa-Tor)
- [Brian's Brain](https://en.wikipedia.org/wiki/Brian%27s*Brain)
- [Langton's Ant](https://en.wikipedia.org/wiki/Langton%27s*ant)
- [Rule 110](https://en.wikipedia.org/wiki/Rule_110)

Di seguito una breve descrizione per ognuno di essi.

#### Conway's Game of Life

![Conway's Game of Life in esecuzione](https://upload.wikimedia.org/wikipedia/commons/e/e6/Conways_game_of_life_breeder_animation.gif)

Conway's Game of Life è un semplice automa cellulare che simula l'evoluzione di
un universo artificiale composto da cellule. Queste cellule possono essere in
due stati: vive o morte.

Le regole sono molto semplici:

- Isolamento: Una cella viva con meno di due vicini muore di solitudine.
- Sopravvivenza: Una cella viva con due o tre vicini sopravvive alla
  generazione successiva.
- Sovrapopolazione: Una cella viva con più di tre vicini muore per
  sovraffollamento.
- Riproduzione: Una cella morta con esattamente tre vicini diventa viva.

#### Brian's Brain

![Brian's Brain in esecuzione](https://upload.wikimedia.org/wikipedia/commons/a/a7/Brian%27s_brain.gif)

Brian's Brain è un automa cellulare che simula l'evoluzione di un universo
artificiale composto da celle che possono trovarsi in uno dei seguenti tre
stati:

- Viva (`ON`): La cella è attiva e può influenzare le celle vicine.
- Morente (`DYING`): stato della cella un'iterazione successiva dopo essere
  stata attiva.
- Morta (`OFF`): La cella è inattiva, non ha alcun effetto sulle celle vicine.

Ad ogni step della simulazione, una cella `OFF` diventa `ON` se esattamente due
degli otto vicini hanno stato `ON`. Tutte le celle `ON`, allo step successivo
diventano `DYING`: tutte le celle in questo, all'iterazione successiva avranno
stato `OFF`.

#### Langton's ANT

![Langton's Ant dopo 11.000 iterazioni](https://upload.wikimedia.org/wikipedia/commons/thumb/0/01/LangtonsAnt.svg/368px-LangtonsAnt.svg.png)

Questo automa cellulare ha un comportamento molto semplice ma i risultati che
emergono possono essere piuttosto complessi.

La griglia iniziale è composta da sole celle bianche, e in una qualunque posizione
viene collocata una formica. Ad ogni iterazione la formica si sposta di una posizione
secondo le seguenti regole:

- Al di sopra di una cella *bianca*, ruota la formica di 90 gradi in senso
  orario, inverti il colore della cella sottstante, e muovi la formica di una
  posizione in avanti.
- Al di sopra di una cella *nera*, ruota la formica di 90 gradi in senso
  anti-orario, inverti il colore della cella sottstante, e muovi la formica di
  una posizione in avanti.

#### WaTor

WaTor simula l'evoluzione di una popolazione contenente prede e predatori,
nel nostro caso `Fish` e `Shark` rispettivamente, all'interno di un ambiente
toroidale. I risultati possono essere interessanti, come l'estinzione di una o
entrambe le specie o un equilibrio tra le due.

Le regole possono essere così riassunte:

- Fish:
  1. Ad ogni iterazione, viene scelta una cella casualmente tra le celle vicine
     che non siano occupate da un'entità. Se non esiste, nessun movimento viene
     effettuato.
  2. Quando un pesce sopravvive per un certo numero di iterazioni, si
     riproduce: quando avviene lo spostamento verso una cella vicina, nella
     vecchia cella viene collocato un nuovo pesce, resettando il contatore di
     riproduzione mantenuto dal pesce originario.

- Shark:
  1. Ad ogni iterazione, viene scelta una cella casualmente tra le celle vicine
     che siano occupate da un pesce. Se non sono presenti, si cerca una cella
     non occupata da un'altro squalo. Se non sono presenti celle libere, nessun
     movimento viene effettuato.
  2. Ad ogni iterazione, uno squalo viene privato di un'unità di energia: una
     volta arrivato a zero, lo squalo muore.
  3. Se uno squalo raggiunge una cella occupata da un pesce, lo squalo mangia
     il pesce e lo squalo incrementa di una certa quantità la sua energia.
  4. Come per il pesce, una volta che uno squalo è sopravvissuto per un certo
     numero di iterazioni, si può riprodurre.

#### Rule110

![Rule 110 dopo 256 iterazioni](https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Sample_run_of_Rule_110_elementary_cellular_automaton%2C_starting_from_single_cell.png/440px-Sample_run_of_Rule_110_elementary_cellular_automaton%2C_starting_from_single_cell.png)

Rule110 è un automa cellulare monodimensionale che si espande all'infinito.
Ogni cella può assumere due valori, e lo stato successivo è definito dalla cella
stessa e i suoi vicini. Le celle possono assumere valore 1 e 0, visivamente
codificati rispettivamente in nero e bianco.

In base alla configurazione di una cella e delle due vicine si definisce lo stato
della cella sottostante al passo successivo in base allo schema:

| 000 | 001 | 010 | 011 | 100| 101 | 110 | 111 |
|-----|-----|-----|-----|----|-----|-----|-----|
| 0   | 1   | 1   | 1   | 0  | 1   | 1   | 0   |

### Utente

Gli utenti saranno in grado di interagire con il sistema principalmente in due
modalità:

- Attraverso interfaccia grafica;
- Attraverso un esportazione video della simulazione.

In entrambe le modalità un utente potrà configurare i parametri della simulazione,
tra cui:

- Scelta tra alcuni automi cellulari implementati;
- Numero di iterazioni della simulazione nel caso di esportazione video;
- Dimensioni dell'ambiente;
- Numero delle diverse entità all'interno della simulazione (e.g. in Game of
  Life, numero iniziale di celle vive e celle morte).

### Requisiti Funzionali

Il sistema software deve essere in grado di rappresentare una vasta gamma
di automi cellulari, ovvero supportare un ambiente costituito da celle
aventi un certo stato, il quale può mutare nel tempo in base a specifiche
regole dell'automa. Oltre ad essere in grado di modellare un automa, il sistema
deve poter effettuare un certo numero di iterazioni dell'evoluzione
del sistema, ossia applicare l'insieme di regole caratterizzanti l'automa
alle celle componenti l'ambiente, in modo tale da causare un susseguirsi
di cambiamenti di stato delle stesse ad ogni ciclo.

Il sistema deve garantire la correttezza di ognuno degli automi cellulari
listati precedentemente, permettendo inoltre una visualizzazione in tempo reale
della loro evoluzione o un'esportazione grafica del loro risultato.

### Requisiti Non Funzionali

Trattandosi di un simulatore con la possibilità di visualizzazione in tempo
reale, il sistema deve garantire un discreto livello di efficienza. Riguardo
l'interfaccia grafica che permette l'interazione col simulatore stesso, essa
dev'essere intuitiva e di facile uso, specialmente per quanto riguarda
aspetti più avanzati come la configurazione della simulazione.

### Requisiti di Implementazione

- Utilizzo della Java Virtual Machine
- Utilizzo di Scala 3.x
- JDK?

### Requisiti Opzionali

- Implementazione di tutti gli automi cellulari listati nei [Requisiti-Funzionali](#Requisiti-Funzionali);
- Caricamento da file standard (JSON, XML, YAML) della configurazione del simulatore.
- Caricamento da file standard delle specifiche di un semplice automa cellulare.

[Indice](./index.md) | [Capitolo Precedente](./2-development-process.md) | [Capitolo Successivo](./4-high-level-design.md)
