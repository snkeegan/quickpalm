# Building QuickPALM from source #

Building QuickPALM is fairly easy:
  * you will need to have Java installed and available from the shell - probably your installation of ImageJ already brings a Java installation
  * [checkout](http://code.google.com/p/quickpalm/source/checkout) the latest QuickPALM source code
  * open a terminal or shell in the created directory with the code.
  * run the following commands:

```
javac -cp /Applications/ImageJ/ij.jar QuickPALM/*.java
jar cvfM QuickPALM_.jar QuickPALM/*.class QuickPALM/*.txt QuickPALM/plugins.config
```

And voilà, to finalise copy the generated QuickPALM`_`.jar to the plugins folder of ImageJ and it should be ready to use. Note, you may need to adapt `/Applications/ImageJ/ij.jar` to the correct location of your ImageJ installation.