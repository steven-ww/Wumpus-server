# Hunt the Wumpus — Requirements

A specification for a text-based "Hunt the Wumpus" game, based on Gregory Yob's
original 1973 design. The goal is to find and shoot the Wumpus with an arrow
before it eats you or you fall foul of the cave system's other hazards.

This document uses the term **cave** throughout (to match the existing code).
The original game called these "rooms"; they are the same thing.

---

## 1. The cave system

- There are **20 caves**, connected as a **dodecahedron**: every cave links to
  **exactly 3 other caves**, every link is two-way, and no cave links to itself.
- The layout is fixed for the whole game; only the contents of caves are random.

## 2. What lives in the caves

At the start of each game, the following are placed in **distinct, randomly
chosen** caves (no two hazards ever share a cave, and the player never starts on
a hazard):

| Thing            | Count | Notes                                          |
|------------------|-------|------------------------------------------------|
| Wumpus           | 1     | The creature the player is hunting.            |
| Bottomless pits  | 2     | Falling in ends the game.                      |
| Super bats       | 2     | Carry the player to a random cave.             |
| Player           | 1     | Where the hunt begins.                         |

The player starts with **5 arrows**.

## 3. Warnings (the senses)

Before each turn, the game describes hazards in any of the **3 caves adjacent**
to the player's current cave. Warnings do **not** reveal which neighbour holds
the hazard. If more than one applies, show all of them.

- Wumpus in an adjacent cave: **"You smell something terrible nearby."**
- Pit in an adjacent cave: **"You feel a cold draft from a nearby cave."**
- Bats in an adjacent cave: **"You hear the flapping of giant bats nearby."**

If no hazard is adjacent, show no warning.

## 4. The turn loop

Each turn proceeds in this order:

1. Show the player's location and the warnings (Section 3).
2. Show the tunnels leading out, e.g.
   **"You are in cave 12. Tunnels lead to caves 1, 13 and 20."**
3. Prompt the player to act:
   **"Shoot or move? (S/M)"**
4. Resolve the chosen action (move — Section 5; shoot — Section 6).
5. Check for a win or loss (Sections 5–7). If the game is not over, repeat.

Input handling: accept upper- or lower-case. On unrecognised input, re-prompt
without ending the turn (e.g. **"I don't understand. Please enter S or M."**).

## 5. Moving

1. Prompt: **"Move to which cave?"**
2. The player enters a cave number. It must be **one of the 3 adjacent caves**.
   - If it is not adjacent, re-prompt:
     **"You can't get there from here. Tunnels lead to caves X, Y and Z."**
3. The player moves into the chosen cave, and its contents take effect:

   - **Empty cave** — nothing happens; the turn ends.

   - **Wumpus cave** — the player bumps the Wumpus and wakes it:
     **"You bumped into the Wumpus!"** The Wumpus then moves (Section 7).
     - If it moves into the player's (now current) cave, the player is eaten —
       **loss**.
     - Otherwise the player survives in that cave and the turn ends.

   - **Pit cave** — **"You fell into a bottomless pit. You lose."** — **loss**.

   - **Bats cave** — **"Super bats grab you and whisk you away!"** The bats drop
     the player in a **uniformly random cave** (any of the 20, chosen
     independently of where the bats are). The destination's contents then take
     effect immediately using the same rules — so bats can drop the player onto
     a pit, onto the Wumpus, or onto another bat cave (relocating them again).
     Repeat until the player lands somewhere with no bats.

## 6. Shooting (crooked arrows)

The player fires an arrow along a path they describe, cave by cave. The arrow
can pass through up to **5 caves**.

1. Prompt: **"How many caves should the arrow fly through? (1-5)"**
   - Reject anything outside 1–5 and re-prompt.
2. For each cave in the path, prompt: **"Cave #N?"** and read a cave number.
   - **Crooked-path rule:** an arrow can't double back on itself. If the entered
     cave is the same as the one **two steps earlier** in the path (i.e. the
     arrow would immediately reverse), reject it:
     **"Arrows aren't that crooked — pick another cave."** and re-ask that step.
   - **Aiming vs. drift:** if the entered cave **is adjacent** to the arrow's
     current cave, the arrow flies there. If it is **not adjacent**, the arrow
     instead flies to a **random adjacent cave** (the shot has gone wide).
3. As the arrow enters each cave along its path:
   - **Wumpus cave** — **"You shot the Wumpus! You win!"** — **win** (stop
     immediately).
   - **Player's own cave** — **"You shot yourself. You lose!"** — **loss** (stop
     immediately).
   - Otherwise the arrow continues to the next cave in the path.
4. If the arrow reaches the end of its path without hitting anything, the shot
   **misses**: **"You missed."**
5. **On a miss**, the Wumpus is startled and moves (Section 7).
   - If the startled Wumpus moves into the player's cave, the player is eaten —
     **loss**.
6. Each shot consumes **one arrow**. If the player fires their **last arrow and
   misses**, they are out of ammunition: **"You've run out of arrows. You
   lose."** — **loss**.

## 7. How the Wumpus moves

The Wumpus is asleep and stays put until **startled**. It is startled when:

- the player bumps into its cave by moving there (Section 5), or
- the player fires an arrow and misses (Section 6).

When startled, the Wumpus picks at random from **four equally likely (1 in 4
each)** options: move to **one of its 3 adjacent caves**, or **stay where it
is**.

- If it moves (or stays) such that it ends up in the **player's current cave**,
  the player is eaten: **"The Wumpus found you and ate you. You lose."** —
  **loss**.
- Otherwise the Wumpus's new location is hidden from the player (revealed only
  through the smell warning on later turns).

## 8. Winning and losing

- **Win:** an arrow enters the Wumpus's cave (Section 6).
- **Lose**, by any of:
  - falling into a pit (Section 5),
  - being eaten by the Wumpus — bumped-then-moved-onto, or startled-onto
    (Sections 5, 7),
  - shooting yourself with your own arrow (Section 6),
  - running out of arrows (Section 6).

When the game ends, show the outcome and offer to play again:
**"Play again? (Y/N)"** A new game re-randomises all cave contents (Section 2);
the cave layout (Section 1) stays the same.

## 9. Opening text

On launch, before the first turn, the game shows an introduction explaining the
setting, the hazards, and the controls (the smell/draft/flapping warnings, the
2 pits, the 2 bat caves, the 5 arrows, and that an arrow can be steered through
up to 5 caves). It then waits for the player to press Enter to begin the hunt.

> **Note:** the current intro text in `App.java` says arrows are shot "into an
> adjacent cave." That wording predates this spec and should be updated to
> describe the crooked-arrow mechanic (an arrow steered through up to 5 caves).

---

## Appendix — example turn

```
You are in cave 12. Tunnels lead to caves 1, 13 and 20.
You feel a cold draft from a nearby cave.
Shoot or move? (S/M)
> m
Move to which cave?
> 13
You are in cave 13. Tunnels lead to caves 12, 4 and 19.
You smell something terrible nearby.
Shoot or move? (S/M)
> s
How many caves should the arrow fly through? (1-5)
> 2
Cave #1? 4
Cave #2? 19
You shot the Wumpus! You win!
Play again? (Y/N)
```
