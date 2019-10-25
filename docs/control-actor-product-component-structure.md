# An extensible component structure build around { CONTROL, ACTOR, PRODUCT }

## The Basic Idea  
Weave has an extensible component structure based on an idea distilled from a common practice in software engineering, 
config, which is widely adopted either intuitively or intentionally, when software engineers write codes at different 
levels methods/functions, classes, components and build applications and complex systems. The distilled idea consists 
of three parts,

![](CONTROL-ACTOR-PRODUCT.png)

- CONTROL, controls the behavior of ACTOR 
- ACTOR, acts upon CONTROL, processes input and produces PRODUCT  
- PRODUCT, produced by ACTOR  

To read the idea in context of method,  
- control parameter, controls the behavior of method, 
- method, processes input and may or may not produce a return
- return, produced by method

Similarly, to read the idea in context of application,
- config, controls configurable part of behavior of entire application
- application, does processing as instructed by configuration
- yield, typically application generates something

## The Challenges
While conceptually this idea is very simple to understand, to build an extensible component structure based on it is a 
different story. There are several challenge points need to be tackled.
 
### 1.Separate control and actor  

The idea of control is to make actor easy to change in some cases, because the lifecycle of control is different from 
that of actor. To really achieve that, separation between control and actor is needed. However, that's not something 
 guaranteed without discipline. Separation happens at different levels,  
 * separate code of control from code of actor
 * separate persisted form of control from code of control and code actor
 * externalize persisted form of control from package of control and actor in compiled binary  
 
Applications fails at any of these three levels with regard of separation of control and actor will exhibit some level 
of difficulty to change and adapt. For example, when control is not externalized, means it's packaged together with 
compiled code, even a smallest change in one parameter in a control will need full development/test/deploy cycle.

### 2.Separate from implementations and among implementations

The idea of concept and implementation is well understood, however why/how to separate concept from implementation is 
not fully understand and grasped by every engineer. Separating concept from implementation happens at 2 levels,
* separate the code representing concept and the code representing implementation. Programming languages like Java gives
good structures for this, interface/abstract class are for concept while concrete class is for implementation. 
* separate the package of compiled binary for concept and the package of compiled binary for implementation.
In case of {CONTROL, ACTOR, PRODUCT}, both two levels of separations between concept and implementation is needed to 
fully achieve extensibility of application built on a set of {CONTROL, ACTOR, PRODUCT}. Otherwise, the system is still
somewhat rigid for change.
Separation among implementations is equally important. 

### 3.Bind control, actor and product without knowing implementation upfront

### 4.Representation form of control
