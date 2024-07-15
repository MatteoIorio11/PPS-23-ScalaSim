# Analisi

## Requisiti

### Requisiti Funzionali

In primis, il sistema deve predisporre un insieme di componenti software per la
creazione, popolamento e manipolazione della griglia, o più in generale, di uno
spazio o un ambiente all'interno del quale risiedono un numero finito di celle.
Deve essere possibile la manipolazione di spazi sia bidimensionali sia
tridimensionali, e allo stesso tempo deve poter essere possibile astrarre
l'ambiente in modo tale da supportare spazi complessi come spazi *Toroidali* o
spazi dove le celle non siano necessariamente quadrate (e.g. celle esagonali).

Successivamente, il sistema deve rendere possibile la specificazione di regole
che siano dipendenti dall'ambiente, ma allo stesso tempo permettere
di specificare comportamenti più o meno complessi. Come dimostrazione di questa
caratteristica sarà quindi necessario sviluppare almeno 3 dei seguenti automi cellulari:

- [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s*Game*of*Life)
- [Wa-Tor](https://en.wikipedia.org/wiki/Wa-Tor)
- [Brian's Brain](https://en.wikipedia.org/wiki/Brian%27s*Brain)
- [Langton's Ant](https://en.wikipedia.org/wiki/Langton%27s*ant)
- ...

Una volta definito il modello della simulazione, deve essere predisposta una
componente software in grado di eseguire e, potenzialmente mostrare o esportare
il risultato della computazione. Il "motore" del simulatore dovrà quindi essere in
grado di eseguire un qualsiasi tipo di simulazione in maniera del tutto
indipendente dal tipo di ambiente utilizzato dall'automa..

Parallelamente all'esecuzione di una simulazione, dovrà essere possibile

- visualizzare attraverso un'interfaccia grafica l'evoluzione dell'automa
  cellulare in tempo reale;
- esportare un'immagine dello step *i-esimo* dell'automa.
- esportare un video dell'evoluzione dell'automa fino secondo un certo numero
  di iterazioni.

### Requisiti Non Funzionali

Trattandosi di un simulatore con la possibilità di visualizzazione in tempo
reale, il sistema deve garantire un discreto livello di efficienza. Riguardo
l'interfaccia grafica che permette l'interazione col simulatore stesso, essa
dev'essere intuitiva e rapida.
