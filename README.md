# PPS-23-ScalaSim

## Istruzioni

Il progetto necessita di essere lanciato con l'opzione JVM `--enable-preview`.
L'opzione è già inserita all'interno del file `build.sbt`, ma per l'esecuzione
del jar è necessario specificarla all'interno del comando `java
--enable-preview -jar scalasim.jar`.

È presente lo script `./build_and_run_jar.sh` per poter costruire e lanciare
correttamente il jar in caso di problemi con il jar all'interno della release.

## Proposta di Progetto

**Deadline**: 22 Luglio 2024

**Acronimo**: ScalaSim

**Cognomi dei Membri**: Iorio, Furi, Vincenzi

**Titolo**: Scala Cellular Automata Simulator

**Indirizzi Mail**: 

- "Matteo Iorio - matteo.iorio2@studio.unibo.it"
- "Stefano Furi - stefano.furi@studio.unibo.it"
- "Fabio Vincenzi - fabio.vincenzi2@studio.unibo.it"

**Processo di Sviluppo**: Il nostro gruppo intende adottare come processo di sviluppo la metodologia SCRUM.

**Divisione del Lavoro tra studenti**:
Inizialmente si coopererà per definire in modo più specifico i requisiti del
sistema, per poi successivamente elaborare un design dell'elaborato in grado di
sfruttare al meglio il pradigma FP. Una volta definito il design
dell'elaborato, il lavoro verrà suddiviso equamente in 3 macro categorie:
Engine, Configurazione Simulazioni (attraverso DSL) e resa grafica (attraverso
GUI oppure immagini PNG). Ogni componenete contribuirà in maniera equa ad ognuna
di queste categorie (la suddivisione del lavoro verrà illustrata in dettaglio
all'interno della relazione finale).

**Sintesi dei Requisiti di Sistema**:
Si vuole realizzare un simulatore di Cellular Automaton, sfruttando il paradigma
FP e il linguaggio scala. Il simulatore dovrà essere in grado di rappresentare le più
comuni simulazioni di automi cellulari (e.g. Game of Life, Predators and Prey, ...).

Nel seguento sono riassunte le specifiche principali del sistema:
- Motore per l'esecuzione automatica delle simulazioni.
- Astrazione dell'ambiente (e.g. ambiente 1D, 2D, 3D).
- Astrazione della definizione dell'automa cellulare.
- Astrazione del comportamento che renda semplice definire un nuovo comportamento di automa.
- Definizione comportamento automa attraverso DSL.
- Configurazione della simulazione attraverso DSL.
- Realizzazione GUI oppure motore di esportazione grafica (e.g. immagini PNG, video, ...).
- Implementazione di almeno 2 tra le seguenti simulazioni:
    - [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life)
    - [Brian's Brain](https://en.wikipedia.org/wiki/Brian's_Brain)
    - [Predators And Prey](https://en.wikipedia.org/wiki/Wa-Tor)
    - [Langton's Ant](https://en.wikipedia.org/wiki/Langton%27s_ant)
