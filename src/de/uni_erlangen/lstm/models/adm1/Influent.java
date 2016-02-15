/*
 * jADM1 -- Java Implementation of Anaerobic Digestion Model No 1
 * ===============================================================
 *
 * Copyright 2015 Liam Pettigrew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ********************************************************************************************
 */

package de.uni_erlangen.lstm.models.adm1;

import java.util.logging.Logger;

import de.uni_erlangen.lstm.file.CSVReader;
import de.uni_erlangen.lstm.file.CSVWriter;

/**
 * Specifies the variables for digester influent/effluent
 * Modified from the BSM2 adjusted model for IAWQ AD Model No 1.
 * 
 * Special thanks to  Ulf Jeppsson, Christian Rosen and Darko Vrecko
 * for use of their Matlab code of the ADM1, 
 * developed when (around 2004) they were all working together at the 
 * Department of Industrial Electrical Engineering and Automation (IEA), Lund University, Sweden.
 * 
 * @author liampetti
 *
 */
public class Influent {	
	public final static Logger LOGGER = Logger.getLogger(Influent.class.getName());
	
	/*
	 * Digestor Influent
	 */
	private double S_su, S_aa, S_fa, S_va, S_bu, S_pro, S_ac, S_h2, S_ch4, S_IC, S_IN, S_I;
	private double X_xc, X_ch, X_pr, X_li, X_su, X_aa, X_fa, X_c4, X_pro, X_ac, X_h2, X_I;
	private double S_cat, S_an, S_hva, S_hbu, S_hpro, S_hac, S_hco3, S_nh3, S_gas_h2, S_gas_ch4, S_gas_co2;
	private double Q_D, T_D, temp0, gas_vol, ph, temp1, temp2;

	/** 
	 * Default settings according to what you would typically find for sludge digesters
	 */
	public Influent() {		
		/*
		 * Digester Influent (42 Variables)
		 */
		// array position, (ADM1 id), description, (units)
		S_su =  0.01; // 0.		(1) monosaccharides (kg COD/m3)[g COD/L]
		S_aa =  0.001; // 1. 	(2) amino acids (kg COD/m3)[g COD/L]
		S_fa =  0.001; // 2. 	(3) total long chain fatty acids (LCFA) (kg COD/m3)[g COD/L]
		S_va =  0.001; // 3. 	(4) total valerate (kg COD/m3)[g COD/L]
		S_bu =  0.001; // 4. 	(5) total butyrate (kg COD/m3)[g COD/L]
		S_pro = 0.001; // 5. 	(6) total propionate (kg COD/m3)[g COD/L]
		S_ac =  0.001; // 6. 	(7) total acetate (kg COD/m3)[g COD/L]
		S_h2 =  1.0e-8; // 7. 	(8) hydrogen gas (kg COD/m3)[g COD/L]
		S_ch4 = 1.0e-5; // 8. 	(9) methane gas (kg COD/m3)[g COD/L]
		S_IC = 0.04; // 9. 		(10) inorganic carbon (kmole C/m3)
		S_IN = 0.01; // 10. 	(11) inorganic nitrogen (kmole N/m3)
		S_I = 0.02; // 11. 		(12) soluble inerts (kg COD/m3)[g COD/L]
		// Particulates ---->
		X_xc = 2.0; // 12. 		(13) composites (kg COD/m3)[g COD/L]
		X_ch = 5.0; // 13. 		(14) carbohydrates (kg COD/m3)[g COD/L]
		X_pr = 20.0; // 14. 	(15) proteins (kg COD/m3)[g COD/L]
		X_li = 5.0; // 15. 		(16) lipids (kg COD/m3)	[g COD/L]	
		X_su = 0.0; // 16. 		(17) sugar degraders (kg COD/m3)[g COD/L]
		X_aa = 0.01; // 17. 	(18) amino acid degraders (kg COD/m3)[g COD/L]
		X_fa = 0.01; // 18.  	(19) LCFA degraders (kg COD/m3)[g COD/L]
		X_c4 = 0.01; // 19. 	(20) valerate and butyrate degraders (kg COD/m3)[g COD/L]
		X_pro = 0.01; // 20. 	(21) propionate degraders (kg COD/m3)[g COD/L]
		X_ac = 0.01; // 21. 	(22) acetate degraders (kg COD/m3)[g COD/L]
		X_h2 =  0.01; // 22. 	(23) hydrogen degraders (kg COD/m3)[g COD/L]
		X_I =  25.0;; // 23. 	(24) particulate inerts (kg COD/m3)[g COD/L]
		// <---- Particulates
		S_cat =  0.04; // 24. cations (metallic ions, strong base) (kmole/m3)
		S_an =  0.02; // 25. anions (metallic ions, strong acid) (kmole/m3)
		// Set from init --->
		S_hva = 0.0;   // 26. is actually Sva- = valerate (kg COD/m3)[g COD/L]
		S_hbu = 0.0;   // 27. is actually Sbu- = butyrate (kg COD/m3)[g COD/L]
		S_hpro =  0.0; // 28. is actually Spro- = propionate (kg COD/m3)[g COD/L]
		S_hac =  0.0;   // 29. is actually Sac- = acetate (kg COD/m3)[g COD/L]
		S_hco3 = 0.0; // 30. bicarbonate (kmole C/m3)
		S_nh3 = 0.0; // 31. ammonia (kmole N/m3)
		// <--- Set from init
		S_gas_h2 = 0.0; // 32. hydrogen concentration in gas phase (kg COD/m3)[g COD/L]
		S_gas_ch4 = 0.0; // 33. methane concentration in gas phase (kg COD/m3)[g COD/L]
		S_gas_co2 = 0.0; // 34. carbon dioxide concentration in gas phase (kmole C/m3)		
		Q_D = 170.0; // 35. flow rate (m3/d)
		T_D = 0.0; // 36. temperature (deg C) - SET BY DIGESTER
		temp0 = 0.0; // 37. Optional
		gas_vol = 0.0; // 38. gas volume (m3/d)
		ph = 0.0; // 39. pH
		temp1 = 0.0; // 40. Optional
		temp2 = 0.0; // 41. Optional
	}
	
