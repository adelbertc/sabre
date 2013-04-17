#Sabre
Sabre is a distributed (in-memory) graph processing framework
designed to make distribution of "trivially parallelizable"
graph computations across a compute grid nice and easy. It
is designed for producing raw results in a raw text format.

Sabre, at the core, is written with 
[Scala](http://www.scala-lang.org/) 2.10.1,
[Akka](http://akka.io/) 2.1.2,
and [Graph for Scala](https://www.assembla.com/spaces/scala-graph/wiki) 1.6.1.
It also uses the [Scala IO](http://jesseeichar.github.com/scala-io-doc/index.html)
library in a few places for IO.

This project is the manifestation of my Akka Summer of Blog post on distributed graph
processing, found [here](http://letitcrash.com/post/29044669086/balancing-workload-across-nodes-with-akka-2).

# Usage
DISCLAIMER: Sabre assumes undirected, unweighted graphs.

Write your algorithm (see `example/` for guidance), making sure it has a `main`
method that calls `Sabre.execute()`. Make sure `sabre.cfg` is in order (see below)
and run your program on the master machine.

Then on each machine you intend to distribute over, run `sabre.system.Worker`.

If you are distributing over several machines, the use of a script may be
useful.

Make sure when you run both the master and workers that the current working
directory has the `sabre.conf` file.

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

### sabre.conf
The configuration file uses the [Typesafe Config](https://github.com/typesafehub/config) and 
follows a very simple format that looks like this:

```
sabre {
    graph = "/path/to/graph.edgelist"

    master = "master.address.here"

    deploy {
        worker1.machine.address.nr-of-workers = 2
        worker2.machine.address.nr-of-workers = 3
        worker3.machine.address.nr-of-workers = 1
    }
}
```

You may opt to declare a "global" variable `nr-of-workers` in the `sabre` scope and set the
`nr-of-workers` for each machine to be `${sabre.nr-of-workers}`.

It is up to the programmer to tune the # number of workers to their specific
algorithm - algorithms whose work finishes very fast and has time dominated
by waiting for network I/O may want to spawn many more workers than the default.
Those whose work are CPU intensive may want to simply use the default. A
more extensive discussion can be found [here](http://stackoverflow.com/questions/10879296/how-to-determine-the-number-of-actors-to-spawn-in-akka).

The # of workers spawned is an upper bound on the number of processors that will be used
on the system - it does not guarentee that that number of processors will be fully utilized
throughout the whole process. Such an example would be when the number of workers begins to
be greater than the number of jobs available - workers then start becoming idle and the
number of active processors will diminish.

##License
Please refer to the LICENSE file.

###Author
Adelbert Chang [@adelbertchang](https://twitter.com/adelbertchang)

##Acknowledgements
A large portion of the architecture of this project is taken from
[Derek Wyatt](https://twitter.com/derekwyatt)'s Akka Summer of Blog submissions.
The workload balancing and fault-tolerance mechanisms are taken from
[this](http://letitcrash.com/post/29044669086/balancing-workload-across-nodes-with-akka-2) post
and the shutdown mechanism was taken from [this](http://letitcrash.com/post/30165507578/shutdown-patterns-in-akka-2) one.

##Contributing
I believe in the current implementation, the most glaring need for improvement
is the lack of typing (types as `Any`), most notably in
`sabre.result`. As I am a full-time student as well as a research assistant, I do not
have a lot of time to fix this, but it is something I am looking into (in the back
of my mind at least). If you would like to undertake the challenge, please submit a
pull request! (This goes for any other improvements as well)

1. Fork
2. Create a feature branch
3. Commit and push your branch to your fork
4. Submit pull request
