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
## Requisiti Engine (da spostare in analisi)

All'intero del sistema che si andra a sviluppare, oltre ai diversi tool con la quale modellare l'astrazione di un _Cellular Automaton_ e tutto cio che lo riguarda, sara necessario realizzare un ulteriore componente software il cui compito sara quello di svolgere l'intera simulazione. Questo componente, denominato _Engine_ dovra essere in grado di eseguire un qualsiasi tipo di simulazione in maniera del tutto indipendente dal tipo di ambiente utilizzato dal _Cellular Automaton_, in modo da far evolvere lo stato delle diverse _Cell_ memorizzate nello spazio.

In parallelo all'esecuzione della simulazione dovra essere possibile visualizzarla, per fare cio l'_Engine_ dovra quindi permettere all'utente finale di visualizzare la sua simulazione in due specifiche modalita:
1. Visualizzazione tramite GUI in real time;
2. Visualizzazione tramite export video.