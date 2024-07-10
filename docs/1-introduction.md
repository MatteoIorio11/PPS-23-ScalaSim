# Introduzione
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

## Requisiti del Sistema (da spostare in analisi)

In primis, il sistema deve predisporre un insieme di componenti software per la
creazione, popolamento e manipolazione della griglia, o più in generale, di uno
spazio o un ambiente all'interno del quale risiedono un numero finito di celle.
Deve essere possibile la manipolazione di spazi sia bidimensionali sia
tridimensionali, e allo stesso tempo deve poter essere possibile astrarre
l'ambiente in modo tale da supportare spazi complessi come spazi *Toroidali* o
spazi dove le celle non siano necessariamente quadrate.

Successivamente, il sistema deve rendere possibile la specificazione di regole
che siano dipendenti dall'ambiente specificato, ma allo stesso tempo permettere
di specificare comportamenti più o meno complessi. Come dimostrazione di questa
caratteristica sarà quindi necessario sviluppare almeno 3 dei seguenti automi cellulari:

- [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life)
- [Wa-Tor](https://en.wikipedia.org/wiki/Wa-Tor)
- [Brian's Brain](https://en.wikipedia.org/wiki/Brian%27s_Brain)
- [Langton's Ant](https://en.wikipedia.org/wiki/Langton%27s_ant)
- ...

Una volta definito il modello della simulazione, deve essere predisposta una componente
software in grado di eseguire e, potenzialmente mostrare o esportare il risultato
della computazione.

*Requisiti engine qui*
