package dao;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Test;

import antlr.ParserRunner;

public class DaoMethodsTest {

	@Test
	public void shouldMatchClassNameWithTheMethodReturnType() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public Invoice getAll() {}"
				+ "}";
		
		DaoMethods methods = new ParserRunner().run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(1,methods.getRightOnes().size());
		Assert.assertEquals(0,methods.getProblematicOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("getAll"));
	}

	@Test
	public void shouldMatchClassNameWithTheMethodParameters() {
		String dao = 
				"class InvoiceDAO {"
						+ "public AnyDTO getAll(Invoice inv) {}"
						+ "public AnyDTO getAll2(int x, Invoice inv) {}"
						+ "}";
		
		DaoMethods methods = new ParserRunner().run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(2,methods.getRightOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("getAll"));
		Assert.assertTrue(methods.getRightOnes().contains("getAll2"));
	}
	
	@Test
	public void shouldMatchPrimitives() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public boolean x1() {}"
				+ "public int x2() {}"
				+ "public Integer x3() {}"
				+ "public String x4() {}"
				+ "public BigDecimal x5() {}"
				+ "public Calendar x6() {}"
				+ "public double x7() {}"
				+ "public Long x8() {}"
				+ "public long x9() {}"
				+ "}";
		
		DaoMethods methods = new ParserRunner().run(new ByteArrayInputStream(dao.getBytes()));
		Assert.assertEquals(9,methods.getRightOnes().size());
	}

	@Test
	public void shouldMatchClassNameWithTheMethodReturnGenericType() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public List<Invoice> getAll() {}"
				+ "public Set<Invoice> getAll2() {}"
				+ "}";

		DaoMethods methods = new ParserRunner().run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(2,methods.getRightOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("getAll"));
		Assert.assertTrue(methods.getRightOnes().contains("getAll2"));
	}

	@Test
	public void shouldIgnoreInnerClasses() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public List<Invoice> getAll() {}"
				+ "class ShittyInnerClass {"
				+ "public Invoice bla() {}"
				+ "}"
				+ "public Invoice getAll2() {}"
				+ "}";
		
		DaoMethods methods = new ParserRunner().run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(2,methods.getRightOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("getAll"));
		Assert.assertTrue(methods.getRightOnes().contains("getAll2"));
	}

	@Test
	public void shouldWarnAboutMethodsWithReturnTypeDoesntMatch() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public Car getAll() {}"
				+ "}";
		
		DaoMethods methods = new ParserRunner().run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(0,methods.getRightOnes().size());
		Assert.assertEquals(1,methods.getProblematicOnes().size());
		Assert.assertTrue(methods.getProblematicOnes().contains("getAll"));
	}
	
	@Test
	public void shouldIgnoreNonPublicMethods() {
		String dao = 
				  "class InvoiceDAO {"
				+ "private Invoice getAll1() {}"
				+ "protected Invoice getAll2() {}"
				+ "Invoice getAll3() {}"
				+ "public Invoice getAll4() {}"
	
				+ "private Car x1() {}"
				+ "protected Car x2() {}"
				+ "Car x3() {}"
				+ "public Car x4() {}"
				+ "}";
		
		DaoMethods methods = new ParserRunner().run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(1,methods.getProblematicOnes().size());
		Assert.assertEquals(1,methods.getRightOnes().size());
		Assert.assertTrue(methods.getProblematicOnes().contains("x4"));
		Assert.assertTrue(methods.getRightOnes().contains("getAll4"));
	}
}
