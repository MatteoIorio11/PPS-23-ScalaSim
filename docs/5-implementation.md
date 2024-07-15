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
Dopo la definizione del _trait_ si e passati allo sviluppo dell'_object Cell_ il cui compito e quello di essere una _factory_ per le _Cell_.
### Neighbourhood
### Rule

## Environment

## Engine

## Interfaccia Grafica
