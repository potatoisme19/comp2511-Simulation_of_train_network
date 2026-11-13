1. Introduction

"To travel by train is to see nature and human beings, towns and churches and rivers, in fact, to see life." – Agatha Christie.

The modern day train network allows for the flow of thousands of passengers and cargo to be transported across stations in cities worldwide, enabling the functioning of society as we know it today. Modern software solutions are required to design a robust and effective system that millions rely on for their daily needs.
You have been tasked with designing and implementing a software solution that will manage a simulation of a modern day transit network. Your solution must adopt an Object-Oriented approach that utilises concepts such as abstraction, encapsulation, composition, and inheritance, as taught in lectures.

1.1. Simulation

A simulation is an incremental process that models the changes in a system over time.
In our model, we simulate the changes to our train network every minute. We start with an initial world state, say WorldState_00. We add a specified time interval of 1 minute and calculate the new positions of all the trains after the minute, to derive the next world state WorldState_01. Similarly, we derive WorldState_02 from WorldState_01, WorldState_03 from WorldState_02, and so on. This act of feeding a world state into the next forms a sort of state machine.
WorldState_00 -> WorldState_01 -> WorldState_02 -> …
In our case our simulation runs at an accuracy of 1 minute and each state transition will take only 1 minute.

1.2. Positions

The simulation will use a Cartesian coordinate system. Each entity will have a position (x,y), where x, y are doubles.
A Position class that represents an entity's position has been provided for you, with some simple utility methods. This is located in the util package.

1.3. Assumptions

As well as requirements laid out in subsequent sections, you may make the following assumptions about your system:

- No invalid inputs will be provided to your functions, unless specified.
- All IDs are unique across all entities (trains/tracks/loads/stations). E.g. There will never be a track and a train with the same ID. All IDs are strings.
- You can assume no distance or speed will be <= 0.
- All x and y coordinate values are doubles between 0 and 200 (inclusive).
- Trains, tracks and stations will never be deleted once they are created.
- Trains maintain the specified speed whenever they are actively moving, without accounting for inertia or requiring time to accelerate.
- All stations and tracks are created before any simulation ticks occur.
