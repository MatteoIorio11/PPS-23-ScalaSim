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

Nel corso di questo documento verrà illustrato il processo di sviluppo e di
design dell'architettura software realizzata, illustrando e motivando i compiti
delle componenti costruite e i loro comportamenti, insieme alle interazioni tra
di esse che permettono il corretto funzionamento del sistema software
costruito.

[Indice](./index.md) | [Capitolo Successivo](./2-development-process.md)
