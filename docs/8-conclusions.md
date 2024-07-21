
# Retrospettiva

Il progetto sviluppato è complessivamente soddisfacente per tutti i membri del
gruppo. Sono risultate fondamentali una buona progettazione a priori delle
varie componenti, risultato quindi in un processo di sviluppo agile e chiaro
per tutti i componenti. Il lavoro è stato fin da subito correttamente
suddiviso, e riteniamo che il carico assegnato ad ognuno di noi sia
correttamente proporzionato e entro il limite del monte ore di lavoro.

Per quanto riguarda aspetti strettamente legati alla metodologia di sviluppo,
settimanalmente sono stati prodotti risultati quasi sempre al di sopra delle
aspettative, mantenendo sempre il team motivato e produttivo. Ogni giorno di
lavoro ci si è confrontati e fissato gli obiettivi giornalieri e ridefinizione
degli obiettivi settimanali, sempre considerando eventuali difficoltà o
imprevisti che non sono di certo mancati, come per esempio il tempo impiegato e
il carico di lavoro per lo sviluppo del DSL è risultato piuttosto sottostimato,
dovuto anche dall'inesperienza di lavorare con certi aspetti più avanzati del
linguaggio Scala.

Per quanto i risultati ottenuti siano soddisfacenti, è importante notare come
una prima versione tangibile del software sia stata realizzata a circa tre
quinti del monte ore stimato, rendendo quindi più difficoltoso giustificare il
valore aggiunto ad ogni sprint settimanale. Questo pensiamo sia dovuto da una
ridotta capacità di determinare il valore delle specifiche componenti software
sviluppate, poiché le priorità sono state maggiormente puntate su elementi di
dominio e perciò difficilmente apprezzabili da potenziali stakeholder.

Come già detto, il risultato ottenuto è al pari delle aspettative iniziali,
considerando anche la novità nell'impiego di tale metodologia di organizzazione
del lavoro di gruppo, la quale sicuramente ha permesso di toccare con mano un
processo di sviluppo "moderno" e allo stesso tempo farci maturare una
competenza importante nel mondo dell'ingegneria del software.

## Sviluppi Futuri

Tra gli eventuali sviluppi futuri si possono includere:

- Implementazione di ulteriori automi cellulari, capaci anche di far emergere
  ulteriori generalizzazioni del simulatore, i suoi punti di forza e punti di
  debolezza, all'adattabilità verso scenari non previsti, ...;
- Configurazione del simulatore tramite file di configurazione standard (JSON,
  YAML, ...);
- Estensione dell'`Exporter` per poter esportare in formato PNG o JPEG
  dell'i-esima iterazione di un determinato automa cellulare.
- Produrre motori che possano supportare calcolo distribuito sfruttando il
  framework Akka.
- Implementazione di un DSL più generale, in grado di poter esprimere regole
  arbitrariamente complesse, come per esempio le regole dell'automa WaTor. È
  stato attribuito un piccolo slot di ore di lavoro per un possibile sviluppo
  di quest'ultimo, in modo tale da riuscire a produrre un prototipo
  funzionante; purtroppo il livello di complessità del risultato, sia da un
  punto di vista implementativo, sia da un punto di vista di uso vero e proprio
  del DSL, è risultato troppo elevato, ma può essere sicuramente oggetto di
  discussione di eventuali lavori di miglioramento futuri.
- Sviluppo di DSL o meccanismi che semplifichino la creazione di ambienti
  relativi ad automi cellulari.

[Indice](./index.md) | [Capitolo Precedente](./7-testing.md)
