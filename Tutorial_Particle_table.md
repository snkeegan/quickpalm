## How saving and loading the particles table image works ##

After you go through the "Analyse Particles" step, QuickPALM allows you to save your "particle table" as an image. By doing so QuickPALM generates a 32-bit float image in your harddrive that is 14 pixels wide and has as many pixels in height as the number of particles detected. All the values of the table are then saved as pixel values in this image, as such:

For particle N (where N represents the Nth particle):
  * The pixel 1xN is the "Intensity".
  * The pixel 2xN is the "X (px)".
  * The pixel 3xN is the "Y (px)".
  * The pixel 4xN is the "X (nm)" divided by 10^3.
  * The pixel 5xN is the "Y (nm)" divided by 10^3.
  * The pixel 6xN is the "Z (nm)" divided by 10^3.
  * The pixel 7xN is the "Left-StdDev (px)".
  * The pixel 8xN is the "Right-StdDev (px)".
  * The pixel 9xN is the "Up-StdDev (px)".
  * The pixel 10xN is the "Down-StdDev (px)".
  * The pixel 11xN is the "X Symmetry (%)".
  * The pixel 12xN is the "Y Symmetry (%)".
  * The pixel 13xN is the "Width minus Height (px)".
  * The pixel 14xN is the "Frame Number" divided by 10^6.

### Why saving a table to an image? ###

The loading and saving of images in ImageJ is extremely optimized. While saving a particle table with a large number of elements to a "tab separated text file" can take several minutes, doing the same to an image only takes a few seconds.