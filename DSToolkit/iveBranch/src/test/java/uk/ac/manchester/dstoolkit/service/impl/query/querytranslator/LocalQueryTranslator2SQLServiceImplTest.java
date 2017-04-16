package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LocalQueryTranslator2SQLServiceImplTest {

	LocalQueryTranslator2SQLServiceImpl localQueryTranslator2SQLService = new LocalQueryTranslator2SQLServiceImpl();

	@Test
	public void testRemoveUnionFromString1() {
		String stringWithoutUnion = localQueryTranslator2SQLService.removeUnionFromString("union1.citye");
		assertEquals("citye", stringWithoutUnion);
	}

	@Test
	public void testRemoveUnionFromString2() {
		String stringWithoutUnion = localQueryTranslator2SQLService.removeUnionFromString("union1.citye, union1.countrye");
		assertEquals("citye, countrye", stringWithoutUnion);
	}

	@Test
	public void testRemoveUnionFromString3() {
		String stringWithoutUnion = localQueryTranslator2SQLService.removeUnionFromString("union1.union2.citye");
		assertEquals("citye", stringWithoutUnion);
	}

	@Test
	public void testRemoveUnionFromString4() {
		String stringWithoutUnion = localQueryTranslator2SQLService.removeUnionFromString("union1.union2.citye, union1.union2.countrye");
		assertEquals("citye, countrye", stringWithoutUnion);
	}

	@Test
	public void testRemoveUnionFromString5() {
		String stringWithoutUnion = localQueryTranslator2SQLService.removeUnionFromString("union1.union2.countrye.citye");
		assertEquals("countrye.citye", stringWithoutUnion);
	}

	@Test
	public void testRemoveUnionFromString6() {
		String stringWithoutUnion = localQueryTranslator2SQLService
				.removeUnionFromString("union1.union2.countrye.citye, union1.union2.countrye.countrye");
		assertEquals("countrye.citye, countrye.countrye", stringWithoutUnion);
	}

	@Test
	public void testRemoveUnionFromString7() {
		String stringWithoutUnion = localQueryTranslator2SQLService.removeUnionFromString("union1.union2.countrye1.citye2");
		assertEquals("countrye1.citye2", stringWithoutUnion);
	}

	@Test
	public void testRemoveUnionFromString8() {
		String stringWithoutUnion = localQueryTranslator2SQLService
				.removeUnionFromString("union1.union2.countrye1.citye2, union1.union2.countrye3.countrye4");
		assertEquals("countrye1.citye2, countrye3.countrye4", stringWithoutUnion);
	}

}
