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
import java.util.*;

public class Create_3D_calibration implements PlugIn 
{
	ImagePlus imp;
	ImageProcessor ip;
	
	MyDialogs dg = new MyDialogs();
	MyFunctions f = new MyFunctions();
	MyIO io = new MyIO();
	ResultsTable res = new ResultsTable();
	ResultsTable extrainfo = new ResultsTable();
	
	public boolean setup(String arg) 
	{
        
		if (! dg.checkBeads() || ! dg.beadCalibration3d())
			return false;
		imp = dg.imp;
        imp.setSlice(1);
		IJ.register(Create_3D_calibration.class);
		return true;
	}

	public void run(String arg) 
	{
		if (! setup(arg)) return;

		// Start processing
		ij.IJ.run(imp, "Select None", "");

		double [][] sgnl  = new double[dg.nrois][dg.nslices];
		double [][] xstd = new double[dg.nrois][dg.nslices];
		double [][] ystd = new double[dg.nrois][dg.nslices];
		double [][] wmh = new double[dg.nrois][dg.nslices];
		double [] mean_wmh = new double[dg.nslices];
		
		int index_z0 = 0;
		double sSum = 0;
		double [] results;
		Rectangle roi;
		for (int s=1;s<=dg.nslices;s++)
		{
			imp.setSlice(s);
			ip=imp.getProcessor().duplicate();
			
			f.sizeGating(ip, 0.5, dg.fwhm*2);
			
			sSum = 0;
			for (int r=0;r<dg.rois.length;r++)
			{
				roi = dg.rois[r].getBoundingRect();
				results = f.getParticleForCalibration(ip, dg, roi.x, roi.x+roi.width, roi.y, roi.y+roi.height);
				sgnl[r][s-1]=results[0];
				xstd[r][s-1]=results[3]+results[4];
				ystd[r][s-1]=results[5]+results[6];
				wmh[r][s-1]=results[7];
				mean_wmh[s-1]+=results[7]*results[0];
				sSum+=results[0];
			}
			mean_wmh[s-1] /= sSum;
		}		
		
		// get the bias for each particle
		double [] bias = new double [dg.rois.length];
		for (int r=0;r<dg.rois.length;r++)
		{
			sSum = 0;
			for (int s=1;s<=dg.nslices;s++)
			{
				bias[r]+=(wmh[r][s-1]-mean_wmh[s-1])*sgnl[r][s-1];
				sSum+= sgnl[r][s-1];
			}
			bias[r]/=sSum;
			//ij.IJ.log(""+bias[r]);
		}
		
		// realign each ROI
		for (int r=0;r<dg.rois.length;r++)
			for (int s=1;s<=dg.nslices;s++)
				wmh[r][s-1]-=bias[r];
		
		//	averaging
		for (int s=1;s<=dg.nslices;s++)
		{
			mean_wmh[s-1]=0;
			sSum=0;
			for (int r=0;r<dg.rois.length;r++)
			{
				mean_wmh[s-1]+=wmh[r][s-1]*sgnl[r][s-1];
				sSum+=sgnl[r][s-1];
			}
			mean_wmh[s-1]/=sSum;
		}
		mean_wmh=f.movingMean(mean_wmh, dg.window);
		
		// calculate the Z-position
		double [] zpos = new double[dg.nslices];
		for (int s=1;s<=dg.nslices;s++) zpos[s-1]=s;
		
		double [] cal_mean_wmh = (double []) mean_wmh.clone();

		if (dg.model!=dg.models[0])
		{
			CurveFitter cf = new CurveFitter(zpos, mean_wmh);
			 if (dg.model==dg.models[1])
				cf.doFit(CurveFitter.STRAIGHT_LINE);
			else if (dg.model==dg.models[2])
				cf.doFit(CurveFitter.POLY2);
			else if (dg.model==dg.models[3])
				cf.doFit(CurveFitter.POLY3);
			else if (dg.model==dg.models[4])
				cf.doFit(CurveFitter.POLY4);
			IJ.log("---- Model Estimation ----");
			IJ.log(cf.getResultString());
			cal_mean_wmh=cf.getResiduals();
			for (int s=0;s<cal_mean_wmh.length;s++)
				cal_mean_wmh[s]=mean_wmh[s]-cal_mean_wmh[s];
		}
		
		// realign with zero
		index_z0 = f.getClosest(0, cal_mean_wmh, 0);
		for (int s=1;s<=dg.nslices;s++)
			zpos[s-1]=((s-index_z0)*dg.cal_z);
		
		// cut down inflexions
		int pmax = f.argmax(cal_mean_wmh);
		int pmin = f.argmin(cal_mean_wmh);
		
		int start = (pmax<pmin)?pmax:pmin;
		int stop = (pmax<pmin)?pmin:pmax;
		
		mean_wmh 	 = java.util.Arrays.copyOfRange(mean_wmh, start, stop);
		cal_mean_wmh = java.util.Arrays.copyOfRange(cal_mean_wmh, start, stop);
		zpos		 = java.util.Arrays.copyOfRange(zpos, start, stop);
		
		Plot plot = new Plot("Calibration Values", "Z-position (nm)", "PSF Width minus Height (px)", zpos, cal_mean_wmh);
		
		float a, b, c, f;
		java.awt.Color color;// = new java.awt.Color(0.1,0.1,0.1);
		Random rand = new Random(0);
		
		plot.setLineWidth(1);
		for (int r=0;r<dg.nrois;r++)
		{
			f = ((float) r/(dg.nrois-1))*2;
			a = (f<1)?(1-f):0;
			b = (f<1)?f/2:(2-f)/2;
			c = (f<1)?0:(f-1);
			//ij.IJ.log(f+" "+a+" "+b+" "+c);
			//a = rand.nextFloat();
			//b = rand.nextFloat();
			//c = rand.nextFloat();
			color = new java.awt.Color(a, b, c);
			plot.setColor(color);
			plot.addPoints(zpos, java.util.Arrays.copyOfRange(wmh[r], start, stop), Plot.CROSS);
		}
		
		plot.setLineWidth(2);
		plot.setColor(java.awt.Color.BLACK);
		plot.show();
		
		for (int s=0;s<zpos.length;s++)
		{
			res.incrementCounter();
			res.addValue("Z-Step", zpos[s]);
			res.addValue("Raw Width minus Heigh", mean_wmh[s]);
			res.addValue("Calibration Width minus Height", cal_mean_wmh[s]);
		}
		res.show("Astigmatism calibration");

		if (!dg.part_extrainfo)
			return;

		for (int s=0;s<dg.nslices;s++)
		{
			extrainfo.incrementCounter();
			for (int r=0; r<dg.nrois;r++)
			{
				extrainfo.addValue("Width P"+r, xstd[r][s]);
				extrainfo.addValue("Height P"+r, ystd[r][s]);
				//extrainfo.addValue("Width minus Height P"+r, wmh[r][s]);
			}
		}
		extrainfo.show("Particle extra information...");

		//boolean save = IJ.showMessageWithCancel("Save calibration...", "Save calibration into file?");
		//if (!save) return;
		//ij.io.SaveDialog sv = new ij.io.SaveDialog("Save calibration file...", "AstigmatismCalibration", ".txt");
		//java.lang.String filepath = sv.getDirectory()+sv.getFileName();
		//io.saveTransform(filepath, small_zpos, small_xmystd, cal_xmystd);
	}
}