	/**
	 * Read the outputs from a given CSV file
	 * 
	 * @param filename
	 */
	public void readIn(String filename) {
		CSVReader reader = new CSVReader(filename, ";");
		String[] outputs = reader.getStrings();
		double[] x = new double[outputs.length];
		for (int i=0;i<x.length;i++) {
			x[i] = Double.parseDouble(outputs[i]);
		}
		setIn(x);
	}
	
	/**
	 * Writes the current outputs to a CSV file
	 * 
	 * @param filename
	 */
	public void writeIn(String filename) {
		double[] x = getIn();
		CSVWriter writer = new CSVWriter();
		writer.WriteArray(filename, x);
	}
	
	/**
	 * Retrieves the outputs as an array
	 */
	public double[] getIn() {
		return new double[] { S_su, S_aa, S_fa, S_va, S_bu, S_pro, S_ac, S_h2, S_ch4,
				S_IC, S_IN, S_I, X_xc, X_ch, X_pr, X_li, X_su, X_aa, X_fa, X_c4, X_pro, X_ac,
				X_h2, X_I, S_cat, S_an, S_hva, S_hbu, S_hpro, S_hac, S_hco3, S_nh3, S_gas_h2, S_gas_ch4,
				S_gas_co2, Q_D, T_D, temp0, gas_vol, ph, temp1, temp2 };
	}
	
	/**
	 * Sets the outputs from an array
	 */
	public void setIn(double[] x) {
		S_su=x[0];
		S_aa=x[1];
		S_fa=x[2];
		S_va=x[3];
		S_bu=x[4];
		S_pro=x[5];
		S_ac=x[6];
		S_h2=x[7];
		S_ch4=x[8];
		S_IC=x[9];
		S_IN=x[10];
		S_I=x[11];
		X_xc=x[12]; 
		X_ch=x[13];
		X_pr=x[14];
		X_li=x[15];
		X_su=x[16];
		X_aa=x[17];
		X_fa=x[18];
		X_c4=x[19];
		X_pro=x[20];
		X_ac=x[21];
		X_h2=x[22];
		X_I=x[23]; 
		S_cat=x[24];
		S_an=x[25];
		// CSV file generated by Matlab implementation does not describe all variables
		if (x.length < 42) {
			Q_D=x[26];
			T_D=x[27];
			S_hva=0.0;
			S_hbu=0.0;
			S_hpro=0.0;
			S_hac=0.0;
			S_hco3=0.0;
			S_nh3=0.0;
			S_gas_h2=0.0;
			S_gas_ch4=0.0;
			S_gas_co2=0.0;
			temp0=0.0;
			gas_vol=0.0;
			ph=0.0;
			temp1=0.0;
			temp2=0.0;
		} else {
			S_hva=x[26];
			S_hbu=x[27];
			S_hpro=x[28];
			S_hac=x[29];
			S_hco3=x[30];
			S_nh3=x[31];
			S_gas_h2=x[32];
			S_gas_ch4=x[33];
			S_gas_co2=x[34];
			Q_D=x[35];
			T_D=x[36];
			temp0=x[37];
			gas_vol=x[38];
			ph=x[39];
			temp1=x[40];
			temp2=x[41];
		}
	}
	
	/**
	 * Getters and Setters for all individual variables
	 */
	public double getS_su() {
		return S_su;
	}

	public void setS_su(double s_su) {
		S_su = s_su;
	}

	public double getS_aa() {
		return S_aa;
	}

	public void setS_aa(double s_aa) {
		S_aa = s_aa;
	}

	public double getS_fa() {
		return S_fa;
	}

	public void setS_fa(double s_fa) {
		S_fa = s_fa;
	}

	public double getS_va() {
		return S_va;
	}

	public void setS_va(double s_va) {
		S_va = s_va;
	}

	public double getS_bu() {
		return S_bu;
	}

	public void setS_bu(double s_bu) {
		S_bu = s_bu;
	}

	public double getS_pro() {
		return S_pro;
	}

	public void setS_pro(double s_pro) {
		S_pro = s_pro;
	}

