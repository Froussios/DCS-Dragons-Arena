DCS-Dragons-Arena
=================

This project contains the implementation of the prototype for an MMO game that is served by a distributed system.

The implementation is in Java and RMI.



Guide to the code
-----------------

nl.dcs.da contains the Game logic.
More specifically, it contains the implementation of a game state, the implementation of the Trailling State Synchronization alogrithm, the events that signify changes to a game state and client interfaces of assuming a role in the game.

nl.dcs.app contains the logic for bots that run on the game.

nl.dcs.network contains the implementation of the message relay system.
