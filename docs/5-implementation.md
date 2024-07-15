# Implementazione del Sistema
All'intero di questo capitolo verranno descritte le modalita attraverso la quale sono state definite le varie classi, gli eventuali problemi riscontrati ed infine le modalita attraverso la quale queste difficolta sono state risolte.

## Cellular Automaton
Uno dei primi concetti che e stato sviluppato, riguarda il _Cellular Automaton_. Le classi sviluppate per la realizzazione del _Cellular Automaton_, sono diverse e possono essere elencate qui di seguito:
1. _Cellular Automaton_
2. _Dimension_
3. _Cell_
5. _Neighbour_
6. _Rule_
Le seguenti classi qui sopra elencate, ad esclusione di _Dimension_, sono racchiuse all'intero del _package_: _domain.automaton_, dove al suo interno sono contenute tutte le componenti software a supporto del concetto di automa cellulare.
### Dimension
Per rappresentare il concetto di dimensione, all'interno della quale l'automa cellulare si sviluppa, e stato realizzato all'interno dell'_object_ _Dimensions_ il _trait_ **Dimension**. Tale _trait_ definisce un singolo valore, _dimensions_ che permette di rappresentare la dimensione dello spazio. A sua volta all'interno dell'_object_ vengono definite anche due _case class_ che estendono dal _trait dimension_ e permettono di rappresentare uno spazio bidimensionale, tramite la classe _TwoDimensionalSpace_  ed uno spazio tridimensionale tramite la classe _ThreeDimensionalSpace_. In questo modo si possono utilizzare le seguenti due classi per rappresentare un spazio dimensionale 2D o 3D.

```scala
object Dimensions:
  trait Dimension:
    def dimensions: Int
  case class TwoDimensionalSpace() extends Dimension:
    override val dimensions: Int = 2
  case class ThreeDimensionalSpace() extends Dimension:
    override val dimensions: Int = 3
```
Tramite questa implementazione sara possibile definire in maniera agile nuovi spazi dimensionali.
### Cell
Uno dei componenti fondamentali per un _Cellular Automaton_ e il concetto di _Cell_. La realizzazione di questo componente ha previsto la definizione di un nuovo _trait_ denominato **Cell**, tale _trait_ e definito generico nella dimensione dal momento in cui una _Cell_ deve far riferimento ad una dimensionalita dello spazio generica, per definire cio e stato necessario forzare il parametro generico come sotto classe del _trait Dimension_. In questo modo si ha la garanzia che _Cell_ ed _Cellular Automaton_ facciano riferimento allo stesso tipo di dimensione dello spazio.

Una delle componenti principali di _Cell_ e la posizione che assumoe nello pazio, rappresentato dal valore _position_, Tale posizione e definita anch'essa generica nella dimensione **D** dello spazio. Inoltre la _Cell_ e caratteriizzata da uno stato che ha in un preciso istante di tempo. Per modellare lo stato e stato definito un nuovo parametro denominato _state_ di tipo _State_.

```scala
trait Cell[D <: Dimension]:
    def position: Position[D]
    def state: State
object Cell:
    def apply[D <: Dimension](p: Position[D], s: State): Cell[D] = CellImpl(p, s)
    def unapply[D <: Dimension](cell: Cell[D]): Option[(Position[D], State)] = Some((cell.position, cell.state))
    private case class CellImpl[D <: Dimension]
      (override val position: Position[D], override val state: State)
    extends Cell[D]
```
Dopo la definizione del _trait_ si e passati allo sviluppo dell'_object Cell_ il cui compito e quello di fornire una _factory_ per le _Cell_. Oltre a cio e stata definita una _case class_ che implementasse il _trait Cell_, in modo da fornire una sua implementazione da utilizzare durante lo sviluppo delle simulazioni.
### Neighbourhood
### Rule

