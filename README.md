# PPS-23-ScalaCalc

**Deadline**: 9 luglio 2024

**Acronimo**: ScalaCalc

**Cognomi dei membri**: Furi, Iorio, Vincenzi

**Titolo**: ScalaCalculus

**Indirizzi Mail**: 
1. "Stefano Furi - stefano.furi@studio.unibo.it"
2. "Matteo Iorio - matteo.iorio2@studio.unibo.it"
3. "Fabio Vincenzi - fabio.vincenzi2@studio.unibo.it"

**Processo di Sviluppo**: Il nostro gruppo intende adottare come processo di sviluppo la metodologia SCRUM

**Compiti di ogni studente**: 
1. Furi:
    * Operazioni aritmetiche: Somma, Co-varianza
    * Creazione del linguaggio DSL
    * Strategia di lavoro dato un singolo file
    * Implementazione della strategia risolutiva basata su più file
2. Iorio:
    * Operazioni aritmetiche: Max, Varianza
    * Operazioni di filtraggio
    * Creazione della strategia per il riconoscimento del tipo contenuto nel file
    * Implementazione della strategia risolutiva basata su singolo file
3. Vincenzi: 
    * Operazioni aritmetiche: Min, Media
    * Interfaccia grafica
    * Gestione file nei diversi formati CSV e JSON
    * Gestione di più file contemporaneamente

**Sintesi dei requisiti di massima del sistema da realizzare**: Il fine del nostro progetto è quello di realizzare tramite un'architettura Client-Server, un sistema distribuito per l'esecuzione di calcoli dati uno o più file.

Nella realizzazione di questo progetto sarà estremamente necessario individuare un design semplice ed estendibile, rispettando le best-practices affrontate durante il corso. Utilizzando inoltre come strategia di sviluppo il TDD.

**Elenco delle funzionalità offerte dal sistema**: 
+ Modellare in maniera estendibile le strategie col la quale eseguire le operazioni messe a disposizione dal sistema tramite l'utilizzo dei *Mixin*;
+ Implementare una strategia risolutiva tramite algoritmo distribuito;
+ Gestione di un singolo file;
+ Gestione di più file contemporaneamente;
+ Gestione di file in formato CSV e JSON;
+ Gestione per la tipologia file con singolo valore (file multi-linea con singola colonna);
+ Gestione per la tipologia file con coppia chiave-valore (file multi-linea con due colonne una per la chiave ed una per il valore);
+ Esecuzione di analisi su file per riconoscere il tipo dei valori contenuti in esso;
+ Implementazione di operazioni aritmetiche distribuite generiche su un dato file (Somma, Sottrazione, Min, Max, ...) tramite l'utilizzo dei *Mixin*, su qualsiasi tipologia di file;
+ Implmentazione di operazioni di filtraggio dati sulla base di certi criteri;
+ Concatenazione di più operazioni aritmetiche gestendo anche il loro ordine;
+ Introduzione di un linguaggio DSL per eseguire le operazioni scelte;
+ Interfaccia grafica lato client realizzate tramite Scala.js;

