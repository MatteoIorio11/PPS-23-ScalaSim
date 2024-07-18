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

Il simulatore prodotto dovrà implementare almeno 3 dei seguenti automi
cellulari:

- [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s*Game*of*Life)
- [Wa-Tor](https://en.wikipedia.org/wiki/Wa-Tor)
- [Brian's Brain](https://en.wikipedia.org/wiki/Brian%27s*Brain)
- [Langton's Ant](https://en.wikipedia.org/wiki/Langton%27s*ant)
- [Rule 110](https://en.wikipedia.org/wiki/Rule_110)

Il sistema deve garantire la correttezza di ognuno degli automi cellulari
listati, permettendo inoltre una visualizzazione in tempo reale della sua
evoluzione.

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