## Environment
Il secondo macro componente che e stato necessario sviluppare e l'_Environment_. Questo componente software incapsula lo spazio all'interno della quale vengono memorizzate le _Cell_ di un _Cellular Automaton_. Per modellare questa astrazione nel tipo di simulazione che si vuole implementare, e stato necessario fare in modo che l'intero _Environment_ fosse generico nel tipo di _Cellular Automaton_ che si sta utilizzando. Per fare in modo di avere una rappresentazione generale dell'ambiente, e stato realizzato il _trait Generic Environment_, il quale e definito in due campi generici, il primo fa riferimento alla _Dimension_ della simulazione, mentre il secondo generico riguarda il tipo di ritorno dopo l'applicazione di una specifica regola.

```scala
trait GenericEnvironment[D <: Dimension, R] extends Space[D]:
  protected def saveCell(cells: Cell[D]*): Unit
  def applyRule(neighbors: Neighbour[D]): R
```
La definizione di questo _Environment_ permette all'utente di applicare una determinata regola dato uno specifico _Neighbour_ per poi sucessivamente salvare il risulato all'intero della struttura dati che si occupa di mantenere tutte le celle della simulazione.

### Tipologie di Environment
Come descritto in precedenza, un automa cellulare ha il proprio spazio in cui evolve, lo spazio potrebbe essere rappresentato da un semplice rettangolo dimensionale, a forme molto piu complesse come quelle di cubi e toroidi. Per rappresentare questa diversita di struttura dell'_Environment_ sono state definite una serie di _trait_ che possono rappresentare diverse strutture spaziali da utilizzare nella simulazione.

```scala
trait SquareEnvironment extends Space[TwoDimensionalSpace]:
    def side: Int
    override def dimension: Tuple2[Int, Int] = (side, side)
trait CubicEnvironment extends Space[ThreeDimensionalSpace]:
    def edge: Int
    override def dimension = (edge, edge, edge)
trait RectangularEnvironment extends Space[TwoDimensionalSpace]:
    def width: Int
    def heigth: Int
    override def dimension: Tuple2[Int, Int] = (heigth, width)
trait ToroidEnvironmnt extends RectangularEnvironment:
  extension (dividend: Int)
    infix def /%/(divisor: Int): Int = 
      val result = dividend % divisor
      result match
        case value if value < 0 => result + divisor
        case _ => result
```
I _trait_ qui sopra definiti permettono di modellare uno specifico _Space_ da utilizzare per una simulazione. Questo tipo di modellazione permette di introdurre concetti specifici caratteristici per un certo tipo di _Space_, come nel caso di uno spazio toroidale il quale puo essere visualizzato come un rettangolo in uno spazio bidimensionale. Questo tipo di visione ci permette poi di introdurre specifici metodi da utilizzare per modellare lo spazio correttamente.

### Space
Come formulato in precedenza, e fondamentale avere all'interno di una simulazione una strtuttura dati al cui interno vengono memorizzate tutte le _Cell_ inerenti ad una simulazione. Per la rappresentazione di questo particolare aspetto si e sviluppato uno specifico _trait_ all'interno dell'_Environment_ denominato _Space_ generico nella dimensione. La dimensione dello spazio e dell'automa cellulare sara sempre la stessa dal momento in cui vengono definite tramite lo stesso generico in input all'_Environment_.

```scala
trait Space[D <: Dimension]:
   type Matrix
   def currentMatrix: Matrix
   def matrix: Matrix
   def neighbours(cell: Cell[D]): Neighbour[TwoDimensionalSpace]
   def dimension: Tuple
   protected def initialise(): Unit
   protected def availableCells(positions: Iterable[Position[D]]): Iterable[Cell[D]]
```

Dal momento in cui non e possibile sapere in anticipo il tipo di dimensione, la struttura dati all'interno della quale verranno memorizzate le _Cell_ e definita tramite un _type_ in questo modo lo user avra libera scelta nella tipologia di struttura dati da utilizare. Questo _trait_ in oltre si occupa anche di andare a definire alcune operazioni di utility per lavorare sulla struttura dati utilizzata. Uno dei passaggi fondamentali per una qualsiasi simulazione riguarda l'inizializzazione dello spazio. Proprio per questo e stato definito il metodo _initialise_, al cui interno verra definita lo stato iniziale della simulazione.




### Space Implementation
Durante lo sviluppo dello _

### Cellular Automaton's Type
ADD mixin

### Space
### Implementations

## Engine

## Interfaccia Grafica
