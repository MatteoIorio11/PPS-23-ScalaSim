# Testing

## Tecnologie usate

Per quanto riguarda il testing automatizzato, sono stati effettuati
principalmente unit test tramite la libreria `scalatest`. Successivamente
è stata rilevata la copertura dei test tramite il plugin [scoverage](https://github.com/scoverage/sbt-scoverage).

## Testing Automatizzato

Gran parte del modello del dominio è stato sviluppato attraverso *Test Driven
Development*, risultando in una copertura media dell'80% per i package
`domain.automaton`, `domain.base` ed `Environment`. Per quanto riguarda le
simulazioni, contenute all'interno del package `domain.simulations`, la
copertura dei test di circa l'82% si è riflessa molto positivamente nel momento
di integrazione dell'interfaccia grafica ed esportazione video, poiché era
pressoché garantito a partire dai test che le simulazioni funzionassero
correttamente. Sono stati inoltre eseguiti test per la conversione delle simulazioni in matrici di immagini, assicurando che le rappresentazioni visive fossero corrette. Un'altra componente importante per il testing, sia da un punto
di vista di verifica della correttezza del funzionamento, ma anche come
*documentazione* d'uso, è stata per la componente del DSL. Attraverso i test
(coverage di circa il 90% di media per il package `dsl`) è sia possibile
osservare l'utilizzo pensato per il DSL, ma anche test di verifica della
correttezza del codice implementato.

## Testing non automatizzato

`GUI`
I test automatizzati della GUI non sono stati implementati a causa della complessità e della natura interattiva dell'interfaccia grafica. Invece, è stato effettuato un ampio testing manuale per assicurarsi che tutte le funzionalità dell'interfaccia utente funzionino correttamente, includendo la verifica della corretta visualizzazione degli automi, l'interazione con i vari controlli (bottoni, combo box, ecc.), e l'aggiornamento in tempo reale dello stato delle simulazioni.

`Video Exporter`
Il testing non automatizzato per l'exporter video ha incluso la verifica manuale dei file video generati. Questo ha comportato l'esecuzione di simulazioni e l'esportazione dei risultati in file video, seguita dalla revisione visiva dei file per assicurarsi che i video rappresentassero correttamente le simulazioni.

[Indice](./index.md) | [Capitolo Precedente](./6-implementation.md) | [Capitolo Successivo](./8-conclusions.md)
