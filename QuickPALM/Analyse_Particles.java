package QuickPALM;

import ij.*;
import ij.measure.*;
import ij.plugin.*;
import ij.plugin.filter.*;
import ij.plugin.frame.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.CurveFitter.*;
import java.awt.*;
import java.lang.*;

public class Analyse_Particles implements PlugIn 
{
	ImagePlus imp;
	ImageProcessor ip;
	
	MyDialogs dg = new MyDialogs();
	MyFunctions f = new MyFunctions();
	MyIO io = new MyIO();

	public void run(String arg) 
	{
		IJ.register(Analyse_Particles.class);
		if (!dg.analyseParticles(f)) return;
		
		// Erase particle table
		f.ptable.reset();
		
		if (dg.is3d)
		{
			dg.getCalibrationFile();
			io.loadTransformation(dg.calfile, f.caltable);
			f.initialize3d();
		}
		
		if (dg.attach)
		{
			dg.getImageDirectory();
			imp=f.getNextImage(dg, 0);
			if (imp==null)
			{
				IJ.error("Could not find image following given pattern");
			    return;
			}
		}
		else
		{
			imp = IJ.getImage();
			if (imp==null)
			{
			    IJ.noImage();
			    return;
			}
			else if (imp.getType() != ImagePlus.GRAY8 && imp.getType() != ImagePlus.GRAY16 ) 
			{
			    // In order to support 32bit images, pict[] must be changed to float[], and  getPixel(x, y); requires a Float.intBitsToFloat() conversion
			    IJ.error("8 or 16 bit greyscale image required");
			    return;
			}
		}
	
		ReconstructionViewer viewer = new ReconstructionViewer(imp.getShortTitle()+" Reconstruction", imp.getWidth(), imp.getHeight(), dg, f);
		ViewerUpdate vUpdate; //= new ViewerUpdate();
		ViewerUpdateShort vUpdateShort; //= new ViewerUpdateShort();
		//int nslices = imp.getStackSize();
		
		//ProcessFrame [] threads = new ProcessFrame[Runtime.getRuntime().availableProcessors()*2];
		//ProcessFrame [] threads = new ProcessFrame[50];
		ProcessFrame [] threads = new ProcessFrame[dg.threads];
		int freeThread=-1;
		
		long time_start = java.lang.System.currentTimeMillis();
		long time_took = 0;
		long time_now=0;
		long nparticles=0;

		int s=0;
		boolean ok = true;
		
		//f.ptable.show("Particles table");
		while (ok)
		{
			if (dg.attach)
			{
				imp=f.getNextImage(dg, s);
				if (imp==null) ok=false;
				else ip=imp.getProcessor();
			}
			else
			{
				if (s>=imp.getStackSize()) ok=false;
				else
				{
					//imp.setSliceWithoutUpdate(s+1);
					imp.setSlice(s+1);
					ip=imp.getProcessor().duplicate();
				}
			}
			
			if (ok)
			{
				if (s<threads.length)
					freeThread=s;
				else
				{
					freeThread=-1;
					while (freeThread==-1)
					{
						for (int t=0;t<threads.length;t++)
						{
							if (!threads[t].isAlive())
							{
								freeThread=t;
								break;
							}
						}
						if (freeThread==-1)
						{
							try
							{
								Thread.currentThread().sleep(1);
							}
							catch(Exception e)
							{
									IJ.error(""+e);
							}
						}
					}
				}
			
				threads[freeThread] = new ProcessFrame();
				threads[freeThread].mysetup(ip, f, dg, s);
				threads[freeThread].start();
				
				time_now = java.lang.System.currentTimeMillis();
				time_took += time_now-time_start;
				time_start = time_now;
				if ((s>0) && (s%dg.viewer_update==0))
				{
					ij.IJ.showStatus("Processing at "+time_took/dg.viewer_update+" ms/frame "+(f.ptable.getCounter()-nparticles)/dg.viewer_update+" part/frame, detected "+nparticles+" particles");
					nparticles=f.ptable.getCounter();
					//f.ptable.updateResults();
					time_took=0;
					if (dg.viewer_accumulate==0)
					{
						//try {vUpdate.join();}
						//catch (Exception e) {IJ.error(""+e);}
						vUpdate = new ViewerUpdate();
						vUpdate.mysetup(viewer);
						vUpdate.start();
					}
					else
					{
						//try {vUpdateShort.join();}
						//catch (Exception e) {IJ.error(""+e);}
						vUpdateShort = new ViewerUpdateShort();
						vUpdateShort.mysetup(viewer, Math.round(s+1-dg.viewer_accumulate/2), Math.round(s+1+dg.viewer_accumulate/2));
						vUpdateShort.start();
					}
				}
				//if (s%100000==0) f.ptable.show("Results");
			}
			s++;
		}
		for (int t=0; t<threads.length;t++)
		{
			try
			{
				threads[t].join();
			}
			catch(Exception e)
			{
				IJ.error(""+e);
			}
		}
		if (f.psave!=null) f.psave.close();
		
		if (dg.viewer_accumulate==0)
			viewer.update();
		else
			viewer.updateShort(Math.round(s-dg.viewer_accumulate/2), s);
		if (f.ptable.getCounter()<5000000)
		{
            IJ.showStatus("Creating particle table, this should take a few seconds...");
			f.ptable.show("Results");
		}
        else
            IJ.showMessage("Warning", "Results table has too many particles, they will not be shown but the data still exists within it\nyou can still use all the plugin functionality or save table changes though the 'Save Particle Table' command.");

	}
}

class ProcessFrame extends Thread 
{
	private ImageProcessor ip;
	private MyDialogs dg;
	private int frame;
	private MyFunctions f;
	
	public void mysetup(ImageProcessor ip, MyFunctions f, MyDialogs dg, int frame)
	{
		this.f=f;
		this.ip=ip;
		this.dg=dg;
		this.frame=frame;
	}
	
	public void run()
	{
		//f.ptable.updateResults();
		//this.f.sizeGating(this.ip, this.dg.spsize, this.dg.lpsize);
		this.f.detectParticles(this.ip, this.dg, this.frame);
	}
}

class ViewerUpdate extends Thread
{
	private ReconstructionViewer viewer;
	//private java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
	
	public void mysetup(ReconstructionViewer viewer)
	{
		//this.lock.lock();
		this.viewer=viewer;
		//this.lock.unlock();
	}
	
	public void run()
	{
		//this.lock.lock();
		this.viewer.update();
		//this.lock.unlock();
	}
}

class ViewerUpdateShort extends Thread
{
	private ReconstructionViewer viewer;
	private int start;
	private int stop;
	//private java.util.concurrent.locks.Lock lock = new java.util.concurrent.locks.ReentrantLock();
	
	public void mysetup(ReconstructionViewer viewer, int start, int stop)
	{
		//this.lock.lock();
		this.viewer=viewer;
		this.start=start;
		this.stop=stop;
		//this.lock.unlock();
	}
	
	public void run()
	{
		//this.lock.lock();
		this.viewer.updateShort(this.start, this.stop);
		//this.lock.unlock();
	}
}