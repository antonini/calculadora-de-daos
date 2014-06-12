package listeners;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import antlr.ParserRunner;

public class DaoMethodsTest {

	private DaoMethods methods;
	private HashSet<String> enumerators;
	private HashMap<String, Set<String>> interfaces;

	@Before
	public void setUp() {
		enumerators = new HashSet<String>();
		interfaces = new HashMap<String, Set<String>>();

		methods = new DaoMethods(enumerators, interfaces);
	}
	
	@Test
	public void shouldMatchClassNameWithTheMethodReturnType() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public Invoice getAll() {}"
				+ "}";
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(1,methods.getRightOnes().size());
		Assert.assertEquals(0,methods.getProblematicOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("getAll"));
	}
	
	@Test
	public void shouldIgnoreConstructors() {
		String dao = 
				"class InvoiceDAO {"
						+ "public InvoiceDAO() {}"
						+ "public Invoice getAll() {}"
						+ "}";
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(1,methods.getRightOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("getAll"));
	}

	@Test
	public void shouldMatchClassNameWithTheMethodParameters() {
		String dao = 
				"class InvoiceDAO {"
						+ "public AnyDTO getAll(Invoice inv) {}"
						+ "public AnyDTO getAll2(int x, Invoice inv) {}"
						+ "}";
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		
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
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		Assert.assertEquals(9,methods.getRightOnes().size());
	}
	
	@Test
	public void shouldMatchEnumerator() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public PaymentInfoEnum x1() {}"
				+ "}";
		
		enumerators.add("PaymentInfoEnum");
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		Assert.assertEquals(1,methods.getRightOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("x1"));
	}
	
	@Test
	public void shouldMatchChildReturn() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public SuperInvoice x1() {}"
				+ "}";
		
		interfaces.put("SuperInvoice", new HashSet<String>());
		interfaces.get("SuperInvoice").add("Invoice");
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		Assert.assertEquals(1,methods.getRightOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("x1"));
	}

	@Test
	public void shouldMatchClassNameWithTheMethodReturnGenericType() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public List<Invoice> getAll() {}"
				+ "public Set<Invoice> getAll2() {}"
				+ "}";

		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(2,methods.getRightOnes().size());
		Assert.assertTrue(methods.getRightOnes().contains("getAll"));
		Assert.assertTrue(methods.getRightOnes().contains("getAll2"));
	}
	
	@Test
	public void shouldConsiderRightWhenMethodReturnsGenericWithTwoOrTypes() {
		String dao = 
				  "class InvoiceDAO {"
				+ "public Map<A, B> getAll() {}"
				+ "public Map<A, B, C> getAll2() {}"
				+ "}";

		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		
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
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		
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
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		
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
		
		new ParserRunner(methods).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertEquals(1,methods.getProblematicOnes().size());
		Assert.assertEquals(1,methods.getRightOnes().size());
		Assert.assertTrue(methods.getProblematicOnes().contains("x4"));
		Assert.assertTrue(methods.getRightOnes().contains("getAll4"));
	}
}
