# News #
  * New [video](http://www.youtube.com/watch?v=2Y0u6rg_fjM) reviewing the homebuilt microscope.
  * Wiki fixed after some issues.
  * Andrew York from Hari Shroff's lab recently released also an excellent open-source algorithm for 3D PALM, check it [here](http://code.google.com/p/palm3d/) or the publication [here](http://dx.doi.org/10.1038/nmeth.1571).

Join our [mailing list](http://groups.google.com/group/quickpalm) for the latest news and release announcements.

# About QuickPALM #
QuickPALM is a set of programs to aid in the acquisition and image analysis of data in “photoactivated localization microscopy” (PALM) and “stochastic optical reconstruction microscopy” (STORM).

<a href='http://www.youtube.com/watch?feature=player_embedded&v=QEcS7c_JXYc' target='_blank'><img src='http://img.youtube.com/vi/QEcS7c_JXYc/0.jpg' width='425' height=344 /></a>

Currently QuickPALM features the associated software:
  * **QuickPALM ImageJ plugin** - enables PALM/STORM 2D/3D/4D particle detection and image reconstruction in ImageJ.
  * **Laser control for QuickPALM** - a laser control software that facilitates molecule photoswitching.


### Features ###
  * **Speed**: it is _**very fast**_, typically takes less than 100 ms processing time per frame on an 8 CPU computer, permitting reconstructions in real-time and allowing direct visualization of 2D/3D or 4D super-resolution images during acquisition - 4D in this case stands for 3D+time as the algorithm allows one to visualize super-resolution movies in live imaging.
  * **3D super-resolution**: able to do _**both 2D and 3D**_ (by using astigmatism) PALM/STORM - see our publications for a detailed description of how to easily change almost any TIRF microscope into a system able to achieve 3D PALM/STORM.
  * **Drift correction**: Drift is a major challenge in PALM/STORM microscopy and in most cases the limiting factor for resolution. Since the acquisition can take from several minutes to hours, drifts of even a few tens of nanometers can drastically deteriorate resolution, or worse, create anomalies in reconstructed images (e.g. artifactual doubling of filamentary structures). QuickPALM provides a drift correction method based on tracking beads or other visual landmarks.
  * **Availability, ease of use and open-source**: both the software and its source code are freely available as an _**ImageJ plugin**_. This allows maximal dissemination of the software in the biological research community, optimal usability, and will offer users the ability to modify and improve the software at will.
  * **Integrated software for parallel acquisition & processing**: QuickPALM is divided into two software parts, the ImageJ plugin for image analysis/visualization and a _**laser control software**_ that simplifies laser pulsing sometimes needed in PALM/STORM. These two parts can be used in parallel with acquisition software such as [Micro-Manager](http://www.micro-manager.org/), [Andor iQ](http://www.andor.com/software/iq/), etc... to create a full system for parallel acquisition, analysis and visualization of the super-resolution datasets.

### How it works ###

<a href='http://www.youtube.com/watch?feature=player_embedded&v=RE70GuMCzww' target='_blank'><img src='http://img.youtube.com/vi/RE70GuMCzww/0.jpg' width='425' height=344 /></a>

Here we use a funny example to explain the principles of QuickPALM. This small movie was taken in 2008 at night while the Eiffel Tower blinks its lights, it is quite similar to what we try to achieve in a PALM/STORM experiments by stochastically switching on some of the fluorescent molecules in a cell. The first task of the algorithm is to detect the blinking spots (sub-diffraction in microscopy) and localize each with a sub-pixel accuracy. Following this step it reconstructs an image composed of each detected spot but whose resolution is far greater than that conventional imaging.

## References ##

  * Ricardo Henriques, Mickael Lelek, Eugenio F Fornasiero, Flavia Valtorta, Christophe Zimmer & Musa M Mhlanga (2010). QuickPALM: 3D real-time photoactivation nanoscopy image processing in ImageJ. **Nature Methods** 7:339–340 [doi:10.1038/nmeth0510-339](http://dx.doi.org/10.1038/nmeth0510-339).
  * Henriques R, Griffiths C, Rego EH and Mhlanga MM (2011). PALM and STORM: unlocking live‐cell super‐resolution. **Biopolymers** vol. 95(5):322-331 [doi:10.1002/bip.21586](http://onlinelibrary.wiley.com/doi/10.1002/bip.21586/abstract).
  * Henriques R and Mhlanga MM (2009). PALM and STORM: what hides beyond the Rayleigh limit?. **Biotechnol. J.**  vol. 4(6):846-57 [doi:10.1002/biot.200900024](http://www3.interscience.wiley.com/journal/122462281/abstract?CRETRY=1&SRETRY=0).