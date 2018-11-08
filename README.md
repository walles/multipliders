A game where maths challenges fall down from the sky and you can shoot
them by typing the correct answer.

# TODO Before Testing on a Child
* Add a launch screen
* Dismissing the level-failed dialog should go to the launch screen
* Remove click handler from GameView, but verify that swiping down from
  the top still works
* Show level-success dialog after clearing a level
* Pick maths depending on level
* Add feedback on key presses
* Add sound effects
* Add a music score

# TODO Before Publishing on Google Play
* Adapt font sizes to screen
* Add a credits list of some form to the launch screen
* Hide controls by default so that we get the full-screen experience,
  good for production, search for `mHideRunnable`. Verify that we can
  easily show the controls when they are hidden.
* Pause game while controls are visible
* Add a license
* Make an icon
* Make a "Feature Graphics"

# TODO Misc
* Actually publish on Google Play
* Make the level-failed answers dialog look better
* Tune the hit areas for the keys on our keyboard
* Drop easier maths faster than harder maths
* Add starry background


## DROPPED
* In the Model, create a way to iterate only over our `FallingMaths`
  objects. Could have done something like
  [this](https://codereview.stackexchange.com/a/112111/159546), but it
  feels too complicated. Suggestions welcome.
* Make our keyboard react to actual digits presses from a real keyboard
  as well. Nice during development. Tried with `setOnKeyListener()` and
  [this](https://stackoverflow.com/a/26567134/473672) but only got
  events sporadically, never mind.
* Stop all the invalidate() calls when the simulation is frozen. Can't
  see how this would help with much of anything.

## DONE
* Make text move down from top
* Add more falling text when the first text is 20% down
* View controls by default so that we can pause the game easily, good
  for development.
* Add a numeric keyboard
* Fire away numbers when keys are pressed
* Make maths assignments fall from the sky
* Shoot at maths assignments when the right answer is entered
* Fire a slow red shot when a wrong answer is entered
* Kill player when some maths lands
* When player is killed, freeze the simulation after a delay.
* Log frame rate on emulator (we're fine)
* Log frame rate on a 10" tablet (we're doing great)
* Show correct answer on screen when the player dies.