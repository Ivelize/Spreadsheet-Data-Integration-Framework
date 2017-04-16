package uk.ac.manchester.dstoolkit.domain.provenance;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.BenchmarkType;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.DampeningEffectPolicy;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.PlotType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ErrorMeasuresTypes;

public class ControlParameter {

	//Generic annotation parameters
	private BenchmarkType bType;
	private PlotType pType;
	private DampeningEffectPolicy dep;
	private ControlParameterType name;
	private ErrorMeasuresTypes errType;
	private String value;
	private SelectionType selType;
	private boolean bool;

	//TODO might have to be ControlParameterType rather than String, or use ontology ... similar to annotation?
	//should probably be made persistent as part of provenance model, which we don't have yet

	public ControlParameter(ControlParameterType name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public ControlParameter(ControlParameterType name, BenchmarkType bt) {
		this.name = name;
		this.bType = bt;
	}

	public ControlParameter(ControlParameterType name, ErrorMeasuresTypes eT) {
		this.name = name;
		this.errType = eT;
	}	
	
	public ControlParameter(ControlParameterType name, PlotType pt) {
		this.name = name;
		this.pType = pt;
	}	

	public ControlParameter(ControlParameterType name, DampeningEffectPolicy p) {
		this.name = name;
		this.dep = p;
	}	
	
	public ControlParameter(BenchmarkType bt) {
		this.bType = bt;
	}
	
	public ControlParameter(PlotType pt) {
		this.pType = pt;
	}

	public ControlParameter(ControlParameterType name, boolean bool) {
		this.name = name;
		this.bool = bool;
	}
	
	public ControlParameter(ControlParameterType name, SelectionType value) {
		this.name = name;
		this.selType = value;
	}
	
	/**
	 * @return the name
	 */
	public ControlParameterType getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(ControlParameterType name) {
		this.name = name;
	}

	public SelectionType getSelType() {
		return selType;
	}
	
	public BenchmarkType getBenchmarkType() {
		return this.bType;
	}
	
	public ErrorMeasuresTypes getErrorMeasureType() {
		return this.errType;
	}

	public PlotType getPlotType() {
		return this.pType;
	}
	
	public DampeningEffectPolicy getDampeningEffectPolicy() {
		return this.dep;
	}	
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public void setSelType(SelectionType selType) {
		this.selType = selType;
	}
	
}
