package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

/**
 * Different types of kernel density estimators are now given as Enum type. Therefore if I have other estimators for other
 * matchers I should add them in this Enum Class
 * 
 * @author klitos
 */
public enum KernelEstimatorType {
	LEVENSHTEIN_KDE, NGRAM_KDE;
}