	public double getS_ac() {
		return S_ac;
	}

	public void setS_ac(double s_ac) {
		S_ac = s_ac;
	}

	public double getS_h2() {
		return S_h2;
	}

	public void setS_h2(double s_h2) {
		S_h2 = s_h2;
	}

	public double getS_ch4() {
		return S_ch4;
	}

	public void setS_ch4(double s_ch4) {
		S_ch4 = s_ch4;
	}

	public double getS_IC() {
		return S_IC;
	}

	public void setS_IC(double s_IC) {
		S_IC = s_IC;
	}

	public double getS_IN() {
		return S_IN;
	}

	public void setS_IN(double s_IN) {
		S_IN = s_IN;
	}

	public double getS_I() {
		return S_I;
	}

	public void setS_I(double s_I) {
		S_I = s_I;
	}

	public double getX_xc() {
		return X_xc;
	}

	public void setX_xc(double x_xc) {
		X_xc = x_xc;
	}

	public double getX_ch() {
		return X_ch;
	}

	public void setX_ch(double x_ch) {
		X_ch = x_ch;
	}

	public double getX_pr() {
		return X_pr;
	}

	public void setX_pr(double x_pr) {
		X_pr = x_pr;
	}

	public double getX_li() {
		return X_li;
	}

	public void setX_li(double x_li) {
		X_li = x_li;
	}

	public double getX_su() {
		return X_su;
	}

	public void setX_su(double x_su) {
		X_su = x_su;
	}

	public double getX_aa() {
		return X_aa;
	}

	public void setX_aa(double x_aa) {
		X_aa = x_aa;
	}

	public double getX_fa() {
		return X_fa;
	}

	public void setX_fa(double x_fa) {
		X_fa = x_fa;
	}

	public double getX_c4() {
		return X_c4;
	}

	public void setX_c4(double x_c4) {
		X_c4 = x_c4;
	}

	public double getX_pro() {
		return X_pro;
	}

	public void setX_pro(double x_pro) {
		X_pro = x_pro;
	}

	public double getX_ac() {
		return X_ac;
	}

	public void setX_ac(double x_ac) {
		X_ac = x_ac;
	}

	public double getX_h2() {
		return X_h2;
	}

	public void setX_h2(double x_h2) {
		X_h2 = x_h2;
	}

	public double getX_I() {
		return X_I;
	}

	public void setX_I(double x_I) {
		X_I = x_I;
	}

	public double getS_cat() {
		return S_cat;
	}

	public void setS_cat(double s_cat) {
		S_cat = s_cat;
	}

	public double getS_an() {
		return S_an;
	}

	public void setS_an(double s_an) {
		S_an = s_an;
	}

	public double getS_hva() {
		return S_hva;
	}

	public void setS_hva(double s_hva) {
		S_hva = s_hva;
	}

	public double getS_hbu() {
		return S_hbu;
	}

	public void setS_hbu(double s_hbu) {
		S_hbu = s_hbu;
	}

	public double getS_hpro() {
		return S_hpro;
	}

	public void setS_hpro(double s_hpro) {
		S_hpro = s_hpro;
	}

	public double getS_hac() {
		return S_hac;
	}

	public void setS_hac(double s_hac) {
		S_hac = s_hac;
	}

	public double getS_hco3() {
		return S_hco3;
	}

	public void setS_hco3(double s_hco3) {
		S_hco3 = s_hco3;
	}

	public double getS_nh3() {
		return S_nh3;
	}

	public void setS_nh3(double s_nh3) {
		S_nh3 = s_nh3;
	}

	public double getS_gas_h2() {
		return S_gas_h2;
	}

	public void setS_gas_h2(double s_gas_h2) {
		S_gas_h2 = s_gas_h2;
	}

	public double getS_gas_ch4() {
		return S_gas_ch4;
	}

	public void setS_gas_ch4(double s_gas_ch4) {
		S_gas_ch4 = s_gas_ch4;
	}

	public double getS_gas_co2() {
		return S_gas_co2;
	}

	public void setS_gas_co2(double s_gas_co2) {
		S_gas_co2 = s_gas_co2;
	}

	public double getQ_D() {
		return Q_D;
	}

	public void setQ_D(double q_D) {
		Q_D = q_D;
	}

	public double getT_D() {
		return T_D;
	}

	public void setT_D(double t_D) {
		T_D = t_D;
	}

	public double getQ_Gas() {
		return temp0;
	}

	public void setQ_Gas(double q_gas) {
		temp0 = q_gas;
	}

	public double getP_Ch4() {
		return gas_vol;
	}

	public void setP_Ch4(double p_ch4) {
		gas_vol = p_ch4;
	}

	public double getPh() {
		return ph;
	}

	public void setPh(double ph) {
		this.ph = ph;
	}

	public double getS_gas_h2s() {
		return temp1;
	}

	public void setS_gas_h2s(double s_gas_h2s) {
		temp1 = s_gas_h2s;
	}

	public double getX_D5_D() {
		return temp2;
	}

	public void setX_D5_D(double x_D5_D) {
		temp2 = x_D5_D;
	}
}
