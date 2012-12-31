#Sabre
Sabre is a distributed (in-memory) graph processing framework
designed to make distribution of "trivially parallelizable"
graph computations across a compute grid nice and easy.

Sabre is written with 
[Scala](http://www.scala-lang.org/) 2.10.0,
[Akka](http://akka.io/) 2.1.0,
and [Graph for Scala](https://www.assembla.com/spaces/scala-graph/wiki) 1.5.2 (to be replaced with 1.6).

# Usage
Spin up an example app by doing something like:

```
$ sbt 'run-main ShortestPathApp'
```

Alternatively, run the appropriate command to load your own app. Then on
each machine you want to distribute over, run `sabre.system.Worker`.


If you are distributing over several machines, the use of a script may be
useful.

### Fault Tolerance
Each individual `Worker` is watched by the `Master` - on worker failure
the master will simply queue up the corresponding work to be done by
other workers.

If a worker machine JVM crashes, all workers are considered dead and all
work that was being done on that machine is queued up again.

Because of this fault tolerance mechanism, Sabre is elastic - worker
machines are free to join (if they are specified in `sabre.cfg`) and leave
as they please.

### Edgelists
Edgelists are formatted as two columns. Each column corresponds to an endpoint
of an edge in the graph. Note that since Sabre assumes an undirected graph,
it is sufficient to simply have one "direction" listed in the edgelist.

### sabre.cfg
The configuration file follows a very simple format that looks like this:

```
Edgelist filename (stored in `edgelists/`)
master machines address (optional: # of workers)
worker 1 machine address (optional: # of workers)
worker 2 machine address (optional: # of workers)
.
.
.
worker n machine address (optional: # of workers)
```

Note that because workers can also be spawned on the client machine, there
is the option of listing the # of workers to spawn on that line.

If the # of workers is not specified, the number of workers spawned will
be the value returned by `Runtime.getRuntime().availableProcessors()`. This
value is subtracted by two on the master machine address so as to not
interfere with the `Master` and `ResultHandler` - if you really want to
do that, override it in the config!

An example configuration file may look like this:

```
SomeGraph.edgelist
master.machine.address 2
worker.one.address
worker.two.address 3
```

This would setup the system to load the graph specified by `edgelists/SomeGraph.edgelist`.
Spinning up `sabre.system.Worker` on the client machine would spawn 2 workers. Likewise,
spinning it up on `worker.one.address` would spawn a set of workers of size equal to the number
of available processors, and three for `worker.two.address`.

The # of workers spawned is an upper bound on the number of processors that will be used
on the system - it does not guarentee that that number of processors will be fully utilized
throughout the whole process. Such an example would be when the number of workers begins to
be greater than the number of jobs available - workers then start becoming idle and the
number of active processors will diminish.
